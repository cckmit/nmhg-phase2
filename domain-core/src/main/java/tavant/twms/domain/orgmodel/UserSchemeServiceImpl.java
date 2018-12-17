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

import java.util.List;

import tavant.twms.domain.common.Purpose;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class UserSchemeServiceImpl extends GenericServiceImpl<UserScheme, Long, Exception> implements UserSchemeService{

	private UserSchemeRepository userSchemeRepository;
	
	public void setUserSchemeRepository(UserSchemeRepository userSchemeRepository) {
		this.userSchemeRepository = userSchemeRepository;
	}

	@Override
	public GenericRepository<UserScheme, Long> getRepository() {
		return userSchemeRepository;
	}

	public List<Purpose> findEmployedPurposes() {
		return userSchemeRepository.findEmployedPurposes();
	}

	public UserScheme findSchemeForPurpose(Purpose purpose) {
		return userSchemeRepository.findSchemeForPurpose(purpose);
	}

}
