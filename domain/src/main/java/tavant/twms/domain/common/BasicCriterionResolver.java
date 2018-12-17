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

import tavant.twms.domain.claim.Criteria;
import tavant.twms.infra.BitSetValueComputer;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class BasicCriterionResolver {

	public static final Logger logger = Logger
			.getLogger(BasicCriterionResolver.class);

	/**
	 * Filters a List of objects based on their criteria's claim type and
	 * warranty type and returns the filtered list.
	 * 
	 * @param objects -
	 *            The List of objects to be filtered on.
	 * @param criteriaPropertyName -
	 *            The name of the property in the object that is of the type
	 *            Criteria.
	 * @return - returns a filtered List of Objects filtered based on the
	 *         precedence evaluation of the claim type and the warranty type.
	 */
	@SuppressWarnings("unchecked")
	public static List findMostRelevantMatch(List objects,
			String criteriaPropertyName) {
		List relevantObjects = new ArrayList();
		long mostRelevantScore = 0;
		for (Object object : objects) {
			try {
				Criteria criteria = (Criteria) Ognl.getValue(
						criteriaPropertyName, object);
				long score = getRelevanceScore(criteria);
				if (score > mostRelevantScore) {
					relevantObjects.clear();
					mostRelevantScore = score;
					relevantObjects.add(object);
				} else if (score == mostRelevantScore) {
					relevantObjects.add(object);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return relevantObjects;
	}

	/**
	 * Gets the relevance score based on a criteria's claim type and warranty
	 * type.
	 * 
	 * @param forCriteria -
	 *            the criteria for which the relevance score needs to be
	 *            computed.
	 * @return - returns a long value as a criteria's relevance score.
	 */
	private static long getRelevanceScore(Criteria forCriteria) {
		BitSetValueComputer bitSetValueComputer = new BitSetValueComputer();
		boolean[] bits = new boolean[] { forCriteria.getClaimType() != null,
				forCriteria.getWarrantyType() != null };
		return bitSetValueComputer.compute(bits);
	}
}
