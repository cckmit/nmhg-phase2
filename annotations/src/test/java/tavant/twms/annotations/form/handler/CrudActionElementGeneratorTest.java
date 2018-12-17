package tavant.twms.annotations.form.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tavant.twms.annotations.form.ActionType;
import static tavant.twms.annotations.form.action.CRUDFormAware.INPUT_WRAPPER_ATTRIBUTE;
import static tavant.twms.annotations.form.action.CRUDFormAware.SUCCESS_WRAPPER_ATTRIBUTE;
import tavant.twms.annotations.form.annotation.CrudAction;
import static tavant.twms.annotations.form.handler.CrudActionElementGenerator.*;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import static tavant.twms.annotations.form.util.EnvPropertyReader.*;
import static tavant.twms.annotations.form.util.TestUtil.getElementNodes;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * User: Janmejay.singh
 * Date: Jul 28, 2007
 * Time: 6:30:10 PM
 */
public class CrudActionElementGeneratorTest extends AbstractElementGeneratorTest {

    public static final String ACTION_NAME_WITHOUT_CHAINING = "methodWithoutChaining",
                               ACTION_NAME_WITH_INPUT_CHAINED = "methodWithChainingOnInput",
                               ACTION_NAME_WITH_SUCCESS_CHAINED = "methodWithChainingOnSuccess",
                               ACTION_NAME_WITH_BOTH_CHAINED = "methodWithChainingBoth",
                               ACTION_NAME_CONVERTOR = "methodWithConvertor",
                               ACTION_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR = "methodWithChainInputAndConvertor";

    public static final String SUCCESS_JSP_NAME_WITHOUT_CHAINING = "withoutChainSuccess.jsp",
                               SUCCESS_JSP_NAME_WITH_INPUT_CHAINED = "withInputChainedSuccess.jsp",
                               SUCCESS_JSP_NAME_WITH_SUCCESS_CHAINED = "success",
                               SUCCESS_JSP_NAME_WITH_BOTH_CHAINED = "bothChainedSuccess",
                               SUCCESS_JSP_NAME_CONVERTOR = "withConvertorSuccess.jsp",
                               SUCCESS_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR = "withInputChainedConvertorSuccess.jsp";

    public static final String INPUT_JSP_NAME_WITHOUT_CHAINING = "withoutChainInput.jsp",
                               INPUT_JSP_NAME_WITH_INPUT_CHAINED = "input",
                               INPUT_JSP_NAME_WITH_SUCCESS_CHAINED = "withSuccessChainedInput.jsp",
                               INPUT_JSP_NAME_WITH_BOTH_CHAINED = "bothChainedInput",
                               INPUT_JSP_NAME_CONVERTOR = "withConvertorInput.jsp",
                               INPUT_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR = "withInputChainedWithConvertorInput";

    public static final String INTERCEPTOR_REF_VALUE = "twms_prepare_params";

    public static final String CONVERTOR_WITHOUT_CHAINING = "convertorWithoutChaining",
                               CONVERTOR_WITH_INPUT_CHAINING = "convertorWithInputChaining";

    private class TestAction {
        @CrudAction(
                actionName = ACTION_NAME,
                actionType = ActionType.CREATE_REQUEST,
                onSuccess = SUCCESS_JSP_NAME_WITHOUT_CHAINING,
                onInput = INPUT_JSP_NAME_WITHOUT_CHAINING,
                interceptorRef = INTERCEPTOR_REF_VALUE
        )
        public String methodWithoutChaining() {
            return "success";
        }

        @CrudAction(
                actionName = ACTION_NAME_WITH_INPUT_CHAINED,
                actionType = ActionType.UPDATE_SUBMIT,
                onSuccess = SUCCESS_JSP_NAME_WITH_INPUT_CHAINED,
                onInput = INPUT_JSP_NAME_WITH_INPUT_CHAINED,
                chainInput = true,
                interceptorRef = INTERCEPTOR_REF_VALUE
        )
        public String methodWithChainingOnInput() {
            return "input";
        }

        @CrudAction(
                actionName = ACTION_NAME_WITH_SUCCESS_CHAINED,
                actionType = ActionType.UPDATE_REQUEST,
                onSuccess = SUCCESS_JSP_NAME_WITH_SUCCESS_CHAINED,
                onInput = INPUT_JSP_NAME_WITH_SUCCESS_CHAINED,
                chainSuccess = true,
                interceptorRef = INTERCEPTOR_REF_VALUE
        )
        public String methodWithChainingOnSuccess() {
            return "success";
        }

