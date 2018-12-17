package tavant.twms.infra;

import junit.framework.TestCase;

public class TreeNodeInfoTest extends TestCase {

	private static class MockTreeNode implements TreeNode {
		private final TreeNodeInfo two;

		private MockTreeNode(TreeNodeInfo two) {
			this.two = two;
		}

		public String getForestName() {
			return null;
		}

		public Long getId() {
			return null;
		}

		public TreeNodeInfo getNodeInfo() {
			return two;
		}

		public TreeNode getParent() {
			return null;
		}
	}

	public void testEqualsAndHashCode() {
		//same tree, and depth, different nodes.
		TreeNodeInfo one = new TreeNodeInfo(1,2,2,3);
		TreeNodeInfo two = new TreeNodeInfo(1,2,4,5);
		assertNotSame(one,two);
		assertNotSame(one.hashCode(),two.hashCode());
		
		two.setLft(2);
		two.setRgt(3);
		
		assertEquals(one.hashCode(),two.hashCode());
		assertEquals(one,two);
	}

	public void testIsDescendentOf() {
		TreeNodeInfo one = new TreeNodeInfo(1,1,1,4);
		TreeNodeInfo two = new TreeNodeInfo(1,2,2,3);
		
		assertFalse(one.isDescendentOf(two));
		assertTrue(two.isDescendentOf(one));
		
		two.setLft(5);
		two.setRgt(6);
		assertFalse(two.isDescendentOf(one));
		
		//Change the tree ids.
		two.setTreeId(10);
		two.setLft(2);
		two.setRgt(3);
		
		assertFalse(one.isDescendentOf(two));
		assertFalse(two.isDescendentOf(one));
		
	}

	public void testIsAncestorOf() {
		TreeNodeInfo one = new TreeNodeInfo(1,1,1,4);
		TreeNodeInfo two = new TreeNodeInfo(1,2,2,3);
		
		assertTrue(one.isAncestorOf(two));
		assertFalse(two.isAncestorOf(one));
		
		two.setLft(5);
		two.setRgt(6);
		assertFalse(two.isAncestorOf(one));
		
		//Change the tree ids.
		two.setTreeId(10);
		two.setLft(2);
		two.setRgt(3);
		
		assertFalse(one.isAncestorOf(two));
		assertFalse(two.isAncestorOf(one));
	}

	public void testIsAncestorOfWithoutTreeConsidertion() {
		TreeNodeInfo one = new TreeNodeInfo(1,1,1,4);
		TreeNodeInfo two = new TreeNodeInfo(1,2,2,3);
		
		assertTrue(one.isAncestorOfWithoutTreeConsideration(two));
		assertFalse(two.isAncestorOfWithoutTreeConsideration(one));
		
		two.setLft(5);
		two.setRgt(6);
		assertFalse(two.isAncestorOf(one));
		
		//Change the tree ids.
		two.setTreeId(10);
		two.setLft(2);
		two.setRgt(3);
		
		assertTrue(one.isAncestorOfWithoutTreeConsideration(two));
		assertFalse(two.isAncestorOfWithoutTreeConsideration(one));
	}	
	
	public void testIsLeaf() {
		TreeNodeInfo one = new TreeNodeInfo(1,1,1,4);
		TreeNodeInfo two = new TreeNodeInfo(1,2,2,3);
		assertFalse(one.isLeaf());
		assertTrue(two.isLeaf());
	}

	public void testIsTreeSame() {
		final TreeNodeInfo one = new TreeNodeInfo(1,1,1,4);
		final TreeNodeInfo two = new TreeNodeInfo(1,2,2,3);
		assertTrue( one.isTreeSame(new MockTreeNode(two)));
	}

	public void testGetInsertionDeletionOffset() {
		TreeNodeInfo two = new TreeNodeInfo(1,2,1,2);
		assertEquals(2,two.getInsertionDeletionOffset());
		
		two = new TreeNodeInfo(1,2,1,17);
		assertEquals(18,two.getInsertionDeletionOffset());
		
		two = new TreeNodeInfo(1,2,1,16);
		assertEquals(16,two.getInsertionDeletionOffset());
	}

}
