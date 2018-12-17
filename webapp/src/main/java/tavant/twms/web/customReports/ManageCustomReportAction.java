package tavant.twms.web.customReports;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

import tavant.twms.web.actions.InventoryAction;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.customReports.*;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.infra.i18n.ProductLocale;

import java.util.*;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 2:04:24 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * @author amritha.k
 *
 */
public class ManageCustomReportAction extends I18nActionSupport implements Preparable, Validateable {
	//Fields
    private ItemGroupRepository itemGroupRepository;
    private String taskName;
    private ReportSection section;
    private ReportFormQuestion questionnaire;
    private CustomReportService customReportService;
    private CustomReport customReport ;
    private ProductLocaleService productLocaleService;
    private List<ProductLocale> locales;
    private List<InventoryType> inventoryTypes = new ArrayList<InventoryType>();
    private Boolean overWriteReports = Boolean.FALSE;    
    private List<ItemGroup> conflictingProdModForReport = new ArrayList<ItemGroup>();
    private List<CustomReportApplicablePart> conflictingAppParts = new ArrayList<CustomReportApplicablePart>();
    private boolean validItemGroups;
    private boolean validInventoryTypes;
    private List<String> productTypes = new ArrayList<String>();
    private List<Applicability>  applicabilities = new ArrayList<Applicability>();
    private List<CustomReportApplicablePart> applicableParts = new ArrayList<CustomReportApplicablePart>();
    private CatalogService catalogService;
    private String forItemGroupId;
    private String applicablePartItemId;
    private String applicablePartItemGroupId;
    private Boolean addNextQuestion;
    private Map<ReportFormAnswerTypes,String> answerTypes ;
	private static final Logger logger = Logger
    .getLogger(ManageCustomReportAction.class);
    
	//Prepare method
    public void prepare() throws Exception {
    	inventoryTypes.add(InventoryType.RETAIL);
    	inventoryTypes.add(InventoryType.STOCK);
    	applicabilities.add(Applicability.CAUSAL);
    	applicabilities.add(Applicability.INSTALLED);
    	applicabilities.add(Applicability.REMOVED);
    	}
      	
   //Validate Methods 
    
	@Override
    public void validate() {    
	 if(!hasFieldErrors() && !hasActionErrors())
		 validateReport();
    }    
	
	private void validateReport() {
		setBooleansForValidItems();
		if (customReport != null && validItemGroups && validInventoryTypes) {
			if (checkForDuplicateProductsAndParts() && validateSectionOrderDuplicacy(getCustomReport())) {
				checkIfReportIsDuplicate();
			}
		}
	}
	
	public boolean isReportNameDuplicate(){
		if(this.customReport !=null && StringUtils.hasText(this.customReport.getName())){
		 return	this.customReportService.isReportNameDuplicate(this.customReport.getName(),this.customReport.getId());
		}
		return false;
	}
	

	private boolean validateSectionOrderDuplicacy(CustomReport customReport) {
		/* This validation is performed while creating a section for the Report */
		boolean isValid = true;
		if (customReport != null && customReport.getSections() != null) {
			for (ReportSection currentSection : customReport.getSections()) {
				for (ReportSection eachSection : customReport.getSections()) {
					if (!eachSection.equals(currentSection)
							&& eachSection.getOrder().longValue() == currentSection.getOrder().longValue()) {
						isValid = false;
						break;
					}
				}
			}
			if (!isValid) {
				addActionError("label.customReport.duplicateSectionOrder");
			}
		}
		return isValid;
	}
	
    private void setBooleansForValidItems(){
    	validItemGroups = customReport!=null && 
							 atLeastOneElementExists(customReport.getForItemGroups()) 
							&& atLeastOneElementExistsInAppPart(customReport.getApplicableParts());
    	validInventoryTypes = checkIfInventoryTypesSet();
    }
    
	
	private boolean checkIfInventoryTypesSet() {
		String param = request.getParameter("customReport.forInventoryTypes");
		if (customReport != null && param == null) {
			customReport.setForInventoryTypes(null);
			if (CollectionUtils.isEmpty(customReport.getForItemGroups())) {
				return true;
			} else {
				addActionError("error.customReport.inventoryTypesRequired");
				return false;
			}
		}
		return true;
	}
    
