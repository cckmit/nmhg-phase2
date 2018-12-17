package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("exactlyGreaterBy")
public class DateExactlyGreaterBy extends DateGreaterBy {

	public DateExactlyGreaterBy() {		
	}

	@Override
	public String getDomainTerm() {
		return "label.operators.isExactlyGreaterBy";
	}
	
	@Override
	public int getComparisionType() {
		return AbstractDateDurationPredicate.COMPARE_TYPE_EXACTLY;
	}

}
