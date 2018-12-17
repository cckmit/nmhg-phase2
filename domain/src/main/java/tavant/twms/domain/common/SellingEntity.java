package tavant.twms.domain.common;

import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.bu.BusinessUnitAware;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 19, 2009
 * Time: 12:49:00 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SELLINGENTITY")
@Filters({
  @Filter(name="excludeInactive")
})
public class SellingEntity extends ListOfValues implements AuditableColumns , BusinessUnitAware {

	public SellingEntity() {
		super();
	}

	public ListOfValuesType getType() {
		return ListOfValuesType.SellingEntity;
	}
}
