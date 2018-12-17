/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.campaign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAdminService;
import tavant.twms.domain.campaign.CampaignRangeCoverage;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author Kiran.Kollipara
 *
 */
@SuppressWarnings("serial")
public class PreviewCampaignAction extends I18nActionSupport implements Preparable {

    private String id;
    private Campaign campaign;
    private String campaignFor;
    private ConfigParamService configParamService;
    private Map<String,CostCategory> configuredCostCategories = new HashMap<String,CostCategory>();
    private CampaignAdminService campaignAdminService;

    public void prepare() throws Exception {
        Assert.notNull(id, "Campaign definition id cannot be null/empty");
        Long idTobeUsed = Long.parseLong(id);
        campaign = campaignAdminService.findById(idTobeUsed);
    	this.setConfiguredCostCategories(configuredCostCategories);
        if(campaign.getCampaignCoverage() != null && campaign.getCampaignCoverage().getRangeCoverage() != null){
        	campaignFor = "SERIAL_NUMBER_RANGES";
        }else{
        	campaignFor = "SERIAL_NUMBERS";
        }
    }
    
    @Override
    public String execute() {
        return SUCCESS;
    }

    public Campaign getCampaign() {
        return campaign;
    }
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Required
    public void setCampaignAdminService(CampaignAdminService campaignAdminService) {
        this.campaignAdminService = campaignAdminService;
    }

	public String getCampaignFor() {
		return campaignFor;
	}

	public void setCampaignFor(String campaignFor) {
		this.campaignFor = campaignFor;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public Map<String,CostCategory> getConfiguredCostCategories() {
		return configuredCostCategories;
	}

	public void setConfiguredCostCategories(Map<String,CostCategory> configuredCostCategories) {
	    List<Object> costCategoryObjects = configParamService
        .getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES.getName());
        for (Object object : costCategoryObjects) {
        	CostCategory costCategory = new HibernateCast<CostCategory>().cast(object);
        	this.configuredCostCategories.put(costCategory.getCode(),costCategory);
        }
	}


}