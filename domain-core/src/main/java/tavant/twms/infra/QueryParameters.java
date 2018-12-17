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
package tavant.twms.infra;

import java.util.List;
import java.util.Map;


/**
 * Represents a collection of positional and named parameters for a query.
 * @author roopali.agrawal
 *
 */
public class QueryParameters {
	private List<TypedQueryParameter> positionalParameters;
	
	private Map<String,Object> namedParameters;
	
	public QueryParameters(){
		
	}
	
	public QueryParameters(List<TypedQueryParameter> positionalParams,Map<String,Object> namedParameters){
		this.positionalParameters=positionalParams;
		this.namedParameters=namedParameters;
	}
	
	public QueryParameters(List<TypedQueryParameter> positionalParams){
		this.positionalParameters=positionalParams;
	}
	
	public QueryParameters(Map<String,Object> namedParameters){
		this.namedParameters=namedParameters;
	}

	public List<TypedQueryParameter> getPositionalParameters() {
		return positionalParameters;
	}

	public void setPositionalParameters(List<TypedQueryParameter> positionalParameters) {
		this.positionalParameters = positionalParameters;
	}

	public Map<String, Object> getNamedParameters() {
		return namedParameters;
	}

	public void setNamedParameters(Map<String, Object> namedParameters) {
		this.namedParameters = namedParameters;
	}
	
	
	
	
}
