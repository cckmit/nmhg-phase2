/**
 * 
 */
package tavant.twms.domain.inventory;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.infra.xstream.HibernateCollectionConverter;
import tavant.twms.infra.xstream.HibernateCollectionMapper;
import tavant.twms.infra.xstream.HibernateProxyConverter;
import tavant.twms.infra.xstream.XMLBeanConverter;

/**
 * @author fatima.marneni
 *
 */
public class InventoryStolenTransactionXMLConverter {
	private static Logger logger = Logger.getLogger(XMLBeanConverter.class);

	private XStream xstream;

	public void initialize() {
		xstream = new XStream() {
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new HibernateCollectionMapper(next);
			}
		};
		xstream.registerConverter(new HibernateCollectionConverter(xstream
				.getConverterLookup()));
		xstream.registerConverter(new HibernateProxyConverter(xstream
				.getMapper(), new PureJavaReflectionProvider(), xstream
				.getConverterLookup()));
	}

	public String convertObjectToXML(Object object) {
		String xml;
		try {
			xml = xstream.toXML(object);
		} catch (RuntimeException e) {
			logger.error("Got an exception while converting object to xml", e);
			throw e;
		}
		return xml;
	}

	public Object convertXMLToObject(String xml) {
		Object obj;
		try {
			obj = xstream.fromXML(xml);
		} catch (RuntimeException e) {
			logger.error("Got an exception while converting xml to object", e);
			throw e;
		}
		return obj;
	}

}
