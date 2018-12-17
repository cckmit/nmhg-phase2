package tavant.twms.web.claim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.common.RecoveryClaimAcceptanceReason;
import tavant.twms.domain.common.RecoveryClaimRejectionReason;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.infra.ListCriteria;
import tavant.twms.web.search.RecoveryClaimSearchAction;
import tavant.twms.worklist.WorkListItemService;

import com.opensymphony.xwork2.Preparable;
import common.Logger;

@SuppressWarnings("serial")
public class MultiRecoveryClaimMaintenanceAction extends RecoveryClaimSearchAction implements Preparable{

	private final Logger logger = Logger.getLogger(this.getClass());
	private String contextName;
	private String isMultiRecClaimMaintainace;
	private boolean attributeSelected;
	private boolean multiSupplierClaimsSelected;
	List<RecoveryClaim> recoveryClaims = new ArrayList<RecoveryClaim>(5);
	List<RecoveryClaim> restoreRecClaimsList = new ArrayList<RecoveryClaim>(5);
	private MultiClaimMaintenanceAttributes attributeMapper = new MultiClaimMaintenanceAttributes();
	private WorkListItemService workListItemService;
	
	public MultiRecoveryClaimMaintenanceAction(){
		super();
		this.contextName="RecoveryClaimSearches";
		this.attributeMapper.getRecClaimAcceptanceReason().setName(getText("title.attributes.acceptanceReason"));
		this.attributeMapper.getRecClaimRejectionReason().setName(getText("title.attributes.disputeReason"));
		this.attributeMapper.getRgaNumber().setName(getText("columnTitle.recoveryClaim.rgaNumber"));
		this.attributeMapper.getLocation().setName(getText("label.partReturnConfiguration.location"));
		this.attributeMapper.getCarrier().setName(getText("columnTitle.duePartsReceipt.carrier"));
		this.attributeMapper.setListOfRecoveryAttributes();
	}
	
	
	public void prepare() throws Exception {
		
    }
	
    
    @Override
	public void validate() {
    	
    	if(!isRecoveryClaimSelected()){
    		addActionError("error.multiClaimMaintain.selectClaim");
    	}
    	
     	if(!this.attributeMapper.isAnySupplierAttributeSelected(isMultiSupllierClaims())){
    		addActionError("error.multiClaimMaintain.selectAttribute");
    	}
    	if(isAttributeSelected() &&
    			!this.attributeMapper.isAttributeValueSet()){
    		addActionError("error.multiClaimMaintain.setRgaNumber");
    	}
    	recoveryClaims = getSelectedClaims(recoveryClaims);
    	if(attributeMapper.getRecClaimAcceptanceReason().isSelected() && 
    			!attributeMapper.getRecClaimRejectionReason().isSelected()){
    		checkValidityForReasonUpdation(true,recoveryClaims);
    	}else if(attributeMapper.getRecClaimRejectionReason().isSelected() && 
    					!attributeMapper.getAcceptanceReason().isSelected()){
    		checkValidityForReasonUpdation(false,recoveryClaims); 
    	}
    	if(attributeMapper.getCarrier().isSelected() || attributeMapper.getLocation().isSelected() || attributeMapper.getRgaNumber().isSelected()){
	    	if (isMultiSupplierClaimsSelected()){
	    		addActionError("error.multiClaimMaintain.multipleSupplier");
	    	}else {
	    		checkValidityForShipmentUpdation(recoveryClaims);
	    	}
    	}
    	if(this.recoveryClaims==null || this.recoveryClaims.size() == 0 ||
    			(getActionErrors()!=null && getActionErrors().size() > 0)){
//    		if(this.recoveryClaims==null || this.recoveryClaims.size() == 0){
//    			//addActionError("error.multiClaimMaintain.selectClaim");
//    		}
    		this.recoveryClaims=this.restoreRecClaimsList;
    	}
    	
    	if (this.recoveryClaims != null && this.recoveryClaims.size() > 0) {
			if (isMultiBUClaimsSelected()) {
				addActionError("error.multiClaimMaintain.multiBUClaimsSelected");
				this.recoveryClaims = this.restoreRecClaimsList;
			}

		}
    	
    }
    
    private List<RecoveryClaim> getSelectedClaims(List<RecoveryClaim> recClaims){
    	List<RecoveryClaim> selClaims = new ArrayList<RecoveryClaim>();
    	for(RecoveryClaim claim : recClaims){
    		if(claim.isSelected()){
    			selClaims.add(claim);
    		}
    	}
    	return selClaims;
    }
    
    private boolean isRecoveryClaimSelected(){
    	boolean isSelected = false;
    	
    	if(this.recoveryClaims != null && this.recoveryClaims.size() > 0){
    		for(RecoveryClaim recClaim : this.recoveryClaims){
    			if(recClaim.isSelected()){
    				isSelected = true;
    				break;
    			}
    		}
    	}
    	return isSelected;
    }
	
