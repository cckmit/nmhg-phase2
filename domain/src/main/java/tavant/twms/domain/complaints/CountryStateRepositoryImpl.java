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
package tavant.twms.domain.complaints;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.infra.GenericRepositoryImpl;

//TODO : any more apis not in the GenericRepository likely to come ?
public class CountryStateRepositoryImpl extends GenericRepositoryImpl<CountryState, Long>
		implements CountryStateRepository {

	public List<CountryState> fetchCountryStates(String country) {
		String query="from CountryState where country=:country order by state asc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("country", country);
		return findUsingQuery(query, params);
	}

	public CountryState fetchState(String stateCode, String country) {
		String query="from CountryState where country=:country and stateCode=:stateCode";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("country", country);
		params.put("stateCode", stateCode);
		return findUniqueUsingQuery(query, params);
	}
	
	public CountryState fetchStateCodeByName(String state, String country){
		String query="from CountryState where UPPER(country)=:country and UPPER(state)=:state";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("country", country.toUpperCase());
		params.put("state", state.toUpperCase());
		return findUniqueUsingQuery(query, params);
	}
	
	public Country fetchCountryCodeByName(final String countryName){
		return (Country) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Country country = (Country) session
						.createQuery("select  c from Country c where UPPER(c.name) = :countryName")
						.setParameter("countryName", countryName.toUpperCase())
						.uniqueResult();
						return country;
						
					}
					});
	}
}
