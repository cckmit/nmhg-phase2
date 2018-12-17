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

package tavant.twms.domain.login;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class LoginHistoryServiceImpl extends GenericServiceImpl<LoginHistory, Long, Exception> implements LoginHistoryService{
	private LoginHistoryRepository loginHistoryRepository;
	
	@Override
	public GenericRepository<LoginHistory, Long> getRepository() {

		return this.loginHistoryRepository;
	}
	
	@Required
	public void setLoginHistoryRepository(
			LoginHistoryRepository loginHistoryRepository) {
		this.loginHistoryRepository = loginHistoryRepository;
	}
}
