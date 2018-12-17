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


/**
 * 
 * @author radhakrishnan.j
 *
 */
public class CompareTo implements BinaryPredicate, MethodInvocation {
    
    private Value lhs;
    private Value rhs;
    
    public CompareTo(Value lhs, Value rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void validate(ValidationContext validationContext) {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getDomainTerm() {
        return " compare to ";
    }

    public Value getLhs() {
        return lhs;
    }

    public Value getRhs() {
        return rhs;
    }

    public boolean isCollection() {
        return false;
    }

    public String getType() {
        return Type.INTEGER;
    }

    public Visitable invokeOn() {
        return lhs;
    }

    public Visitable[] arguments() {
        return new Visitable[] {rhs};
    }

    public String methodName() {
        return "compareTo";
    }

    public String returnType() {
        return Type.INTEGER;
    }
    
    public Predicate getInverse() {
    	//todo
		throw new UnsupportedOperationException("Method getInverse() is not supported for "
				+this.getClass().getName());
	}
}