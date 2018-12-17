package tavant.twms.infra;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.infra.NestedSetInterceptor.IdAwareLinkedHashSet;
import tavant.twms.infra.NestedSetInterceptor.SortedMapOfOrderedSets;
import tavant.twms.infra.NestedSetInterceptor.TreeNodes;

public class NestedSetInterceptorTest extends DomainRepositoryTestCase {
	private SampleGroupRepository sampleGroupRepository;
	private static TreeNodes treeNodes = new TreeNodes();

	/**
	 * @param testGroupRepository
	 *            the testGroupRepository to set
	 */
	@Required
	public void setSampleGroupRepository(
			SampleGroupRepository testGroupRepository) {
		this.sampleGroupRepository = testGroupRepository;
	}

	public void testIdAwareSet() {
		IdAwareLinkedHashSet fixture = new IdAwareLinkedHashSet();
		assertEquals(0, fixture.size());

		SampleGroup _id1 = new SampleGroup();
		_id1.setId(1L);
		fixture.add(_id1);
		assertEquals(1, fixture.size());

		SampleGroup _id2 = new SampleGroup();
		_id2.setId(2L);
		fixture.add(_id2);
		assertEquals(2, fixture.size());

		
		SampleGroup _id2New = new SampleGroup();
		_id2New.setId(2L);
		fixture.add(_id2New);
		assertEquals(2,fixture.size());
		assertTrue(fixture.contains(_id1));
		assertFalse(fixture.contains(_id2));
		assertTrue(fixture.contains(_id2New));
		
		SampleGroup _noID = new SampleGroup();		
		fixture.add(_noID);
		assertEquals(3,fixture.size());
		assertTrue(fixture.contains(_noID));
	}

	public void testSortedMapOfOrderedSets() {
		SortedMapOfOrderedSets fixture = new SortedMapOfOrderedSets();
		SampleGroup sampleGroup = new SampleGroup();
		sampleGroup.setId(1L);
		
		assertNotNull(fixture.get(sampleGroup.getForestName()));
		fixture.addNode(sampleGroup);
		
		String sampleForest = sampleGroup.getForestName();
		Set<TreeNode> linkedHashSet = fixture.get(sampleForest);
		assertEquals(1,linkedHashSet.size());
		assertTrue(linkedHashSet.contains(sampleGroup));
		
		sampleGroup = new SampleGroup();
		sampleGroup.setId(2L);
		fixture.addNode(sampleGroup);
		assertEquals(2,linkedHashSet.size());
		assertTrue(linkedHashSet.contains(sampleGroup));
		
		sampleGroup = new SampleGroup() {

			@Override
			public String getForestName() {
				return "Another Forest";
			}
			
		};
		fixture.addNode(sampleGroup);
		linkedHashSet = fixture.get("Another Forest");
		assertEquals(1,linkedHashSet.size());
		assertTrue(linkedHashSet.contains(sampleGroup));
	}
	
	public void testTreeNodes() {
		SampleGroup sampleGroup = new SampleGroup();
		assertFalse(treeNodes.isPresent(sampleGroup));
		Set<TreeNode> nodesInForest = treeNodes.getNodesInForest(sampleGroup.getForestName());
		
		treeNodes.add(sampleGroup);
		assertTrue(treeNodes.isPresent(sampleGroup));
		assertEquals(1,nodesInForest.size());
		assertTrue(nodesInForest.contains(sampleGroup));
		
		sampleGroup.setId(1L);
		assertTrue(treeNodes.isPresent(sampleGroup));
		assertEquals(1,nodesInForest.size());
		assertTrue(nodesInForest.contains(sampleGroup));
		
		sampleGroup = new SampleGroup();
		sampleGroup.setId(1L);
		assertTrue(treeNodes.add(sampleGroup));
		assertTrue(treeNodes.isPresent(sampleGroup));
		
		assertEquals(1,nodesInForest.size());
		assertTrue(nodesInForest.contains(sampleGroup));
		
		assertTrue(treeNodes.remove(sampleGroup));
		
		assertFalse(treeNodes.isPresent(sampleGroup));
		
		assertEquals(0,nodesInForest.size());
	}
	
