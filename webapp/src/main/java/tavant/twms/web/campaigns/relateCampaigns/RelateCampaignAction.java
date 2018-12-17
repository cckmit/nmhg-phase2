package tavant.twms.web.campaigns.relateCampaigns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignRepository;
import tavant.twms.domain.campaign.relateCampaigns.RelateCampaign;
import tavant.twms.domain.campaign.relateCampaigns.RelateCampaignService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import com.opensymphony.xwork2.Preparable;

public class RelateCampaignAction extends SummaryTableAction implements Preparable{
	
	RelateCampaign relateCampaign;
	
	CampaignRepository campaignRepository;

	RelateCampaignService relateCampaignService;
	
	List<Campaign> includedcampaignsBeforeModifications;

	public void setIncludedcampaignsBeforeModifications(
			List<Campaign> includedcampaignsBeforeModifications) {
		this.includedcampaignsBeforeModifications = includedcampaignsBeforeModifications;
	}

	@Override
	protected PageResult<?> getBody() {
		return relateCampaignService.findPage(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "String", false,
				true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.code",
				"code", 20, "String", "code", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.description", "description", 50, "String",
				"description", true, false, false, false));
	   return tableHeadData;
	}

	public void prepare() throws Exception {
	}

	@Override
	public void validate() {
		validateRelatedCampaigns();
		if (!hasActionErrors()) {
			if (!relateCampaignService
					.checkDuplicateRelatedCampaign(relateCampaign)) {
				addActionError("relatedCampaign.code.duplicate");
			}
			Set<Long> uniqueCampaignId = new HashSet<Long>();
			for (Campaign campaign : relateCampaign.getIncludedCampaigns()) {
				if (campaign.getId() != null && !uniqueCampaignId.add(campaign.getId())) {
					addActionError("relatedCampaign.campaign.duplicate");
					break;
				}
			}

		}

	}
	
	
	private void validateRelatedCampaigns() {
		String code = relateCampaign.getCode();
		String description = relateCampaign.getDescription();
		List<Campaign> activeCampaigns = new ArrayList<Campaign>();
		for (Campaign campaign : relateCampaign.getIncludedCampaigns()) {
			if(campaign.getId() != null){
				if (campaign.getD().isActive()) {
					activeCampaigns.add(campaign);
				}				
			}
			else{
				addActionError("relatedCampaign.campaign.emptyFieldModification");
				break;
			}
		}
		if(activeCampaigns == null || activeCampaigns.isEmpty() || activeCampaigns.size()<2) {
			addActionError("relatedCampaign.minimumCampaignRequired");
		}
		if(code != null) {
			if(code.trim().length() == 0) {
				addActionError("relatedCampaignCode.noinput", new String[] { "Code" });
			} else {
				relateCampaign.setCode(code.trim());
			}
		}
		if(description != null) {
			if(description.trim().length() == 0) {
				addActionError("relatedCampaignCode.noinput", new String[] { "Description" });
			} else {
				relateCampaign.setDescription(description.trim());
			}
		}
		
	}

	public void setRelateCampaignService(RelateCampaignService relateCampaignService) {
		this.relateCampaignService = relateCampaignService;
	}

	public RelateCampaign getRelateCampaign() {
		return relateCampaign;
	}

	public void setRelateCampaign(RelateCampaign relateCampaign) {
		this.relateCampaign = relateCampaign;
	}
	
	public String loadRelatedCampaignData() {
		this.relateCampaign = this.relateCampaignService.findById(Long
				.valueOf(id));
		List<Campaign> deActivatedCampaigns = new ArrayList<Campaign>();
		for (Campaign campaign : relateCampaign.getIncludedCampaigns()) {
			if (!campaign.getD().isActive()) {
				deActivatedCampaigns.add(campaign);
			}
		}
		relateCampaign.getIncludedCampaigns().removeAll(deActivatedCampaigns);
		return SUCCESS;
	}
	
	public String modifyRelatedCampaign() throws Exception {
		if (hasActionErrors()) {
			return INPUT;
		}
		
		for (Campaign includedcampaignBeforeModification : includedcampaignsBeforeModifications) {
			if (!relateCampaign.getIncludedCampaigns().contains(
					includedcampaignBeforeModification)) {
				List<CampaignNotification> notifications = this.campaignRepository
						.getNotificationForItemsWithCampaign(includedcampaignBeforeModification);
				for (CampaignNotification notification : notifications) {
					if (notification != null
							&& notification.getNotificationStatus().equals(
									"COMPLETE")) {
						List<CampaignNotification> notificationsTobeActivated = campaignRepository
								.getNotificationForItemWithCampaigns(
										notification.getItem(),
										convertToList(getAllRelatedCampaigns(includedcampaignBeforeModification)));

						if (notificationsTobeActivated != null
								&& notificationsTobeActivated.size() > 0) {
							this.campaignRepository
									.activateAllCampaignNotifications(notificationsTobeActivated);
						}
					}
				}
			} else {
				List<CampaignNotification> notifications = this.campaignRepository
						.getNotificationForItemsWithCampaign(includedcampaignBeforeModification);
				List<Campaign> excludedCampaigns = getExcludedCampaigns();
				if (excludedCampaigns != null && excludedCampaigns.size() > 0) {
					for (CampaignNotification notification : notifications) {
						if (notification != null
								&& notification.getNotificationStatus().equals(
										"COMPLETE")) {
							List<CampaignNotification> notificationsTobeActivated = new ArrayList<CampaignNotification>();

							notificationsTobeActivated = campaignRepository
									.getNotificationForItemWithCampaigns(
											notification.getItem(),
											excludedCampaigns);
							if (notificationsTobeActivated != null
									&& notificationsTobeActivated.size() > 0) {
								this.campaignRepository
										.activateAllCampaignNotifications(notificationsTobeActivated);
							}
						}
					}
				}
			}
		}
		deactivateOrActivateCampaignNotificationOfRelatedCampaigns(true);
		this.relateCampaignService.update(relateCampaign);
		addActionMessage("relatedCampaign.update.success");
		return SUCCESS;
	}
	
	public String deleteRelatedCampaign() throws Exception {
		if (hasActionErrors()) {
			return INPUT;
		}
		relateCampaign.setIncludedCampaigns(null);
		this.relateCampaignService.save(relateCampaign);
		this.relateCampaignService.deactivateRelatedCampaign(relateCampaign);
		addActionMessage("relatedCampaign.delete.success");
		return SUCCESS;
	}
	
	public String createRelatedCampaignData() {
		return SUCCESS;
	}
	
	public String saveRelatedCampaign() throws Exception {
		if (hasActionErrors()) {
			return INPUT;
		}
		deactivateOrActivateCampaignNotificationOfRelatedCampaigns(true);
		this.relateCampaignService.save(relateCampaign);
		addActionMessage("relatedCampaign.add.success");
		return SUCCESS;
	}

	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	public List<Campaign> getIncludedcampaignsBeforeModifications() {
		return includedcampaignsBeforeModifications;
	}
	
	private Set<Campaign> getAllRelatedCampaigns(Campaign campaign) {
		Set<Campaign> allRelatedCampaigns = new HashSet<Campaign>();
		for (Campaign toBeIncludedCampaign : relateCampaign
				.getIncludedCampaigns()) {
			if (toBeIncludedCampaign.getId() != campaign.getId()) {
				allRelatedCampaigns.add(toBeIncludedCampaign);
			}
		}

		return allRelatedCampaigns;
	}
	
	private List<Campaign> getExcludedCampaigns(){
		List<Campaign> excludedCampaigns = new ArrayList<Campaign>();
		for(Campaign previuosCampaigns:includedcampaignsBeforeModifications){
			excludedCampaigns.add(previuosCampaigns);
		}
		for(Campaign includedCampaign : relateCampaign
				.getIncludedCampaigns()){
			excludedCampaigns.remove(includedCampaign);
		}
		return excludedCampaigns;
	}
	
	private List<Campaign> convertToList(Set<Campaign> set){
		List<Campaign> list = new ArrayList<Campaign>();
		for(Campaign campaign :set){
			list.add(campaign);
		}
		
		return list;
	}
	
	private void deactivateOrActivateCampaignNotificationOfRelatedCampaigns(
			Boolean deactivate) {

		for (Campaign campaign : relateCampaign.getIncludedCampaigns()) {
			List<CampaignNotification> notifications = this.campaignRepository
					.getNotificationForItemsWithCampaign(campaign);
			for (CampaignNotification notification : notifications) {
				if (notification != null
						&& notification.getNotificationStatus().equals(
								"COMPLETE")) {
					if (!deactivate) {
						List<CampaignNotification> notificationsTobeActivated = campaignRepository
								.getNotificationForItemWithCampaigns(
										notification.getItem(),
										convertToList(getAllRelatedCampaigns(campaign)));

						if (notificationsTobeActivated != null
								&& notificationsTobeActivated.size() > 0) {
							this.campaignRepository
									.activateAllCampaignNotifications(notificationsTobeActivated);
						}
					} else {
						List<CampaignNotification> notificationsTobeDeactivated = campaignRepository
								.getNotificationForItemWithCampaigns(
										notification.getItem(),
										convertToList(getAllRelatedCampaigns(campaign)));

						if (notificationsTobeDeactivated != null
								&& notificationsTobeDeactivated.size() > 0) {
							this.campaignRepository
									.deactivateAllCampaignNotifications(notificationsTobeDeactivated);
						}
					}
				}
			}
		}
	}
	

}
