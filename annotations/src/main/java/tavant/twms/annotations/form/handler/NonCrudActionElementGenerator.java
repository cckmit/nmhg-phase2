package tavant.twms.annotations.form.handler;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import tavant.twms.annotations.form.annotation.Action;

/**
 * User: Janmejay.singh
 * Date: Aug 1, 2007
 * Time: 5:26:09 PM
 */
public class NonCrudActionElementGenerator extends StrutsActionElementGenerator {

    private Action action;

    private static class ActionData {
        String actionName;
        String convertor;
        String onSuccess;
        String onInput;
        boolean chainSuccess;
        boolean chainInput;
        String interceptorRef;
    }

    public NonCrudActionElementGenerator(final Action action, final String actionClassName, final String methodName) {
        super(actionClassName, methodName);
        this.action = action;
    }

    public void populateDocument(Document document, EnvPropertyReader propertyReader, Element parentElem) {
        ActionData[] actions = parseActionDecl(action);
        for (ActionData action : actions) {
            generateActionElem(document, parentElem, action);
        }
    }

    private void generateActionElem(final Document document, final Element parentElem, final ActionData actionDetails) {
        //creating action
        Element action = getActionNode(document, actionDetails.actionName, actionDetails.convertor);

        //creating interceptor-ref
        Element interceptorRef = getInterceptorRefNode(document, actionDetails.interceptorRef);

        //creating results
        Element successResult = getSuccessResultNode(document, actionDetails);
        Element inputResult = getInputResultNode(document, actionDetails);

        //putting things in place
        action.appendChild(interceptorRef);
        action.appendChild(successResult);
        action.appendChild(inputResult);
        parentElem.appendChild(action);
    }

    private Element getInputResultNode(Document document, NonCrudActionElementGenerator.ActionData actionDetails) {
        Element inputResult = document.createElement(RESULT_TAG_NAME);
        inputResult.setAttribute(RESULT_NAME, RESULT_NAME_INPUT);
        if (actionDetails.chainInput) inputResult.setAttribute(RESULT_TYPE, RESULT_TYPE_CHAIN);
        inputResult.appendChild(document.createTextNode(actionDetails.onInput));
        return inputResult;
    }

    private Element getSuccessResultNode(Document document, NonCrudActionElementGenerator.ActionData actionDetails) {
        Element successResult = document.createElement(RESULT_TAG_NAME);
        successResult.setAttribute(RESULT_NAME, RESULT_NAME_SUCCESS);
        if (actionDetails.chainSuccess) successResult.setAttribute(RESULT_TYPE, RESULT_TYPE_CHAIN);
        successResult.appendChild(document.createTextNode(actionDetails.onSuccess));
        return successResult;
    }

    private static ActionData[] parseActionDecl(Action annotation) {
        int expectedNoOfElems = annotation.actionNames().length;
        ActionData[] actions = new ActionData[expectedNoOfElems];
        Boolean[] chainSuccesses = box(annotation.chainSuccesses());
        Boolean[] chainInputs = box(annotation.chainInputs());
        for(int i = 0; i < actions.length; i++) {
            ActionData action = new ActionData();
            action.actionName = annotation.actionNames()[i];
            action.interceptorRef = getEffectiveNthElement(annotation.interceptorRefs(), i);
            action.convertor = getEffectiveNthElement(annotation.convertors(), i);
            action.onSuccess = getEffectiveNthElement(annotation.onSuccesses(), i);
            action.onInput = getEffectiveNthElement(annotation.onInputs(), i);
            action.chainSuccess = getEffectiveNthElement(chainSuccesses, i);
            action.chainInput = getEffectiveNthElement(chainInputs, i);
            actions[i] = action;
        }
        return actions;
    }

    private static <T> T getEffectiveNthElement(T[] ts, int index) {
        int length = ts.length;
        if (length == 0) return null;
        return length > index ? ts[index] : ts[ts.length - 1];
    }

    private static Boolean[] box(boolean[] bools) {
        Boolean[] boxedBools = new Boolean[bools.length];
        int i = 0;
        while (i < bools.length) boxedBools[i] = bools[i++];
        return boxedBools;
    }
}
