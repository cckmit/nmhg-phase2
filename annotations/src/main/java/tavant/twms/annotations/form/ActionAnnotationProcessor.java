package tavant.twms.annotations.form;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import tavant.twms.annotations.form.annotation.Action;
import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.handler.XmlGeneratorFactory;
import tavant.twms.annotations.form.handler.ElementGeneratorFactoryImpl;
import tavant.twms.annotations.form.util.EnvPropertyReader;
import tavant.twms.annotations.form.util.EnvPropertyReaderFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : janmejay.singh
 *         Date: Jul 17, 2007
 *         Time: 9:31:10 PM
 */
class ActionAnnotationProcessor implements AnnotationProcessor {

    private EnvPropertyReader propertyReader;
    private Collection<Declaration> crudActionElems, actionElems;
    private XmlGeneratorFactory xmlGeneratorFactory;

    public ActionAnnotationProcessor(AnnotationProcessorEnvironment env,
                                     EnvPropertyReaderFactory propertyReaderFactory,
                                     XmlGeneratorFactory xmlGeneratorFactory) {
        propertyReader = propertyReaderFactory.getPropertyReader(env);
        // i want to listen ONLY to @CrudAction guys... so configuring to listen only to that.
        crudActionElems = env.getDeclarationsAnnotatedWith(
                            (AnnotationTypeDeclaration) env.getTypeDeclaration(CrudAction.class.getCanonicalName()));
        actionElems = env.getDeclarationsAnnotatedWith(
                            (AnnotationTypeDeclaration) env.getTypeDeclaration(Action.class.getCanonicalName()));
        this.xmlGeneratorFactory = xmlGeneratorFactory;
    }

    public void process() {
        Set<MethodDeclaration> actions = new HashSet<MethodDeclaration>();
        actions.addAll(getCrudActions());
        actions.addAll(getNonCrudActions());

        xmlGeneratorFactory.getXmlGenerator(actions, propertyReader,
                new ElementGeneratorFactoryImpl())
                .generateDocument();
    }

    private Set<MethodDeclaration> getNonCrudActions() {
        final Set<MethodDeclaration> annotatedActions = new HashSet<MethodDeclaration>();

        DeclarationVisitor visitor = new SimpleDeclarationVisitor() {
            @Override
            public void visitMethodDeclaration(MethodDeclaration methodDeclaration) {
                annotatedActions.add(methodDeclaration);
            }
        };

        for (Declaration decl : actionElems) {
            decl.accept(visitor);
        }
        return annotatedActions;
    }

    private Set<MethodDeclaration> getCrudActions() {
        final Set<MethodDeclaration> annotatedCrudActions = new HashSet<MethodDeclaration>();
        
        DeclarationVisitor crudActionVisitor = new SimpleDeclarationVisitor() {
            @Override
            public void visitMethodDeclaration(MethodDeclaration methodDeclaration) {
                annotatedCrudActions.add(methodDeclaration);
            }
        };

        for (Declaration decl : crudActionElems) {
            decl.accept(crudActionVisitor);
        }

        return annotatedCrudActions;
    }
}
