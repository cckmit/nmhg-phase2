package tavant.twms.web.search;

import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import java.util.List;
import java.util.Map;

public class QuickSearch extends TwmsActionSupport {

	private String context;

    private ConfigParamService configParamService;

    public String showSearchpage()
	{
		if(context.equalsIgnoreCase("InventorySearches"))
			return "inventory";
		else if (context.equalsIgnoreCase("MajorComponentSearches"))
			return "majorComponent";
		else if (context.equalsIgnoreCase("VINEquipmentSearches"))
			return "vinEquipment";
		else if (context.equalsIgnoreCase("WpraSearches"))
			return "wpra";
		else if (context.equalsIgnoreCase("HistoricalClaimSearches"))
			return "historicalClaim";
		else 
			return "claim";
	}

	public String getContext() {
		return context;
	}
	
	public boolean isPageReadOnly() {
		return false;
	}

	public void setContext(String context) {
		this.context = context;
	}

    public boolean isEligibleForExtendedWarrantyPurchase(){
        boolean isEligible = false;
        Map<String, List<Object>> buValues = configParamService.
                getValuesForAllBUs(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY.getName());
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isEligible=true;
                 break;
              }
        }
        return isEligible;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public boolean isDealerEligibleToPerformRMT() {
        boolean isEligible = false;
        if (isLoggedInUserADealer()) {
            Map<String, List<Object>> buValues = configParamService.
                    getValuesForAllBUs(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
            for (String buName : buValues.keySet()) {
                Boolean booleanValue = new Boolean(buValues.get(buName).get(0).toString());
                if (booleanValue) {
                    isEligible = true;
                    break;
                }
            }
        }
        return isEligible;
    }
   
    public boolean isStockClaimAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.STOCK_CLAIM_ALLOWED
                                .getName());
    }
    
	public boolean isD2DAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.D2D_ALLOWED
                                .getName());
    }
	
}
