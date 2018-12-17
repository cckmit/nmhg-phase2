package tavant.twms.domain.rules;

import java.util.HashSet;
import java.util.Set;

public class EnumType extends StringType {
	
	@Override
    public Set<Class<? extends Predicate>> supportedPredicates() {
        return this.predicates ;
    }
    
    public EnumType() {
        predicates = new HashSet<Class<? extends Predicate>>();
        predicates.add(EqualsEnum.class);
        predicates.add(NotEqualsEnum.class);
    }

    public String getName() {
        return ENUM;
    }
    
}
