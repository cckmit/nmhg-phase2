package tavant.twms.annotations.form.handler;

import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import org.apache.commons.io.FileUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.annotation.Action;
import tavant.twms.annotations.form.ActionType;
import static tavant.twms.annotations.form.handler.StrutsActionXmlGenerator.*;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import static tavant.twms.annotations.form.util.EnvPropertyReader.*;
import static tavant.twms.annotations.form.util.TestUtil.getElementNodes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: janmejay.singh
 * Date: Jul 27, 2007
 * Time: 1:40:49 PM
 */
public class StrutsActionXmlGeneratorTest extends MockObjectTestCase {

    private static final String ACTION_NAME_ONE = "actionOne",
                                ACTION_NAME_TWO = "actionTwo",
                                ACTION_NAME_THREE = "actionThree",
                                INTERCEPTOR_REF_ONE = "twms_prepare_params",
                                INTERCEPTOR_REF_TWO = "twms_validation",
                                INTERCEPTOR_REF_THREE = "twms_validation_stack_3",
                                INPUT_JSP_ONE = "input.jsp",
                                INPUT_ACTION_TWO = "inputTwo",
                                INPUT_ACTION_THREE = "inputThree",
                                SUCCESS_JSP_ONE = "success.jsp",
                                SUCCESS_JSP_THREE = "success3.jsp",
                                SUCCESS_JSP_TWO = "success2.jsp";

    //NOTE : JDK for some reason... doesn't allow constants to be used as value to enum attributes.
    //so im using these constants here... but the actual @CrudAction annotation, is using the actual enum constants.
    //please make sure.. the two guys are in sync.
    //this is just to let me do some quick asserts... without bothering to see the actual implementation.
    public static final ActionType ACTION_TYPE_ONE = ActionType.CREATE_REQUEST,
                                   ACTION_TYPE_TWO = ActionType.UPDATE_SUBMIT;

    private CrudAction actionOneAnnotation, actionTwoAnnotation;
    private Action actionThreeAnnotation;
    private String methodNameOne, methodNameTwo, methodNameThree;
    private TypeDeclaration typeDeclaration;

    private class TestClass {

        @CrudAction(
                actionName = ACTION_NAME_ONE,
                interceptorRef = INTERCEPTOR_REF_ONE,
                onInput = INPUT_JSP_ONE,
                onSuccess = SUCCESS_JSP_ONE,
                actionType = ActionType.CREATE_REQUEST//NOTE : please make sure it is same as ACTION_TYPE_ONE
        )
        public String methodOne() {
            return "success";
        }

        @CrudAction(
                actionName = ACTION_NAME_TWO,
                interceptorRef = INTERCEPTOR_REF_TWO,
                onInput = INPUT_ACTION_TWO,
                chainInput = true,
                onSuccess = SUCCESS_JSP_TWO,
                actionType = ActionType.UPDATE_SUBMIT//NOTE : please make sure it is same as ACTION_TYPE_TWO
        )
        public String methodTwo() {
            return "input";
        }

        @Action(
                actionNames = ACTION_NAME_THREE,
                interceptorRefs = INTERCEPTOR_REF_THREE,
                chainInputs = true,
                onSuccesses = SUCCESS_JSP_THREE,
                onInputs = INPUT_ACTION_THREE
        )
        public String methodThree() {
            return "success";
        }
    }

    private MethodDeclaration methodDeclOne, methodDeclTwo, methodDeclThree;

    private Set<MethodDeclaration> methodDeclarations;

    private EnvPropertyReader propertyReader;

    private ElementGeneratorFactory elementGeneratorFactory;

    private final String className = TestClass.class.getName();

    public final String OUT_FILE_NAME = "/testXmlFile.xml";

    public final String DTD_FILE_PATH = StrutsActionXmlGeneratorTest.class.getResource("/struts-2.0.dtd").getPath();

