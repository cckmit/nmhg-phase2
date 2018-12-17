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

import org.springframework.util.Assert;


/**
 * @author radhakrishnan.j
 */
public class Addition implements BinaryPredicate, Value {

    private Value lhs;
    private Value rhs;

    public Addition() {
        super();
    }

    public Addition(Value lhs, Value rhs) {
        super();
        this.lhs = lhs;
        this.rhs = rhs;
        Assert.notNull(lhs, "lhs is null");
        Assert.notNull(rhs, "rhs is null");
        Assert.isTrue(lhs.getType().equals(rhs.getType()), "Lhs and rhs are not of same type");
    }

    public Value getLhs() {
        return lhs;
    }

    public void setLhs(Value lhs) {
        this.lhs = lhs;
    }

    public Value getRhs() {
        return rhs;
    }

    public void setRhs(Value rhs) {
        this.rhs = rhs;
    }

    public String getType() {
        return lhs.getType();
    }

    public boolean isCollection() {
        return false;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getDomainTerm() {
        return "add";
    }
    
    public Predicate getInverse() {
		throw new UnsupportedOperationException("Method getInverse() is not supported for "
				+this.getClass().getName());
	}

}
