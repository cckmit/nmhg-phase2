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

package tavant.twms.domain.claim;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.additionalAttributes.AttributePurpose;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.AcceptanceReasonForCP;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.ListOfValuesType;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.customReports.Applicability;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.domain.customReports.CustomReportApplicablePart;
import tavant.twms.domain.customReports.CustomReportService;
import tavant.twms.domain.customReports.ReportFormAnswer;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryItemSource;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemReplacementReason;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.MinimumLaborRoundUp;
import tavant.twms.domain.orgmodel.MinimumLaborRoundUpService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyAudit;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.query.HibernateQuery;
import tavant.twms.domain.query.HibernateQueryGenerator;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.warranty.MajorCompRegUtil;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;
import tavant.twms.infra.TypedQueryParameter;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author kamal.govindraj
 *
 */
public class ClaimServiceImpl implements ClaimService {	

	private ClaimRepository claimRepository;

	private PaymentService paymentService;

	private PartReturnService partReturnService;
	
	private PolicyService policyService;

	private ContractService contractService;
	
	private InventoryTransactionService invTransactionService;

	private ClaimXMLConverter claimXMLConverter;

	private SecurityHelper securityHelper;

	private PredicateAdministrationService predicateAdministrationService;
	
	private OrgService orgService;

	private ClaimAttributesRepository claimAttributesRepository;
	
	private AttributeAssociationService attributeAssociationService;

	private CatalogService catalogService;
	
	private FailureStructureService failureStructureService;
	
	private ConfigParamService configParamService;
	
	private MinimumLaborRoundUpService minimumLaborRoundUpService;
	
	private InventoryService inventoryService;	

    private CustomReportService customReportService;

    private ItemGroupService itemGroupService; 
    
    private EventService eventService;
    
    private MajorCompRegUtil majorCompRegUtil;
    
    private WarrantyService warrantyService;
    
    protected LovRepository lovRepository;

    public FailureStructureService getFailureStructureService() {
		return failureStructureService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}
	
	public void setMinimumLaborRoundUpService(MinimumLaborRoundUpService minimumLaborRoundUpService) {
		this.minimumLaborRoundUpService = minimumLaborRoundUpService;
	}