	public void testSave() {
		/**
		 * 1 2 save all -> flush -> clear -> load -> assert
		 */
		SampleGroup group1 = new SampleGroup();
		group1.setGroupName("group 1");

		SampleGroup group2 = new SampleGroup();
		group2.setGroupName("group 2");

		group1.includeGroup(group2);
		sampleGroupRepository.save(group1);

		Long treeId = group1.getId();
		assertNotNull(treeId);
		assertNotNull(group2.getId());

		flushAndClear();

		final List<SampleGroup> all = sampleGroupRepository.findAll();
		assertEquals(2, all.size());
		assertNull(all.iterator().next().getParent());
		final Iterator<SampleGroup> iterator = all.iterator();
		assertEquals(new TreeNodeInfo(treeId, 1, 1, 4), iterator.next()
				.getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 3), iterator.next()
				.getNodeInfo());
	}

	public void testUpdate() {
		/**
		 * 1 2 save 1 -> flush -> clear -> load -> link 1 and 2 -> update ->
		 * flush -> clear -> load -> assert
		 */

		SampleGroup group1 = new SampleGroup();
		group1.setGroupName("group 1");

		sampleGroupRepository.save(group1);
		Long treeId = group1.getId();
		assertNotNull(treeId);

		/**
		 * clear the session and reload snapshot.
		 */

		flushAndClear();

		SampleGroup group2 = new SampleGroup();
		group2.setGroupName("group 2");

		sampleGroupRepository.save(group2);
		assertNotNull(group2.getId());

		group1 = sampleGroupRepository.findById(treeId);
		group1.includeGroup(group2);
		sampleGroupRepository.update(group1);

		flushAndClear();

		final List<SampleGroup> all = sampleGroupRepository.findAll();
		assertEquals(2, all.size());
		Iterator<SampleGroup> iterator = all.iterator();
		assertNull(iterator.next().getParent());

		iterator = all.iterator();
		assertEquals(new TreeNodeInfo(treeId, 1, 1, 4), iterator.next()
				.getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 3), iterator.next()
				.getNodeInfo());
	}

	public void testComplexityLevel_1() {
		/**
		 * 0 1 2
		 */
		SampleGroup[] testGroups = new SampleGroup[11];
		for (int i = 0; i < 3; i++) {
			testGroups[i] = new SampleGroup();
			testGroups[i].setGroupName(" group " + i);
		}
		testGroups[0].includeGroup(testGroups[1]);
		testGroups[0].includeGroup(testGroups[2]);

		sampleGroupRepository.save(testGroups[0]);

		Long treeId = testGroups[0].getId();
		for (int i = 0; i < 3; i++) {
			assertNotNull(testGroups[i].getId());
		}

		/**
		 * node info 0 -> (1,6) 1 -> (2,3) 2 -> (4,5)
		 */
		flush();

		assertEquals(new TreeNodeInfo(treeId, 1, 1, 6), testGroups[0]
				.getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 3), testGroups[1]
				.getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 4, 5), testGroups[2]
				.getNodeInfo());

		flushAndClear();

		assertEquals(3, sampleGroupRepository.findAll().size());

		// It turns out that the order in which the children are iterated over
		// while defining the node infos would determine these numbers :-?
		assertEquals(new TreeNodeInfo(treeId, 1, 1, 6), sampleGroupRepository
				.findById(testGroups[0].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 3), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 4, 5), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
	}

	public void testComplexityLevel_MoveNodesAround() {
		/**
		 * 0 1 2 3 4 5 6
		 */
		SampleGroup[] testGroups = new SampleGroup[11];
		for (int i = 0; i < 7; i++) {
			testGroups[i] = new SampleGroup();
			testGroups[i].setGroupName(" group " + i);
		}
		testGroups[0].includeGroup(testGroups[1]);
		testGroups[0].includeGroup(testGroups[2]);

		testGroups[1].includeGroup(testGroups[3]);
		testGroups[1].includeGroup(testGroups[4]);

		testGroups[2].includeGroup(testGroups[5]);
		testGroups[2].includeGroup(testGroups[6]);

		sampleGroupRepository.save(testGroups[0]);

		Long treeId = testGroups[0].getId();
		for (int i = 0; i < 7; i++) {
			assertNotNull(testGroups[i].getId());
		}

		/**
		 * node info 0 -> (1,14) 1 -> (2,7) 2 -> (8,13) 3 -> (3,4) 4 -> (5,6)
		 * 5-> (9,10) 6 -> (11,12)
		 */
		flushAndClear();

		assertEquals(7, sampleGroupRepository.findAll().size());

		// It turns out that the order in which the children are iterated over
		// while defining the node infos would determine these numbers :-?
		// Its hard to test with a hash set based association
		assertEquals(new TreeNodeInfo(treeId, 1, 1, 14), sampleGroupRepository
				.findById(treeId).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 7), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 8, 13), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 3, 4), sampleGroupRepository
				.findById(testGroups[3].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 5, 6), sampleGroupRepository
				.findById(testGroups[4].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 9, 10), sampleGroupRepository
				.findById(testGroups[5].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 11, 12), sampleGroupRepository
				.findById(testGroups[6].getId()).getNodeInfo());

		SampleGroup _1_reloaded = sampleGroupRepository.findById(testGroups[1]
				.getId());
		SampleGroup _4_reloaded = sampleGroupRepository.findById(testGroups[4]
				.getId());
		_1_reloaded.excludeGroup(_4_reloaded);
		sampleGroupRepository.update(_1_reloaded);
		flushAndClear();

		assertEquals(7, sampleGroupRepository.findAll().size());

		/**
		 * node info 0 -> (1,12) 1 -> (2,5) 2 -> (6,11) 3 -> (3,4) 4->(1,2) 5->
		 * (7,8) 6 -> (9,10)
		 */
		assertEquals(new TreeNodeInfo(treeId, 1, 1, 12), sampleGroupRepository
				.findById(treeId).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 5), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 6, 11), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 3, 4), sampleGroupRepository
				.findById(testGroups[3].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 7, 8), sampleGroupRepository
				.findById(testGroups[5].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 9, 10), sampleGroupRepository
				.findById(testGroups[6].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[4].getId(), 1, 1, 2),
				sampleGroupRepository.findById(testGroups[4].getId())
						.getNodeInfo());

		assertEquals(7, sampleGroupRepository.findAll().size());

		// Check orphaned node.
		SampleGroup group4 = (SampleGroup) getSession().get(SampleGroup.class,
				testGroups[4].getId());
		assertNull(group4.getMyParent());

		/**
		 * From above to this structure ->
		 * 
		 * node info 0 -> (1,12) 1 -> (2,3) 3 -> (4,11) 2-> (5,10) 4->(1,2) 5->
		 * (6,7) 6 -> (8,9)
		 */

		SampleGroup _0_reloaded = sampleGroupRepository.findById(treeId);
		_1_reloaded = sampleGroupRepository.findById(testGroups[1].getId());
		SampleGroup _2_reloaded = sampleGroupRepository.findById(testGroups[2]
				.getId());
		SampleGroup _3_reloaded = sampleGroupRepository.findById(testGroups[3]
				.getId());

		_1_reloaded.excludeGroup(_3_reloaded);
		_0_reloaded.excludeGroup(_2_reloaded);

		flushAndClear();

		assertEquals(7, sampleGroupRepository.findAll().size());

		_0_reloaded = sampleGroupRepository.findById(treeId);
		_1_reloaded = sampleGroupRepository.findById(testGroups[1].getId());
		_2_reloaded = sampleGroupRepository.findById(testGroups[2].getId());
		_3_reloaded = sampleGroupRepository.findById(testGroups[3].getId());

		assertEquals(new TreeNodeInfo(treeId, 1, 1, 4), _0_reloaded
				.getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 3), _1_reloaded
				.getNodeInfo());

		assertEquals(new TreeNodeInfo(testGroups[3].getId(), 1, 1, 2),
				_3_reloaded.getNodeInfo());

		assertEquals(new TreeNodeInfo(testGroups[2].getId(), 1, 1, 6),
				_2_reloaded.getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[2].getId(), 2, 2, 3),
				sampleGroupRepository.findById(testGroups[5].getId())
						.getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[2].getId(), 2, 4, 5),
				sampleGroupRepository.findById(testGroups[6].getId())
						.getNodeInfo());

		_0_reloaded.includeGroup(_3_reloaded);
		_3_reloaded.includeGroup(_2_reloaded);
		sampleGroupRepository.update(_0_reloaded);
		sampleGroupRepository.update(_3_reloaded);
		flushAndClear();

		assertEquals(7, sampleGroupRepository.findAll().size());

		assertEquals(new TreeNodeInfo(treeId, 1, 1, 12), sampleGroupRepository
				.findById(treeId).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 3), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 5, 10), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 4, 11), sampleGroupRepository
				.findById(testGroups[3].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 6, 7), sampleGroupRepository
				.findById(testGroups[5].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 8, 9), sampleGroupRepository
				.findById(testGroups[6].getId()).getNodeInfo());
	}

	public void testVeryComplex() {
		SampleGroup[] testGroups = new SampleGroup[11];
		for (int i = 0; i < 11; i++) {
			testGroups[i] = new SampleGroup();
			testGroups[i].setGroupName(" group " + i);
		}
		testGroups[0].includeGroup(testGroups[1]);
		testGroups[0].includeGroup(testGroups[2]);

		testGroups[1].includeGroup(testGroups[3]);
		testGroups[1].includeGroup(testGroups[4]);

		testGroups[2].includeGroup(testGroups[5]);
		testGroups[2].includeGroup(testGroups[6]);

		testGroups[6].includeGroup(testGroups[10]);

		testGroups[4].includeGroup(testGroups[7]);
		testGroups[4].includeGroup(testGroups[8]);

		testGroups[7].includeGroup(testGroups[9]);

		sampleGroupRepository.save(testGroups[0]);

		for (int i = 0; i < 11; i++) {
			assertNotNull(testGroups[i].getId());
		}

		/**
		 * node info 0 -> (1,22) 1 -> (2,13) 2 -> (14,21) 3 -> (3,4) 4 -> (5,12)
		 * 5 -> (15,16) 6 -> (17,20) 7 -> (6,9) 8 -> (10,11) 10-> (18,19) 9 ->
		 * (7,8)
		 */
		flushAndClear();

		assertEquals(11, sampleGroupRepository.findAll().size());

		Long treeId = testGroups[0].getId();
		assertEquals(new TreeNodeInfo(treeId, 1, 1, 22), sampleGroupRepository
				.findById(treeId).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 13), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 14, 21), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 3, 4), sampleGroupRepository
				.findById(testGroups[3].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 5, 12), sampleGroupRepository
				.findById(testGroups[4].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 15, 16), sampleGroupRepository
				.findById(testGroups[5].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 17, 20), sampleGroupRepository
				.findById(testGroups[6].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 6, 9), sampleGroupRepository
				.findById(testGroups[7].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 10, 11), sampleGroupRepository
				.findById(testGroups[8].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 5, 7, 8), sampleGroupRepository
				.findById(testGroups[9].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 18, 19), sampleGroupRepository
				.findById(testGroups[10].getId()).getNodeInfo());

		/**
		 * node info 0 -> (1,22) 1 -> (2,15) 2 -> (16,21) 3 -> (3,4) 4 -> (5,14)
		 * 5 -> (17,18) 6 -> (19,20) 7 -> (6,11) 8 -> (12,13) 9 -> (7,8) 10->
		 * (9,10)
		 * 
		 * 
		 */
		testGroups[10] = sampleGroupRepository.findById(testGroups[10].getId());
		testGroups[6] = sampleGroupRepository.findById(testGroups[6].getId());
		testGroups[6].excludeGroup(testGroups[10]);

		testGroups[7] = sampleGroupRepository.findById(testGroups[7].getId());
		testGroups[7].includeGroup(testGroups[10]);

		sampleGroupRepository.save(testGroups[10]);
		sampleGroupRepository.save(testGroups[6]);
		sampleGroupRepository.save(testGroups[7]);
		flushAndClear();

		assertEquals(11, sampleGroupRepository.findAll().size());

		assertEquals(new TreeNodeInfo(treeId, 1, 1, 22), sampleGroupRepository
				.findById(treeId).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 15), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 16, 21), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 3, 4), sampleGroupRepository
				.findById(testGroups[3].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 5, 14), sampleGroupRepository
				.findById(testGroups[4].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 17, 18), sampleGroupRepository
				.findById(testGroups[5].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 19, 20), sampleGroupRepository
				.findById(testGroups[6].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 6, 11), sampleGroupRepository
				.findById(testGroups[7].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 4, 12, 13), sampleGroupRepository
				.findById(testGroups[8].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 5, 7, 8), sampleGroupRepository
				.findById(testGroups[9].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 5, 9, 10), sampleGroupRepository
				.findById(testGroups[10].getId()).getNodeInfo());

		flushAndClear();

		assertEquals(11, sampleGroupRepository.findAll().size());

		testGroups[1] = sampleGroupRepository.findById(testGroups[1].getId());
		testGroups[4] = sampleGroupRepository.findById(testGroups[4].getId());

		testGroups[1].excludeGroup(testGroups[4]);

		/**
		 * node info 0 -> (1,12) 1 -> (2,5) 2 -> (6,11) 3 -> (3,4) 4 -> (1,10) 5 ->
		 * (7,8) 6 -> (9,10) 7 -> (2,7) 8 -> (8,9) 9 -> (3,4) 10-> (5,6)
		 * 
		 * 
		 */
		sampleGroupRepository.update(testGroups[4]);
		sampleGroupRepository.update(testGroups[1]);
		flushAndClear();

		assertEquals(11, sampleGroupRepository.findAll().size());

		assertEquals(new TreeNodeInfo(treeId, 1, 1, 12), sampleGroupRepository
				.findById(treeId).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 2, 5), sampleGroupRepository
				.findById(testGroups[1].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 2, 6, 11), sampleGroupRepository
				.findById(testGroups[2].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 3, 4), sampleGroupRepository
				.findById(testGroups[3].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[4].getId(), 1, 1, 10),
				sampleGroupRepository.findById(testGroups[4].getId())
						.getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 7, 8), sampleGroupRepository
				.findById(testGroups[5].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(treeId, 3, 9, 10), sampleGroupRepository
				.findById(testGroups[6].getId()).getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[4].getId(), 2, 2, 7),
				sampleGroupRepository.findById(testGroups[7].getId())
						.getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[4].getId(), 2, 8, 9),
				sampleGroupRepository.findById(testGroups[8].getId())
						.getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[4].getId(), 3, 3, 4),
				sampleGroupRepository.findById(testGroups[9].getId())
						.getNodeInfo());
		assertEquals(new TreeNodeInfo(testGroups[4].getId(), 3, 5, 6),
				sampleGroupRepository.findById(testGroups[10].getId())
						.getNodeInfo());

	}

}
