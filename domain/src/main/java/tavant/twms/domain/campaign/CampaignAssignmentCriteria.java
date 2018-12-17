package tavant.twms.domain.campaign;

import java.util.List;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.ListCriteria;

public class CampaignAssignmentCriteria extends ListCriteria {

	private ServiceProvider dealer;
	public List<Organization> dealersList;
	
	public ServiceProvider getDealer() {
		return dealer;
	}

	public void setDealer(ServiceProvider dealer) {
		this.dealer = dealer;
	}
	

	public List<Organization> getDealersList() {
		return dealersList;
	}

	public void setDealersList(List<Organization> dealersList) {
		this.dealersList = dealersList;
	}
	
	
}
