/*
 *   Copyright (c)2007 Tavant Technologies
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

/**
 * @author radhakrishnan.j
 * 
 */
public class TypeSystem {
    private Map<String, Type> typeMap = new HashMap<String, Type>();
    private Set<String> primitiveTypes = new HashSet<String>();
    private static TypeSystem _instance = new TypeSystem(); 
    
    
    protected TypeSystem() {
        initialize();
    }
    
    public static TypeSystem getInstance() {
        return _instance;
    }
    
    @SuppressWarnings("unchecked")
    public Type getType(String typeName) {
        Type type = typeMap.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown type [" + typeName + "]");
        }
        return type;
    }

    public boolean isPrimitive(String typeName) {
        return primitiveTypes.contains(typeName);
    }    
    
    public boolean isPrimitive(Type type) {
        return isPrimitive(type.getName());
    }
    
    public boolean isString(Type type) {
        return isString(type.getName());
    }
    
    public boolean isString(String type) {
        return Type.STRING.equals( type );
    }    
    
    /*public boolean isKnown(String type) {
        return typeMap.containsKey(type);
    }*/
    
    public Collection<Type> listAllTypes() {
        return typeMap.values();
    }

    /*DomainType getDomainType(String typeName) {
        return (DomainType) getType(typeName);
    }

    public void registerDomainType(DomainType domainType) {
        typeMap.put(domainType.getName(), domainType);
    }*/
    
    @SuppressWarnings("unchecked")
    protected void initialize() {
        typeMap.put(Type.STRING, new StringType());
        typeMap.put(Type.ENUM, new EnumType());
        typeMap.put(Type.BOOLEAN, new BooleanType());
        typeMap.put(Type.INTEGER, new IntegerType());
        typeMap.put(Type.LONG, new LongType());
        typeMap.put(Type.BIGDECIMAL, new BigDecimalType());
        typeMap.put(Type.DATE, new DateType());
        typeMap.put(Type.MONEY, new MoneyType());
        typeMap.put(Type.VOID, new VoidType());
        typeMap.put(Type.DOUBLE, new DoubleType());
        
        primitiveTypes.add(Type.INTEGER);
        primitiveTypes.add(Type.LONG);
        primitiveTypes.add(Type.BOOLEAN);        
        primitiveTypes.add(Type.DOUBLE);
    }

    protected XStream typeSerializer() {
        XStream xStream = new XStream();
        Annotations.configureAliases(xStream, DateType.class, BigDecimalType.class, IntegerType.class,
                LongType.class, MoneyType.class, StringType.class, BooleanType.class, DomainType.class,
                DomainType.class, SimpleField.class, OneToOneAssociation.class, OneToManyAssociation.class, DoubleType.class);

        xStream.useAttributeFor("name", String.class);
        xStream.useAttributeFor("domainName", String.class);
        xStream.useAttributeFor("expression", String.class);
        xStream.useAttributeFor("type", String.class);
        xStream.useAttributeFor("id", String.class);        
        xStream.useAttributeFor(Boolean.TYPE);
        
        xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
        
        return xStream;
    }
}