	private boolean atLeastOneElementExists(List<ItemGroup> forItemGroups) {
		boolean isValid=true;
		if ((forItemGroups == null || forItemGroups.isEmpty())&& //use constants here.!
				(customReport.getReportType()!=null && !("FAILURE REPORT".equalsIgnoreCase(customReport.getReportType().getCode())))) {
			addActionError("error.customReport.itemGroupsRequired.notEmptyForFailureReports");
			isValid=false;
		} else {
			for (ItemGroup ig : forItemGroups) {
				if (ig == null || (ig != null && ig.getId() == null)) {
					isValid=false;
				} 
			}
			if(!isValid){
				addActionError("error.customReport.itemGroupsRequired");
			}
		}
		return isValid;
	}

    
	private boolean atLeastOneElementExistsInAppPart(List<CustomReportApplicablePart> appParts) {
		boolean isValid = true;
		for (CustomReportApplicablePart custApp : appParts) {
			if (custApp != null && custApp.getItemCriterion() != null) {
				if (!custApp.isItemCriterionItemGroup() &&  custApp.getItemCriterion().getItem() == null ) {
					isValid = false;
				}else if(custApp.isItemCriterionItemGroup() && custApp.getItemCriterion().getItemGroup()== null){
					isValid=false;
				}
				if(!isValid){
					addActionError("error.customReport.applicablePartsRequired");
					return false;
				}
				
			}
			if(custApp!=null && CollectionUtils.isEmpty(custApp.getApplicabilityList())){
				addActionError("error.customReport.applicableParts.applicabilityRequired");
				return false;
			}
		}
		return isValid;
	}
    

    
    private boolean checkForDuplicateProductsAndParts(){
    	Set<ItemGroup> noDuplicateListProductModels= new HashSet<ItemGroup>();
    	Set<Item> noDuplicateItems = new HashSet<Item>();
    	Set<ItemGroup> noDuplicateItemGroups = new HashSet<ItemGroup>();
    	for(CustomReportApplicablePart custRepAppPart :customReport.getApplicableParts()){
    		if(custRepAppPart != null && custRepAppPart.getItemCriterion().getItem()!=null){
    			if(!noDuplicateItems.add(custRepAppPart.getItemCriterion().getItem())){
    				addActionError("error.customReport.hasDuplicateApplicableParts");
    				return false;
    			}
    		}else if(custRepAppPart!=null){
    			if(!noDuplicateItemGroups.add(custRepAppPart.getItemCriterion().getItemGroup())){
    				addActionError("error.customReport.hasDuplicateApplicableParts");
    				return false;
    			}
    				
    		}
    	}
    	
    	for(ItemGroup itemGroup:customReport.getForItemGroups()){
    		if(!noDuplicateListProductModels.add(itemGroup)){
    			addActionError("error.customReport.hasDuplicateProductModel");
    			return false;
    		}
    	
    	}
    	return true;
    	
    }
	
	
	private void checkIfReportIsDuplicate() {
		if(customReport.getReportType().getCode().equalsIgnoreCase("FAILURE REPORT")){//?is this the standard metd?
			checkForConflictingApplicablePartsInExistingReport();
		
		}else{
			checkForConflictingProductsModelsInExistingReport();
		}
	}
	
	private void checkForConflictingProductsModelsInExistingReport() {
		List<CustomReport> existingCustomReports = customReportService.findConflictingReports(customReport
				.getForInventoryTypes(), Boolean.TRUE, customReport.getReportType(),customReport.getForItemGroups());
		for (CustomReport existingCustomReport : existingCustomReports) {
			if ((customReport.getId() != null && !customReport.equals(existingCustomReport))
					|| customReport.getId() == null) {
				conflictingProdModForReport = customReportService.findConflictingProductsModelsInReports(customReport,
						existingCustomReport);
				if (conflictingProdModForReport.size() > 0) {
					addActionError("error.customReport.reportExistsForProducts", new String[] {
							existingCustomReport.getName(), customReport.getReportType().getCode() });
					for (ItemGroup itemGroup : conflictingProdModForReport) {
						addActionError(itemGroup.getName());
					}
					this.conflictingProdModForReport.clear();
				}

			}
			if (hasActionErrors()) {
				break;// One report at a time.
			}
		}
	}
	
