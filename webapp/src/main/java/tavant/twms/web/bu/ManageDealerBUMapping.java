package tavant.twms.web.bu;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.security.SecurityHelper;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ManageDealerBUMapping extends ActionSupport {
	
    private static Logger logger = LogManager.getLogger(ManageBUConfiguration.class);
    private OrgService orgService;
    private BusinessUnitService businessUnitService;
    private String dealerNumber;
    private ServiceProvider dealer;
    private List<BusinessUnit> buListSelected = new ArrayList<BusinessUnit>();
    private SortedSet<BusinessUnit> allBusinessUnits = new TreeSet<BusinessUnit>();
    private SecurityHelper securityHelper;

    public String show() {
        return SUCCESS;
    }

    public String search() {
        if (dealerNumber != null && !dealerNumber.equals("")) {
            allBusinessUnits = securityHelper.getLoggedInUser().getBusinessUnits(); 
            dealer = orgService.findServiceProviderByNumberWithOutBU(dealerNumber);
            if (dealer == null) {
                addActionError(getText("error.resultsNotFound"));
                return INPUT;
            }
        } else {
            addActionError(getText("error.searchStrEmpty"));
            return INPUT;
        }
        addActionError(getText("error.buNotChecked"));
        return SUCCESS;
    }

    public String update() {
    	TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
    	if (dealer.getBusinessUnits() != null){
    		businessUnitMapping.addAll(dealer.getBusinessUnits());
    	}
    	for (BusinessUnit buToBeMapped : buListSelected) {
    		if (buToBeMapped !=null) {
    			businessUnitMapping.add(buToBeMapped);                		
    		}
    	}
    	if (!businessUnitMapping.isEmpty()){
    		dealer.setBusinessUnits(businessUnitMapping);
    		orgService.updateDealership(dealer);
    		addActionMessage(getText("message.update.dealerBuMapping"));
    		return SUCCESS;
    	}
    	return INPUT;
    }

	public boolean isBUAlreadyMapped(BusinessUnit businessUnit) {
		SortedSet<BusinessUnit> mappedBUs = dealer.getBusinessUnits();
		for (BusinessUnit mappedBU : mappedBUs) {
			if (mappedBU.getName().equals(businessUnit.getName())) {
				return true;
			}
		}
		return false;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		ManageDealerBUMapping.logger = logger;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public BusinessUnitService getBusinessUnitService() {
		return businessUnitService;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	public ServiceProvider getDealer() {
		return dealer;
	}

	public void setDealer(ServiceProvider dealer) {
		this.dealer = dealer;
	}

	public List<BusinessUnit> getBuListSelected() {
		return buListSelected;
	}

	public void setBuListSelected(List<BusinessUnit> buListSelected) {
		this.buListSelected = buListSelected;
	}

	public SortedSet<BusinessUnit> getAllBusinessUnits() {
		return allBusinessUnits;
	}

	public void setAllBusinessUnits(SortedSet<BusinessUnit> allBusinessUnits) {
		this.allBusinessUnits = allBusinessUnits;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
