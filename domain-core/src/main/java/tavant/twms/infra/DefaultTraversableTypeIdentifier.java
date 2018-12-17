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
package tavant.twms.infra;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import tavant.twms.infra.ObjectGraphTraverser.TraversablePathIdentifier;
import tavant.twms.infra.ObjectGraphTraverser.TraversableTypeIdentifier;

public class DefaultTraversableTypeIdentifier implements TraversableTypeIdentifier,TraversablePathIdentifier {
	private Set<Class> primitives = new HashSet<Class>();
	private Set<String> richPrimitives = new TreeSet<String>();
	private GenericsUtil genericsUtils = new GenericsUtil();
	
	public DefaultTraversableTypeIdentifier() {
		super();
		primitives.add(Integer.class);
		primitives.add(Long.class);
		primitives.add(Float.class);
		primitives.add(Double.class);
		primitives.add(String.class);
	}

	public void setRichPrimitives(List<String> richPrimitives) {
		this.richPrimitives.addAll(richPrimitives);
	}

	public boolean isTraversable(Class<? extends Object> fieldType) {
		boolean primitive = fieldType.isPrimitive() || primitives.contains(fieldType);
		boolean annotation = fieldType.isAnnotation();
		boolean synthetic = fieldType.isSynthetic();
		boolean isEnum = fieldType.isEnum();
		boolean numberType = Number.class.isAssignableFrom(fieldType);
		boolean string = String.class.equals(fieldType);
		boolean jdkClass = fieldType.getName().startsWith("java.");
		boolean toBeIgnored = primitive || annotation || synthetic || isEnum || numberType || string || jdkClass;
		toBeIgnored = toBeIgnored || richPrimitives.contains(fieldType.getName());
		toBeIgnored = toBeIgnored || fieldType.getName().equals("java.lang.Object");
		return !toBeIgnored;
	}
	
	
	public boolean isTraversablePath(Object root, String fieldPathFromRoot, Field aField,Object ofObject) {
		Type genericType = aField.getGenericType();
		Class<?> type = aField.getType();
		if( genericsUtils.isCollectionType(type) ) {
			if( genericsUtils.isParameterizedType(genericType) ) {
				Class<? extends Object> collectionElementType = genericsUtils.getParameterizedCollectionContentType((ParameterizedType)genericType);
				return collectionElementType!=null && isTraversable(collectionElementType);	
			} else {
				return false;
			}
		} else {
			return isTraversable(type);
		}
	}


}