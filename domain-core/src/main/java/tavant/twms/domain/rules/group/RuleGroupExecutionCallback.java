package tavant.twms.domain.rules.group;

import tavant.twms.domain.rules.AbstractExecutionCallback;

import java.util.List;
import java.util.Map;

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 * Date: 29 Sep, 2008
 * Time: 5:57:19 PM
 */
public abstract class RuleGroupExecutionCallback extends AbstractExecutionCallback {

    public RuleGroupExecutionCallback(Map<String, Object> context, Map<String, Object> resultMap) {
        super(context, resultMap);
    }

    public RuleGroupExecutionCallback() {
    }

    public abstract List<DomainRuleGroup> getRuleGroupsForExecution();
}
