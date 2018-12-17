/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author mritunjay.kumar
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "USER_ATTR_VALUE")
@SuppressWarnings("serial")
public class UserAttributeValue extends AttributeValue implements Serializable{

	public UserAttributeValue() {

	}

	public UserAttributeValue(Attribute attribute, String value) {
		super(attribute, value);
	}
}
