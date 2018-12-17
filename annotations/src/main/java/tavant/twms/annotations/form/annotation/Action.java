package tavant.twms.annotations.form.annotation;

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
public @interface Action {
    String[] actionNames();
    String[] onSuccesses();
    String[] onInputs();
    String[] interceptorRefs();
    String[] convertors() default "";
    boolean[] chainSuccesses() default false;
    boolean[] chainInputs() default false;
}