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

import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("StringType")
public class StringType extends AbstractType implements LiteralSupport {
    Set<Class<? extends Predicate>> predicates;
    
    @Override
    public Set<Class<? extends Predicate>> supportedPredicates() {
        return this.predicates ;
    }
    
    public StringType() {
        predicates = new HashSet<Class<? extends Predicate>>();
        setDefaultPredicates();
        predicates.add(IsOneOf.class);
        predicates.add(IsNoneOf.class);
        predicates.add(IsNotSet.class);
        predicates.add(StartsWith.class);
        predicates.add(DoesNotStartWith.class);
        predicates.add(Contains.class);
        predicates.add(DoesNotContain.class);
        predicates.add(EndsWith.class);
        predicates.add(DoesNotEndWith.class);

        
        //TODO the is exactly and the other three are being picked up from here
        addOperatorAlias(Equals.class, "label.operators.isExactly");
        addOperatorAlias(IsNotSet.class, "label.operators.isEmpty");
        addOperatorAlias(IsSet.class, "label.operators.isNotEmpty");
    }
    public Object getJavaObject(String literal) {
        return literal;
    }

    public String getName() {
        return Type.STRING;
    }

   
    public String getLiteralForDefaultValue() {
        return "\"\"";
    }

    public String getEvaluableExpression(String literal) {
        return "\""+literal+"\"";
    }

    public boolean supportsLiteral() {
        return true;
    }
    
    public boolean isLiteralValid(String literal) {
        try {
            getJavaObject(literal);
            return true;
        } catch (RuntimeException e) {
            logger.error(" '"+literal+"' is not a valid '"+getName()+"' literal");
        }
        return false;
    }

    public void setPredicates(Set<Class<? extends Predicate>> predicates) {
        this.predicates = predicates;
    }    
    
    
}