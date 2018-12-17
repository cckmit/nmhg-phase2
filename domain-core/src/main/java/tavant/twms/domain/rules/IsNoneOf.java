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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 *
 */
@XStreamAlias("isNoneOf")
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class IsNoneOf implements BinaryPredicate,ExpressionToken {
    protected Value lhs;

    protected Value rhs;

    public IsNoneOf(Value oneValue, Value anotherValue) {
        Assert.notNull(oneValue, "cannot be null");
        Assert.notNull(anotherValue, "cannot be null");
        Assert.isTrue(oneValue.getType().equals(anotherValue.getType()), " (lhs="
                + oneValue.getType() + ", rhs=" + anotherValue.getType()
                + ") type incompatible values");
        Assert.isTrue(!oneValue.isCollection(),"LHS cannot be a collection value");
        Assert.isTrue(anotherValue.isCollection(),"RHS has to be a collection value");
        this.lhs = oneValue;
        this.rhs = anotherValue;
    }
    
    
    public IsNoneOf() {
    }


    public String getToken() {
        return " not in ";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


    public Value getLhs() {
        return lhs;
    }


    public Value getRhs() {
        return rhs;
    }
    
    
    public void validate(ValidationContext validationContext) {
        if( lhs==null ) {
            validationContext.addError("lhs is null");
        }
        if( rhs==null ) {
            validationContext.addError("rhs is null");            
        }
        if(  lhs!=null && rhs!=null && ! ( lhs.getType().equals(rhs.getType()) ) ) {
            validationContext.addError(" both (lhs="+lhs+",rhs="+rhs+") need to be of same type");
        }
        
    }

    @Override
    public String toString() {
        StringBuffer text = new StringBuffer(50);
        text.append("(");
        text.append(lhs.toString());
        text.append(" is not among  [");
        text.append(rhs.toString());
        text.append("])");
        return super.toString();
    }


    public String getDomainTerm() {
        return "label.operators.isNotOneOf";
    }
    
    public Predicate getInverse() {
		return new IsOneOf(lhs,rhs);
	}
    
    
}


