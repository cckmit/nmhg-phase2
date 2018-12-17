/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


abstract class AbstractType implements Type {
    protected static Logger logger  = LogManager.getLogger(AbstractType.class);

    private Map<Class, String> operatorAliases = new HashMap<Class, String>(5);

    public boolean isComparableWith(Type anotherType) {
        return equals(anotherType);
    }

    public abstract Set<Class<? extends Predicate>> supportedPredicates();

    public void setDefaultPredicates() {
        Set<Class<? extends Predicate>> predicates = supportedPredicates();
        predicates.add(Equals.class);
        predicates.add(NotEquals.class);
        predicates.add(IsNotSet.class);
        predicates.add(IsSet.class);
    }

    protected void addOperatorAlias(Class operator, String alias) {
        operatorAliases.put(operator,  alias);
    }

    public String getAliasIfAnyForOperator(Class operator) {
        return operatorAliases.get(operator);
    }
}