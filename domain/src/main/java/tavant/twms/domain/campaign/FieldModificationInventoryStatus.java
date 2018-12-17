package tavant.twms.domain.campaign;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.ListOfValuesType;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("FieldModificationInventoryStatus")
@Filters({
  @Filter(name="excludeInactive")
})
public class FieldModificationInventoryStatus extends ListOfValues  {
	
	public FieldModificationInventoryStatus() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.FieldModificationInventoryStatus;
	}

}
