package tavant.twms.interceptor;

import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;

/**
 * @author binil.thomas, janmejay.singh
 * Date: Aug 10, 2007
 * Time: 1:02:00 AM
 */
@SuppressWarnings("serial")
public class RepeatTableInterceptor extends AbstractInterceptor {
    private static final String KEY_REPEAT_REMOVE = "__remove.";
    private static final Logger logger = Logger.getLogger(RepeatTableInterceptor.class);

    @Override
    @SuppressWarnings("unchecked")
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();
        final Map parameters = ac.getParameters();

        if (parameters != null) {
            ValueStack stack = ac.getValueStack();

            Set<Map.Entry> p = parameters.entrySet();
            for (Map.Entry<Object, Object> parameter : p) {
                String name = (String) parameter.getKey();
                if (name.length() <= KEY_REPEAT_REMOVE.length()) continue;
                if (!name.startsWith(KEY_REPEAT_REMOVE)) continue;

                name = name.substring(KEY_REPEAT_REMOVE.length(), name.length());
                Object colObj = stack.findValue(name);
                if ((colObj == null) || (!(colObj instanceof Collection))) continue;
                Collection collection = (Collection) colObj;
                String[] valuesToBeRemoved = null;
                if(collection!=null){
              		if(!collection.isEmpty()){
        				valuesToBeRemoved =valuesToBeRemoved(parameter.getValue(),collection,stack);
            		}
                }
                removeNulls(collection);
                Object o = parameter.getValue();
                if (o instanceof String) {
                    removeFromCollection(collection, (String) o, stack);
                } else if (o instanceof String[]) {
                    if (valuesToBeRemoved != null) {
                        String[] values = valuesToBeRemoved;
                        List objectsForRemoval = new ArrayList();
                        for (String value : values) {
                            if (StringUtils.hasText(value))
                            {
                            	objectsForRemoval.add(objectForRemovalFromCollection(collection, value, stack));                            	
                            }                               
                        }
                        removeFromCollection(collection,objectsForRemoval );
                    }
                }
            }
        }
        return invocation.invoke();
    }
    
    
    private void removeFromCollection(Collection collection,List list)
    {
    	for(Object o :list)
    	{
    		collection.remove(o);
    	}
    }
    
    private Object objectForRemovalFromCollection(Collection collection, String ognlPropName, ValueStack stack) {
        if (collection.isEmpty()) {
            return null; 
        }

        Object targetObject = collection.iterator().next();
        // Get the actual target type, bypassing proxies.
        Class actualToType = Hibernate.getClass(targetObject);

        if (logger.isDebugEnabled()) {
            logger.debug("Received target class is [" + targetObject.getClass()
                    + "]. Actual target class, after adjusting for hibernate " +
                    "proxy if any, is [" + actualToType + "].");
        }

        Object obj = stack.findValue(ognlPropName, actualToType);
        return obj;
    }

    private void removeFromCollection(Collection collection, String ognlPropName, ValueStack stack) {
    	
    	Object obj = objectForRemovalFromCollection(collection, ognlPropName, stack);
        if (obj == null) {
            return;
        }
        collection.remove(obj);
    }

    @SuppressWarnings("unchecked")
    private void removeNulls(Collection fromCollection) {
        Collection NULL = Collections.singleton(null);
        fromCollection.removeAll(NULL);
    }

    private String[] valuesToBeRemoved(Object parameterValue,
                                       Collection collection,
                                       ValueStack stack){
        String[] removalValuz = null;
        Object o = parameterValue;
        Object targetObject = null;
        for (Object collectionObject : collection) {
             if(collectionObject!=null){
                 targetObject=collectionObject;
                 break;
             }
        }
        if(targetObject==null)
        {
        	return removalValuz;
        }
        if (o instanceof String[]) {
            removalValuz = (String[]) o;
            for (int i=0;i<removalValuz.length;i++){
                // Get the actual target type, bypassing proxies.
                Class actualToType = Hibernate.getClass(targetObject);
                Object obj = stack.findValue(removalValuz[i], actualToType);
                if (obj!=null && !obj.getClass().getName().equalsIgnoreCase(targetObject.getClass().getName())) {
                    removalValuz[i] = null;
                }
            }
        }
        return removalValuz;
    }
}