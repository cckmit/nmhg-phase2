package tavant.twms.annotations.form.handler;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import tavant.twms.annotations.form.util.TestUtil;

import java.util.List;

/**
 * User: Janmejay.singh
 * Date: Aug 2, 2007
 * Time: 12:20:00 PM
 */
public abstract class AbstractElementGeneratorTest extends TestCase {
    protected final EnvPropertyReader propertyReader = new EnvPropertyReader() {
        public String getProperty(String key) {
            return key;
        }
    };

    protected Document document;

    protected AbstractElementGeneratorTest() {}

    protected AbstractElementGeneratorTest(String s) {
        super(s);
    }


    protected Element createAndReturnElement(ElementGenerator generator) {
        Element rootElem = document.createElement("root");

        generator.populateDocument(document, propertyReader, rootElem);

        NodeList children = rootElem.getChildNodes();

        List<Element> elements = TestUtil.getElementNodes(children);

        assertEquals(1, elements.size());

        return elements.get(0);
    }
}
