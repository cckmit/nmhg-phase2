package tavant.twms.annotations.form.handler;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tavant.twms.annotations.form.annotation.Action;
import static tavant.twms.annotations.form.handler.ElementGenerator.*;
import tavant.twms.annotations.form.util.TestUtil;
import static tavant.twms.annotations.form.util.TestUtil.getElementNodes;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * User: Janmejay.singh
 * Date: Aug 1, 2007
 * Time: 6:06:46 PM
 */
public class NonCrudActionElementGeneratorTest extends AbstractElementGeneratorTest {
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

    public static final String MULTIACTION_ONE = "actionOne",
                               MULTIACTION_TWO = "actionTwo",
                               MULTIACTION_THREE = "actionThree";

    public static final String MULTIACTION_CONVERTOR_ONE_AND_TWO = "multiactionConvertor",
                               MULTIACTION_CONVERTOR_THREE_VALUED_NULL = "";

    public static final String MULTIACTION_INPUT_JSP = "input.jsp",
                               MULTIACTION_SUCCESS_ONE_JSP = "success1.jsp",
                               MULTIACTION_SUCCESS_TWO_AND_THREE_JSP = "success2and3.jsp",
                               MULTIACTION_SUCCESS_TWO_JSP = "success2.jsp";

    private class TestAction {
        @Action(
                actionNames = ACTION_NAME,
                onSuccesses = SUCCESS_JSP_NAME_WITHOUT_CHAINING,
                onInputs = INPUT_JSP_NAME_WITHOUT_CHAINING,
                interceptorRefs = INTERCEPTOR_REF_VALUE
        )
        public String methodWithoutChaining() {
            return "success";
        }

        @Action(
                actionNames = ACTION_NAME_WITH_INPUT_CHAINED,
                onSuccesses = SUCCESS_JSP_NAME_WITH_INPUT_CHAINED,
                onInputs = INPUT_JSP_NAME_WITH_INPUT_CHAINED,
                chainInputs = true,
                interceptorRefs = INTERCEPTOR_REF_VALUE
        )
        public String methodWithChainingOnInput() {
            return "input";
        }

        @Action(
                actionNames = ACTION_NAME_WITH_SUCCESS_CHAINED,
                onSuccesses = SUCCESS_JSP_NAME_WITH_SUCCESS_CHAINED,
                onInputs = INPUT_JSP_NAME_WITH_SUCCESS_CHAINED,
                chainSuccesses = true,
                interceptorRefs = INTERCEPTOR_REF_VALUE
        )
        public String methodWithChainingOnSuccess() {
            return "success";
        }

        @Action(
                actionNames = ACTION_NAME_WITH_BOTH_CHAINED,
                onSuccesses = SUCCESS_JSP_NAME_WITH_BOTH_CHAINED,
                onInputs = INPUT_JSP_NAME_WITH_BOTH_CHAINED,
                chainSuccesses = true,
                chainInputs = true,
                interceptorRefs = INTERCEPTOR_REF_VALUE
        )
        public String methodWithChainingBoth() {
            return "success";
        }

        @Action(
                actionNames = ACTION_NAME_CONVERTOR,
                onSuccesses = SUCCESS_JSP_NAME_CONVERTOR,
                onInputs = INPUT_JSP_NAME_CONVERTOR,
                interceptorRefs = INTERCEPTOR_REF_VALUE,
                convertors = CONVERTOR_WITHOUT_CHAINING
        )
        public String methodWithConvertor() {
            return "success";
        }

        @Action(
                actionNames = ACTION_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                onSuccesses = SUCCESS_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                onInputs = INPUT_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                chainInputs = true,
                interceptorRefs = INTERCEPTOR_REF_VALUE,
                convertors = CONVERTOR_WITH_INPUT_CHAINING
        )
        public String methodWithChainedInputAndConvertor() {
            return "success";
        }

        @Action(
                actionNames = {MULTIACTION_ONE, MULTIACTION_TWO},
                onSuccesses = {MULTIACTION_SUCCESS_ONE_JSP, MULTIACTION_SUCCESS_TWO_JSP},
                onInputs = MULTIACTION_INPUT_JSP,
                interceptorRefs = INTERCEPTOR_REF_VALUE,
                convertors = {}
        )
        public String methodWithMultipleActions() {
            return "success";
        }

        @Action(
                actionNames = {MULTIACTION_ONE, MULTIACTION_TWO, MULTIACTION_THREE},
                onSuccesses = {MULTIACTION_SUCCESS_ONE_JSP, MULTIACTION_SUCCESS_TWO_AND_THREE_JSP},
                onInputs = MULTIACTION_INPUT_JSP,
                interceptorRefs = INTERCEPTOR_REF_VALUE,
                convertors = {MULTIACTION_CONVERTOR_ONE_AND_TWO, MULTIACTION_CONVERTOR_ONE_AND_TWO,
                        MULTIACTION_CONVERTOR_THREE_VALUED_NULL}
        )
        public String methodWithMultipleActionsAndConvertors() {
            return "success";
        }
    }

