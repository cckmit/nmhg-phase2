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

import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class ClaimRulesBusinessObjectModel extends AbstractRulesBusinessObjectModel {
	private DomainType domainType;

	public String getExpressionForDomainType(String typeName) {
		String expression = null;
		if ("Claim".equals(typeName))
			expression = "claim";
		return expression;
	}

	@Override
	public Set<DomainType> getDomainTypes() {
		Set<DomainType> set = new HashSet<DomainType>();
		set.add(domainType);
		return set;
	}

	public ClaimRulesBusinessObjectModel() {
		domainTypeSystem = new DomainTypeSystem();

		domainType = claim();
		discoverPathsToFields(domainType, "claim");
	}	
	
	public String getTopLevelTypeName() {
		return "Claim";
	}
	
	public String getTopLevelAlias() {
		return "claim";
	}

}
