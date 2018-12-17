package tavant.twms.web.admin.miscellaneousParts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.catalog.MiscItemRate;
import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.catalog.MiscellaneousItemCriteria;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CurrencyExchangeRateService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.domainlanguage.money.Money;
import com.opensymphony.xwork2.Preparable;


@SuppressWarnings("serial")
public class ManageMiscellaneousPartsConfiguration extends SummaryTableAction implements Preparable {

	private static Logger logger = Logger.getLogger(ManageMiscellaneousPartsConfiguration.class);
	
	private MiscellaneousItemConfigService miscellaneousItemConfigService;
		
	private MiscellaneousItemCriteria miscItemCrit;
	
	private List<ItemUOMTypes> uomsList;
	
	private CatalogService catalogService;
	
	private String number;
	
	private DealerGroupService dealerGroupService;	
	
	private DealerGroup persistedDealerGroupValue;
	
	private boolean dealerGroupSelected;
	
	private Boolean deActivateMiscPartConfig;
	
	private CurrencyExchangeRateService currencyExchangeRateService;
    
    private List<Currency> currencyList = new ArrayList<Currency>();
    
    private List<MiscItemRate> miscItems = new ArrayList<MiscItemRate>();
    
    private boolean isServiceProviderNameEntered= false;
	private boolean isDealerGroupNameEntered = false;
	
	
	public void prepare() throws Exception{
		
		currencyList = orgService.listUniqueCurrencies();    	
    	for(Currency currency : currencyList )
		{
			MiscItemRate miscItemRate = new MiscItemRate();
			Money rate = Money.valueOf(new BigDecimal(0.00), currency,2);
			miscItemRate.setRate(rate);
			this.miscItems.add(miscItemRate);
		}
    	
    	if(this.miscItemCrit != null)
    	{
	    	if(this.miscItemCrit.getServiceProvider()!=null && !StringUtils.isBlank(miscItemCrit.getServiceProvider().getName())){
				isServiceProviderNameEntered = true;
			}
			
			if(this.miscItemCrit.getDealerGroup() !=null && !StringUtils.isBlank(miscItemCrit.getDealerGroup().getName())){
				isDealerGroupNameEntered =true;
			}
    	}
    	setupDataForPage();
	}
	
