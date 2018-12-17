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

import java.util.Iterator;
import java.util.List;


/**
 * @author radhakrishnan.j
 */
public class EmptyVisitor implements Visitor {

    public void visit(And visitable) {
        visitBinaryPredicate(visitable);
    }

    public void visit(Constant constant) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(Constants constants) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(DomainSpecificVariable visitable) {
    	//visitable.accept(this);
    }

    public void visit(Equals visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(ForAnyNOf visitable) {
        // todo-temp
    }

    public void visit(GreaterThan visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(GreaterThanOrEquals visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(IsNoneOf visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(IsOneOf visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(LessThan visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(LessThanOrEquals visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(Not visitable) {
        // todo
    }

    public void visit(NotEquals visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(Or visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(DomainPredicate visitable) {
        visitable.getPredicate().accept(this);
    }

    public void visit(IsNotSet isNotSet) {
    	isNotSet.getOperand().accept(this);
    }

    public void visit(ForAnyOf forAnyOf) {
    	DomainSpecificVariable collectionValuedVariable = forAnyOf
		.getCollectionValuedVariable();
    	collectionValuedVariable.accept(this);
    	forAnyOf.getConditionToBeSatisfied().accept(this);
   
    }

    public void visit(All visitable) {
    	List<Predicate> predicates = visitable.getPredicates();
		for (Iterator<Predicate> iter = predicates.iterator(); iter.hasNext();) {
			iter.next().accept(this);
		}
    }

    public void visit(Any visitable) {
    	List<Predicate> predicates = visitable.getPredicates();
		for (Iterator<Predicate> iter = predicates.iterator(); iter.hasNext();) {
			iter.next().accept(this);
		}
    }

    public void visit(ForEachOf visitable) {
    	DomainSpecificVariable collectionValuedVariable = visitable
		.getCollectionValuedVariable();
    	collectionValuedVariable.accept(this);
    	visitable.getConditionToBeSatisfied().accept(this);

    }

    public void visit(MethodInvocation visitable) {
    	//todo-verify this
    	if(visitable.arguments()!=null && visitable.arguments().length>=1)
    	{
    		DomainSpecificVariable invokeOn = (DomainSpecificVariable)visitable.arguments()[0];
    		invokeOn.accept(this);
    	}
    }

    public void visit(IsSet isNotSet) {
        isNotSet.getOperand().accept(this);
    }

    public void visit(Addition addition) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(IsTrue isTrue) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(IsFalse isFalse) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(Contains contains) {
        visitBinaryPredicate(contains);
    }

    public void visit(DoesNotContain doesNotContain) {
    	visitBinaryPredicate(doesNotContain);
    }

    public void visit(Between between) {
    	between.getLhs().accept(this);
    }

    public void visit(NotBetween notBetween) {
    	notBetween.getLhs().accept(this);
    }

    public void visit(DoesNotEndWith doesNotEndWith) {
    	visitBinaryPredicate(doesNotEndWith);
    }

    public void visit(DoesNotStartWith visitable) {
    	visitBinaryPredicate(visitable);
    }

    public void visit(EndsWith endsWith) {
    	visitBinaryPredicate(endsWith);
    }

    public void visit(IsBefore isBefore) {
    	visitBinaryPredicate(isBefore);
    }

    public void visit(IsAfter isAfter) {
    	visitBinaryPredicate(isAfter);
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(IsOnOrAfter isOnOrAfter) {
    	visitBinaryPredicate(isOnOrAfter);
    }
    
    public void visit(IsOnOrBefore isOnOrBefore){
    	visitBinaryPredicate(isOnOrBefore);
    }

    public void visit(IsDuringLast isDuringLast) {
    	visitBinaryPredicate(isDuringLast);
    }

    public void visit(IsNotDuringLast isNotDuringLast) {
    	visitBinaryPredicate(isNotDuringLast);
    }

    public void visit(IsDuringNext isDuringNext) {
    	visitBinaryPredicate(isDuringNext);
    }

    public void visit(IsNotDuringNext isNotDuringNext) {
    	visitBinaryPredicate(isNotDuringNext);
    }

    public void visit(IsWithinLast isWithinLast) {
    	visitBinaryPredicate(isWithinLast);
    }

    public void visit(IsNotWithinLast isNotWithinLast) {
    	visitBinaryPredicate(isNotWithinLast);
    }

    public void visit(IsWithinNext isWithinNext) {
    	visitBinaryPredicate(isWithinNext);
    }

    public void visit(IsNotWithinNext isNotWithinNext) {
    	visitBinaryPredicate(isNotWithinNext);
    }

    public void visit(StartsWith startsWith) {
    	visitBinaryPredicate(startsWith);
    }
    
    private void visitBinaryPredicate(BinaryPredicate visitable) {

		visitable.getLhs().accept(this);
		visitable.getRhs().accept(this);
	}

    public void visit(DateGreaterBy dateGreatererBy) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(DateLesserBy dateLesserBy) {
        // Empty implementation. Child classes can selectively override.
    }

    public void visit(IsSameAs isSameAs) {
        // Empty implementation. Child classes can selectively override.
        
    }

	public void visit(EqualsEnum visitable) {
		// Empty implementation. Child classes can selectively override.
	}

	public void visit(NotEqualsEnum visitable) {
		// Empty implementation. Child classes can selectively override.
	}

	public void visit(DateNotGreaterBy dateNotGreaterBy) {
		// Empty implementation. Child classes can selectively override.
		
	}

	public void visit(DateNotLesserBy dateNotLesserBy) {
		// Empty implementation. Child classes can selectively override.
		
	}

}
