package tavant.twms.web.supplier;

import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;

import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class WarrantyProcessorSRAction extends AbstractSupplierActionSupport implements Preparable {

    private Logger logger = Logger.getLogger(WarrantyProcessorSRAction.class);

    private String transition;

    private WorkListItemService workListItemService;

    private DomainRepository domainRepository;

    private PartReplacedService partReplacedService;
    
    @Override
    protected String getAlias() {
    	return "recoveryClaim";
    }
    

    @Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        return workListService.getSupplierRecoveryClaimBasedView(criteria);
    }

    // These two fields are only used for the popup window which shows
    // RecoveryAmountDetails
    private Long partId;

    public String preview() {
        fetchClaimView();
        return SUCCESS;
    }
    
    public String getSwimlaneRole(){
    	Assert.hasText(getId(), "Id should not be empty for fetch");
        TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(new Long(getId()).longValue(),
                getTaskName());
        return taskInstance.getSwimlaneInstance().getName();
    }
    
    public String detail() {
        fetchClaimView();
        return SUCCESS;
    }

    public String submitPreview() {

        return submit();
    }

    /**
     * This method is directly called in the jsp to get the claim for a
     * OEMPartReplaced
     * 
     * @param id
     * @return
     */
    public Claim getClaimForOEMPartReplaced(OEMPartReplaced oemPartReplaced) {
        return partReplacedService.getClaimForOEMPartReplaced(oemPartReplaced);
    }

    private void fetchClaimView() {
        Assert.hasText(getId(), "Id should not be empty for fetch");
        setRecoveryClaim(getRecoveryClaimService().findRecoveryClaim(new Long(getId())));
        getRecoveryClaim().setLoggedInUser(getLoggedInUser());
        setClaim(getRecoveryClaim().getClaim());
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(
        		getRecoveryClaim().getBusinessUnitInfo().getName());
    }

    private String submit() {
        getRecoveryClaim().setLoggedInUser(getLoggedInUser());
        if (!"".equals(transition)) {
            TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(getRecoveryClaim().getId(),
                    getTaskName());
            if(taskInstance == null){
                addActionError("error.common.simultaneousAction");
                return SUCCESS;
            }else{
                workListItemService.endTaskWithTransition(taskInstance, transition);
                addActionMessage("message.processor.claimResponded");
                return SUCCESS;
            }
        }
        return SUCCESS;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public WorkListItemService getWorkListItemService() {
        return workListItemService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public DomainRepository getDomainRepository() {
        return domainRepository;
    }

    public void setDomainRepository(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public PartReplacedService getPartReplacedService() {
        return partReplacedService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<Shipment>(inboxItems, pageSpecification, noOfPages);
    }

    @Override
    public void validate() {

        if (WorkflowConstants.REJECT.equals(transition) && !StringUtils.hasText(getRecoveryClaim().getComments())) {
            addActionError("error.supplier.requiredReason");
        }
        if(WorkflowConstants.ACCEPT.equals(transition) || WorkflowConstants.REJECT.equals(transition)){
        	if((getRecoveryClaim().getClaim().getAccountabilityCode() == null) || 
        			(getRecoveryClaim().getClaim().getAccountabilityCode() != null && getRecoveryClaim().getClaim().getAccountabilityCode().getCode() == null) ) 
        	addActionError("error.MultiClaimMaintainance.invalidReviewResponsibility");
        }        
    }

    public void prepare() throws Exception {
        // TODO Auto-generated method stub

    }    
    public String getClaimProcessedAsForDisplay(Claim claim) {	
    	if(!org.apache.commons.lang.StringUtils.isEmpty(claim.getPolicyCode())){
			return claim.getPolicyCode();
		}
		if (Constants.INVALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
			return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_STOCK.equals(claim.getClaimProcessedAs())) {
			return getText("label.common.itemInStock");
		} else if (Constants.VALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {			
				return getText("label.common.noWarranty");
		} else if (Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return getText("label.common.outOfWarranty");			
		}
		return claim.getClaimedItems().get(0).getApplicablePolicy().getCode();
	}

    public boolean isCommentsExist(Claim claim) {
		for (ClaimAudit claimAudit : claim.getClaimAudits()) {
			if (StringUtils.hasText(claimAudit.getInternalComments())  && !claimAudit.getUpdatedBy().hasRole(Role.SYSTEM)) {
				return true;
			}
		}
		return false;
	}

}
