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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author radhakrishnan.j
 *
 */
@XStreamImplicitCollection(value="literals",item="constant")
@XStreamAlias("constants")
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Constants implements Value,ExpressionToken {
    private List<String> literals = new ArrayList<String>();
    
    private String type;
    
    public Constants() {
    }
    
    public Constants(Collection<String> values,String type) {
        this.literals.addAll(values);
        this.type = type;
    }
    
    public String getType() {
        return type;
    }

    public boolean isCollection() {
        return true;
    }

    public String getToken() {
        int i = 0;
        StringBuffer buf = new StringBuffer();
        
        for(String eachLiteral : literals ) {
            if( i == 0 ) {
                i++;
            } else {
                buf.append(",");
            }
            
            buf.append(literalType().getEvaluableExpression(eachLiteral));
        }
        buf.insert(0, '{');
        buf.append('}');
        return buf.toString();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void validate(ValidationContext validationContext) {
        for(String eachLiteral : literals ) {
            if( !literalType().isLiteralValid(eachLiteral) ) {
                validationContext.addError(" incompatible literal ["+eachLiteral+"] for type ["+type+"]");
            }
        }
    }

    @Override
    public String toString() {
        return literals.toString();
    }  
    
    private LiteralSupport literalType() {
        return ((LiteralSupport)TypeSystem.getInstance().getType(type));
    }

    public List<String> getLiterals() {
        return literals;
    }

    public void setLiterals(List<String> literals) {
        this.literals = literals;
    }

    public void setType(String type) {
        this.type = type;
    }

    
}
