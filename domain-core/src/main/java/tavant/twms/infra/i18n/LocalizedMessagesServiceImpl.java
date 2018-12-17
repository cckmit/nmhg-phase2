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

package tavant.twms.infra.i18n;

import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.GenericRepository;

import java.util.Locale;

public class LocalizedMessagesServiceImpl
        extends GenericServiceImpl<LocalizedMessages, Locale, Exception>
        implements LocalizedMessagesService {

    private LocalizedMessagesRepository localizedMessagesRepository;
    
    private boolean isMessageRepositoryEnabled;

    public void setLocalizedMessagesRepository(
            LocalizedMessagesRepository localizedMessagesRepository) {
        this.localizedMessagesRepository = localizedMessagesRepository;
    }

    @Override
    public GenericRepository<LocalizedMessages, Locale> getRepository() {
        return localizedMessagesRepository;
    }

	public boolean isMessageRepositoryEnabled() {
		return isMessageRepositoryEnabled;
	}

	public void setMessageRepositoryEnabled(boolean isMessageRepositoryEnabled) {
		this.isMessageRepositoryEnabled = isMessageRepositoryEnabled;
	}
    
    
}
