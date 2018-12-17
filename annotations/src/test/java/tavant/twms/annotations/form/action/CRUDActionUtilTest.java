package tavant.twms.annotations.form.action;

import junit.framework.TestCase;
import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.ActionType;
import static tavant.twms.annotations.form.action.CRUDActionUtil.*;

import java.lang.reflect.Method;

/**
 * User: Janmejay.singh
 * Date: Jul 31, 2007
 * Time: 2:50:07 PM
 */
public class CRUDActionUtilTest extends TestCase {

    private static final String AVAILABLE_ACTION_ONE_NAME = "availableActionOne",
                                AVAILABLE_ACTION_TWO_NAME = "availableActionTwo";

    private class ActionWithAnnotatedMethods implements CRUDFormAware {

        private Method[] methods;

        public ActionWithAnnotatedMethods() {
            methods = ActionWithAnnotatedMethods.class.getMethods();
        }

        @CrudAction(
                actionName = AVAILABLE_ACTION_ONE_NAME,
                actionType = ActionType.CREATE_REQUEST,
                interceptorRef = "someInterceptor",
                onInput = "input.jsp",
                onSuccess = "success.jsp"
        )
        public String availableAction() {
            return "success";
        }

        @CrudAction(
                actionName = AVAILABLE_ACTION_TWO_NAME,
                actionType = ActionType.UPDATE_SUBMIT,
                interceptorRef = "someInterceptor",
                onInput = "input.jsp",
                onSuccess = "success.jsp"
        )
        public String anotherAvailableAction() {
            return "success";
        }

        public String unAvailableAction() {
            return "input";
        }

        public Method[] getMethods() {
            return methods;
        }

        public void setInputWrapperJsp(String inputWrapperJsp) {}

        public void setSuccessWrapperJsp(String successWrapperJsp) {}

        public String getPageTitle() {
            return null;
        }
    }

    ActionWithAnnotatedMethods action;

    protected void setUp() throws Exception {
        super.setUp();
        action = new ActionWithAnnotatedMethods();
    }

    public void testGetActionForAvailableActions() {
        assertEquals(AVAILABLE_ACTION_ONE_NAME, getActionFor(ActionType.CREATE_REQUEST, action));
        assertEquals(AVAILABLE_ACTION_TWO_NAME, getActionFor(ActionType.UPDATE_SUBMIT, action));
    }

    public void testGetActionForUnAvailableAction() {
        assertNull(getActionFor(ActionType.ACTIVATE_SUBMIT, action));
    }
}
