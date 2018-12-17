/**
 * 
 */
package tavant.twms.worklist.supplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;

/**
 * @author kannan.ekanath
 *
 */
public class TestTaskInstanceClaimBasedComparator extends TestCase {

    public void testClaimOrdering() {
        List<TaskInstance> tasks = new ArrayList<TaskInstance>();
        TaskInstance t1 = new TaskInstance();
        Claim claim1 = new MachineClaim();
        claim1.setId(new Long(5));
        t1.setVariable("claim", claim1);
        
        TaskInstance t2 = new TaskInstance();
        Claim claim2 = new MachineClaim();
        claim2.setId(new Long(1));
        t2.setVariable("claim", claim2);
        
        TaskInstance t3 = new TaskInstance();
        Claim claim3 = new MachineClaim();
        claim3.setId(new Long(2));
        t3.setVariable("claim", claim3);
        
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        
        Collections.sort(tasks, new TaskInstanceClaimBasedComparator());
        
        assertEquals(t2, tasks.get(0));
        assertEquals(t3, tasks.get(1));
        assertEquals(t1, tasks.get(2));
    }
}
