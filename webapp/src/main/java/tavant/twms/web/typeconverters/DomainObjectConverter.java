package tavant.twms.web.typeconverters;

import ognl.DefaultTypeConverter;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.BeanLocator;

import java.util.Map;

public class DomainObjectConverter extends DefaultTypeConverter {

    private static final Logger logger = Logger.getLogger(DomainObjectConverter.class);

    private DomainRepository domainRepository;

    //The UI can populate this, when it explicitly wants a null to be shown
    public static final String NULL = "null";
    @Override
    public Object convertValue(Map ctx, Object value, Class toType) {
        if(this.domainRepository == null) {
            initDomainRepository();
        }
        Assert.notNull(this.domainRepository, "Domain repository is null");
        if(toType == String.class) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Attempting to convert [" + value + "] to string will execute getId() method on it");
            }
            try {
                Object output = ReflectionUtil.executeMethod(value, "getId", new Object[]{});
                return output == null ? "" : output.toString();
            } catch(RuntimeException e) {
                throw new RuntimeException("The method getId() is not found for object [" + value + "]");
            }
        } else {
            return convertToNonStringType(value, toType);
        }
    }

    protected Object convertToNonStringType(Object value, Class toType) {
        if(logger.isDebugEnabled()) {
            logger.debug("Attemping to convert to class [" + toType + "]");
        }

        String id;

        if (value instanceof String[]) {
            id = ((String[]) value)[0];
        } else if (value instanceof String) {
            id = (String) value;
        } else {
            id = value.toString();
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Id obtained is [" + id + "]");
        }

        //sometimes a NULL is explicitly needed by the UI
        if (NULL.equals(id)) {
            return null;
        } else if (StringUtils.hasText(id)) {
            return getDomainRepository().load(toType, new Long(id));
        } else {
            if(logger.isDebugEnabled()) {
                logger.debug("Attempting to instantiate the new domain class [" + toType + "]");
            }

            return ReflectionUtil.createNewInstance(toType.getName());
        }
    }
    
    private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.domainRepository = (DomainRepository) beanLocator.lookupBean("domainRepository");
    }

    public DomainRepository getDomainRepository() {
        if (domainRepository == null) {
            initDomainRepository();
        }

        return domainRepository;
    }

}
