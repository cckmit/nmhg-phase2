package tavant.twms.annotations.form.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * User: Janmejay.singh
 * Date: Jul 28, 2007
 * Time: 8:34:06 PM
 */
public class TestUtil {
    public static List<Element> getElementNodes(NodeList list) {
        List<Element> elementNodes = new ArrayList<Element>();
        for(int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (node instanceof Element) {
                elementNodes.add((Element) node);
            }
        }
        return elementNodes;
    }
   
}
