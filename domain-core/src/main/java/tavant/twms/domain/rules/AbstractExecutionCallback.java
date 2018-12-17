package tavant.twms.domain.rules;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 16 Oct, 2008
 * Time: 3:26:55 AM
 */
public abstract class AbstractExecutionCallback implements ExecutionCallback {
    protected Map<String, Object> context;
    protected Map<String, Object> resultMap;

    public AbstractExecutionCallback() {
    }

    public AbstractExecutionCallback(Map<String, Object> context, Map<String, Object> resultMap) {
        this.context = context;
        this.resultMap = resultMap;
    }

    public Map<String, Object> getTransactionContext() {
        return context;
    }

    public Map<String, Object> getActionResultHolder() {
        return resultMap;
    }
}
