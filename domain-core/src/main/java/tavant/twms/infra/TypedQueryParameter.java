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

import org.hibernate.type.Type;
import org.springframework.core.style.ToStringCreator;
/**
 * 
 * @author roopali.agrawal
 *
 */
public class TypedQueryParameter {
	private Object value;
	private Type type;
	
	public TypedQueryParameter(Object value,Type type){
		this.value=value;
		this.type=type;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return new ToStringCreator(this).append("value",value).append("type",type).toString();
	}
	
	
	
	
	
	
}
