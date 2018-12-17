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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * @author radhakrishnan.j
 * 
 */
public abstract class RuleExecutionCallback extends AbstractExecutionCallback {

    public RuleExecutionCallback(Map<String, Object> context, Map<String, Object> resultMap) {
        super(context, resultMap);
    }

    public RuleExecutionCallback() {
    }

    public abstract List<DomainRule> getRulesForExecution();
}
