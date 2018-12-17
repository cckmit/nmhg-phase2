/*Copyright (c)2006 Tavant Technologies
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

package tavant.twms.infra.xstream;

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author roopali.agrawal
 * 
 */
public class HibernateProxyConverter extends ReflectionConverter {
	ConverterLookup converterLookup;

	public HibernateProxyConverter(Mapper arg0, ReflectionProvider arg1,
			ConverterLookup pconverterLookup) {
		super(arg0, arg1);
		converterLookup = pconverterLookup;
	}

	/**
	 * be responsible for hibernate proxy
	 */
	public boolean canConvert(Class clazz) {
		return HibernateProxy.class.isAssignableFrom(clazz);
	}

	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		
		Object obj = ((HibernateProxy) arg0).getHibernateLazyInitializer()
				.getImplementation();
		Converter converter = converterLookup.lookupConverterForType(obj
				.getClass());

		if (converter != null) {
			converter.marshal(obj, arg1, arg2);
		} else {
			super.marshal(((HibernateProxy) arg0).getHibernateLazyInitializer()
					.getImplementation(), arg1, arg2);
		}
	}

}
