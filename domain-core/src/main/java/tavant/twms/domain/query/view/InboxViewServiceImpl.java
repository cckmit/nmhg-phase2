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
package tavant.twms.domain.query.view;

import java.util.List;


import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.view.DefaultFolderView;
import tavant.twms.domain.view.DefaultFolderViewService;
import tavant.twms.infra.GenericRepositoryImpl;
/**
 * 
 * @author roopali.agrawal
 *
 */
public class InboxViewServiceImpl extends GenericRepositoryImpl<InboxView,Long> implements InboxViewService {
	InboxViewRepository inboxViewRepository;
	DefaultFolderViewService defaultFolderViewService;
	

	public List<InboxView> findInboxViewForUser(Long userId,String type,String folderName) {
		return inboxViewRepository.findInboxViewForUser(userId,type,folderName);
	}
	
	

	public InboxView findInboxViewByNameAndUser(String name, Long userId, String type) {
		return inboxViewRepository.findInboxViewByNameAndUser(name, userId, type);
	}



	public InboxViewRepository getInboxViewRepository() {
		return inboxViewRepository;
	}

	public void setInboxViewRepository(InboxViewRepository inboxViewRepository) {
		this.inboxViewRepository = inboxViewRepository;
	}



	public InboxView findDefaultInboxViewForUserAndFolder(Long userId, String folderName) {
		DefaultFolderView defaultFolderView = defaultFolderViewService.findDefaultInboxViewForUserAndFolder(userId,folderName);
		if (defaultFolderView == null)
			return null;
		else
			return findById(defaultFolderView.getDefaultInboxView().getId());			
	}


	public DefaultFolderViewService getDefaultFolderViewService() {
		return defaultFolderViewService;
	}



	public void setDefaultFolderViewService(
			DefaultFolderViewService defaultFolderViewService) {
		this.defaultFolderViewService = defaultFolderViewService;
	}
	
	@Override
    public void delete(InboxView inboxView){
		List<DefaultFolderView> defaultFolderViews = defaultFolderViewService.findDefaultInboxViewForInboxView(inboxView);
		defaultFolderViewService.deleteAll(defaultFolderViews);
        getHibernateTemplate().delete(inboxView);
    }
	
}
