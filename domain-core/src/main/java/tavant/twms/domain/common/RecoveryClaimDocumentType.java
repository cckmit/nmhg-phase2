package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.PolymorphismType;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("RECOVERYDOCUMENTTYPE")
@Filters({ @Filter(name = "excludeInactive") })
public class RecoveryClaimDocumentType extends DocumentType {

	public RecoveryClaimDocumentType() {
		super();
	}

	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.RecoveryClaimDocumentType;
	}
}