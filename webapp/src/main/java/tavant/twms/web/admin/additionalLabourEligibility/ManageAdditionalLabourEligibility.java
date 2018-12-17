package tavant.twms.web.admin.additionalLabourEligibility;



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
import tavant.twms.domain.orgmodel.AdditionalLaborEligibility;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibilityService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.web.i18n.I18nActionSupport;



@SuppressWarnings("serial")
public class ManageAdditionalLabourEligibility extends I18nActionSupport implements ServletResponseAware {
	private static Logger logger = LogManager.getLogger(ManageAdditionalLabourEligibility.class);
	
	private List<ServiceProvider> serviceProviders=new ArrayList<ServiceProvider>();
	
	
	private AdditionalLaborEligibilityService additionalLaborService;
	
	public AdditionalLaborEligibilityService getAdditionalLaborService() {
		return additionalLaborService;
	}


	public void setAdditionalLaborService(
			AdditionalLaborEligibilityService additionalLaborService) {
		this.additionalLaborService = additionalLaborService;
	}
    
	
    public List<ServiceProvider> getServiceProviders() {
		return serviceProviders;
	}


	public void setServiceProviders(List<ServiceProvider> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}


	//Saves  Eligible Dealers for Additional Labour
	public String save(){
		 
		 AdditionalLaborEligibility additionalLaborEligibility = 
			                additionalLaborService.findAddditionalLabourEligibility();
		 if(null == getServiceProviders() || getServiceProviders().isEmpty()){
			 if(null !=additionalLaborEligibility){
				 additionalLaborEligibility.getServiceProviders().clear();
				 additionalLaborService.updateAdditionalLaborEligibility(additionalLaborEligibility);
				 addActionMessage(getText("label.manageALE.update"));
				 return SUCCESS;
		      }else{
		    	  addActionMessage(getText("label.manageALE.update"));
			     return SUCCESS; 
		      }
		 }else{	
			 for(ServiceProvider serviceProvider: serviceProviders){
				   if(null!=serviceProvider){
					 if(null == serviceProvider.getDealerNumber()){
						 if(null !=additionalLaborEligibility){	
							 getServiceProviders().clear();
							 getServiceProviders().addAll(additionalLaborEligibility.getServiceProviders()); 
						 }else{
							 getServiceProviders().clear();
						 }
						 addActionMessage("error.manageMLR.validDealer");
						 return INPUT; 
					 }
				   } 
			 }
			 if(isDuplicateDealers(getServiceProviders())){
				 addActionMessage(getText("label.manageALE.duplicate")); 
				 return INPUT; 
			 }
			 if(null ==additionalLaborEligibility){
				 additionalLaborEligibility = new AdditionalLaborEligibility();
				 additionalLaborEligibility.setServiceProviders(new ArrayList<ServiceProvider>());
				 additionalLaborEligibility.setServiceProviders(getServiceProviders());
				 additionalLaborService.createAdditionalLaborEligibility(additionalLaborEligibility);
				 
			 }else{
				 additionalLaborEligibility.getServiceProviders().clear();
				 additionalLaborEligibility.getServiceProviders().addAll(getServiceProviders());
				 additionalLaborService.updateAdditionalLaborEligibility(additionalLaborEligibility);
			 }
			 
		     addActionMessage(getText("label.manageALE.update"));
			 return SUCCESS;
		 }   
		  	
	}
	
    
	//Loads Eligible Dealers for Additional Labour
	public String load() {
		AdditionalLaborEligibility additionalLaborEligibility = 	
			additionalLaborService.findAddditionalLabourEligibility();
	      if(null !=additionalLaborEligibility){
	    	  getServiceProviders().addAll(additionalLaborEligibility.getServiceProviders());	  
	      }
	      return SUCCESS;
	}


	
	// Checks  duplicate Service Providers
	public static boolean isDuplicateDealers(List<ServiceProvider> serviceProviders) {
		HashSet<ServiceProvider> duplicateServiceProviders = new HashSet<ServiceProvider>();
		 for (int i = 0; i < serviceProviders.size(); i++) {
		  boolean val = duplicateServiceProviders.add(serviceProviders.get(i));
		  if (val == false) {
		  	return true;
		  }
		 }
		 return false;
		}


}
