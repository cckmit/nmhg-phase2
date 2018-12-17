package tavant.twms.domain.common;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.ListOfValuesType;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SUPPLIERS")
@Filters({
  @Filter(name="excludeInactive")
})
public class Suppliers extends ListOfValues {
	public Suppliers() {
		super();
	}
	
	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.Suppliers;

	}
}

