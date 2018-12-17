package tavant.twms.domain.campaign.relateCampaigns;

import tavant.twms.infra.GenericRepository;

public interface RelateCampaignRepository extends GenericRepository<RelateCampaign,Long> {
	
	public void deactivateAllCampaignNotifications(final RelateCampaign relateCampaign);
	

}
