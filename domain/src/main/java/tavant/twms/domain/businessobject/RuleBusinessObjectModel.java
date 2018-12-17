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

import java.util.HashSet;
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
public class RuleBusinessObjectModel extends AbstractRulesBusinessObjectModel {
	private Set<DomainType> domainTypeSet = new HashSet<DomainType>();

	public String getExpressionForDomainType(String typeName) {
		String expression = null;
		if ("Claim".equals(typeName))
			expression = "claim";
		/*else if ("Policy".equals(typeName))
			expression = "policy";
		else if ("Contract".equals(typeName))
			expression = "contract";*/
		return expression;
	}

	@Override
	public Set<DomainType> getDomainTypes() {
		return domainTypeSet;
	}

	public RuleBusinessObjectModel() {
		domainTypeSystem = new DomainTypeSystem();

		DomainType claim = claim();
		/*DomainType policy = policy();
		DomainType contract = contract();*/

		domainTypeSet.add(claim);
		/*domainTypeSet.add(policy);
		domainTypeSet.add(contract);*/

		discoverPathsToFields(claim, "claim");
		/*discoverPathsToFields(policy, "policy");
		discoverPathsToFields(contract, "contract");*/
	}
	
	public static void main(String[] args){
		IBusinessObjectModel bus=BusinessObjectModelFactory.getInstance().getBusinessObjectModel("PolicyRules");
		SortedMap<String, FieldTraversal> map= bus.getAllLevelDataElements();
		System.out.println(map);
	}
	
	public String getTopLevelTypeName() {
		return "Claim";
	}
	
	public String getTopLevelAlias() {
		return "claim";
	}

}
