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


import java.util.Set;

/**
 * @author radhakrishnan.j
 * 
 */
public interface Type {
	
	String ENUM = "enum";
	
    String STRING = "string";

    String DATE = "date";

    String INTEGER = "integer";

    String LONG = "long";

    String BIGDECIMAL = "bigdecimal";

    String MONEY = "money";

    String BOOLEAN = "boolean";

    String VOID = "void";
    
    String DOUBLE = "double";

    public String getName();

    public boolean supportsLiteral();

    public boolean isComparableWith(Type anotherType);

    public Set<Class<? extends Predicate>> supportedPredicates();

    public void setDefaultPredicates();

    public String getAliasIfAnyForOperator(Class operator);
}
