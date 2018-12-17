package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.common.I18nDomainTextReader;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SecurityHelper;
import com.domainlanguage.timeutil.Clock;
/**
 * This class is used for setting the details for unit service history
 * @author TWMSUSER
 */
public class InventoryItemUtil {
    
    private SecurityHelper securityHelper;
    private ClaimService claimService;
    private final String terminatedStatus = RegisteredPolicyStatusType.TERMINATED.getStatus();
    private final String activeStatus = RegisteredPolicyStatusType.ACTIVE.getStatus();
    private Organization retailedDealer = null;
    private OrgService orgService;
    
    private InventoryService inventoryService;
    
    private ConfigParamService configParamService;
    
    public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	private I18nDomainTextReader i18nDomainTextReader;
    
    
    
    private final static String OEM = "OEM";
    private final static String THIRD_PARTY = "THIRDPARTY";
    public final static String PURCHASE_DATE = "purchaseDate";
    public final static String END_DATE = "endDate";
    public final static String START_DATE = "startDate";
    public final static String MONTHS_COVERED = "monthsCovered";    
    public final static String DEFINITION_ID = "definitionId";
    public final static String POLICY_CODE = "policyCode";
    public final static String POLICY_NAME = "policyName";
    public final static String POLICY_TERMS_CONDITIONS = "policyTermsConditions";
    public final static String TYPE = "type";
    public final static String PURCHASE_ORD_NUMBER = "purchaseOrdNumber";
    public final static String COMMENTS = "comments";
    public final static String AUDIT_SIZE = "auditSize";
    public final static String POLICY_STATUS_FOR_DISPLAY = "policyStatusForDisplay";
    public final static String POLICY_STATUS = "policyStatus";
    public final static String CURRENT_POLICY_STATUS = "currentPolicyStatus";
    public final static String HOURS_COVERED = "hoursCovered";
    public final static String ACCEPTED_CLOSED = "Accepted and Closed";
    public final static String ID = "id";

    public boolean isInternalUser() {
        return this.orgService.isInternalUser(getLoggedInUser());
    }
    
