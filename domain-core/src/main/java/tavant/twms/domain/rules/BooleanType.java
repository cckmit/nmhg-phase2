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

@XStreamAlias("BooleanType")
public class BooleanType extends AbstractType implements LiteralSupport {
    Set<Class<? extends Predicate>> predicates;
    
    @Override
    public Set<Class<? extends Predicate>> supportedPredicates() {
        return this.predicates ;
    }
    
    public BooleanType() {
        predicates = new HashSet<Class<? extends Predicate>>();
        predicates.add(IsTrue.class);
        predicates.add(IsFalse.class);
    }
    
    public boolean supportsLiteral() {
        return true;
    }

    public Object getJavaObject(String literal) {
        return Boolean.valueOf(literal);
    }

    public String getName() {
        return Type.BOOLEAN;
    }

    public String getLiteralForDefaultValue() {
        return "false";
    }

    public String getEvaluableExpression(String literal) {
        return literal;
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