package tavant.twms.annotations.form.handler;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.util.HashMap;
import java.util.Map;

import tavant.twms.annotations.form.util.EnvPropertyReader;
import tavant.twms.annotations.form.util.EnvPropertyReaderImpl;

/**
 * User: janmejay.singh
 * Date: Jul 27, 2007
 * Time: 1:12:02 PM
 */
public class EnvPropertyReaderImplTest extends MockObjectTestCase {

    private AnnotationProcessorEnvironment env;
    private EnvPropertyReader propReader;

    protected void setUp() throws Exception {
        super.setUp();
        env = mock(AnnotationProcessorEnvironment.class);
        propReader = new EnvPropertyReaderImpl(env);
    }

    public void testGetProperty_when_available_as_key() {
        final Map<String, String> options = new HashMap<String, String>();
        final String KEY_ONE = "keyOne", VALUE_ONE = "valueOne",
                     KEY_TWO = "keyTwo", VALUE_TWO = "valueTwo";
        options.put(KEY_ONE, VALUE_ONE);
        options.put(KEY_TWO, VALUE_TWO);
        checking(new Expectations() {{
            exactly(2).of(env).getOptions();
                will(returnValue(options));
        }});
        assertEquals(VALUE_ONE, propReader.getProperty(KEY_ONE));
    }

    public void testGetProperty_when_not_available_directly_but_as_flag_name_key() {
        final Map<String, String> options = new HashMap<String, String>();
        final String KEY_ONE = "keyOne", VALUE_ONE = "valueOne",
                     KEY_TWO = "keyTwo", VALUE_TWO = "valueTwo";
        final String FLAG_TOKEN = "-A", EQUALS = "=";
        options.put(FLAG_TOKEN + KEY_ONE + EQUALS + VALUE_ONE, null);
        options.put(FLAG_TOKEN + KEY_TWO + EQUALS + VALUE_TWO, null);
        checking(new Expectations() {{
            exactly(2).of(env).getOptions();
                will(returnValue(options));
        }});
        assertEquals(VALUE_ONE, propReader.getProperty(KEY_ONE));
    }
}
