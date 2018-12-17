package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@XStreamAlias("atleastLesserBy")
public class DateAtleastLesserBy extends DateLesserBy {

	public DateAtleastLesserBy() {
    }
	
	@Override
    public String getDomainTerm() {
        return "label.operators.isAtleastLesserBy";
    }
	
	@Override
	public int getComaprisionType() {
    	return AbstractDateDurationPredicate.COMPARE_TYPE_ATLEAST;
    }
}