        @CrudAction(
                actionName = ACTION_NAME_WITH_BOTH_CHAINED,
                actionType = ActionType.DELETE_REQUEST,
                onSuccess = SUCCESS_JSP_NAME_WITH_BOTH_CHAINED,
                onInput = INPUT_JSP_NAME_WITH_BOTH_CHAINED,
                chainSuccess = true,
                chainInput = true,
                interceptorRef = INTERCEPTOR_REF_VALUE
        )
        public String methodWithChainingBoth() {
            return "success";
        }

        @CrudAction(
                actionName = ACTION_NAME_CONVERTOR,
                actionType = ActionType.ACTIVATE_SUBMIT,
                onSuccess = SUCCESS_JSP_NAME_CONVERTOR,
                onInput = INPUT_JSP_NAME_CONVERTOR,
                interceptorRef = INTERCEPTOR_REF_VALUE,
                convertor = CONVERTOR_WITHOUT_CHAINING
        )
        public String methodWithConvertor() {
            return "success";
        }

        @CrudAction(
                actionName = ACTION_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                actionType = ActionType.READ_ONLY,
                onSuccess = SUCCESS_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                onInput = INPUT_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                chainInput = true,
                interceptorRef = INTERCEPTOR_REF_VALUE,
                convertor = CONVERTOR_WITH_INPUT_CHAINING
        )
        public String methodWithChainedInputAndConvertor() {
            return "success";
        }
    }

    private Method actionWithoutChaining,
                   actionWithInputChained,
                   actionWithSuccessChained,
                   actionWithBothChained,
                   actionWithConvertor,
                   actionWithConvertorAndInputChained;

    private final String METHOD_WITHOUT_CHAINING = "methodWithoutChaining",
                         METHOD_WITH_CHAINING_ON_INPUT = "methodWithChainingOnInput",
                         METHOD_WITH_CHAINING_ON_SUCCESS = "methodWithChainingOnSuccess",
                         METHOD_WITH_CHAINING_BOTH = "methodWithChainingBoth",
                         METHOD_WITH_CONVERTOR = "methodWithConvertor",
                         METHOD_WITH_CHAINED_INPUT_AND_CONVERTOR = "methodWithChainedInputAndConvertor";

    private final EnvPropertyReader propertyReader = new EnvPropertyReader() {
        public String getProperty(String key) {
            return key;
        }
    };

    public CrudActionElementGeneratorTest() {
        init();
    }

