/*
 *   Copyright (c)2006 Tavant Technologies
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author roopali.agrawal
 *
 */
public class DomainTypeSystem{
	private Map<String,Type> domainTypeMap = new HashMap<String, Type>();
	private TypeSystem typeSystem=TypeSystem.getInstance();

	public boolean isKnown(String type) {
		return domainTypeMap.containsKey(type);
	}

	public DomainType getDomainType(String typeName) {
		return (DomainType) getType(typeName);
	}

	public void registerDomainType(DomainType domainType) {
		domainTypeMap.put(domainType.getName(), domainType);
	}
	
	public Collection<Type> listAllTypes() {
		Collection<Type> primitiveTypes = typeSystem.listAllTypes();
	    Collection<Type> tempCollection=new ArrayList<Type>();
	    tempCollection.addAll(primitiveTypes);
	    Collection<Type> listAllDomainTypes = domainTypeMap.values();
	    tempCollection.addAll(listAllDomainTypes);
	    return tempCollection;
	}
	
	@SuppressWarnings("unchecked")
    public Type getType(String typeName) {
        Type type = domainTypeMap.get(typeName);
        if(type==null)
        	type=typeSystem.getType(typeName);        	
        if (type == null) {
            throw new IllegalArgumentException("Unknown type [" + typeName + "]");
        }
        return type;
    }
}