	public String recClaimsForMultipleMaintenance(){
		if (getServletRequest().getAttribute("domainPredicateId") != null) {
			setDomainPredicateId(getServletRequest().getAttribute("domainPredicateId").toString());
		}
		if (getServletRequest().getAttribute("savedQueryId") != null) {
			setSavedQueryId(getServletRequest().getAttribute("savedQueryId").toString());
		}
		if (getDomainPredicateId() != null && !("".equals(getDomainPredicateId().trim()))) {
			this.recoveryClaims = getRecoveryClaimService().findAllRecClaimsForMultiMaintainance(Long.parseLong(getDomainPredicateId()), getCriteria());
		} else {
			this.logger.error("domain Predicate Id is null ");
		}
		return SUCCESS;
		
	}
	
	public String setClaimsForMaintenance(){
		this.attributeSelected=true;
		return SUCCESS;
	}
	
	public String updateClaimsForMaintenance(){
		
		if(this.attributeMapper.isAnySupplierAttributeSelected(isMultiSupplierClaimsSelected()) && isAttributeSelected()){
			String bu=this.recoveryClaims.get(0).getBusinessUnitInfo().getName();
			this.attributeMapper.setLovObjects(bu);
			this.attributeMapper.setShippingInfo();
		}
		List<String> recClaims = new ArrayList<String>();
		for (Iterator<RecoveryClaim> iterator = this.recoveryClaims.iterator(); iterator.hasNext();) {
			RecoveryClaim recClaim = iterator.next();

			if(!validateReasonSelected()){
				break;
			}
			setSelectedAttributes(attributeMapper, recClaim);
			getRecoveryClaimService().updateRecoveryClaim(recClaim);
			recClaims.add(recClaim.getRecoveryClaimNumber());
		}
		
		if(hasActionErrors()){
			return INPUT;
		}
		addActionMessage("success.multiRecClaimMaintain.updated", recClaims.toArray(new String[0]));
		return SUCCESS;
	}
	
	public Collection<Location> getSupplierLocation(){
		Collection<Location> locations=recoveryClaims.get(0).getContract().getSupplier().getLocations();
	    return locations;
    }
	
	public List<Carrier> getAllCarriers() {
		return this.attributeMapper.getCarriers();
	}
	
	
	
	public String getContextName() {
		return this.contextName;
	}


	public void setContextName(String contextName) {
		this.contextName = contextName;
	}


	@Override
	public ListCriteria getCriteria(){
		ListCriteria listCriteria = new ListCriteria();
		return listCriteria;
	}


	public List<RecoveryClaim> getRestoreRecClaimsList() {
		return this.restoreRecClaimsList;
	}


	public void setRestoreRecClaimsList(List<RecoveryClaim> restoreRecClaimsList) {
		this.restoreRecClaimsList = restoreRecClaimsList;
	}


	public MultiClaimMaintenanceAttributes getAttributeMapper() {
		return this.attributeMapper;
	}


	public void setAttributeMapper(MultiClaimMaintenanceAttributes attributeMapper) {
		this.attributeMapper = attributeMapper;
	}
	
	private void setSelectedAttributes(MultiClaimMaintenanceAttributes mapper, RecoveryClaim recClaim) {
		if (mapper.getRecClaimAcceptanceReason().isSelected()) {
			recClaim.setRecoveryClaimAcceptanceReason((RecoveryClaimAcceptanceReason) mapper.getRecClaimAcceptanceReason().getAttribute());
		}
		if (mapper.getRecClaimRejectionReason().isSelected()) {
			recClaim.setRecoveryClaimRejectionReason((RecoveryClaimRejectionReason) mapper.getRecClaimRejectionReason().getAttribute());
		}

		for (RecoverablePart recoverablePart : recClaim.getRecoveryClaimInfo().getRecoverableParts()) {
			for (SupplierPartReturn supplierPartReturn : recoverablePart.getSupplierPartReturns()) {
				if (supplierPartReturn != null
						&& supplierPartReturn.getStatus().ordinal() > PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED.ordinal()
						&& supplierPartReturn.getStatus().ordinal() < PartReturnStatus.SHIPMENT_GENERATED.ordinal()) {
					if (mapper.getRgaNumber().isSelected()) {
						supplierPartReturn.setRgaNumber(((String[]) mapper.getRgaNumber().getAttribute())[0]);
					}
					if (mapper.getCarrier().isSelected()) {
						supplierPartReturn.setCarrier((Carrier) mapper.getCarrier().getAttribute());
					}
					if (mapper.getLocation().isSelected()) {
						supplierPartReturn.setReturnLocation(((Location) mapper.getLocation().getAttribute()));
					}
				}
			}
		}
	}
	
	private boolean validateReasonSelected(){
		boolean selected = true;
		if (attributeMapper.getRecClaimAcceptanceReason().isSelected()) {
			selected = isReasonSelected(attributeMapper.getRecClaimAcceptanceReason().getAttribute(), 
					attributeMapper.getRecClaimAcceptanceReason().getName());
		} 
		if (attributeMapper.getRecClaimRejectionReason().isSelected()) {
			selected = isReasonSelected(attributeMapper.getRecClaimRejectionReason().getAttribute(), 
					attributeMapper.getRecClaimRejectionReason().getName());
		}
		return selected;
	}

