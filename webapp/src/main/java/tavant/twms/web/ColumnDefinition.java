/*
 *   Copyright (c)2006 Tavant Technologies
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
/**
 * 
 */
package tavant.twms.web;

import org.springframework.core.style.ToStringCreator;

/**
 * 
 * @author Radhakrishnan
 */
//TODO: Identify an appropriate package.
public class ColumnDefinition {
    private String displayName;

    private String name;

    private String type;

    private String expression;

    private String align;

    public ColumnDefinition(String displayName, String name, String type, String expression, String align) {
        this.displayName = displayName;
        this.name = name;
        this.type = type;
        this.expression = expression;
        this.align = align;
    }

    public ColumnDefinition(String displayName, String name, String type, String expression) {
        this(displayName, name, type, expression, "left");
    }

    public ColumnDefinition(String displayName, String name, String type) {
        this(displayName,name,type,name);
    }
    
    public ColumnDefinition(String displayName, String name) {
        this(displayName,name,"String");
    }    
    
    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    public String getAlign() {
        return align;
    }
    
    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("displayName", displayName)
            .append("name ", name)
            .append("type", type)
            .append("expression", expression)
            .append("align", align)
            .toString();
    }
}