package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import tavant.twms.security.AuditableColumns;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("REQUESTINFOFROMUSER")
@Filters({
  @Filter(name="excludeInactive")
})
public class RequestInfoFromUser  extends ListOfValues  implements AuditableColumns{
	public RequestInfoFromUser() {
		super();
	}
	
	public ListOfValuesType getType() {
		return ListOfValuesType.RequestInfoFromUser;
	}
}
