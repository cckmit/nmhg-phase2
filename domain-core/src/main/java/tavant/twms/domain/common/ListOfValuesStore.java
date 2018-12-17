package tavant.twms.domain.common;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class ListOfValuesStore {

	@Id
	private ListOfValuesStorePK listOfValuesStorePK;
	
	private String description;

	public ListOfValuesStorePK getListOfValuesStorePK() {
		return listOfValuesStorePK;
	}

	public void setListOfValuesStorePK(ListOfValuesStorePK listOfValuesStorePK) {
		this.listOfValuesStorePK = listOfValuesStorePK;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
