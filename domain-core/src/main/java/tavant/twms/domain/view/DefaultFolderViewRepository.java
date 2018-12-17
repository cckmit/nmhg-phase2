package tavant.twms.domain.view;

import java.util.List;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.view.InboxView;
import tavant.twms.infra.GenericRepository;

public interface DefaultFolderViewRepository extends GenericRepository<DefaultFolderView,Long>{
	public DefaultFolderView findDefaultInboxViewForUserAndFolder(Long userId, String folderName);
	
	public void deleteDefaultInboxViewForUserAndFolder(User user, String folderName);
	
	public List<DefaultFolderView> findDefaultInboxViewForInboxView(InboxView inboxView);
}
