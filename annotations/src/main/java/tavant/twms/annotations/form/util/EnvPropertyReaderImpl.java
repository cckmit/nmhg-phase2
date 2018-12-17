package tavant.twms.annotations.form.util;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

import java.util.Map;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 1:47:51 PM
 */
public class EnvPropertyReaderImpl implements EnvPropertyReader {

    private AnnotationProcessorEnvironment env;

    public EnvPropertyReaderImpl(AnnotationProcessorEnvironment env) {
        this.env = env;
    }

    public String getProperty(String key) {
        if (env.getOptions().containsKey(key)) return env.getOptions().get(key);
        // there is a bug in the 1.5 apt implementation:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6258929
        // this is a work-around
        for (Map.Entry<String, String> entry : env.getOptions().entrySet()) {
            String keyValuePair = entry.getKey();
            String[] splitted = keyValuePair.split("=");
            if (splitted[0].equals("-A" + key)) return splitted[1];
        }
        return null;
    }
}
