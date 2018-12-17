package tavant.twms.annotations.form.handler;

import com.sun.mirror.declaration.MethodDeclaration;
import tavant.twms.annotations.form.util.EnvPropertyReader;

import java.util.Set;

/**
 * @author : janmejay.singh
 *         Date: Jul 26, 2007
 *         Time: 4:10:48 PM
 */
public class XmlGeneratorFactoryImpl implements XmlGeneratorFactory {
    public XmlGenerator getXmlGenerator(Set<MethodDeclaration> methodDecl,
                                        EnvPropertyReader propertyReader, ElementGeneratorFactory elementGeneratorFactory) {
        return new StrutsActionXmlGenerator(methodDecl, propertyReader, elementGeneratorFactory);
    }
}