	private void checkForConflictingApplicablePartsInExistingReport() {
		List<CustomReport> existingCustomReports = customReportService.findConflictingReports(customReport
				.getForInventoryTypes(), Boolean.TRUE, customReport.getReportType(), customReport.getForItemGroups());
		List<CustomReport> conflictingCustomReports = new ArrayList<CustomReport>();
		for (CustomReport existingCustomReport : existingCustomReports) {
			/*
			 * The custom report should not be validated against itself if it is already 
			 * published.
			 */
			if ((customReport.getId() != null && !customReport.equals(existingCustomReport))
					|| customReport.getId() == null) {
				/*
				 * (If forItemgroups() is empty - report is defined for Stand
				 * alone part claims .
				 */
				if (CollectionUtils.isEmpty(customReport.getForItemGroups())) {
					conflictingCustomReports.add(existingCustomReport);
				} else {
					conflictingProdModForReport = customReportService.findConflictingProductsModelsInReports(
							customReport, existingCustomReport);
					if (conflictingProdModForReport.size() > 0) {
						conflictingCustomReports.add(existingCustomReport);
					}
				}
			}
		}
		if (conflictingCustomReports.size() > 0) {
			conflictingCustomReports=customReportService.findReportsWithConflictingParts(customReport,conflictingCustomReports);
		}
		if(!CollectionUtils.isEmpty(conflictingCustomReports)){
			addActionError("error.customReport.reportExistsForCustomReports");
			for(CustomReport custReport : conflictingCustomReports){
				addActionError(custReport.getName());
			}
		}
	}
	

	public boolean isSectionValidationRequired() {
		boolean isSectionValidationRequired = true;
		if (customReport != null && customReport.getSections() != null) {
			if (customReport.getPublished() && !(customReport.getSections().size() > 0))
				isSectionValidationRequired = false;

		}
		return isSectionValidationRequired;
	}
	
	public boolean areDocumentsImages() {
		if (questionnaire != null) {
			if (!isInstructionDocumentImage(questionnaire.getPostInstructions())
					|| !isInstructionDocumentImage(questionnaire.getPreInstructions())) {
				return false;
			}
		}
		return true;
	}

	 private boolean isInstructionDocumentImage(CustomReportInstructions customReportInstruction){
		 if(customReportInstruction !=null){
	    		return customReportInstruction.isDocumentImage();
	    }
		 return true;
	 }
	    	
	
	//Action methods.
	
	
	public String saveCustomReport() {
		if (hasActionErrors() || hasFieldErrors()) {
			return INPUT;	
		} else {
			return saveReportSuccessfully();
		}
	}
	
	public String publishReport(){
		if (hasActionErrors()  || hasFieldErrors()) {
			customReport.setPublished(false);
			return INPUT;
		}else{
			if(!sectionsHaveQuestions()){
				addActionError("error.customReport.sectionsRequired");
				return INPUT;
			}else{
				return saveReportSuccessfully();
			}
		}
	}
    
