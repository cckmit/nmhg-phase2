/**
 * 
 */
package tavant.twms.web.admin.groups;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import tavant.twms.web.common.ImgColAwarePropertyResolver;

import java.util.Set;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class GroupsMemberTypeResolver extends ImgColAwarePropertyResolver {

	private final Logger logger = Logger.getLogger(GroupsMemberTypeResolver.class);

	@SuppressWarnings("unchecked")
	public Object getProperty(String propertyPath, Object root) {
		try {
			if("labelsImg".equals(propertyPath)) {
				Object parsedExpression = Ognl.parseExpression("consistsOf");
				Set subGroups = (Set) Ognl.getValue(parsedExpression, root);
				JSONObject obj;
                if(subGroups != null && !subGroups.isEmpty()) {
                    obj = getImgColValue("", "image/tag_group.png");
                } else {
                    obj = getImgColValue("", "image/tag_indv.png");
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

}
