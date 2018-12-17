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

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.Country;

@Transactional(readOnly=true)
public interface CountryStateService {

	/**
	 * Assumes only US and Canada states are stored in the DB.
	 * @return Collection
	 */
	public List<CountryState> fetchAllStates();
	
	public List<CountryState> fetchCountryStates(String country);
	
	public CountryState fetchState(String stateCode, String country);
	
	public CountryState fetchStateCodeByName(String state, String country);
	
	public Country fetchCountryCodeByName(String country);
}
