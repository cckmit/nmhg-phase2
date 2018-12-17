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
package tavant.twms.web.admin.supplier;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CONTRACT_APPLICABILITY_RULES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionRepository;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.CarrierRepository;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.supplier.ItemMappingRepository;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractRepository;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.contract.CoverageCondition;
import tavant.twms.domain.supplier.contract.RecoveryFormula;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.actions.AbstractGridAction;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.rules.SerializerFactory;

/**
 * 
 * @author dinesh.kk
 * 
 */
@SuppressWarnings("serial")
public class ManageContract extends AbstractGridAction {

	private static Logger logger = LogManager.getLogger(ManageContract.class);

	private Contract contract;

	private Long id;
    
	private ContractRepository contractRepository;

	private CatalogRepository catalogRepository;

	private PaymentDefinitionRepository paymentDefinitionRepository;

	private ItemMappingRepository itemMappingRepository;

	private CarrierRepository carrierRepository;
	
	String addedItems;	

	String removedItems;

	/*
	 * private RuleJSONSerializer ruleJSONSerializer = new
	 * RuleJSONSerializer(CONTRACT_APPLICABILITY_RULES);
	 */

	private String jsonString;

	private UserRepository userRepository;

	private SerializerFactory serializerFactory;

	private String itemName;

	private String itemNumber;

	private String supplierName;

	private String duplicateItemName;

	private String prevContractName;

	private String locationName;

	private ContractService contractService;

	private List<Item> items = new ArrayList<Item>();

	private List<Item> prevItems = new ArrayList<Item>();
	
	private List<Item> includeItems = new ArrayList<Item>();

	private boolean hasWarning = false;

	protected String searchPrefix;

	private CatalogService catalogService;

	private String supplierId;

	private SupplierService supplierService;
	
	private Integer pageNo = new Integer(0);
	
	private List<Integer> pageNoList = new ArrayList<Integer>();
	
	private ConfigParamService configParamService;

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public String preview() {
		contract = contractRepository.findById(id);
		//populateItemsCovered();
		prepareCoverageConditions();
		return SUCCESS;
	}

	/**
	 * This method is directly called from the jsp's.
	 * 
	 * @return
	 */
	public List<Section> getSections() {
		return paymentDefinitionRepository.findAllSections();
	}

	public List<Carrier> getCarriers() {
		List<Carrier> carriers = carrierRepository.findAllCarriers();
		Collections.sort(carriers, new Comparator<Carrier>() {
			public int compare(Carrier o1, Carrier o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}

		});
		return carriers;
	}

	public String detail() {
		if (id == null) {
			contract = contractRepository.findById(contract.getId());
		} else {
			contract = contractRepository.findById(id);
		}
		for (CompensationTerm compensationTerm : contract
				.getCompensationTerms()) {
			compensationTerm.setCovered(true);
		}
		//populateItemsCovered();
		prepareCoverageConditions();
		return SUCCESS;
	}
	
	private void populateItemsCovered() {
		items = contract.getItemsCovered();
		for(Item i : items) {
			i.setOemItemNumber(getOemPartNumberForItem(i));
		}
		Collections.sort(this.items, new Comparator<Item>() {
            public int compare(Item i1, Item i2) {
           	 return i1.getOemItemNumber().compareToIgnoreCase(i2.getOemItemNumber());
           }
		});
	}
	
	private void populateOemItemNumber(List<Item> items) {
		for(Item i : items) {
			i.setOemItemNumber(getOemPartNumberForItem(i));
		}
		Collections.sort(items, new Comparator<Item>() {
            public int compare(Item i1, Item i2) {
           	 return i1.getOemItemNumber().compareToIgnoreCase(i2.getOemItemNumber());
           }
		});
	}
	
	public String submit() {
		prepareCompensationTerms();
		boolean isNewContract = contract.getId() == null;		
		if (!isHasWarning()) {			
			this.contractService.createOrUpdateContract(contract);
			if (isNewContract) {
				addActionMessage("message.contractAdmin.contractCreated",
						new String[] { contract.getId().toString() });
			} else {
				addActionMessage("message.contractAdmin.contractUpdated",
						new String[] { contract.getId().toString() });
			}
			return SUCCESS;
		} else {
			return INPUT;
		}
	}

