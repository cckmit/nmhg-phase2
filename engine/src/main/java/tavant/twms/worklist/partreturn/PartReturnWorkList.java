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
package tavant.twms.worklist.partreturn;

import java.util.List;

import tavant.twms.worklist.InboxItemList;

/**
 * TODO : For Backporting, I am supplying a constructor
 * to initiate from an InboxItemList. This class
 * and InboxItemList should be merged.
 * 
 * I would prefer InboxItemList since we can share it
 * across SR, Part returns any functionality with
 * an Inbox View
 * @author kannan.ekanath
 *
 */
public class PartReturnWorkList {

    // TODO:
    private List partReturnTaskItem;
    
    private int taskItemCount;

    public PartReturnWorkList() {
        
    }
    
    public PartReturnWorkList(InboxItemList inboxItemList) {
        this.partReturnTaskItem = inboxItemList.getInboxItems();
        this.taskItemCount = inboxItemList.getTotalCount();
    }
    
    public List getPartReturnTaskItem() {
        return partReturnTaskItem;
    }

    public void setPartReturnTaskItem(List partReturnTaskList) {
        this.partReturnTaskItem = partReturnTaskList;
    }

    public int getTaskItemCount() {
        return taskItemCount;
    }

    public void setTaskItemCount(int taskListCount) {
        this.taskItemCount = taskListCount;
    }
}
