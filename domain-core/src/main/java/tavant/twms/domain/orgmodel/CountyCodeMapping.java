package tavant.twms.domain.orgmodel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name="COUNTY_CODE_MAPPING")
@Entity
public class CountyCodeMapping {

	@Id
    private Long id;
    
    private String state;
    
    private String countyCode;

    private String countyName;
    
    private String country;
    
    @Transient
    private String codeWithName;
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getCodeWithName() {
		return this.countyCode+"-"+this.countyName;
	}

	public void setCodeWithName(String codeWithName) {
		this.codeWithName = codeWithName;
	}  
}
