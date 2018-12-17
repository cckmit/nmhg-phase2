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

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.orgmodel.Country;

public class CountryStateServiceImpl implements CountryStateService {

	private CountryStateRepository countryStateRepository;

	public List<CountryState> fetchAllStates() {		
		return countryStateRepository.findAll();
	}

	@Required
	public void setCountryStateRepository(
			CountryStateRepository countryStateRepository) {
		this.countryStateRepository = countryStateRepository;
	}

	public List<CountryState> fetchCountryStates(String country) {
		return this.countryStateRepository.fetchCountryStates(country);
	}
	
	public CountryState fetchState(String stateCode, String country) {
		return this.countryStateRepository.fetchState(stateCode,country);
	}
	
	public CountryState fetchStateCodeByName(String state, String country){
		return this.countryStateRepository.fetchStateCodeByName(state,country);
	}
	
	public Country fetchCountryCodeByName(String country){
		return this.countryStateRepository.fetchCountryCodeByName(country);
	}

}