    private Method actionWithoutChaining,
                   actionWithInputChained,
                   actionWithSuccessChained,
                   actionWithBothChained,
                   actionWithConvertor,
                   actionWithConvertorAndInputChained,
                   actionWithMultipleBindings,
                   actionWithMultipleBindingsAndConvertors;

    private final String METHOD_WITHOUT_CHAINING = "methodWithoutChaining",
                         METHOD_WITH_CHAINING_ON_INPUT = "methodWithChainingOnInput",
                         METHOD_WITH_CHAINING_ON_SUCCESS = "methodWithChainingOnSuccess",
                         METHOD_WITH_CHAINING_BOTH = "methodWithChainingBoth",
                         METHOD_WITH_CONVERTOR = "methodWithConvertor",
                         METHOD_WITH_CHAINED_INPUT_AND_CONVERTOR = "methodWithChainedInputAndConvertor",
                         METHOD_WITH_MULTIPLE_ACTIONS = "methodWithMultipleActions",
                         METHOD_WITH_MULTIPLE_BINDINGS_AND_CONVERTORS = "methodWithMultipleActionsAndConvertors";

    public NonCrudActionElementGeneratorTest() {
        init();
    }

    private void setupDocument() {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public NonCrudActionElementGeneratorTest(String arg) {
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
            actionWithMultipleBindings = TestAction.class.getMethod(METHOD_WITH_MULTIPLE_ACTIONS);
            actionWithMultipleBindingsAndConvertors = TestAction.class.getMethod(METHOD_WITH_MULTIPLE_BINDINGS_AND_CONVERTORS);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }




    public void testPopulateDocument_without_chaining() {
        final Action annotation = actionWithoutChaining.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithoutChaining.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITHOUT_CHAINING, false, annotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing results
        runCommonResultElementsTest(element, false, false, SUCCESS_JSP_NAME_WITHOUT_CHAINING,
                INPUT_JSP_NAME_WITHOUT_CHAINING, annotation);
    }

    public void testPopulateDocument_with_input_chained() {
        final Action annotation = actionWithInputChained.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithInputChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINING_ON_INPUT, false, annotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing results
        runCommonResultElementsTest(element, false, true, SUCCESS_JSP_NAME_WITH_INPUT_CHAINED,
                INPUT_JSP_NAME_WITH_INPUT_CHAINED, annotation);
    }

    public void testPopulateDocument_with_success_chained() {
        final Action annotation = actionWithSuccessChained.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithSuccessChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINING_ON_SUCCESS, false, annotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing results
        runCommonResultElementsTest(element, true, false, SUCCESS_JSP_NAME_WITH_SUCCESS_CHAINED,
                INPUT_JSP_NAME_WITH_SUCCESS_CHAINED, annotation);
    }

    public void testPopulateDocument_with_both_chained() {
        final Action annotation = actionWithBothChained.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithBothChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINING_BOTH, false, annotation);

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing results
        runCommonResultElementsTest(element, true, true, SUCCESS_JSP_NAME_WITH_BOTH_CHAINED,
                INPUT_JSP_NAME_WITH_BOTH_CHAINED, annotation);
    }

