/**
 * 
 */
package tavant.twms.domain.campaign;

/**
 * @author mritunjay.kumar
 * 
 */
public interface CampaignNotificationRepository {

	public void generateCampaignNotificationForCampaignItems(
			final Long campaignId, boolean forAllCampaignItems);
	
	public void removeCampaignNotificationsForItems(
			final Long campaignId);

}
