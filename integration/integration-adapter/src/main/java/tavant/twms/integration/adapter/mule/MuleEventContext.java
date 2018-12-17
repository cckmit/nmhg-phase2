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

package tavant.twms.integration.adapter.mule;

import tavant.twms.integration.adapter.EventContext;
import tavant.twms.integration.adapter.SyncTracker;
import org.mule.umo.UMOException;
import org.mule.impl.RequestContext;
import org.mule.impl.MuleMessage;

import java.util.List;

public class MuleEventContext implements EventContext {

    ThreadLocal<List<SyncTracker>> recordsBeingSynced = new ThreadLocal<List<SyncTracker>>();

    public void sendEvent(Object message) {
        try {
            RequestContext.getEventContext().sendEvent(new MuleMessage(message));
        } catch (UMOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SyncTracker> getRecordsBeingSynced() {
        return recordsBeingSynced.get();
    }

    public void setRecordsBeingSynced(List<SyncTracker> syncTrackers) {
        recordsBeingSynced.set(syncTrackers);
    }
}