	// as it not possible to remove the div section for in jsp removing it in
	// action class for the time being
	private void prepareCompensationTerms() {
		List<CompensationTerm> compensationTerms = new ArrayList<CompensationTerm>();
		for (CompensationTerm term : contract.getCompensationTerms()) {
			if (term != null && term.isCovered()) {
				if(term.getRecoveryFormula().getMaximumAmount()!=null && term.getRecoveryFormula().getMaximumAmount().isZero())
				{
					term.getRecoveryFormula().setMaximumAmount(null);
				}
				compensationTerms.add(term);
				if (Section.LABOR.equals(term.getSection().getName())) {
					if (term.isStdLaborCovered()) {
						term.getRecoveryFormula().setNoOfHours(0.0);
						if (CompensationTerm.STD_DEALER_LABOR_RATE.equals(term
								.getPriceType())) {
							term.getRecoveryFormula().setSupplierRate(null); 
						}
					} else {
						term.getRecoveryFormula().setMaximumAmount(null);
						term.getRecoveryFormula().setAddedConstant(null);
					}
				}
			}
		}
		contract.setCompensationTerms(compensationTerms);
	}

	private void prepareCoverageConditions() {
		Collections.sort(contract.getCoverageConditions(), new Comparator<CoverageCondition>(){
			public int compare(CoverageCondition condition1, CoverageCondition condition2) {
				if(condition1 == null || condition1.getComparedWith() == null) return 1;
				if(condition2 == null || condition2.getComparedWith() == null) return 1;
				if(condition1.getComparedWith().ordinal() > condition2.getComparedWith().ordinal())
					return 1;
				return -1;
			}
		});
	}
	
/*	public boolean validateDuplicateParts() {
		for (Item item : getItems()) {
			List<Contract> existingContracts = this.contractRepository
					.findContractsForItem(item);
			if (existingContracts != null && !existingContracts.isEmpty()) {
				Contract existingContract = existingContracts.iterator().next();
				if (existingContract.getId() != contract.getId()) {
					setDuplicateItemName(item.getNumber());
					setPrevContractName(existingContract.getName());
					return true;
				}
			}
		}
		return false;
	}*/

	/**
	 * Removes all the null items from contract.itemsCovered
	 * 
	 * @return
	 */
	public List<Item> getItemsCovered() {
		if (contract == null || contract.getItemsCovered() == null
				|| contract.getItemsCovered().isEmpty()) {
			ArrayList<Item> itemList = new ArrayList<Item>();
			itemList.add(new Item());
			return itemList;
		}
		prepareItems();
		return contract.getItemsCovered();
	}

	private void prepareItems() {
		for (Iterator<Item> iter = items.iterator(); iter.hasNext();) {
			Item item = iter.next();
			if (item == null || item.getId() == null) {
				iter.remove();
			}
		}
		contract.setItemsCovered(items);
	}

