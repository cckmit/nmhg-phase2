package tavant.twms.annotations.form.handler;

import com.sun.mirror.declaration.MethodDeclaration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.annotation.Action;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import static tavant.twms.annotations.form.util.EnvPropertyReader.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;

/**
 * @author : janmejay.singh
 *         Date: Jul 19, 2007
 *         Time: 10:06:51 AM
 */
public class StrutsActionXmlGenerator implements XmlGenerator {

    static final String PACKAGE_ATTRIBUTE_NAME = "name",
                        PACKAGE_ATTRIBUTE_EXTENDS = "extends",
                        PACKAGE_ATTRIBUTE_ABSTRACT = "abstract",
                        PACKAGE_ATTRIBUTE_EXTERNAL_REFERENCE_RESOLVER = "externalReferenceResolver",
                        PACKAGE_ATTRIBUTE_NAMESPACE = "namespace";

    private Set<ElementGenerator> actions;
    private EnvPropertyReader propertyReader;
    private ElementGeneratorFactory elementGeneratorFactory;

    public StrutsActionXmlGenerator(Set<MethodDeclaration> annotatedActions,
                              EnvPropertyReader propertyReader,
                              ElementGeneratorFactory elementGeneratorFactory) {
        this.actions = new HashSet<ElementGenerator>();
        this.elementGeneratorFactory = elementGeneratorFactory;
        this.propertyReader = propertyReader;
        populateActionData(annotatedActions);
    }

    private void populateActionData(Set<MethodDeclaration> annotatedActions) {
        for (MethodDeclaration methodDeclaration : annotatedActions) {
            Annotation crudActionAnnotation = methodDeclaration.getAnnotation(CrudAction.class);
            Annotation nonCrudActionAnnotation = methodDeclaration.getAnnotation(Action.class);
            if (crudActionAnnotation != null) addElementGenerator(methodDeclaration, crudActionAnnotation);
            if (nonCrudActionAnnotation != null) addElementGenerator(methodDeclaration, nonCrudActionAnnotation);
        }
    }

    private void addElementGenerator(MethodDeclaration methodDeclaration, Annotation annotation) {
        actions.add(
                    elementGeneratorFactory.getElementGenerator(annotation,
                            methodDeclaration.getDeclaringType().getQualifiedName(),
                            methodDeclaration.getSimpleName()));
    }

    public void generateDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            // create xml document
            builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlVersion("1.0");

            Element packageElem = putRootLevelWrappers(document);

            // create actions
            for (ElementGenerator entry : actions) {
                entry.populateDocument(document, propertyReader, packageElem);
            }

            //and now dump it to file.
            saveXml(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Element putRootLevelWrappers(Document document) {
        Element struts = document.createElement("struts");
        document.appendChild(struts);
        Element packageElem = createPackageElem(document);
        struts.appendChild(packageElem);
        return packageElem;
    }

    private void saveXml(Document document) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", 2);
        Transformer transformer = tf.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                                      "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                                      propertyReader.getProperty(DTD_PATH));
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        Source source = new DOMSource(document);
        final FileOutputStream outStream = new FileOutputStream(propertyReader.getProperty(OUT_FILE));
        try {
            Result result = new StreamResult(new OutputStreamWriter(outStream));
            transformer.transform(source, result);
        } finally {
            outStream.flush();
            outStream.close();
        }
    }

    private Element createPackageElem(Document document) {
        Element packageElem = document.createElement("package");
        putPackageName(packageElem);
        putPackageParentName(packageElem);
        putPackageAbstractAttribute(packageElem);
        putPackageExternalRefResolver(packageElem);
        putPackageNamespace(packageElem);
        return packageElem;
    }

    private void putPackageNamespace(Element packageElem) {
        final String packageNamespace = propertyReader.getProperty(PACKAGE_NAMESPACE_VALUE);
        if(packageNamespace != null) packageElem.setAttribute(PACKAGE_ATTRIBUTE_NAMESPACE, packageNamespace);
    }

    private void putPackageExternalRefResolver(Element packageElem) {
        final String packageExtRefResolver = propertyReader.getProperty(PACKAGE_EXTERNAL_REFERENCE_RESOLVER_VALUE);
        if(packageExtRefResolver != null) packageElem.setAttribute(PACKAGE_ATTRIBUTE_EXTERNAL_REFERENCE_RESOLVER,
                packageExtRefResolver);
    }

    private void putPackageAbstractAttribute(Element packageElem) {
        final String packageAbstractAttrib = propertyReader.getProperty(PACKAGE_ABSTRACT_VALUE);
        if(packageAbstractAttrib != null) packageElem.setAttribute(PACKAGE_ATTRIBUTE_ABSTRACT, packageAbstractAttrib);
    }

    private void putPackageParentName(Element packageElem) {
        final String packageExtends = propertyReader.getProperty(PACKAGE_EXTENDS_VALUE);
        assert packageExtends != null;
        packageElem.setAttribute(PACKAGE_ATTRIBUTE_EXTENDS, packageExtends);
    }

    private void putPackageName(Element packageElem) {
        final String packageName = propertyReader.getProperty(PACKAGE_NAME_VALUE);
        assert packageName != null;
        packageElem.setAttribute(PACKAGE_ATTRIBUTE_NAME, packageName);
    }
}
