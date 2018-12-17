/**
 * 
 */
package tavant.twms.domain.claim;

import org.springframework.util.StringUtils;

import tavant.twms.domain.policy.Customer;
import tavant.twms.infra.ListCriteria;

/**
 * @author pradyot.rout
 * 
 */
public class MultiInventorySearch extends ListCriteria {

	private String inventoryType;

	private String serialNumber;

	private String modelNumber;

	private Customer customer;
	
	private boolean customerSelected;
	
	private String dealerNumber;
	
	private String yearOfShipment;
	
	public String getInventoryType() {
		return this.inventoryType;
	}

	public void setInventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		if (StringUtils.hasText(serialNumber)) {
			this.serialNumber = serialNumber;
		}
	}

	public String getModelNumber() {
		return this.modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		if (StringUtils.hasText(modelNumber)) {
			this.modelNumber = modelNumber;
		}
	}
	
	public Customer getCustomer() {
		if(this.customer != null && !StringUtils.hasText(this.customer.getCompanyName()) &&
                    !StringUtils.hasText(this.customer.getCorporateName())) {
				this.customer=null;
		}
        
        return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isCustomerSelected() {
		return customerSelected;
	}

	public void setCustomerSelected(boolean customerSelected) {
		this.customerSelected = customerSelected;
	}

	public String getYearOfShipment() {
		return yearOfShipment;
	}

	public void setYearOfShipment(String yearOfShipment) {
		if (StringUtils.hasText(yearOfShipment)) {
			this.yearOfShipment = yearOfShipment;
		}
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		if (StringUtils.hasText(dealerNumber)) {
			this.dealerNumber = dealerNumber;
		}
	}

}
