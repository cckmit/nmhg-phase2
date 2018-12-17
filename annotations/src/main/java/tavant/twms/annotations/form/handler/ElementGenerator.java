package tavant.twms.annotations.form.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tavant.twms.annotations.form.util.EnvPropertyReader;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 3:57:48 PM
 */
public interface ElementGenerator {
    
    String ACTION_TAG_NAME = "action";
    String ACTION_NAME = "name";
    String ACTION_CLASS = "class";
    String ACTION_METHOD = "method";
    String ACTION_CONVERTER = "converter";
    String RESULT_TAG_NAME = "result";
    String RESULT_TYPE = "type";
    String RESULT_NAME = "name";
    String RESULT_NAME_SUCCESS = "success";
    String RESULT_NAME_INPUT = "input";
    String INTERCEPTOR_REF_TAG_NAME = "interceptor-ref";
    String INTERCEPTOR_REF_NAME = "name";
    String RESULT_TYPE_CHAIN = "chain";

    void populateDocument(Document document, EnvPropertyReader propertyReader, Element parentElem);
}
