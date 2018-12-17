package tavant.twms.domain.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import tavant.twms.domain.catalog.ItemGroup;

public class ItemGroupCriterionResolverTest extends TestCase {

	List<DummyClassWithCriteria> objects;
	ItemGroup group1, group2, group3, group4, group5;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.objects = new ArrayList<DummyClassWithCriteria>();
		setupItemGroups();
		setUpCommonObjects();
	}

	@SuppressWarnings("unchecked")
	public void testListWithOneObjectPerGroup() {
		List matches = ItemGroupCriterionResolver
				.findMostRelevantItemGroupMatch(this.objects, "forCriteria");
		assertEquals(1, matches.size());
		assertEquals(this.group5, ((DummyClassWithCriteria) matches.get(0))
				.getForCriteria().getProductType());
	}

	@SuppressWarnings("unchecked")
	public void testListWithOneObjectPerGroupAndOneObjectWithNothing() {
		DummyClassWithCriteria object = new DummyClassWithCriteria();
		object.getForCriteria().setProductType(null);
		this.objects.add(object);
		List matches = ItemGroupCriterionResolver
				.findMostRelevantItemGroupMatch(this.objects, "forCriteria");
		assertEquals(1, matches.size());
		assertEquals(this.group5, ((DummyClassWithCriteria) matches.get(0))
				.getForCriteria().getProductType());
	}

	@SuppressWarnings("unchecked")
	public void testListWithMoreThanOneObjectPerGroup() {
		addObjectWithItemGroup(this.group5);
		List matches = ItemGroupCriterionResolver
				.findMostRelevantItemGroupMatch(this.objects, "forCriteria");
		assertEquals(2, matches.size());
		for (Object object : matches) {
			assertEquals(this.group5, ((DummyClassWithCriteria) object)
					.getForCriteria().getProductType());
		}
	}

	@SuppressWarnings("unchecked")
	public void testListWithNoItemGroupSet() {
		this.objects.clear();
		for (int i = 0; i < 3; i++) {
			DummyClassWithCriteria object = new DummyClassWithCriteria();
			object.getForCriteria().setProductType(null);
			this.objects.add(object);
		}
		List matches = ItemGroupCriterionResolver
				.findMostRelevantItemGroupMatch(this.objects, "forCriteria");
		assertEquals(3, matches.size());
		for (Object object : matches) {
			assertNull(((DummyClassWithCriteria) object).getForCriteria()
					.getProductType());
		}
	}

	private void setupItemGroups() throws GroupInclusionException {
		this.group1 = new ItemGroup();
		this.group2 = new ItemGroup();
		this.group3 = new ItemGroup();
		this.group4 = new ItemGroup();
		this.group5 = new ItemGroup();
		this.group5.setIsPartOf(this.group4);
		this.group4.setIsPartOf(this.group3);
		this.group3.setIsPartOf(this.group2);
		this.group2.setIsPartOf(this.group1);
	}

	private void setUpCommonObjects() {
		addObjectWithItemGroup(this.group1);
		addObjectWithItemGroup(this.group2);
		addObjectWithItemGroup(this.group3);
		addObjectWithItemGroup(this.group4);
		addObjectWithItemGroup(this.group5);
	}

	private void addObjectWithItemGroup(ItemGroup group) {
		DummyClassWithCriteria object = new DummyClassWithCriteria();
		object.getForCriteria().setProductType(group);
		this.objects.add(object);
	}

}
