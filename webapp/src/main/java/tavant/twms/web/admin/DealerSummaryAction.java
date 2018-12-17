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
package tavant.twms.web.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.CriteriaBasedValue;
import tavant.twms.domain.claim.payment.definition.modifiers.DealerSummaryService;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.claim.payment.rates.LaborRate;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.inventory.InvClassDealerMapping;
import tavant.twms.domain.inventory.InvClassDealerMappingService;
import tavant.twms.domain.inventory.InventoryClass;
import tavant.twms.domain.inventory.InventoryClassService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.TechnicianCertification;
import tavant.twms.domain.orgmodel.TechnicianCertificationService;
import tavant.twms.domain.orgmodel.TechnicianDetails;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserAttributeValue;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.web.actions.TwmsActionSupport;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DealerSummaryAction extends TwmsActionSupport {

	private static Logger logger = Logger
			.getLogger(DealerSummaryAction.class);
    private String dealerId;
    private List<LaborRate> laborRatesList;
    private List<TravelRate> travelRatesList;
    private List<CriteriaBasedValue> criteriaBasedValuesList;
    private ServiceProvider serviceProvider;
    private Map<String,List<String>> dealerGroupMap;
    private DealerSummaryService dealerSummaryService;
    private Map<Long,String> criteriaBasedValueSectionNameMap;
	private boolean certificationFlag;
	private String updateFlag;
	private int certification;
    private WarrantyUtil warrantyUtil ;
    private TechnicianDetails technician;
    private Long selectedTechnician;
    private List<TechnicianCertification> certificationList = new ArrayList();
    private TechnicianCertificationService technicianCertificationService;
    private String login;
    private UserRepository userRepository;
    private String emailForSapNotifications;
    
    private InventoryClassService inventoryClassService;
    private InvClassDealerMappingService invClassDlrMappingService;
    private List<InvClassDealerMapping> dealerEligibleClassMappings = new ArrayList<InvClassDealerMapping>();
    private List<InventoryClass> thirtyDayNcrClasses = new ArrayList<InventoryClass>();
    private List<InventoryClass> currentAllowed30DayNcrClasses = new ArrayList<InventoryClass>();
    private List<Long> currentAllowed30DayNcrClassIds = new ArrayList<Long>();
    
	public String getEmailForSapNotifications() {
		return emailForSapNotifications;
	}

	public void setEmailForSapNotifications(String emailForSapNotifications) {
		this.emailForSapNotifications = emailForSapNotifications;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public int getCertification() {
		return certification;
	}

	public void setCertification(int certification) {
		this.certification = certification;
	}
	
    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String showDealerSummaryPage(){
        return SUCCESS;
    }

    public String showTechnicianPage() {
		return SUCCESS;
	}

   public String showTechnicianDetailsPage(){
	   return SUCCESS;
   }

    public String getServiceProviderNames(){
        List<ServiceProvider> serviceProviders = orgService.findDealersWhoseNameStartsWith(getSearchPrefix(), 0, 10) ;

        return generateAndWriteComboboxJson(serviceProviders,"id","name");
    }

    public String getServiceProviderNumbers(){
        List<ServiceProvider> serviceProviders = orgService.findDealersWhoseNumberStartingWith(getSearchPrefix(), 0, 10);

        return generateAndWriteComboboxJson(serviceProviders,"id","serviceProviderNumber");
    }

    public String getDealerData(){
//        Fix for TWMS4.3-685
        long dealerIdAsLong = NumberUtils.toLong(dealerId, -1);
        if(dealerIdAsLong != -1){
            serviceProvider=orgService.findDealerById(dealerIdAsLong);
            this.certification= serviceProvider.getCertified()?1:0;
            Set<BusinessUnit> businessUnitSet=serviceProvider.getBusinessUnits();
            List<String> businessUnitNameList=new ArrayList<String>();
            for(BusinessUnit bu:businessUnitSet){
                businessUnitNameList.add(bu.getName());
            }
            List<DealerGroup> dealerGroups=dealerSummaryService.findAllParentsOfServiceProvider(serviceProvider,
                    AdminConstants.ORGANISTAION_HIERARCHY_PURPOSE,businessUnitNameList);
            if(dealerGroups!=null && !dealerGroups.isEmpty()){
                separateDealerGroup(dealerGroups);
            }
            travelRatesList=dealerSummaryService.findTravelRates(serviceProvider,businessUnitNameList);
            laborRatesList=dealerSummaryService.findLaborRate(serviceProvider,businessUnitNameList);
            criteriaBasedValuesList=dealerSummaryService.findCriteriaBasedValues(serviceProvider,businessUnitNameList);
            setBUSpecificSectionName(criteriaBasedValuesList);
            
            thirtyDayNcrClasses = inventoryClassService.findAll();
            Collections.sort(thirtyDayNcrClasses);
            dealerEligibleClassMappings = invClassDlrMappingService.findInvClassDealerMappings(serviceProvider);
            currentAllowed30DayNcrClasses.clear();
            currentAllowed30DayNcrClassIds.clear();
            for (InvClassDealerMapping icdm : dealerEligibleClassMappings) {
            	currentAllowed30DayNcrClasses.add(icdm.getInventoryClass());
            	currentAllowed30DayNcrClassIds.add(icdm.getInventoryClass().getId());
            }
            Collections.sort(currentAllowed30DayNcrClasses);
        }
        return SUCCESS;

    }

    private void setBUSpecificSectionName(List<CriteriaBasedValue> criteriaBasedValuesList) {
        criteriaBasedValueSectionNameMap=new HashMap<Long,String>();
        for(CriteriaBasedValue criteriaBasedValue:criteriaBasedValuesList){
            if(criteriaBasedValue.getParent()!=null && criteriaBasedValue.getParent().getForPaymentVariable()!=null){
                PaymentVariable paymentVariable=criteriaBasedValue.getParent().getForPaymentVariable();
                Section section=paymentVariable.getSection();
                if(section.getMessageKey().equalsIgnoreCase("label.section.replacedParts")){
                    criteriaBasedValueSectionNameMap.put(criteriaBasedValue.getId(),
                            paymentVariable.getBusinessUnitInfo().getName()+" Parts");
                }
                else if(section.getMessageKey().equalsIgnoreCase("label.section.nonReplacedParts")){
                    criteriaBasedValueSectionNameMap.put(criteriaBasedValue.getId(),
                            "Non "+paymentVariable.getBusinessUnitInfo().getName()+" Parts");
                }else{
                     criteriaBasedValueSectionNameMap.put(criteriaBasedValue.getId(),section.getName());
                }
            }else{
                criteriaBasedValueSectionNameMap.put(criteriaBasedValue.getId(),"");
            }
        }
    }

    private void separateDealerGroup(List<DealerGroup> dealerGroups) {
        Map<String, List<DealerGroup>> dealerGroupMapBasedOnBU = separateDealerGroupBasedOnBU(dealerGroups);
        for (String key : dealerGroupMapBasedOnBU.keySet()) {
            List<DealerGroup> dealerGroupForBU = dealerGroupMapBasedOnBU.get(key);
            List<ArrayList<DealerGroup>> arrayOfListOfDealerGroup =
                    new ArrayList<ArrayList<DealerGroup>>();
            int numberOfListCreated = 0;
            
            for (DealerGroup dealerGroup : dealerGroupForBU) {
                if (dealerGroup.getNodeInfo().getDepth() == 1) {
                    ArrayList<DealerGroup> dealerGroupList = new ArrayList<DealerGroup>();
                    dealerGroupList.add(dealerGroup);
                    arrayOfListOfDealerGroup.add(numberOfListCreated++, dealerGroupList);
                }else{
                    int numberOfListCreatedtillNow = numberOfListCreated;
                    boolean parentFound = false;
                    for (int i = 0; i < numberOfListCreatedtillNow; i++) {
                        List<DealerGroup> dealerGroupList = arrayOfListOfDealerGroup.get(i);
                        if (dealerGroupList.size() == dealerGroup.getNodeInfo().getDepth() - 1) {
                            if (dealerGroupList.get(dealerGroup.getNodeInfo().getDepth() - 2).
                                    getNodeInfo().isAncestorOf(dealerGroup.getNodeInfo())) {
                                dealerGroupList.add(dealerGroup);
                                parentFound = true;
                                break;
                            }
                        }
                    }
                    if (!parentFound) {
                        for (int i = 0; i < numberOfListCreatedtillNow; i++) {
                            ArrayList<DealerGroup> dealerGroupList = arrayOfListOfDealerGroup.get(i);
                            ArrayList<DealerGroup> newdealerGroupList = new ArrayList<DealerGroup>();
                            if (dealerGroupList.size() >= dealerGroup.getNodeInfo().getDepth() - 1 &&
                                    dealerGroupList.get(dealerGroup.getNodeInfo().getDepth() - 2).
                                            getNodeInfo().isAncestorOf(dealerGroup.getNodeInfo())) {
                                for (int j = 0; j <= dealerGroup.getNodeInfo().getDepth() - 2; j++) {
                                    newdealerGroupList.add(j, dealerGroupList.get(j));
                                }
                                newdealerGroupList.add(dealerGroup);
                                arrayOfListOfDealerGroup.add(numberOfListCreated++, newdealerGroupList);
                                break;
                            }
                        }
                    }
                }
            }
            createDealerGroupMapForDisplay(key, arrayOfListOfDealerGroup, numberOfListCreated);
        }
    }

    private void createDealerGroupMapForDisplay(String key, List<ArrayList<DealerGroup>> arrayOfListOfDealerGroup,
                                                int numberOfListCreated) {
        if (dealerGroupMap == null) {
            dealerGroupMap = new HashMap<String, List<String>>();
        }

        for (int i = 0; i < numberOfListCreated; i++) {
            List<String> dealerGroupNames = new ArrayList<String>();
            for (DealerGroup dealerGroup : arrayOfListOfDealerGroup.get(i)) {
                dealerGroupNames.add(dealerGroup.getName());
            }
            dealerGroupMap.put(key + (i + ""), dealerGroupNames);
        }
    }

    
    private Map<String, List<DealerGroup>> separateDealerGroupBasedOnBU(List<DealerGroup> dealerGroups) {
        Map<String, List<DealerGroup>> dealerGroupMap = new HashMap<String, List<DealerGroup>>();
        for (DealerGroup dealerGroup : dealerGroups) {
            List<DealerGroup> dealerGroupForBU = dealerGroupMap.get(dealerGroup.getBusinessUnitInfo().getName());
            if (dealerGroupForBU == null) {
                List<DealerGroup> dealerGroupName = new ArrayList<DealerGroup>();
                dealerGroupName.add(dealerGroup);
                dealerGroupMap.put(dealerGroup.getBusinessUnitInfo().getName(), dealerGroupName);
            } else {
                dealerGroupForBU.add(dealerGroup);
            }
        }
        return dealerGroupMap;
    }

    public void setDealerSummaryService(DealerSummaryService dealerSummaryService) {
        this.dealerSummaryService = dealerSummaryService;
    }

    public List<LaborRate> getLaborRatesList() {
        return laborRatesList;
    }

    public List<TravelRate> getTravelRatesList() {
        return travelRatesList;
    }

    public List<CriteriaBasedValue> getCriteriaBasedValuesList() {
        return criteriaBasedValuesList;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Map<String, List<String>> getDealerGroupMap() {
        return dealerGroupMap;
    }

    public void setDealerGroupMap(Map<String, List<String>> dealerGroupMap) {
        this.dealerGroupMap = dealerGroupMap;
    }

    public Map<Long, String> getCriteriaBasedValueSectionNameMap() {
        return criteriaBasedValueSectionNameMap;
    }

    public void setCriteriaBasedValueSectionNameMap(Map<Long, String> criteriaBasedValueSectionNameMap) {
        this.criteriaBasedValueSectionNameMap = criteriaBasedValueSectionNameMap;
    }
    
	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}

	public boolean getCertificationFlag() {
		return certificationFlag;
	}

	public void setCertificationFlag(boolean certificationFlag) {
		this.certificationFlag = certificationFlag;
	}
	
	public String updateCertificationStatus() {
		
		serviceProvider = orgService.findDealerByNumber(dealerId);
		
		Pattern emailPattern = Pattern.compile(
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		//return !pattern.matcher(email).matches();
		
		if (StringUtils.hasText(emailForSapNotifications) == false
				|| emailPattern.matcher(emailForSapNotifications).matches() == false) {
			
			String wrongInput = StringUtils.hasText(emailForSapNotifications) ? emailForSapNotifications : "Empty value";
			
			addActionError(getText("error.dealer.summary.sap.email.invalid", 
					new String[] {serviceProvider.getDisplayName(), wrongInput}));
			
			return INPUT;
		}
		
		

		certificationFlag = (this.certification == 1) ? true : false;
		serviceProvider.setCertified(certificationFlag);
		serviceProvider.getAddress().setEmailForSapNotifications(emailForSapNotifications);
		
		orgService.updateDealership(serviceProvider);

		Set<BusinessUnit> businessUnitSet = serviceProvider.getBusinessUnits();
		List<String> businessUnitNameList = new ArrayList<String>();
		for (BusinessUnit bu : businessUnitSet) {
			businessUnitNameList.add(bu.getName());
		}
		List<DealerGroup> dealerGroups = dealerSummaryService
				.findAllParentsOfServiceProvider(serviceProvider,
						AdminConstants.ORGANISTAION_HIERARCHY_PURPOSE,
						businessUnitNameList);
		if (dealerGroups != null && !dealerGroups.isEmpty()) {
			separateDealerGroup(dealerGroups);
		}
		travelRatesList = dealerSummaryService.findTravelRates(serviceProvider,
				businessUnitNameList);
		laborRatesList = dealerSummaryService.findLaborRate(serviceProvider,
				businessUnitNameList);
		criteriaBasedValuesList = dealerSummaryService.findCriteriaBasedValues(
				serviceProvider, businessUnitNameList);
		setBUSpecificSectionName(criteriaBasedValuesList);
		addActionMessage("Certification Details Updated for "
				+ serviceProvider.getDisplayName());
		updateFlag = "Updated";
		return SUCCESS;
	}
	
	public String updateShipmentAddress() {
		ServiceProvider serviceProvider1 = orgService.findDealerByNumber(dealerId);
		Address updatedAddress=serviceProvider.getShipmentAddress();
        /*//Fix fofr Bug no NMHGSLMS-1063 ask srini for clarification*/
        if(updatedAddress.getId()==null)
		{
            orgService.createShipmentAddress(updatedAddress);
            serviceProvider1.setShipmentAddress(updatedAddress);
            orgService.updateDealership(serviceProvider1);
            addActionMessage(getText("message.shipment.address")
                    + serviceProvider1.getDisplayName());
			return SUCCESS;
		}
		Address shipmentAddress=serviceProvider1.getShipmentAddress();
		
		shipmentAddress.setAddressLine1(updatedAddress.getAddressLine1());
		shipmentAddress.setAddressLine2(updatedAddress.getAddressLine2());
		shipmentAddress.setCountry(updatedAddress.getCountry());
		shipmentAddress.setState(updatedAddress.getState());
		shipmentAddress.setEmail(updatedAddress.getEmail());
		shipmentAddress.setPhone(updatedAddress.getPhone());
		shipmentAddress.setCity(updatedAddress.getCity());
		shipmentAddress.setFax(updatedAddress.getFax());
		shipmentAddress.setZipCode(updatedAddress.getZipCode());
		orgService.updateShipmentAddress(shipmentAddress);
		addActionMessage(getText("message.shipment.address")
				+ serviceProvider1.getDisplayName());
		return SUCCESS;
	}
	
	public String updateEligible30DayNcrClasses() {
		
		ServiceProvider mSp = orgService.findDealerByNumber(dealerId);
		List<InventoryClass> mCurrentSelection = getCurrentAllowed30DayNcrClasses();
		List<InventoryClass> previousSelection = new ArrayList<InventoryClass>();
		dealerEligibleClassMappings = invClassDlrMappingService.findInvClassDealerMappings(mSp);
		
		
		// Collect all previous applicable classes
		for (InvClassDealerMapping mapping : dealerEligibleClassMappings) {
			
			// If existing mapping is not part of new selection, deactivate
			// A deactivated record is historical, it should not be activated again
			// Whenever a class is selected which was earlier not selected
			// results in a new record, this way we can track who enabled eligibility
			if (false == mCurrentSelection.contains(mapping.getInventoryClass())) {
				mapping.getD().setActive(false);
				invClassDlrMappingService.update(mapping);
			}
			
			previousSelection.add(mapping.getInventoryClass());
		}
		
		for (InventoryClass ic : mCurrentSelection) {
			
			if (ic.getId() == null) {
				continue; // Header Selected
			}	
			
			// For new class selected, create the mapping
			if (false == previousSelection.contains(ic)) {
				InvClassDealerMapping mIcdm = new InvClassDealerMapping();
				mIcdm.setInventoryClass(ic);
				mIcdm.setServiceProvider(mSp);
				invClassDlrMappingService.save(mIcdm);
			}
		}
		
		addActionMessage(getText("message.eligible30dayclass.updated") + " " + mSp.getDisplayName());
		
		return SUCCESS;
	}
	
	public List<OrganizationAddress> getOrgAddresses() {
		List<OrganizationAddress> orgAddresses = null;
		if(serviceProvider != null)
			orgAddresses = orgService.getAddressesForOrganization(serviceProvider);
		if(orgAddresses == null)
			orgAddresses = new ArrayList<OrganizationAddress>();
		Collections.sort(orgAddresses);
		return orgAddresses;
	}

    public WarrantyUtil getWarrantyUtil() {
        return warrantyUtil;
    }

    public void setWarrantyUtil(WarrantyUtil warrantyUtil) {
        this.warrantyUtil = warrantyUtil;
    }

	public Long getSelectedTechnician() {
		return selectedTechnician;
	}

	public void setSelectedTechnician(Long selectedTechnician) {
		this.selectedTechnician = selectedTechnician;
	}

	public Map<Long, String> getTechnicianList() {
		return this.userRepository.findTechnicianForDealer(
				Long.parseLong(dealerId), "Thermo King TSA");
	}

	public String getTechnicianDetails() {
		if (selectedTechnician!=-1) {
			User user = this.userRepository.findById(selectedTechnician);
			setTechnician(fetchTechnicianDetails(user));
			UserAttributeValue attr=fetchLatestAddedUserAttributes(user);
			certificationList=technicianCertificationService.getCertificationForTechnician(attr);
			setLogin(user.getName());
		}
		return SUCCESS;
	}
	
	private TechnicianDetails fetchTechnicianDetails(User user) {
		if (user == null || user.getUserAttrVals() == null)
			return null;
		try {
			for (UserAttributeValue attr : user.getUserAttrVals()) {
				if (AttributeConstants.TECHNICIAN_DETAILS.equals(attr
						.getAttribute().getName())) {
					XStream xstream = new XStream(new DomDriver());
					return (TechnicianDetails) xstream.fromXML(attr.getValue());
				}
			}
		} catch (Exception exception) {
			logger.error("Failed to fetch technician details", exception);
		}
		return null;
	}

	private UserAttributeValue fetchLatestAddedUserAttributes(User user) {
		if (user == null || user.getUserAttrVals() == null)
			return null;
		try {
			for (UserAttributeValue attr : user.getUserAttrVals()) {
				if (AttributeConstants.TECHNICIAN_DETAILS.equals(attr
						.getAttribute().getName())) {
					return attr;
				}
			}
		} catch (Exception exception) {
			logger.error("Failed to fetch technician details", exception);
		}
		return null;
	}
	
	public TechnicianDetails getTechnician() {
		return technician;
	}

	public void setTechnician(TechnicianDetails technician) {
		this.technician = technician;
	}

	public List<TechnicianCertification> getCertificationList() {
		return certificationList;
	}

	public void setCertificationList(List<TechnicianCertification> certificationList) {
		this.certificationList = certificationList;
	}

	public TechnicianCertificationService getTechnicianCertificationService() {
		return technicianCertificationService;
	}

	public void setTechnicianCertificationService(
			TechnicianCertificationService technicianCertificationService) {
		this.technicianCertificationService = technicianCertificationService;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public InventoryClassService getInventoryClassService() {
		return inventoryClassService;
	}
	
	public void setInventoryClassService(InventoryClassService inventoryClassService) {
		this.inventoryClassService = inventoryClassService;
	}
	
	public InvClassDealerMappingService getInvClassDlrMappingService() {
		return invClassDlrMappingService;
	}
	
	public void setInvClassDlrMappingService(InvClassDealerMappingService invClassDlrMappingService) {
		this.invClassDlrMappingService = invClassDlrMappingService;
	}
	
	public List<InventoryClass> getThirtyDayNcrClasses() {
		return thirtyDayNcrClasses;
	}
	
	public List<InventoryClass> getCurrentAllowed30DayNcrClasses() {	
		return currentAllowed30DayNcrClasses;
	}
	
	public void setCurrentAllowed30DayNcrClasses(List<InventoryClass> currentAllowed30DayNcrClasses) {	
		this.currentAllowed30DayNcrClasses = currentAllowed30DayNcrClasses;
	}
	
	public List<Long> getCurrentAllowed30DayNcrClassIds() {
		return currentAllowed30DayNcrClassIds;
	}
	
	public void setCurrentAllowed30DayNcrClassIds(List<Long> currentAllowed30DayNcrClassIds) {
		this.currentAllowed30DayNcrClassIds = currentAllowed30DayNcrClassIds;
	}
}
