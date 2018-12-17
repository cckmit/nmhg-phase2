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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Responsible for converting object to xml and vice-versa.
 *
 * @author roopali.agrawal
 *
 */
public abstract class XMLBeanConverter {

    private static Logger logger = Logger.getLogger(XMLBeanConverter.class);

    private List<Converter> customXMLConverters;

    private XStream xstream;

    public List<Converter> getCustomXMLConverters() {
        return customXMLConverters;
    }

    public void setCustomXMLConverters(List<Converter> customConverters) {
        this.customXMLConverters = customConverters;
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

    public void initialize() {
        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new HibernateCollectionMapper(next);
            }
        };
        for (Converter converter : customXMLConverters) {
            xstream.registerConverter(converter);
        }
               
        xstream.addDefaultImplementation(java.util.ArrayList.class, org.hibernate.collection.PersistentList.class); 
        xstream.addDefaultImplementation(java.util.HashMap.class, org.hibernate.collection.PersistentMap.class); 
        xstream.addDefaultImplementation(java.util.HashSet.class, org.hibernate.collection.PersistentSet.class);
        
        xstream.registerConverter(new HibernateCollectionConverter(xstream.getConverterLookup()));
        xstream.registerConverter(new HibernateProxyConverter(xstream.getMapper(),
                new PureJavaReflectionProvider(), xstream.getConverterLookup()));

        Map<Class, String[]> fieldsToOmit = getCustomFieldsToOmit();
        for (Map.Entry<Class, String[]> e : fieldsToOmit.entrySet()) {
            String[] fields = e.getValue();
            for (String fld : fields) {
                xstream.omitField(e.getKey(), fld);
            }
            // xstream.setMode(XStream.NO_REFERENCES);

        }
        
        Map<Class, String[]> fieldsToAlias = getCustomFieldsToAlias();
        for (Map.Entry<Class, String[]> e : fieldsToAlias.entrySet()) {
            String[] fields = e.getValue();
            xstream.aliasField(fields[1], e.getKey(), fields[0]);
        }

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

    public abstract Map<Class, String[]> getCustomFieldsToOmit();

    public abstract Map<Class, String[]> getCustomFieldsToAlias();

}