    protected void setUp() throws Exception {
        super.setUp();
        methodDeclOne = mock(MethodDeclaration.class);
        methodDeclTwo = mock(MethodDeclaration.class);
        methodDeclThree = mock(MethodDeclaration.class);
        methodDeclarations = new HashSet<MethodDeclaration>();
        methodDeclarations.add(methodDeclOne);
        methodDeclarations.add(methodDeclTwo);
        methodDeclarations.add(methodDeclThree);
        Method actionMethodOne = TestClass.class.getMethod("methodOne");
        Method actionMethodTwo = TestClass.class.getMethod("methodTwo");
        Method actionMethodThree = TestClass.class.getMethod("methodThree");
        propertyReader = new EnvPropertyReader() {
            public String getProperty(String key) {
                if(key.equals(OUT_FILE)) {
                    return System.getProperty("java.io.tmpdir") + OUT_FILE_NAME;
                } else if (key.equals(DTD_PATH)) {
                    return DTD_FILE_PATH;
                }
                return key;
            }
        };
        elementGeneratorFactory = mock(ElementGeneratorFactory.class);

        actionOneAnnotation = actionMethodOne.getAnnotation(CrudAction.class);
        methodNameOne = actionMethodOne.getName();

        actionTwoAnnotation = actionMethodOne.getAnnotation(CrudAction.class);
        methodNameTwo = actionMethodTwo.getName();

        actionThreeAnnotation = actionMethodThree.getAnnotation(Action.class);
        methodNameThree = actionMethodThree.getName();

        typeDeclaration = mock(TypeDeclaration.class);
    }

    public void testConstructor_testing_reading_of_annotation_data_for_creating_element_generators() {
        checking(new Expectations() {{

            one(methodDeclOne).getAnnotation(CrudAction.class);
                will(returnValue(actionOneAnnotation));

            one(methodDeclOne).getAnnotation(Action.class);
                will(returnValue(null));

            one(methodDeclOne).getDeclaringType();
                will(returnValue(typeDeclaration));

            one(methodDeclOne).getSimpleName();
                will(returnValue(methodNameOne));

            one(methodDeclTwo).getAnnotation(CrudAction.class);
                will(returnValue(actionTwoAnnotation));

            one(methodDeclTwo).getAnnotation(Action.class);
                will(returnValue(null));

            one(methodDeclTwo).getDeclaringType();
                will(returnValue(typeDeclaration));

            one(methodDeclTwo).getSimpleName();
                will(returnValue(methodNameTwo));

            one(methodDeclThree).getAnnotation(CrudAction.class);
                will(returnValue(null));

            one(methodDeclThree).getAnnotation(Action.class);
                will(returnValue(actionThreeAnnotation));

            one(methodDeclThree).getDeclaringType();
                will(returnValue(typeDeclaration));

            one(methodDeclThree).getSimpleName();
                will(returnValue(methodNameThree));

            exactly(3).of(typeDeclaration).getQualifiedName();
                will(returnValue(className));

            final ElementGenerator elementGenerator = mock(ElementGenerator.class);

            one(elementGeneratorFactory).getElementGenerator(actionOneAnnotation, className, methodNameOne);
                will(returnValue(elementGenerator));

            one(elementGeneratorFactory).getElementGenerator(actionTwoAnnotation, className, methodNameTwo);
                will(returnValue(elementGenerator));

            one(elementGeneratorFactory).getElementGenerator(actionThreeAnnotation, className, methodNameThree);
                will(returnValue(elementGenerator));
        }});
        new StrutsActionXmlGenerator(methodDeclarations, propertyReader, elementGeneratorFactory);
    }

