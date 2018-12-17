package tavant.twms.web.actions;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import tavant.twms.annotations.form.ActionType;
import static tavant.twms.annotations.form.action.CRUDActionUtil.getActionFor;
import tavant.twms.annotations.form.action.CRUDFormAware;
import tavant.twms.web.i18n.I18nActionSupport;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

/**
 * @author : janmejay.singh
 *         Date: Jul 19, 2007
 *         Time: 10:34:07 AM
 */
public abstract class CRUDFormAction extends I18nActionSupport implements ServletRequestAware, CRUDFormAware {

    protected HttpServletRequest request;
    
    /**
     * This is used by the class to keep an array of all its methods...
     * This comes handy... when trying to find the annotation attributes while trying to wire up the button actions.
     */
    private Method[] methods;

    public static final String SAVE_BUTTON_ACTION = "CRUD_FORM_SAVE_BUTTON_ACTION",
                               DELETE_BUTTON_ACTION = "CRUD_FORM_DELETE_BUTTON_ACTION",
                               UPDATE_BUTTON_ACTION = "CRUD_FORM_UPDATE_BUTTON_ACTION",
                               ACTIVATE_BUTTON_ACTION = "CRUD_FORM_ACTIVATE_BUTTON_ACTION",
                               DEACTIVATE_BUTTON_ACTION = "CRUD_FORM_DEACTIVATE_BUTTON_ACTION";


    Logger logger = Logger.getLogger(this.getClass());

    protected CRUDFormAction() {
        methods = this.getClass().getMethods();
    }

    public final Method[] getMethods() {
        return methods;
    }

    public final void setInputWrapperJsp(String inputWrapperJsp) {
        request.setAttribute(INPUT_WRAPPER_ATTRIBUTE, inputWrapperJsp);
    }

    public final void setSuccessWrapperJsp(String successWrapperJsp) {
        request.setAttribute(SUCCESS_WRAPPER_ATTRIBUTE, successWrapperJsp);
        putActionNamesInRequest();
    }

    private void putActionNamesInRequest() {
        for(ActionType actionType : ActionType.values()) {
            String actionName = getActionFor(actionType, this);
            request.setAttribute(actionType.toString(), actionName != null ? actionName : "NOT_DEFINED");
        }
    }

    public final void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }
    
    public String getPageTitle() {
        logger.warn("The class extending " + CRUDFormAction.class + " should " +
                "override getPageTitle method to return a sensible title.");
        return "NOT-DEFINED";
    }

    public boolean isAjaxValidatable() {
        logger.warn("The class extending " + CRUDFormAction.class + " should " +
                "override isAjaxValidatable method to return a sensible value(returning false by default).");
        return false;
    }

    public boolean isShowSaveButton() {
        return true;
    }

    public boolean isShowUpdateButton() {
        return true;
    }

    public boolean isShowDeleteButton() {
        return true;
    }

    public boolean isShowCancelButton() {
        return true;
    }

    public boolean isShowActivateButton() {
        return false;
    }

    public boolean isShowDeactivateButton() {
        return false;
    }

    public boolean hasWarningSupport() {
        return false;
    }

    public Collection getWarnings() {
        return Collections.emptySet();
    }
}
