package tavant.twms.domain.common;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.security.AuditableColumns;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("REPORTTYPE")
@Filters({
  @Filter(name="excludeInactive")
})
public class ReportType extends ListOfValues implements AuditableColumns , BusinessUnitAware {

	public ReportType(){
		super();
	}
	
	@Override
	public ListOfValuesType getType() {
		return ListOfValuesType.ReportType;
	}

}
