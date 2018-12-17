package tavant.twms.annotations.form;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import tavant.twms.annotations.form.handler.XmlGeneratorFactoryImpl;
import static tavant.twms.annotations.form.util.EnvPropertyReader.*;
import tavant.twms.annotations.form.util.EnvPropertyReaderFactoryImpl;
import tavant.twms.annotations.form.annotation.CrudAction;
import tavant.twms.annotations.form.annotation.Action;

import java.util.Arrays;
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;
import java.util.Set;

/**
 * @author : janmejay.singh
 *         Date: Jul 17, 2007
 *         Time: 8:34:00 PM
 */
public class FormFrameworkAnnotationProcessorFactoryImpl implements AnnotationProcessorFactory {

    private static final Collection<String> supportedAnnotations =
            unmodifiableCollection(Arrays.asList(CrudAction.class.getCanonicalName(),
                                                 Action.class.getCanonicalName()));

    private static final Collection<String> supportedOptions = unmodifiableCollection(
                                                                Arrays.asList("-A" + OUT_FILE,
                                                                              "-A" + DTD_PATH,
                                                                              "-A" + CREATE_REQUEST_VIEW_WRAPPER,
                                                                              "-A" + CREATE_REQUEST_INPUT_VIEW_WRAPPER,
                                                                              "-A" + CREATE_INPUT_VIEW_WRAPPER,
                                                                              "-A" + CREATE_SUCCESS_VIEW_WRAPPER,
                                                                              "-A" + UPDATE_REQUEST_VIEW_WRAPPER,
                                                                              "-A" + UPDATE_REQUEST_INPUT_VIEW_WRAPPER,
                                                                              "-A" + UPDATE_INPUT_VIEW_WRAPPER,
                                                                              "-A" + UPDATE_SUCCESS_VIEW_WRAPPER,
                                                                              "-A" + DELETE_REQUEST_INPUT_VIEW_WRAPPER,
                                                                              "-A" + DELETE_INPUT_VIEW_WRAPPER,
                                                                              "-A" + DELETE_SUCCESS_VIEW_WRAPPER,
                                                                              "-A" + VIEW_REQUEST_VIEW_WRAPPER,
                                                                              "-A" + VIEW_REQUEST_INPUT_VIEW_WRAPPER,
                                                                              "-A" + VIEW_INPUT_VIEW_WRAPPER,
                                                                              "-A" + VIEW_SUCCESS_VIEW_WRAPPER,
                                                                              "-A" + PACKAGE_ABSTRACT_VALUE,
                                                                              "-A" + PACKAGE_EXTENDS_VALUE,
                                                                              "-A" + PACKAGE_NAME_VALUE,
                                                                              "-A" + PACKAGE_NAMESPACE_VALUE,
                                                                              "-A" + WRAPPER_FILE_DIRECTORY_PATH));

    public Collection<String> supportedOptions() {
        return supportedOptions;
    }

    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotations;
    }

    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> set, AnnotationProcessorEnvironment env) {
        if (set.isEmpty()) {
            return AnnotationProcessors.NO_OP;
        } else {
            return new ActionAnnotationProcessor(env, new EnvPropertyReaderFactoryImpl(), new XmlGeneratorFactoryImpl());
        }
    }
}
