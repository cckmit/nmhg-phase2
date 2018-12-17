/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.NoSuchPropertyException;

/**
 * @author radhakrishnan.j
 * 
 */
public abstract class PredicateEvaluator {

    public static final String QUERY_EXECUTION_METHOD = "executeQuery";
    public static final String ADD_DATE_DURATION = "addDurationToDate";
    public static final String SUBTRACT_DATE_DURATION = "subtractDurationFromDate";
    public static final String IS_NOT_PREDICATE = "isNotPredicate";
    public static final String EXPRESSION_STRING = "expressionString";

	public abstract boolean evaluatePredicate(Predicate aPredicate,
			Map<String, Object> evaluationContext)throws NoSuchPropertyException;

	public Map<Predicate, String> getPredicateEvaluationActionResults(
			Predicate predicate, Map<String, Object> evaluationContext) {
		DomainPredicate domainPredicate = (DomainPredicate) predicate;
		List<Predicate> leafPredicates = domainPredicate.getLeafPredicates();
		Map<Predicate, String> resultsMap = new HashMap<Predicate, String>();
		for (Predicate leafPredicate : leafPredicates) {
			if (leafPredicate instanceof Actionable) {
				String resultMessage = ((Actionable) leafPredicate).getAction()
						.performAction(evaluationContext);
				if (resultMessage != null && !resultMessage.equals("")) {
					resultsMap.put(leafPredicate, resultMessage);
				}
			}
		}
		return resultsMap;
	}

}
