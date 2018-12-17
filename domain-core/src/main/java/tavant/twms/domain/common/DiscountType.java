package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DISCOUNTTYPE")
@Filters({ @Filter(name = "excludeInactive") })
public class DiscountType extends ListOfValues {

	public DiscountType() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.DiscountType;
	}
}
