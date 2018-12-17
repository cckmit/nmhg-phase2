package tavant.twms.interceptor;

import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.log4j.Logger;

import java.util.*;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 29 Jun, 2009
 * Time: 5:19:31 PM
 */
public class TrimListByElementIdInterceptor extends AbstractInterceptor {

    public static final String REMOVAL_INDEX_PARAM_PREFIX = "__twmsDeleteByElementId.";
    private static final int REMOVAL_ID_PARAM_PREFIX_LEN = REMOVAL_INDEX_PARAM_PREFIX.length();
    private static final Logger logger = Logger.getLogger(TrimListByElementIdInterceptor.class);

    @SuppressWarnings("unchecked")
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> params = invocation.getInvocationContext().getParameters();
        List<String> invalidParamNames = new ArrayList<String>();

        ValueStack stack = invocation.getStack();

        for (Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> param = iterator.next();
            String paramName = param.getKey();
            if (paramName.startsWith(REMOVAL_INDEX_PARAM_PREFIX)) {
                if (processTrimListByElementIdParam(paramName, param.getValue(), stack, invalidParamNames)) {
                    iterator.remove();
                } else {
                    break;
                }
            }
        }

        return invocation.invoke();
    }

    private boolean processTrimListByElementIdParam(String paramName, Object paramValue, ValueStack stack,
                                                    List<String> invalidParamNames) {

        String listName = paramName.substring(REMOVAL_ID_PARAM_PREFIX_LEN);

        if (invalidParamNames.contains(listName)) {
            return false;
        }

        Object possibleList = stack.findValue(listName);

        if (possibleList instanceof List) {
            List list = (List) possibleList;

            String idsCsv = ((String[]) paramValue)[0];

            List<String> ids = new ArrayList<String>();
            ids.addAll(Arrays.asList(idsCsv.split("\\,")));

            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Object listElement = iterator.next();
                Object elementIdObj;
                String elementId;

                try {
                    elementIdObj = Ognl.getValue("id", listElement);

                    if(elementIdObj == null) {
                        if(logger.isDebugEnabled()) {
                            logger.debug("Encountered list element with null id : " + listElement);
                        }

                        break;
                    } else {
                        elementId = elementIdObj.toString();
                    }
                } catch (OgnlException e) {
                    logger.warn("Encountered list element without id property : " + listElement);
                    break;
                }

                if (ids.contains(elementId)) {
                    // we are assuming that the list won't contain multiple elements with same id.
                    ids.remove(elementId);
                    iterator.remove();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Removed element [" + listElement + "] having id [" + elementId +
                                "] from list '" + listName + "'");
                    }

                    if(ids.isEmpty()) {
                        break;
                    }
                }
            }
        } else {
            invalidParamNames.add(listName);
            logger.warn("Encountered the deletion request param '" + paramName + "', but the associated " +
                    "object '" + listName + "' is not a possibleList. Ignoring this parameter.");
        }

        return true;
    }
}