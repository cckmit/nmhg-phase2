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

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.infra.TypedQueryParameter;
import tavant.twms.security.SecurityHelper;
/**
 * Need to refactor/remodel this class.Needed as of now because there is no direct relationship
 * from part return to claim object and generic query generator doesnt know how to handle theta style
 * of joins.Also -custom return types are not supported by generic query generator.
 * @author roopali.agrawal
 *
 */
public class RecoveryClaimQueryGenerator extends HibernateQueryGenerator{

	private static final Logger logger = Logger
	.getLogger(RecoveryClaimQueryGenerator.class);

	public RecoveryClaimQueryGenerator(){
		super(BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES);
	}

	@Override
    public HibernateQuery getHibernateQuery() {
		HibernateQuery query = new HibernateQuery();
		StringBuffer selectClause = new StringBuffer("select recoveryClaim ");

		query.setSelectClause(selectClause.toString());
		query.setQueryWithoutSelect(getParameterizedQueryString());
		query.setParameters(this.indexedQueryParameters);
		if(logger.isInfoEnabled())
		{
		    logger.info("HQL query is " + query);
		}
		return query;
	}


	private String getParameterizedQueryString() {
		StringBuffer finalQuery = new StringBuffer();
		IBusinessObjectModel model = BusinessObjectModelFactory.getInstance()
				.getBusinessObjectModel(this.businessObjectContext);
		//String alias = model.getTopLevelAlias();

		finalQuery.append(" from RecoveryClaim recoveryClaim where 1 = 1 ");

		if (!("".equals(this.queryString.toString()))) {
			finalQuery.append(" AND ( ");
			finalQuery.append(this.queryString.toString());
			finalQuery.append(" ) ");
		}
		User loggedinUser=new SecurityHelper().getLoggedInUser();
		if(loggedinUser!=null && loggedinUser.hasRole("supplier")
				&& !loggedinUser.hasRole("masterSupplier")) {
		  finalQuery.append(" and recoveryClaim.contract.supplier in (?) ");
		  this.indexedQueryParameters.add(
                  new TypedQueryParameter(loggedinUser.getBelongsToOrganization(),
                  Hibernate.entity(Organization.class)));
		}
        
        return finalQuery.toString();
	}


}
