package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;


@Entity
@SuppressWarnings("serial")
@Inheritance(strategy=InheritanceType.JOINED)
public class InterCompany extends ServiceProvider implements Serializable{
   
	
	private String interCompanyNumber;

	public String getInterCompanyNumber() {
		return interCompanyNumber;
	}

	public void setInterCompanyNumber(String interCompanyNumber) {
		this.interCompanyNumber = interCompanyNumber;
	}
	
	@Transient
	public String getAddressBookType(){
		return AddressBookType.INTERCOMPANY.getType();
	}

}
