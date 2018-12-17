package tavant.twms.web.customReports;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.domain.customReports.CustomReportService;
import tavant.twms.domain.customReports.ReportFormAnswer;
import tavant.twms.domain.customReports.ReportFormAnswerTypes;
import tavant.twms.domain.customReports.ReportFormQuestion;
import tavant.twms.domain.customReports.ReportSection;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemUtil;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Validateable;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 6, 2009
 * Time: 9:53:34 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("serial")
public class InventoryItemReportAnswerAction extends I18nActionSupport implements Validateable{
    private InventoryItem inventoryItem;
    private List<CustomReport> customReportList;
    private CustomReportService customReportService;
    private CustomReportAnswer reportAnswer;
    private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
    private WarrantyService warrantyService;
    private ConfigParamService configParamService;
    private String reportLinkType;
    private InventoryItemUtil inventoryItemUtil;

   boolean installationDateIsAfter = false;

    public boolean isInstallationDateIsAfter() {
		return installationDateIsAfter;
	}
    public boolean isPageReadOnly() {
		return false;

	}
    
    public boolean isPageReadOnlyAdditional() {
		boolean isReadOnlyDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
				isReadOnlyDealer = true;
				break;
			}
		}
		return isReadOnlyDealer;
	}
    
	public void validate() {
		if (getReportAnswer() != null) {			
			for (ReportFormAnswer formAnswer : getReportAnswer().getFormAnswers()) {
				if (formAnswer.getQuestion() != null) {
					if (formAnswer.getQuestion().getMandatory()) {
						if (!StringUtils.hasText(formAnswer.getAnswerValue()) && formAnswer.getAnswerOptions().isEmpty()) { 
							addActionError("error.reportAnswer.answerMandatory");
							break;
						} 						
					}					
					if (formAnswer.getQuestion().getAnswerType().equals(ReportFormAnswerTypes.NUMBER)
							&& StringUtils.hasText(formAnswer.getAnswerValue())
							&& !NumberUtils.isDigits(formAnswer.getAnswerValue())) {
						addActionError("error.reportAnswer.invalidAnswerType",
								new String[] { formAnswer.getQuestion().getName(),
										formAnswer.getQuestion().getForSection().getName() });
						break;
					}
				}
			}			
			if (getReportAnswer().getCustomReport().getReportType() !=null
					&& getReportAnswer().getCustomReport().getReportType() instanceof ReportType
					&& getReportAnswer().getCustomReport().getReportType().getCode().equalsIgnoreCase("COMMISSION")) {
				validateInstallationDate(getReportAnswer().getForInventory());
			}
		}

	}

    public String fetchReports(){
        customReportList=customReportService.findReportsForInventory(inventoryItem);

        if("PDI".equals(reportLinkType)){
        	List<CustomReport> pdiReports = new ArrayList<CustomReport>();

        	for(CustomReport report : customReportList){

        		if("PDI".equals(report.getReportType().getCode())){
        			pdiReports.add(report);
    			}
    		}
        	customReportList = pdiReports;
        }
        return SUCCESS;
    }

    public String displayReport(){
    	
		for (CustomReportAnswer reportAnswer : getReportAnswer().getForInventory().getReportAnswers()) {
			if (reportAnswer.getCustomReport().getId().longValue() == getReportAnswer().getCustomReport().getId().longValue()) {
				setReportAnswer(reportAnswer);
			}
		}
    	if (getReportAnswer().getId() == null) {
			if (isEnableReportFiling())
				createReportFormAnswers(getReportAnswer());
			else if (!isEnableReportFiling() && (getLoggedInUser().getBelongsToOrganization().getId().longValue() == getReportAnswer().getForInventory()
							.getCurrentOwner().getId().longValue() || isLoggedInUserAnInternalUser()
							|| inventoryItemUtil.stockBelongsToOEM(getReportAnswer().getForInventory())))
				createReportFormAnswers(getReportAnswer());
			else{
        		// Only if logged in user is current owner of inventory, allow him to file report
        		addActionError("error.customreport.ownedBy");
        		return INPUT;
        	}
		} else {
			if (getReportAnswer().getStatus() != null) {
				if ((getReportAnswer().getStatus().equalsIgnoreCase("DRAFT")
						&& (getReportAnswer().getD().getLastUpdatedBy().getId().longValue() == getLoggedInUser().getId().longValue() || isLoggedInUserAnInternalUser()))
						|| (getReportAnswer().getStatus().equalsIgnoreCase("SUBMITTED")))
					return SUCCESS;				
				else {
					addActionError("error.customreport.ownedBy");
					return INPUT;
				}
			}
		}
        return SUCCESS;
    }

    public String submitReport(){
    	InventoryItem inventoryItem = getReportAnswer().getForInventory();
    	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem.getBusinessUnitInfo().getName());
    	if(isWarrantyStartDateModificationAllowed()){
	    	Warranty warranty = inventoryItem.getLatestWarranty();
	    	Set<RegisteredPolicy> registeredPolicies = warranty.getPolicies();
	    	for(RegisteredPolicy registeredPolicy : registeredPolicies){
	    		if(WarrantyType.STANDARD.getType().equals(registeredPolicy.getWarrantyType().getType())
	    				|| WarrantyType.EXTENDED.getType().equals(registeredPolicy.getWarrantyType().getType())){
	    			PolicyDefinition definition = registeredPolicy.getPolicyDefinition();
	    			try {
	    				registeredPolicy.setWarrantyPeriod(definition.warrantyPeriodFor(inventoryItem,
	    						getReportAnswer().getInstallationDate()));
	    				this.warrantyService.activateRegisteredPolicyBasedOnInstallationDate(
	    						warranty, registeredPolicy,"Updated by Commission Report");
	    			} catch (PolicyException e) {
	    				addActionError(e.getMessage() + registeredPolicy.getCode());
	    			}
	    		}else if(WarrantyType.POLICY.getType().equals(registeredPolicy.getWarrantyType().getType())){
	    			Integer serviceHoursCovered = registeredPolicy.getLatestPolicyAudit().getServiceHoursCovered();
	    			CalendarDate fromDate = getReportAnswer().getInstallationDate();
	    			CalendarDate toDate = fromDate.plusDays(registeredPolicy.getLatestPolicyAudit().getWarrantyPeriod().getFromDate()
	    					.through(registeredPolicy.getLatestPolicyAudit().getWarrantyPeriod().getTillDate()).lengthInDaysInt()) ;
	    			CalendarDuration newWarrantyPeriod = new CalendarDuration(fromDate,toDate);	    			
	    			registeredPolicy.setWarrantyPeriod(newWarrantyPeriod);
	    			registeredPolicy.getLatestPolicyAudit().setServiceHoursCovered(serviceHoursCovered);
	    			this.warrantyService.activateRegisteredPolicyBasedOnInstallationDate(
	    					warranty, registeredPolicy,"Updated by Commission Report");
	    		}
	    	}
	    	if(hasActionErrors()){
	    		return INPUT;
	    	}
    	}
    	getReportAnswer().getAttachments().remove(Collections.singleton(null));
    	this.warrantyService.updateInventoryForWarrantyDates(getReportAnswer().getForInventory());
    	getReportAnswer().setStatus(SUBMITTED);
        customReportService.createCustomReportAmswer(getReportAnswer());
        addActionMessage("success.customReport.answeredSuccessfully");
        return SUCCESS;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }
    
    public String saveReport(){
    	getReportAnswer().setStatus(DRAFT);
    	getReportAnswer().getAttachments().remove(Collections.singleton(null));
        customReportService.updateCustomReportAnswer(getReportAnswer());
        addActionMessage("success.customReport.saveSuccessfully");
        return SUCCESS;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public List<CustomReport> getCustomReportList() {
        return customReportList;
    }

    public void setCustomReportList(List<CustomReport> customReportList) {
        this.customReportList = customReportList;
    }

    public CustomReportAnswer getReportAnswer() {
        return reportAnswer;
    }

    public void setReportAnswer(CustomReportAnswer reportAnswer) {
        this.reportAnswer = reportAnswer;
    }

    private void createReportFormAnswers(CustomReportAnswer reportAnswer){
        int counter=0;
        for (ReportSection section : reportAnswer.getCustomReport().getSections()) {
            for (ReportFormQuestion question : section.getQuestionnaire()) {
                ReportFormAnswer answer = new ReportFormAnswer();
                answer.setQuestion(question);
                answer.setSection(section);
                answer.setOrder(new Integer(counter));
                answer.getAnswerOptions().addAll(question.getDefaultAnswers());
                /*if(!Constants.TEXTBOX.equalsIgnoreCase(question.getAnswerType())){
                    answer.getAnswerOptions().add(question.getDefaultAnswer());
                }*/
                reportAnswer.getFormAnswers().add(answer);
                counter++;
            }
        }
    }

    public List<ReportFormAnswer> getReportAnswersForSection(Long sectionId){
        List<ReportFormAnswer> answers = new ArrayList<ReportFormAnswer>();
        for (ReportFormAnswer formAnswer : getReportAnswer().getFormAnswers()) {
          if(formAnswer.getSection()!=null){
            if(formAnswer.getSection().getId().longValue()==sectionId.longValue()){
                answers.add(formAnswer);
            }
          } 
        }
        return answers;
    }

    @Required
    public void setCustomReportService(CustomReportService customReportService) {	
        this.customReportService = customReportService;
    }
    
    private boolean validateInstallationDate(InventoryItem inventoryItem) {
		boolean isValid = true;

		if (getReportAnswer().getInstallationDate() == null) {
			installationDateIsAfter=true;
			// User needs to choose installation date
			addActionError("error.installationDateNotFound",
					new String[] { inventoryItem.getSerialNumber() });
			isValid = false;
		} else {
			if (getReportAnswer().getInstallationDate().isAfter(Clock.today())) {
				// The installation date chosen by the user is not on or before
				// today.
				installationDateIsAfter=true;
				addActionError("error.installationDateCannotBeInFuture",
						new String[] { inventoryItem.getSerialNumber() });
				isValid = false;
			}

			if (getReportAnswer().getInstallationDate().isBefore(
					inventoryItem.getShipmentDate())) {
				// The installation date chosen by the user is before shipment date
				installationDateIsAfter=true;
				addActionError("error.installationDateBeforeShipment",
						new String[] { inventoryItem.getSerialNumber() });
				isValid = false;
			}
		}

		return isValid;
	}

	public InventoryScrapTransactionXMLConverter getInventoryScrapTransactionXMLConverter() {
		return inventoryScrapTransactionXMLConverter;
	}

	public void setInventoryScrapTransactionXMLConverter(
			InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
		this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
	}

	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}
	
	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		this.configParamService = (ConfigParamService) beanLocator
				.lookupBean("configParamService");
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public boolean isWarrantyStartDateModificationAllowed(){
		boolean warrantyStartDateModificationAllowed = false;
		CalendarDate deliveryDate = getReportAnswer().getForInventory().getDeliveryDate();
		
		if (this.configParamService == null) {
			initDomainRepository();
		}
		
		Long windowPeriod  = this.configParamService.getLongValue(ConfigName.INSTALLATION_DATE_WINDOW_PERIOD.
									getName());
		
		if(deliveryDate != null){
			return deliveryDate.plusDays(windowPeriod.intValue()).isAfter(getReportAnswer().getInstallationDate());
		}
		return warrantyStartDateModificationAllowed;
		
	}
	
	public String showAttachments() {
		return SUCCESS;
	}
	
	// FIXME: Any exception that occurs comes up as a java script
	// Hence returning "{}" for any exception that occurs.
	public String getJSONifiedAttachmentList() {
		try {
			if (getReportAnswer() != null && getReportAnswer().getAttachments() != null
					&& !getReportAnswer().getAttachments().isEmpty()){
				return getDocumentListJSON(getReportAnswer().getAttachments()).toString();
			}
			return "[]";
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public boolean isEnableReportFiling() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_REPORT_FILING_ANY_DEALER.getName());
	}

	public String getReportLinkType() {
		return reportLinkType;
	}

	public void setReportLinkType(String reportLinkType) {
		this.reportLinkType = reportLinkType;
	}

	public void setInventoryItemUtil(InventoryItemUtil inventoryItemUtil) {
		this.inventoryItemUtil = inventoryItemUtil;
	}
	
}
