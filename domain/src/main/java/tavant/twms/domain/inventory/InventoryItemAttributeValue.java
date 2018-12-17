/**
 * 
 */
package tavant.twms.domain.inventory;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import tavant.twms.domain.orgmodel.Attribute;
import tavant.twms.domain.orgmodel.AttributeValue;

/**
 * @author mritunjay.kumar
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "INV_ITEM_ATTR_VALUE")
public class InventoryItemAttributeValue extends AttributeValue {

	public InventoryItemAttributeValue() {

	}

	public InventoryItemAttributeValue(Attribute attribute, String value) {
		super(attribute, value);
	}
}
