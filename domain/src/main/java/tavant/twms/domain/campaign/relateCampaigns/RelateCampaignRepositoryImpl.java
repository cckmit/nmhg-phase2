package tavant.twms.domain.campaign.relateCampaigns;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class RelateCampaignRepositoryImpl extends
GenericRepositoryImpl<RelateCampaign, Long> implements RelateCampaignRepository {

	@SuppressWarnings("unchecked")
	public void deactivateAllCampaignNotifications(final RelateCampaign relateCampaign) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createSQLQuery("UPDATE relate_campaign rc SET rc.d_active = 0 WHERE " +
								" rc.id = :relateCampaign and rc.d_active = 1 ");
				query.setParameter("relateCampaign", relateCampaign);
				return query.executeUpdate();
			}
		});
	}
	
}
