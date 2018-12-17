package tavant.twms.domain.view;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.view.InboxView;
import tavant.twms.infra.GenericService;

public interface DefaultFolderViewService extends GenericService<DefaultFolderView,Long, Exception>{
	
	@Transactional(readOnly=false)
	public void setDefaultInboxViewForUserAndFolder(User user, String folderName, long inboxViewId);
	
	@Transactional(readOnly=false)
	public void deleteDefaultInboxViewForUserAndFolder(User user, String folderName);

	@Transactional(readOnly=true)
	public DefaultFolderView findDefaultInboxViewForUserAndFolder(Long userId, String folderName);
	
	@Transactional(readOnly=true)
	public List<DefaultFolderView> findDefaultInboxViewForInboxView(InboxView inboxView);
}
