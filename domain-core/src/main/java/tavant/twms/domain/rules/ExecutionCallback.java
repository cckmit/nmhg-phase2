package tavant.twms.domain.rules;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 16 Oct, 2008
 * Time: 3:31:29 AM
 */
public interface ExecutionCallback {
    Map<String,Object> getTransactionContext();

    Map<String,Object> getActionResultHolder();

    void setRuleEvaluationResult(Map<DomainRule, Map<Boolean, String>> results);
}