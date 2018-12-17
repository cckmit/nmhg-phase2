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
package tavant.twms.domain.query;

import tavant.twms.domain.rules.FunctionField;

/**
 * 
 * @author roopali.agrawal
 *
 */
public class QueryTemplate extends FunctionField {

    private String[] joinedEntityNames;
	private String groupBy;
	private String[] aliasNames;

	public QueryTemplate(String domainName, String expression, String domainType,
			boolean isHardWired, Class baseType,String joinedEntityName,String alias) {
		super(domainName, expression, domainType, isHardWired, baseType);
		if (joinedEntityName != null && alias != null) {
			this.joinedEntityNames = new String[] {joinedEntityName};
			this.aliasNames = new String[] {alias};
		}		
	}

    public QueryTemplate(String domainName, String expression, String domainType,
			boolean isHardWired, Class baseType,String[] joinedEntityNames,String[] aliases) {
		super(domainName, expression, domainType, isHardWired, baseType);
		this.joinedEntityNames=joinedEntityNames;
		this.aliasNames=aliases;
	}
	
	public QueryTemplate(String domainName, String expression, String domainType,
			boolean isHardWired, Class baseType,String joinedEntityName,String groupBy,String alias) {
		super(domainName, expression, domainType, isHardWired, baseType);
        this.joinedEntityNames = new String[] {joinedEntityName};
		this.groupBy=groupBy;
        this.aliasNames = new String[] {alias};
	}

    public String[] getJoinedEntityNames() {
        return joinedEntityNames;
    }

    public void setJoinedEntityNames(String[] joinedEntityNames) {
        this.joinedEntityNames = joinedEntityNames;
    }

    public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

    public String[] getAliasNames() {
        return aliasNames;
    }

    public void setAliasNames(String[] aliasNames) {
        this.aliasNames = aliasNames;
    }
}
