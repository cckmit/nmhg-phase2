package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("UNITDOCUMENTTYPE")
@Filters({ @Filter(name = "excludeInactive") })
public class UnitDocumentType extends ListOfValues {

	public UnitDocumentType() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.UnitDocumentType;
	}
}
