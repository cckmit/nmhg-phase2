package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

@Entity
@SuppressWarnings("serial")
@Inheritance(strategy=InheritanceType.JOINED)
public class DirectCustomer extends ServiceProvider implements Serializable {

	private String  directCustomerNumber;

	public String getDirectCustomerNumber() {
		return directCustomerNumber;
	}

	public void setDirectCustomerNumber(String directCustomerNumber) {
		this.directCustomerNumber = directCustomerNumber;
	}

	@Transient
	public String getAddressBookType(){
		return AddressBookType.DIRECTCUSTOMER.getType();
	}
	
}
