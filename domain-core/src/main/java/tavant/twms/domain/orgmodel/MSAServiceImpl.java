/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

/**
 * @author mritunjay.kumar
 * 
 */
public class MSAServiceImpl implements MSAService {

	private MSARepository msaRepository;

	public void setMsaRepository(MSARepository msaRepository) {
		this.msaRepository = msaRepository;
	}

	public List<Country> getCountryList() {
		return msaRepository.getCountryList();
	}
	
	public List<String> getStatesByCountry(String country) {
		return msaRepository.getStatesByCountry(country);
	}

	public List<String> getCitiesByCountryAndState(String country, String state) {
		return msaRepository.getCitiesByCountryAndState(country, state);
	}

	public List<String> getCountriesFromMSA() {
		return msaRepository.getCountriesFromMSA();
	}

	public List<String> getZipsByCountryStateAndCity(String country,
			String state, String city) {
		return msaRepository.getZipsByCountryStateAndCity(country, state, city);
	}
	
	public List<CountyCodeMapping> getCountiesByCountryStateAndZip(String country,
			String state, String zip) {
		return msaRepository.getCountiesByCountryStateAndZip(country, state, zip);
	}

	public Boolean isValidAddressCombination(String country, String state,
			String city, String zip) {
		return msaRepository.isValidAddressCombination(country, state, city,
				zip);
	}
	
	public MSA findMSAByZipCode(String zipCode)
	{
		return msaRepository.findMSAByZipCode(zipCode);
	}

	@Override
	public String findCountyNameByStateAndCode(String state, String county) {
		return msaRepository.findCountyNameByStateAndCode(state,county);
	}
	
}
