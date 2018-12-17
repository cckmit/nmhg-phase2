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
package tavant.twms.domain.orgmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.common.Purpose;
import tavant.twms.infra.GenericRepositoryImpl;

public class RoleSchemeRepositoryImpl extends
		GenericRepositoryImpl<RoleScheme, Long> implements RoleSchemeRepository {

	public List<Purpose> findEmployedPurposes() {
		List<RoleScheme> schemes = findAll();
		List<Purpose> employedPurposes = new ArrayList<Purpose>();
		for (RoleScheme scheme : schemes) {
			employedPurposes.addAll(scheme.getPurposes());
		}
		return employedPurposes;
	}

	public RoleScheme findSchemeForPurpose(Purpose purpose) {
		String theQuery = "select roleScheme from RoleScheme roleScheme join roleScheme.purposes as purpose "
				+ " where purpose=:purpose ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("purpose", purpose);
		RoleScheme resultFound = findUniqueUsingQuery(theQuery, params);
		return resultFound;
	}

}
