/**
 *
 */
package tavant.twms.web.typeconverters;

import ognl.DefaultTypeConverter;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author kannan.ekanath
 *
 */
public class EnumConverter extends DefaultTypeConverter {

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
            Enum n = (Enum) value;
            return n.name();
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
            if(NULL.equals(id)){
            	return null;
            }
            else {
            	return Enum.valueOf(toType, id);
            }
        }
    }

}