	public String getApplicabilityTermsJSON() throws JSONException {
		List<DomainPredicate> predicates = new ArrayList<DomainPredicate>();
		if (contract != null) {
			List<DomainRule> rules = contract.getApplicabilityTerms();
			for (DomainRule r : rules) {
				predicates.add(r.getPredicate());
			}
		}
		return serializerFactory.getRuleJSONSerializer(
				CONTRACT_APPLICABILITY_RULES).toJSONArray(predicates)
				.toString();
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContractRepository getContractRepository() {
		return contractRepository;
	}

	public void setContractRepository(ContractRepository contractRepository) {
		this.contractRepository = contractRepository;
	}

	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public void setPaymentDefinitionRepository(
			PaymentDefinitionRepository paymentDefinitionRepository) {
		this.paymentDefinitionRepository = paymentDefinitionRepository;
	}

	public ItemMappingRepository getItemMappingRepository() {
		return itemMappingRepository;
	}

	@Required
	public void setItemMappingRepository(
			ItemMappingRepository itemMappingRepository) {
		this.itemMappingRepository = itemMappingRepository;
	}

	@Override
	
	 
	public void validate() {
		for (CompensationTerm compensationTerm : contract
				.getCompensationTerms()) {			
			if (compensationTerm != null && compensationTerm.isCovered()) {
				if (compensationTerm.getSection() != null) {
					RecoveryFormula recoveryFormula = compensationTerm
							.getRecoveryFormula();
					if (Section.LABOR.equals(compensationTerm.getSection()
							.getName())) {
						if (recoveryFormula == null) {
							addActionError(
									"error.contractAdmin.sectionDetailsRequired",
									new String[] { compensationTerm
											.getSection().getName() });
						}
					}
					if (recoveryFormula != null
							&& recoveryFormula.getPercentageOfCost() == null) {
						addActionError(
								"error.contractAdmin.percentageCostRequired",
								new String[] { compensationTerm.getSection()
										.getName() });
					}
					if (recoveryFormula != null
							&& recoveryFormula.getPercentageOfCost() != null
							&& recoveryFormula.getPercentageOfCost().doubleValue() < 0.0) {
						addActionError(
								"error.contractAdmin.percentageCostNegative",
								new String[] { compensationTerm.getSection()
										.getName() });
					}
					if (recoveryFormula != null
							&& recoveryFormula.getMaximumAmount() != null
							&& recoveryFormula.getMaximumAmount().isZero()) {
						recoveryFormula.setMaximumAmount(null);
					}
					if (recoveryFormula != null
							&& recoveryFormula.getMaximumAmount() != null
							&& recoveryFormula.getAddedConstant() != null) {
						if (recoveryFormula.getAddedConstant().isGreaterThan(
								recoveryFormula.getMaximumAmount())) {
							addActionError(
									"error.contractAdmin.flatRateGreaterThanMax",
									new String[] { compensationTerm
											.getSection().getName() });
						}
					}
					

					if (Section.LABOR.equals(compensationTerm.getSection()
							.getName())) {
						recoveryFormula = compensationTerm.getRecoveryFormula();
						if (CompensationTerm.SPL_DEALER_LABOR_RATE
								.equals(compensationTerm.getPriceType())
								|| CompensationTerm.SPL_SUPPLIER_LABOR_RATE
										.equals(compensationTerm.getPriceType())) {
							if (recoveryFormula.getNoOfHours() == null) {
								addActionError(
										"error.contractAdmin.labor.noOfHours",
										new String[] { compensationTerm
												.getSection().getName() });
							}
						}
						if (CompensationTerm.SPL_SUPPLIER_LABOR_RATE
								.equals(compensationTerm.getPriceType())
								|| CompensationTerm.STD_SUPPLIER_LABOR_RATE
										.equals(compensationTerm.getPriceType())) {
							if (recoveryFormula.getSupplierRate() == null) {
								addActionError(
										"error.contractAdmin.labor.rate",
										new String[] { compensationTerm
												.getSection().getName() });
							}
						}
					}
				} else {
					addActionError("error.contractAdmin.sectionRequired");
				}
			}
		}
		addSelectedItemsToItemsCovered(contract);
		List<Item> itemsCovered = getItems();
		
		if (itemsCovered.isEmpty()) {
			addActionError("error.contractAdmin.noSupplierItem");
		} 
		if (!hasActionErrors() && !isHasWarning()) {
			/*if (validateDuplicateParts()) {
				setHasWarning(true);
			}*/
		} else {
			setHasWarning(false);
		}
	}

	public String listSuppliers() {
		List<Supplier> suppliers = supplierService.findSuppliersWithNameLike(
				getSearchPrefix(), 0, 10);
		if (StringUtils.hasText(this.searchPrefix)) {
			List<String> labelNames = new ArrayList<String>(2);
			labelNames.add("name");
			//labelNames.add("supplierNumber");
			return generateAndWriteComboboxJson(suppliers, "id", labelNames);
		} else {
			return generateAndWriteEmptyComboboxJson();
		}
	}

	public String getSupplierCurrency() throws JSONException {
		Supplier supplier = supplierService.findById(new Long(this.supplierId));
		JSONArray details = new JSONArray();
		JSONObject json = new JSONObject();
		json.append("id", supplier.getId());
		json.append("preferredCurrency", supplier.getPreferredCurrency());
		details.put(json);
		jsonString = details.toString();
		return SUCCESS;
	}

	public String listShipmentLocations() {
		String locationCode = getSearchPrefix().toLowerCase();
		List<Location> locations = new ArrayList<Location>();
		for (Location location : contract.getSupplier().getLocations()) {
			if (location.getD() != null
					&& location.getD().isActive()
					&& StringUtils.hasText(location.getCode())
					&& location.getCode().toLowerCase()
							.startsWith(locationCode)) {
				locations.add(location);
			}
		}
		return generateAndWriteComboboxJson(locations, "id", "code");
	}

	public String listSupplierItems() {
//      Fix for TWMS4.3-706
        if(contract != null && contract.getSupplier() != null){
            List<Item> itemsFromSupplier = catalogRepository.findItemsOwnedBy(contract.getSupplier().getId());
            return generateAndWriteComboboxJson(itemsFromSupplier,"id","number");
        }
        return generateEmptyComboboxJson();
	}

	public String getSupplierItemDetails() throws CatalogException {
		Item item = catalogService.findSupplierItem(itemNumber, Long
				.parseLong(supplierId));
		jsonString = serializeToJson(item).toString();
		return SUCCESS;
	}

	/**
	 * Serializes the important portions of an Item to a JSONObject. Returns
	 * null if the serialization cannot be performed.
	 * 
	 * @param item
	 *            the item to serialize
	 * @return the serialized JSONObject
	 */
	private JSONObject serializeToJson(Item item) {
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("oemPartNumber", getOemPartNumberForItem(item));
			jsonObj.put("description", item.getDescription());
			jsonObj.put("number", item.getNumber());
			jsonObj.put("id", item.getId());
		} catch (JSONException ignored) {
			logger.warn("Error serializing " + item
					+ " to JSON; ignoring this item", ignored);
		}

		return jsonObj;
	}