	@Override
	protected PageResult<MiscellaneousItemCriteria> getBody() {
		return miscellaneousItemConfigService.findAllConfigurations(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "Number", "id",
				false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.miscellaneousParts.configName",
				"configName", 40, "String","configName",
				true,false,false,false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.miscellaneousParts.dealerOrDealerGroup",
				"dealerOrDealerGroup", 40, "String",SummaryTableColumnOptions.NO_SORT | SummaryTableColumnOptions.NO_FILTER));
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.status","active", 20,"String", 
				SummaryTableColumnOptions.NO_SORT | SummaryTableColumnOptions.NO_FILTER));
		return tableHeadData;
	}
	
	private void setupDataForPage(){
		this.uomsList = this.catalogService.findAllUoms();
	}
	
	public String showDetail(){
		this.miscItemCrit = this.miscellaneousItemConfigService.findMiscPartConfigById(new Long(getId()));
		if(this.miscItemCrit != null)		{	
			this.deActivateMiscPartConfig = this.miscItemCrit.isActive();
		}
		return SUCCESS;
	}
	
	public String toggleActiveConfiguration()
	{		
		try {			
			validateConfigurationData();
			if(hasActionErrors()){				
				return INPUT;
			}
			if (deActivateMiscPartConfig)
			{
				miscItemCrit.setActive(Boolean.FALSE);
				for (MiscellaneousItemConfiguration element : miscItemCrit
						.getItemConfigs()) {
					element.setActive(false);
				}
			}
			else 
			{
				miscItemCrit.setActive(Boolean.TRUE);
				for (MiscellaneousItemConfiguration element : miscItemCrit
						.getItemConfigs()) {
					element.setActive(true);
				}
			}
			
			updateMiscPartConfiguration(deActivateMiscPartConfig ? Boolean.FALSE : Boolean.TRUE);		
			addActionMessage("message.misc.config.update.successfull");
			return SUCCESS;
		}
		catch (Exception exception)
		{
			logger.debug(exception.getMessage());
			return INPUT;
		}
	}
	
	public String create(){		
		return SUCCESS;
	}
	
	
	public String updateConfiguration() throws Exception {		
		validateConfigurationData();
		if(hasActionErrors()){			
			return INPUT;
		}
		updateMiscPartConfiguration(Boolean.TRUE);		
		addActionMessage("message.misc.config.update.successfull");
		return SUCCESS;
	}

	private void updateMiscPartConfiguration(Boolean isActive) throws Exception {								
	    this.miscellaneousItemConfigService.update(this.miscItemCrit);
	}
	
	public String saveConfiguration() throws Exception {
		validateData();
		if(hasActionErrors()){			
			return INPUT;
		}
		/**
		 * Relevance score logic
		 * Dealer - 2
		 * Dealer Group - 1
		 * All - 0 
		 */
		if(this.miscItemCrit.getServiceProvider() != null){			
			miscItemCrit.setDealerGroup(null);
			miscItemCrit.setRelevanceScore(2);
		}		
		if(this.persistedDealerGroupValue != null ){
			miscItemCrit.setDealerGroup(this.persistedDealerGroupValue);
			miscItemCrit.setServiceProvider(null);
			miscItemCrit.setRelevanceScore(1);
		}
		else{
			miscItemCrit.setDealerGroup(null);			
		}
		miscellaneousItemConfigService.save(miscItemCrit);		
		addActionMessage("message.misc.config.save.successfull");
		return SUCCESS;	
	}	
	
	private void validateData(){
		validateCriteriaData();
		validateConfigurationData();		
	}

	private void validateCriteriaData() {
		if(StringUtils.isBlank(miscItemCrit.getConfigName())){
			addActionError("error.miscCriteria.configName.notInput");
		} else{
			if(miscellaneousItemConfigService.findIfConfigurationWithSameNameExists(miscItemCrit.getConfigName())){
				addActionError("error.miscCriteria.configName.exists");
			}			
		}
	}

	private void validateConfigurationData() {
		
		if(miscItemCrit.getItemConfigs() == null || miscItemCrit.getItemConfigs().size() == 0){
			addActionError("error.miscCriteria.parts.notConfigured");
			return;
		}
		
		boolean isPartNotSpecified =false;
		boolean isQuantityNotSpecified =false;
		boolean isPriceNotSpecified =false;
		boolean isPriceInvalid = false;
		boolean isUomNotSpecified =false;
		boolean isQuantityInvalid =false;
		boolean isPartDuplicated =false;
		List<String> miscPartsList = new ArrayList<String>() ;	
		List<MiscellaneousItem> miscItemForValidation  = new ArrayList<MiscellaneousItem>();
	
		for (MiscellaneousItemConfiguration element : miscItemCrit
				.getItemConfigs()) {
			if(element != null)
			{
				if (element.getMiscellaneousItem() == null || element.getMiscellaneousItem().getPartNumber() == null 
						|| StringUtils.isBlank(element.getMiscellaneousItem().getPartNumber())) {
					isPartNotSpecified = true;
				}	
				else
				{
					 if(miscPartsList.contains(element.getMiscellaneousItem().getPartNumber())){ 
						 isPartDuplicated = true;				 
					 } else{
						 miscPartsList.add(element.getMiscellaneousItem().getPartNumber());
						 if(element.getId() == null)
						 {
							//we need the validation only for newly added elements. 
						 	miscItemForValidation.add(element.getMiscellaneousItem());
						 }	
					 }	
				}
				
				if (element.getMiscItemRates() == null || element.getMiscItemRates().isEmpty()) {
					isPriceNotSpecified = true;
				} 				
				else
				{
					for(MiscItemRate rate:element.getMiscItemRates()){
						if(rate.getRate() == null || rate.getRate().isNegative())
							isPriceInvalid = true;
					}
				}
				
				if (element.getTresholdQuantity() == null) {
					isQuantityNotSpecified = true;
				} else{
					if(element.getTresholdQuantity().longValue() <=0){
						isQuantityInvalid = true;
					}				
				}
				
				if (element.getUom() == null || StringUtils.isBlank(element.getUom().getType())) {
					isUomNotSpecified = true;
				}
			}
		}
		
		if(isPartDuplicated){
			addActionError("error.miscellaneousPart.duplication");			 
		}
		
		if(isPartNotSpecified){
			addActionError("error.miscCriteria.part.notInput");
		}

		if(isQuantityNotSpecified){
			addActionError("error.miscCriteria.quanity.notInput");
		}
		
		if(isPriceNotSpecified){
			addActionError("error.miscCriteria.price.notInput");
		}
				
		if(isUomNotSpecified){
			addActionError("error.miscCriteria.uom.notInput");
		}
		
		if(isQuantityInvalid){
			addActionError("error.miscCriteria.quanity.notValid");
		}
		
		if(isPriceInvalid){
			addActionError("error.miscCriteria.price.notValid");
		}
		
		if(isServiceProviderNameEntered && miscItemForValidation != null && !miscItemForValidation.isEmpty()){
			if(miscellaneousItemConfigService.isDataForServiceProviderConfigured(this.miscItemCrit.getServiceProvider(), miscItemForValidation)){
				addActionError("error.miscCriteria.dealer.config.exists");
			}			
		}
		
		if(isDealerGroupNameEntered){
			this.persistedDealerGroupValue= dealerGroupService.findByNameAndPurpose(miscItemCrit.getDealerGroup().getName(), AdminConstants.DEALER_RATES_PURPOSE);
			if (this.persistedDealerGroupValue == null) {
				addActionError("error.miscPart.invalidDealerGroup");
			} else {
				if(this.persistedDealerGroupValue != null && this.persistedDealerGroupValue.isGroupOfGroups())	{
					addActionError("error.miscPart.dealerGroupOfGroup");
				}
				if(miscItemForValidation != null && !miscItemForValidation.isEmpty()){
					if(miscellaneousItemConfigService.isDataForDealerGroupConfigured(this.persistedDealerGroupValue, miscItemForValidation)){
						addActionError("error.miscCriteria.dealerGroup.config.exists");
					}
				}
			}
		}
		
		if(!isServiceProviderNameEntered && !isDealerGroupNameEntered)
		{
			if(miscItemForValidation != null && !miscItemForValidation.isEmpty()){
				if(miscellaneousItemConfigService.isMiscItemConfiguredForAll(miscItemForValidation))
				{
					addActionError("error.miscCriteria.allDealer.config.exists");
				}
			}
		}
	}

	public String showPreview(){
		this.miscItemCrit = this.miscellaneousItemConfigService.findMiscPartConfigById(Long.parseLong(getId()));		
		return SUCCESS;
	}
		
	
	public BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {
			@Override
			public Object getProperty(String propertyPath, Object root) {
				Object value = null;
				if ("dealerOrDealerGroup".equals(propertyPath)) {
					value = super.getProperty("serviceProvider.name", root);
					if (value == null) {
						value = super.getProperty("dealerGroup.name", root);
						if(value == null){
							value = "All Dealers";
						}
					}
					return value;
				} else if("active".equals(propertyPath)){
					value = super.getProperty("active", root);
					if(value != null && value.toString().equalsIgnoreCase("true")) {
						return "ACTIVE";
					}else{
						return "INACTIVE";
					}
				}else {
					return super.getProperty(propertyPath, root);
				}

			}
		};
	}
	
	
	public String getAlias(){
		return "mic";
	}

	public MiscellaneousItemCriteria getMiscItemCrit() {
		return miscItemCrit;
	}

	public void setMiscItemCrit(MiscellaneousItemCriteria miscItemCrit) {
		this.miscItemCrit = miscItemCrit;
	}


	public List<ItemUOMTypes> getUomsList() {
		return uomsList;
	}

	public void setUomsList(List<ItemUOMTypes> uomsList) {
		this.uomsList = uomsList;
	}

	public boolean isDealerGroupSelected() {
		return dealerGroupSelected;
	}
	
	public void setDealerGroupSelected(boolean dealerGroupSelected) {
		this.dealerGroupSelected = dealerGroupSelected;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}
	
	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public Boolean getDeActivateMiscPartConfig() {
		return deActivateMiscPartConfig;
	}

	public void setDeActivateMiscPartConfig(Boolean deActivateMiscPartConfig) {
		this.deActivateMiscPartConfig = deActivateMiscPartConfig;
	}

	public CurrencyExchangeRateService getCurrencyExchangeRateService() {
		return currencyExchangeRateService;
	}

	public void setCurrencyExchangeRateService(
			CurrencyExchangeRateService currencyExchangeRateService) {
		this.currencyExchangeRateService = currencyExchangeRateService;
	}

	public List<Currency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<Currency> currencyList) {
		this.currencyList = currencyList;
	}

	public List<MiscItemRate> getMiscItems() {
		return miscItems;
	}

	public void setMiscItems(List<MiscItemRate> miscItems) {
		this.miscItems = miscItems;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMiscPartDescription() throws JSONException {
		JSONArray details=new JSONArray();       
        MiscellaneousItem miscItem = this.miscellaneousItemConfigService.findMiscellaneousItemByPartNumber(number);
		details.put(miscItem.getDescription());		
		jsonString = details.toString();
		return SUCCESS;
	}
	
	
}
