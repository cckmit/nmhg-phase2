/*
 *   Copyright (c)2006 Tavant Technologies
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

package tavant.twms.domain.upload.history;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author kaustubhshobhan.b
 *
 */

public class UploadHistoryRepositoryImpl extends GenericRepositoryImpl<UploadHistory, Long>
        implements UploadHistoryRepository {


    @SuppressWarnings("unchecked")
    public List<UploadHistory> findUploadHistory(final int pageSize, final String type) {
        return (List<UploadHistory>) getHibernateTemplate().execute(new HibernateCallback() {

            @SuppressWarnings("unchecked")
            public Object doInHibernate(final Session session) throws HibernateException,
                    SQLException {
                List<UploadHistory> pagedClaimHistory =
                    (List<UploadHistory>) session.createQuery("from UploadHistory uploadHistory"
                            + " where uploadHistory.type= :type"
                            + " order by uploadHistory.dateOfUpload desc").setParameter("type", type)
                            .setMaxResults(pageSize).list();
                return pagedClaimHistory;
            }

        });
    }
    
    @SuppressWarnings("unchecked")
	public UploadHistory findPartInventoryUploadHistory(final Long documentId) {
		return (UploadHistory) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"FROM UploadHistory where documentId = :documentId")
								.setLong("documentId", documentId)		
								.list().get(0);
					}
				});
	}

    public Blob getErrorFileContentById(final Long id) {
		return (Blob) getHibernateTemplate().execute(new HibernateCallback() {
			public Blob doInHibernate(Session session)
					throws HibernateException, SQLException {
				return (Blob) session.createSQLQuery(
						"SELECT error_file FROM upload_history WHERE ID =  :id")
						.setLong("id", id).list().get(0);
			}
		});
	}

}
