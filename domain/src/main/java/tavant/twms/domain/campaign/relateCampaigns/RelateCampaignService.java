package tavant.twms.domain.campaign.relateCampaigns;

import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface RelateCampaignService extends GenericService<RelateCampaign,Long, Exception> {
	
	public PageResult<RelateCampaign> findPage(ListCriteria listCriteria);
	
	public boolean checkDuplicateRelatedCampaign(RelateCampaign relateCampaign);
	
	public void deactivateRelatedCampaign(RelateCampaign relateCampaign);

}
