package tavant.twms.domain.orgmodel;

import junit.framework.TestCase;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;

public class UserClusterTest extends TestCase {
	
	UserCluster cluster, c1, c2, c3, c4;
	User user;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cluster = new UserCluster();
		user = new User();
		c1 = new UserCluster();
		c2 = new UserCluster();
		c3 = new UserCluster();
		c4 = new UserCluster();
	}

	public void testUserCluster() {
		assertTrue(cluster.isClusterOfClusters());
		assertTrue(cluster.isClusterOfUsers());
	}
	
	public void testAdditionAndRemovalOfUser() throws GroupInclusionException {
		//Add User
		cluster.includeUser(user);
		assertTrue(cluster.isClusterOfUsers());
		assertFalse(cluster.isClusterOfClusters());
		assertTrue(cluster.getIncludedUsers().contains(user));
		
		//Remove User
		cluster.removeUser(user);
		assertTrue(cluster.isClusterOfUsers());
		assertTrue(cluster.isClusterOfClusters());
		assertFalse(cluster.getIncludedUsers().contains(user));
	}
	
	public void testAdditionAndRemovalOfCluster() throws GroupInclusionException, GroupHierarchyException {
		//Add Cluster
		cluster.includeGroup(c1);
		assertFalse(cluster.isClusterOfUsers());
		assertTrue(cluster.isClusterOfClusters());
		assertTrue(cluster.getConsistsOf().contains(c1));
		
		//Remove Cluster
		cluster.removeGroup(c1);
		assertTrue(cluster.isClusterOfUsers());
		assertTrue(cluster.isClusterOfClusters());
		assertFalse(cluster.getConsistsOf().contains(c1));
	}
	
	public void testAdditionOfUserWhenClusterHasIncludedClusters() {
		try {
			cluster.includeGroup(c1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			cluster.includeUser(user);
			fail("Group Inclusion Exception expected here.");
		} catch (GroupInclusionException e) {
		}
	}
	
	public void testAdditionOfClusterWhenClusterHasIncludedUsers() {
		try {
			cluster.includeUser(user);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			cluster.includeGroup(c1);
			fail("Group Inclusion Exception expected here.");
		} catch (GroupInclusionException e) {
		} catch (GroupHierarchyException e) {
			fail("Group Inclusion Exception expected here.");
		}
	}
	
	public void testAdditionOfClusterWhichAlreadyHasAParent() {
		try {
			c1.includeGroup(c2);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			cluster.includeGroup(c2);
			fail("Group Hierarchy Exception expected here.");
		} catch (GroupInclusionException e) {
			fail("Group Hierarchy Exception expected here.");
		} catch (GroupHierarchyException e) {
		}
	}
	
	public void testFindTopMostParent() throws Exception {
		cluster.includeGroup(c1);
		c1.includeGroup(c2);
		cluster.includeGroup(c3);
		
		assertEquals(cluster, cluster.findTopMostParent());
		assertEquals(cluster, c1.findTopMostParent());
		assertEquals(cluster, c2.findTopMostParent());
		assertEquals(cluster, c3.findTopMostParent());
		assertEquals(c4, c4.findTopMostParent());
	}
	
}
