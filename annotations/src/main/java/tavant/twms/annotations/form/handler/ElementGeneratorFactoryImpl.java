package tavant.twms.annotations.form.handler;

import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.annotation.Action;

import java.lang.annotation.Annotation;

/**
 * User: janmejay.singh
 * Date: Jul 27, 2007
 * Time: 3:00:37 PM
 */
public class ElementGeneratorFactoryImpl implements ElementGeneratorFactory {
    public ElementGenerator getElementGenerator(Annotation crudAction, String actionClassName, String actionMethodName) {
        if(crudAction instanceof CrudAction) {
            return new CrudActionElementGenerator((CrudAction)crudAction, actionClassName, actionMethodName);
        } else {//there are only two... hence this else... otherwise this would have been an else if.
            return new NonCrudActionElementGenerator((Action)crudAction, actionClassName, actionMethodName);
        }
    }
}
