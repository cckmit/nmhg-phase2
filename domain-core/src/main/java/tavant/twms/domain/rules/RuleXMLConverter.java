/*
 *   Copyright (c) 2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.infra.xstream.HibernateCollectionConverter;
import tavant.twms.infra.xstream.HibernateCollectionMapper;
import tavant.twms.infra.xstream.HibernateProxyConverter;
import tavant.twms.infra.xstream.XMLBeanConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author mritunjay.kumar
 */
public class RuleXMLConverter {
	private static Logger logger = Logger.getLogger(XMLBeanConverter.class);

	protected XStream xstream;

	private Map<Class, String[]> customFieldsToOmit = new HashMap<Class, String[]>();

	public RuleXMLConverter() {
		customFieldsToOmit.put(DomainRule.class, new String[] { "ruleAudits", "d" });
		customFieldsToOmit.put(DomainPredicate.class, new String[] { "d" });
		customFieldsToOmit.put(DomainRuleAction.class, new String[] { "d" });		
		customFieldsToOmit.put(DomainRuleGroup.class, new String[] { "rules", "d" });		
	}

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
		Map<Class, String[]> fieldsToOmit = getCustomFieldsToOmit();
		for (Map.Entry<Class, String[]> e : fieldsToOmit.entrySet()) {
			String[] fields = e.getValue();
			for (String fld : fields) {
				xstream.omitField(e.getKey(), fld);
			}
		}
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

	public Map<Class, String[]> getCustomFieldsToOmit() {
		return customFieldsToOmit;
	}

	public void setCustomFieldsToOmit(Map<Class, String[]> customFieldsToOmit) {
		this.customFieldsToOmit = customFieldsToOmit;
	}

}
