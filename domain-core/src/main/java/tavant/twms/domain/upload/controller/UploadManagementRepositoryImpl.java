/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

package tavant.twms.domain.upload.controller;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.common.I18NUploadErrorText;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.upload.staging.FileReceiver;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.QueryParameters;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("unchecked")
public class UploadManagementRepositoryImpl extends GenericRepositoryImpl implements UploadManagementRepository {

	private FileReceiver fileReceiver;
	
	public List<UploadManagement> findAll()
	{
		return (List<UploadManagement>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery("from UploadManagement").list();
					}
					
				}
			);
	}

	public PageResult<UserFileUpload> findAllUploadHistory(final ListCriteria listCriteria, 
			final String templateName){
         StringBuffer query = new StringBuffer(" from UserFileUpload userFileUpload WHERE userFileUpload.templateName=:templateName");
   		 final Map<String, Object> parameterMap = listCriteria.getParameterMap();
   		 parameterMap.put("templateName", templateName);
   		 QueryParameters parameters = new QueryParameters(parameterMap);
   		 if (listCriteria.isFilterCriteriaSpecified()) {
   			 query.append(" and ");
   			 query.append(listCriteria.getParamterizedFilterCriteria());
   		 }
   		 
   		 return findPageUsingQuery(query.toString(), listCriteria.getSortCriteriaString().toString(), "select userFileUpload ", listCriteria.getPageSpecification(), parameters);
	}
	
	public PageResult<UserFileUpload> findAllUploadHistory(final ListCriteria listCriteria, 
			final String templateName, final Long userId){         
         StringBuffer query = new StringBuffer(" from UserFileUpload userFileUpload WHERE userFileUpload.uploadedBy.id =:userId AND userFileUpload.templateName=:templateName");
   		 final Map<String, Object> parameterMap = listCriteria.getParameterMap();
   		 parameterMap.put("userId", userId);
   		 parameterMap.put("templateName", templateName);
   		 QueryParameters parameters = new QueryParameters(parameterMap);
   		 
   		 if (listCriteria.isFilterCriteriaSpecified()) {
   			 query.append(" and ");
   			 query.append(listCriteria.getParamterizedFilterCriteria());
   		 }
   		 
   		 return findPageUsingQuery(query.toString(), listCriteria.getSortCriteriaString().toString(), "select userFileUpload ", listCriteria.getPageSpecification(), parameters);
	}

	public UserFileUpload findUserFileUploadById(final Long userFileUploadId) {
		 return (UserFileUpload) getHibernateTemplate().execute(
					new HibernateCallback() {
						    public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
								Criteria userFilecriteria = session.createCriteria(UserFileUpload.class);
								userFilecriteria.add(Restrictions.eq("id", userFileUploadId));
								List<UserFileUpload> userFileUploads = userFilecriteria.list();
								return (userFileUploads!=null && !userFileUploads.isEmpty()) ? 
										userFileUploads.get(0): null;
						    }});
	}

	public File getErrorFileById(final Long id) {
		 return (File) getHibernateTemplate().execute(
					new HibernateCallback() {
						    public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
						    	Connection conn = session.connection();
						    	try {
									return fileReceiver.getErrorBlobReadOnly(conn, id);
								} catch (IOException e) {
									logger.error("Faield to read error blob", e);
									return null;
								}
					}});
	}

	public File getUploadedFileById(final Long id) {
		 return (File) getHibernateTemplate().execute(
					new HibernateCallback() {
						    public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
						    	Connection conn = session.connection();
						    	try {
									return fileReceiver.getUploadedBlobReadOnly(conn, id);
								} catch (IOException e) {
									logger.error("Faield to read upload blob", e);
									return null;
								}
					}});
	}
	
	public List<UploadError> getUploadErrorsForUploadMgt(Connection conn, long uploadMgtId)
	throws SQLException {
		Statement stmt = null;
		List<UploadError> uploadErrors = new ArrayList<UploadError>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT e.id, e.code, e.upload_field, t.locale, t.description " +
					"FROM upload_mgt_upload_errors me, upload_error e, " +
					"    i18nupload_error_text t " +
					"WHERE e.id = t.upload_error AND e.id = me.upload_errors " +
					"      AND me.upload_mgt = "+uploadMgtId +
					" ORDER BY e.id ");
			UploadError currentError = null;
			while(rs.next()) {
				Long id = rs.getLong(1);
				if(currentError != null && currentError.getId() != id) {
					uploadErrors.add(currentError);
					currentError = null;
				}
				if(currentError == null) {
					currentError = new UploadError();
					currentError.setId(id);
					currentError.setCode(rs.getString(2));
					currentError.setUploadField(rs.getString(3));
				}
				I18NUploadErrorText text = new I18NUploadErrorText();
				text.setLocale(rs.getString(4));
				text.setDescription(rs.getString(5));
				currentError.getI18nUploadErrorTexts().add(text);			
			}
			if(currentError != null)
				uploadErrors.add(currentError);
		} finally {
			 if (stmt!=null)
				 stmt.close();
		}
		return uploadErrors;
	}
	
	public FileReceiver getFileReceiver() {
		return fileReceiver;
	}

	public void setFileReceiver(FileReceiver fileReceiver) {
		this.fileReceiver = fileReceiver;
	}

    public UploadManagement findByTemplateName(final String templateName) {
        return (UploadManagement) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery("from UploadManagement where nameOfTemplate = :templateName")
                                .setParameter("templateName", templateName)
                                .uniqueResult();
					}
					
				}
			);
    }
    
    public List<Long> loadIdsByTemplateNameForProcessing(final String templateName){
        return (List<Long>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(UserFileUpload.class)
                                .add(Restrictions.eq("templateName",templateName))
                                .add(Restrictions.in("uploadStatus",  new Object[]{
                                        UploadStatusDetail.STATUS_NOT_PROCESSED,
                                        UploadStatusDetail.STATUS_PROCESSING
                                    }))
                                .add(Restrictions.lt("retryCount",2));
                        criteria.setProjection(Projections.property("id"));
                        return criteria.list();
                        }
					}
			);
    }
	
    public UserFileUpload findFileUploadedById(final Long fileUploadId){
 		return (UserFileUpload) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery("from UserFileUpload where id = :id")
                                .setParameter("id", fileUploadId)
                                .uniqueResult();
					}
					
				}
			);
       
    }

    public User findUploadedUser(final Long fileUploadId) {
        return (User) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select distinct user from UserFileUpload userFileUpload , User user where userFileUpload.id = :id and userFileUpload.uploadedBy = user.id")
                        .setParameter("id", fileUploadId).uniqueResult();
            }

        });

    }

	public PageResult<UserFileUpload> findAllUploadHistoryByOrganization(ListCriteria listCriteria, String templateName,Organization organization) {
		 StringBuffer query = new StringBuffer(" from UserFileUpload userFileUpload JOIN userFileUpload.uploadedBy.belongsToOrganizations orgs WHERE userFileUpload.templateName=:templateName AND orgs.id = :org ");
		 final Map<String, Object> parameterMap = listCriteria.getParameterMap();
		 parameterMap.put("org", organization.getId());
		 parameterMap.put("templateName", templateName);
		 QueryParameters parameters = new QueryParameters(parameterMap);
		 
		 if (listCriteria.isFilterCriteriaSpecified()) {
			 query.append(" and ");
			 query.append(listCriteria.getParamterizedFilterCriteria());
			}
		 
		 return findPageUsingQuery(query.toString(), listCriteria.getSortCriteriaString().toString(), "select userFileUpload ", listCriteria.getPageSpecification(), parameters);
	}
	
}
