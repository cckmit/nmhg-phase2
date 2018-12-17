package tavant.twms.web.common;

import org.apache.log4j.Logger;
import ognl.Ognl;
import ognl.OgnlException;
import org.json.JSONObject;
import tavant.twms.infra.BeanProvider;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 *
 * @author prasad.r
 */
public abstract class CheckBoxPropertyResolver extends I18nActionSupport implements BeanProvider{

    private static final Logger logger = Logger.getLogger(CheckBoxPropertyResolver.class);

    public static final String CB_VALUE = "value",
                               CB_NAME = "name",
                               TOOL_TIP = "toolTip", // tool tip for check box if any
                               CB_DISABLED="disabled";

    protected JSONObject getCheckBoxColValue(String toolTip, String name, String value, boolean isEnabled) {
        try {
            return new JSONObject().put(TOOL_TIP, toolTip)
                                   .put(CB_NAME, name)
                                   .put(CB_VALUE, value)
                                   .put(CB_DISABLED, (!isEnabled) ? "DISABLED" : "");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Object getProperty(String propertyPath, Object root) {
        try {
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

    // Used for providing values for the XLS Cell
    public static String getCheckBoxValue(JSONObject cbColValue){
        try{
            return cbColValue.getString(CB_VALUE);
        }catch(Exception e){
            logger.error("Error fetching value from Check Box!!! " + cbColValue.toString());
        }
        return "";
    }
}