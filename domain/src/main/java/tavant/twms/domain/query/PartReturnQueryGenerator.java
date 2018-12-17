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
import org.springframework.beans.factory.BeanFactory;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
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
public class PartReturnQueryGenerator extends HibernateQueryGenerator{

	private static final Logger logger = Logger
	.getLogger(PartReturnQueryGenerator.class);
	
    private  BeanFactory beanFactory;
    
    
	public PartReturnQueryGenerator(){
		super(BusinessObjectModelFactory.PART_RETURN_SEARCHES);
	}
	
	public PartReturnQueryGenerator(BeanFactory beanFactory){
		super(BusinessObjectModelFactory.PART_RETURN_SEARCHES);
		this.beanFactory=beanFactory;
	}

	@Override
    public HibernateQuery getHibernateQuery() {
		HibernateQuery query = new HibernateQuery();
		StringBuffer selectClause = new StringBuffer("select new tavant.twms.domain." +
				"query.PartReturnClaimSummary(partReturn,claim)");

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
		ConfigParamService configParamService= (ConfigParamService) beanFactory.getBean(
					"configParamService", ConfigParamService.class);
        if(!configParamService.getBooleanValue(ConfigName.PARTS_REPLACED_INSTALLED_SECTION_VISIBLE.getName())){
			finalQuery.append(" from PartReturn partReturn,Claim claim join "
					+ "claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced oemPartsReplaced join "
					+ "oemPartsReplaced.partReturns as partReturns " + "where (partReturns.id= partReturn.id)");
        	
        }else {
			finalQuery
					.append(" from PartReturn partReturn,Claim claim join "
							+ "claim.activeClaimAudit.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled hussReplaced join hussReplaced.replacedParts oemPartsReplaced join "
							+ "oemPartsReplaced.partReturns as partReturns " + "where (partReturns.id= partReturn.id)");
        }

		if (!("".equals(this.queryString.toString()))) {
			finalQuery.append(" AND ( ");
			finalQuery.append(this.queryString.toString());
			finalQuery.append(" ) ");
		}
		
		User loggedinUser=new SecurityHelper().getLoggedInUser();
		Boolean isExternalUser=!loggedinUser.isInternalUser();
		if(isExternalUser) {
		  finalQuery.append(" and ( claim.forDealer = ? or " +
					" ( claim.forDealer in (select tp from ThirdParty tp) and " +
					"	claim.filedBy in (select users from Organization org join org.users users where org = ?))) ");
		  this.indexedQueryParameters.add(new TypedQueryParameter(loggedinUser.getCurrentlyActiveOrganization(),
                          Hibernate.entity(Organization.class)));
		  this.indexedQueryParameters.add(new TypedQueryParameter(loggedinUser.getCurrentlyActiveOrganization(),
                  Hibernate.entity(Organization.class)));
		}

        return finalQuery.toString();
	}


   

}
