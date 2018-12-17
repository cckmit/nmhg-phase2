package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@XStreamAlias("atmostLesserBy")
public class DateAtmostLesserBy extends DateLesserBy {

	public DateAtmostLesserBy() {
    }
	
	@Override
    public String getDomainTerm() {
        return "label.operators.isAtmostLesserBy";
    }
	
	@Override
	public int getComaprisionType() {
    	return COMPARE_TYPE_ATMOST;
    }
}
