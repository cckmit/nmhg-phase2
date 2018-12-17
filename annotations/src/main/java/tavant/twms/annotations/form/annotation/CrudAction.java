package tavant.twms.annotations.form.annotation;

import tavant.twms.annotations.form.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : janmejay.singh
 *         Date: Jul 17, 2007
 *         Time: 3:03:17 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CrudAction {
    String actionName();
    String onSuccess();
    String onInput();
    String interceptorRef();
    ActionType actionType();
    String convertor() default "";
    boolean chainSuccess() default false;
    boolean chainInput() default false;
}
