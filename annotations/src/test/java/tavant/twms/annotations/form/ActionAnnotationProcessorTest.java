package tavant.twms.annotations.form;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitor;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import tavant.twms.annotations.form.handler.ElementGeneratorFactory;
import tavant.twms.annotations.form.handler.XmlGenerator;
import tavant.twms.annotations.form.handler.XmlGeneratorFactory;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import tavant.twms.annotations.form.util.EnvPropertyReaderFactory;
import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.annotation.Action;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 6:21:46 PM
 */
public class ActionAnnotationProcessorTest extends MockObjectTestCase {

    private EnvPropertyReaderFactory envPropertyReaderFactory;
    private XmlGeneratorFactory xmlGeneratorFactory;
    private AnnotationProcessorEnvironment env;

    protected void setUp() throws Exception {
        super.setUp();
        envPropertyReaderFactory = mock(EnvPropertyReaderFactory.class);
        xmlGeneratorFactory = mock(XmlGeneratorFactory.class);
        env = mock(AnnotationProcessorEnvironment.class);
    }

    public void testConstructor() {
        final AnnotationTypeDeclaration mockAnnotationTypeDeclaration = mock(AnnotationTypeDeclaration.class);
        checking(new Expectations() {{
            one(env).getTypeDeclaration(CrudAction.class.getCanonicalName());
                will(returnValue(mockAnnotationTypeDeclaration));
            one(env).getTypeDeclaration(Action.class.getCanonicalName());
                will(returnValue(mockAnnotationTypeDeclaration));
            one(envPropertyReaderFactory).getPropertyReader(env);
            exactly(2).of(env).getDeclarationsAnnotatedWith(mockAnnotationTypeDeclaration);
        }});
        new ActionAnnotationProcessor(env, envPropertyReaderFactory, xmlGeneratorFactory);
    }

    @SuppressWarnings({"unchecked"})
    public void testProcess() {
        final AnnotationTypeDeclaration mockAnnotationTypeDeclaration = mock(AnnotationTypeDeclaration.class);
        final Collection<Declaration> decl = new HashSet<Declaration>();
        final Declaration declarationOne = mock(Declaration.class);
        final Declaration declarationTwo = mock(Declaration.class);
        decl.add(declarationOne);
        decl.add(declarationTwo);
        checking(new Expectations() {{
            ignoring(env).getTypeDeclaration(CrudAction.class.getCanonicalName());
                will(returnValue(mockAnnotationTypeDeclaration));
            ignoring(env).getTypeDeclaration(Action.class.getCanonicalName());
                will(returnValue(mockAnnotationTypeDeclaration));
            ignoring(env).getDeclarationsAnnotatedWith(mockAnnotationTypeDeclaration);
                will(returnValue(decl));
            ignoring(envPropertyReaderFactory);
        }});
        final ActionAnnotationProcessor annotationProcessor =
                new ActionAnnotationProcessor(env, envPropertyReaderFactory, xmlGeneratorFactory);
        checking(new Expectations() {{
            exactly(2).of(declarationOne).accept(with(a(DeclarationVisitor.class)));//two because we visit it 2 times....
            exactly(2).of(declarationTwo).accept(with(a(DeclarationVisitor.class)));//for CrudAction and Action
            XmlGenerator xmlGenerator = mock(XmlGenerator.class);
            one(xmlGeneratorFactory).getXmlGenerator(with(a(HashSet.class)),
                                                     with(an(EnvPropertyReader.class)),
                                                     with(a(ElementGeneratorFactory.class)));
                will(returnValue(xmlGenerator));
            one(xmlGenerator).generateDocument();
        }});
        annotationProcessor.process();
    }
}
