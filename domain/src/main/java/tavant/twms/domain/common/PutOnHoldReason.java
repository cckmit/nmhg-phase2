package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import tavant.twms.security.AuditableColumns;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("PUTONHOLDREASON")
@Filters({
  @Filter(name="excludeInactive")
})
public class PutOnHoldReason extends ListOfValues  implements AuditableColumns{
	public PutOnHoldReason() {
		super();
	}
	
	public ListOfValuesType getType() {
		return ListOfValuesType.PutOnHoldReason;
	}
}
