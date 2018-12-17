package tavant.twms.domain.customReports;

import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.ListCriteria;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.claim.Claim;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.collections.CollectionUtils;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 2:29:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomReportServiceImpl extends GenericServiceImpl<CustomReport, Long, Exception> implements
        CustomReportService {
	
	protected static Logger logger = LogManager.getLogger(CustomReportServiceImpl.class);
	
    private CustomReportRepository customReportRepository;
    
    private ItemGroupService itemGroupService;
    
    public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public CustomReportRepository getCustomReportRepository() {
        return customReportRepository;
    }

    public void setCustomReportRepository(CustomReportRepository customReportRepository) {
        this.customReportRepository = customReportRepository;
    }

    @Override
    public GenericRepository<CustomReport, Long> getRepository() {
        return customReportRepository;
    }

    public ReportSection createReportSection(ReportSection section) {
        return customReportRepository.createReportSection(section);
    }

    public ReportSection updateReportSection(ReportSection section) {
        return customReportRepository.updateReportSection(section);
    }

    public ReportFormQuestion createReportFormQuestion(ReportFormQuestion formQuestion) {
        return customReportRepository.createReportFormQuestion(formQuestion);
    }

    public ReportFormQuestion createReportFormQuestion(ReportSection reportSection,
                                    ReportFormQuestion formQuestion) {
        createReportFormQuestion(formQuestion);
        formQuestion.setForSection(reportSection);
        prepareAnswerValueForOtherOption(formQuestion);
        reportSection.getQuestionnaire().add(formQuestion);
        updateReportSection(reportSection);
        return formQuestion;
    }
    
	@SuppressWarnings("unchecked")
	private void prepareAnswerValueForOtherOption(
			ReportFormQuestion formQuestion) {
		if (formQuestion.isIncludeOtherAsAnOption()) {
			boolean alreadyExists = false;
			for (ReportFormAnswerOption answer : formQuestion
					.getAnswerOptions()) {
				if (answer.isOtherOption()) {
					alreadyExists = true;
				}
			}
			if (!alreadyExists) {
				formQuestion.getAnswerOptions().add(
						createAnswerOptionForOther(formQuestion));
			}

		} else {
			Iterator it = formQuestion.getAnswerOptions().iterator();
			while (it.hasNext()) {
				if (((ReportFormAnswerOption) it.next()).isOtherOption()) {
					it.remove();
				}
			}
		}
	}
    
    private ReportFormAnswerOption createAnswerOptionForOther(ReportFormQuestion formQuestion){
    	ReportFormAnswerOption ansOption= new ReportFormAnswerOption();
    	ansOption.setAnswerOption("Other");
    	ansOption.setOtherOption(true);
    	if(formQuestion.getAnswerOptions()!=null){
    		ansOption.setOrder(formQuestion.getAnswerOptions().size()+1);
    	}
    	return ansOption;
    }

    public PageResult<CustomReport> findReports(ListCriteria criteria) {
        return customReportRepository.findReports(criteria);
    }

    public ReportFormQuestion updateReportFormQuestion(ReportFormQuestion formQuestion) {
    	prepareAnswerValueForOtherOption(formQuestion);
        return customReportRepository.updateReportFormQuestion(formQuestion);
    }

    public CustomReportAnswer createCustomReportAmswer(CustomReportAnswer reportAnswer){
        return customReportRepository.createCustomReportAmswer(reportAnswer);
    }

    /*
     * This method returns all the applicable reports.
     * If an Commission or ACR report is already answered,then the applicable reports
     * ACR or Commission are removed and the answered report is added to the list 
     * */
    public List<CustomReport> findReportsForInventory(InventoryItem inventoryItem) {
    	try
    	{
	    	List<CustomReport> applicableReports = customReportRepository.findReportsForInventory(inventoryItem);
	    	List<CustomReport> applicableReportsTempForRemoval = new ArrayList<CustomReport>(applicableReports);
	    	for(CustomReportAnswer reportAnswer:inventoryItem.getReportAnswers()){
	    		  if(("COMMISSION").equalsIgnoreCase(reportAnswer.getCustomReport().getReportType().getCode())
	        				|| ("ACR").equalsIgnoreCase(reportAnswer.getCustomReport().getReportType().getCode())){
	        			for(CustomReport report:applicableReportsTempForRemoval){
	        				if(reportAnswer.getCustomReport().getReportType().getCode().equalsIgnoreCase(
	        						report.getReportType().getCode())){
	        					applicableReports.remove(report);
	        				}        				
	        			}
	        		}
	    		  if(!(applicableReports.contains(reportAnswer.getCustomReport()))){
	    		 	  applicableReports.add(reportAnswer.getCustomReport());
	    		  }
	    		  
	    	}
			for (Iterator<CustomReport> applicableReport = applicableReports
					.iterator(); applicableReport.hasNext();)

			{
				CustomReport applicableCustomReport = applicableReport.next();
				if (("FAILURE REPORT").equalsIgnoreCase(applicableCustomReport
						.getReportType().getCode())) {
					applicableReport.remove();
				}
			}
	    
	    	return applicableReports;
    	}
    	catch (Exception e)	{
    		logger.error("Encountered an exception while fetching reports for inventory ",e);
    	}
    	return null;
    }

	public CustomReportAnswer updateCustomReportAnswer(CustomReportAnswer reportAnswer) {
		return customReportRepository.updateCustomReportAnswer(reportAnswer);
	}
	
	public List<CustomReport> findPublishedReportsForProducts(List<ItemGroup> itemGroups,List<InventoryType> inventoryTypes,Boolean published, ReportType reportType){
		List<Long> itemGroupIds= new ArrayList<Long>();
		for(ItemGroup itemGroup :itemGroups){
			itemGroupIds.add(itemGroup.getId());
		}
		List<String> inventoryTypeNames= new ArrayList<String>();
		for(InventoryType inventoryType :inventoryTypes){
			inventoryTypeNames.add(inventoryType.getType());
		}	
		
		return customReportRepository.findPublishedReportsForProducts(itemGroupIds,inventoryTypeNames,published, reportType);
	}
	
	//To find duplicate custom reports for reports of type other than failure reports
	public List<CustomReport> findConflictingReports(List<InventoryType> inventoryTypes,Boolean published, ReportType reportType,List<ItemGroup> forItemGroups){
		
		List<String> inventoryTypeNames= new ArrayList<String>();
		if(!CollectionUtils.isEmpty(inventoryTypes)){
			for(InventoryType inventoryType :inventoryTypes){
				inventoryTypeNames.add(inventoryType.getType());
			}
		}
		if(CollectionUtils.isEmpty(forItemGroups)){
			return customReportRepository. findConflictingReportsForStandAlonePartsClaim(published,reportType);
		}else{
			return customReportRepository.findConflictingReports(inventoryTypeNames,published,reportType);
		}
	}

    public List<CustomReport> findReportsForParts(Collection<Item> items, Claim claim){
        return customReportRepository.findReportsForParts(items,claim);
    }
	
    //To find duplicate reports for reports of type other than "failure reports"
	public List<ItemGroup> findConflictingProductsModelsInReports(CustomReport userDefinedReport,CustomReport existingReport){
		List<ItemGroup> conflictingProdModForReport = new ArrayList<ItemGroup>();
		for (ItemGroup itemGroupUserDefined : userDefinedReport.getForItemGroups()) {
			for (ItemGroup itemGroupExisting : existingReport.getForItemGroups()) {
				conflictingProdModForReport = isUDItemGroupSame(itemGroupUserDefined, itemGroupExisting,conflictingProdModForReport);
				conflictingProdModForReport = isUserDefinedExistingItemGroupRelated(itemGroupUserDefined, itemGroupExisting,conflictingProdModForReport);
				conflictingProdModForReport=  isUserDefinedExistingItemGroupRelated(itemGroupExisting, itemGroupUserDefined,conflictingProdModForReport); 
			}
		}
		return conflictingProdModForReport;
	}

	private List<ItemGroup> isUDItemGroupSame(ItemGroup UDItemGroup, ItemGroup existingItemGroup,
			List<ItemGroup> conflictingProdModForReport) {
		if (UDItemGroup.equals(existingItemGroup)) {
			conflictingProdModForReport.add(existingItemGroup);
		}
		return conflictingProdModForReport; 
	}
	
	
	private List<ItemGroup> isUserDefinedExistingItemGroupRelated(ItemGroup product, ItemGroup model,List<ItemGroup> conflictingProdModForReport) {
		if (product.getItemGroupType().equalsIgnoreCase("PRODUCT")
				&& model.getItemGroupType().equalsIgnoreCase("MODEL")) {
			List<ItemGroup> models = itemGroupService.findModelsForProduct(product);
			for (ItemGroup modelIg : models) {
				if (modelIg.equals(model)) {
					conflictingProdModForReport.add(model);
				}
			}
		}
		return conflictingProdModForReport;
	}
     
	//To find duplicate reports for reports of type "failure reports"
	
	  public List<CustomReport> findReportsWithConflictingParts(CustomReport customReport,
			  List<CustomReport> conflictingCustomReports){
		  Map<Item,List<Applicability>> items=new HashMap<Item,List<Applicability>>();
		  Map<ItemGroup,List<Applicability>> itemGroups = new HashMap<ItemGroup,List<Applicability>>();
		  List<CustomReport> customReportswithDuplicateParts=new ArrayList<CustomReport>();
		  List<CustomReportResultSet> duplicateCustomReportResultSet=new ArrayList<CustomReportResultSet>();
		  
		  //Populate items and item groups for query.
		  for(CustomReportApplicablePart custApp : customReport.getApplicableParts()){
			  if(custApp.isItemCriterionItemGroup()){
				  itemGroups.put(custApp.getItemCriterion().getItemGroup(),custApp.getApplicabilityList());
			  }else{
				  items.put(custApp.getItemCriterion().getItem(),custApp.getApplicabilityList());
			  }
		  }
		  
		  /*Check if item groups are conflicting before firing query.
		  (this when integrated into query takes a performance hit)*/
		  if(!CollectionUtils.isEmpty(itemGroups.keySet())){
			  for(CustomReport existingReport : conflictingCustomReports){
				 if(findCustomReportsWithItemGroups(itemGroups,existingReport)){
					 customReportswithDuplicateParts.add(existingReport);
				  }
			  }
		  }
		  //Break here to avoid unnecessary checks.
		  if(customReportswithDuplicateParts.size()>0){
			  return customReportswithDuplicateParts;
		  }
		  //Fire query to see if items are conflicting.
		  if(!CollectionUtils.isEmpty(items.keySet())){
			  duplicateCustomReportResultSet=customReportRepository.findConflictingReportsBasedOnItems(new ArrayList<Item>(items.keySet()), conflictingCustomReports);
		  }
		  //Fire query to see if items in item groups are conflicting.
		 
		  if(CollectionUtils.isEmpty(duplicateCustomReportResultSet) && !CollectionUtils.isEmpty(itemGroups.keySet())){
			  duplicateCustomReportResultSet = customReportRepository.findConflictingReportsBasedOnItemGroups(new ArrayList<ItemGroup>(itemGroups.keySet()), conflictingCustomReports);
		  }
		  
		  return filterBasedOnApplicability(duplicateCustomReportResultSet,items,itemGroups);
	  }
	  
	public boolean isReportNameDuplicate(String name,Long id){
		return this.customReportRepository.isReportNameDuplicate(name,id);
	}
	  
	  
	private boolean findCustomReportsWithItemGroups(Map<ItemGroup, List<Applicability>> itemGroups,
			CustomReport existingReport) {
		for (CustomReportApplicablePart custApp : existingReport.getApplicableParts()) {
			if (custApp.getItemCriterion() != null && custApp.getItemCriterion().isGroupCriterion()) {
				for (ItemGroup itemGroup : itemGroups.keySet()) {
					if (itemGroup.equals(custApp.getItemCriterion().getItemGroup())
							&& applicabililityListSame(custApp, itemGroups.get(itemGroup))) {
						return true;
					}
				}

			}
		}
		return false;
	}
	
	private boolean applicabililityListSame(CustomReportApplicablePart existingAppPart,
			List<Applicability> applicabilityList) {
		for (Applicability UDapplicability : applicabilityList) {
			if (UDapplicability != null && !CollectionUtils.isEmpty(existingAppPart.getApplicabilityList())) {
				if (existingAppPart.getApplicabilityList().contains(UDapplicability))
					return true;
			}
		}
		return false;
	}

	private List<CustomReport> filterBasedOnApplicability(List<CustomReportResultSet> duplicateReports,
			Map<Item, List<Applicability>> items, Map<ItemGroup, List<Applicability>> itemGroups) {
		Set<CustomReport> customReports = new HashSet<CustomReport>();
		for(CustomReportResultSet result : duplicateReports){
			if(result.getItemGroup()!=null){
				if(applicabililityListSame(result.getCustRepAppPart(),itemGroups.get(result.getItemGroup()))){
					customReports.add(result.getCustomReport());
				}
			}else if(result.getItem()!=null){
				if(applicabililityListSame(result.getCustRepAppPart(),items.get(result.getItem()))){
					customReports.add(result.getCustomReport());
				}
			}else if(result.getItemInGroup()!=null){
				if(applicabililityListSame(result.getCustRepAppPart(),items.get(result.getItemInGroup()))){
					customReports.add(result.getCustomReport());
				}
				
			}
		}

		return new ArrayList<CustomReport>(customReports);

	}

	public boolean isPdiReportLinkAvailable(InventoryItem inventoryItem){
		List<CustomReport> customReports = customReportRepository.findReportsForInventory(inventoryItem);
		
		for(Iterator<CustomReport> iter = customReports.iterator();iter.hasNext();){
			if("PDI".equals(iter.next().getReportType().getCode())){
				return true;
			}
		}
		return false;
	}
	
	
}