package tavant.twms.web.warranty;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.policy.CompetitionType;
import tavant.twms.domain.policy.CompetitorMake;
import tavant.twms.domain.policy.CompetitorModel;
import tavant.twms.domain.policy.Market;
import tavant.twms.domain.policy.MarketService;
import tavant.twms.domain.policy.MarketType;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.web.i18n.I18nActionSupport;

public class WarrantyAutoPopulateAction extends I18nActionSupport {
	private String selectedBusinessUnit;
	private List<TransactionType> listOfTransactionTypes = new ArrayList<TransactionType>();
	private List<Market> listOfMarketTypes = new ArrayList<Market>();
	private List<CompetitionType> listOfCompetitionTypes = new ArrayList<CompetitionType>();
	private List<CompetitorMake> listOfCompetitorMakes = new ArrayList<CompetitorMake>();
	private List<CompetitorModel> listOfCompetitorModels = new ArrayList<CompetitorModel>();
	
	private WarrantyService warrantyService;
	private MarketService marketService;
	
	public String listBUTransactionTypes() {	
		listOfTransactionTypes = this.warrantyService.listTransactionTypes();
		
		return generateAndWriteComboboxJson(listOfTransactionTypes, "id", "displayType");
	}
	
	
	public String listBUMarketTypes() {	
		listOfMarketTypes =  this.marketService.listMarketTypes();
		
		return generateAndWriteComboboxJson(listOfMarketTypes, "id", "displayTitle");
	}
	
	public String listBUCompetitionTypes() {	
		listOfCompetitionTypes = this.warrantyService.listCompetitionTypes();
		
		return generateAndWriteComboboxJson(listOfCompetitionTypes, "id", "displayType");
	}
	
	
	public String listBUCompetitorMakes() {	
		listOfCompetitorMakes = this.warrantyService.listCompetitorMake();
		
		return generateAndWriteComboboxJson(listOfCompetitorMakes, "id", "displayMake");
	}
	
	
	public String listBUCompetitorModels() {	
		listOfCompetitorModels = this.warrantyService.listCompetitorModel();
		
		return generateAndWriteComboboxJson(listOfCompetitorModels, "id", "displayModel");
	}
	
	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public List<TransactionType> getListOfTransactionTypes() {
		return listOfTransactionTypes;
	}

	public void setListOfTransactionTypes(
			List<TransactionType> listOfTransactionTypes) {
		this.listOfTransactionTypes = listOfTransactionTypes;
	}

	public WarrantyService getWarrantyService() {
		return warrantyService;
	}

	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}


	public MarketService getMarketService() {
		return marketService;
	}


	public void setMarketService(MarketService marketService) {
		this.marketService = marketService;
	}	
}
