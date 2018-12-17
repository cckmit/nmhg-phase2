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
package tavant.twms.worklist.supplier;

import java.util.Comparator;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;

/**
 *
 * Order task instances based on their claim id
 * @author kannan.ekanath
 *
 */
public class TaskInstanceClaimBasedComparator implements Comparator<TaskInstance>{

    private static final Logger logger = Logger.getLogger(TaskInstanceClaimBasedComparator.class);

    public int compare(TaskInstance t1, TaskInstance t2) {
        Assert.notNull(t1);
        Assert.notNull(t2);
        RecoveryClaim c1 = (RecoveryClaim) t1.getVariable("recoveryClaim");
        if(logger.isDebugEnabled()) {
            logger.debug("Task [" + t1 + "] has claim [" + c1 + "]");
        }
        RecoveryClaim c2 = (RecoveryClaim) t2.getVariable("recoveryClaim");
        if(logger.isDebugEnabled()) {
            logger.debug("Task [" + t2 + "] has claim [" + c2 + "]");
        }
        Assert.notNull(c1, "Task [" + t1 + "] doesnt have recovery claim");
        Assert.notNull(c1.getId(), "Task [" + t1 + "] doesnt have recovery claim with Id");
        Assert.notNull(c2, "Task [" + t2 + "] doesnt have recovery claim");
        Assert.notNull(c2.getId(), "Task [" + t2 + "] doesnt have recovery claim with Id");
        return c1.getId().compareTo(c2.getId());
    }


}
