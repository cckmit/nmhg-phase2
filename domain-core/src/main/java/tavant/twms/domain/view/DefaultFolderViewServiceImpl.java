package tavant.twms.domain.view;

import java.util.List;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.view.InboxView;
import tavant.twms.domain.query.view.InboxViewService;
import tavant.twms.infra.GenericRepositoryImpl;

public class DefaultFolderViewServiceImpl extends GenericRepositoryImpl<DefaultFolderView,Long> implements DefaultFolderViewService{

	DefaultFolderViewRepository defaultFolderViewRepository;	
	
	public void setDefaultInboxViewForUserAndFolder(User user, String folderName, long inboxViewId) {
		DefaultFolderView defaultFolderView = 
			defaultFolderViewRepository.findDefaultInboxViewForUserAndFolder(user.getId(),folderName);
		if (defaultFolderView == null) {
			defaultFolderView = new DefaultFolderView();
			defaultFolderView.setFolderName(folderName);			
			defaultFolderView.setCreatedBy(user);
		}
		else if (defaultFolderView.getDefaultInboxView().getId() == inboxViewId)
			return;
		defaultFolderView.setDefaultInboxView((InboxView)(getHibernateTemplate().get(InboxView.class,inboxViewId)));
		save(defaultFolderView);
	}
	
	

	public void deleteDefaultInboxViewForUserAndFolder(User user,
			String folderName) {
		defaultFolderViewRepository.deleteDefaultInboxViewForUserAndFolder(user,folderName);
		
	}



	public DefaultFolderView findDefaultInboxViewForUserAndFolder(Long userId, String folderName) {
		return defaultFolderViewRepository.findDefaultInboxViewForUserAndFolder(userId, folderName);
	}

	public DefaultFolderViewRepository getDefaultFolderViewRepository() {
		return defaultFolderViewRepository;
	}

	public void setDefaultFolderViewRepository(
			DefaultFolderViewRepository defaultFolderViewRepository) {
		this.defaultFolderViewRepository = defaultFolderViewRepository;
	}



	public List<DefaultFolderView> findDefaultInboxViewForInboxView(InboxView inboxView) {
		return defaultFolderViewRepository.findDefaultInboxViewForInboxView(inboxView);
		
	}
}
