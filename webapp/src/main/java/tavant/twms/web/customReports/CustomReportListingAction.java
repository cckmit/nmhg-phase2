package tavant.twms.web.customReports;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.ActionContext;

import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.customReports.*;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.infra.i18n.ProductLocale;

import java.util.*;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
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
 *///Need to move the listing methods into a different action class.
public class CustomReportListingAction extends SummaryTableAction implements Preparable{
	//Fields
    private ItemGroupRepository itemGroupRepository;
    private CustomReportService customReportService;
    private ProductLocaleService productLocaleService;
	private ConfigParamService configParamService;

	private List<ProductLocale> locales;
    private List<InventoryType> inventoryTypes = new ArrayList<InventoryType>();
    private List<Applicability>  applicabilities = new ArrayList<Applicability>();
    private CatalogService catalogService;
    private CustomReport customReport;
    private ReportSection section;
    private String forItemGroupId;
    private String applicablePartItemId;
    private String applicablePartItemGroupId;
    private String taskName;
    private static final Logger logger = Logger.getLogger(CustomReportListingAction.class);
	
	//Prepare method
    public void prepare() throws Exception {
    	inventoryTypes.add(InventoryType.RETAIL);
    	inventoryTypes.add(InventoryType.STOCK);
    	applicabilities.add(Applicability.CAUSAL);
    	applicabilities.add(Applicability.INSTALLED);
    	applicabilities.add(Applicability.REMOVED);

    	}
      	
	 
	    /* Summary table,listing methods */
    
    
    @Override
    protected PageResult<?> getBody() {
        return customReportService.findReports(getCriteria());
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
        header.add(new SummaryTableColumn("columnTitle.newClaim.task", "id", 0,
                "String", "id", false, true, true, false));
        header.add(new SummaryTableColumn("columnTitle.manageReportAction.reportName",
                "name", 30, "String", "name", true,
                false, false, true));
        header.add(new SummaryTableColumn("columnTitle.manageReportAction.reporttype",
                "reportType.code", 70, "String", "reportType.code", false,
                false, false, true));
        return header;
    }

    public String listOfProductsAndModels(){
       List<String> itemGroupTypes = new ArrayList<String>();
       String listKeyToDisplay="";
       itemGroupTypes.add("PRODUCT");
		if (displayModelProductInFailureHeirarchy()) {
			itemGroupTypes.add("MODEL");
		}
		if(isBuConfigAMER()){
			listKeyToDisplay = "groupCode";
		}else{
			listKeyToDisplay = "itemGroupDescription";
		}
       PageSpecification pageSpecification =  new PageSpecification(0, 10);
       SelectedBusinessUnitsHolder.setSelectedBusinessUnit(new SecurityHelper().getWarrantyAdminBusinessUnit());
       try {
			List<ItemGroup> itemGroups = itemGroupRepository.listAllProductsAndModelsMatchingName(getSearchPrefix(), itemGroupTypes,pageSpecification);
			return generateAndWriteComboboxJson(itemGroups,"id",listKeyToDisplay);
		} catch (Exception e) {
			throw new RuntimeException("Error while generating JSON", e);
		}
    	
    }
    
    public String getItemGroupDetails() throws JSONException {
		JSONArray details;
		try {
			details = new JSONArray();
			if (StringUtils.hasText(forItemGroupId)) {
					ItemGroup itemGroup = this.itemGroupRepository.findById(new Long(forItemGroupId));
					details.put(itemGroup.getItemGroupType());
					details.put(itemGroup.getDescription());
			} else if(StringUtils.hasText(applicablePartItemGroupId)){
				ItemGroup itemGroup = this.itemGroupRepository.findById(new Long(applicablePartItemGroupId));
				details.put(itemGroup.getDescription());
			}else if (StringUtils.hasText(applicablePartItemId)){
				Item item = this.catalogService.findById(new Long(applicablePartItemId));
				details.put(item.getDescription());
			}else{
				details.put("----");
			}
		} catch (Exception e) {
			details = new JSONArray();
			details.put("----");
		}
		jsonString = details.toString();
		return SUCCESS;
	}
    

	public String fetchNewPage(){
		return SUCCESS;
	}
	
	public String detail() {
		 customReport = customReportService.findById(new Long(id));
		 prepareApplicablePartsGroupFlag();
		 return SUCCESS;
	}
	
	public boolean internationalizeButtontoBeDisplayed() {
		return getCustomReport().getPublished();
	}
	
	private void prepareApplicablePartsGroupFlag() {
		if (customReport != null && customReport.getApplicableParts() != null) {
			for (CustomReportApplicablePart custApp : customReport.getApplicableParts()) {
				if (custApp != null && custApp.getItemCriterion() != null) {
					custApp.setItemCriterionItemGroup(custApp.getItemCriterion().isGroupCriterion());
				}
			}
		}
	}

	public boolean publishButtontoBeDisplayed() {
		return (customReport == null || !customReport.getPublished());
	}

	// not used currently.but mite be required later on.so have not removed
	// these.

	public String displayInternationalize() {
		locales = productLocaleService.findAll();
		return SUCCESS;
	}
	

	public String updateCustomReport()
	{
		 try {
 	if( customReport.getPublished()){
	        		 customReportService.update(getCustomReport());
	        	}

	        } catch (Exception e) {
	            logger.error("Unable to save internationalized report", e);
	        }
        	addActionMessage("label.customReport.savedSuccessfully", getCustomReport().getName());
	        return SUCCESS;
	}
	
	public String preview() {
		customReport = customReportService.findById(new Long(id));
		return SUCCESS;
	}

	public String showNewSection() {
		return SUCCESS;
	}
	
	   
	  /*getters and setters */
	   

	public ItemGroupRepository getItemGroupRepository() {
		return itemGroupRepository;
	}


	public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
		this.itemGroupRepository = itemGroupRepository;
	}


	public CustomReportService getCustomReportService() {
		return customReportService;
	}


	public void setCustomReportService(CustomReportService customReportService) {
		this.customReportService = customReportService;
	}


	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}


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


	public List<Applicability> getApplicabilities() {
		return applicabilities;
	}


	public void setApplicabilities(List<Applicability> applicabilities) {
		this.applicabilities = applicabilities;
	}


	public CatalogService getCatalogService() {
		return catalogService;
	}


	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}


	public CustomReport getCustomReport() {
		return customReport;
	}


	public void setCustomReport(CustomReport customReport) {
		this.customReport = customReport;
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


	public ReportSection getSection() {
		return section;
	}


	public void setSection(ReportSection section) {
		this.section = section;
	}


	public String getTaskName() {
		return taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
    public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
    public ConfigParamService getConfigParamService() {
		return configParamService;
	}
	
	public boolean displayModelProductInFailureHeirarchy() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_MODEL_PRODUCT_IN_SERIES_FAILURE_HEIRARCHY_PAGE.getName());
	}
	
}
