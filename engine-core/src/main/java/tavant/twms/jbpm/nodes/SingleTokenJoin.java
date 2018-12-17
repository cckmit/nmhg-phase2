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
package tavant.twms.jbpm.nodes;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

/**
 * @author vineeth.varghese
 * @date Jun 20, 2007
 */
public class SingleTokenJoin extends Node {

    static final Logger logger = Logger.getLogger(SingleTokenJoin.class);

    public void execute(ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        TaskMgmtInstance taskMgmtInstance = executionContext.getTaskMgmtInstance();
        if (token.isAbleToReactivateParent()) {
            logger.debug("Token[" + token + "] can reactivate the parent. So stopping all siblings");
            token.setAbleToReactivateParent(false);
            Collection<Token> siblings = getSiblingToken(token);
            for (Token sibling : siblings) {
                logger.debug("Ending Token[" + sibling + "] and its tasks");
                sibling.setAbleToReactivateParent(false);
                Collection<TaskInstance> aliveTasks = taskMgmtInstance.getUnfinishedTasks(sibling);
                for (TaskInstance task : aliveTasks) {
                    task.end();
                    logger.debug("Ended Task[" + task.getTask().getName() + "] assigned to actor["
                            + task.getActorId() + "]");
                }
            }
            ExecutionContext parentContext = new ExecutionContext(token.getParent());
            leave(parentContext);
        } else {
            logger.debug("Token[" + token + "] cannot reactivate the parent.");
        }
    }

    Collection<Token> getSiblingToken(Token token) {
        Collection<Token> siblings = new ArrayList<Token>();
        Token parenToken = token.getParent();
        if (parenToken != null) {
            Collection<Token> childTokens = parenToken.getChildren().values();
            for (Token childToken : childTokens) {
                if (!(token.equals(childToken))) {
                    siblings.add(childToken);
                }
            }
        }
        return siblings;
    }

}
