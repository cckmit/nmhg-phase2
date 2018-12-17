/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

/**
 * @author mritunjay.kumar
 * 
 */
public interface MSAService {
	public List<Country> getCountryList();

	public List<String> getStatesByCountry(String country);

	public List<String> getCitiesByCountryAndState(String country,
			final String state);

	public List<String> getCountriesFromMSA();

	public List<String> getZipsByCountryStateAndCity(String country,
			String state, String city);
	
	public List<CountyCodeMapping> getCountiesByCountryStateAndZip(String country,
			String state, String zip);

	public Boolean isValidAddressCombination(String country, String state,
			String city, String zip);

	public MSA findMSAByZipCode(String zipCode);

	public String findCountyNameByStateAndCode(String state, String county);

}