	private boolean isReasonSelected(Object selectedReason, String name){
		boolean selected = true;
		if(selectedReason == null){
			addActionError("error.MultiClaimMaintainance.reasonAttributeRequired", name);
			selected = false;
		}		
		return selected;
	}
	
	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
	public boolean isAttributeSelected() {
		return this.attributeSelected;
	}


	public void setAttributeSelected(boolean attributeSelected) {
		this.attributeSelected = attributeSelected;
	}
	
	
	public boolean isMultiSupplierClaimsSelected() {
		return this.multiSupplierClaimsSelected;
	}


	public void setMultiSupplierClaimsSelected(boolean multiSupplierClaimsSelected) {
		this.multiSupplierClaimsSelected = multiSupplierClaimsSelected;
	}


	private boolean isMultiSupllierClaims(){
		Long supplierId =null ;
		if(this.recoveryClaims!=null && this.recoveryClaims.size()>0){
			for (Iterator<RecoveryClaim> iterator = this.recoveryClaims.iterator(); iterator.hasNext();) {
				if(iterator.next()==null){
					iterator.remove();
				}
			}
		}
		if(recoveryClaims!=null && recoveryClaims.size()!=0){
			supplierId=this.recoveryClaims.get(0).getContract().getSupplier().getId();
		}
		for (RecoveryClaim recClaim : this.recoveryClaims) {
			if(recClaim !=null && supplierId!=null && supplierId.longValue()!=recClaim.getContract().getSupplier().getId().longValue()){
				this.multiSupplierClaimsSelected=true;
				break;
			}
		}
		return this.multiSupplierClaimsSelected;
	}
	
	private void checkValidityForReasonUpdation(boolean isAcceptanceSelected,List<RecoveryClaim> recClaims){
		for (RecoveryClaim recClaim : recClaims) {
			
			if(isAcceptanceSelected && (recClaim.getRecoveryClaimState() == null ||
					! recClaim.getRecoveryClaimState().getState().contains("Closed") ||
						RecoveryClaimState.CLOSED_UNRECOVERED.equals(recClaim.getRecoveryClaimState().getState()))){
				addActionError("error.MultiClaimMaintainance.recoveryAcceptanceReason");
				break;
			}else if(!isAcceptanceSelected && (recClaim.getRecoveryClaimState() == null ||
					!( RecoveryClaimState.REJECTED.getState().equals(recClaim.getRecoveryClaimState().getState())
							||  RecoveryClaimState.CLOSED_UNRECOVERED.getState().equals(recClaim.getRecoveryClaimState().getState().contains("Closed"))))){
				addActionError("error.MultiClaimMaintainance.recoveryRejectionReason");
				break;
			}
		}
	}
	
	private void checkValidityForShipmentUpdation(List<RecoveryClaim> recClaims) {
		for (RecoveryClaim recClaim : recClaims) {
			for (RecoverablePart recoverablePart : recClaim.getRecoveryClaimInfo().getRecoverableParts()) {
				for (SupplierPartReturn supplierPartReturn : recoverablePart.getSupplierPartReturns()) {
					if (supplierPartReturn == null
							|| supplierPartReturn.getStatus().ordinal() <= PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED.ordinal()) {
						addActionError("error.MultiClaimMaintainence.noPartReturn", new String[] { recClaim.getClaim().getClaimNumber() });
						break;
					} else if (supplierPartReturn.getStatus().ordinal() > PartReturnStatus.SHIPMENT_GENERATED.ordinal()) {
						addActionError("error.MultiClaimMaintainence.partShipped");
						break;
					}
				}
			}
		}
	}
	
	private boolean isMultiBUClaimsSelected() {
		boolean multiBUClaimsSelected = false;
		String selectedClaimBu = null;

		if (this.recoveryClaims != null && !this.recoveryClaims.isEmpty()) {
			for (Iterator<RecoveryClaim> iterator = this.recoveryClaims.iterator(); iterator.hasNext();) {
				if (iterator.next() == null) {
					iterator.remove();
				}
			}
		}
		if (this.recoveryClaims != null && !this.recoveryClaims.isEmpty()) {
			selectedClaimBu = this.recoveryClaims.get(0).getBusinessUnitInfo().getName();
		}
		for (RecoveryClaim recoveryClaims : this.recoveryClaims) {
			if (recoveryClaims.getBusinessUnitInfo().getName() != null
					&& !selectedClaimBu.equalsIgnoreCase(recoveryClaims.getBusinessUnitInfo().getName())) {
				multiBUClaimsSelected = true;
				break;
			}
		}
		return multiBUClaimsSelected;
	}


	public List<RecoveryClaim> getRecoveryClaims() {
		return recoveryClaims;
	}


	public void setRecoveryClaims(List<RecoveryClaim> recoveryClaims) {
		this.recoveryClaims = recoveryClaims;
	}


	public String getIsMultiRecClaimMaintainace() {
		return isMultiRecClaimMaintainace;
	}


	public void setIsMultiRecClaimMaintainace(String isMultiRecClaimMaintainace) {
		this.isMultiRecClaimMaintainace = isMultiRecClaimMaintainace;
	}


	
}
