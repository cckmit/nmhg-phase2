package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.springframework.core.style.ToStringCreator;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@SuppressWarnings("serial")
public class ThirdParty extends ServiceProvider implements Serializable 
{
	
	private String thirdPartyNumber;
	
		
	public String getThirdPartyNumber() {
		return thirdPartyNumber;
	}

	public void setThirdPartyNumber(String thirdPartyNumber) {
		this.thirdPartyNumber = thirdPartyNumber;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("third party number", thirdPartyNumber)
				.toString();
	}
	
}
