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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class IsOnOrAfter implements BinaryPredicate {
    
    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    protected Value lhs;

    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    protected Value rhs;

    public IsOnOrAfter() {
        super();
    }

    public IsOnOrAfter(Value oneValue, Value anotherValue) {
        Assert.notNull(oneValue, "cannot be null");
        Assert.notNull(anotherValue, "cannot be null");
        Assert.isTrue(oneValue.getType().equals(anotherValue.getType()), " (lhs="
                + oneValue.getType() + ", rhs=" + anotherValue.getType()
                + ") type incompatible values");
        
        Assert.isTrue(oneValue.getType().equals(Type.DATE)," on or after check applies ONLY to date types");
        lhs = oneValue;
        rhs = anotherValue;
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
        if(  lhs!=null && rhs!=null && ! ( lhs.getType().equals(rhs.getType()) && lhs.getType().equals(Type.DATE)) ) {
            validationContext.addError(" both (lhs="+lhs+",rhs="+rhs+") need to be of type DATE");
        }
    }

    public String getDomainTerm() {
        return "label.operators.isOnOrAfter";
    }    
    
    public Predicate getInverse() {
		return new IsBefore(lhs,rhs);
	}

}
