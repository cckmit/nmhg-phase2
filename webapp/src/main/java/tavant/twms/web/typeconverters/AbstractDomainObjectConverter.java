package tavant.twms.web.typeconverters;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.BeanLocator;

import java.util.Map;

/**
 * Abstract base class for type converters which convert between strings and domain objects.
 *
 * @author binil.thomas
 */
public abstract class AbstractDomainObjectConverter extends StrutsTypeConverter {
    private static final Logger logger = Logger.getLogger(AbstractDomainObjectConverter.class);

    private final DomainRepository domainRepository;

    public AbstractDomainObjectConverter() {
        BeanLocator beanLocator = new BeanLocator();
        this.domainRepository = (DomainRepository) beanLocator.lookupBean("domainRepository");
    }

    @Override
    public Object convertFromString(Map ctx, String[] values, Class toClass) {
        if (values.length > 1) {
            throw new TypeConversionException("More than one value for the same form field name " +
                    "cannot be converted using this converter");
        }
        if (values.length < 0) {
            throw new TypeConversionException("No value to convert");
        }

        String id = values[0];
        if(logger.isDebugEnabled())
        {
            logger.debug("Attemping to convert Id " + id + " to class [" + toClass + "]");
        }

        return fetchDomainObject(id, toClass);
    }

    @Override
    public String convertToString(Map ctx, Object o) {
        if(logger.isDebugEnabled()){
            logger.debug("Attempting to convert [" + o + "] to string will execute getId() method on it");
        }
        Object id = null;
        try {
            id = ReflectionUtil.executeMethod(o, "getId", new Object[]{});
        } catch(RuntimeException e) {
            throw new TypeConversionException("The method getId() is not found for object [" + o + "]");
        }
        return convertIdToString(id);
    }

    protected DomainRepository getDomainRepository() {
        return this.domainRepository;
    }

    protected abstract Object fetchDomainObject(String id, Class toClass);

    protected abstract String convertIdToString(Object id);
}
