package tavant.twms.web.admin.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.payment.rates.PartPrice;
import tavant.twms.domain.claim.payment.rates.PartPriceAdminService;
import tavant.twms.domain.claim.payment.rates.PartPriceAudit;
import tavant.twms.domain.claim.payment.rates.PartPriceHistoryRepository;
import tavant.twms.domain.claim.payment.rates.PartPriceRepairDateAudit;
import tavant.twms.domain.claim.payment.rates.PartPriceValues;
import tavant.twms.domain.claim.payment.rates.PartPrices;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class ManagePartPrice extends I18nActionSupport implements Validateable,
		Preparable {
    private final Logger logger = Logger.getLogger(ManagePartPrice.class);
	private String id;
	private PartPrices definition;
	private PartPriceAdminService partPriceAdminService;
	private List<Currency> currencyList = new ArrayList<Currency>();
	private String partPriceComments;
	private CatalogService catalogService;
	private List<PartPriceValues> partRateValues = new ArrayList<PartPriceValues>();
//	private Item nmhgPartNumber;
	private String nmhgPartNumber;
    private PartPriceAudit partPriceAudit;
	private Long partPriceAuditID;
	private PartPriceHistoryRepository partPriceHistoryRepository;
	private List<PartPrice> rates = new ArrayList<PartPrice>();
	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private static JSONArray EMPTY_PART_DETAIL;
	private String jsonString;
	
	
	public List<PartPrice> getRates() {
		return rates;
	}

	public void setRates(List<PartPrice> rates) {
		this.rates = rates;
	}

	private static final String MESSAGE_KEY_DUPLICATE = "error.manageRates.duplicatePartPrice";

    private static final String MESSAGE_KEY_UPDATE = "message.manageRates.updatePartPriceSuccess";

    private static final String MESSAGE_KEY_CREATE = "message.manageRates.createPartPriceSuccess";

    private static final String MESSAGE_KEY_DELETE = "message.manageRates.deletePartPriceSuccess";
	
	public String getNmhgPartNumber() {
		return nmhgPartNumber;
	}

	public void setNmhgPartNumber(String nmhgPartNumber) {
		this.nmhgPartNumber = nmhgPartNumber;
	}

	
	public List<PartPriceValues> getPartRateValues() {
		return partRateValues;
	}

	public void setPartRateValues(List<PartPriceValues> partRateValues) {
		this.partRateValues = partRateValues;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public String getPartPriceComments() {
		return partPriceComments;
	}

	public void setPartPriceComments(String partPriceComments) {
		this.partPriceComments = partPriceComments;
	}

	public List<Currency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<Currency> currencyList) {
		this.currencyList = currencyList;
	}

	public void prepare() throws Exception {
		if (StringUtils.isNotBlank(this.id)) {
			Long pk = Long.parseLong(this.id);
			this.definition = this.partPriceAdminService.findById(pk);
		} else {
			if(this.definition!=null){							
				if(StringUtils.isNotEmpty(nmhgPartNumber)){
					String partNumber = (nmhgPartNumber.replace(",","")).trim();
				try {
					definition.setNmhg_part_number(catalogService.findItemByItemNumberOwnedByManuf(partNumber));
				} catch (Exception e) {
					addActionError("error.manageRates.invalidPartNumber");
				}
				//	nmhgPartNumber=this.definition.getNmhg_part_number().getNumber();
				}
			}
			if(this.definition!=null && StringUtils.isNotEmpty(this.definition.getComments())){
				partPriceComments=this.definition.getComments();
			}
//			if(definition!=null && CollectionUtils.isEmpty(partRateValues)){
//			 definition.getRates().addAll(partRateValues);
//			}
			this.definition = new PartPrices();
			//definition.getPartPriceValues().addAll(partRateValues);
			
		}
		
		currencyList = orgService.listUniqueCurrencies();
		for (Currency currency : currencyList) {
			PartPriceValues partPriceValues = new PartPriceValues();
			Money rate = Money.valueOf(new BigDecimal(0.00), currency, 2);
			partPriceValues.setDealerNetPrice(rate);
			partPriceValues.setStandardCostPrice(rate);
			partPriceValues.setPlantCostPrice(rate);
			this.partRateValues.add(partPriceValues);
		}

	}
	
	 @Override
	    public void validate() {
	        super.validate();
	        validatePartPrices();
	        validatePartNumber();
	        if (!hasActionErrors()) {
				for (Iterator<PartPrice> iter = this.rates.iterator(); iter.hasNext();) {
					PartPrice partPrice = iter.next();
					if (partPrice == null
							|| (partPrice.getDuration().getFromDate() == null && partPrice.getDuration().getTillDate() == null)) {
						iter.remove();
					}
				}

			
				for (PartPrice partPrice : getRates()) {
					partPrice.setPartPrices(this.definition);

				}
			}
	        if(hasActionErrors() || hasFieldErrors())
	        	this.prepareRatesForUI();
	    }
	 
	 private void validatePartNumber() {
		 if(nmhgPartNumber.endsWith(", ")){
			 nmhgPartNumber = nmhgPartNumber.substring(0, nmhgPartNumber.length()-2);
		 }
		 if(this.definition != null && this.definition.getId() !=null){
			 PartPrices partPrices = this.partPriceAdminService.findPartPricesByItemNumber(nmhgPartNumber);
			 if(!(partPrices != null && partPrices.getId().equals(this.definition.getId()))){
				 addActionError("error.manageRates.partNumberExists");
			 }
		 }else{
			if(this.partPriceAdminService.findPartPricesByItemNumber(nmhgPartNumber)!=null){
				addActionError("error.manageRates.partNumberExists");
			}
		 }
	}

	private void validatePartPrices() {

		 int numRates = this.rates.size();
			if (this.rates.size() == 0) {
				addActionError("error.manageRates.moreConfigRequired");
				return;
			}
			for (PartPrice rate : rates) {
				if (rate != null && rate.getDuration() != null) {
					CalendarDate startDate = rate.getDuration().getFromDate();
					CalendarDate endDate = rate.getDuration().getTillDate();
					if (startDate == null) {
						addActionError("error.manageRates.startDateNotSpecified");
					}
					if (endDate == null) {
						addActionError("error.manageRates.endDateNotSpecified");
					}
				} else {
					addActionError("error.manageRates.invalidDateSpecified");
				}
			}
			if (hasActionErrors()) {
				return;
			}
			if (numRates < 2) {
				CalendarDate startDate = this.rates.get(0).getDuration().getFromDate();
				CalendarDate endDate = this.rates.get(0).getDuration().getTillDate();
				if (startDate.isAfter(endDate)) {
					addActionError("error.manageRates.endDateBeforeStartDate", new String[] { endDate.toString(DATE_FORMAT),
							startDate.toString(DATE_FORMAT) });
				}

//				for (PartPriceValues partPriceValues : this.rates.get(0).getPartPriceValues()) {
//
//					if (laborRateValues.getRate().breachEncapsulationOfAmount().signum() < 0) {
//						addActionError("error.manageRates.invalidLabourRate");
//
//					}
//				}
				return;
			}
			for (int i = 1; i < numRates; i++) {
				CalendarDuration thisLRDuration = this.rates.get(i).getDuration();
				CalendarDuration prevLRDuration = this.rates.get(i - 1).getDuration();
				CalendarDate preStartDate = prevLRDuration.getFromDate();
				CalendarDate prevEndDate = prevLRDuration.getTillDate();
				CalendarDate currentStartDate = thisLRDuration.getFromDate();
				CalendarDate currentEndDate = thisLRDuration.getTillDate();
				if ((preStartDate.isAfter(prevEndDate) || currentStartDate.isAfter(currentEndDate))){
					addActionError("error.manageRates.endDateBeforeStartDate", new String[] { prevEndDate.toString(DATE_FORMAT),
							currentStartDate.toString(DATE_FORMAT) });
				}

/*				if (!(prevEndDate.plusDays(1).equals(currentStartDate))) {
					addActionError("error.manageRates.noGapsInConsecutiveDateRange", new String[] { prevEndDate.toString(DATE_FORMAT),
							currentStartDate.toString(DATE_FORMAT) });

				}*/
			   validatePartPriceEntries();
		}
	 }

	  private void validatePartPriceEntries() {
//	    	boolean hasPriceEntry = false;
//		   	for(PartPriceValues partPriceValues:partPrices.getPartPriceValues()) {
//		   		if(partPriceValues.getDealerNetPrice()!= null && 
//		   				partPriceValues.getDealerNetPrice().breachEncapsulationOfAmount().signum()<0)
//		   		{
//		   			addActionError("error.manageRates.invalidDealerNetPrice");
//		   		}
//		   		if(partPriceValues.getStandardCostPrice()!=null && 
//		   				partPriceValues.getStandardCostPrice().breachEncapsulationOfAmount().signum()<0)
//		   		{
//		   			addActionError("error.manageRates.invalidStandardCostPrice");
//		   		}
//		   		if(partPriceValues.getPlantCostPrice()!=null &&
//		   				partPriceValues.getPlantCostPrice().breachEncapsulationOfAmount().signum()<0)
//		   		{
//		   			addActionError("error.manageRates.invalidPlantCostPrice");
//		   		}
//		   		if(!hasPriceEntry && (partPriceValues.getDealerNetPrice()!= null 
//		   				|| partPriceValues.getStandardCostPrice()!=null || partPriceValues.getPlantCostPrice()!=null))
//		   			hasPriceEntry = true;		   	
//		   	}
//		   	if(!hasPriceEntry) {
//		   		addActionError("error.manageRates.morePriceEntriesRequired");
//		   	}
//		   	if (partPrices != null && partPrices.getDuration() != null) {
//				CalendarDate startDate = partPrices.getDuration().getFromDate();
//				CalendarDate endDate = partPrices.getDuration().getTillDate();
//				if (startDate == null) {
//					addActionError("error.manageRates.startDateNotSpecified");
//				}
//				if (endDate == null) {
//					addActionError("error.manageRates.endDateNotSpecified");
//				}
//			} else {
//				addActionError("error.manageRates.invalidDateSpecified");
//			}
	    }
	 
	public String showPartPrice() {
	if (this.definition!=null && this.definition.getId() != null) {
		this.rates.addAll(this.definition.getRates());
		this.nmhgPartNumber = this.definition.getNmhg_part_number().getNumber();
	}
		this.prepareRatesForUI();
		return SUCCESS;
	}

	private void prepareRatesForUI() {
		List<Currency> currencies = new ArrayList<Currency>();
		    for(PartPrice rate : rates){
			for (Iterator<PartPriceValues> it = rate.getPartPriceValues()
					.iterator(); it.hasNext();) {
				PartPriceValues values = it.next();
				Currency currency = getCurrencyOfPartPriceValues(values);
				// currency is null, so no value has been entered
				if (currency == null) {
					it.remove(); // we are removing null rate values here as
					// default values are added in next loop
					continue;
				}
				currencies.add(currency);
				preparePartPriceValuesForUI(values, currency);
			}
			for (Currency currency : currencyList) {
				if (!currencies.contains(currency)) {
					PartPriceValues values = new PartPriceValues();
					preparePartPriceValuesForUI(values, currency);
					rate.getPartPriceValues().add(values);
				}
			 }
		    }

	}

	private Currency getCurrencyOfPartPriceValues(
			PartPriceValues partPriceValues) {
		Money money = null;
		if (partPriceValues.getCurrency() != null)
			money = partPriceValues.getCurrency();
		else if (partPriceValues.getDealerNetPrice() != null)
			money = partPriceValues.getDealerNetPrice();
		else if (partPriceValues.getStandardCostPrice() != null)
			money = partPriceValues.getStandardCostPrice();
		else if (partPriceValues.getPlantCostPrice() != null)
			money = partPriceValues.getPlantCostPrice();
		if (money != null)
			return money.breachEncapsulationOfCurrency();
		return null;
	}

	private void preparePartPriceValuesForUI(PartPriceValues partPriceValues,
			Currency currency) {
		Money rate = Money.valueOf(new BigDecimal(0.00), currency, 2);
		if (partPriceValues.getDealerNetPrice() == null)
			partPriceValues.setDealerNetPrice(rate);
		if (partPriceValues.getStandardCostPrice() == null)
			partPriceValues.setStandardCostPrice(rate);
		if (partPriceValues.getPlantCostPrice() == null)
			partPriceValues.setPlantCostPrice(rate);
	}

	public String listNMHGPartNumbers() {
		return getItemNumbersStartingWith(getSearchPrefix());
	}
	
    public String getCausalPartDetails() throws JSONException {
        JSONArray details;
//        fix for TWMS4.3-711
            try {
                details = new JSONArray();
                Item item = this.catalogService.findItemByItemNumber(nmhgPartNumber);
                details.put(item.getDescription());

            } catch (Exception e) {
                details = EMPTY_PART_DETAIL;
            }
        jsonString = details.toString();
        return SUCCESS;
    }
    
    static {
		EMPTY_PART_DETAIL = new JSONArray();
		EMPTY_PART_DETAIL.put("-");
	}

	private String getItemNumbersStartingWith(String prefix) {
		if (StringUtils.isNotEmpty(prefix)) {
			List<Item> items = catalogService.findItemsWhoseNumbersStartWith(
					prefix.toUpperCase(), 0, 10);
			return generateAndWriteComboboxJson(items, "number");
		} else {
			return generateAndWriteEmptyComboboxJson();
		}
	}

	private String preparePrice() {
		 Set<Long> idsFromUI = new HashSet<Long>();
	        for (PartPrice rate : this.rates) {
	            if (rate.getId() != null) {
	                idsFromUI.add(rate.getId());
	            }
	            else {
					this.definition.getRates().add(rate);
				}
	        }

	        for (Iterator<PartPrice> it = this.definition.getRates().iterator(); it.hasNext();) {
	        	PartPrice rate = it.next();
	            if (rate.getId() != null && !idsFromUI.contains(rate.getId())) {
	            	rate.getD().setActive(Boolean.FALSE);
	            }
	            for (Iterator<PartPriceValues> iter = rate.getPartPriceValues()
						.iterator(); iter.hasNext();) {
					PartPriceValues values = iter.next();
					if (values.getDealerNetPrice() == null
							&& values.getStandardCostPrice() == null
							&& values.getPlantCostPrice() == null)
						iter.remove();
				}
	        }

//	        if (!this.partPriceAdminService.isUnique(this.definition)) {
//	            addActionError(MESSAGE_KEY_DUPLICATE);
//                return INPUT;
//	     }
			
		 

		// this.definition.getRates().clear();
		// this.definition.getRates().addAll(rates);
		return SUCCESS;
	}

	public String createPartPrice() throws Exception {
		String action = preparePrice();
		prepareAudit(definition);
		if(StringUtils.isNotEmpty(nmhgPartNumber)){
			String partNumber = (nmhgPartNumber.replace(",","")).trim();
			try {
				definition.setNmhg_part_number(catalogService.findItemByItemNumberOwnedByManuf(partNumber));
			} catch (Exception e) {
				addActionError("error.manageRates.invalidPartNumber");
				return INPUT;
			}	
		}
		definition.setComments(partPriceComments);
		if (SUCCESS.equals(action)) {
			this.partPriceAdminService.save(this.definition);
			addActionMessage(MESSAGE_KEY_CREATE);
		}
		prepareRatesForUI();
		return action;
	}

	public String updatePartPrice() throws Exception {
		String action = preparePrice();
		prepareAudit(definition);
		if (SUCCESS.equals(action)) {
		try {	
	    this.partPriceAdminService.update(this.definition);
		prepareRatesForUI();
		 } catch (Exception e) {
	       this.logger.error("Exception in Updating Part Price Configuration", e);
	       return INPUT;
	     }
	     addActionMessage(MESSAGE_KEY_UPDATE);
		}
		return action;
	}

	private void prepareAudit(PartPrices definition) {
		List<PartPrice> partPriceList = definition.getRates();
		if(CollectionUtils.isNotEmpty(partPriceList)){
			final PartPriceAudit partPriceAudit=new PartPriceAudit();
			final List<PartPriceRepairDateAudit> partPriceRepairDateAudits= new ArrayList<PartPriceRepairDateAudit>();
			boolean partPriceRepairDateAuditNull=true;
			for(PartPrice partPrice:partPriceList){
			List<PartPriceValues> partPriceValuesList = partPrice.getPartPriceValues();
			for(PartPriceValues partPriceValues:partPriceValuesList){
				PartPriceRepairDateAudit partPriceRepairDateAudit=new PartPriceRepairDateAudit();
				if(partPriceValues.getDealerNetPrice()!=null && 
				     partPriceValues.getDealerNetPrice().breachEncapsulationOfAmount().
				     compareTo(BigDecimal.ZERO) != 0){
					partPriceRepairDateAudit.setDealerNetPrice(partPriceValues.getDealerNetPrice());
					partPriceRepairDateAuditNull=false;
				}
				if(partPriceValues.getStandardCostPrice()!=null && 
					     partPriceValues.getStandardCostPrice().breachEncapsulationOfAmount().
					     compareTo(BigDecimal.ZERO) != 0){
					partPriceRepairDateAudit.setStandardCostPrice(partPriceValues.getStandardCostPrice());
					partPriceRepairDateAuditNull=false;
				}
				if(partPriceValues.getPlantCostPrice()!=null && 
					     partPriceValues.getPlantCostPrice().breachEncapsulationOfAmount().
					     compareTo(BigDecimal.ZERO) != 0){
					partPriceRepairDateAudit.setPlantCostPrice(partPriceValues.getPlantCostPrice());
					partPriceRepairDateAuditNull=false;
				}
				if(!partPriceRepairDateAuditNull){
					partPriceRepairDateAudit.setDuration(partPrice.getDuration());	
				 partPriceRepairDateAudits.add(partPriceRepairDateAudit);
				}
			}
		  }
			if(CollectionUtils.isNotEmpty(partPriceRepairDateAudits)){
			 partPriceAudit.setPartPriceRepairDateAudits(partPriceRepairDateAudits);	
			 partPriceAudit.setComments(definition.getComments());
			 definition.getPartPriceAudits().add(partPriceAudit);
			}
			
		}
		
	}

	public String deletePartPrice() throws Exception {
		this.partPriceAdminService.delete(this.definition);
		addActionMessage(MESSAGE_KEY_DELETE);
		return SUCCESS;
	}
	
	 public String getHistory(){
	  	setPartPriceAudit(partPriceHistoryRepository.find(partPriceAuditID));
	    return "success";
	  }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PartPrices getDefinition() {
		return definition;
	}

	public void setDefinition(PartPrices definition) {
		this.definition = definition;
	}

	public PartPriceAdminService getPartPriceAdminService() {
		return partPriceAdminService;
	}

	public void setPartPriceAdminService(
			PartPriceAdminService partPriceAdminService) {
		this.partPriceAdminService = partPriceAdminService;
	}
	
	public PartPriceHistoryRepository getPartPriceHistoryRepository() {
		return partPriceHistoryRepository;
	}

	public void setPartPriceHistoryRepository(
			PartPriceHistoryRepository partPriceHistoryRepository) {
		this.partPriceHistoryRepository = partPriceHistoryRepository;
	}

	public Long getPartPriceAuditID() {
		return partPriceAuditID;
	}

	public void setPartPriceAuditID(Long partPriceAuditID) {
		this.partPriceAuditID = partPriceAuditID;
	}

	public PartPriceAudit getPartPriceAudit() {
		return partPriceAudit;
	}

	public void setPartPriceAudit(PartPriceAudit partPriceAudit) {
		this.partPriceAudit = partPriceAudit;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}


}
