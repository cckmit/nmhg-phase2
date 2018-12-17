package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.domain.thirdparty.ThirdPartySearch;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.QueryParameters;

public class ThirdPartyRepositoryImpl extends GenericRepositoryImpl<ThirdParty, Long> implements ThirdPartyRepository
{

	/* (non-Javadoc)
	 * @see tavant.twms.domain.orgmodel.ThirdPartyRepository#findThirdPartyByNumberOrName(tavant.twms.domain.thirdparty.ThirdPartySearch, tavant.twms.infra.ListCriteria)
	 */
	public PageResult<ThirdParty> findThirdPartyByNumberOrName(ThirdPartySearch thirdPartySearch, ListCriteria listCriteria) 
	{
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer query = new StringBuffer(" from ThirdParty tp ");
		if (StringUtils.hasText(thirdPartySearch.getThirdPartyName()) && StringUtils.hasText(thirdPartySearch.getThirdPartyNumber()))
		{
			query.append(" where upper(tp.name) like :name and upper(tp.thirdPartyNumber) like :number");
			params.put("name", "%"+thirdPartySearch.getThirdPartyName().toUpperCase()+"%");
			params.put("number", "%"+thirdPartySearch.getThirdPartyNumber().toUpperCase()+"%");
		} 
		else if (StringUtils.hasText(thirdPartySearch.getThirdPartyName())) 
		{
			query.append("  where upper(tp.name) like :name");
			params.put("name", "%"+thirdPartySearch.getThirdPartyName().toUpperCase()+"%");
		}
		else if (StringUtils.hasText(thirdPartySearch.getThirdPartyNumber()))
		{
			query.append("  where upper(tp.thirdPartyNumber) like :number");
			params.put("number", "%"+thirdPartySearch.getThirdPartyNumber().toUpperCase()+"%");
		}
		
		//call the dude method to fetch my records and then lets party people I am telling ya!!
		return findPageUsingQueryForDistinctItems
												(
													query.toString(),
													"tp.name asc", 
													"select distinct(tp)", 
													listCriteria.getPageSpecification(), 
													new QueryParameters(params),
													"distinct tp"
												);
	}
	
	/**
	 * This method will return true if the sent user belongs to a third party organization else it'll
	 * return a false value.
	 * 
	 * @param user
	 * @return
	 */
	public boolean isThirdParty(final User user) {
		ServiceProvider dealer = (ServiceProvider) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Third_Party organization where organization = :org")
								.setParameter("org",
										user.getBelongsToOrganizations())
								.uniqueResult();
					};
				});
		return dealer != null;
	}

}
