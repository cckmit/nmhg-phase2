package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("atmostGreaterBy")
public class DateAtmostGreaterBy extends DateGreaterBy {

	public DateAtmostGreaterBy() {		
	}

	@Override
	public String getDomainTerm() {
		return "label.operators.isAtmostGreaterBy";
	}
	
	@Override
	public int getComparisionType() {
		return AbstractDateDurationPredicate.COMPARE_TYPE_ATMOST;
	}

}
