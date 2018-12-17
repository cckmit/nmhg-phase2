package tavant.twms.annotations.form.action;

import java.lang.reflect.Method;

/**
 * @author : janmejay.singh
 *         Date: Jul 19, 2007
 *         Time: 10:48:46 AM
 */
public interface CRUDFormAware {

    /**
     * These names must be the same as the expected name for the attributes these setters will set.
     * IMPORTANT : Please don't forget to change the names to reflect the changes in method names if u refractor.
     * I could have used reflection.. but why make it slow and cryptic unnecesarily...??? 
     */
    public static final String INPUT_WRAPPER_ATTRIBUTE = "inputWrapperJsp",
                               SUCCESS_WRAPPER_ATTRIBUTE = "successWrapperJsp";

    public Method[] getMethods();

    public void setInputWrapperJsp(String inputWrapperJsp);

    public void setSuccessWrapperJsp(String successWrapperJsp);

    public String getPageTitle();
}
