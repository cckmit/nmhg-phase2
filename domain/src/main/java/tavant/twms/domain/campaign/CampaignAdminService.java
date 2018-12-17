/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.campaign;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.infra.GenericService;

/**
 * 
 * @author Kiran.Kollipara
 */
public interface CampaignAdminService extends
		GenericService<Campaign, Long, Exception> {


	public static final String CAMPAIGN_DRAFT_STATUS="Draft";
	public static final String CAMPAIGN_ACTIVE_STATUS="Active";
	public static final String CAMPAIGN_INACTIVE_STATUS="Inactive";
	
	public static final String CAMPAIGN_DELETE_ITEMS="delete";
	
	@Transactional(readOnly = true)
	List<CampaignClass> getAllClasses();
	
	@Transactional(readOnly = true)
	List<FieldModificationInventoryStatus> getFieldModificationInventoryStatus();

	@Transactional(readOnly = true)
	Campaign findByCode(String code);

	@Transactional(readOnly = false)
	void generateInventoryItemsForCampaign(Campaign campaign);
	
	@Transactional(readOnly = false)
	public void deactivateCampaign(Campaign campaign);

	@Transactional(readOnly = false)
	public void activateCampaign(Campaign campaign);
	
	@Transactional(readOnly = false)
	public void deactivateNotificationBasedOnRelatedCampaign(Campaign campaign);
	
	public void addActionHistory(Campaign campaign);
	
	@Transactional(readOnly = false)
	public void addActionHistoryAndUpdateCampaign(Campaign campaign);
	
    public List<Campaign> findByIds(Collection<Long> collectionOfIds);
    
    public List<Campaign> findAllCampaignsForLabel(Label label);
}