	public MinimumLaborRoundUpService getMinimumLaborRoundUpService() {
		return minimumLaborRoundUpService;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public AttributeAssociationService getAttributeAssociationService() {
		return attributeAssociationService;
	}

	public void setAttributeAssociationService(
			AttributeAssociationService attributeAssociationService) {
		this.attributeAssociationService = attributeAssociationService;
	}



	@SuppressWarnings("deprecation")
	public Claim initializeClaim(Claim claim) {
		if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
			boolean considerWarrantyCoverageForPartClaim = this.configParamService
					.getBooleanValue(ConfigName.CONSIDER_WARRANTY_COVERAGE_FOR_PART_CLAIM.getName());
			PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
			ServiceInformation serviceInformation = partsClaim.getServiceInformation();
			Assert.notNull(serviceInformation, "Service Information should be initialized");
			ItemReference claimedItemReference = claim.getItemReference();
			if (claimedItemReference == null && partsClaim.getPartInstalled()) {
				throw new IllegalArgumentException(
						"Installed ItemRefrence cannot be null (i.e either model or inventoryItem)");
			}

			final Item part = claim.getPartItemReference().getReferredItem()== null? claim.getPartItemReference().getReferredInventoryItem().getOfType():claim.getPartItemReference().getReferredItem();

			if (part == null) {
				throw new IllegalArgumentException("Part Item reference should be initialized");
			}
			if (claim.getSource()== null) {
				if (considerWarrantyCoverageForPartClaim) {
					serviceInformation.setCausalPart(part);
                    serviceInformation.setCausalBrandPart(claim.getBrandPartItem());
				} else if (!partsClaim.getPartInstalled() || partsClaim.getPartInstalled()
						&& (partsClaim.getCompetitorModelBrand()!=null && !partsClaim.getCompetitorModelBrand().isEmpty()
								&& !partsClaim.getCompetitorModelDescription().isEmpty() && !partsClaim
								.getCompetitorModelTruckSerialnumber().isEmpty())) {
					serviceInformation.setCausalPart(part);
                    serviceInformation.setCausalBrandPart(claim.getBrandPartItem());
				} else if (!partsClaim.getPartItemReference().isSerialized() 
						&& !partsClaim.getItemReference().isSerialized() 
						&& partsClaim.getPartItemReference().getReferredItem() != null) {
					serviceInformation.setCausalPart(part);
                    serviceInformation.setCausalBrandPart(claim.getBrandPartItem());
				}
			}
			
			if (this.configParamService.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName())
					&& !this.configParamService.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName())) {
				if (!partsClaim.getPartInstalled() || (!partsClaim.getPartItemReference().isSerialized() && partsClaim.getPartInstalled()
						&& (partsClaim.getCompetitorModelBrand()!=null && !partsClaim.getCompetitorModelBrand().isEmpty()
								&& !partsClaim.getCompetitorModelDescription().isEmpty() && !partsClaim
								.getCompetitorModelTruckSerialnumber().isEmpty())) || (!partsClaim.getPartItemReference().isSerialized() && !partsClaim.getItemReference().isSerialized())) {					
					if (serviceInformation.getServiceDetail().getHussmanPartsReplacedInstalled().size() == 0) { // checking already collection populated or not
						List<OEMPartReplaced> oemPartReplaced = new ArrayList<OEMPartReplaced>();
						oemPartReplaced.add(new OEMPartReplaced(claim.getPartItemReference(),claim.getBrandPartItem(), 1));
						InstalledParts installedPart = new InstalledParts();
						installedPart.setItem(claim.getPartItemReference().getReferredItem());
						installedPart.setNumberOfUnits(1);
                        installedPart.setBrandItem(claim.getBrandPartItem());
						if (claim.getPartItemReference().isSerialized()) {
							installedPart.setSerialNumber(claim.getPartItemReference().getReferredInventoryItem()
									.getSerialNumber());
						}
						List<InstalledParts> hussmanInstalledParts = new ArrayList<InstalledParts>();
						hussmanInstalledParts.add(installedPart);
						HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled = new HussmanPartsReplacedInstalled();
						hussmanPartsReplacedInstalled.setReplacedParts(oemPartReplaced);
						hussmanPartsReplacedInstalled.setHussmanInstalledParts(hussmanInstalledParts);
						serviceInformation.getServiceDetail().getHussmanPartsReplacedInstalled().add(
								hussmanPartsReplacedInstalled);
						}	
					}					
								
			}else{
				serviceInformation.getServiceDetail().addOEMPartReplaced(
						new OEMPartReplaced(claim.getPartItemReference(),claim.getBrandPartItem(), 1));
			}
		}
		if(claim!=null&&claim.getClaimedItems()!=null){
			for(ClaimedItem claimedItem : claim.getClaimedItems()){
				if(claimedItem.getItemReference()!=null&&
						claimedItem.getItemReference().getReferredInventoryItem()!=null){
					claimedItem.setVinNumber(claimedItem.
							getItemReference().getReferredInventoryItem().getVinNumber());
				}
			}
    	}

		return claim;
	}
	

	public void createClaim(Claim claim) {
		this.claimRepository.save(claim);
	}

	public void updateClaim(Claim claim) {
		this.claimRepository.update(claim);
	}
	
	public void updateInstalledParts(InstalledParts installedParts)
	{
		this.claimRepository.updateInstalledParts(installedParts);
	}
	
	public void updateLineItemGroup(LineItemGroup lineItemGroup)
	{
		this.claimRepository.updateLineItemGroup(lineItemGroup);
	}
	
	public void updatePayment(Payment payment)
	{
		this.claimRepository.updatePayment(payment);
	}

	public void deleteClaim(Claim claim) {
		this.claimRepository.delete(claim);
	}

    public void deactivateClaim(Claim claim) {
        claim.setState(ClaimState.DEACTIVATED);
    }

    public Claim findClaim(Long id) {
		return this.claimRepository.find(id);
	}

   public Claim findClaimWithServiceInfoAttributes(Long id)
    {
    	return this.claimRepository.findClaimWithServiceInfoAttributes(id);	
    }
	public Claim findClaimByNumber(String claimNumber) {
		return this.claimRepository.findClaimByNumber(claimNumber);
	}
	
	public Claim findClaimByNumber(final String claimNumber,
			final String dealerNumber) {
		return this.claimRepository
				.findClaimByNumber(claimNumber, dealerNumber);
	}

    public Collection<Claim> findAllPreviousClaimsForItem(Long invItemId) {
		return this.claimRepository.findAllPreviousClaimsForItem(invItemId);
	}
    
    public Collection<Claim> findAllPreviousClaimsForMajorComp(Long majorComInvId) {
		return this.claimRepository.findAllPreviousClaimsForMajorComp(majorComInvId);
	}

	public PageResult<Claim> findAllClaimsMatchingQuery(Long domainPredicateId,
			ListCriteria listCriteria) {
		DomainPredicate predicate = this.predicateAdministrationService
				.findById(domainPredicateId);
		HibernateQueryGenerator generator = new HibernateQueryGenerator(
				BusinessObjectModelFactory.CLAIM_SEARCHES);
		generator.visit(predicate);
		HibernateQuery query = generator.getHibernateQuery();
		PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		String queryWithoutSelect = query.getQueryWithoutSelect();
		Boolean isAdmin=this.orgService.doesUserHaveRole(new SecurityHelper().getLoggedInUser(), "admin");
		if(isAdmin){
		//processor should not able to see the draft claims
			queryWithoutSelect = queryWithoutSelect + "and claim.activeClaimAudit.state not in ('DRAFT','DRAFT_DELETED','DELETED')" ;
		}
		else{
		queryWithoutSelect = queryWithoutSelect
				+ " and claim.activeClaimAudit.state not in ('DRAFT_DELETED','DELETED') ";
		}
		Boolean isExternalUser=!this.orgService.isInternalUser(new SecurityHelper().getLoggedInUser());
		Boolean isBuConfigAMER = this.securityHelper.getDefaultBusinessUnit().getName().equals(AdminConstants.NMHGAMER);
		if(isExternalUser)
		{
			//A dealer can see only his own claims
			Long dealerId = new SecurityHelper().getLoggedInUser()
			.getCurrentlyActiveOrganization().getId();
			queryWithoutSelect = queryWithoutSelect + " and ( claim.forDealer.id = " + dealerId +
				" or ( claim.forDealer in (select tp from ThirdParty tp) " +
				" 	and claim.filedBy in (select users from Organization org join org.users users where org.id = "+dealerId+"))) ";
		}
        
        if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect = queryWithoutSelect + " and ("
					+ listCriteria.getParamterizedFilterCriteria() + " )";
		}
        
        if(isBuConfigAMER && query.getQueryWithoutSelect().contains("clmType")){
        	for(TypedQueryParameter parameter:query.getParameters()){
        		if(parameter.getValue()!= null && parameter.getValue().toString().toUpperCase().contains("UNIT")){
        			parameter.setValue(parameter.getValue().toString().replace("UNIT", "MACHINE"));
        		}
        	}
        }

		QueryParameters params = new QueryParameters(query.getParameters(),
				listCriteria.getTypedParameterMap());


        // TypedQueryParameter queryParam = new
		// TypedQueryParameter(securityHelper.getLoggedInUser().getLocale().toString(),
		// Hibernate.STRING);

		// params.getNamedParameters().put("userLocale", queryParam);
		
			
		queryWithoutSelect = queryWithoutSelect.replaceAll("USER_LOCALE",
				securityHelper.getLoggedInUser().getLocale().toString());
		return this.claimRepository.findClaimsUsingDynamicQuery(
				queryWithoutSelect, listCriteria.getSortCriteriaString(), query
						.getSelectClause(), pageSpecification, params);
	}

	public PageResult<Claim> findAllClaimsForMultiMaintainance(
			Long domainPredicateId, ListCriteria listCriteria) {
		DomainPredicate predicate = this.predicateAdministrationService
				.findById(domainPredicateId);
		HibernateQueryGenerator generator = new HibernateQueryGenerator(
				BusinessObjectModelFactory.CLAIM_SEARCHES);
		generator.visit(predicate);
		HibernateQuery query = generator.getHibernateQuery();
		String queryWithoutSelect = query.getQueryWithoutSelect();
		queryWithoutSelect = queryWithoutSelect + " and claim.activeClaimAudit.state like '%CLOSED')";
		if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect = queryWithoutSelect + " and ("
					+ listCriteria.getParamterizedFilterCriteria() + " )";
		}
		QueryParameters params = new QueryParameters(query.getParameters(),
				listCriteria.getTypedParameterMap());
		return this.claimRepository.findClaimsUsingDynamicQuery(queryWithoutSelect,
				listCriteria.getSortCriteriaString(), query.getSelectClause(),
				listCriteria.getPageSpecification(), params);
	}

	public PageResult<Claim> findAllClaimsForMultiTransferReProcess(
			Long domainPredicateId, ListCriteria listCriteria, User user) {
		DomainPredicate predicate = this.predicateAdministrationService
				.findById(domainPredicateId);
		HibernateQueryGenerator generator = new HibernateQueryGenerator(
				BusinessObjectModelFactory.CLAIM_SEARCHES);
		generator.visit(predicate);
		HibernateQuery query = generator.getHibernateQuery();
		query = prepareClaimSearchQueryForMultiTransferReprocess(query, user);
		String queryWithoutSelect = query.getQueryWithoutSelect();
		if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect = queryWithoutSelect + " and ("
					+ listCriteria.getParamterizedFilterCriteria() + " )";
		}
		QueryParameters params = new QueryParameters(query.getParameters(),
				listCriteria.getTypedParameterMap());
		/*return this.claimRepository.findAllClaimsForMultiMaintainance(
				queryWithoutSelect, listCriteria.getSortCriteriaString(), query
						.getSelectClause(), params);*/
		return this.claimRepository.findClaimsUsingDynamicQuery(queryWithoutSelect,
				listCriteria.getSortCriteriaString(), query.getSelectClause(),
				listCriteria.getPageSpecification(), params);
	}

	private HibernateQuery prepareClaimSearchQueryForMultiTransferReprocess(HibernateQuery query, User user){
		String queryWithoutSelect = query.getQueryWithoutSelect();
    	StringBuffer tempQueryWithoutSelect = new StringBuffer(queryWithoutSelect);
    	StringBuffer claimStates = new StringBuffer();
		claimStates.append("'");
		claimStates.append("PROCESSOR_REVIEW");
		claimStates.append("'");
		claimStates.append(",");
		claimStates.append("'");
		claimStates.append("REJECTED_PART_RETURN");
		claimStates.append("'");
		claimStates.append(",");
		claimStates.append("'");
		claimStates.append("REOPENED");
		claimStates.append("'");
		claimStates.append(",");
		claimStates.append("'");
		claimStates.append("ON_HOLD_FOR_PART_RETURN");
		claimStates.append("'");
		claimStates.append(",");
		claimStates.append("'");
		claimStates.append("ON_HOLD");
		claimStates.append("'");
		claimStates.append(",");
		claimStates.append("'");
		claimStates.append("REPLIES");
		claimStates.append("'");
		claimStates.append(",");
		claimStates.append("'");
		claimStates.append("TRANSFERRED");
		claimStates.append("'");
    	String filterClaimsOnClaimState = "and claim.activeClaimAudit.state in ( " + claimStates.toString() +")";
    	String filterClaimsAssignedTo=  " and exists(select taskInstance.claimId from "
    												+ " TaskInstance taskInstance "
													+ " where taskInstance.isOpen = true "
													+ " and taskInstance.claimId = claim.id "
    												+ " )";
    	tempQueryWithoutSelect = tempQueryWithoutSelect.append(filterClaimsOnClaimState)
    												   .append(filterClaimsAssignedTo);
    	query.setQueryWithoutSelect(tempQueryWithoutSelect.toString());
		return query;
	}

	public PageResult<Claim> findAllClaimsForQuickSearch(String claimNumber,
			ListCriteria listCriteria) {
		PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		return this.claimRepository.findClaimsUsingDynamicQuery(claimNumber,
				pageSpecification);
	}

	public void updateOEMPartInformation(Claim claim,List<OEMPartReplaced> replacedParts) {
		this.partReturnService.updatePartReturnsForClaim(claim, replacedParts);
	}

	// FIX-ME: Need to specify pointcuts in terms of interface classes and
	// annotations must be specified
	// FIX-ME: at interface leve.
	public void updatePaymentInformation(Claim theClaim)
			throws PaymentCalculationException {
		this.paymentService.calculatePaymentForClaim(theClaim,null);
		this.claimRepository.update(theClaim);
	}

	public void createClaimAudit(Object claimObject, boolean isExternal) {
		ClaimAudit audit = new ClaimAudit();
		// Object casted to Claim cannot be used in the following API as the
		// generated
		// XML stores classname as Claim.Claim being an interface,conversion
		// to object from such XML fails.
		String xml = this.claimXMLConverter.convertObjectToXML(claimObject);
		audit.setPreviousClaimSnapshotAsString(xml);
		Claim claim = (Claim) claimObject;
		audit.setPreviousState(claim.getState());
		audit.setInternal(!isExternal);
		audit.setInternalComments(claim.getInternalComment());
		if(claim.getExternalComment()!=null)
			audit.setExternalComments(claim.getExternalComment());
         else
        	 audit.setExternalComments(claim.getInternalComment());
		audit.setUpdatedBy(this.securityHelper.getLoggedInUser());
		// todo-verify following.
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		audit.setUpdatedOn(Clock.now());
		audit.setUpdatedTime(new Date());
		audit.setPreviousClaimSnapshot(claim);
		audit.setForClaim(claim);
		this.claimRepository.createClaimAudit(audit);
	}

	public ClaimAudit findClaimAudit(Long id) {
		ClaimAudit claimAudit = this.claimRepository.findClaimAudit(id);
        if (claimAudit != null) {
            prepareClaimFromAudit(claimAudit);
            return claimAudit;
        } else {
		    return null;
        }
	}

    private Claim prepareClaimFromAudit(ClaimAudit claimAudit) {
    	List<ClaimAudit> subSetclaimAudits =new ArrayList<ClaimAudit>();
    	for(ClaimAudit audit :claimAudit.getForClaim().getClaimAudits()){
    		subSetclaimAudits.add(audit);
    		if(audit.getId() == claimAudit.getId()){
    			break;
    		}
    	}
    	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claimAudit.getForClaim().getBusinessUnitInfo().getName());
        Claim claim = claimAudit.getForClaim();

        //Set the current audit as Active Audit.
        claimAudit.setUpdatedTime(new Date());
        claim.setActiveClaimAudit(claimAudit);
        return claim;
    }

    @SuppressWarnings("deprecation")
	public void checkMinRndUpAndComputePaymentForClaim(Claim claim) {
		if (claim.getItemReference().isSerialized()) {
			roundUpLaborOnClaim(claim);
			try {
				Payment payment = this.paymentService.calculatePaymentForClaim(claim,null);
				claim.setPayment(payment);
			} catch (PaymentCalculationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void roundUpLaborOnClaim(Claim claim) {
		MinimumLaborRoundUp minLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
		BigDecimal minimimLaborRoundUp = BigDecimal.ONE;
		int windowPeriod = 0;
		boolean isRoundUpApplicable = false;
		ServiceProcedure serviceProcedureForRoundUp = this.failureStructureService
				.findServiceProcedureForRoundUp(AdminConstants.ROUND_UP_JOB_CODE);
		if (minLaborRoundUp != null) {
			isRoundUpApplicable = checkIfRoundUpApplicable(minLaborRoundUp, claim, serviceProcedureForRoundUp);
		}
		if (isRoundUpApplicable) {
			minimimLaborRoundUp = minLaborRoundUp.getRoundUpHours() != null ? new BigDecimal(minLaborRoundUp
					.getRoundUpHours()) : minimimLaborRoundUp;
			windowPeriod = minLaborRoundUp.getDaysBetweenRepair() != null ? minLaborRoundUp.getDaysBetweenRepair()
					.intValue() : windowPeriod;
			BigDecimal totalHours = totalLaborHoursOnClaim(claim);
			boolean roundUpMinLaborHours = roundUpMinLaborHours(totalHours, minimimLaborRoundUp);
			BigDecimal adjustedHours = BigDecimal.ZERO;
			List<Claim> previousAcceptedClaimsInWindowPeriod = getAcceptedClaimsInWindowPeriod(claim);
			if(previousAcceptedClaimsInWindowPeriod.contains(claim))
			previousAcceptedClaimsInWindowPeriod.remove(claim);
			if (previousAcceptedClaimsInWindowPeriod.size() > 0) {
				adjustedHours = computeAdjustedHours(previousAcceptedClaimsInWindowPeriod, claim);
				if (adjustedHours.doubleValue() > 0) {
					if (adjustedHours.compareTo(totalHours) == -1) {
						adjustedHours = adjustedHours.negate();
					} else {
						adjustedHours = totalHours.negate();
					}
					createRoundUPJobCode(claim, adjustedHours, serviceProcedureForRoundUp);
				}
			} else if (roundUpMinLaborHours && totalHours.compareTo(BigDecimal.ZERO)!=0) {
				roundUpMinLoborHoursOnClaim(claim, totalHours, adjustedHours, minimimLaborRoundUp, windowPeriod,
						serviceProcedureForRoundUp);
			}
		}
	}

	private void createRoundUPJobCode(Claim claim, BigDecimal adjustedHours, ServiceProcedure serviceProcedureForRoundUp) {
        if(adjustedHours.compareTo(BigDecimal.ZERO)!=0)
        {
		LaborDetail laborDetail = new LaborDetail();
		laborDetail.setHoursSpent(adjustedHours);
		laborDetail.setServiceProcedure(serviceProcedureForRoundUp);
		claim.getServiceInformation().getServiceDetail().getLaborPerformed().add(laborDetail);
        }
	}

	private BigDecimal computeAdjustedHours(List<Claim> previousAcceptedClaimsInWindowPeriod, Claim claim) {
		BigDecimal adjustedHours = BigDecimal.ZERO;
		for (Claim previousClaims : previousAcceptedClaimsInWindowPeriod) {
			claim.setLaborRoundupWindow(previousClaims.getLaborRoundupWindow());
			for (LaborDetail labor : previousClaims.getServiceInformation().getServiceDetail().getLaborPerformed()) {
				if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)) {
					adjustedHours = adjustedHours.add(labor.getHoursSpent());
				}
			}
		}
		return adjustedHours;
	}

	private boolean isClaimDeniedAfterAcceptance(Claim claim) {
		boolean isClaimDeniedAfterAcceptance = false;
		if (claim.getState().equals(ClaimState.ACCEPTED_AND_CLOSED)) {
			isClaimDeniedAfterAcceptance = true;
		} else if (claim.getState().equals(ClaimState.REOPENED) && claim.getClaimAudits() != null
				&& claim.getClaimAudits().size() > 2) {
			ClaimState previousClaimState = claim.getClaimAudits().get(claim.getClaimAudits().size() - 2)
					.getPreviousState();
			if (previousClaimState.equals(ClaimState.ACCEPTED)
					|| previousClaimState.equals(ClaimState.ACCEPTED_AND_CLOSED)) {
				isClaimDeniedAfterAcceptance = true;
			}
		}
		return isClaimDeniedAfterAcceptance;
	}

	public Claim reopenClaimForLaborRndUpOnClaimDenial(Claim claim) {
		Claim previousAcceptedClaim = null;
		Claim claimToBeReopened = null;
		if (isClaimDeniedAfterAcceptance(claim)) {
			List<Claim> allAcceptedClaimsInWindowPeriod = getAllAcceptedAndClosedClaimsInWindowPeriod(claim);
            if (allAcceptedClaimsInWindowPeriod != null && allAcceptedClaimsInWindowPeriod.size() > 0) { 
                if(allAcceptedClaimsInWindowPeriod.contains(claim))
                    allAcceptedClaimsInWindowPeriod.remove(claim);
                if (allAcceptedClaimsInWindowPeriod.size() > 0) {
                    previousAcceptedClaim = allAcceptedClaimsInWindowPeriod.iterator().next();
                    boolean isRoundUpApplicable = false;
                    MinimumLaborRoundUp minLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
                    BigDecimal minimimLaborRoundUp = BigDecimal.ONE;
                    if (minLaborRoundUp != null) {
                        minimimLaborRoundUp = minLaborRoundUp.getRoundUpHours() != null ? new BigDecimal(minLaborRoundUp
                                .getRoundUpHours()) : minimimLaborRoundUp;
                        ServiceProcedure serviceProcedureForRoundUp = this.failureStructureService
                                .findServiceProcedureForRoundUp(AdminConstants.ROUND_UP_JOB_CODE);
                        isRoundUpApplicable = checkIfRoundUpApplicable(minLaborRoundUp, previousAcceptedClaim,
                                serviceProcedureForRoundUp);
                    }
                    BigDecimal roundUpHoursOnDeniedClaim = getRoundUpHoursOnClaim(claim);
                    if (isRoundUpApplicable && roundUpHoursOnDeniedClaim.compareTo(BigDecimal.ZERO)==1) {
                        updateAdjustedHoursOnClaim(previousAcceptedClaim, roundUpHoursOnDeniedClaim,minimimLaborRoundUp);
                        claimToBeReopened = previousAcceptedClaim;
                        this.removeRoundUpJobCodeOnClaim(claim);
                        this.updateClaim(claim);
                    }
                }
            }
		}
		return claimToBeReopened;
	}

	@SuppressWarnings("deprecation")
	public Claim reopenClaimForLaborRndUpOnCreditSubmission(Claim claim) {
		Claim claimToBeReopened = null;
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		Collection<Claim> allClaimsForItemFiledByDealer = this.claimRepository.findAllClaimsForItemFiledByDealer(
                           claim.getItemReference().getReferredInventoryItem().getSerialNumber(), claim.getForDealer());
		if(isAdjustmentToBeMadeOnCurrentClaim(claim,allClaimsForItemFiledByDealer))
		{
			boolean isRoundUpApplicable = false;
			MinimumLaborRoundUp minLaborRoundUp = this.minimumLaborRoundUpService.findMinimumLaborRoundUp();
			BigDecimal minimimLaborRoundUp = BigDecimal.ONE;
			if (minLaborRoundUp != null) {
				minimimLaborRoundUp = minLaborRoundUp.getRoundUpHours() != null ? new BigDecimal(minLaborRoundUp
						.getRoundUpHours()) : minimimLaborRoundUp;
				ServiceProcedure serviceProcedureForRoundUp = this.failureStructureService
						.findServiceProcedureForRoundUp(AdminConstants.ROUND_UP_JOB_CODE);
				isRoundUpApplicable = checkIfRoundUpApplicable(minLaborRoundUp, claim, serviceProcedureForRoundUp);
			}			
			if (isRoundUpApplicable) {		
				Claim deniedClaim = getDeniedClaim(allClaimsForItemFiledByDealer,claim);
				BigDecimal roundUpHoursOnDeniedClaim = getRoundUpHoursOnClaim(deniedClaim);
				updateAdjustedHoursOnClaim(claim, roundUpHoursOnDeniedClaim,minimimLaborRoundUp);
				claimToBeReopened = claim;
				this.removeRoundUpJobCodeOnClaim(deniedClaim);
				this.updateClaim(deniedClaim);
			}
		}
		return claimToBeReopened;
	}	
	
	private Claim getDeniedClaim(Collection<Claim> allPreviousClaimsForItemFiledByDealer,Claim currentClaim) {
		Claim claim = null;
		for (Claim previousClaims : allPreviousClaimsForItemFiledByDealer) {
			if (previousClaims.getLaborRoundupWindow() != null
					&& previousClaims.getLaborRoundupWindow().includes(currentClaim.getRepairDate())
					&& previousClaims.isClaimInDeniedState()
					&& getRoundUpHoursOnClaim(previousClaims).compareTo(BigDecimal.ZERO) == 1) {

				claim = previousClaims;
				break;
			}

		}
		return claim;
	}
	
	private boolean isAdjustmentToBeMadeOnCurrentClaim(Claim claim, Collection<Claim> allPrevClaimsForItemFiledByDealer) {
		Boolean isAdjustmentToBeMadeOnCurrentClaim = false;
		Collection<Claim> allPreviousClaimsForItemFiledByDealer = allPrevClaimsForItemFiledByDealer;
		if (isAnyClaimDeniedAfterAcceptanceInWinPer(claim, allPreviousClaimsForItemFiledByDealer)) {
			allPreviousClaimsForItemFiledByDealer: for (Claim previousClaims : allPreviousClaimsForItemFiledByDealer) {
				if (previousClaims.getLaborRoundupWindow() != null
						&& previousClaims.getLaborRoundupWindow().includes(claim.getRepairDate())
						&& (previousClaims.getState().equals(ClaimState.ACCEPTED_AND_CLOSED))) {
					for (LaborDetail labor : previousClaims.getServiceInformation().getServiceDetail()
							.getLaborPerformed()) {
						if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)
								&& labor.getHoursSpent().compareTo(BigDecimal.ZERO) == 1) {
							isAdjustmentToBeMadeOnCurrentClaim = false;
							break allPreviousClaimsForItemFiledByDealer;
						} else if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)
								&& ((labor.getHoursSpent().compareTo(BigDecimal.ZERO) == -1) || (labor.getHoursSpent().compareTo(BigDecimal.ZERO) == 0))) {
							isAdjustmentToBeMadeOnCurrentClaim = true;
						}
					}
				} else {
					isAdjustmentToBeMadeOnCurrentClaim = true;
				}
			}
		}
		return isAdjustmentToBeMadeOnCurrentClaim;
	}

	private boolean isAnyClaimDeniedAfterAcceptanceInWinPer(Claim claim,
			Collection<Claim> allPrevClaimsForItemFiledByDealer) {
		Boolean isAnyClaimDeniedAfterAcceptanceInWinPer = false;
		if(allPrevClaimsForItemFiledByDealer.contains(claim))
			allPrevClaimsForItemFiledByDealer.remove(claim);
		Collection<Claim> allPreviousClaimsForItemFiledByDealer = allPrevClaimsForItemFiledByDealer;
		if (allPreviousClaimsForItemFiledByDealer.size() > 0) {
			for (Claim previousClaims : allPreviousClaimsForItemFiledByDealer) {
				if (previousClaims.getLaborRoundupWindow() != null
						&& previousClaims.getLaborRoundupWindow().includes(claim.getRepairDate())
						&& previousClaims.isClaimInDeniedState() && getRoundUpHoursOnClaim(previousClaims).compareTo(BigDecimal.ZERO)==1) {
					isAnyClaimDeniedAfterAcceptanceInWinPer = true;
					break;
				}
			}
		}
		return isAnyClaimDeniedAfterAcceptanceInWinPer;
	}
	
	public BigDecimal getRoundUpHoursOnClaim(Claim claim) {
		BigDecimal roundUpHours = BigDecimal.ZERO;
		for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
			if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)) {
				roundUpHours = labor.getHoursSpent();
			}
		}
		return roundUpHours;
	}
		
	private void updateAdjustedHoursOnClaim(Claim acceptedClaim, BigDecimal roundUpHoursOnDeniedClaim,
			BigDecimal minimimLaborRoundUp) {
		BigDecimal totalHours = totalLaborHoursOnClaim(acceptedClaim).add(roundUpHoursOnDeniedClaim);
		for (LaborDetail labor : acceptedClaim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
			if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)) {
				if (totalHours.compareTo(minimimLaborRoundUp) == 1) {
					BigDecimal adjustedHoures = totalHours.subtract(totalLaborHoursOnClaim(acceptedClaim)
							.subtract(labor.getHoursSpent()));
					labor.setHoursSpent(adjustedHoures);
				} else {
					BigDecimal adjustedHoures = minimimLaborRoundUp.subtract(totalLaborHoursOnClaim(
							acceptedClaim).subtract(labor.getHoursSpent()));
					labor.setHoursSpent(adjustedHoures);
				}	
			}
		}
	}
	
	private void roundUpMinLoborHoursOnClaim(Claim claim, BigDecimal totalHours, BigDecimal adjustedHours,
			BigDecimal minimimLaborRoundUp, int windowPeriod,ServiceProcedure serviceProcedureForRoundUp) {
		CalendarDate fromDate = claim.getRepairDate().plusDays(-windowPeriod);
		CalendarDate tillDate = claim.getRepairDate().plusDays(windowPeriod);
		CalendarDuration duration = new CalendarDuration();
		duration.setFromDate(fromDate);
		duration.setTillDate(tillDate);
		if(claim.getLaborRoundupWindow()==null)
		claim.setLaborRoundupWindow(duration);
		adjustedHours = minimimLaborRoundUp.subtract(totalHours);
		createRoundUPJobCode(claim, adjustedHours,serviceProcedureForRoundUp);

	}
	
	@SuppressWarnings("deprecation")
	public List<Claim> getAcceptedClaimsInWindowPeriod(Claim claim) {
		List<Claim> previousAcceptedClaimsInWindowPeriod = new ArrayList<Claim>();
		Collection<Claim> allPreviousClaimsForItemFiledByDealer = this.claimRepository
				.findAllClaimsForItemFiledByDealer(claim.getItemReference().getReferredInventoryItem()
						.getSerialNumber(), claim.getForDealer());
		if (allPreviousClaimsForItemFiledByDealer.size() > 0) {
			for (Claim previousClaims : allPreviousClaimsForItemFiledByDealer) {
				if (previousClaims.getLaborRoundupWindow() != null
						&& !previousClaims.isClaimInDeniedState()
						&& previousClaims.getLaborRoundupWindow().includes(claim.getRepairDate())) {
					previousAcceptedClaimsInWindowPeriod.add(previousClaims);
				}
			}
		}
		return previousAcceptedClaimsInWindowPeriod;
	}

    /*
     * This returns all the accepted and closed claim in the window period irrespective of open recovery claims.
     * We can allow warranty claims to be reopened even if it has recovery claims as this claim will not be touched by
     * any user. Only round up labor will be adjusted by system.
     * 
     */
	@SuppressWarnings("deprecation")
	private List<Claim> getAllAcceptedAndClosedClaimsInWindowPeriod(Claim claim) {
		List<Claim> previousAcceptedClaimsInWindowPeriod = new ArrayList<Claim>();
		Collection<Claim> allPreviousClaimsForItemFiledByDealer = this.claimRepository
				.findAllClaimsForItemFiledByDealer(claim.getItemReference().getReferredInventoryItem()
						.getSerialNumber(), claim.getForDealer());
		if (allPreviousClaimsForItemFiledByDealer.size() > 0) {
			for (Claim previousClaims : allPreviousClaimsForItemFiledByDealer) {
				if (previousClaims.getLaborRoundupWindow() != null
						&& previousClaims.getState().equals(ClaimState.ACCEPTED_AND_CLOSED)
						&& previousClaims.getLaborRoundupWindow().includes(claim.getRepairDate())) {
					previousAcceptedClaimsInWindowPeriod.add(previousClaims);
				}
			}
		}
		return previousAcceptedClaimsInWindowPeriod;
	}
	
	public  BigDecimal totalLaborHoursOnClaim(Claim claim) {
		BigDecimal totalLaborHours = BigDecimal.ZERO;
		if (claim.getServiceInformation() != null) {
			for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
				totalLaborHours = totalLaborHours.add(labor.getTotalHours());
			}
		}
		return totalLaborHours;
	}
	
	public  BigDecimal totalLaborHoursOnClaimExcludingRoundUp(Claim claim) {
		BigDecimal totalLaborHours = BigDecimal.ZERO;
		if (claim.getServiceInformation() != null) {
			for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
				if(!labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE))
				totalLaborHours = totalLaborHours.add(labor.getTotalHours());
			}
		}
		return totalLaborHours;
	}	

	@SuppressWarnings("deprecation")
	private boolean checkIfRoundUpApplicable(MinimumLaborRoundUp minimumLaborRoundUp, Claim claim,
			ServiceProcedure serviceProcedure) {
		boolean isRoundUpApplicable = false;
		if (minimumLaborRoundUp.getApplicableProducts() == null || minimumLaborRoundUp.getApplicableProducts().isEmpty()
				|| (minimumLaborRoundUp.getApplicableProducts() != null && minimumLaborRoundUp.getApplicableProducts()
						.contains(claim.getItemReference().getUnserializedItem().getProduct()))) {
			isRoundUpApplicable = true;
		} else {
			isRoundUpApplicable = false;
		}		
		isRoundUpApplicable = serviceProcedure == null ? false : isRoundUpApplicable;
		if(claim.getCommercialPolicy())
		isRoundUpApplicable = minimumLaborRoundUp.getApplCommericalPolicy() ? isRoundUpApplicable :false;
		if (claim.getType().equals(ClaimType.CAMPAIGN))
			isRoundUpApplicable = minimumLaborRoundUp.getApplCampaignClaim() ? isRoundUpApplicable : false;		
		if(claim.getType().equals(ClaimType.MACHINE))
			isRoundUpApplicable = minimumLaborRoundUp.getApplMachineClaim() ? isRoundUpApplicable : false;
		if(claim.getType().equals(ClaimType.PARTS))
			isRoundUpApplicable = minimumLaborRoundUp.getApplPartsClaim() ? isRoundUpApplicable : false;
		
		return isRoundUpApplicable;
	}
	
	private boolean roundUpMinLaborHours(BigDecimal totalHours, BigDecimal minimimLaborRoundUp) {
		return totalHours.compareTo(minimimLaborRoundUp) == -1;
	}
	
	public Collection<ClaimState> findAllClaimStates() {
		return this.claimRepository.findAllClaimStates();
	}

	/**
	 * @param claimRepository
	 *            the claimRepository to set
	 */
	@Required
	public void setClaimRepository(ClaimRepository claimRepository) {
		this.claimRepository = claimRepository;
	}

	/**
	 * @param paymentService
	 *            the paymentService to set
	 */
	@Required
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	@Required
	public void setClaimXMLConverter(ClaimXMLConverter claimXMLConverter) {
		this.claimXMLConverter = claimXMLConverter;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public PredicateAdministrationService getPredicateAdministrationService() {
		return this.predicateAdministrationService;
	}

	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public List<Claim> findClaimsForCreditSubmitRetry() {
		return claimRepository.findClaimsToRetryCreditSubmission();
	}

	public List<SyncTracker> findAllCreditSubmissionsAwaitingNotification(){
		return claimRepository.findAllCreditSubmissionsAwaitingNotification();
	}

	public PageResult<Claim> findAllClaimsMatchingCriteria(
			ClaimSearchCriteria claimSearchCriteria) {
				
		return claimRepository
				.findAllClaimsMatchingCriteria(claimSearchCriteria);
	}
	
	public PageResult<Claim> findAllHistClaimsMatchingCriteria(
			final ListCriteria listCriteria,ServiceProvider loggedInUser){
		return claimRepository
		.findAllHistClaimsMatchingCriteria(listCriteria,loggedInUser);
	}
	
	public PageResult<Claim> getAllAcceptedClaimsMatchingCriteriaForDealer(
			final ListCriteria listCriteria,ServiceProvider loggedInUser){
		return claimRepository
		.getAllAcceptedClaimsMatchingCriteriaForDealer(listCriteria,loggedInUser);
	}
	
	public PageResult<Claim> getAllPartShippedNotReceivedClaims(
			ListCriteria listCriteria, Long buConfigDays) {
		return claimRepository.getAllPartShippedNotReceivedClaims(listCriteria, buConfigDays);
	}

	public List<Claim> findClaimsWithPartsShipped(String buQueryAppended) {
		return claimRepository.findClaimsWithPartsShipped(buQueryAppended);
	}

	public List<ItemGroup> findProductTypes(String itemGroupType) {
		return this.claimRepository.findProductTypes(itemGroupType);
	}

	public List<ItemGroup> findModelTypes(String itemGroupType) {
		return this.claimRepository.findProductTypes(itemGroupType);
	}
	
	public String findClaimByPartReturnId(final Long partReturnId)
	{
		return this.claimRepository.findClaimByPartReturnId(partReturnId);
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public ClaimAttributesRepository getClaimAttributesRepository() {
		return claimAttributesRepository;
	}

	public void setClaimAttributesRepository(
			ClaimAttributesRepository claimAttributesRepository) {
		this.claimAttributesRepository = claimAttributesRepository;
	}	
		
	public void prepareAttributesForInventory(Claim claim) {
		if(claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty() &&
				claim.getClaimedItems().get(0).getItemReference() != null)
			prepareAttributesForInventory(claim, claim.getClaimedItems().get(0).getItemReference().isSerialized());
	}
	
	public void prepareAttributesForInventory(Claim claim, boolean isSerialized) {
		String businessUnit = null;
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
        	ItemGroup model = null;
        	ItemGroup product = null;
            if(isSerialized){
            	if(claimedItem.getItemReference().getReferredInventoryItem() != null) {
            		if(businessUnit == null){
            			businessUnit = claimedItem.getItemReference().getReferredInventoryItem().getOfType()
            			.getModel().getBusinessUnitInfo().getName();
            		}
            		model = claimedItem.getItemReference().getReferredInventoryItem().getOfType()
	                			.getModel();
            		product = claimedItem.getItemReference().getReferredInventoryItem().getOfType()
								.getProduct();
            	}
            } else if(claimedItem.getItemReference() != null 
            		&& claimedItem.getItemReference().getModel() != null && !AdminConstants.UPLOAD_WARRANTY_CLAIM.equals(claim.getSource())){
            	if(businessUnit == null){
        			businessUnit = claimedItem.getItemReference().getModel().getBusinessUnitInfo().getName();
        		}
            	model = claimedItem.getItemReference().getModel();
            	product = getProduct(model);
            }
            if(org.springframework.util.StringUtils.hasText(businessUnit)){            	
            	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(businessUnit);
            }	
            List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
            List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
            if(model != null){
            	itemGroups.add(model);	
            }
            if(product != null){
            	itemGroups.add(product);
            }
            if(!itemGroups.isEmpty()){
            	additionalAttributes.addAll(this.attributeAssociationService
						.findAttributesForItemGroups(itemGroups, claim.getType(), AttributePurpose.CLAIMED_INVENTORY_PURPOSE));	
            }
            
            prepareClaimAttributes(claim, claimedItem.getClaimAttributes(), additionalAttributes);
        }
	}
	
	private ItemGroup getProduct(ItemGroup itemGroup) {
		ItemGroup product = null;
		if (itemGroup.getItemGroupType().equals(ItemGroup.MODEL)) {
			if (itemGroup.getIsPartOf() != null
					&& !ItemGroup.PRODUCT.equals(itemGroup.getIsPartOf().getItemGroupType())) {
				product = getProduct(itemGroup.getIsPartOf());
			} else if (itemGroup.getIsPartOf() != null) {
				product = itemGroup.getIsPartOf();
			}
		}
		return product;
	}
	
	public List<AdditionalAttributes> findAdditionalAttributesForFaultCode(Claim claim, Long faultCodeId) {
		List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
		if(faultCodeId != null) {
    		ClaimedItem claimedItem = (claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty())
										? claim.getClaimedItems().get(0)
										: null; 
			if(claimedItem != null) {
				boolean isSerialized = (claimedItem.getItemReference() != null
											&& claimedItem.getItemReference().isSerialized());
				Long modelId = null;
				if(isSerialized){
					if(claimedItem.getItemReference().getReferredInventoryItem() != null)
						modelId = claimedItem.getItemReference().getReferredInventoryItem().getOfType()
							.getModel().getId();
				} else if(claimedItem.getItemReference() != null 
						&& claimedItem.getItemReference().getModel() != null){
					modelId = claimedItem.getItemReference().getModel().getId();
				}
				if(modelId != null)
		            additionalAttributes.addAll(this.attributeAssociationService
		            				.findAttributesForFaultCode(faultCodeId, modelId, claim.getType()));
			}
        }
		return additionalAttributes;
	}
	
	public void prepareAttributesForFaultCode(Claim claim, Long faultCodeId) {		
    	if(faultCodeId != null) {
    		List<AdditionalAttributes> additionalAttributes = findAdditionalAttributesForFaultCode(claim, faultCodeId);
    		prepareClaimAttributes(claim, claim.getServiceInformation().getFaultClaimAttributes(), additionalAttributes);
        } else if(!claim.getServiceInformation().getFaultClaimAttributes().isEmpty()) {
        	claim.getServiceInformation().getFaultClaimAttributes().clear();
        }
    }
	
	public void prepareAttributesForClaim(Claim claim, Long smrReasonId) {
		List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
        additionalAttributes.addAll(this.attributeAssociationService
        				.findAttributesForClaim(smrReasonId, claim.getType()));
        prepareClaimAttributes(claim, claim.getClaimAdditionalAttributes(), additionalAttributes);
        }
	

	public void prepareAttributesForJobCode(Claim claim, LaborDetail laborDetail, Long serviceProcId) {
		List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
        additionalAttributes.addAll(this.attributeAssociationService
        				.findAttributesForJobCode(serviceProcId, claim.getType()));
        prepareClaimAttributes(claim, laborDetail.getClaimAttributes(), additionalAttributes);
	}
	
	public void prepareAttributesForJobCode(Claim claim) {
		for(LaborDetail laborDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
			Long serviceProcId = null;
        	if(laborDetail.getServiceProcedure() != null)
        		serviceProcId = laborDetail.getServiceProcedure().getId();
	        if(serviceProcId != null)
	        	prepareAttributesForJobCode(claim, laborDetail, serviceProcId);
		}
    }
	
	public void prepareAttributesForReplacedPart(Claim claim, OEMPartReplaced oemPartReplaced) {
		if(oemPartReplaced != null && oemPartReplaced.getItemReference() != null)
		{
			
			Item part = oemPartReplaced.getItemReference().getReferredItem();
            if (part != null) {
                String partNumber = part.getDuplicateAlternateNumber() ?
                                    part.getNumber() : part.getAlternateNumber();
                prepareAttributesForReplacedPart(claim, oemPartReplaced, partNumber);
            }
        }
	}
	
	public void prepareAttributesForReplacedPart(Claim claim, OEMPartReplaced oemPartReplaced, String partNumber) {
		if(partNumber != null && oemPartReplaced != null) {
			List<AdditionalAttributes> additionalAttributes = getAdditionalAttributesForPart(claim, partNumber); 
			prepareClaimAttributes(claim, oemPartReplaced.getClaimAttributes(), additionalAttributes);			   
		}
	}
	
	public void prepareAttributesForCausalPart(Claim claim, Item causalPart) {
		if(causalPart != null)
		{
			String partNumber = causalPart.getDuplicateAlternateNumber() 
								? causalPart.getNumber() : causalPart.getAlternateNumber();
			prepareAttributesForCausalPart(claim, partNumber);
		}
	}
	

	public void prepareAttributesForCausalPart(Claim claim, String partNumber) {
		
		if(partNumber != null && claim.getServiceInformation() != null) {
			List<AdditionalAttributes> additionalAttributes = getAdditionalAttributesForPart(claim, partNumber); 
			prepareClaimAttributes(claim, claim.getServiceInformation().getPartClaimAttributes(), additionalAttributes);			   
		}else if(!claim.getServiceInformation().getPartClaimAttributes().isEmpty()) {
        	for(ClaimAttributes claimAttributes : claim.getServiceInformation().getPartClaimAttributes())
        		claimAttributesRepository.delete(claimAttributes);
        	claim.getServiceInformation().getPartClaimAttributes().clear();
        }
	}
	
	private List<AdditionalAttributes> getAdditionalAttributesForPart(Claim claim, String partNumber) {
		List<AdditionalAttributes> additionalAttributes = new ArrayList<AdditionalAttributes>();
		if(org.springframework.util.StringUtils.hasText(claim.getBusinessUnitInfo().getName())){
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		}	
		
		if(partNumber != null) {
			Item item;
			try {
				item = catalogService.findItemOwnedByManuf(partNumber);
			} catch (CatalogException e) {
				item = null;
			}
			 
			if(item != null)	
				additionalAttributes.addAll(attributeAssociationService.findAttributesForItem(item.getId(), claim.getType()));
			//perf fix: look for contracts only if any attribute is defined for supplier at all
			if(attributeAssociationService.isAnyAttributeConfiguredForSupplier())
			{
				List<Contract> applicableContracts = this.contractService.findContract(claim,item, true);
				if (applicableContracts != null
						&& applicableContracts.size() == 1) {
					Supplier supplier = applicableContracts.get(0)
							.getSupplier();
					List<AdditionalAttributes> supplierAttributes = this.attributeAssociationService
							.findAttributesForSupplier(supplier.getId(), claim.getType());
					if (supplierAttributes != null
							&& !supplierAttributes.isEmpty()) {
						additionalAttributes.addAll(supplierAttributes);
					}
				}
			}
			
		}
		return additionalAttributes;
	}

	private void prepareClaimAttributes(Claim claim, List<ClaimAttributes> claimAttributes, List<AdditionalAttributes> additionalAttributes) {
		Iterator<ClaimAttributes> it = claimAttributes.iterator();
		List<ClaimAttributes> claimAttributesToBeDeleted = new ArrayList<ClaimAttributes>();
		boolean isDraft = (claim.getState() == ClaimState.DRAFT);
    	while(it.hasNext()) {
    		ClaimAttributes clmAttr = it.next();
    		boolean found = false;
    		for(AdditionalAttributes addAttr : additionalAttributes) {
    			if(addAttr.getId() == clmAttr.getAttributes().getId()) {
    				found = true;
    				break;
    			}
    		}
    		if(!found) {
    			if(isDraft || !StringUtils.hasText(clmAttr.getAttrValue())) {
    				it.remove();
    				claimAttributesToBeDeleted.add(clmAttr);
    			} 
    		}
    	}    	
    	for(AdditionalAttributes addAttr : additionalAttributes) {
    		if(getClaimAttrForAdditionalAttr(claimAttributes, addAttr) == null)
    			claimAttributes.add(new ClaimAttributes(addAttr,null));
    	}
    }
	
	private ClaimAttributes getClaimAttrForAdditionalAttr(List<ClaimAttributes> claimAttributes, AdditionalAttributes additionalAttr) {
		if(claimAttributes == null || claimAttributes.isEmpty() || additionalAttr == null)
    		return null;
    	for(ClaimAttributes claimAttr : claimAttributes) {
    		if(additionalAttr.getId() == claimAttr.getAttributes().getId())
    			return claimAttr;
    	}
    	return null;
	}

    public Payment getLatestManualCpReviewedPayment(Claim claim) {
        String autoReviewComment = "Auto reply generated by the system due to time window elapse";

        //Hack:The reason for adding Auto Reply is sometimes jbpm timer picks up this records
        //and sets review comment as "Auto Reply" instead of Quartz task
        String autoReply = "Auto Reply";

        List<ClaimAudit> claimAudits = claim.getClaimAudits();
        int auditSize = claimAudits.size();
        boolean manuallyReviewedFlag = false;

        for (int i = (auditSize - 1); i >= 0; i--) {
            ClaimAudit audit = claimAudits.get(i);
            if (ClaimState.REPLIES.equals(audit.getPreviousState())
                    && !(autoReviewComment.equalsIgnoreCase(audit.getInternalComments())
                    || autoReply.equalsIgnoreCase(audit.getInternalComments()))) {
                manuallyReviewedFlag = true;
            }
            if (manuallyReviewedFlag && ClaimState.CP_REVIEW.equals(claimAudits.get(i).getPreviousState())) {
                return claimAudits.get(i).getPayment();
            }
        }
        return null;
    }

	public List<Claim> findClaimsForRecovery(){
		return claimRepository.findClaimsForRecovery();
	}
	
	public List<Claim> findClaimsForRecovery(int pageNumber, int pageSize){
		return claimRepository.findClaimsForRecovery(pageNumber, pageSize);
	}
	
	public List<Claim> findClaimsForIds(List<Long> claimIds) {
		return this.claimRepository.findClaimsForIds(claimIds);
	}

	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart, Claim claim){
		return this.claimRepository.isAnyOpenClaimWithInstalledPart(installedPart, claim);
	}	
	
	
	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

    public void updateBOMPartOnPartOffCoverage(Long claimId, Boolean buPartReplaceableByNonBUPart) {
        Claim claim = findClaim(claimId);
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        if (!claim.isAcceptedOrNotReopenedOrAppealedClaim()) {
            claim.setBomUpdationNeeded(false);
            return;
        }
        PartsClaim partsClaim = null;
        Set<RegisteredPolicy> policysToBeTransfered = new HashSet<RegisteredPolicy>();
        if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
            partsClaim = new HibernateCast<PartsClaim>().cast(claim);
            if (!partsClaim.getPartInstalled()) {
                return;
            }
        }
        if (buPartReplaceableByNonBUPart == null) {
            buPartReplaceableByNonBUPart = configParamService.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName());
        }
        if (buPartReplaceableByNonBUPart) {
            return;
        }
        InventoryItem claimedInvItem = claim.getItemReference().getReferredInventoryItem();
        List<HussmanPartsReplacedInstalled> replacedInstalledParts = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
        for (HussmanPartsReplacedInstalled replacedInstalledPart : replacedInstalledParts) {
            if (replacedInstalledPart.getReplacedParts() != null
                    && replacedInstalledPart.getReplacedParts().size() > 0) {
                OEMPartReplaced replacedPart = replacedInstalledPart.getReplacedParts().iterator().next();
                if (replacedPart.getItemReference() != null
                        && replacedPart.getItemReference().getReferredInventoryItem() != null) {
                    /**
                     * Each serialized part will have only one serialized
                     * installed part
                     */
                    InventoryItem replacedInventoryItem = replacedPart.getItemReference().getReferredInventoryItem();
                    InstalledParts installedPart = replacedInstalledPart.getHussmanInstalledParts().iterator().next();
                    /**
                     * Part on/Part off can the same serialized part for parts claim hosted on competitor model
                     * and parts claim hosted on non serialized inventory. In these cases no updation/coverage 
                     * transfered is needed.
                     */
                    if (!(replacedInventoryItem.getSerialNumber().equals(installedPart.getSerialNumber()) && replacedInventoryItem.getOfType().equals(installedPart.getItem()))) {
                        if (!inventoryService.doesInvItemExistWithSNAndItem(installedPart.getSerialNumber(),
                                installedPart.getItem())) {
                            InventoryItem installedPartInvItem = new InventoryItem();
                            installedPartInvItem.setSerialNumber(installedPart.getSerialNumber());
                            installedPartInvItem.setOfType(installedPart.getItem());
                            installedPartInvItem.setOwnershipState(getInventoryService().findOwnershipStateByName(
                                    OwnershipState.FIRST_OWNER.getName()));
                            installedPartInvItem.setCurrentOwner(claim.getForDealer());
                            if (claimedInvItem != null) {
                                installedPartInvItem.setLatestBuyer(claimedInvItem.getLatestBuyer());
                                installedPartInvItem.setBusinessUnitInfo(claimedInvItem.getBusinessUnitInfo());
                            } else {
                                installedPartInvItem.setLatestBuyer(replacedInventoryItem.getLatestBuyer());
                                installedPartInvItem.setBusinessUnitInfo(replacedInventoryItem.getBusinessUnitInfo());
                            }
                            installedPartInvItem.setType(InventoryType.RETAIL);
                            installedPartInvItem.setConditionType(InventoryItemCondition.NEW);
                            installedPartInvItem.setInstallationDate(claim.getRepairDate());
                            if (partsClaim != null) {
                                installedPartInvItem.setSource(InventoryItemSource.MAJORCOMPREGISTRATION);
                            }
                            installedPartInvItem.setDeliveryDate(claim.getRepairDate());
                            installedPartInvItem.setSerializedPart(true);
                            if (partsClaim != null) {
                                policysToBeTransfered = transferPolicies(installedPartInvItem, replacedPart, claim);
                                createWarrantyForInstalledPart(installedPartInvItem, claim, policysToBeTransfered);
                                this.warrantyService.updateInventoryForWarrantyDates(installedPartInvItem);
                            }
                            replacedInventoryItem.getD().setActive(false);
                            this.inventoryService.updateInventoryItem(replacedInventoryItem);
                            this.inventoryService.createInventoryItem(installedPartInvItem);
                            if (partsClaim != null && !policysToBeTransfered.isEmpty() && hasMisMatchOfCoverage(policysToBeTransfered, installedPartInvItem, claim)) {
                                eventService.createEvent("claim", EventState.MISMATCH_OF_COVERAGE, claim.getId());
                            }
                            ItemReplacementReason replacementReason = new ItemReplacementReason();
                            replacementReason.setClaim(claim);
                            if (claimedInvItem != null && claimedInvItem.includes(replacedPart.getItemReference().getReferredInventoryItem())) {
                                claimedInvItem.replaceSerializedPart(replacedPart.getItemReference().getReferredInventoryItem(), installedPartInvItem, replacementReason);
                            }
                        }
                    }
                }
                if (claimedInvItem != null) {
                    inventoryService.updateInventoryItem(claimedInvItem);
                }
            }
        }
        claim.setBomUpdationNeeded(false);
    }
	
	private boolean hasMisMatchOfCoverage(Set<RegisteredPolicy> policiesToBeTransfered, InventoryItem installedPart,
			Claim claim) {
		List<PolicyDefinition> policyDefinitionsToBeTransfered = new ArrayList<PolicyDefinition>();
		List<PolicyDefinition> availablePolicies = this.policyService.findPoliciesAvailableForMajorCompRegistration(
				installedPart, installedPart.getDeliveryDate(), installedPart.getCustomerType(), claim.getForDealer());
		for (RegisteredPolicy registeredPolicy : policiesToBeTransfered) {
			policyDefinitionsToBeTransfered.add(registeredPolicy.getPolicyDefinition());
		}		
		for (PolicyDefinition policyToBeTransfered : policyDefinitionsToBeTransfered) {
			if(!availablePolicies.contains(policyToBeTransfered)){
				return true;
			}
		}
		return false;
	}
	
	private Set<RegisteredPolicy> transferPolicies(InventoryItem installedPartInvItem, OEMPartReplaced replacedPart,
			Claim claim) {
		Set<RegisteredPolicy> transferablePolicys = new HashSet<RegisteredPolicy>();
		if (replacedPart.getItemReference().getReferredInventoryItem().getWarranty() != null
				&& !replacedPart.getItemReference().getReferredInventoryItem().getWarranty().getPolicies().isEmpty()) {
			for (RegisteredPolicy policy : replacedPart.getItemReference().getReferredInventoryItem().getWarranty()
					.getPolicies()) {
				if (policy.getLatestPolicyAudit().getStatus().equals(RegisteredPolicyStatusType.ACTIVE.getStatus())) {
					RegisteredPolicy policyToBeTransfered = policy.clone();					
					CalendarDuration coverage = new CalendarDuration();
					coverage.setFromDate(installedPartInvItem.getInstallationDate());
					if (installedPartInvItem.getInstallationDate().isAfter(policy.getWarrantyPeriod().getTillDate())) {
						coverage.setTillDate(installedPartInvItem.getInstallationDate());					
					} else {
						coverage.setTillDate(policy.getWarrantyPeriod().getTillDate());
					}			
					RegisteredPolicyAudit policyAudit = new RegisteredPolicyAudit();
					policyAudit.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
					policyAudit.setComments(claim.getClaimNumber());
					policyAudit.setWarrantyPeriod(coverage);
					policyToBeTransfered.setStatus(RegisteredPolicyStatusType.ACTIVE.getStatus());
					policyToBeTransfered.getPolicyAudits().add(policyAudit);
					transferablePolicys.add(policyToBeTransfered);
					RegisteredPolicyAudit audit = new RegisteredPolicyAudit();
					audit.setWarrantyPeriod(policy.getWarrantyPeriod());
					audit.setStatus(RegisteredPolicyStatusType.TERMINATED.getStatus());
					audit.setComments(claim.getClaimNumber());
					policy.getPolicyAudits().add(audit);
				}
			}
		}		
		
		return transferablePolicys;
	}
	
	private void createWarrantyForInstalledPart(InventoryItem inventory, Claim claim,Set<RegisteredPolicy> policies) {
		InventoryTransaction newTransaction = this.majorCompRegUtil.createInventoryTransaction(inventory);		
		newTransaction.setBuyer(inventory.getLatestBuyer());
		newTransaction.setSeller(claim.getForDealer());
		newTransaction.setOwnerShip(claim.getForDealer());	
		inventory.getTransactionHistory().add(newTransaction);
		createWarranty(inventory,claim,policies);	
	}
	
	private void createWarranty(InventoryItem majorComponent, Claim claim, Set<RegisteredPolicy> policies) {
		Warranty warranty = this.majorCompRegUtil.setWarrantyAttributesFromMajorComponent(majorComponent);			
		warranty.setCertifiedInstaller(claim.getForDealer());		
		Customer latestBuyer = new HibernateCast<Customer>().cast(majorComponent.getLatestBuyer());
		warranty.setCustomer(latestBuyer);
		warranty.setPolicies(policies);
		warranty.setForDealer(claim.getForDealer());	
		WarrantyAudit warrantyAudit = new WarrantyAudit();
		warrantyAudit.setStatus(WarrantyStatus.ACCEPTED);
		warranty.getWarrantyAudits().add(warrantyAudit);
		for (RegisteredPolicy policy : policies) {
			policy.setWarranty(warranty);
		}
	}
	
	public void autoAcceptedClaimPostActivities(Claim claim, String isPartCorrected) {
		if (!Boolean.TRUE.toString().equals(isPartCorrected)) {
			checkMinRndUpAndComputePaymentForClaim(claim);
		}
	}	
	
	public void checkAdjustmentForRndUpLaborOnClaim(Claim claim,LaborDetail roundUpLaborDetail) {
		if (roundUpLaborDetail!=null || hasRoundUpCode(claim)) {
			BigDecimal currentRoundUpHours = roundUpLaborDetail!=null?roundUpLaborDetail.getHoursSpent():getRoundUpHoursOnClaim(claim);
			if(!hasRoundUpCode(claim))
				claim.getServiceInformation().getServiceDetail().getLaborPerformed().add(roundUpLaborDetail);
			List<Claim> allAcceptedClaimsInWindowPeriod = getAcceptedClaimsInWindowPeriod(claim);
			if (allAcceptedClaimsInWindowPeriod.size() > 0) {
				BigDecimal totalHoursOnAllClaims = getTotalHoursOnAllClaimsInWindowPeriod(allAcceptedClaimsInWindowPeriod).add(currentRoundUpHours.negate());
				BigDecimal totalRoundUpHoursOnAllClaims = getRoundUpHoursOnAllClaimsInWindowPeriod(allAcceptedClaimsInWindowPeriod).add(currentRoundUpHours.negate());
				BigDecimal adjustedHours = BigDecimal.ZERO;
				BigDecimal minimimLaborRoundUp = BigDecimal.ZERO;
				MinimumLaborRoundUp minLaborRoundUp = minimumLaborRoundUpService.findMinimumLaborRoundUp();
				if (minLaborRoundUp != null) {
					minimimLaborRoundUp = minLaborRoundUp.getRoundUpHours() != null ? new BigDecimal(minLaborRoundUp
							.getRoundUpHours()) : minimimLaborRoundUp;
				}
				if (totalHoursOnAllClaims.compareTo(minimimLaborRoundUp) != 0) {
					if (totalHoursOnAllClaims.compareTo(minimimLaborRoundUp) == 1) {
						adjustedHours = totalRoundUpHoursOnAllClaims.negate();
						adjustedHours = (adjustedHours.compareTo(BigDecimal.ZERO)==-1)&&(adjustedHours.negate().compareTo(totalLaborHoursOnClaimExcludingRoundUp(claim))==1)?totalLaborHoursOnClaimExcludingRoundUp(claim).negate():adjustedHours;
					} else if (totalHoursOnAllClaims.compareTo(minimimLaborRoundUp) == -1) {
						adjustedHours = minimimLaborRoundUp.add(totalHoursOnAllClaims.negate());
					}					
					setRoundUpHoursOnClaim(claim, adjustedHours);
				}
				else
				{
					setRoundUpHoursOnClaim(claim, BigDecimal.ZERO);
				}
			}
		} else {
			roundUpLaborOnClaim(claim);
		}

	}
	
	private void setRoundUpHoursOnClaim(Claim claim, BigDecimal adjustedHours) {
		
		Iterator<LaborDetail> iterator = claim.getServiceInformation().getServiceDetail().getLaborPerformed().iterator();
		while (iterator.hasNext()) {
			LaborDetail labor = iterator.next();
			if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)) {
				if(adjustedHours.compareTo(BigDecimal.ZERO)==0)
				{
				iterator.remove();	
				}
				else
				{
				labor.setHoursSpent(adjustedHours);				
				}
			}
		}
	}
	
	private void removeRoundUpJobCodeOnClaim(Claim claim) {
		Iterator<LaborDetail> iterator = claim.getServiceInformation().getServiceDetail().getLaborPerformed()
				.iterator();
		while (iterator.hasNext()) {
			LaborDetail labor = iterator.next();
			if (labor.getServiceProcedure().getDefinition().getCode().equals(AdminConstants.ROUND_UP_JOB_CODE)) {
				iterator.remove();
			}
		}
	}
	
	public boolean hasRoundUpCode(Claim claim) {
		boolean hasRoundUpCode = false;
		for (LaborDetail labor : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
			if (labor.getServiceProcedure() != null 
					&& labor.getServiceProcedure().getDefinition() != null
					&& AdminConstants.ROUND_UP_JOB_CODE.equals(labor.getServiceProcedure().getDefinition().getCode())) {
				hasRoundUpCode = true;
				break;
			}
		}
		return hasRoundUpCode;

	}
	
	public BigDecimal getTotalHoursOnAllClaimsInWindowPeriod(List<Claim> allAcceptedClaimsInWindowPeriod) {
		BigDecimal totalHours = BigDecimal.ZERO;
		for (Claim claim : allAcceptedClaimsInWindowPeriod) {			
			totalHours = totalHours.add(totalLaborHoursOnClaim(claim));
			}
		return totalHours;
	}
	
	public BigDecimal getRoundUpHoursOnAllClaimsInWindowPeriod(List<Claim> allAcceptedClaimsInWindowPeriod) {
		BigDecimal roundUpHours = BigDecimal.ZERO;
		for (Claim claim : allAcceptedClaimsInWindowPeriod) {			
			roundUpHours = roundUpHours.add(getRoundUpHoursOnClaim(claim));
			}
		return roundUpHours;
	}
	
	/* (non-Javadoc)
	 * @see tavant.twms.domain.claim.ClaimService#validateReplacedParts(tavant.twms.domain.claim.HussmanPartsReplacedInstalled, tavant.twms.domain.claim.Claim)
	 */
	public Map<String, String[]> validateReplacedParts(HussmanPartsReplacedInstalled replacedInstalledPart, Claim claim) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		if (replacedInstalledPart != null) {
			List<OEMPartReplaced> replacedParts = replacedInstalledPart.getReplacedParts();
			List<InstalledParts> installedParts = replacedInstalledPart.getHussmanInstalledParts();
			replacedParts.removeAll(Collections.singleton(null));
			installedParts.removeAll(Collections.singleton(null));
			if (replacedParts != null && replacedParts.size() > 0 && installedParts != null && installedParts.size() > 0) {				
				for (OEMPartReplaced replacedPart : replacedParts) {
					boolean isAnySerializedReplacedPartGiven = false;
					boolean isAnyNonSerializedInstalledPartGiven = false;
					boolean isValid = true;


					if (isValid && !isAnySerializedReplacedPartGiven && !isAnyNonSerializedInstalledPartGiven) {
						if (replacedPart != null && replacedPart.getItemReference().getReferredInventoryItem() != null)// Serialized
																														// replaced
																														// part
						{							
							if (replacedParts.size() > 1) {
								for (OEMPartReplaced eachReplacedPart : replacedParts) {
									if (eachReplacedPart != null && eachReplacedPart.getItemReference() != null
											&& eachReplacedPart.getItemReference().getReferredInventoryItem() != null) {
										errorCodeMap.put("error.claim.oemPartReplacedSerialized", null);
										isAnySerializedReplacedPartGiven = true;
										break;
									}
								}
								if (!isAnySerializedReplacedPartGiven) {
									errorCodeMap.put("error.claim.oemPartReplacedInstalledNonSerialized", null);
								}
							}
							// By now replacedParts.size() = 1 && Each
							// serialized replaced part should be replaced by
							// yet another replaced part with quantity one only
							if (installedParts == null || installedParts.size() == 0) {
								errorCodeMap.put("error.claim.oemPartReplacedInstalledNull", null);
								isValid = false;
							} else if (installedParts != null && installedParts.size() > 1) {

								for (InstalledParts installedPart : installedParts) {
									if (installedPart != null && !org.springframework.util.StringUtils.hasText(installedPart.getSerialNumber())) {
										errorCodeMap.put("error.claim.oemPartReplacedInstalledNonSerialized",
												null);
										isAnyNonSerializedInstalledPartGiven = true;
										break;
									}
								}
								// Coomented this code since for NMHG one serialized part can be replaced with multiple installed parts
								/*if (!isAnyNonSerializedInstalledPartGiven) {
									errorCodeMap.put("error.claim.oemPartReplacedInstalledQty", null);
								}*/
							} else {
								// By now installedParts.size() = 1 and that too
								// should be serialized
								InstalledParts installedPart = installedParts.iterator().next();
								if (installedPart == null || !org.springframework.util.StringUtils.hasText(installedPart.getSerialNumber())
										|| installedPart.getItem() == null) {
									errorCodeMap.put("error.claim.oemPartReplacedInstalledNull", null);
									isValid = false;									
								} 
								// Coomented this code since for NMHG one serialized part can be replaced with multiple installed parts
								/*else if (installedPart.getNumberOfUnits() == null
										|| installedPart.getNumberOfUnits() != 1) {
									errorCodeMap.put("error.claim.oemPartReplacedInstalledQty", null);
									isValid = false;
								}*/
							}
							if (replacedPart.getItemReference().getReferredInventoryItem().getInstallationDate() != null
									&& claim.getRepairDate().isBefore(
											replacedPart.getItemReference().getReferredInventoryItem()
													.getInstallationDate())) { 

								errorCodeMap.put("error.claim.oemPartReplacedInstalledInstallDate", new String[] {
										replacedPart.getItemReference().getReferredInventoryItem().getOfType()
												.getNumber(),
										replacedPart.getItemReference().getReferredInventoryItem().getSerialNumber() });
								isValid = false;
							}
						} else if (replacedPart != null && replacedPart.getItemReference().getReferredItem() != null)// Nonserialized
																														// replaced
																														// part
						{
							if (replacedParts != null && replacedParts.size() > 1) {
								for (OEMPartReplaced eachReplacedPart : replacedParts) {
									if (eachReplacedPart != null && eachReplacedPart.getItemReference() != null
											&& eachReplacedPart.getItemReference().getReferredInventoryItem() != null) {
										errorCodeMap.put("error.claim.oemPartReplacedInstalledNonSerialized",
												null);
										isValid = false;
										break;
									}
								}
							}
							//these checks are not required for NMHG.
						/*	if (installedParts != null && installedParts.size() > 0) {
								for (InstalledParts installedPart : installedParts) {
									if (installedPart != null && org.springframework.util.StringUtils.hasText(installedPart.getSerialNumber())) {
										errorCodeMap.put("error.claim.oemPartReplacedInstalledNonSerialized",
												null);
										isValid = false;
										break;
									}
								}
							}*/
						}
					}
				}
				// Installed Part not on any other open claim
				// Commented this code since for NMHG this check is not required
/*				for (InstalledParts installedPart : installedParts) {
					if (installedPart != null && installedPart.getItem() != null
							&& StringUtils.hasText(installedPart.getSerialNumber())) {
						boolean isExists = false;
						if(claim.getId() == null) {
							//isExists = isAnyOpenClaimWithInstalledPart(installedPart);
						} else {
							//isExists = isAnyOpenClaimWithInstalledPart(installedPart, claim);
						}
						if (isExists) {
							errorCodeMap.put("error.claim.installedPartOnOtherClaim", new String[] {
									installedPart.getItem().getNumber(), installedPart.getSerialNumber() });
							break;
						}
					}
				}*/

				// Installed Part not any other inventory's BOM
				/*OEMPartReplaced replacedOEMPart = replacedInstalledPart.getReplacedParts().get(0);
				InstalledParts partInstalled = replacedInstalledPart.getHussmanInstalledParts().get(0);*/
				//One more condition added for TKTSA-887 All scenarios except reopen and appealed should check this condition
				/*if (!claim.isReopenedOrAppealedClaim()) {
					// This Condition is to check for a serialized part installed on either a competitor model or a non-serialized host 
					// and if the part off and part on is the same.
					//for NMHG these checks are not required
				if (!((claim.getClaimCompetitorModel() != null || (claim.getItemReference() != null && !claim
							.getItemReference().isSerialized())) && (partInstalled != null
							&& partInstalled.getSerialNumber() != null
							&& replacedOEMPart.getItemReference() != null
							&& replacedOEMPart.getItemReference().getReferredInventoryItem() != null
							&& partInstalled.getSerialNumber().equals(
									replacedOEMPart.getItemReference().getReferredInventoryItem().getSerialNumber()) && partInstalled
							.getItem().equals(replacedOEMPart.getItemReference().getReferredItem())))) {
						for (InstalledParts installedPart : installedParts) {
							if (installedPart != null && StringUtils.hasText(installedPart.getSerialNumber())
									&& installedPart.getItem() != null) {
								if (inventoryService.doesInvItemExistWithSNAndItem(installedPart.getSerialNumber(),
										installedPart.getItem())) {
									errorCodeMap.put("error.claim.installedPartOnAnotherUnit", new String[] {
											installedPart.getItem().getNumber(), installedPart.getSerialNumber() });
									break;
								}
							}
						}
					}
				}*/
				
//				Replaced Part not on any other open claim
				for (OEMPartReplaced replacedPart : replacedParts) {
					if (replacedPart != null && replacedPart.getItemReference().getReferredInventoryItem() != null) {
						boolean isExists = false;					
				    //        isExists = isAnyOpenClaimWithReplacedPart(replacedPart,claim);						 
						if (isExists) {
							errorCodeMap.put("error.claim.replacedOnOtherClaim", new String[] {
									replacedPart.getItemReference().getReferredInventoryItem().getOfType().getNumber(), replacedPart.getItemReference().getReferredInventoryItem().getSerialNumber() });
							break;
						} else if (claim.isAcceptedOrNotReopenedOrAppealedClaim()){
							if(claim.getItemReference() != null && claim.getItemReference().isSerialized()){
								InventoryItem claimedInvItem = claim.getItemReference().getReferredInventoryItem();
								if(!claimedInvItem.includes(replacedPart.getItemReference().getReferredInventoryItem())){
									errorCodeMap.put("error.claim.replacedOnNotOnInventory", new String[] {
											replacedPart.getItemReference().getReferredInventoryItem().getOfType().getNumber(), replacedPart.getItemReference().getReferredInventoryItem().getSerialNumber() });
									break;
								}
							}
							
						}
					}
				}
				
			}
			/*else {
				if (installedParts != null && installedParts.size() > 0){
					errorCodeMap.put("error.claim.selectAtleastOneOfReplacedParts", null);
				}
				if (replacedParts != null && replacedParts.size() > 0){
					errorCodeMap.put("error.claim.selectAtleastOneOfTSAInstallParts", null);					
				}
			}*/

		}

		return errorCodeMap;
	}
	
	
	
	public Boolean isAnyOpenClaimWithInstalledPart(final InstalledParts installedPart) {
		return this.claimRepository.isAnyOpenClaimWithInstalledPart(installedPart);
	}
	
	public Boolean isAnyOpenClaimWithPolicyOnInventoryItem(final InventoryItem inventoryItem,final RegisteredPolicy policy){
		return this.claimRepository.isAnyOpenClaimWithPolicyOnInventoryItem(inventoryItem,policy);
	}

	public Boolean isAnyFailureReportPendingOnClaim(final Claim claim) {
		List<CustomReport> failureReports = fetchfailureReportsForItemsOnClaim(claim);
		if (!failureReports.isEmpty()) {
			for (CustomReport report : reportsFilteredBasedOnApplicability(claim, failureReports)) {
				if (!reportAlreadyAnswered(claim, report)) {
					return true;
				}
			}
		}
		return false;
	}
    /*This would be invoked whenever a claim is submitted after being modified by processor/smr/advisor etc.Each time we  reset the failure report answer , based on whether user has modified the 
	replaced/installed/causal part.The function is also invoked while checking if reports are present on the claim.*/
	private Collection<CustomReport> reportsFilteredBasedOnApplicability(Claim claim, List<CustomReport> failureReports) {
		Set<CustomReport> filteredReports = new HashSet<CustomReport>();
		for (CustomReport failureReport : failureReports) {
			for (CustomReportApplicablePart applicablePart : failureReport.getApplicableParts()) {
				if (applicablePart.isApplicableForType(Applicability.CAUSAL)
						&& claim.getServiceInformation().getCausalPart() != null // this null check required for Field Mod Claim submission
						&& isCustomReportPartApplicableForItem(applicablePart, claim.getServiceInformation()
								.getCausalPart())) {
					ServiceInformation serviceInformation = claim.getServiceInformation();
					if (serviceInformation.getCustomReportAnswer() == null) {
						filteredReports.add(failureReport);
					}
				}
				if (applicablePart.isApplicableForType(Applicability.INSTALLED)) {
					for (InstalledParts installedPart : claim.getServiceInformation().getServiceDetail()
							.getInstalledParts()) {
						CustomReport currentReport = installedPart.getCustomReportAnswer() == null ? null
								: installedPart.getCustomReportAnswer().getCustomReport();
						boolean isApplicable=isCustomReportPartApplicableForItem(applicablePart, installedPart.getItem());
						if(isApplicable){
							filteredReports.add(failureReport);
						}
						if(currentReport !=null){
							/*Check if the failure report is applicable , but the answer is associated with some other report in which case 
							reset this answer.This means that the installed part has been modified as same part cannot be associated with more than one report
							Also check if report is not applicable but still the current answer is associated with the same report .Reset the answer in this case also.
							This means that the installed part has been modified from a part that was applicable but not applicable now.
							*/
							if((isApplicable && !currentReport.getId().equals(failureReport.getId()))||(!isApplicable && currentReport.getId().equals(failureReport.getId()))){
								installedPart.setCustomReportAnswer(null);
							}
						}
						/*If current report is not null and the report associated is the same as the one that is applicable on it, no need to reset it.This basically 
						means the report has already been answered and the dealer does not have to reenter the information for the report
						*/
					}
				}
				if (applicablePart.isApplicableForType(Applicability.REMOVED)) {
					for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail()
							.getReplacedParts()) {
						//Is this part associated with a report
						CustomReport currentReport = oemPartReplaced.getCustomReportAnswer() == null ? null
								: oemPartReplaced.getCustomReportAnswer().getCustomReport();
						boolean isApplicable=isCustomReportPartApplicableForItem(applicablePart, oemPartReplaced.getItemReference()
								.referenceForItem());
						//Report is applicable for the part , so add it to the list of reports
						if(isApplicable){
							filteredReports.add(failureReport);
						}
						//The replaced part has already an answer associated with it.
						if(currentReport !=null){
							/*Check if the failure report is applicable , but the answer is associated with some other report in which case 
							reset this answer.This means that the replaced part has been modified as same part cannot be associated with more than one report
							Also check if report is not applicable but still the answer is associated with the same report .Reset the answer in this case also.
							This means that the replaced part has been modified.
							*/
							if((isApplicable && !currentReport.getId().equals(failureReport.getId()))||(!isApplicable && currentReport.getId().equals(failureReport.getId()))){
								oemPartReplaced.setCustomReportAnswer(null);
							}
						}
					}
				}
			}
		}
		return filteredReports;
	}
	
	public Collection<Item> itemsApplicableOnReport(Claim claim, CustomReport report) {
		Set<Item> items = new HashSet<Item>();
		for (CustomReportApplicablePart applicablePart : report.getApplicableParts()) {
			if (applicablePart.isApplicableForType(Applicability.CAUSAL)					
					&& isCustomReportPartApplicableForItem(applicablePart, claim.getServiceInformation()
							.getCausalPart())) {
				items.add(claim.getServiceInformation().getCausalPart());
			}
			if (applicablePart.isApplicableForType(Applicability.INSTALLED)) {
				for (InstalledParts installedPart : claim.getServiceInformation().getServiceDetail()
						.getInstalledParts()) {
					if (isCustomReportPartApplicableForItem(applicablePart, installedPart.getItem())) {
						items.add(installedPart.getItem());
					}
				}
			}
			for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail().getReplacedParts()) {
				if (isCustomReportPartApplicableForItem(applicablePart, oemPartReplaced.getItemReference()
						.referenceForItem())) {
					items.add(oemPartReplaced.getItemReference().referenceForItem());
				}
			}
		}
		return items;
	}

	public boolean reportAlreadyAnswered(Claim claim, CustomReport failureReport) {
		boolean reportPresent = false;
		boolean isAnswered = true;
		CustomReportAnswer answer = null;
		if (claim.getServiceInformation().getCustomReportAnswer() != null) {
			answer = claim.getServiceInformation().getCustomReportAnswer();
			reportPresent = failureReport.getId().equals(answer.getCustomReport().getId());
		}
		for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail().getReplacedParts()) {
			if (oemPartReplaced.getCustomReportAnswer() != null
					&& failureReport.getId().equals(oemPartReplaced.getCustomReportAnswer().getCustomReport().getId())) {
				answer = oemPartReplaced.getCustomReportAnswer();
				reportPresent = true;
			}
		}
		for (InstalledParts installedPart : claim.getServiceInformation().getServiceDetail().getInstalledParts()) {
			if (installedPart.getCustomReportAnswer() != null
					&& failureReport.getId().equals(installedPart.getCustomReportAnswer().getCustomReport().getId())) {
				answer = installedPart.getCustomReportAnswer();
				reportPresent = true;
			}
		}

		if (reportPresent) {//This need not be done.
			for (ReportFormAnswer reportFormAnswer : answer.getFormAnswers()) {
				if (reportFormAnswer.getQuestion().getMandatory()
						&& (reportFormAnswer.getAnswerOptions().isEmpty()
								&& !StringUtils.hasText(reportFormAnswer.getAnswerValue()) && reportFormAnswer
								.getAnswerDate() == null)) {
					isAnswered = false;
					break;
				}
			}
		} 
		return isAnswered && reportPresent;
	}
	
	public boolean acceptedClaimFailsRulesAfterPartOff(Claim claim){
		boolean validationFails=false;
		if(claim.getState().equals(ClaimState.ACCEPTED)){
			for(HussmanPartsReplacedInstalled hpr : claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()){
				if(!((Map)validateReplacedParts(hpr, claim)).isEmpty()
						|| isAnyFailureReportPendingOnClaim(claim)){
					validationFails= true;
				}
			}
		}
		return validationFails;
	}


    public List<CustomReport> fetchfailureReportsForItemsOnClaim(Claim claim) {
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(claim.getServiceInformation().getCausalPart());
        for (OEMPartReplaced replacedPart : claim.getServiceInformation().getServiceDetail().getReplacedParts()) {
            if(replacedPart.getItemReference().getReferredInventoryItem()!=null){
                itemList.add(replacedPart.getItemReference().getReferredInventoryItem().getOfType());
            }else{
                itemList.add(replacedPart.getItemReference().getReferredItem());
            }
        }
        for (HussmanPartsReplacedInstalled parts : claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()) {
            for (InstalledParts installedPart : parts.getHussmanInstalledParts()) {
                itemList.add(installedPart.getItem());
            }
        }
        return customReportService.findReportsForParts(itemList,claim);
    }

    public void setCustomReportService(CustomReportService customReportService) {
        this.customReportService = customReportService;
    }

    public boolean isCustomReportPartApplicableForItem(CustomReportApplicablePart customReportApplicablePart,Item item){
        if (customReportApplicablePart.getItemCriterion().getItem() != null && item != null
                && customReportApplicablePart.getItemCriterion().getItem().getId().longValue() == item.getId().longValue()) {
            return true;
        }else if(customReportApplicablePart.getItemCriterion().getItemGroup()!=null && item != null){
           ItemGroup itemGroupForItem = itemGroupService.findItemGroupForItem(customReportApplicablePart.getItemCriterion().getItemGroup(),item, AdminConstants.FAIURE_REPORT_PURPOSE);
            if(itemGroupForItem!=null){
                return true;
            }
        }
        return false;
    }
    
    public Long findAllClaimsCountForMultiTransferReProcess(Long domainPredicateId, ListCriteria listCriteria, User user) {
 		DomainPredicate predicate = this.predicateAdministrationService.findById(domainPredicateId);
 		HibernateQueryGenerator generator = new HibernateQueryGenerator(
 				BusinessObjectModelFactory.CLAIM_SEARCHES);
 		generator.visit(predicate);
 		HibernateQuery query = generator.getHibernateQuery();
 		query = prepareClaimSearchQueryForMultiTransferReprocess(query, user);
 		String queryWithoutSelect = query.getQueryWithoutSelect();
 		if (listCriteria.isFilterCriteriaSpecified()) {
 			queryWithoutSelect = queryWithoutSelect + " and ("
 					+ listCriteria.getParamterizedFilterCriteria() + " )";
 		}
 		QueryParameters params = new QueryParameters(query.getParameters(),
 				listCriteria.getTypedParameterMap());
 
 		return this.claimRepository.findAllClaimsCountForMultiClaimMaintenance(queryWithoutSelect, params);
 
	}
    
    public Long findAllClaimsCountForClaimAttributes(Long domainPredicateId, ListCriteria listCriteria) {
    	
    	DomainPredicate predicate = this.predicateAdministrationService.findById(domainPredicateId);
        HibernateQueryGenerator generator = new HibernateQueryGenerator(
		         BusinessObjectModelFactory.CLAIM_SEARCHES);
        generator.visit(predicate);
        HibernateQuery query = generator.getHibernateQuery();
        String queryWithoutSelect = query.getQueryWithoutSelect();
        queryWithoutSelect = queryWithoutSelect + " and claim.activeClaimAudit.state like '%CLOSED')";
		if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect = queryWithoutSelect + " and ("
					+ listCriteria.getParamterizedFilterCriteria() + " )";
		}
		QueryParameters params = new QueryParameters(query.getParameters(),
		listCriteria.getTypedParameterMap()); 
 		return this.claimRepository.findAllClaimsCountForMultiClaimMaintenance(queryWithoutSelect, params); 
	}

	public void updateEPOerrorMessagesDetails(Claim claim) {
		int i=this.claimRepository.updateEPOerrorMessagesDetails(claim);
		
	}
    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

	public boolean areOpenClaimsPresentForServiceProvider(final String serviceProviderNumber) {
		return this.claimRepository.areOpenClaimsPresentForServiceProvider(serviceProviderNumber);
	}
	
	public Boolean isAnyOpenClaimWithReplacedPart(final OEMPartReplaced oemPartReplaced,final Claim claim){
		return this.claimRepository.isAnyOpenClaimWithReplacedPart(oemPartReplaced,claim);
	}
	
	public Boolean isAnyActiveClaimOnMajorComponent(final InventoryItem inventoryItem) {
		return this.claimRepository.isAnyActiveClaimOnMajorComponent(inventoryItem);
	}
	
	public ClaimedItem findClaimedItem(final Long claimId, final Long inventoryId){
		return this.claimRepository.findClaimedItem(claimId, inventoryId);
	}
	
	public List<String> findTasksAssingedToUser(final String userId) {
		return this.claimRepository.findTasksAssingedToUser(userId);		
	}
	
	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}


	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public void setInvTransactionService(InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public void setMajorCompRegUtil(MajorCompRegUtil majorCompRegUtil) {
		this.majorCompRegUtil = majorCompRegUtil;
	}

	public MajorCompRegUtil getMajorCompRegUtil() {
		return majorCompRegUtil;
	}	
	
	public List<Long> findClaimsForBOMUpdation(){
		return this.claimRepository.findClaimsForBOMUpdation();
	}

	public PageResult<Claim> findClaimsForRecovery(ListCriteria listCriteria){
		return claimRepository.findClaimsForRecovery(listCriteria);
	}

    public List<ClaimType> fetchAllClaimTypesForBusinessUnit()
    {
        List<ClaimType> claimTypes = new ArrayList<ClaimType>();
		for (Object claimType : this.configParamService.getListofObjects(ConfigName.CLAIM_TYPE.getName())) {
			claimTypes.add(ClaimType.getUIDisplayName((String)claimType));
		}
		return claimTypes;
    }
    
	@SuppressWarnings("unchecked")
	public List<ListOfValues> getLovsForClass(String className, Claim claim) {
		List<ListOfValues> lovs = new ArrayList<ListOfValues>();

		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim
				.getBusinessUnitInfo().getName());
		lovs = this.lovRepository.findAllActive(className);
		if (ListOfValuesType.AccountabilityCode.name().equals(className)) {
		} else if (ListOfValuesType.AcceptanceReason.name().equals(className)) {
			if (claim.getAcceptanceReason() == null) {
				claim.setAcceptanceReason((AcceptanceReason) getConfigParamService()
						.getListOfValues("default" + className).get(0));
			}
		} else if (ListOfValuesType.AcceptanceReasonForCP.name().equals(
				className)) {
			if (claim.getAcceptanceReasonForCp() == null && getConfigParamService()
					.getListOfValues("default" + className) != null) {
				claim.setAcceptanceReasonForCp((AcceptanceReasonForCP) getConfigParamService()
						.getListOfValues("default" + className).get(0));
			}
		}

		return lovs;
	}
	
	public List<ItemGroup> listGroupCodeBasedOnGroupType() {
		return itemGroupService.listGroupCodeBasedOnGroupType();
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
	
	public List<Claim> getClaimsForItalyClaimNotification(Date recentTime){
		return claimRepository.getClaimsForItalyClaimNotification(recentTime);
	}

	public List<ItemGroup> findProductTypesByBrand(String itemGroupType,String brandType) {
		return this.claimRepository.findProductTypesByBrand(itemGroupType,brandType);
	}

	public List<ItemGroup> findModelTypesByBrand(String itemGroupType,String brandType) {
		return this.claimRepository.findModelTypesByBrand(itemGroupType,brandType);
	}
	
	public Long getCountOfPendingRecoveryClaims(){
		return this.claimRepository.getCountOfPendingRecoveryClaims();
	}
	
	public int updateCreditSubmissionDate(String claimNumber) {
		return this.claimRepository.updateCreditSubmissionDate(claimNumber);
	}

	public void updateCreditDateOfAcceptedAndDeniedClaims() {
	 int result=this.claimRepository.updateCreditSubmissionDateForAcceptedOrDeniedClaims();
	}

	public List<MarketingGroupsLookup> lookUpMktgGroupCodes(MarketingGroupsLookup lookup,boolean forProcessor) {
		return this.claimRepository.lookUpMktgGroupCodes(lookup,forProcessor);
	}
	public Location getLocationForDefaultPartReturn(String defaultPartReturnLocation){
		return this.claimRepository.getLocationForDefaultPartReturn(defaultPartReturnLocation);
	}
}

