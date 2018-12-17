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

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author vineeth.varghese
 * @date Mar 1, 2007
 */

/**
 * Need to relook at this node to find a better way of achieving the func
 */
public class EndTasksJoin extends Node {

    private static Logger logger = Logger.getLogger(EndTasksJoin.class);

    private String taskNamesToEnd;

    @Override
    public void read(Element element, JpdlXmlReader jpdlReader) {
        this.taskNamesToEnd = element.attributeValue("task-name-to-end");
        Assert.hasText(this.taskNamesToEnd, "The End Tasks join node [" + this
                + "] doesnt have a task-name-to-end attribute");
        if(logger.isDebugEnabled())
        {
            logger.debug("The task to end while joining is [" + this.taskNamesToEnd + "]");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        if (token.isAbleToReactivateParent()) {
            TaskMgmtInstance taskMgmtInstance = executionContext.getTaskMgmtInstance();
            if (isTokenEligibleForJoin(token, taskMgmtInstance)) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Token [" + token + "] can join");
                }
                token.setAbleToReactivateParent(false);
                Collection<Token> siblingTokens = getSiblingTokens(token);
                for (Token siblingToken : siblingTokens) {
                    siblingToken.setAbleToReactivateParent(false);
                    Collection<TaskInstance> tasks = taskMgmtInstance.getUnfinishedTasks(siblingToken);
                    for (TaskInstance task : tasks) {
                        task.end();
                        if(logger.isDebugEnabled()) {
                            logger.debug("Ended Task[" + task.getTask().getName() + "] assigned to actor["
                                    + task.getActorId() + "]");
                        }
                    }
                }
                ExecutionContext parentContext = new ExecutionContext(token.getParent());
                leave(parentContext);
            } else {
                if(logger.isDebugEnabled()) {
                    logger.debug("Token[" + token + "] is not eligible for a join, no tasks will be ended");
                }
                leave(executionContext);
            }
        }
    }

    private boolean isTokenEligibleForJoin(Token token, TaskMgmtInstance taskMgmtInstance) {
        Collection<Token> siblingTokens = getSiblingTokens(token);
        if(logger.isDebugEnabled()) {
            logger.debug("Token [" + token + "] has siblings " + siblingTokens);
        }
        /**
         * The token is eligible to join if there is a sibling token with a task
         * name designated by "taskNameToEnd"
         */
        boolean isEligibleForJoin = false;
        for (Token siblingToken : siblingTokens) {
            isEligibleForJoin = isEligibleForJoin || isSiblingTokenToBeEnded(taskMgmtInstance, siblingToken);
        }
        return isEligibleForJoin;
    }

    @SuppressWarnings("unchecked")
    private Collection<Token> getSiblingTokens(Token token) {
        return token.getParent() == null ? Collections.EMPTY_LIST : token.getParent().getChildren().values();
    }

    @SuppressWarnings("unchecked")
    private boolean isSiblingTokenToBeEnded(TaskMgmtInstance taskMgmtInstance, Token siblingToken) {
        Collection<TaskInstance> tasks = taskMgmtInstance.getUnfinishedTasks(siblingToken);
        if (tasks.size() == 1) {
            Collection taskNameList = StringUtils.commaDelimitedListToSet(this.taskNamesToEnd);
            if(logger.isDebugEnabled()) {
                logger.debug("The task name list to end is [" + taskNameList + "]");
            }
            return taskNameList.contains(tasks.iterator().next().getName());
        }
        return false;
    }
}
