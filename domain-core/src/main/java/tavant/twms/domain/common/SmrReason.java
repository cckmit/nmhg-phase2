/**
 * 
 */
package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SMRREASON")
@Filters({
  @Filter(name="excludeInactive")
})
public class SmrReason extends ListOfValues {
	public SmrReason() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.SmrReason;
	}

}