	public String getOemPartNumberForItem(Item item) {
		Item supplierItem = itemMappingRepository
				.findOEMItemForSupplierItem(item);
		return (supplierItem == null) ? " " : supplierItem.getNumber();
	}

	public Map<String, String> getStandardRate() {
		Map<String, String> rateMap = new HashMap<String, String>();
		rateMap.put(CompensationTerm.STD_SUPPLIER_LABOR_RATE, "Supplier Rate");
		rateMap.put(CompensationTerm.STD_DEALER_LABOR_RATE, "Dealer Rate");
		return rateMap;
	}

	public Map<String, String> getSpecialRate() {
		Map<String, String> rateMap = new HashMap<String, String>();
		rateMap.put(CompensationTerm.SPL_SUPPLIER_LABOR_RATE, "Supplier Rate");
		rateMap.put(CompensationTerm.SPL_DEALER_LABOR_RATE, "Dealer Rate");
		return rateMap;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public SerializerFactory getSerializerFactory() {
		return serializerFactory;
	}

	@Required
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}

	public void setCarrierRepository(CarrierRepository carrierRepository) {
		this.carrierRepository = carrierRepository;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public List<Item> getPrevItems() {
		return prevItems;
	}

	public void setPrevItems(List<Item> prevItems) {
		this.prevItems = prevItems;
	}

	public boolean isHasWarning() {
		return hasWarning;
	}

	public void setHasWarning(boolean hasWarning) {
		this.hasWarning = hasWarning;
	}

	public String getDuplicateItemName() {
		return duplicateItemName;
	}

	public void setDuplicateItemName(String duplicateItemName) {
		this.duplicateItemName = duplicateItemName;
	}

	public String getPrevContractName() {
		return prevContractName;
	}

	public void setPrevContractName(String prevContractName) {
		this.prevContractName = prevContractName;
	}

	public String getSearchPrefix() {
		return searchPrefix;
	}

	public void setSearchPrefix(String searchPrefix) {
		this.searchPrefix = searchPrefix;
	}

	public Item getOemItemForSupplierItem(Item item) {
		return itemMappingRepository.findOEMItemForSupplierItem(item);
	}

	public String importRecClaim() {
		return SUCCESS;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public SupplierService getSupplierService() {
		return supplierService;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}


	public String searchItems() {
		if(null!=supplierId && !"".equals(supplierId)){
			PageResult<Item> items = catalogRepository.findAllItemsOwnedByWithNumberLike(new Long(supplierId), itemNumber, getListCriteria());
			this.items = items.getResult();
			this.prevItems.addAll(this.items);
			for (Item item : this.items) {
				if (this.contract != null && this.contract.getItemsCovered().contains(item)) {
					this.prevItems.remove(item);
					includeItems.add(item);
				}
			}
			for (int i = 0; i < items.getNumberOfPagesAvailable(); i++) {
				this.pageNoList.add(new Integer(i + 1));
			}
		}
		return SUCCESS;
	}

	public void addSelectedItemsToItemsCovered(Contract contract) {
		if (StringUtils.hasText(this.addedItems)) {
			List<Item> existingItems = contract.getItemsCovered();
			if (existingItems == null)
				existingItems = new ArrayList<Item>();
			StringTokenizer st2 = new StringTokenizer(this.addedItems, ",");
			List<Long> selectedOptionsList = new ArrayList<Long>();
			while (st2.hasMoreElements()) {
				String nextElement = (String) st2.nextElement();
				selectedOptionsList.add(new Long(nextElement));
			}
			List<Item> itemsToBeAdded = catalogService.findByIds(selectedOptionsList);
			existingItems.addAll(itemsToBeAdded); // add newly added items
			setItems(existingItems);
		} else {
			setItems(contract.getItemsCovered());
		}
	}

	public String removeItemsFromItemGroup() throws Exception {
		if (contract != null && contract.getId() != null) {
			StringTokenizer st = new StringTokenizer(this.removedItems, ",");
			List<Long> selectedItemsToDelete = new ArrayList<Long>();
			while (st.hasMoreElements()) {
				String nextElement = (String) st.nextElement();
				selectedItemsToDelete.add(new Long(nextElement));
			}
			List<Item> existingItems = this.contract.getItemsCovered();
			existingItems.removeAll(catalogService.findByIds(selectedItemsToDelete));
			this.contractService.createOrUpdateContract(this.contract);
			addActionMessage("message.contractAdmin.contractUpdated", new String[] { contract.getId().toString() });
		}
		return SUCCESS;
	}

	@Override
	protected void transformRowData(Object result, JSONObject row) throws JSONException {
		Item item = (Item) result;
		row.putOpt("id", item.getId());
		row.putOpt("item_number", item.getNumber());
		row.putOpt("item_oemItemNumber", item.getOemItemNumber());
		row.putOpt("item.description", item.getDescription());

	}

	@Override
	protected PageResult<?> getBody() {
		if (contract != null && contract.getId() != null) {
			PageResult<Item> pageResult = catalogService.findAllItemsForContract(contract, getCriteria());
			populateOemItemNumber(pageResult.getResult());
			return pageResult;
		}
		return getEmptyPageResult();
	}

	private ListCriteria getListCriteria() {
		ListCriteria criteria = new ListCriteria();
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageNumber(this.pageNo.intValue());
		pageSpecification.setPageSize(10);
		criteria.setPageSpecification(pageSpecification);
		criteria.addSortCriteria("item.number", true);
		criteria.setCaseSensitiveSort(true);
		return criteria;
	}
	
	public boolean displayOnAcceptAndOnVendorReviewResponsibilitybutton() {
		return configParamService
				.getBooleanValue(ConfigName.AUTO_INITIATE_ON_CLAIM_ACCEPT_AND_VENDOR_REVIEW_RESPONSIBILITY
						.getName());
	}

	public List<Item> getIncludeItems() {
		return includeItems;
	}

	public void setIncludeItems(List<Item> includeItems) {
		this.includeItems = includeItems;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	
	public List<Integer> getPageNoList() {
		return pageNoList;
	}

	public void setPageNoList(List<Integer> pageNoList) {
		this.pageNoList = pageNoList;
	}
	
	public String getAddedItems() {
		return addedItems;
	}

	public void setAddedItems(String addedItems) {
		this.addedItems = addedItems;
	}

	public String getRemovedItems() {
		return removedItems;
	}

	public void setRemovedItems(String removedItems) {
		this.removedItems = removedItems;
	}
	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
}