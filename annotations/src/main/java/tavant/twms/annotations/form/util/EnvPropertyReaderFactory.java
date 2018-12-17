package tavant.twms.annotations.form.util;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 4:03:53 PM
 */
public interface EnvPropertyReaderFactory {
    EnvPropertyReader getPropertyReader(AnnotationProcessorEnvironment env);
}
