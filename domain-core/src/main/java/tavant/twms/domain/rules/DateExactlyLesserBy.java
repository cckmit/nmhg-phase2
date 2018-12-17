package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@XStreamAlias("exactlyLesserBy")
public class DateExactlyLesserBy extends DateLesserBy {

	public DateExactlyLesserBy() {
    }
	
	@Override
    public String getDomainTerm() {
        return "label.operators.isExactlyLesserBy";
    }
	
	@Override
	public int getComaprisionType() {
    	return AbstractDateDurationPredicate.COMPARE_TYPE_EXACTLY;
    }
}
