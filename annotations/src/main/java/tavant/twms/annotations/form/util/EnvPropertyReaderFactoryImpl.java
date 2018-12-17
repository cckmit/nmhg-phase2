package tavant.twms.annotations.form.util;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 4:01:13 PM
 */
public class EnvPropertyReaderFactoryImpl implements EnvPropertyReaderFactory {
    public EnvPropertyReader getPropertyReader(AnnotationProcessorEnvironment env) {
        return new EnvPropertyReaderImpl(env);
    }
}
