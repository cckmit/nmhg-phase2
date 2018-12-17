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
package tavant.twms.jbpm.assignment;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.infra.EngineRepositoryTestCase;

public class LoadBalancingServiceImplTest extends EngineRepositoryTestCase {

	private LoadBalancingService loadBalancingService;
	
	//TODO : Basic test with no users assigned any task . Need to include more tests.
	public void testFindUsersSortedByLoad() {
    	List<String> names = new ArrayList<String>();
    	names.add("processor");
    	names.add("processor1");
    	List<String> users = loadBalancingService.findUsersSortedByLoad(names);    	
    	assertNotNull(users);
    	assertEquals(0, users.size());
	}
	
	public void setLoadBalancingService(
			LoadBalancingService claimAssignmentService) {
		this.loadBalancingService = claimAssignmentService;
	}
}
