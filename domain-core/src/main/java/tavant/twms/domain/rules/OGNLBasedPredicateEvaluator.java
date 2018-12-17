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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * @author radhakrishnan.j
 *
 */
public class OGNLBasedPredicateEvaluator extends PredicateEvaluator {
	private static Logger logger = LogManager
			.getLogger(PredicateEvaluator.class);

	private final Map<String, Object> parsedExpressions = new HashMap<String, Object>();

	@Override
	public boolean evaluatePredicate(Predicate aPredicate,
			Map<String, Object> evaluationContext) throws NoSuchPropertyException {
	String expressionString = (String) evaluationContext
					.get(EXPRESSION_STRING);
			if (expressionString == null) {
				OGNLExpressionGenerator expressionGenerator = new OGNLExpressionGenerator();
				aPredicate.accept(expressionGenerator);
				expressionString = expressionGenerator.getExpressionString();
			}
			Boolean result = null;
			try {
				Object compiledExpression = parsedExpressions.get(expressionString);
				if (compiledExpression == null) {
					compiledExpression = Ognl.parseExpression(expressionString);
					parsedExpressions.put(expressionString, compiledExpression);
				}
				result = (Boolean) Ognl.getValue(compiledExpression, evaluationContext);
				return result;
			} 
			catch(NoSuchPropertyException e){
				throw e;
			}
			catch (OgnlException e) {
				throw new RuntimeException(e);
			} finally {
				if (logger.isDebugEnabled()) {
					String format = " Predicate {0}, expression {1}, evaluation context {2}, result {3} ";
					String message = MessageFormat.format(format, aPredicate
							.getDomainTerm(), expressionString, evaluationContext,
							result);
					logger.debug(message);
				}
		}
	}

	public Map<String, Object> getParsedExpressions() {
		return Collections.unmodifiableMap(parsedExpressions);
	}
}
