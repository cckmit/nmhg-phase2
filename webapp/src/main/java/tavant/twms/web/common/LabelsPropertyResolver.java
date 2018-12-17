/**
 * 
 */
package tavant.twms.web.common;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import tavant.twms.domain.common.Label;

import java.util.Iterator;
import java.util.Set;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class LabelsPropertyResolver extends ImgColAwarePropertyResolver {
	
	private final Logger logger = Logger.getLogger(LabelsPropertyResolver.class);

	@SuppressWarnings("unchecked")
	public Object getProperty(String propertyPath, Object root) {
		try {
			if("labelsImg".equals(propertyPath)) {
				Object parsedExpression = Ognl.parseExpression("labels");
				Set<Label> labels = (Set<Label>) Ognl.getValue(parsedExpression, root);
				JSONObject obj;
                if(labels != null && !labels.isEmpty()) {
                    obj = getImgColValue(getTitle(labels), "image/tag_orange.png");
                } else {
                    obj = getImgColValue("", "image/tag_grey.png");
                }
                return obj;
			}
			Object parsedExpression = Ognl.parseExpression(propertyPath);
			return Ognl.getValue(parsedExpression, root);
		} catch (OgnlException e) {
			logger.error("failed to evaluate expression[" + propertyPath
					+ "] on object [" + root + "]", e);
		} catch (IndexOutOfBoundsException e) {
			logger.error("failed to evaluate expression[" + propertyPath
					+ "] on object [" + root + "]", e);
		}
		return null;
	}
	
	private String getTitle(Set<Label> labels) {
		StringBuffer str = new StringBuffer();
		str.append("Labels: ");
		for (Iterator iter = labels.iterator(); iter.hasNext();) {
			Label element = (Label) iter.next();
			str.append(element.getName());
			if(iter.hasNext()) {
				str.append(", ");
			}
		}
		return str.toString();
	}
}
