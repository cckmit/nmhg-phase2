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
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * @author radhakrishnan.j
 * 
 */
@XStreamAlias("LongType")
public class LongType extends NumberType implements LiteralSupport {
    Set<Class<? extends Predicate>> predicates;
    
    @Override
    public Set<Class<? extends Predicate>> supportedPredicates() {
        return this.predicates ;
    }
    


    private static final Pattern LONG_PATTERN =
            Pattern.compile("^-?[1-9][0-9]*$");

    public LongType() {
        predicates = new HashSet<Class<? extends Predicate>>();
        setDefaultPredicates();
    }
    
    public String getEvaluableExpression(String literal) {
        return literal;
    }

    public Object getJavaObject(String literal) {
        return Long.parseLong(literal);
    }

    public String getName() {
        return LONG;
    }

   
    public String getLiteralForDefaultValue() {
        return "0L";
    }

    public boolean supportsLiteral() {
        return true;
    }
    
    public boolean isLiteralValid(String literal) {
        boolean isValid = LONG_PATTERN.matcher(literal).matches();

        if(!isValid) {
            logger.error(" '"+literal+"' is not a valid '"+getName()+"' literal");
        }

        return isValid;
    }

    public void setPredicates(Set<Class<? extends Predicate>> predicates) {
        this.predicates = predicates;
    }
    
    
}