    private void setupDocument() {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public CrudActionElementGeneratorTest(String arg) {
        super(arg);
        init();
    }

    private void init() {
        setupMethodRef();
        setupDocument();
    }

    private void setupMethodRef() {
        try {
            actionWithoutChaining = TestAction.class.getMethod(METHOD_WITHOUT_CHAINING);
            actionWithInputChained = TestAction.class.getMethod(METHOD_WITH_CHAINING_ON_INPUT);
            actionWithSuccessChained = TestAction.class.getMethod(METHOD_WITH_CHAINING_ON_SUCCESS);
            actionWithBothChained = TestAction.class.getMethod(METHOD_WITH_CHAINING_BOTH);
            actionWithConvertor = TestAction.class.getMethod(METHOD_WITH_CONVERTOR);
            actionWithConvertorAndInputChained = TestAction.class.getMethod(METHOD_WITH_CHAINED_INPUT_AND_CONVERTOR);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }




    public void testPopulateDocument_without_chaining() {
        final CrudAction crudActionAnnotation = actionWithoutChaining.getAnnotation(CrudAction.class);

        final ElementGenerator generator = new CrudActionElementGenerator(crudActionAnnotation,
                                                                      TestAction.class.getCanonicalName(),
                                                                      actionWithoutChaining.getName());
        
        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITHOUT_CHAINING, false, crudActionAnnotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing param thingy
        runCommonParamElementsTest(element, false, false, SUCCESS_JSP_NAME_WITHOUT_CHAINING,
                INPUT_JSP_NAME_WITHOUT_CHAINING);

        //testing results
        runCommonResultElementsTest(element, false, false, CREATE_REQUEST_VIEW_WRAPPER,
                CREATE_REQUEST_INPUT_VIEW_WRAPPER, crudActionAnnotation);
    }

    public void testPopulateDocument_with_input_chained() {
        final CrudAction crudActionAnnotation = actionWithInputChained.getAnnotation(CrudAction.class);

        final ElementGenerator generator = new CrudActionElementGenerator(crudActionAnnotation,
                                                                      TestAction.class.getCanonicalName(),
                                                                      actionWithInputChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINING_ON_INPUT, false, crudActionAnnotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing param thingy
        runCommonParamElementsTest(element, false, true, SUCCESS_JSP_NAME_WITH_INPUT_CHAINED,
                INPUT_JSP_NAME_WITH_INPUT_CHAINED);

        //testing results
        runCommonResultElementsTest(element, false, true, UPDATE_SUCCESS_VIEW_WRAPPER,
                UPDATE_INPUT_VIEW_WRAPPER, crudActionAnnotation);
    }

    public void testPopulateDocument_with_success_chained() {
        final CrudAction crudActionAnnotation = actionWithSuccessChained.getAnnotation(CrudAction.class);

        final ElementGenerator generator = new CrudActionElementGenerator(crudActionAnnotation,
                                                                      TestAction.class.getCanonicalName(),
                                                                      actionWithSuccessChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINING_ON_SUCCESS, false, crudActionAnnotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing param thingy
        runCommonParamElementsTest(element, true, false, SUCCESS_JSP_NAME_WITH_SUCCESS_CHAINED,
                INPUT_JSP_NAME_WITH_SUCCESS_CHAINED);

        //testing results
        runCommonResultElementsTest(element, true, false, UPDATE_REQUEST_VIEW_WRAPPER,
                UPDATE_REQUEST_INPUT_VIEW_WRAPPER, crudActionAnnotation);
    }

    public void testPopulateDocument_with_both_chained() {
        final CrudAction crudActionAnnotation = actionWithBothChained.getAnnotation(CrudAction.class);

        final ElementGenerator generator = new CrudActionElementGenerator(crudActionAnnotation,
                                                                      TestAction.class.getCanonicalName(),
                                                                      actionWithBothChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINING_BOTH, false, crudActionAnnotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing param thingy
        runCommonParamElementsTest(element, true, true, SUCCESS_JSP_NAME_WITH_BOTH_CHAINED,
                INPUT_JSP_NAME_WITH_BOTH_CHAINED);

        //testing results
        runCommonResultElementsTest(element, true, true, DELETE_REQUEST_VIEW_WRAPPER,
                DELETE_REQUEST_INPUT_VIEW_WRAPPER, crudActionAnnotation);
    }

    public void testPopulateDocument_with_convertor() {
        final CrudAction crudActionAnnotation = actionWithConvertor.getAnnotation(CrudAction.class);

        final ElementGenerator generator = new CrudActionElementGenerator(crudActionAnnotation,
                                                                      TestAction.class.getCanonicalName(),
                                                                      actionWithConvertor.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CONVERTOR, true, crudActionAnnotation);
        assertEquals(CONVERTOR_WITHOUT_CHAINING, element.getAttribute(ACTION_CONVERTER));

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing param thingy
        runCommonParamElementsTest(element, false, false, SUCCESS_JSP_NAME_CONVERTOR,
                INPUT_JSP_NAME_CONVERTOR);

        //testing results
        runCommonResultElementsTest(element, false, false, UPDATE_SUCCESS_VIEW_WRAPPER,
                UPDATE_INPUT_VIEW_WRAPPER, crudActionAnnotation);
    }

    public void testPopulateDocument_with_convertor_and_input_chained () {
        final CrudAction crudActionAnnotation = actionWithConvertorAndInputChained.getAnnotation(CrudAction.class);

        final ElementGenerator generator = new CrudActionElementGenerator(crudActionAnnotation,
                                                                      TestAction.class.getCanonicalName(),
                                                                      actionWithConvertorAndInputChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINED_INPUT_AND_CONVERTOR, true, crudActionAnnotation);
        assertEquals(CONVERTOR_WITH_INPUT_CHAINING, element.getAttribute(ACTION_CONVERTER));

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing param thingy
        runCommonParamElementsTest(element, false, true, SUCCESS_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                INPUT_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR);

        //testing results
        runCommonResultElementsTest(element, false, true, READ_REQUEST_VIEW_WRAPPER,
                READ_REQUEST_INPUT_VIEW_WRAPPER, crudActionAnnotation);
    }

    


    private void runCommonResultElementsTest(final Element element, final boolean successChained, final boolean inputChained,
                                             final String successResult, final String inputResult,
                                             final CrudAction crudActionAnnotation) {
        final List<Element> resultTags = getElementNodes(element.getElementsByTagName(RESULT_TAG_NAME));
        assertEquals(2, resultTags.size());
        //testing success result
        final Element successResultTag = findElementInCollection(resultTags, RESULT_NAME, RESULT_NAME_SUCCESS);
        assertNotNull(successResultTag);
        if(successChained) {
            assertTrue(successResultTag.hasAttribute(RESULT_TYPE));
            assertEquals(RESULT_TYPE_CHAIN, successResultTag.getAttribute(RESULT_TYPE));
            assertEquals(crudActionAnnotation.onSuccess(), successResultTag.getTextContent());
        } else {
            assertEquals(prependWrapperDirName(successResult), successResultTag.getTextContent());
        }
        //testing input result
        final Element inputResultTag = findElementInCollection(resultTags, RESULT_NAME, RESULT_NAME_INPUT);
        assertNotNull(inputResultTag);
        if(inputChained) {
            assertTrue(inputResultTag.hasAttribute(RESULT_TYPE));
            assertEquals(RESULT_TYPE_CHAIN, inputResultTag.getAttribute(RESULT_TYPE));
            assertEquals(crudActionAnnotation.onInput(), inputResultTag.getTextContent());
        } else {
            assertEquals(prependWrapperDirName(inputResult), inputResultTag.getTextContent());
        }
    }

    private void runCommonParamElementsTest(final Element element, final boolean successChained, final boolean inputChained,
                                            final String successParamValue, final String inputParamValue) {
        final List<Element> paramTags = getElementNodes(element.getElementsByTagName(PARAM_TAG_NAME));
        short paramTagCount = 0;
        final Element successParamTag = findElementInCollection(paramTags, PARAM_NAME, SUCCESS_WRAPPER_ATTRIBUTE);
        final Element inputParamTag = findElementInCollection(paramTags, PARAM_NAME, INPUT_WRAPPER_ATTRIBUTE);
        if(!successChained) {
            paramTagCount++;
            validateParamTag(successParamTag, successParamValue);
        }
        if(!inputChained) {
            paramTagCount++;
            validateParamTag(inputParamTag, inputParamValue);
        }
        assertEquals(paramTagCount, paramTags.size());
    }

    private void validateParamTag(final Element paramTag, final String jspName) {
        assertNotNull(paramTag);
        assertEquals(jspName, paramTag.getTextContent());
    }

    private void runCommonInterceptorRefTest(final Element element) {
        final List<Element> interceptorRefs = getElementNodes(element.getElementsByTagName(INTERCEPTOR_REF_TAG_NAME));
        assertEquals(1, interceptorRefs.size());
        assertEquals(INTERCEPTOR_REF_VALUE, interceptorRefs.get(0).getAttribute(INTERCEPTOR_REF_NAME));
    }

    private void runCommonTestsForAction(Element element, String methodName, boolean hasConvertor,
                                         CrudAction crudActionAnnotation) {
        assertEquals(ACTION_TAG_NAME, element.getTagName());
        assertEquals(crudActionAnnotation.actionName(), element.getAttribute(ACTION_NAME));
        assertEquals(TestAction.class.getCanonicalName(), element.getAttribute(ACTION_CLASS));
        assertEquals(methodName, element.getAttribute(ACTION_METHOD));
        assertEquals(hasConvertor, element.hasAttribute(ACTION_CONVERTER));
    }

    private String prependWrapperDirName(String jspSimpleName) {
        return propertyReader.getProperty(WRAPPER_FILE_DIRECTORY_PATH) + "/" +jspSimpleName;
    }

    private Element findElementInCollection(List<Element> elements, String attributeName, String attributeValue) {
        for (Element elem : elements) {
            if (elem.hasAttribute(attributeName) && elem.getAttribute(attributeName).equals(attributeValue)) {
                return elem;
            }
        }
        return null;
    }
}
