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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Visits the various elements of a rule and determines service dependencies. Piggy
 * rides on top of OGNLExpressionGenerator's visiting pattern to cut down on coding
 * drudgery.
 * 
 * @author radhakrishnan.j
 *
 */
public class RuleExecutionDependenciesIdentifier extends OGNLExpressionGenerator {
    private SortedSet<String> requiredDependencies = new TreeSet<String>();

    @Override
    public void visit(MethodInvocationTarget visitable) {
        super.visit(visitable);
        
        //a method invocation target would typically be a dependency.
        requiredDependencies.add(visitable.getBeanName());
    }

    public SortedSet<String> getRequiredDependencies() {
        return requiredDependencies;
    }

    public void setRequiredDependencies(SortedSet<String> requiredDependencies) {
        this.requiredDependencies = requiredDependencies;
    } 
}
