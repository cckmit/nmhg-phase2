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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */@XStreamAlias("OneToOneAssociation")
public class OneToOneAssociation implements Field {
    private String domainName;

    private String expression;

    private DomainType ofType;

    public OneToOneAssociation(String domainName, String expression, DomainType field) {
        super();
        this.domainName = domainName;
        this.expression = expression;
        this.ofType = field;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getExpression() {
        return expression;
    }

    public String getType() {
        return ofType.getName();
    }

    public DomainType getOfType() {
        return ofType;
    }
    
    public Set<Field> getFields() {
        return ofType.getFields();
    }
}
