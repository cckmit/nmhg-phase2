package tavant.twms.domain.rules;

import java.util.Set;

public abstract class NumberType extends AbstractType {

    @Override
    public void setDefaultPredicates() {
        super.setDefaultPredicates();
        Set<Class<? extends Predicate>> predicates = supportedPredicates();
        predicates.add(LessThan.class);
        predicates.add(LessThanOrEquals.class);
        predicates.add(GreaterThan.class);
        predicates.add(GreaterThanOrEquals.class);
        predicates.add(Between.class);
        predicates.add(NotBetween.class);
        predicates.add(IsOneOf.class);
        predicates.add(IsNoneOf.class);        
        predicates.add(IsSet.class);        
        predicates.add(IsNotSet.class);

        addOperatorAlias(Equals.class, "label.operators.equals");
        addOperatorAlias(NotEquals.class, "label.operators.doesNotEqual");
    }
}