	public String saveReportSuccessfully() {
		try {
			int i = 0;
			for (CustomReportApplicablePart appPart : getCustomReport().getApplicableParts()) {
				if (appPart != null && appPart.isItemCriterionItemGroup()) {
					appPart.getItemCriterion().setItem(null);
				} else {
					appPart.getItemCriterion().setItemGroup(null);
				}
				i++;
			}
			if (CollectionUtils.isEmpty(getCustomReport().getForItemGroups())) {
				addActionMessage("label.customReport.sectionCreated.forStandAlonePartClaim");
			}
			if (getCustomReport().getId() == null) {
				addActionMessage("label.customReport.reportCreated", getCustomReport().getName());
				customReportService.save(getCustomReport());
			} else {
				if (taskName.equals("Publish")) {
					addActionMessage("label.customReport.publishedSuccessfully", getCustomReport().getName());

				} else {
					addActionMessage("label.customReport.updated", getCustomReport().getName());
				}
				customReportService.update(getCustomReport());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error while saving report [ " + getCustomReport().getName() + " ]");
		}
		return SUCCESS;
	}
	
	public String addQuestionToSection() {
		if (getQuestionnaire() != null && getSection().getQuestionnaire() != null) {
			if (getQuestionnaire().getId() != null) {
				customReportService.updateReportFormQuestion(getQuestionnaire());
				addActionMessage("label.customReport.questionUpdated", getSection().getName());
			} else {
				customReportService.createReportFormQuestion(getSection(), getQuestionnaire());
				if (isAddNextQuestion()) {
					addActionMessage("label.addedQuestionsToSection.addNextQuestion", new String[] { getSection()
							.getName() });
					setQuestionnaire(null);
				} else {
					addActionMessage("label.addedQuestionsToSection.exit", new String[] { getSection().getName() });
				}
			}

			return SUCCESS;
		} else {
			addActionError("label.section.error.request");
			return INPUT;
		}

	}
	
	public boolean publishButtontoBeDisplayed() {
		return (customReport == null
				|| (taskName != null && taskName.equalsIgnoreCase("Publish") && (hasFieldErrors() || hasActionErrors())) || !customReport
				.getPublished());
	}
	
	public boolean sectionsHaveQuestions(){
		for(ReportSection section : customReport.getSections()){
			if(CollectionUtils.isEmpty(section.getQuestionnaire())){
				return false;
			}
		}
		return true;
	}
	
	public String updateQuestionOrder() {
		try {
			String[] sectionCount = request.getParameterValues("sectionCount");
			int index = 0;
			boolean fail = false;
			for (ReportSection reportSection:customReport.getSections()) {
				if (reportSection.getQuestionnaire().size() != Integer.parseInt(sectionCount[index++])) {
					fail = true;
					break;
				}
			}			
			if (!fail) {
				customReportService.save(customReport);
				addActionMessage("label.questions.reprioritized.successfully",new String[]{customReport.getName()});
			} else {
				addActionError("error.questions.reordered.fail");
				return INPUT;
			}			
		} catch (Exception e) {
			addActionError("error.inPrioritizingQuestions");
			logger.debug("exception whiel updating question order for report [ " + customReport.getName() + " ] ");
		}
		return SUCCESS;
	}
    
  	
	public String viewAllQuestions(){
		return SUCCESS;
	}
	
	public String addQuestion(){
		return SUCCESS;
	}
	
	public Map<ReportFormAnswerTypes,String> getAnswerTypes(){
	     this.answerTypes=new TreeMap<ReportFormAnswerTypes, String>();
	  	 this.answerTypes.put(ReportFormAnswerTypes.SMALL_TEXT,ReportFormAnswerTypes.SMALL_TEXT.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.LARGE_TEXT,ReportFormAnswerTypes.LARGE_TEXT.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.NUMBER,ReportFormAnswerTypes.NUMBER.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.SINGLE_SELECT,ReportFormAnswerTypes.SINGLE_SELECT.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.MULTI_SELECT,ReportFormAnswerTypes.MULTI_SELECT.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.MULTI_SELECT_LIST,ReportFormAnswerTypes.MULTI_SELECT_LIST.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.DATE,ReportFormAnswerTypes.DATE.getType());
	  	 this.answerTypes.put(ReportFormAnswerTypes.SINGLE_SELECT_LIST,ReportFormAnswerTypes.SINGLE_SELECT_LIST.getType());
	  	  return this.answerTypes;
	    }
	
  /*getters and setters */
    public ReportSection getSection() {
        return section;
    }

    public void setSection(ReportSection section) {
        this.section = section;
    }

    public void setCustomReportService(CustomReportService customReportService) {
        this.customReportService = customReportService;
    }

    public CustomReport getCustomReport() {
        return customReport;
    }

    public void setCustomReport(CustomReport customReport) {
        this.customReport = customReport;
    }

    public ReportFormQuestion getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(ReportFormQuestion questionnaire) {
        this.questionnaire = questionnaire;
    }
    
    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }


    @Required
    public void setProductLocaleService(ProductLocaleService productLocaleService) {
        this.productLocaleService = productLocaleService;
    }

    public List<ProductLocale> getLocales() {
        return locales;
    }

    public void setLocales(List<ProductLocale> locales) {
        this.locales = locales;
    }

	public List<InventoryType> getInventoryTypes() {
		return inventoryTypes;
	}

	public void setInventoryTypes(List<InventoryType> inventoryTypes) {
		this.inventoryTypes = inventoryTypes;
	}

	public Boolean getOverWriteReports() {
		return overWriteReports;
	}

