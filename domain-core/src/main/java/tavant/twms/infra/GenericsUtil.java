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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author radhakrishnan.j
 *
 */
public class GenericsUtil {

	protected boolean isCollectionType(Class<?> type) {
		return Collection.class.isAssignableFrom(type);
	}

	protected boolean isParameterizedType(Type genericType) {
		return genericType instanceof ParameterizedType;
	}

	public Class<? extends Object> getParameterizedCollectionContentType(ParameterizedType genericType) {
		ParameterizedType pType = (ParameterizedType)genericType;
		Type nestedType = pType.getActualTypeArguments()[0];
		if( isParameterizedType(nestedType) ) {
			ParameterizedType parameterizedType = (ParameterizedType)nestedType;
			Class<? extends Object> rawType = (Class<? extends Object>)parameterizedType.getRawType();
			Type typeParam = parameterizedType.getActualTypeArguments()[0];
			if( isCollectionType(rawType) && isParameterizedType(typeParam)) {
				return getParameterizedCollectionContentType((ParameterizedType)typeParam);
			} else {
				return rawType;
			}
		} else {
			return (Class<? extends Object>)nestedType ;
		}
	}
}
