package tavant.twms.domain.policy;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.infra.xstream.HibernateCollectionConverter;
import tavant.twms.infra.xstream.HibernateCollectionMapper;
import tavant.twms.infra.xstream.HibernateProxyConverter;
import tavant.twms.infra.xstream.XMLBeanConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class PolicyXMLConverter  extends XMLBeanConverter{

    private static Logger logger = Logger.getLogger(PolicyXMLConverter.class);

    private XStream xstream;

    private Map<Class, String[]> customFieldsToOmit = new HashMap<Class, String[]>();

    private Map<Class, String[]> customFieldsToAlias = new HashMap<Class, String[]>();

    public PolicyXMLConverter() {
            this.customFieldsToOmit.put(RegisteredPolicy.class, new String[] {"policyAudits","warranty"});
    }

    @Override
    public void initialize() {
            this.xstream = new XStream() {
                    @Override
                    protected MapperWrapper wrapMapper(MapperWrapper next) {
                            return new HibernateCollectionMapper(next);
                    }
            };
            this.xstream.registerConverter(new HibernateCollectionConverter(this.xstream
                            .getConverterLookup()));
            this.xstream.registerConverter(new HibernateProxyConverter(this.xstream
                            .getMapper(), new PureJavaReflectionProvider(), this.xstream
                            .getConverterLookup()));
            Map<Class, String[]> fieldsToOmit = getCustomFieldsToOmit();
            for (Map.Entry<Class, String[]> e : fieldsToOmit.entrySet()) {
                    String[] fields = e.getValue();
                    for (String fld : fields) {
                            this.xstream.omitField(e.getKey(), fld);
                    }
            }
    }

    @Override
    public String convertObjectToXML(Object object) {
            String xml;
            try {
                    xml = this.xstream.toXML(object);
            } catch (RuntimeException e) {
                    logger.error("Got an exception while converting object to xml", e);
                    throw e;
            }
            return xml;
    }

    @Override
    public Object convertXMLToObject(String xml) {
            Object obj;
            try {
                    obj = this.xstream.fromXML(xml);
            } catch (RuntimeException e) {
                    logger.error("Got an exception while converting xml to object", e);
                    throw e;
            }
            return obj;
    }

    @Override
    public Map<Class, String[]> getCustomFieldsToOmit() {
            return this.customFieldsToOmit;
    }

    public void setCustomFieldsToOmit(Map<Class, String[]> customFieldsToOmit) {
            this.customFieldsToOmit = customFieldsToOmit;
    }

    @Override
    public Map<Class, String[]> getCustomFieldsToAlias() {
        return customFieldsToAlias;
    }

    public void setCustomFieldsToAlias(Map<Class, String[]> customFieldsToAlias) {
        this.customFieldsToAlias = customFieldsToAlias;
    }

}
