package tavant.twms.annotations.form.handler;

import java.lang.annotation.Annotation;

/**
 * User: janmejay.singh
 * Date: Jul 27, 2007
 * Time: 3:03:38 PM
 */
public interface ElementGeneratorFactory {
    ElementGenerator getElementGenerator(Annotation crudAction, String actionClassName, String actionMethodName);
}
