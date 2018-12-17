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

import org.mule.impl.DefaultExceptionStrategy;
import org.mule.providers.jms.MessageRedeliveredException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOImmutableEndpoint;

/**
 * An excpetion strategy which does a rollback of the current jms transaction.
 * The only scenario where it doesn't do a rollback is when the number of
 * times a jms message has been redelivered equals the configured 'maxRedelivery'
 * property on the jms connector. This is to prevent a poison message from being
 * retried infinitely.
 */
public class RollbackExceptionStrategy extends DefaultExceptionStrategy {

    public void handleRoutingException(UMOMessage message, UMOImmutableEndpoint endpoint, Throwable t) {
        defaultHandler(t);
        markTransactionForRollback();
        routeException(message, endpoint, t);
    }

    public void handleMessagingException(UMOMessage message, Throwable t) {
        defaultHandler(t);
        if (!(t instanceof MessageRedeliveredException)) {
            markTransactionForRollback();
        }
        routeException(message, null, t);
    }
}
