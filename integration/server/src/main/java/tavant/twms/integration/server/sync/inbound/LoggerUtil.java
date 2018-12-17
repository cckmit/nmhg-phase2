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
 *
 */
package tavant.twms.integration.server.sync.inbound;

import org.apache.log4j.Logger;

/**
 *
 * @author prasad.r
 */
public class LoggerUtil {
    
    public static void trace(String message, Logger logger){
        if(logger.isTraceEnabled())
            logger.trace(message);
    }

    
    public static void debug(String message, Logger logger){
        if(logger.isDebugEnabled())
            logger.debug(message);
    }
    
    public static void info(String message, Logger logger){
        if(logger.isInfoEnabled())
            logger.info(message);
    }
    
}
