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
package tavant.twms.web.xforms;

import java.util.List;

import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.worklist.WorkListItemService;

public class TaskViewServiceImplTest extends MockObjectTestCase {

    TaskViewServiceImpl target;

    Mock workListItemServiceMock;

    private TaskInstance taskInstance;

    public void setUp() {
        target = new TaskViewServiceImpl() {
            @SuppressWarnings("unused")
            Claim getClaimFromTask(TaskInstance taskInstance) {
                return new MachineClaim();
            }

            @SuppressWarnings("unused")
            List getTransitionNames(List transitions) {
                return null;
            }
        };
        workListItemServiceMock = mock(WorkListItemService.class);
        target.setWorkListItemService((WorkListItemService) workListItemServiceMock.proxy());
        taskInstance = new TaskInstance();
        taskInstance.setId(1);
        Task task = new Task();
        task.setName("Testing");
        taskInstance.setTask(task);
    }

    public void testGetTaskView() {
        Long id = new Long(1);
        workListItemServiceMock.expects(once()).method("findTask").with(eq(id)).will(returnValue(taskInstance));
        TaskView taskView = target.getTaskView(id);
        assertTrue(String.valueOf(taskView.getTaskId()).equals("1"));
        assertEquals(taskView.getTaskName(), "Testing");
    }
 
}
