package tavant.twms.domain.view;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.view.InboxView;
import tavant.twms.infra.GenericRepositoryImpl;

public class DefaultFolderViewRepositoryImpl extends GenericRepositoryImpl<DefaultFolderView, Long> implements DefaultFolderViewRepository{

	public DefaultFolderView findDefaultInboxViewForUserAndFolder(Long userId, String folderName) {
        String queryString = "select dfv from DefaultFolderView dfv " + "join dfv.createdBy cb "
        					+ "where cb.id=:id and dfv.folderName=:name";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", userId);
        params.put("name", folderName);
        return findUniqueUsingQuery(queryString, params);	}
	
	public void deleteDefaultInboxViewForUserAndFolder(User user, String folderName){
		final String queryString = "delete from DefaultFolderView dfv where "
		+ "dfv.createdBy =:user and dfv.folderName=:name";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("name", folderName);
		getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.executeUpdate();
            }
        });
	}
	
	public List<DefaultFolderView> findDefaultInboxViewForInboxView(InboxView inboxView) {
		String queryString = "select dfv from DefaultFolderView dfv "
				+ "join dfv.createdBy cb "
				+ "where cb.id=:id and dfv.defaultInboxView=:inboxView";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", inboxView.getCreatedBy().getId());
		params.put("inboxView", inboxView);
		return findUsingQuery(queryString, params);
	}
}
