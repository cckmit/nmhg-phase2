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
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: May 18, 2007
 * Time: 5:54:16 PM
 */

package tavant.twms.domain.rules;

public class BinaryPredicateAwareMethodInvocation
        implements MethodInvocation {

    private BinaryPredicate binaryPredicate;
    private String methodName;
    private String returnType;

    public BinaryPredicateAwareMethodInvocation(
            BinaryPredicate binaryPredicate, String methodName,
            String returnType) {
        this.binaryPredicate = binaryPredicate;
        this.methodName = methodName;
        this.returnType = returnType;
    }

    public Visitable invokeOn() {
        return binaryPredicate.getLhs();
    }

    public Visitable[] arguments() {
        return new Visitable[] {binaryPredicate.getRhs()};
    }

    public String methodName() {
        return methodName;
    }

    public String returnType() {
        return returnType;
    }

    public boolean isCollection() {
        return false;
    }

    public String getType() {
        return returnType;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