    public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
        this.i18nDomainTextReader = i18nDomainTextReader;
    }

    public boolean isRetailedDealer() {
        boolean isRetailingDealer = false;
        ServiceProvider dealer = getLoggedInUsersDealership();
        if (dealer != null
                && this.retailedDealer != null
                && this.retailedDealer.getId().longValue() == dealer
                                .getId().longValue()) {
            isRetailingDealer = true;
        } 
        return isRetailingDealer;
    }
    
    /**
     * The method returns the Logged in Users dealership
     * @return ServiceProvider
     */
    private ServiceProvider getLoggedInUsersDealership() {
        ServiceProvider loggedInUserOrganization = null;
        Organization userOrg = securityHelper.getLoggedInUser().getCurrentlyActiveOrganization();
        
        if(userOrg != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, userOrg)) {
            loggedInUserOrganization = new HibernateCast<ServiceProvider>().cast(userOrg);
        }
        return loggedInUserOrganization;
    }
    
    public boolean checkLoggedInDealerOwner(InventoryItem item) {
        if (!getLoggedInUser().getUserType().equals(AdminConstants.SUPPLIER_USER) && this.orgService.isDealer(getLoggedInUser())) {
            ServiceProvider loggedInUserDealership = getLoggedInUsersDealership();
            
            Organization itemCurrentOwner = item.getCurrentOwner();
            if (itemCurrentOwner == null && item.getSerializedPart()) {
            	itemCurrentOwner = (Organization)inventoryService.findInventoryItemForMajorComponent(item.getId()).getOwnedBy();
            }

            boolean canSearchOtherDealersRetail  = Boolean.FALSE;
            if(item.getType().equals(InventoryType.RETAIL)){
            	canSearchOtherDealersRetail = configParamService.getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
            }
                    
            if (loggedInUserDealership != null
                    && (loggedInUserDealership.getId().equals(itemCurrentOwner.getId()) || canSearchOtherDealersRetail
                    || (loggedInUserDealership.getChildDealersIds()!= null
                        && loggedInUserDealership.getChildDealersIds().contains(itemCurrentOwner.getId())))) {
                return true;
            }

            //if logged in dealer is parent of some dealer he is allowed NMHGSLMS-387
            List<Long> dealerIds = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
            if(dealerIds.contains(item.getCurrentOwner().getId())){
                return true;
            }
            
        }
        return false;

    }
    
    public Party getInventoryItemOwner(InventoryItem inventoryItem) {
    	 Party party = inventoryItem.getOwnedBy();
         
         if (party == null && inventoryItem.getSerializedPart()) { // party may be null if inventoryItem is MajorComponent
         	InventoryItem hostedOnInventory = inventoryService.findInventoryItemForMajorComponent(inventoryItem.getId());
         	party = hostedOnInventory.getOwnedBy();
         }
         return party;
    }
    
    
    /**
     * The method checks if the user logged in is same as the owner.
     * @param invTransaction
     * @return boolean true if the logged in user is same as the owner.
     */
    public boolean isLoggedInUserOwnerOfTrnx(InventoryTransaction invTransaction) {
    	boolean isSameDealerAndOwner = false;
    	List<Long> childDealers =orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
    	childDealers.add(getLoggedInUser().getBelongsToOrganization().getId());
    	//ServiceProvider loggedInUserOrg = getLoggedInUsersDealership();
    	if(invTransaction.getOwnerShip() != null && childDealers.size() >0
    	&& (childDealers.contains((new HibernateCast<Organization>()
    	.cast(invTransaction.getOwnerShip())).getId()
    	.longValue()))) {
    	isSameDealerAndOwner = true;
    	}
    	return isSameDealerAndOwner;
    	}
    
    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }
    
    /**
     * The method returns a list of Map containing policies registered.
     * @param policies
     * @param inventoryItem
     * @param isEditable
     * @return List<Map<String, Object>>> 
     */
    public List<Map<String, Object>> getPolicyDetails(Collection<RegisteredPolicy> policies, 
            InventoryItem inventoryItem, boolean isEditable) {
        List<Map<String, Object>> policyList = new ArrayList<Map<String, Object>>();
        boolean addThirdPartyPolicyOnly = false;
        boolean showCurrentPolicy = true;
        Long hoursOnMachine = inventoryItem.getHoursOnMachine();
        User user = securityHelper.getLoggedInUser();
        if (user != null && user.getBelongsToOrganization() != null
                && !user.getBelongsToOrganization().getName().equalsIgnoreCase(OEM)) {
            if (user.getBelongsToOrganization().getType().equalsIgnoreCase(THIRD_PARTY)) {
                addThirdPartyPolicyOnly = true;
            }
        }
        String liuDateFormat = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
        for (RegisteredPolicy policy : policies) {
            showCurrentPolicy = true;
            if (addThirdPartyPolicyOnly) {
                if (policy != null && policy.getPolicyDefinition() != null
                        && !policy.getPolicyDefinition().getIsThirdPartyPolicy()) {
                    showCurrentPolicy = false;
                }
            } 
            if (showCurrentPolicy) {

                Map<String, Object> policyMap = new HashMap<String, Object>();
                policyMap.put(ID, policy.getId());              
                PolicyDefinition policyDefinition = policy.getPolicyDefinition();
                policyMap.put(DEFINITION_ID, policyDefinition.getId());
                policyMap.put(POLICY_CODE, policyDefinition.getCode());
                policyMap.put(POLICY_NAME, policyDefinition.getDescription());
                policyMap.put(POLICY_TERMS_CONDITIONS, policyDefinition.getTermsAndConditions());
                if (WarrantyType.EXTENDED.getType().equals(
                        policy.getPolicyDefinition().getWarrantyType().getType()))
                    policyMap.put(PURCHASE_ORD_NUMBER,
                            policy.getPurchaseOrderNumber() != null ? policy
                                    .getPurchaseOrderNumber() : "");
                else
                    policyMap.put(PURCHASE_ORD_NUMBER, "");
                policyMap.put(TYPE, policy.getWarrantyType().getType());
                if (policy.getWarrantyPeriod() == null) {
                    CalendarDuration warrantyPeriod = null;
                    try {
                        if (!WarrantyType.POLICY.getType().equals(
                                policy.getPolicyDefinition().getWarrantyType().getType())) {
                            warrantyPeriod = policy.getPolicyDefinition().warrantyPeriodFor(
                                    inventoryItem);
                        }
                        if (warrantyPeriod != null) {
                            policyMap.put(START_DATE, warrantyPeriod.getFromDate().toString(
                                    liuDateFormat));
                            policyMap.put(END_DATE, warrantyPeriod.getTillDate().toString(
                                    liuDateFormat));
                            if (WarrantyType.EXTENDED.getType().equals(
                                    policy.getPolicyDefinition().getWarrantyType().getType())
                                    && policy.getPurchaseDate() != null) {
                                policyMap.put(PURCHASE_DATE, policy.getPurchaseDate().toString(
                                        liuDateFormat));
                            } else
                                policyMap.put(PURCHASE_DATE, "");
                        }
                    } catch (PolicyException e) {
                    }

                } else {
                    policyMap.put(START_DATE,
                            policy.getWarrantyPeriod().getFromDate() != null ? policy
                                    .getWarrantyPeriod().getFromDate().toString(liuDateFormat)
                                    : null);
                    policyMap.put(END_DATE,
                            policy.getWarrantyPeriod().getTillDate() != null ? policy
                                    .getWarrantyPeriod().getTillDate().toString(liuDateFormat)
                                    : null);
                    if (WarrantyType.EXTENDED.getType().equals(
                            policy.getPolicyDefinition().getWarrantyType().getType())
                            && policy.getPurchaseDate() != null) {
                        policyMap.put(PURCHASE_DATE, policy.getPurchaseDate().toString(
                                liuDateFormat));
                    } else
                        policyMap.put(PURCHASE_DATE, "");

                }
                 if (policy.getLatestPolicyAudit() != null
                        && RegisteredPolicyStatusType.TERMINATED.getStatus().equalsIgnoreCase(
                                policy.getLatestPolicyAudit().getStatus())) {
                    policyMap.put(MONTHS_COVERED, "0");
                } else if (policy.getWarrantyPeriod() != null && policy.getWarrantyPeriod()
                        .getFromDate()!=null && policy.getWarrantyPeriod().getTillDate()!=null) {
					if (policy.getWarrantyPeriod()
						.getFromDate().equals(
								policy.getWarrantyPeriod()
								.getTillDate()))
					{
						policyMap.put("monthsCovered", Integer.toString(0));
					}
					else
					{
                    policyMap.put(MONTHS_COVERED, Integer.toString(policy.getWarrantyPeriod()
                            .getFromDate().through(policy.getWarrantyPeriod().getTillDate()) .lengthInMonthsInt()));
                    }
                } else {
                	 if (policy.getWarrantyPeriod() == null) {
                         CalendarDuration warrantyPeriod = null;
                         try {
                             if (!WarrantyType.POLICY.getType().equals(
                                     policy.getPolicyDefinition().getWarrantyType().getType())) {
                                 warrantyPeriod = policy.getPolicyDefinition().warrantyPeriodFor(
                                         inventoryItem);
                             }
                             if (warrantyPeriod != null) {
                                     policyMap.put(MONTHS_COVERED, Integer.toString(warrantyPeriod.getFromDate().through(warrantyPeriod.getTillDate()).lengthInMonthsInt()));
                                 } 
                         } catch (PolicyException e) {
                         }
                  

                     }
                  
                }
                policyMap.put(COMMENTS, policy.getLatestPolicyAudit() != null ? policy
                        .getLatestPolicyAudit().getComments() : "");

                /**
                 * this logic is to get end date/comments/status in the new
                 * audit to be created if there is a validation error in the
                 * page, to retain audit at the same location
                 */
                if (!isEditable) {
                    policyMap.put(AUDIT_SIZE, policy.getPolicyAudits() != null ? new Long(policy
                            .getPolicyAudits().size()).toString() : "0");
                    if(policy.getLatestPolicyAudit() != null) {
                    	Integer serviceHoursCovered = policy.getLatestPolicyAudit().getServiceHoursCovered();
                    	if(((policy.getLatestPolicyAudit().getStatus().equals(RegisteredPolicyStatusType.ACTIVE))&& (hoursOnMachine != null && serviceHoursCovered != null && (hoursOnMachine > serviceHoursCovered))
                    			|| policy.getWarrantyPeriod().getTillDate().isBefore(Clock.today()))) {
                    		 policyMap.put(POLICY_STATUS_FOR_DISPLAY,RegisteredPolicyStatusType.INACTIVE.getStatus());
                        } else {
                        	 policyMap.put(POLICY_STATUS_FOR_DISPLAY,policy.getLatestPolicyAudit().getDisplayStatus());
                        }
                    } else {
                    	 policyMap.put(POLICY_STATUS_FOR_DISPLAY,"");
                    }
                    policyMap.put(POLICY_STATUS, policy.getLatestPolicyAudit() != null ? policy
                            .getLatestPolicyAudit().getStatus() : "");
                    policyMap.put(CURRENT_POLICY_STATUS,
                            policy.getLatestPolicyAudit() != null ? policy.getLatestPolicyAudit()
                                    .getStatus() : "");
                    policyMap.put(HOURS_COVERED, policy.getLatestPolicyAudit() != null ? policy
                            .getLatestPolicyAudit().getServiceHoursCovered() : policy.getPolicyDefinition().getCoverageTerms().getServiceHoursCovered());
                } else {
                    policyMap.put(AUDIT_SIZE, CollectionUtils.isNotEmpty(policy.getPolicyAudits())? new Long(policy
                            .getPolicyAudits().size() - 1).toString() : "0");
                    if (policy.getPolicyAudits() != null && policy.getPolicyAudits().size() > 0) {
                        if (terminatedStatus.equals(policy.getLatestPolicyAudit().getStatus())) {
                            String status = activeStatus;
                            Integer serviceHoursCovered = policy.getLatestPolicyAudit().getServiceHoursCovered();
                            if(((policy.getLatestPolicyAudit().getStatus().equals(RegisteredPolicyStatusType.ACTIVE)) && (hoursOnMachine != null && serviceHoursCovered != null && (hoursOnMachine > serviceHoursCovered ))
                        			|| policy.getWarrantyPeriod().getTillDate().isBefore(Clock.today()))) {
                            	status = RegisteredPolicyStatusType.INACTIVE.getStatus();
                            }
                            policyMap.put(POLICY_STATUS_FOR_DISPLAY, status);
                            policyMap.put(POLICY_STATUS, activeStatus);
                            policyMap.put(CURRENT_POLICY_STATUS, terminatedStatus);
                        } else if (activeStatus.equals(policy.getLatestPolicyAudit().getStatus())) {
                            policyMap.put(POLICY_STATUS_FOR_DISPLAY, terminatedStatus);
                            policyMap.put(POLICY_STATUS, terminatedStatus);
                            policyMap.put(CURRENT_POLICY_STATUS, activeStatus);
                        } else {
                            // to show the previous state in case if validation
                            // failure
                            if (policy.getPolicyAudits() != null
                                    && policy.getPolicyAudits().size() >= 2) {
                            	 Integer serviceHoursCovered = policy.getLatestPolicyAudit().getServiceHoursCovered();
                            	if((( policy.getPolicyAudits()
                                        .get(policy.getPolicyAudits().size() - 2).getStatus().equals(RegisteredPolicyStatusType.ACTIVE)) && (hoursOnMachine != null && serviceHoursCovered != null && (hoursOnMachine > serviceHoursCovered ))
                              			|| policy.getWarrantyPeriod().getTillDate().isBefore(Clock.today()))) {
                            		policyMap.put(POLICY_STATUS_FOR_DISPLAY, RegisteredPolicyStatusType.INACTIVE.getStatus());
                            	}
                            	else
                            	{
                            		 policyMap.put(POLICY_STATUS_FOR_DISPLAY, policy.getPolicyAudits()
                                             .get(policy.getPolicyAudits().size() - 2)
                                             .getDisplayStatus());
                            	}
                               
                                policyMap.put(POLICY_STATUS, policy.getPolicyAudits().get(
                                        policy.getPolicyAudits().size() - 2).getStatus());
                                policyMap.put(CURRENT_POLICY_STATUS, policy.getPolicyAudits().get(
                                        policy.getPolicyAudits().size() - 2).getStatus());
                            } else {
                                policyMap.put(POLICY_STATUS_FOR_DISPLAY, policy
                                        .getLatestPolicyAudit().getStatus() != null ? policy
                                        .getLatestPolicyAudit().getDisplayStatus() : "");
                                policyMap.put(POLICY_STATUS, policy.getLatestPolicyAudit()
                                        .getStatus() != null ? policy.getLatestPolicyAudit()
                                        .getStatus() : "");
                                policyMap.put(CURRENT_POLICY_STATUS, policy.getLatestPolicyAudit()
                                        .getStatus() != null ? policy.getLatestPolicyAudit()
                                        .getStatus() : "");
                            }
                        }
                        if (policy.getPolicyAudits() != null
                                && policy.getPolicyAudits().size() >= 2) {
                            policyMap.put(HOURS_COVERED, policy.getPolicyAudits().get(
                                    policy.getPolicyAudits().size() - 2).getServiceHoursCovered());
                        }
                    } else {
                    	
                        policyMap.put(POLICY_STATUS_FOR_DISPLAY, "");
                        policyMap.put(POLICY_STATUS, "");
                        policyMap.put(CURRENT_POLICY_STATUS, "");
                        policyMap.put(HOURS_COVERED, "0");
                    }
                }              
                policyList.add(policyMap);
            }
        }
        return policyList;

    }
    
    /**
     * The method returns the ServiceProvider.
     * @param inventoryItem
     * @return Organization
     */
    public Organization getRetailedDealer(InventoryItem inventoryItem) {
        if (inventoryItem.isRetailed()) {
                for (InventoryTransaction invTransaction : inventoryItem
                                .getTransactionHistory()) {
                        if (InvTransationType.RMT.getTransactionType().equals(
                                        invTransaction.getInvTransactionType()
                                                        .getTrnxTypeValue())) {
                                if (getLoggedInUsersDealership() != null
                                                && invTransaction.getSeller().getId().longValue() == getLoggedInUsersDealership()
                                                                .getId().longValue()) {
                                        // person to whom RMT is done is viewing EHP
                                    this.retailedDealer = new HibernateCast<ServiceProvider>()
                                                        .cast(invTransaction.getSeller());
                                    break;
                                }
                        }
                        if (InvTransationType.DR.getTransactionType().equals(
                                        invTransaction.getInvTransactionType()
                                                        .getTrnxTypeValue()) || (isCustomerDetailsNeededForDR_Rental()?InvTransationType.DR_RENTAL.getTransactionType().equals(
                                                                invTransaction.getInvTransactionType()
                                                                .getTrnxTypeValue()):false) || InvTransationType.DEMO.getTransactionType().equals(
                                                                        invTransaction.getInvTransactionType()
                                                                        .getTrnxTypeValue()) ) { //SLMSPROD-1174, demo should work in the same way as that of End customer
                            this.retailedDealer = new HibernateCast<ServiceProvider>()
                                                .cast(invTransaction.getSeller());
                            break;
                        }
                }
        } else {
        	inventoryItem.setInventoryService(inventoryService);
            this.retailedDealer = new HibernateCast<ServiceProvider>()
                    .cast(inventoryItem.getDealer());
        }
        return this.retailedDealer;
    }
    
    /**
     * 
     * @param inventoryItem
     * @return boolean - true if Owner info can be sent/displayed to the user.
     */
    public boolean isCanViewOwnerInfo(InventoryItem inventoryItem) {
        boolean canView = false;
        if (getLoggedInUsersDealership() != null) {

                // A dealer can see the owner information only he is responsible for
                // the latest trnx, only exception
                // RMT where both the dealers can see the owner information

        		List<InventoryTransaction> transactionHistory = inventoryItem.getTransactionHistory();
        		
        		if (!transactionHistory.isEmpty()) {
        			InventoryTransaction invTransaction = inventoryItem
                    .getTransactionHistory().get(0);

				    if (InvTransationType.RMT.getTransactionType().equals(
				                    invTransaction.getInvTransactionType().getTrnxTypeValue())) {
				            if (getLoggedInUsersDealership() != null
				                            && invTransaction.getSeller().getId().longValue() == getLoggedInUsersDealership()
				                                            .getId().longValue()) {
				                    // person to whom RMT is done is viewing EHP, so owner
				                    // info shld be shown
				                    canView = true;
				            }
				    } else {
				            if (!InvTransationType.EXTENED_WNTY_PURCHASE
				                            .getTransactionType().equals(
				                                            invTransaction.getInvTransactionType()
				                                                            .getTrnxTypeValue())) {
				                    if (isLoggedInUserOwnerOfTrnx(invTransaction)) {
				                            canView = true;
				                    }
				            } else {
				                    invTransaction = inventoryItem.getTransactionHistory()
				                                    .get(1);
				                    if (isLoggedInUserOwnerOfTrnx(invTransaction)) {
				                            canView = true;
				                    }
				            }
				    }
        		}
                
        }
        return canView;
    }

    /**
     * The method returns the Claims for the inventory item.
     * @param inventoryItem
     * @return Collection<Claim>
     */
    public Collection<Claim> getClaimsToBeViewed(InventoryItem inventoryItem) {
        Collection<Claim> previousClaimsForItem = null;
        
        if(!inventoryItem.getSerializedPart()){        
	        previousClaimsForItem = this.claimService
	                        .findAllPreviousClaimsForItem(inventoryItem.getId());
        }else{
        	 previousClaimsForItem = this.claimService
             .findAllPreviousClaimsForMajorComp(inventoryItem.getId());
        }        	
        if(!this.isRetailedDealer() && !this.isInventoryFullView(inventoryItem)) {        	
			Iterator<Claim> iterator = previousClaimsForItem.iterator();
			while (iterator.hasNext()) {
				Claim claim = iterator.next();
				if (!claim.getState().getState().equals(ACCEPTED_CLOSED)
						& claim.getForDealerShip().getId().longValue() != getLoggedInUsersDealership()
								.getId().longValue()) {
					iterator.remove();
				}
			}
        }
        return previousClaimsForItem;
    }
    
    public boolean isDifferentDealerAndOwner(InventoryItem inventoryItem) {
        boolean isDifferentDealerAndOwner = false;
        if (this.retailedDealer != null) {
        	Party owner = inventoryItem.getOwnedBy();
        	
        	if (owner == null && inventoryItem.getSerializedPart()) {
        		owner = inventoryService.findInventoryItemForMajorComponent(inventoryItem.getId()).getOwner();
        	}
            if (this.retailedDealer.getId().longValue() != owner.getId().longValue()) {
                isDifferentDealerAndOwner = true;
            }
        } else {
            isDifferentDealerAndOwner = true;
        }
        return isDifferentDealerAndOwner;
    }
    
    public boolean isDRDoneByLoggedInUser(InventoryItem inventoryItem) {
        if (inventoryItem.isRetailed()) {
            for (InventoryTransaction invTransaction : inventoryItem.getTransactionHistory()) {
                if (InvTransationType.DR.getTransactionType().equals(
                        invTransaction.getInvTransactionType().getTrnxTypeValue())) {
                    if (getLoggedInUsersDealership() != null
                            && invTransaction.getSeller().getId().longValue() == getLoggedInUsersDealership()
                                    .getId().longValue()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }
    
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
    
    public boolean isInventoryFullView(InventoryItem inventoryItem) {
		boolean isInventoryFullViewEnabled = false;
		if(isInternalUser()) {
			isInventoryFullViewEnabled = true;
		} else {	
			isInventoryFullViewEnabled = this.configParamService.getBooleanValue(ConfigName.ENABLE_INVENTORY_FULL_VIEW.getName());
			if(!isInventoryFullViewEnabled) {
				isInventoryFullViewEnabled = this.orgService.doesUserHaveRole(getLoggedInUser(), Role.INVENTORY_FULL_VIEW);
			}
		}
		return isInventoryFullViewEnabled;
    }
    
    public boolean stockBelongsToOEM(InventoryItem inventoryItem) {
		List<String> transactionTypes = new ArrayList<String>();
		transactionTypes.add(InvTransationType.IB.getTransactionType());
		transactionTypes.add(InvTransationType.DEALER_TO_DEALER
				.getTransactionType());
		InventoryTransaction latestIBD2DTrnx = inventoryItem
			.getLatestTransactionForATransactionType(transactionTypes);
		Organization currentOwner = this.orgService
						.findOrgById(latestIBD2DTrnx.getOwnerShip()
								.getId());	
		return currentOwner.isOriginalEquipManufacturer();
	}

    public Organization getRetailedDealer() {
        return this.retailedDealer;
    }
    
    public User getLoggedInUser() {
        return securityHelper.getLoggedInUser();
    }
    
	public boolean isCustomerDetailsNeededForDR_Rental(){
		return this.configParamService
				.getBooleanValue(ConfigName.ALLOW_CUSTOMER_DETAILS_FOR_DEALER_RENTAL
						.getName());
	}
 
	public boolean isLoggedInDealerShipToDealer(InventoryItem inventoryItem) {
		if(inventoryItem.getShipTo()!=null && getLoggedInUsersDealership() != null
				&& getLoggedInUsersDealership().getId().longValue() == inventoryItem.getShipTo().getId().longValue()){
			return true;
		}
		return false;
	}
	
	public boolean checkLoggedInDealerCurrentOwnerOrParent(InventoryItem item){
		if (!getLoggedInUser().getUserType().equals(AdminConstants.SUPPLIER_USER) && this.orgService.isDealer(getLoggedInUser())) {
            ServiceProvider loggedInUserDealership = getLoggedInUsersDealership();
            
            Organization itemCurrentOwner = item.getCurrentOwner();
            if (itemCurrentOwner == null && item.getSerializedPart()) {
            	itemCurrentOwner = (Organization)inventoryService.findInventoryItemForMajorComponent(item.getId()).getOwnedBy();
            }
            Organization shipTo = item.getShipTo();
            if (loggedInUserDealership != null
                    && (loggedInUserDealership.getId().equals(itemCurrentOwner.getId()) ||
                    (loggedInUserDealership.getChildDealersIds()!= null
                        && loggedInUserDealership.getChildDealersIds().contains(itemCurrentOwner.getId())))) {
                return true;
            }

            //if logged in dealer is parent of some dealer he is allowed NMHGSLMS-387
            List<Long> dealerIds = orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId());
            if(dealerIds.contains(item.getCurrentOwner().getId())){
                return true;
            }
            
        }
        return false;
	}
}
