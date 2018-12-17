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
import ognl.Ognl;
import org.apache.log4j.Logger;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ItemGroupCriterionResolver {
	public static final Logger logger = Logger
			.getLogger(ItemGroupCriterionResolver.class);

	/**
	 * This API basically takes a List of objects each having a criteria and
	 * then tries to find the List of the most relevant objects based on their
	 * item Group. For eg: I have some objects with item Groups set and some
	 * objects for All Item Groups. It will return the objects with the most
	 * relevant item group set. By Rule of thumb a child group is considered
	 * more relevant than the parent group. In case of absence of item group
	 * being set, the List of objects which conform to criterias with All Item
	 * Groups will be returned.
	 * 
	 * @param objects -
	 *            List of objects from which most relevant has to be found.
	 * @param criteriaPropertyName -
	 *            The field in the object that is of type Criteria.
	 * @return - returns a filtered List of objects with the most relevant item
	 *         group.
	 */
	@SuppressWarnings("unchecked")
	public static List findMostRelevantItemGroupMatch(List objects,
			String criteriaPropertyName) {
		List relevantObjects = new ArrayList();
		ItemGroup mostRelevantGroup = null;
		boolean itemGroupCriteriaExists = false;
		for (Object object : objects) {
			try {
				Criteria criteria = (Criteria) Ognl.getValue(
						criteriaPropertyName, object);
				ItemGroup group = criteria.getProductType();
				if (group == null && !itemGroupCriteriaExists) {
					relevantObjects.add(object);
				} else {
					itemGroupCriteriaExists = true;
					int relevance = getRelevanceOfItemGroup(group,
							mostRelevantGroup);
					if (relevance == 1) {
						mostRelevantGroup = group;
						relevantObjects = new ArrayList<Object>();
						relevantObjects.add(object);
					} else if (relevance == 0) {
						relevantObjects.add(object);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return relevantObjects;
	}

	/**
	 * If the group is the same as the currentRelevantGroup then 0 is returned.
	 * If the group is anywhere lower in the hierarchy for the
	 * currentRelevantGroup then 1 is returned. Else -1 is returned.
	 * 
	 * @param group -
	 *            The group for which Relevance is to be obtained.
	 * @param currentRelevantGroup -
	 *            The reference group with which the comparison needs to be
	 *            made.
	 * @return - returns an integral value corresponding to the comparison
	 *         between the Dealer Group and the current Most Relevant Group
	 */
	private static int getRelevanceOfItemGroup(ItemGroup group,
			ItemGroup currentRelevantGroup) {
		if (group.equals(currentRelevantGroup)) {
			return 0;
		}
		if (currentRelevantGroup == null
				|| isAChildGroup(group, currentRelevantGroup)) {
			return 1;
		}
		return -1;
	}

	/**
	 * Recursive API to find if the provided group is a child of the
	 * currentRelevantGroup.
	 * 
	 * @param group -
	 *            the group for which has to be checked to be a child group.
	 * @param currentRelevantGroup -
	 *            the group to be checked for being the parent of the group
	 *            provided as parameter.
	 * @return - returns a boolean value depending on if the group is or is not
	 *         lower in hierarchy to the currentRelevant Group
	 */
	private static boolean isAChildGroup(ItemGroup group,
			ItemGroup currentRelevantGroup) {
		ItemGroup parent = group.getIsPartOf();
		if (parent == null) {
			return false;
		}
		if (parent.equals(currentRelevantGroup)) {
			return true;
		}
		return isAChildGroup(parent, currentRelevantGroup);
	}
}
