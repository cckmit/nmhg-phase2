/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author mritunjay.kumar
 * 
 */
public class MSARepositoryImpl extends GenericRepositoryImpl<MSA, Long>
		implements MSARepository {
	
	/** Perf Fix - Begin **/
	private List<Country> countryList;
	private List<String> countriesFromMSA;
	
    @SuppressWarnings("unchecked")
    public void init() {
        countryList = getHibernateTemplate().find(
				"from Country country order by country.name");
        
        countriesFromMSA = (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct msa.country from MSA msa order by msa.country")
								.list();
						return results;
					}
				});
    }	
	/** Perf Fix - End **/
	
	
	@SuppressWarnings("unchecked")
	public List<Country> getCountryList() {
		/** Perf Fix - Begin **/
		return countryList;
		//return getHibernateTemplate().find("from Country country order by country.name");
		/** Perf Fix - End **/
	}

	@SuppressWarnings("unchecked")
	public List<String> getStatesByCountry(final String country) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct msa.st from MSA msa"
												+ " where msa.country = :country order by msa.st")
								.setParameter("country", country).list();
						return results;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> getCitiesByCountryAndState(final String country,
			final String state) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct msa.city from MSA msa"
												+ " where msa.country = :country and msa.st = :state"
												+ " order by msa.city")
								.setParameter("country", country).setParameter(
										"state", state).list();
						return results;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> getCountriesFromMSA() {
		/** Perf Fix - Begin **/
		return countriesFromMSA;
		/*return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct msa.country from MSA msa order by msa.country")
								.list();
						return results;
					}
				}); */
		/** Perf Fix - End **/
	}

	@SuppressWarnings("unchecked")
	public List<String> getZipsByCountryStateAndCity(final String country,
			final String state, final String city) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct msa.zip2 from MSA msa"
												+ " where msa.country = :country and msa.st = :state"
												+ " and msa.city = :city"
												+ " order by msa.zip2")
								.setParameter("country", country).setParameter(
										"state", state).setParameter("city",
										city).list();
						return results;
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<CountyCodeMapping> getCountiesByCountryStateAndZip(final String country,
			final String state, final String zip) {
		return (List<CountyCodeMapping>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct ccm from MSA msa, CountyCodeMapping ccm"
												+ " where upper(msa.county)=upper(ccm.countyName) and ccm.state=msa.st and msa.country = :country and msa.st = :state" 
												+ " and msa.zip2= :zip" 
												+ " order by ccm.countyCode")
								.setParameter("country", country).setParameter("state", state).setParameter(
										"zip", zip).list();
						return results;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public Boolean isValidAddressCombination(final String country,
			final String state, final String city, final String zip) {
		return (Boolean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						List<String> results = session
								.createQuery(
										"select distinct msa.zip2 from MSA msa"
												+ " where msa.country = :country and msa.st = :state"
												+ " and msa.city = :city"
												+ " and msa.zip2 =:zip")
								.setParameter("country", country).setParameter(
										"state", state).setParameter("city",
										city).setParameter("zip", zip).list();
						if (results != null && results.size() > 0) {
							return true;
						} else {
							return false;
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	public MSA findMSAByZipCode(final String zipCode) {
		return (MSA) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				List<String> results = session.createQuery(
						"from MSA msa where msa.zip2 =:zip order by id desc")
						.setParameter("zip", zipCode).list();
				if (results != null && results.size() > 0)
					return results.get(0);
				else
					return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public String findCountyNameByStateAndCode(final String state, final String county) {
		// TODO Auto-generated method stub

		return (String) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Query query = session
								.createQuery(
										"select distinct ccm.countyName from CountyCodeMapping ccm"
												+ " where ccm.state= :state" 
												+ " and ccm.countyCode= :county")
								.setParameter("state", state).setParameter(
										"county", county);
						return query.uniqueResult();
					}
				});
		}

}
