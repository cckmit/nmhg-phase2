package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;


@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("OEM")
@Filters({
  @Filter(name="excludeInactive")
})
public class Oem extends ListOfValues {

	public Oem() {
		super();
	}
	 @Override
	public ListOfValuesType getType() {
		return ListOfValuesType.Oem;
	}

}