    public void testPopulateDocument_with_convertor() {
        final Action annotation = actionWithConvertor.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithConvertor.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CONVERTOR, true, annotation);
        assertEquals(CONVERTOR_WITHOUT_CHAINING, element.getAttribute(ACTION_CONVERTER));

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing results
        runCommonResultElementsTest(element, false, false, SUCCESS_JSP_NAME_CONVERTOR,
                INPUT_JSP_NAME_CONVERTOR, annotation);
    }

    public void testPopulateDocument_with_convertor_and_input_chained () {
        final Action annotation = actionWithConvertorAndInputChained.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithConvertorAndInputChained.getName());

        Element element = createAndReturnElement(generator);

        //testing action element
        runCommonTestsForAction(element, METHOD_WITH_CHAINED_INPUT_AND_CONVERTOR, true, annotation);
        assertEquals(CONVERTOR_WITH_INPUT_CHAINING, element.getAttribute(ACTION_CONVERTER));

        //testing interceptor element
        runCommonInterceptorRefTest(element);

        //testing results
        runCommonResultElementsTest(element, false, true, SUCCESS_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR,
                INPUT_JSP_NAME_WITH_CHAINED_INPUT_AND_CONVERTOR, annotation);
    }


    public void testPopulateDocument_with_multiple_bindings() {
        final Action annotation = actionWithMultipleBindings.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                                             TestAction.class.getCanonicalName(),
                                                                             actionWithMultipleBindings.getName());

        List<Element> elements = createAndReturnElements(generator, 2);

        int i = 0;
        for(Element action : elements) {
            runCommonTestsForAction(action, METHOD_WITH_MULTIPLE_ACTIONS, false, annotation, i++);
            runCommonInterceptorRefTest(action);
            runCommonResultElementsTest(action, false, false,
                    i%2 == 1 ? MULTIACTION_SUCCESS_ONE_JSP : MULTIACTION_SUCCESS_TWO_JSP,
                    MULTIACTION_INPUT_JSP, annotation, i);
        }
    }

    public void testPopulateDocument_with_multiple_bindings_and_convertors() {
        final Action annotation = actionWithMultipleBindingsAndConvertors.getAnnotation(Action.class);

        final ElementGenerator generator = new NonCrudActionElementGenerator(annotation,
                                                 TestAction.class.getCanonicalName(),
                                                 actionWithMultipleBindingsAndConvertors.getName());

        List<Element> elements = createAndReturnElements(generator, 3);

        int i = 0;
        for(Element action : elements) {
            runCommonTestsForAction(action, METHOD_WITH_MULTIPLE_BINDINGS_AND_CONVERTORS, i < 2, annotation, i++);
            runCommonInterceptorRefTest(action);
            runCommonResultElementsTest(action, false, false,
                    i == 1 ? MULTIACTION_SUCCESS_ONE_JSP : MULTIACTION_SUCCESS_TWO_AND_THREE_JSP,
                    MULTIACTION_INPUT_JSP, annotation, i);
        }
    }

    private List<Element> createAndReturnElements(final ElementGenerator generator, final int expectedNumber) {
        Element rootElem = document.createElement("root");
        generator.populateDocument(document, propertyReader, rootElem);
        NodeList children = rootElem.getChildNodes();
        List<Element> elements = TestUtil.getElementNodes(children);
        assertEquals(expectedNumber, elements.size());
        return elements;
    }


    private void runCommonResultElementsTest(final Element element, final boolean successChained, final boolean inputChained,
                                             final String successResult, final String inputResult,
                                             final Action annotation) {
        runCommonResultElementsTest(
                element, successChained, inputChained, successResult, inputResult, annotation, 0);
    }

    private void runCommonResultElementsTest(final Element element, final boolean successChained, final boolean inputChained,
                                             final String successResult, final String inputResult,
                                             final Action annotation, final int index) {
        final List<Element> resultTags = getElementNodes(element.getElementsByTagName(RESULT_TAG_NAME));
        assertEquals(2, resultTags.size());
        //testing success result
        final Element successResultTag = findElementInCollection(resultTags, RESULT_NAME, RESULT_NAME_SUCCESS);
        assertNotNull(successResultTag);
        if(successChained) {
            assertTrue(successResultTag.hasAttribute(RESULT_TYPE));
            assertEquals(RESULT_TYPE_CHAIN, successResultTag.getAttribute(RESULT_TYPE));
            assertEquals(annotation.onSuccesses()[index], successResultTag.getTextContent());
        } else {
            assertEquals(successResult, successResultTag.getTextContent());
        }
        //testing input result
        final Element inputResultTag = findElementInCollection(resultTags, RESULT_NAME, RESULT_NAME_INPUT);
        assertNotNull(inputResultTag);
        if(inputChained) {
            assertTrue(inputResultTag.hasAttribute(RESULT_TYPE));
            assertEquals(RESULT_TYPE_CHAIN, inputResultTag.getAttribute(RESULT_TYPE));
            assertEquals(annotation.onInputs()[index], inputResultTag.getTextContent());
        } else {
            assertEquals(inputResult, inputResultTag.getTextContent());
        }
    }

    private void runCommonInterceptorRefTest(final Element element) {
        final List<Element> interceptorRefs = getElementNodes(element.getElementsByTagName(INTERCEPTOR_REF_TAG_NAME));
        assertEquals(1, interceptorRefs.size());
        assertEquals(INTERCEPTOR_REF_VALUE, interceptorRefs.get(0).getAttribute(INTERCEPTOR_REF_NAME));
    }

    private void runCommonTestsForAction(Element element, String methodName, boolean hasConvertor,
                                         Action annotation) {
        runCommonTestsForAction(element, methodName, hasConvertor, annotation, 0);
    }

    private void runCommonTestsForAction(Element element, String methodName, boolean hasConvertor,
                                         Action annotation, int actionIndex) {
        assertEquals(ACTION_TAG_NAME, element.getTagName());
        assertEquals(annotation.actionNames()[actionIndex], element.getAttribute(ACTION_NAME));
        assertEquals(TestAction.class.getCanonicalName(), element.getAttribute(ACTION_CLASS));
        assertEquals(methodName, element.getAttribute(ACTION_METHOD));
        assertEquals(hasConvertor, element.hasAttribute(ACTION_CONVERTER));
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
