package tavant.twms.web.typeconverters;

import ognl.DefaultTypeConverter;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 14, 2009
 * Time: 5:49:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleConverter extends DefaultTypeConverter {
    private static final Logger logger = Logger.getLogger(DefaultTypeConverter.class);

    //The UI can populate this, when it explicitly wants a null to be shown
    public static final String NULL = "null";

    @SuppressWarnings("unchecked")
    @Override
    public Object convertValue(Map ctx, Object value, Class toType) {
        if(toType == String.class) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Attempting to convert [" + value + "] to string");
            }
            Double n = new Double(value.toString());
            return n.toString();
        }
        else {
            if(logger.isDebugEnabled())
            {
                logger.debug("Attemping to convert to class [" + toType + "]");
            }
            String id = null;
            if (value instanceof String[]) {
            	id = ((String[]) value)[0];
            }
            else if (value instanceof String) {
                id = (String) value;
            } else {
                id = value.toString();
            }
            if(NULL.equals(id) || !StringUtils.hasText(id)){
            	return new Double(0);
            }
            else {
            	return new Double(id);
            }
        }
    }
}
