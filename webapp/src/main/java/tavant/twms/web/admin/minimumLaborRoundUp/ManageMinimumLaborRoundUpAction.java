package tavant.twms.web.admin.minimumLaborRoundUp;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.orgmodel.MinimumLaborRoundUp;
import tavant.twms.domain.orgmodel.MinimumLaborRoundUpService;
import tavant.twms.web.i18n.I18nActionSupport;

@SuppressWarnings("serial")
public class ManageMinimumLaborRoundUpAction extends I18nActionSupport implements ServletResponseAware {
	private static Logger logger = LogManager.getLogger(ManageMinimumLaborRoundUpAction.class);
	
 	private MinimumLaborRoundUpService minimumLaborRoundUpService;
    private MinimumLaborRoundUp minimumLaborRoundUp ;   
    private List<String> claimTypes = new ArrayList<String>();
    private List<String> selectedClaimTypes = new ArrayList<String>();
  
   @Override
    public void validate(){
	   if(null!=minimumLaborRoundUp){
		     if(null!=minimumLaborRoundUp.getApplicableProducts()){ 
		    	//validation for valid product
				 for(ItemGroup appProduct: minimumLaborRoundUp.getApplicableProducts()){
					 if(null != appProduct){
						 if(null == appProduct.getId()){
							 minimumLaborRoundUp.getApplicableProducts().clear();
							 if(null!= minimumLaborRoundUpService.findMinimumLaborRoundUp()){
								 minimumLaborRoundUp= minimumLaborRoundUpService.findMinimumLaborRoundUp();
							 }
							 addActionError(getText("error.manageMLR.validProduct"));
							 break;
						 }
					 }
				 }	
				//validation for duplicates
	 			 if(isDuplicateProducts(minimumLaborRoundUp.getApplicableProducts())){
	 				addActionError(getText("label.manageMLR.duplicate")); 
				 } 
		     }	 
		 
	   } 
		
	}
   
    //Saves  Minimum Labour Round Up 
	 public String save(){
	     	MinimumLaborRoundUp laborRoundUp  
	              = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		     if(null == laborRoundUp){
		    	 setDefaultValuesForLaborRoundUp(minimumLaborRoundUp);
				 minimumLaborRoundUpService.createMinimumLaborRoundUp(minimumLaborRoundUp); 
			 }else{
				setDefaultValuesForLaborRoundUp(laborRoundUp);
				laborRoundUp.setApplCommericalPolicy(minimumLaborRoundUp.getApplCommericalPolicy());
				laborRoundUp.setRoundUpHours(minimumLaborRoundUp.getRoundUpHours());
				laborRoundUp.setDaysBetweenRepair(minimumLaborRoundUp.getDaysBetweenRepair());
				laborRoundUp.getApplicableProducts().clear();
				if (null != minimumLaborRoundUp.getApplicableProducts()) {
					laborRoundUp.getApplicableProducts().addAll(
							minimumLaborRoundUp.getApplicableProducts());
			}
			minimumLaborRoundUpService.updateMinimumLaborRoundUp(laborRoundUp);
			 }
			 addActionMessage(getText("label.manageMLR.update"));
	         return SUCCESS;
	}

	 private void setDefaultValuesForLaborRoundUp(MinimumLaborRoundUp laborRoundUp)
	 {
			laborRoundUp.setApplMachineClaim(Boolean.FALSE);
			laborRoundUp.setApplPartsClaim(Boolean.FALSE);
			laborRoundUp.setApplCampaignClaim(Boolean.FALSE);
			for (String claimType : selectedClaimTypes) {
				if (ClaimType.MACHINE.getType().equals(claimType))
					laborRoundUp.setApplMachineClaim(Boolean.TRUE);
				if (ClaimType.PARTS.getType().equals(claimType))
					laborRoundUp.setApplPartsClaim(Boolean.TRUE);
				if (ClaimType.FIELD_MODIFICATION.getType().equals(claimType))
					laborRoundUp.setApplCampaignClaim(Boolean.TRUE);
			}
	 }
	
	 //Loads  Minimum Labour Round Up 
	public String load() {		  
		  minimumLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();		 
	      return SUCCESS;
	}	
	
	// Checks  duplicate Applicable Products
	public static boolean isDuplicateProducts(List<ItemGroup> applicableProducts) {
		HashSet<ItemGroup> duplicateApplicableProducts = new HashSet<ItemGroup>();
		 for (int i = 0; i < applicableProducts.size(); i++) {
		  boolean val = duplicateApplicableProducts.add(applicableProducts.get(i));
		  if (val == false) {
		  	return true;
		  }
		 }
		 return false;
		}
    
	
	//Setters and Getters
	

	public MinimumLaborRoundUpService getMinimumLaborRoundUpService() {
		return minimumLaborRoundUpService;
	}
	
	public void setMinimumLaborRoundUpService(
			MinimumLaborRoundUpService minimumLaborRoundUpService) {
		this.minimumLaborRoundUpService = minimumLaborRoundUpService;
	}

	public MinimumLaborRoundUp getMinimumLaborRoundUp() {
		return minimumLaborRoundUp;
	}

	public void setMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp) {
		this.minimumLaborRoundUp = minimumLaborRoundUp;
	}	

	public List<String> getClaimTypes() {
		return claimTypes;
	}
	
	public List<String> getClaimTypesForDisplay() {
		claimTypes.add(ClaimType.MACHINE.toString());
		claimTypes.add(ClaimType.PARTS.toString());
		claimTypes.add(ClaimType.FIELD_MODIFICATION.toString());
		return claimTypes;
	}

	public void setClaimTypes(List<String> claimTypes) {
		this.claimTypes = claimTypes;
	}

	public List<String> getSelectedClaimTypes() {
		return selectedClaimTypes;
	}

	public void setSelectedClaimTypes(List<String> selectedClaimTypes) {
		this.selectedClaimTypes = selectedClaimTypes;
	}

}


