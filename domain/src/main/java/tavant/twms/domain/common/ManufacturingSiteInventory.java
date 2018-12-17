package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("MANUFACTURINGSITEINVENTORY")
public class ManufacturingSiteInventory extends ListOfValues{

	public ManufacturingSiteInventory()
	{
		super();
	}
	
	@Override
	public ListOfValuesType getType() {
		// TODO Auto-generated method stub
		return ListOfValuesType.ManufacturingSiteInventory;
	}

}
