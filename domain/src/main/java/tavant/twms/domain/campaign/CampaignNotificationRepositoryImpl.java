/**
 * 
 */
package tavant.twms.domain.campaign;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * @author mritunjay.kumar
 * 
 */
public class CampaignNotificationRepositoryImpl extends JdbcDaoSupport
		implements CampaignNotificationRepository {

	public void generateCampaignNotificationForCampaignItems(
			final Long campaignId, boolean forAllCampaignItems) {

		CallableStatementCallback callBack = new CallableStatementCallback() {
			public Object doInCallableStatement(
					CallableStatement callableStatement) throws SQLException {
				callableStatement.setLong(1, campaignId.longValue());
				callableStatement.execute();
				return null;
			}
		};

		if (forAllCampaignItems) {
			getJdbcTemplate().execute("call GEN_CAMPAIGN_NOTIFICATION_ALL(?)",
					callBack);
		} else {
			getJdbcTemplate().execute("call GEN_CAMPAIGN_NOTIFICATION_NEW(?)",
					callBack);
		}
	}
	
	public void removeCampaignNotificationsForItems(
			final Long campaignId){
		CallableStatementCallback callBack = new CallableStatementCallback() {
			public Object doInCallableStatement(
					CallableStatement callableStatement) throws SQLException {
				callableStatement.setLong(1, campaignId.longValue());
				callableStatement.execute();
				return null;
			}
		};
		
		getJdbcTemplate().execute("call REMOVE_CAMPAIGN_NOTIFICATION(?)",
				callBack);
	}
}
