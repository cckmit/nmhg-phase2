/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.web.admin.additionalAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AdditionalAttributesService;
import tavant.twms.domain.additionalAttributes.AttributePurpose;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.common.I18NAdditionalAttributeName;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * @author pradipta.a
 */
public class AdditionalAttributeAction extends SummaryTableAction implements
		Preparable {

    private AdditionalAttributesService additionalAttributesService;

    private String attributePurpose;

    private AdditionalAttributes additionalAttributes;

    private String id;
    
    private List<ClaimType> claimTypes = new ArrayList<ClaimType>();    
    
    private ClaimService claimService;
   
    private ProductLocaleService productLocaleService;
    
    private List<ProductLocale> locales;
    
    private boolean showI18nButton;
    
    private boolean isDeleted;

    @Override
    protected PageResult<?> getBody() {
        return additionalAttributesService.findAttributesForPurpose(attributePurpose, getCriteria());
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        header.add(new SummaryTableColumn("columnTitle.newClaim.task", "id", 0, "String", "id", false, true, true,
                false));
        header.add(new SummaryTableColumn("columnTitle.attribute.name", "name", 11, "string", "attributeName", true, false,
                false, false));
        header.add(new SummaryTableColumn("columnTitle.attribute.purpose", "attributePurpose", 11, "string", 
        			"attributePurpose.purpose", SummaryTableColumnOptions.NO_FILTER));
        return header;
    }

    public String preview() {
        if (id != null) {
            additionalAttributes = additionalAttributesService.findAdditionalAttributes(Long.parseLong(id));
        }
        return SUCCESS;
    }

    public String detail() {
        if (id != null) {
            additionalAttributes = additionalAttributesService.findAdditionalAttributes(Long.parseLong(id));
        }
        return SUCCESS;

    }

    @Override
    public void validate() {
    	String name[] = (String[]) ActionContext.getContext().getParameters().get("localizedFailureMessages_en_US");
        if (org.apache.commons.lang.StringUtils.isBlank(name[0])) {
            addActionError("error.additionalAttribute.name");
        }
        if (! StringUtils.hasText(additionalAttributes.getClaimTypes())) {
            addActionError("error.additionalAttribute.Claimtypes");
        }
        if (! StringUtils.hasText(additionalAttributes.getAttributeType())) {
            addActionError("error.additionalAttribute.attributeType");
        }
        
        //Validation for duplicate names.......... commented it for time being (While doing i18N for names.....)
        if(StringUtils.hasText(name[0])
        		&& !hasActionErrors()) {
        	//String name=additionalAttributes.getName();
        	AttributePurpose purpose=null;
        	if (AttributePurpose.PART_SOURCING_PURPOSE.getPurpose().equals(attributePurpose)) {
        		purpose=AttributePurpose.PART_SOURCING_PURPOSE;
            }else if (AttributePurpose.JOB_CODE_PURPOSE.getPurpose().equals(attributePurpose)) {
            	purpose=AttributePurpose.JOB_CODE_PURPOSE;
            }
            else if (AttributePurpose.CLAIMED_INVENTORY_PURPOSE.getPurpose().equals(attributePurpose)) {
            	purpose=AttributePurpose.CLAIMED_INVENTORY_PURPOSE;
            }else if (AttributePurpose.CLAIM_PURPOSE.getPurpose().equals(attributePurpose)) {
            	purpose=AttributePurpose.CLAIM_PURPOSE;
            }
            	if(additionalAttributesService.findAdditionalAttributeByNameForPurpose(name[0], purpose)!=null){
        		addActionError("error.additionalAttribute.duplicacyCheck");
        	}
        }
    }

    public String createOrUpdate() {
    	prepareAdditionalAtrributeName();
        if (AttributePurpose.PART_SOURCING_PURPOSE.getPurpose().equals(attributePurpose)) {
            additionalAttributes.setAttributePurpose(AttributePurpose.PART_SOURCING_PURPOSE);
        }
        if (AttributePurpose.JOB_CODE_PURPOSE.getPurpose().equals(attributePurpose)) {
            additionalAttributes.setAttributePurpose(AttributePurpose.JOB_CODE_PURPOSE);
        }  
       if (AttributePurpose.CLAIMED_INVENTORY_PURPOSE.getPurpose().equals(attributePurpose)) {
           additionalAttributes.setAttributePurpose(AttributePurpose.CLAIMED_INVENTORY_PURPOSE);
       }  
       if (AttributePurpose.CLAIM_PURPOSE.getPurpose().equals(attributePurpose)) {
           additionalAttributes.setAttributePurpose(AttributePurpose.CLAIM_PURPOSE);
       }  
        if (StringUtils.hasText(id)) {
            additionalAttributesService.updateAdditionalAttribute(additionalAttributes);
            addActionMessage("message.additionalAttribute.updateSuccessful");
        } else {
            additionalAttributesService.saveAdditionalAttribute(additionalAttributes);
            addActionMessage("message.additionalAttribute.createSuccessful");
        }

        return SUCCESS;

    }

    public String softDelete() {
    	if (additionalAttributes.getAttributeAssociations() != null
				&& additionalAttributes.getAttributeAssociations().size() > 0) {
			addActionMessage("message.additionalAttribute.deleteFailure");
			return INPUT;
		}

		if (additionalAttributes.getD() != null) {
			additionalAttributes.getD().setActive(Boolean.FALSE);
		}
		additionalAttributesService
				.updateAdditionalAttribute(additionalAttributes);
		isDeleted=true;
		addActionMessage("message.additionalAttribute.deleteSuccessful");
		return SUCCESS;

    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void prepareAdditionalAtrributeName(){
    	additionalAttributes.getI18NAdditionalAttributeNames().clear();
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedMessages[] = (String[]) params
					.get("localizedFailureMessages_" + locale.getLocale());
			if(localizedMessages !=null && i18nLocale.equals("en_US")) {
				if(!StringUtils.hasText(localizedMessages[0]))
					addActionError("error.common.additionalAttributefailureMessageUS");
				else
					additionalAttributes.setName(localizedMessages[0]);
			}
			if(localizedMessages !=null){
				I18NAdditionalAttributeName i18nName = new I18NAdditionalAttributeName();
				i18nName.setName(localizedMessages[0]);
				i18nName.setLocale(i18nLocale);
				additionalAttributes.getI18NAdditionalAttributeNames().add(
						i18nName);
			}
		}
	}
    public String updateAtrributeName(){
    	if(StringUtils.hasText(id)){
    		additionalAttributes = additionalAttributesService.findAdditionalAttributes(new Long(id));
    		prepareAdditionalAtrributeName();
    		if(!hasActionErrors()){
	    		additionalAttributesService.updateAdditionalAttribute(additionalAttributes);
	            addActionMessage("message.additionalAttribute.updateSuccessful");
             	return SUCCESS;
             }
     	}
        return INPUT;
     }
    public void createAttribute() {

    }
    
    public Map<Boolean,String> getYesNo(){
    	Map<Boolean, String> yesNo = new HashMap<Boolean, String>();
    	yesNo.put(true, getText("yes"));
        yesNo.put(false, getText("no"));
        return yesNo;
    }

	public void setAdditionalAttributesService(AdditionalAttributesService additionalAttributesService) {
        this.additionalAttributesService = additionalAttributesService;
    }

    public String getAttributePurpose() {
        return attributePurpose;
    }

    public void setAttributePurpose(String attributePurpose) {
        this.attributePurpose = attributePurpose;
    }

    public AdditionalAttributes getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(AdditionalAttributes additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}

	public void setProductLocaleService(ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public void prepare() throws Exception {
		setClaimTypes();
		locales = productLocaleService.findAll();
		
	}

	public boolean isShowI18nButton() {
		return showI18nButton;
	}

	public void setShowI18nButton(boolean showI18nButton) {
		this.showI18nButton = showI18nButton;
	}
	
	public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}
	
	public void setClaimTypes() {
		 this.claimTypes = this.claimService.fetchAllClaimTypesForBusinessUnit();
	}
}
