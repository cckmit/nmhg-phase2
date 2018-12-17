package tavant.twms.web.admin.supplier;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.domain.supplier.SupplierItemLocationService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.actions.AbstractGridAction;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("serial")
public class ManageSuppliers extends AbstractGridAction {
	private static Logger logger = LogManager.getLogger( ManageSuppliers.class);
	private SupplierService supplierService;
	private ContractService contractService;
	private ItemMappingService itemMappingService;
	private BusinessUnitService businessUnitService;
	private SupplierItemLocationService supplierItemLocationService;
	private Supplier supplier;
	private String inputCurrency;

	private String id;

	private String number;
	
	private String removedItems;
	
	private String jsonString;

	private List<Contract> supplierContracts;

	private CatalogService catalogService;
	
    private List<Location> allLocations;  

	private List<Long> itemLocations = new ArrayList<Long>();

	private List<ItemMapping> itemMappings = new ArrayList<ItemMapping>();
 
	private List<SupplierItemLocation> supplierItems = new ArrayList<SupplierItemLocation>();
	
	private SupplierItemLocation supplierItemLocation ;
	
	private boolean validateDialog;
	
	private boolean hideRemoveButton;
	

	
	public boolean isHideRemoveButton() {
		return hideRemoveButton;
	}

	public void setHideRemoveButton(boolean hideRemoveButton) {
		this.hideRemoveButton = hideRemoveButton;
	}

	public String getRemovedItems() {
		return removedItems;
	}

	public void setRemovedItems(String removedItems) {
		this.removedItems = removedItems;
	}

	public SupplierItemLocation getSupplierItemLocation() {
		return supplierItemLocation;
	}

	public void setSupplierItemLocation(SupplierItemLocation supplierItemLocation) {
		this.supplierItemLocation = supplierItemLocation;
	}

	public boolean isValidateDialog() {
		return validateDialog;
	}

	public void setValidateDialog(boolean validateDialog) {
		this.validateDialog = validateDialog;
	}

	public SupplierService getSupplierService() {
		return supplierService;
	}

	public ItemMappingService getItemMappingService() {
		return itemMappingService;
	}

	public List<SupplierItemLocation> getSupplierItems() {
		return supplierItems;
	}

