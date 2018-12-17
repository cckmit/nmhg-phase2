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
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("unchecked")
public interface UploadManagementRepository extends GenericRepository {

	List<UploadManagement> findAll();

	public PageResult<UserFileUpload> findAllUploadHistory(final ListCriteria listCriteria, 
			final String templateName);

	public PageResult<UserFileUpload> findAllUploadHistory(final ListCriteria listCriteria, 
			final String templateName, final Long userId);
	
	UserFileUpload findUserFileUploadById(Long userFileUploadId);

	File getErrorFileById(Long id);
	
	File getUploadedFileById(Long id);
	
	public List<UploadError> getUploadErrorsForUploadMgt(Connection conn, long uploadMgtId)
	throws SQLException;
    
    public UploadManagement findByTemplateName(String templateName);
    
    public List<Long> loadIdsByTemplateNameForProcessing(String templateName);
    
    public UserFileUpload findFileUploadedById(Long fileUploadId);

    public User findUploadedUser(final Long fileUploadId);

    PageResult<UserFileUpload> findAllUploadHistoryByOrganization(ListCriteria listCriteria, String templateName, Organization organization);
}
