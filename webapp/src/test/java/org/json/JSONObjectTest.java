package org.json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

/**
 * 
 */

/**
 * @author radhakrishnan.j
 *
 */
public class JSONObjectTest extends TestCase {
    private JSONObject fixture;
    
    @SuppressWarnings("unchecked")
	public void testJSONObjectStringIntegration() throws Exception {
        fixture = new JSONObject();
        ClassPathResource classpathResource = new ClassPathResource("testJSONObjectString.js",getClass());
        
        String testCaseJSONString = readResourceStream(classpathResource.getInputStream());
        fixture = new JSONObject(testCaseJSONString);
        
        //The JSON Object doesn't provide any APIs to help get "some idea" of the
        //internal structure. You gotta know whats coming in.
        //The JSON Object doesn't provide any APIs to help get "some idea" of the
        //internal structure. You gotta know whats coming in.
        Iterator<String> keys = (Iterator<String>)fixture.keys();
        Set<String> expectedKeys = new HashSet<String>();
        expectedKeys.add("right");
        expectedKeys.add("value");
        expectedKeys.add("type");
        expectedKeys.add("left");
        
        Set<String> actualKeys = new HashSet<String>();
        while( keys.hasNext() ) {
            actualKeys.add(keys.next());
        }
        assertEquals(expectedKeys,actualKeys);
    }
    
    private String readResourceStream(InputStream inputStream) throws IOException {
        final BufferedReader reader =
            new BufferedReader(new InputStreamReader(inputStream));
        return FileCopyUtils.copyToString(reader);
	}

	@SuppressWarnings("unchecked")
	public void testJSONObjectStringStandAlone() throws Exception {
        fixture = new JSONObject();
        
        String personInformation = " " +
                        "{ " +
                        " name : \"John Doe\", " +
                        " address : { " +
                        "       street1 : \" 761 13 'A' Cross 23 Main\"," +
                        "       street2 : \" J P Nagar II Phase \"," +
                        "       city    : \"Bangalore\"," +
                        "       zip     : \"560078\" " +
                        "   }," +
                        " }";
        fixture = new JSONObject(personInformation);
        
        //The JSON Object doesn't provide any APIs to help get "some idea" of the
        //internal structure. You gotta know whats coming in.
        Iterator<String> keys = (Iterator<String>)fixture.keys();
        Set<String> expectedKeys = new HashSet<String>();
        expectedKeys.add("name");
        expectedKeys.add("address");
        
        Set<String> actualKeys = new HashSet<String>();
        while( keys.hasNext() ) {
            actualKeys.add(keys.next());
        }
        assertEquals(expectedKeys,actualKeys);
    }    
}
