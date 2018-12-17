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
 * @author radhakrishnan.j
 */
public interface Visitor {
    public void visit(DomainPredicate visitable);

    public void visit(Equals visitable);

    public void visit(NotEquals visitable);

    public void visit(Not visitable);

    public void visit(LessThan visitable);

    public void visit(LessThanOrEquals visitable);

    public void visit(GreaterThan visitable);

    public void visit(GreaterThanOrEquals visitable);

    public void visit(Or visitable);

    public void visit(Any visitable);

    public void visit(And visitable);

    public void visit(All visitable);

    public void visit(Constant constant);

    public void visit(Constants constants);

    public void visit(IsOneOf visitable);
    
    public void visit(EqualsEnum visitable);
    
    public void visit(NotEqualsEnum visitable);

    public void visit(IsNoneOf visitable);

    public void visit(DomainSpecificVariable visitable);

    public void visit(ForAnyNOf visitable);

    public void visit(ForEachOf visitable);

    public void visit(MethodInvocation visitable);

    public void visit(IsNotSet isNotSet);

    public void visit(IsSet isNotSet);

    public void visit(ForAnyOf forAnyOf);

    public void visit(Addition addition);

    public void visit(IsTrue isTrue);

    public void visit(IsFalse isFalse);

    public void visit(Contains contains);

    public void visit(DoesNotContain doesNotContain);

    public void visit(Between between);

    public void visit(NotBetween notBetween);

    public void visit(StartsWith startsWith);
    
    public void visit(DoesNotStartWith visitable);
    
    public void visit(DoesNotEndWith doesNotEndWith) ;
    
    public void visit(EndsWith endsWith);

    public void visit(IsBefore isBefore);

    public void visit(IsAfter isAfter);

    public void visit(IsOnOrAfter isOnOrAfter);
    
    public void visit(IsOnOrBefore isOnOrBefore);

    public void visit(IsDuringLast isDuringLast);

    public void visit(IsNotDuringLast isNotDuringLast);

    public void visit(IsDuringNext isDuringNext);

    public void visit(IsNotDuringNext isNotDuringNext);

    public void visit(IsWithinLast isWithinLast);

    public void visit(IsNotWithinLast isNotWithinLast);

    public void visit(IsWithinNext isWithinNext);

    public void visit(IsNotWithinNext isNotWithinNext);

    public void visit(DateGreaterBy dateGreaterBy);
    
    public void visit(DateNotGreaterBy dateNotGreaterBy);

    public void visit(DateLesserBy dateLesserBy);
    
    public void visit(DateNotLesserBy dateNotLesserBy);

    public void visit(IsSameAs isSameAs);
}