	public void setSupplierItems(List<SupplierItemLocation> supplierItems) {
		this.supplierItems = supplierItems;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	public void setSelectedBusinessUnitOnSupplier(Supplier supplier) {
		String selectedBU = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
		BusinessUnit businessUnit = businessUnitService.findBusinessUnit(selectedBU);
		TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
		businessUnitMapping.add(businessUnit);
		supplier.setBusinessUnits(businessUnitMapping);
	}

	public String submitSupplier() throws CatalogException {
		if (supplier == null) {
			return INPUT;
		}

		boolean isNewSupplier = supplier.getId() == null;
		if (!hasActionWarnings()) {
			populateLocations();
			supplier.setStatus(STATUS_ACTIVE);
			if (isNewSupplier) {
				supplier.setPreferredCurrency(Currency.getInstance(getInputCurrency().toUpperCase()));
				setSelectedBusinessUnitOnSupplier(supplier);
				supplierService.save(supplier);
				addActionMessage("message.contractAdmin.supplierCreated", new String[] { supplier.getSupplierNumber()
						.toString() });
			} else {
				supplierService.update(supplier);
				if(SelectedBusinessUnitsHolder.getSelectedBusinessUnit() != null)
					SelectedBusinessUnitsHolder.setSelectedBusinessUnit(SelectedBusinessUnitsHolder.getSelectedBusinessUnit());
				addActionMessage("message.contractAdmin.supplierUpdated", new String[]{supplier.getSupplierNumber()
						.toString()});
			}
			return SUCCESS;
		} else
			return INPUT;
	}

	public String deleteSupplier() throws CatalogException {
		if (supplier == null) {
			return INPUT;
		}
		supplierService.delete(supplier);
		for (ItemMapping itemMapping : itemMappings) {
			Item supplierItem = itemMapping.getToItem();
			catalogService.deleteItem(supplierItem);
			itemMappingService.delete(itemMapping);
		}
		addActionMessage("message.contractAdmin.supplierDeleted", new String[] { supplier.getSupplierNumber()
				.toString() });

		return SUCCESS;
	}

	public String listItems(){
		try {
			List<Item> items = new ArrayList<Item>();
			if (StringUtils.hasText(getSearchPrefix())) {
				items = catalogService.findParts(getSearchPrefix().toUpperCase());
			}
			return generateAndWriteComboboxJson(items,"id","number");
		} catch (Exception e) {
			throw new RuntimeException("Error while generating JSON", e);
		}
	}

	public String getOEMPartDetails() {
		try {
			Item item = catalogService.findItemOwnedByManuf(number);
			JSONArray oneEntry = new JSONArray();
			oneEntry.put(item.getDescription());
			jsonString = oneEntry.toString();
		} catch (CatalogException e) {
			throw new RuntimeException("Error while generating JSON", e);
		}
		return SUCCESS;
	}

	private void populateLocations() {
		boolean isexistedLocation=false;
		Location location = new Location();
		location.setAddress(supplier.getAddress());
		/**
		 * change to mock city + sitenumber for supplier
		 */
		location.setCode(supplier.getAddress().getCity()+"-"+supplier.getAddress().getLocation());
		for(Location existedLocation:supplier.getLocations()){
			if(existedLocation.getCode()!= null && existedLocation.getCode().equals(supplier.getAddress().getLocation())){
				isexistedLocation=true;
				break;
			}   			 
		}
		if(!isexistedLocation){
			supplier.addSupplierLocation(supplier.getPreferredLocationType(), location);
		}
	}

	public String preview() {
		supplier = supplierService.findById(Long.parseLong(id));
		if(supplier != null) {
			supplierItems =  supplierItemLocationService.findSupplierItems(supplier);
			supplierContracts = contractService.findContractsForSuppiler(supplier);
		}
		return SUCCESS;
	}
   
	
	@Override
	public void validate() {
		Supplier existingSupplier =supplierService.findSupplierByNumber(supplier.getSupplierNumber());
		if(existingSupplier != null && supplier.getId() == null ){
			addActionError("error.partSource.dupilcateSupplierNumber",new String[]{supplier.getSupplierNumber()});
		}
		//Supplier address validation
		if (supplier.getAddress() == null || !isAddressValid(supplier.getAddress())) {
			addActionError("error.partSource.address");
		}

		if(supplier.getId() == null && 
				(getInputCurrency() == null || !isPreferredCurrencyValid(getInputCurrency().toUpperCase()))){
			addActionError("error.partSource.preferredCurrency");
		}
		// supplierItems.removeAll(Collections.singletonList(null));	
		ItemMapping itemMapping;
		if(validateDialog && supplierItemLocation != null){
					if (supplierItemLocation.getFromDate() == null) {
						addActionError("error.partSource.invalidFromDate");
					}
					if (supplierItemLocation.getToDate() == null) {
						addActionError("error.partSource.invalidToDate");
					}
					if(supplierItemLocation.getFromDate()!=null && supplierItemLocation.getToDate() != null
							&& supplierItemLocation.getFromDate().isAfter(supplierItemLocation.getToDate())){
						addActionError("error.partSource.invalidFromDate");
					}
					if(supplierItemLocation.getLocationCode() == null || !StringUtils.hasText(supplierItemLocation.getLocationCode())){
						addActionError("error.partSource.location");
					}
					if(supplierItemLocation.getItemMapping().getFromItem() != null && supplierItemLocation.getItemMapping().getFromItem().getId()== null)
						addActionError("error.partSource.invalidItemName");
					if (supplierItemLocation.getItemMapping().getToItem() != null && !StringUtils.hasText(supplierItemLocation.getItemMapping().getToItem().getNumber())) {
						addActionError("error.partSource.invalidSupplierItemName");
					}
					if (supplierItemLocation.getItemMapping().getToItem().getDescription() == null ||  !StringUtils.hasText(supplierItemLocation.getItemMapping().getToItem().getDescription())) {
						addActionError("error.partSource.invalidSupplierItemDescription");
					}
					if(!hasActionErrors()){
					Item oemItem=catalogService.findById(supplierItemLocation.getItemMapping().getFromItem().getId())	;
					supplierItemLocation.getItemMapping().setFromItem(oemItem);
					
					//we don't have an option to edit the fields(fromdate,tillDate...).This validation for duplicate check based on fromdate,tilldate and locatiocode.
					try{
					itemMapping = itemMappingService.findItemMappingForOEMandSupplierItem(oemItem, supplierItemLocation.getItemMapping().getToItem().getNumber(), existingSupplier);
					if(itemMapping!=null && (supplierItemLocationService.findLocationsByMapping(itemMapping,supplierItemLocation.getFromDate(),supplierItemLocation.getToDate(),supplierItemLocation.getLocationCode()))>0)
						addActionError("error.partSource.duplicatelocationmapping");
					}catch(HibernateException e){
						logger.info("No Supplier item for"+ supplierItemLocation.getItemMapping().getToItem().getNumber());
						
					}
					
					
		    }		
		
		}


	}

	
	private boolean isPreferredCurrencyValid(String inputCurrency){
		boolean valid = true;
		try{
			Currency currency = Currency.getInstance(inputCurrency);
		} catch(Exception ex){
			valid = false;
		}
		return valid;
	}

	private boolean isAddressValid(Address address ) {
		return(StringUtils.hasText(address.getAddressLine1()) && StringUtils.hasText(address.getCity()) && StringUtils.hasText(address.getCountry()));
	}
     
	public String detail() {
		if (id == null || "".equals(id)) {
			supplier = supplierService.findById(supplier.getId());
		} else {
			supplier = supplierService.findById(new Long(id));
		}
		populateLocationorSiteNumber();
		if(supplier != null && supplier.getId() != null) {
			supplierContracts = contractService.findContractsForSuppiler(supplier);
		}

		return SUCCESS;
	}

	
	 // to populate location/site number on address UI while creating supplier
	
	private void populateLocationorSiteNumber() {
		if(null != supplier && (null != supplier.getLocations() && supplier.getLocations().size() > 0)){
			List<Location> locations = supplier.getLocations();
			supplier.getAddress().setLocation(locations.get(supplier.getLocations().size() - 1).getCode());
		}
	}
	
	
	public void prepare() throws Exception {
		if (StringUtils.hasText(id)){// || (supplier != null && supplier.getId() != null)) {
			Long idTobeUsed = id != null ? Long.parseLong(id) : supplier.getId();
			supplier = supplierService.findById(idTobeUsed);
			supplierItems =  supplierItemLocationService.findSupplierItems(supplier);
		}
		if(supplier != null && supplier.getId() != null) {
			supplierContracts = contractService.findContractsForSuppiler(supplier);
		}
	}
	
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public List<Contract> getSupplierContracts() {
		return supplierContracts;
	}

	public void setSupplierContracts(List<Contract> supplierContracts) {
		this.supplierContracts = supplierContracts;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public List<Location> getAllLocations() {
		return allLocations;
	}

	public void setAllLocations(List<Location> allLocations) {
		this.allLocations = allLocations;
	}

	public List<ItemMapping> getItemMappings() {
		return itemMappings;
	}

	public void setItemMappings(List<ItemMapping> itemMappings) {
		this.itemMappings = itemMappings;
	}

	public void setItemMappingService(ItemMappingService itemMappingService) {
		this.itemMappingService = itemMappingService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public String getJsonString() {
		return jsonString;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getInputCurrency() {
		return inputCurrency;
	}

	public void setInputCurrency(String inputCurrency) {
		this.inputCurrency = inputCurrency;
	}

	public List<Long> getItemLocations() {
		return itemLocations;
	}

	public void setItemLocations(List<Long> itemLocations) {
		this.itemLocations = itemLocations;
	}

	public SupplierItemLocationService getSupplierItemLocationService() {
		return supplierItemLocationService;
	}

	public void setSupplierItemLocationService(
			SupplierItemLocationService supplierItemLocationService) {
		this.supplierItemLocationService = supplierItemLocationService;
	}

	public int getIndex(){
		int index=0;
			for(ItemMapping itemMapping : itemMappings){
					index += itemMapping.getSupplierItemLocations().size();
			}
		return index;
	}

	
	@Override
	protected PageResult<?> getBody() {
		if (supplier != null && supplier.getId() != null) {
			Long idTobeUsed = id != null ? Long.parseLong(id) : supplier.getId();
			supplier = supplierService.findById(idTobeUsed);
			PageResult<SupplierItemLocation> supplierItemLocations = supplierItemLocationService.findAllSupplierLocationItemsForSupplier(supplier,getListCriteria());
			return supplierItemLocations;
		}
		return getEmptyPageResult();
	}
	
	//searchparms issue..need to change this logic
	private ListCriteria getListCriteria() {
		ListCriteria listCriteria =getCriteria();
		if(getSearchParams()!=null){
		listCriteria.removeFilterCriteria();
		for (Map.Entry<String, String[]> searchParamEntry : getSearchParams().entrySet()) {
			 listCriteria.addFilterCriteria("oemitem."+searchParamEntry.getKey(), searchParamEntry.getValue()[0]);
         }
		}
		listCriteria.removeSortCriteria();
		listCriteria.addSortCriteria("oemitem.number", SORT_ASC_PARAM.equalsIgnoreCase(getSord()));
		return listCriteria;
	}
	
	
	@Override
	protected void transformRowData(Object result, JSONObject row)
			throws JSONException {
		supplierItemLocation = (SupplierItemLocation) result;
		row.putOpt("id", supplierItemLocation.getId());
		row.putOpt("number", supplierItemLocation.getItemMapping().getFromItem().getNumber());
		row.putOpt("supplierItemLocation.itemMapping.fromItem.description", supplierItemLocation.getItemMapping().getFromItem().getDescription());
		row.putOpt("supplierItemLocation.itemMapping.toItem.number", supplierItemLocation.getItemMapping().getToItem().getNumber());
		row.putOpt("supplierItemLocation.itemMapping.toItem.description", supplierItemLocation.getItemMapping().getToItem().getDescription());
		row.putOpt("supplierItemLocation.locationCode", supplierItemLocation.getLocationCode());
		String dateFormat = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
		row.putOpt("supplierItemLocation.fromDate",((CalendarDate) supplierItemLocation.getFromDate() ).toString(dateFormat));
		row.putOpt("supplierItemLocation.toDate", ((CalendarDate) supplierItemLocation.getToDate()).toString(dateFormat));
		row.putOpt("supplierItemLocation.status", supplierItemLocation.isStatus()?"ACTIVE":"INACTIVE");
	}
	
	public String removeSupplierLocationItems() throws Exception {
			StringTokenizer st = new StringTokenizer(removedItems, ",");
			List<Long> selectedItemsToDelete = new ArrayList<Long>();
			while (st.hasMoreElements()) {
				String nextElement = (String) st.nextElement();
				selectedItemsToDelete.add(new Long(nextElement));
			}
			List<SupplierItemLocation> removedItms =supplierItemLocationService.findByIds(selectedItemsToDelete);
			for(SupplierItemLocation sil: removedItms){
		      this.supplierItemLocationService.delete(sil);
			}
			addActionMessage("message.supplieritem.updated", new String[] { supplier.getSupplierNumber().toString() });
		    return SUCCESS;
	}
	
	private void generateSupplierItem(){
		
		if (supplierItemLocation.getItemMapping().getToItem().getNumber() != null) {
			String supplierItemNumber = supplierItemLocation.getItemMapping().getToItem().getNumber();
			Item oemItem= supplierItemLocation.getItemMapping().getFromItem();
//			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(oemItem.getBusinessUnitInfo().getName());
			Item supplierItem = null;
			try {
				supplierItem = catalogService.findItemByItemNumberOwnedByServiceProvider(supplierItemNumber, supplier.getId());
				mergeSupplierItem(supplierItem,supplierItemLocation.getItemMapping().getToItem(), supplier);
				catalogService.updateItem(supplierItem);
				
			} catch (CatalogException e) {
				supplierItem = supplierItemLocation.getItemMapping().getFromItem().cloneMe();
				supplierItem.setNumber(supplierItemNumber);
				if (!supplierItemNumber.contains("#")) {
					supplierItem.setAlternateNumber(supplierItemNumber);
				}
				supplierItem.setOwnedBy(supplier);
				mergeSupplierItem(supplierItem,supplierItemLocation.getItemMapping().getToItem(), supplier);
				addBusinessUnit(supplierItem);
				catalogService.createItem(supplierItem);
			}
			
			
		}
		
		
	}
	
	private void addBusinessUnit(Item item) {
		List<String> businessUnitList = new ArrayList<String>();
		businessUnitList.add(IntegrationConstants.NMHG_EMEA);
		businessUnitList.add(IntegrationConstants.NMHG_US);
		final TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
		if (!CollectionUtils.isEmpty(businessUnitList)) {
			for (String buName : businessUnitList) {
				BusinessUnit bu = null;
				if (StringUtils.hasText(buName)) {
					bu=businessUnitService.findBusinessUnit(buName.trim());
				}if(bu!=null){
					businessUnitMapping.add(bu);
				}
			}
			if(!businessUnitMapping.isEmpty())
			item.setBusinessUnits(businessUnitMapping);
		}
	}
	
	private void mergeSupplierItem(Item supplierItem, Item toItem,Supplier supplier) {
		supplierItem.setName(toItem.getDescription());
		supplierItem.setDescription(toItem.getDescription());
		supplierItem.setMake(supplier.getName());
	}
	
	public String addSupplierLocationItem(){
		  boolean create=true;
		  ItemMapping itemMapping = null;
		  Item persistedItem=null;
		  generateSupplierItem();
		  if(supplierItemLocation != null){
			
			try {
				persistedItem = catalogService.findItemByItemNumberOwnedByServiceProvider(supplierItemLocation.getItemMapping().getToItem().getNumber(), supplier.getId());
				supplierItemLocation.getItemMapping().setToItem(persistedItem);
				itemMapping = itemMappingService.findItemMappingForOEMandSupplierItem(supplierItemLocation.getItemMapping().getFromItem(),supplierItemLocation.getItemMapping().getToItem().getNumber(),supplier);
				supplierItemLocation.setItemMapping(itemMapping);
				for(SupplierItemLocation locationItem :itemMapping.getSupplierItemLocations()){
			    	if(locationItem.getFromDate().equals(supplierItemLocation.getFromDate()) && locationItem.getToDate().equals(supplierItemLocation.getToDate()) && 
			    			locationItem.getLocationCode().equalsIgnoreCase(supplierItemLocation.getLocationCode()))
			    		 locationItem.setStatus(true);
			    		 this.supplierItemLocationService.update(locationItem);
			    		 addActionMessage("message.supplieritem.added", new String[] { supplierItemLocation.getItemMapping().getToItem().getNumber()});
			    	    create=false;
			    	    break;
			        }
				}catch (Exception e) {
			     	logger.info("There is not item mapping for supplieritem" + supplierItemLocation.getItemMapping().getToItem().getNumber() + e);
			}
			
	    }
		    if(create){
		    	supplierItemLocation.setStatus(true);
		    	this.supplierItemLocationService.save(supplierItemLocation);
		        addActionMessage("message.supplieritem.added", new String[] { supplierItemLocation.getItemMapping().getToItem().getNumber()});
		 }
		    validateDialog=true;
		    return SUCCESS;
	}
	
}