	public void setOverWriteReports(Boolean overWriteReports) {
		this.overWriteReports = overWriteReports;
	}

	public CustomReportService getCustomReportService() {
		return customReportService;
	}


	public List<ItemGroup> getConflictingProdModForReport() {
		return conflictingProdModForReport;
	}

	public void setConflictingProdModForReport(List<ItemGroup> conflictingProdModForReport) {
		this.conflictingProdModForReport = conflictingProdModForReport;
	}

	public List<CustomReportApplicablePart> getConflictingAppParts() {
		return conflictingAppParts;
	}

	public void setConflictingAppParts(List<CustomReportApplicablePart> conflictingAppParts) {
		this.conflictingAppParts = conflictingAppParts;
	}

	public List<String> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(List<String> productTypes) {
        this.productTypes = productTypes;
    }

    
    public List<Applicability> getApplicabilities() {
		return applicabilities;
	}

	public void setApplicabilities(List<Applicability> applicabilities) {
		this.applicabilities = applicabilities;
	}

	public List<CustomReportApplicablePart> getApplicableParts() {
        return applicableParts;
    }

    public void setApplicableParts(List<CustomReportApplicablePart> applicableParts) {
        this.applicableParts = applicableParts;
    }

    public void addApplicableParts(CustomReportApplicablePart applicablePart) {
        this.applicableParts.add(applicablePart);
    }


	public CatalogService getCatalogService() {
		return catalogService;
	}


	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}


	public String getForItemGroupId() {
		return forItemGroupId;
	}


	public void setForItemGroupId(String forItemGroupId) {
		this.forItemGroupId = forItemGroupId;
	}



	public String getApplicablePartItemId() {
		return applicablePartItemId;
	}


	public void setApplicablePartItemId(String applicablePartItemId) {
		this.applicablePartItemId = applicablePartItemId;
	}


	public String getApplicablePartItemGroupId() {
		return applicablePartItemGroupId;
	}


	public void setApplicablePartItemGroupId(String applicablePartItemGroupId) {
		this.applicablePartItemGroupId = applicablePartItemGroupId;
	}
	
	public Boolean isAddNextQuestion() {
		return addNextQuestion;
	}

	public void setAddNextQuestion(Boolean addNextQuestion) {
		this.addNextQuestion = addNextQuestion;
	}


	public String getTaskName() {
		return taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	
	
	 public String setProductsAndModels(){
	     return SUCCESS;
	 }
	 
	 
	 

	public boolean isValidItemGroups() {
		return validItemGroups;
	}

	public void setValidItemGroups(boolean validItemGroups) {
		this.validItemGroups = validItemGroups;
	}

	public boolean isValidInventoryTypes() {
		return validInventoryTypes;
	}

	public void setValidInventoryTypes(boolean validInventoryTypes) {
		this.validInventoryTypes = validInventoryTypes;
	}

	public ItemGroupRepository getItemGroupRepository() {
		return itemGroupRepository;
	}

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public Boolean getAddNextQuestion() {
		return addNextQuestion;
	}

	public static Logger getLogger() {
		return logger;
	}

	public void setAnswerTypes(Map<ReportFormAnswerTypes, String> answerTypes) {
		this.answerTypes = answerTypes;
	}

	// not used currently.but mite be required later on.so have not removed
	// these.
	private boolean validateQuestionOrderDuplicacy(ReportSection section, ReportFormQuestion question) {
		return validateForOrders();
	}
 
	private boolean validateForOrders() {
		if (section != null && section.getQuestionnaire() != null) {
			Map<Integer, ReportFormQuestion> questions = new HashMap<Integer, ReportFormQuestion>();
			for (ReportFormQuestion eachQuestion : section.getQuestionnaire()) {
				if (eachQuestion.getOrder() < 0 || eachQuestion.getOrder() > section.getQuestionnaire().size()) {
					addActionError("error.customReport.QuestionsForOrderMissing", section.getName());
					return false;
				}
				if (questions.get(eachQuestion.getOrder()) != null) {
					addActionError("label.customReport.duplicateQuestionOrder");
					return false;
				} else {
					questions.put(eachQuestion.getOrder(), eachQuestion);
				}
			}
		}
		return true;
	}

	public String displayInternationalize() {
		locales = productLocaleService.findAll();
		return SUCCESS;
	}


	}
