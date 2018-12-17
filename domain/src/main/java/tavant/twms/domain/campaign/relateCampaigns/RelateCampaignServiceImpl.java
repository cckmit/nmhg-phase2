package tavant.twms.domain.campaign.relateCampaigns;

import java.util.List;

import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class RelateCampaignServiceImpl extends GenericServiceImpl<RelateCampaign, Long, Exception>implements RelateCampaignService {

	private RelateCampaignRepository relateCampaignRepository;

	public PageResult<RelateCampaign> findPage(ListCriteria listCriteria) {
		// TODO Auto-generated method stub
		return relateCampaignRepository.findPage("from RelateCampaign relateCampaign", listCriteria);
	}

	public void setRelateCampaignRepository(
			RelateCampaignRepository relateCampaignRepository) {
		this.relateCampaignRepository = relateCampaignRepository;
	}

	@Override
	public GenericRepository<RelateCampaign, Long> getRepository() {
		return relateCampaignRepository;
	}
	
	public boolean checkDuplicateRelatedCampaign(RelateCampaign relateCampaign) {
		List<RelateCampaign> list = relateCampaignRepository
				.findEntitiesThatMatchPropertyValue("code", relateCampaign);
		if (!isNullOrZero(relateCampaign.getId())) {
			List<RelateCampaign> campaignList = relateCampaignRepository.findEntitiesThatMatchPropertyValue("id", relateCampaign);
			if(campaignList != null && !campaignList.isEmpty()) {
				RelateCampaign modifiableCampaign = campaignList.get(0);
				for (RelateCampaign campaign : list) {
					if (modifiableCampaign.getId().longValue() == campaign.getId()
							.longValue()) {
						list.remove(campaign);
						break;
					}
				}
			}
		}

		return list == null || list.isEmpty();
	}
	
	public void deactivateRelatedCampaign(RelateCampaign relateCampaign) {
		relateCampaignRepository.deactivateAllCampaignNotifications(relateCampaign);
	}
	
	private boolean isNullOrZero(Long id) {
		return id == null || id.longValue() == 0;
	}
}
