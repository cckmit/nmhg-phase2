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
package tavant.twms.domain.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.Dealership;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class DealerCriterionResolverTest extends TestCase {

	List<DummyClassWithCriteria> objects;
	DealerGroup group1, group2, group3, group4, group5;
	Dealership dealer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.objects = new ArrayList<DummyClassWithCriteria>();
		this.dealer = new Dealership();
		setupDealerGroups();
		setUpCommonObjects();
	}

	private void setupDealerGroups() throws GroupInclusionException {
		this.group1 = new DealerGroup();
		this.group2 = new DealerGroup();
		this.group3 = new DealerGroup();
		this.group4 = new DealerGroup();
		this.group5 = new DealerGroup();
		this.group5.setIsPartOf(this.group4);
		this.group4.setIsPartOf(this.group3);
		this.group3.setIsPartOf(this.group2);
		this.group2.setIsPartOf(this.group1);
		this.group5.includeDealer(this.dealer);
	}

	@SuppressWarnings("unchecked")
	public void testListWithOneObjectPerGroupAndNoObjectWithDealer() {
		List matches = DealerCriterionResolver.findMostRelevantDealerMatch(
				this.objects, "forCriteria");
		assertEquals(1, matches.size());
		assertEquals(this.group5, ((DummyClassWithCriteria) matches.get(0))
				.getForCriteria().getDealerCriterion().getDealerGroup());
	}

	@SuppressWarnings("unchecked")
	public void testListWithOneObjectPerGroupAndOneObjectWithNothing() {
		DummyClassWithCriteria object = new DummyClassWithCriteria();
		object.getForCriteria().setDealerCriterion(null);
		this.objects.add(object);
		List matches = DealerCriterionResolver.findMostRelevantDealerMatch(
				this.objects, "forCriteria");
		assertEquals(1, matches.size());
		assertEquals(this.group5, ((DummyClassWithCriteria) matches.get(0))
				.getForCriteria().getDealerCriterion().getDealerGroup());
	}

	@SuppressWarnings("unchecked")
	public void testListWithOneObjectPerGroupAndOneObjectWithDealer() {
		addObjectWithDealer();
		List matches = DealerCriterionResolver.findMostRelevantDealerMatch(
				this.objects, "forCriteria");
		assertEquals(1, matches.size());
		assertEquals(this.dealer, ((DummyClassWithCriteria) matches.get(0))
				.getForCriteria().getDealerCriterion().getDealer());
	}

	@SuppressWarnings("unchecked")
	public void testListWithMoreThanOneObjectPerGroupAndNoObjectWithDealer() {
		addObjectWithDealerGroup(this.group5);
		List matches = DealerCriterionResolver.findMostRelevantDealerMatch(
				this.objects, "forCriteria");
		assertEquals(2, matches.size());
		for (Object object : matches) {
			assertEquals(this.group5, ((DummyClassWithCriteria) object)
					.getForCriteria().getDealerCriterion().getDealerGroup());
		}
	}

	@SuppressWarnings("unchecked")
	public void testListWithOneObjectPerGroupAndMoreThanOneObjectWithDealer() {
		addObjectWithDealer();
		addObjectWithDealer();
		List matches = DealerCriterionResolver.findMostRelevantDealerMatch(
				this.objects, "forCriteria");
		assertEquals(2, matches.size());
		for (Object object : matches) {
			assertEquals(this.dealer, ((DummyClassWithCriteria) object)
					.getForCriteria().getDealerCriterion().getDealer());
		}
	}

	@SuppressWarnings("unchecked")
	public void testListWithNoDealerCriterionSet() {
		this.objects.clear();
		for (int i = 0; i < 3; i++) {
			DummyClassWithCriteria object = new DummyClassWithCriteria();
			object.getForCriteria().setDealerCriterion(null);
			this.objects.add(object);
		}
		List matches = DealerCriterionResolver.findMostRelevantDealerMatch(
				this.objects, "forCriteria");
		assertEquals(3, matches.size());
		for (Object object : matches) {
			assertNull(((DummyClassWithCriteria) object).getForCriteria()
					.getDealerCriterion());
		}
	}

	private void setUpCommonObjects() {
		addObjectWithDealerGroup(this.group1);
		addObjectWithDealerGroup(this.group2);
		addObjectWithDealerGroup(this.group3);
		addObjectWithDealerGroup(this.group4);
		addObjectWithDealerGroup(this.group5);
	}

	private void addObjectWithDealer() {
		DummyClassWithCriteria object = new DummyClassWithCriteria();
		object.getForCriteria().setDealerCriterion(
				new DealerCriterion(this.dealer));
		this.objects.add(object);
	}

	private void addObjectWithDealerGroup(DealerGroup group) {
		DummyClassWithCriteria object = new DummyClassWithCriteria();
		object.getForCriteria().setDealerCriterion(new DealerCriterion(group));
		this.objects.add(object);
	}

}
