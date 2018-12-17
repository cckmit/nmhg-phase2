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

import java.util.HashMap;
import java.util.Map;

/**
 * @author roopali.agrawal
 */
public class InboxViewFieldsFactory {

    private static InboxViewFieldsFactory instance;

    protected Map<String, InboxViewFields> inboxViewFields = new HashMap<String, InboxViewFields>();

    public static InboxViewFieldsFactory getInstance() {
        return instance;
    }

    public InboxViewFieldsFactory() {
        instance = this;
    }

    /**
     * Folder Name cannot be null
     */
    public InboxViewFields getInboxViewFields(String context, String folderName) {
        //Make sure folder name is not null
        if (!inboxViewFields.containsKey(context + folderName)) {
            if (!inboxViewFields.containsKey(context)) {
                throw new RuntimeException(
                        "There is no InboxViewFields specified for key["
                                + context + folderName + "]");
            } else {
                inboxViewFields.get(context);
            }
        }
        return inboxViewFields.get(context + folderName);
    }
}
