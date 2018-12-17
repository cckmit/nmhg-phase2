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
package tavant.twms.domain.businessobject;

import java.util.Set;
import java.util.SortedMap;

import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.FieldTraversal;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public interface IBusinessObjectModel {

	public DomainTypeSystem getDomainTypeSystem();

	public SortedMap<String, FieldTraversal> getDataElementsForType(DomainType businessObject);
	
	public SortedMap<String, FieldTraversal> getAllLevelDataElements();

	public SortedMap<String, FieldTraversal> getTopLevelDataElements();

	public FieldTraversal getField(String typeName, String fieldName);
	
	public String getTopLevelTypeName();
	
	public String getTopLevelAlias();

	public void addDataElementsForType(	DomainType businessObject, String expression);
}
