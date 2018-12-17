package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;


@Entity
@SuppressWarnings("serial")
@Inheritance(strategy=InheritanceType.JOINED)
public class NationalAccount extends ServiceProvider implements Serializable{
	
	private String nationalAccountNumber;

	public String getNationalAccountNumber() {
		return nationalAccountNumber;
	}

	public void setNationalAccountNumber(String nationalAccountNumber) {
		this.nationalAccountNumber = nationalAccountNumber;
	}
	
	@Transient
	public String getAddressBookType(){
		return AddressBookType.NATIONALACCOUNT.getType();
	}
}
