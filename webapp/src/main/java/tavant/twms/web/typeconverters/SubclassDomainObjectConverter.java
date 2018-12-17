package tavant.twms.web.typeconverters;

import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.util.StringUtils;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.BeanLocator;

import java.util.Map;
import java.util.StringTokenizer;

public class SubclassDomainObjectConverter extends StrutsTypeConverter {

	private DomainRepository domainRepository;

	@Override
	public Object convertFromString(Map arg0, String[] value, Class baseClass) {
		String arrayValue = null;
		String id = null;
		String subClassName = null;
		// The array sent through java script is a comma seperated string,
		// the format being 'id,subclassName'.
		// For a new instance there is just the subclass name that is passed.
		if (value instanceof String[]) {
			arrayValue = ((String[]) value)[0];
			StringTokenizer tokenizer = new StringTokenizer(arrayValue, ",");
			if (tokenizer.countTokens() > 1) {
				id = tokenizer.nextToken();
			}
			subClassName = tokenizer.nextToken();
		}

		if (domainRepository == null) {
			initDomainRepository();
		}

		if (StringUtils.hasText(id)) {
			try {
				Object object = domainRepository.load(Class
						.forName(subClassName), new Long(id));
				
				// The object returned could be null incase the
				// subclass for an existing base class has been changed.
				// TODO: Need to figure out a way to retain the id in this case.
				
				return object != null ? object : ReflectionUtil
						.createNewInstance(subClassName);
			} catch (NumberFormatException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			return ReflectionUtil.createNewInstance(subClassName);
		}
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		return arg1.getClass().getName();
	}

	private void initDomainRepository() {
		BeanLocator beanLocator = new BeanLocator();
		domainRepository = (DomainRepository) beanLocator
				.lookupBean("domainRepository");
	}
}
