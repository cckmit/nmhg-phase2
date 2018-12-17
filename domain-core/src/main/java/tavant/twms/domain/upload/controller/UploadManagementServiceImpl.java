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
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("unchecked")
public class UploadManagementServiceImpl extends GenericServiceImpl implements UploadManagementService {

	private UploadManagementRepository uploadManagementRepository;
	
	/* (non-Javadoc)
	 * @see tavant.twms.infra.GenericServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository getRepository() {
		return uploadManagementRepository;
	}

	public void setUploadManagementRepository(
			UploadManagementRepository uploadManagementRepository) {
		this.uploadManagementRepository = uploadManagementRepository;
	}
    
    public UploadManagement findByTemplateName(String templateName){
        return this.uploadManagementRepository.findByTemplateName(templateName);
    }
	
	public List<UploadManagement> findAll()
	{
		return this.uploadManagementRepository.findAll();
	}
	
	public PageResult<UserFileUpload> findAllUploadHistory(final ListCriteria listCriteria, 
			final String templateName) {
		return this.uploadManagementRepository.findAllUploadHistory(listCriteria, templateName);
	}

	public PageResult<UserFileUpload> findAllUploadHistory(final ListCriteria listCriteria, 
			final String templateName, final Long userId) {
		return this.uploadManagementRepository.findAllUploadHistory(listCriteria, templateName, userId);
	}
	
	public UserFileUpload findUserFileUploadById(Long userFileUploadId) {
		return this.uploadManagementRepository.findUserFileUploadById(userFileUploadId);
	}

	public File getErrorFileById(Long id) {
		return this.uploadManagementRepository.getErrorFileById(id);
	}
	
	public File getUploadedFileById(Long id) {
		return this.uploadManagementRepository.getUploadedFileById(id);
	}

	public List<UploadError> getUploadErrorsForUploadMgt(Connection conn, long uploadMgtId)
	throws SQLException {
		return this.uploadManagementRepository.getUploadErrorsForUploadMgt(conn, uploadMgtId);
	}

    public List<Long> loadIdsByTemplateNameForProcessing(String templateName){
        return this.uploadManagementRepository.loadIdsByTemplateNameForProcessing(templateName);
    }
    
    public UserFileUpload findFileUploadedById(Long fileUploadId){
        return this.uploadManagementRepository.findFileUploadedById(fileUploadId);
    }

    public User findUploadedUser(Long fileUploadId){
        return this.uploadManagementRepository.findUploadedUser(fileUploadId);
    }

	public PageResult<UserFileUpload> findAllUploadHistoryByOrganization(ListCriteria listCriteria, String templateName, Organization organization) {
		return this.uploadManagementRepository.findAllUploadHistoryByOrganization(listCriteria, templateName, organization);
	}
    
    
}
