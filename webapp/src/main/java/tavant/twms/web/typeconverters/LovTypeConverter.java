/**
 *
 */
package tavant.twms.web.typeconverters;

import ognl.DefaultTypeConverter;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.BeanLocator;

import java.util.Map;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class LovTypeConverter extends DefaultTypeConverter {

	private static final Logger logger = Logger.getLogger(LovTypeConverter.class);
	public static final String NULL = "null";
	LovRepository lovRepository;

	@Override
    public Object convertValue(Map ctx, Object value, Class toType) {
        if(this.lovRepository == null) {
            initDomainRepository();
        }
        Assert.notNull(this.lovRepository, "LOV repository is null");
        if(toType == String.class) {
            if(logger.isDebugEnabled()){
                logger.debug("Attempting to convert [" + value + "] to string will execute getId() method on it");
            }
            try {
                Object output = ReflectionUtil.executeMethod(value, "getCode", new Object[]{});
                return output == null ? "" : output.toString();
            } catch(RuntimeException e) {
                throw new RuntimeException("The method getCode() is not found for object [" + value + "]");
            }
        }
        else {
            if(logger.isDebugEnabled()){
                logger.debug("Attemping to convert to class [" + toType + "]");
            }
            String code = null;
            if (value instanceof String[]) {
                code = ((String[]) value)[0];
            } else if (value instanceof String) {
                code = (String) value;
            } else {
                code = value.toString();
            }
            if(logger.isDebugEnabled()){
                logger.debug("Code obtained is [" + code + "]");
            }
            //sometimes a NULL is explicitly needed by the UI
            if (NULL.equals(code) || "".equals(code)) {
                return null;
            } else if(StringUtils.hasText(code)) {
                return this.lovRepository.findByCode(toType.getSimpleName(), code);
            } else {
                logger.debug("Attempting to instantiate the new domain class [" + toType + "]");
                return ReflectionUtil.createNewInstance(toType.getName());
            }
        }
    }

	private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.lovRepository = (LovRepository) beanLocator.lookupBean("lovRepository");
    }

}
