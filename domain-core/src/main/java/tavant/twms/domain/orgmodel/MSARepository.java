/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.infra.GenericRepository;

/**
 * @author mritunjay.kumar
 * 
 */
public interface MSARepository extends GenericRepository<MSA, Long> {
	public List<Country> getCountryList();

	public List<String> getStatesByCountry(String country);

	public List<String> getCitiesByCountryAndState(String country,
			final String state);

	public List<String> getCountriesFromMSA();

	public List<String> getZipsByCountryStateAndCity(String country,
			String state, String city);
	
	public List<CountyCodeMapping> getCountiesByCountryStateAndZip(String country,
			String state, String zip);

	public Boolean isValidAddressCombination(final String country,
			final String state, final String city, final String zip);

	public MSA findMSAByZipCode(String zipCode);

	public String findCountyNameByStateAndCode(String state, String county);

}
