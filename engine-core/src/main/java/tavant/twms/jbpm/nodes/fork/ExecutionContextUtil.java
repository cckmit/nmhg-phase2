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
package tavant.twms.jbpm.nodes.fork;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.util.StringUtils;

/**
 * @author vineeth.varghese
 * @date Dec 14, 2006
 */
public class ExecutionContextUtil {

    private static Logger logger = Logger.getLogger(ExecutionContextUtil.class);

    public static ExecutionContext createChildExecutionContext(ExecutionContext parentContext,
            String transitionName) {
        Token parentToken = parentContext.getToken();
        if(!StringUtils.hasText(transitionName)) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Parent token [" + parentToken + "] trying to take a transition with no name");
            }
            int size = (parentToken.getChildren() != null ? parentToken.getChildren().size() + 1 : 1);
            if(logger.isDebugEnabled())
            {
                logger.debug("Parent token has [" + (size -1) + "] children");
            }
            return new ExecutionContext(new Token(parentToken, "" + size));
        }
        if(!parentToken.hasChild(transitionName)) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Parent token [" + parentToken + "] doesnt have the child name ["
                        + transitionName + "]");
            }
            return new ExecutionContext(new Token(parentToken, transitionName));
        }
        for(int i = 0;;i++) {
            String name = transitionName + i;
            if(parentToken.hasChild(name)) {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Token [" + parentToken + "] already has child with transition ["
                            + name + "]");
                }
            } else {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Returning token number [" + name + "]");
                }
                return new ExecutionContext(new Token(parentToken, name));
            }
        }

    }

}
