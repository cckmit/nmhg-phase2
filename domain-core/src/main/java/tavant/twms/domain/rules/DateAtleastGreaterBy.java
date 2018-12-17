package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("atleastGreaterBy")
public class DateAtleastGreaterBy extends DateGreaterBy {

	public DateAtleastGreaterBy() {		
	}

	@Override
	public String getDomainTerm() {
		return "label.operators.isAtleastGreaterBy";
	}
	
	@Override
	public int getComparisionType() {
		return AbstractDateDurationPredicate.COMPARE_TYPE_ATLEAST;
	}

}
