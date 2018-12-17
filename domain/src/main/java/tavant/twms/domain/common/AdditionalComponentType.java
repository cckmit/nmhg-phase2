package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.ListOfValuesType;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("ADDITIONALCOMPONENTTYPE")
@Filters({ @Filter(name = "excludeInactive") })
public class AdditionalComponentType extends ListOfValues {
	public AdditionalComponentType() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.AdditionalComponentsType;

	}
}