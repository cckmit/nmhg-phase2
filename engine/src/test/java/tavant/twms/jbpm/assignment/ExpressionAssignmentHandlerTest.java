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
package tavant.twms.jbpm.assignment;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.orgmodel.User;

public class ExpressionAssignmentHandlerTest extends MockObjectTestCase {

    ExpressionAssignmentHandler fixture;

    Mock assignableMock = mock(Assignable.class);

    Mock executionContextMock = mock(ExecutionContext.class, new Class[] { Token.class },
            new Object[] { new Token() });

    Claim claim;

    public void setUp() {
        fixture = new ExpressionAssignmentHandler();
        claim = createDummyClaim();
    }

    private Claim createDummyClaim() {
        Claim claim = new MachineClaim();
        User dealer = new User();
        dealer.setName("bishop");
        claim.setFiledBy(dealer);
        return claim;
    }

    public void testAssignToUser() throws Exception {

        assignableMock.expects(once()).method("setActorId").with(eq("bishop"));

        fixture.setExpression("actor=bishop");
        fixture.assign((Assignable) assignableMock.proxy(), (ExecutionContext) executionContextMock.proxy());
    }

    public void testAssignToRole() throws Exception {

        assignableMock.expects(once()).method("setPooledActors").with(arrayContaining("dsm"));

        fixture.setExpression("pooledActor=dsm");
        fixture.assign((Assignable) assignableMock.proxy(), (ExecutionContext) executionContextMock.proxy());
    }

    public void testAssignmentBasedOnProcessVariable() throws Exception {
        executionContextMock.expects(once()).method("getVariable").with(eq("claim")).will(returnValue(claim));

        assignableMock.expects(once()).method("setActorId").with(eq("bishop"));

        fixture.setExpression("actor=ognl{claim.filedBy.name}");
        fixture.assign((Assignable) assignableMock.proxy(), (ExecutionContext) executionContextMock.proxy());
    }

}