    public void testGenerateDocument() throws ParserConfigurationException, IOException, SAXException {
        final String DUMMY_CRUD_TAG = "dummyCrudTag";
        final String DUMMY_NON_CRUD_TAG = "dummyNonCrudTag";
        final ElementGenerator elementGeneratorOne = new ElementGenerator() {
            public void populateDocument(Document document, EnvPropertyReader propertyReader, Element parentElem) {
                parentElem.appendChild(document.createElement(DUMMY_CRUD_TAG));
            }
        };
        final ElementGenerator elementGeneratorTwo = new ElementGenerator() {
            public void populateDocument(Document document, EnvPropertyReader propertyReader, Element parentElem) {
                parentElem.appendChild(document.createElement(DUMMY_CRUD_TAG));
            }
        };
        final ElementGenerator elementGeneratorThree = new ElementGenerator() {
            public void populateDocument(Document document, EnvPropertyReader propertyReader, Element parentElem) {
                parentElem.appendChild(document.createElement(DUMMY_NON_CRUD_TAG));
            }
        };
        checking(new Expectations() {{
            allowing(methodDeclOne).getAnnotation(CrudAction.class);
                will(returnValue(actionOneAnnotation));

            allowing(methodDeclOne).getAnnotation(Action.class);
                will(returnValue(null));

            allowing(methodDeclOne).getDeclaringType();
                will(returnValue(typeDeclaration));

            allowing(methodDeclOne).getSimpleName();
                will(returnValue(methodNameOne));

            allowing(methodDeclTwo).getAnnotation(CrudAction.class);
                will(returnValue(actionTwoAnnotation));

            allowing(methodDeclTwo).getAnnotation(Action.class);
                will(returnValue(null));

            allowing(methodDeclTwo).getDeclaringType();
                will(returnValue(typeDeclaration));

            allowing(methodDeclTwo).getSimpleName();
                will(returnValue(methodNameTwo));

            allowing(methodDeclThree).getAnnotation(CrudAction.class);
                will(returnValue(null));

            allowing(methodDeclOne).getAnnotation(Action.class);
                will(returnValue(actionThreeAnnotation));

            allowing(methodDeclThree).getDeclaringType();
                will(returnValue(typeDeclaration));

            allowing(methodDeclThree).getSimpleName();
                will(returnValue(methodNameThree));

            allowing(typeDeclaration).getQualifiedName();
                will(returnValue(TestClass.class.getCanonicalName()));

            allowing(elementGeneratorFactory).
                    getElementGenerator(with(an(Annotation.class)), with(a(String.class)), with(a(String.class)));
                will(onConsecutiveCalls(
                        returnValue(elementGeneratorOne),
                        returnValue(elementGeneratorTwo),
                        returnValue(elementGeneratorThree)//should not need more....
                ));

            ignoring(methodDeclOne);
            ignoring(methodDeclTwo);
            ignoring(methodDeclThree);
            ignoring(typeDeclaration);
            ignoring(elementGeneratorFactory);
        }});
        XmlGenerator generator = new StrutsActionXmlGenerator(methodDeclarations, propertyReader, elementGeneratorFactory);
        generator.generateDocument();
        validateStrutsXml(DUMMY_CRUD_TAG, DUMMY_NON_CRUD_TAG);
        FileUtils.forceDelete(new File(propertyReader.getProperty(OUT_FILE)));
    }

    private void validateStrutsXml(final String dummy_crud_tag, final String dummy_non_crud_tag)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(propertyReader.getProperty(OUT_FILE));
        document.normalizeDocument();
        assertEquals("1.0", document.getXmlVersion());
        assertEquals("UTF-8", document.getXmlEncoding());
        final List<Element> elementNodes = getElementNodes(document.getChildNodes());
        assertEquals(1, elementNodes.size());
        final Element strutsTag = elementNodes.get(0);
        assertEquals("struts", strutsTag.getNodeName());
        final List<Element> strutsTagChildElements = getElementNodes(strutsTag.getChildNodes());
        assertEquals(1, strutsTagChildElements.size());
        final Element packageTag = strutsTagChildElements.get(0);
        validatePackageNode(packageTag);
        assertEquals("package", packageTag.getNodeName());
        final List<Element> packageNodeChildren = getElementNodes(packageTag.getChildNodes());
        assertEquals(3, packageNodeChildren.size());
        int dummyCrudActionCount = 0, dummyNonCrudActionCount = 0;
        for(int i = 0; i < 3; i++) {
            if(dummy_crud_tag.equals(packageNodeChildren.get(i).getNodeName())) dummyCrudActionCount++;
            if(dummy_non_crud_tag.equals(packageNodeChildren.get(i).getNodeName())) dummyNonCrudActionCount++;
        }
        assertEquals(2, dummyCrudActionCount);
        assertEquals(1, dummyNonCrudActionCount);
    }

    private void validatePackageNode(Element packageTag) {
        assertEquals(propertyReader.getProperty(PACKAGE_EXTENDS_VALUE), packageTag.getAttribute(PACKAGE_ATTRIBUTE_EXTENDS));
        assertEquals(propertyReader.getProperty(PACKAGE_ABSTRACT_VALUE),
                packageTag.getAttribute(PACKAGE_ATTRIBUTE_ABSTRACT));
        assertEquals(propertyReader.getProperty(PACKAGE_EXTERNAL_REFERENCE_RESOLVER_VALUE),
                packageTag.getAttribute(PACKAGE_ATTRIBUTE_EXTERNAL_REFERENCE_RESOLVER));
        assertEquals(propertyReader.getProperty(PACKAGE_NAME_VALUE), packageTag.getAttribute(PACKAGE_ATTRIBUTE_NAME));
        assertEquals(propertyReader.getProperty(PACKAGE_NAMESPACE_VALUE),
                packageTag.getAttribute(PACKAGE_ATTRIBUTE_NAMESPACE));
    }
}
