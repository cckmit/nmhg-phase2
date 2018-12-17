package tavant.twms.web.claim;

import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.common.DisplayWarningPropertyResolver;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.web.xforms.TaskView;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.BeanProvider;
import tavant.twms.worklist.WorkList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.customReports.*;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.inventory.InventoryItem;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Mar 24, 2010
 * Time: 2:09:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageClaimFailureReportAction extends ClaimsAction{
	
	private static final Logger logger = Logger.getLogger(ClaimsAction.class);
    private Claim claim;
    private List<CustomReport> failureReportForClaim;
    private CustomReportAnswer customReportAnswer;
    private Item item;
    private InventoryItem inventoryItem;
    private String unSzdSlNo;
    private OEMPartReplaced replacedPart;	
    private Collection<Item> itemsApplicableOnReport;

    @Override
    public Claim getClaimDetail() {
		if(getId()== null){
            logger.info("The task Id cannot be null");
        }else{
            setTask(getTaskViewService().getTaskView(Long
						.parseLong(getId())));
            return getTask().getClaim();
        }
        return null;
    }

    @Override
    public void prepare(){

    }

    @Override
	protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        header.add(new SummaryTableColumn(
                "label.inboxView.workOrderNumber", "claim.activeClaimAudit.workOrderNumber",
                12, "String", "claim.activeClaimAudit.workOrderNumber", true, false, false,
                false));
        header.add(new SummaryTableColumn("columnTitle.newClaim.createdOn",
				"claim.filedOnDate", 10, "date"));
        header.add(new SummaryTableColumn("columnTitle.newClaim.task", "id", 0,
                "String", "id", false, true, true, false));
        header.add(new SummaryTableColumn("label.inboxView.claimType",
                "claim.clmTypeName", 10, "string"));
        header.add(new SummaryTableColumn("columnTitle.newClaim.failureCode",
                "claim.activeClaimAudit.serviceInformation.faultCode", 12, "String"));
        header.add(new SummaryTableColumn("columnTitle.newClaim.causalPart",
                "claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber", 12, "String"));
        header.add(new SummaryTableColumn("label.inboxView.failureDate",
				"claim.activeClaimAudit.failureDate", 10, "date"));
		header.add(new SummaryTableColumn("label.inboxView.repairDate",
				"claim.activeClaimAudit.repairDate", 10, "date"));
        
        return header;
    }

    @Override
    public void validate(){
        validateCustomReportAnswer();
    }
    
    public boolean isPageReadOnly() {
		return false;
	}

    @Override
    public String detail(){
        setClaim(getClaimDetail());
        List<CustomReport> failureReports = fetchFailureReportsForClaim(getClaim());
        if (!failureReports.isEmpty()) {
            associateFailureReportsToParts(failureReports, getClaim());
        }
        return SUCCESS;
    }

	private void associateFailureReportsToParts(List<CustomReport> failureReports, Claim claim) {
		for (CustomReport failureReport : failureReports) {
			boolean reportSetCausalPart = false, reportSetReplaced = false, reportSetInstalled = false;
			OEMPartReplaced oemPartReplacedVal = null;
			InstalledParts installedPartVal = null;
			for (CustomReportApplicablePart applicablePart : failureReport.getApplicableParts()) {
				if (applicablePart.isApplicableForType(Applicability.CAUSAL)
						&& getClaimService().isCustomReportPartApplicableForItem(applicablePart,
								claim.getServiceInformation().getCausalPart()) && !reportSetCausalPart) {
					reportSetCausalPart = true;
				}

				if (applicablePart.isApplicableForType(Applicability.REMOVED)) {
					for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail()
							.getReplacedParts()) {
						if (getClaimService().isCustomReportPartApplicableForItem(applicablePart,
								oemPartReplaced.getItemReference().referenceForItem())
								&& !reportSetCausalPart && !reportSetReplaced) {
							oemPartReplacedVal = oemPartReplaced;
							reportSetReplaced = true;
						}
					}
				}

				if (applicablePart.isApplicableForType(Applicability.INSTALLED)) {
					for (InstalledParts installedPart : claim.getServiceInformation().getServiceDetail()
							.getInstalledParts()) {
						if (getClaimService().isCustomReportPartApplicableForItem(applicablePart,
								installedPart.getItem())
								&& !reportSetCausalPart && !reportSetReplaced && !reportSetInstalled) {
							installedPartVal = installedPart;
							reportSetInstalled = true;
						}
					}
				}
			}
			if (reportSetCausalPart && !claimService.reportAlreadyAnswered(claim,failureReport)) {
				if (claim.getServiceInformation().getCustomReportAnswer() == null) {
					claim.getServiceInformation().setCustomReportAnswer(new CustomReportAnswer());
					createReportAnswersForFailureReportAnswer(claim.getServiceInformation().getCustomReportAnswer(),
							failureReport);
				}
			} else if(reportSetReplaced && !claimService.reportAlreadyAnswered(claim,failureReport)) {
				if (oemPartReplacedVal.getCustomReportAnswer() == null) {
					oemPartReplacedVal.setCustomReportAnswer(new CustomReportAnswer());
					createReportAnswersForFailureReportAnswer(oemPartReplacedVal.getCustomReportAnswer(), failureReport);
				}
			} else if (reportSetInstalled && !claimService.reportAlreadyAnswered(claim,failureReport)) {
				if (installedPartVal.getCustomReportAnswer() == null) {
					installedPartVal.setCustomReportAnswer(new CustomReportAnswer());
					createReportAnswersForFailureReportAnswer(installedPartVal.getCustomReportAnswer(), failureReport);
				}
			}

		}
	}
	

    public ServiceInformation getServiceInformation(){
        return getClaim().getServiceInformation();
    }

    public List<InstalledParts> getInstalledParts(){
       return getClaim().getServiceInformation().getServiceDetail().getInstalledParts();
    }

    public List<OEMPartReplaced> getReplacedParts(){
        return getClaim().getServiceInformation().getServiceDetail().getReplacedParts();
    }

    private List<CustomReport> fetchFailureReportsForClaim(Claim claim){
       return getClaimService().fetchfailureReportsForItemsOnClaim(claim);
    }

    @Override
    public String preview(){
      detail();
      return SUCCESS;
    }

    public String save(){
        if(getClaim()==null){
            logger.info("The task Id cannot be null");
        }else{
            setFailureReportStatus(DRAFT);
            getClaimService().updateClaim(claim);
            addActionMessage("success.failureReports.save");
        }
        return SUCCESS;
    }

    public String delete(){
        setClaim(getClaimDetail());
        if (ClaimState.SERVICE_MANAGER_RESPONSE.getState().equalsIgnoreCase(getClaim().getState().getState())) {
            getTask().setTakenTransition("Delete");
        } else {
            getTask().setTakenTransition("Delete Draft Claim");
        }
        getTaskViewService().submitTaskView(getTask());
        addActionMessage("success.claim.delete");
        return SUCCESS;
    }

    @Override
    public String submit(){
        setClaim(getClaimDetail());
        setFailureReportStatus(SUBMITTED);
        getClaim().setFailureReportPending(false);
        getTask().setTakenTransition("Submit");
        getTaskViewService().submitTaskView(getTask());
        if (StringUtils.hasText(claim.getClaimNumber())) {
            addActionMessage("message.newClaim.submitClaimSuccess", getClaim().getClaimNumber());
        }
        return SUCCESS;
    }

    public String displayReport(){
    	if(this.claim!=null && this.customReportAnswer!=null){
    		 this.itemsApplicableOnReport=claimService.itemsApplicableOnReport(claim,customReportAnswer.getCustomReport());
    	}
        return SUCCESS;
    }

    private void setFailureReportStatus(String status) {
        if(getServiceInformation().getCustomReportAnswer()!=null)
        getServiceInformation().getCustomReportAnswer().setStatus(status);
        for (InstalledParts installedPart : getInstalledParts()) {
            if(installedPart.getCustomReportAnswer()!=null)
            installedPart.getCustomReportAnswer().setStatus(status);
        }
        for (OEMPartReplaced oemPartReplaced : getReplacedParts()) {
            if(oemPartReplaced.getCustomReportAnswer()!=null)
            oemPartReplaced.getCustomReportAnswer().setStatus(status);
        }
    }

    private void validateCustomReportAnswer() {
        if(getServiceInformation().getCustomReportAnswer()!=null){
            validateReportAnswers(getServiceInformation().getCustomReportAnswer().getFormAnswers());
        }
        for (InstalledParts installedPart : getInstalledParts()) {
            if(installedPart.getCustomReportAnswer()!=null)
            validateReportAnswers(installedPart.getCustomReportAnswer().getFormAnswers());
        }
        for (OEMPartReplaced oemPartReplaced : getReplacedParts()) {
            if(oemPartReplaced.getCustomReportAnswer()!=null)
            validateReportAnswers(oemPartReplaced.getCustomReportAnswer().getFormAnswers());
        }
    }

    private void validateReportAnswers(List<ReportFormAnswer> formAnswers) {
        for (ReportFormAnswer formAnswer : formAnswers) {        	
            if(formAnswer.getQuestion().getMandatory()
                    && (formAnswer.getAnswerOptions().isEmpty()
                    && !StringUtils.hasText(formAnswer.getAnswerValue())
                    && formAnswer.getAnswerDate()==null)){
                addActionError("error.failureReport.selectAnswer",new String[]{formAnswer.getQuestion().getName(),formAnswer.getQuestion().getForSection().getName()});
                break;
            }            
            if (formAnswer.getQuestion().getAnswerType().equals(ReportFormAnswerTypes.NUMBER) 
					&& StringUtils.hasText(formAnswer.getAnswerValue()) 
					&& !NumberUtils.isDigits(formAnswer.getAnswerValue())) {
				addActionError("error.reportAnswer.invalidAnswerType",new String[]{formAnswer.getQuestion().getName(),formAnswer.getQuestion().getForSection().getName()});
				break;
			}
        }
    }


    private void createReportAnswersForFailureReportAnswer(CustomReportAnswer failureReportAnswer, CustomReport failureReport){
        int counter =0;
        failureReportAnswer.setCustomReport(failureReport);
        for (ReportSection section : failureReportAnswer.getCustomReport().getSections()) {
            for (ReportFormQuestion question : section.getQuestionnaire()) {
                ReportFormAnswer answer = new ReportFormAnswer(counter,question,section);
                answer.setAnswerOptions(question.getDefaultAnswers());
                failureReportAnswer.getFormAnswers().add(answer);
            }
        }
    }
    

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public List<CustomReport> getFailureReportForClaim() {
        return failureReportForClaim;
    }

    public void setFailureReportForClaim(List<CustomReport> failureReportForClaim) {
        this.failureReportForClaim = failureReportForClaim;
    }

    public CustomReportAnswer getCustomReportAnswer() {
        return customReportAnswer;
    }

    public void setCustomReportAnswer(CustomReportAnswer customReportAnswer) {
        this.customReportAnswer = customReportAnswer;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public String getUnSzdSlNo() {
        return unSzdSlNo;
    }

    public void setUnSzdSlNo(String unSzdSlNo) {
        this.unSzdSlNo = unSzdSlNo;
    }

	public OEMPartReplaced getReplacedPart() {
		return replacedPart;
	}

	public void setReplacedPart(OEMPartReplaced replacedPart) {
		this.replacedPart = replacedPart;
	}

	public Collection<Item> getItemsApplicableOnReport() {
		return itemsApplicableOnReport;
	}

	public void setItemsApplicableOnReport(Collection<Item> itemsApplicableOnReport) {
		this.itemsApplicableOnReport = itemsApplicableOnReport;
	}
	
	
    
}
