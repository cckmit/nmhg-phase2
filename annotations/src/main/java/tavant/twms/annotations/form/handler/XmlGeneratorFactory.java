package tavant.twms.annotations.form.handler;

import com.sun.mirror.declaration.MethodDeclaration;
import tavant.twms.annotations.form.util.EnvPropertyReader;

import java.util.Set;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 5:44:57 PM
 */
public interface XmlGeneratorFactory {
    XmlGenerator getXmlGenerator(Set<MethodDeclaration> methodDecl,
                                 EnvPropertyReader propertyReader,
                                 ElementGeneratorFactory elementGeneratorFactory
    );
}
