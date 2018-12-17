package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("LABORRATETYPE")
@Filters({
  @Filter(name="excludeInactive")
})
public class LaborRateType extends ListOfValues {

	public LaborRateType() {
        super();
		}
	
	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.LaborRateType;
	}

}
