package tavant.twms.annotations.form.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tavant.twms.annotations.form.annotation.CrudAction;
import static tavant.twms.annotations.form.action.CRUDFormAware.INPUT_WRAPPER_ATTRIBUTE;
import static tavant.twms.annotations.form.action.CRUDFormAware.SUCCESS_WRAPPER_ATTRIBUTE;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import static tavant.twms.annotations.form.util.EnvPropertyReader.*;

/**
 * @author : janmejay.singh
 *         Date: Jul 18, 2007
 *         Time: 7:54:53 PM
 */
public class CrudActionElementGenerator extends StrutsActionElementGenerator {

    public static final String PARAM_TAG_NAME = "param";
    public static final String PARAM_NAME = "name";

    private CrudAction crudActionInstanceValues;

    private enum ReturnType {
        SUCCESS,
        INPUT
    }

    public CrudActionElementGenerator(final CrudAction annotation, final String actionClassName, final String methodName) {
        super(actionClassName, methodName);
        this.crudActionInstanceValues = annotation;
    }

    public void populateDocument(final Document document, final EnvPropertyReader propertyReader, Element parentElem) {
        //creating action
        Element action = getActionNode(document,
                                       crudActionInstanceValues.actionName(),
                                       crudActionInstanceValues.convertor());

        //creating interceptor-ref
        Element interceptorRef = getInterceptorRefNode(document, crudActionInstanceValues.interceptorRef());

        //creating results
        Element successResult = getSuccessResultNode(document, action, propertyReader);
        Element inputResult = getInputResultNode(document, action, propertyReader);

        //putting things in place
        action.appendChild(interceptorRef);
        action.appendChild(successResult);
        action.appendChild(inputResult);
        parentElem.appendChild(action);
    }

    private Element getInputResultNode(Document document, Element action, EnvPropertyReader propertyReader) {
        Element inputResult = document.createElement(RESULT_TAG_NAME);
        inputResult.setAttribute(RESULT_NAME, RESULT_NAME_INPUT);
        if (crudActionInstanceValues.chainInput()) {
            inputResult.setAttribute(RESULT_TYPE, RESULT_TYPE_CHAIN);
            inputResult.appendChild(document.createTextNode(crudActionInstanceValues.onInput()));
        } else {
            final Element param = document.createElement(PARAM_TAG_NAME);
            param.setAttribute(PARAM_NAME, INPUT_WRAPPER_ATTRIBUTE);
            param.appendChild(document.createTextNode(crudActionInstanceValues.onInput()));
            action.appendChild(param);
            inputResult.appendChild(document.createTextNode(getWrapperPath(ReturnType.INPUT, propertyReader)));
        }
        return inputResult;
    }

    private Element getSuccessResultNode(Document document, Element action, EnvPropertyReader propertyReader) {
        Element successResult = document.createElement(RESULT_TAG_NAME);
        successResult.setAttribute(RESULT_NAME, RESULT_NAME_SUCCESS);
        if (crudActionInstanceValues.chainSuccess()) {
            successResult.setAttribute(RESULT_TYPE, RESULT_TYPE_CHAIN);
            successResult.appendChild(document.createTextNode(crudActionInstanceValues.onSuccess()));
        } else {
            final Element param = document.createElement(PARAM_TAG_NAME);
            param.setAttribute(PARAM_NAME, SUCCESS_WRAPPER_ATTRIBUTE);
            param.appendChild(document.createTextNode(crudActionInstanceValues.onSuccess()));
            action.appendChild(param);
            successResult.appendChild(document.createTextNode(getWrapperPath(ReturnType.SUCCESS, propertyReader)));
        }
        return successResult;
    }

    private String getWrapper(ReturnType returnType, EnvPropertyReader propertyReader) {
        switch (crudActionInstanceValues.actionType()) {
            case CREATE_REQUEST:
                return chooseWrappers(CREATE_REQUEST_VIEW_WRAPPER, CREATE_REQUEST_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case CREATE_SUBMIT:
                return chooseWrappers(CREATE_SUCCESS_VIEW_WRAPPER, CREATE_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case UPDATE_REQUEST:
                return chooseWrappers(UPDATE_REQUEST_VIEW_WRAPPER, UPDATE_REQUEST_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case UPDATE_SUBMIT:
                return chooseWrappers(UPDATE_SUCCESS_VIEW_WRAPPER, UPDATE_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case DELETE_REQUEST:
                return chooseWrappers(DELETE_REQUEST_VIEW_WRAPPER, DELETE_REQUEST_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case DELETE_SUBMIT:
                return chooseWrappers(DELETE_SUCCESS_VIEW_WRAPPER, DELETE_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case READ_ONLY:
                return chooseWrappers(READ_REQUEST_VIEW_WRAPPER, READ_REQUEST_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case ACTIVATE_SUBMIT:
                return chooseWrappers(UPDATE_SUCCESS_VIEW_WRAPPER, UPDATE_INPUT_VIEW_WRAPPER, returnType, propertyReader);
            case DEACTIVATE_SUBMIT:
                return chooseWrappers(UPDATE_SUCCESS_VIEW_WRAPPER, UPDATE_INPUT_VIEW_WRAPPER, returnType, propertyReader);
        }
        return null;
    }

    private static String chooseWrappers(String successWrapper, String inputWrapper,
                                                        ReturnType returnType, EnvPropertyReader propertyReader) {
        String successWrapperValue = propertyReader.getProperty(successWrapper);
        assert successWrapper != null;
        String inputWrapperValue = propertyReader.getProperty(inputWrapper);
        if(returnType == ReturnType.SUCCESS) {
            return successWrapperValue;
        } else {
            return inputWrapperValue != null ? inputWrapperValue : successWrapperValue;
        }
    }

    private String getWrapperPath(final ReturnType returnType, final EnvPropertyReader propertyReader) {
        return propertyReader.getProperty(WRAPPER_FILE_DIRECTORY_PATH) + "/" + getWrapper(returnType, propertyReader);
    }
}
