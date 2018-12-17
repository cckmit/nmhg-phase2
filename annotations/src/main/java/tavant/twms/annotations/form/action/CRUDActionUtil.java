package tavant.twms.annotations.form.action;

import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.ActionType;

import java.lang.reflect.Method;

/**
 * @author : janmejay.singh
 *         Date: Jul 25, 2007
 *         Time: 5:18:52 PM
 */
public class CRUDActionUtil {

    /**
     * @param actionType (the type of action that method is implementing)
     * @param formAwareAction (action implementing CRUDFormAware)
     * @return actionNames(String) or null depending on the user having the method annotated with the
     * particular annotation type.
     */
    public static String getActionFor(ActionType actionType, CRUDFormAware formAwareAction) {
        for (Method method : formAwareAction.getMethods()) {
            CrudAction crudActionDecl = method.getAnnotation(CrudAction.class);
            if(crudActionDecl != null && crudActionDecl.actionType().equals(actionType)) {
                return crudActionDecl.actionName();
            }
        }
        return null;
    }
}
