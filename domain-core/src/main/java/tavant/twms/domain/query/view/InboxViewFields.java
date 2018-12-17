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
package tavant.twms.domain.query.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tavant.twms.security.SecurityHelper;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public abstract class InboxViewFields {
    protected abstract Map<String, InboxField> getInboxFieldsForAllUsers();

    protected abstract Map<String, InboxField> getInboxFieldsForDealer();

    public Map<String, InboxField> getInboxFields() {
        if (! new SecurityHelper().getLoggedInUser().isInternalUser())
            return getInboxFieldsForDealer();
        else
            return getInboxFieldsForAllUsers();
    }
    
    public List<String> getFieldsNotAvailableForSort(){
    	return new ArrayList<String>();
    }

    public InboxField getField(String key) {
        Map<String, InboxField> inboxFieldsMap = getInboxFields();
        if (inboxFieldsMap.containsKey(key))
            return inboxFieldsMap.get(key);
        else
            throw new RuntimeException("There is no InboxField defined for the specified key["+key+"]");
    }
}
