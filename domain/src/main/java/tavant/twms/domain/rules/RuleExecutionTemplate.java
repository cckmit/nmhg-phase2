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

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_DUPLICACY_RULES;
import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_RULES;
import static tavant.twms.domain.rules.PredicateEvaluator.EXPRESSION_STRING;
import static tavant.twms.domain.rules.PredicateEvaluator.IS_NOT_PREDICATE;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import com.domainlanguage.time.CalendarDate;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.rules.group.AssignmentRuleGroupExecutionCallback;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.domain.rules.group.RuleGroupExecutionCallback;
import tavant.twms.security.SecurityHelper;

/**
 * @author radhakrishnan.j
 * 
 */
public class RuleExecutionTemplate implements BeanFactoryAware {
	private static Logger logger = LogManager.getLogger(RuleExecutionTemplate.class);

	private BeanFactory beanFactory;

	private PredicateEvaluator predicateEvaluator;
	private static int DUPLICATE_CLAIM_STR_LEN;

    public RuleExecutionTemplate() {
        DUPLICATE_CLAIM_STR_LEN = ("executeQuery(\"" + new ClaimDuplicacyQueryGenerator().buildSelectAndFromClause()).length();
    }

    @Required
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Required
	public void setPredicateEvaluator(PredicateEvaluator predicateEvaluator) {
		this.predicateEvaluator = predicateEvaluator;
	}



	public void executeAssignmentRuleGroups(AssignmentRuleGroupExecutionCallback callback) {
		Map<String, Object> actionResultHolder = callback.getActionResultHolder();
		List<DomainRuleGroup> rulesGroupsForExecution = callback.getRuleGroupsForExecution();
		for (DomainRuleGroup domainRuleGroup : rulesGroupsForExecution) {
			if (domainRuleGroup.getD().isActive() || !domainRuleGroup.getStatus().equalsIgnoreCase("inactive")) {
				Map<DomainRule, Map<Boolean, String>> results = new HashMap<DomainRule, Map<Boolean, String>>();
				boolean isClaimAssignedToLOAScheme = false;
				for (DomainRule domainRule : domainRuleGroup.getRules()) {
					if(!domainRule.getD().isActive() || domainRule.getStatus().equalsIgnoreCase("inactive"))
						continue;
					SortedMap<String, Object> ruleExecutionContext = determineAndSetDependencies(callback, domainRule);
					boolean ruleFailedCheck = executeRule(actionResultHolder, domainRule, ruleExecutionContext, results);
					if(ruleFailedCheck && ((AssignmentRuleAction) domainRule.getAction()).getLoaScheme()!=null ){
						isClaimAssignedToLOAScheme=true;	
						break;
					}
				}
				callback.setRuleEvaluationResult(results);
				if(callback.isLastExecutionSuccessful()) {
					int count = callback.getAssignableCount();
					if(count == 1)
						break;
					if(count > 1 && isClaimAssignedToLOAScheme)
						break;
					if(count > 1 && domainRuleGroup.getStopRuleProcessingOnMultipleResult())
						break;
				}else if(domainRuleGroup.getStopRuleProcessingOnNoResult()) {
					break;
				}
			}
		}
	}

	public void executeRuleGroups(RuleGroupExecutionCallback callback) {
		Map<String, Object> actionResultHolder = callback.getActionResultHolder();
		List<DomainRuleGroup> rulesGroupsForExecution = callback.getRuleGroupsForExecution();

		Map<DomainRule, Map<Boolean, String>> results = new HashMap<DomainRule, Map<Boolean, String>>();
		boolean stopRuleExecution = false;
		final  Claim claim = (Claim)callback.context.get("claim");
		for (DomainRuleGroup domainRuleGroup : rulesGroupsForExecution) {
			boolean ruleFired = false;
			if (domainRuleGroup.getD().isActive() || !domainRuleGroup.getStatus().equalsIgnoreCase("inactive")) {
				for (DomainRule domainRule : domainRuleGroup.getRules()) {
					if (!domainRule.getD().isActive() || domainRule.getStatus().equalsIgnoreCase("inactive")) {
						continue;
					}
					SortedMap<String, Object> ruleExecutionContext = determineAndSetDependencies(callback, domainRule);
					ruleFired = executeRule(actionResultHolder, domainRule, ruleExecutionContext, results);

					if (domainRuleGroup.getStopRuleProcessingOnFirstSuccess() && ruleFired) {
						stopRuleExecution = true;
						break;
					}
				}
			}
			if (stopRuleExecution || (domainRuleGroup.getStopRuleProcessingOnSuccess() && ruleFired)) {
				break;
			}
		}

		callback.setRuleEvaluationResult(results);
	}

	public void executeRules(RuleExecutionCallback callback) {
		Map<String, Object> actionResultHolder = callback.getActionResultHolder();
		List<DomainRule> rulesForExecution = callback.getRulesForExecution();
		Map<DomainRule, Map<Boolean, String>> results = new HashMap<DomainRule, Map<Boolean, String>>();

		for (DomainRule domainRule : rulesForExecution) {
			SortedMap<String, Object> ruleExecutionContext = determineAndSetDependencies(callback, domainRule);
			executeRule(actionResultHolder, domainRule, ruleExecutionContext, results);
		}

		callback.setRuleEvaluationResult(results);
	}

	private boolean executeRule(Map<String, Object> actionResultHolder, DomainRule rule,
			SortedMap<String, Object> ruleExecutionContext,
			Map<DomainRule, Map<Boolean, String>> results) {
		DomainPredicate predicate = rule.getPredicate();
		boolean condition = false;
		String clmFailureMsg = null;

		try {
			String predicateContext = predicate.getContext();

			if(predicate!=null && !predicate.isSystemDefinedCondition()) {
				String expressionString = rule.getOgnlExpression();
				ruleExecutionContext.put(EXPRESSION_STRING, expressionString);

				if (expressionString.startsWith("!")) {
					ruleExecutionContext.put(IS_NOT_PREDICATE, Boolean.TRUE);
				} else {
					ruleExecutionContext.put(IS_NOT_PREDICATE, Boolean.FALSE);
				}

				condition = predicateEvaluator.evaluatePredicate(predicate,
						ruleExecutionContext);
			} else {
				String className=predicate.getSystemDefinedConditionName();
				SystemDefinedBusinessCondition businessCondition=(SystemDefinedBusinessCondition)beanFactory.getBean(className);
				condition = businessCondition.execute(ruleExecutionContext);
			}


			if (condition) {
				if (CLAIM_RULES.equals(predicateContext)) {
					clmFailureMsg = processPredicateEvaluationActionResults(rule, ruleExecutionContext,
							predicateEvaluator.getPredicateEvaluationActionResults(predicate, ruleExecutionContext));
				} else if (CLAIM_DUPLICACY_RULES.equals(predicateContext)) {
					clmFailureMsg = duplicateClaimMessage(rule, ruleExecutionContext);
				} 
			}

			if (condition && rule.getRuleAudits()!= null && !rule.getRuleAudits().isEmpty()
					&& rule.getAction() != null && actionResultHolder.get("claimState")== null) {
				rule.getAction().performAction(actionResultHolder);
			}

			Map<Boolean, String> clmFailureMsgMap = new HashMap<Boolean, String>();
			clmFailureMsgMap.put(condition, clmFailureMsg);

			results.put(rule, clmFailureMsgMap);

		} catch(NoSuchPropertyException e){        	
			logger.error("Failed to evaluate rule " + rule.getName(), e);        	
		}
		catch (RuntimeException e) {
			if (logger.isInfoEnabled()) {
				logger.info(" Failed to evaluate rule condition for rule " + rule.getName(), e);
			}
		}        
		return condition;
	}

	private String processPredicateEvaluationActionResults(DomainRule rule, SortedMap<String, Object> context, Map<Predicate, String> predicateEvaluationActionResults) {
		StringBuffer failureMessage = new StringBuffer();
		failureMessage.append("");
		Collection<String> resultMessages = predicateEvaluationActionResults.values();

		failureMessage.append("(").append(rule.getRuleNumber().intValue()).append(")");
		Locale userLocale = new SecurityHelper().getLoggedInUser().getLocale();
        getContextualErrorMessage(rule, context, failureMessage, userLocale);
        if (null != rule.getAction()) {
			failureMessage.append("[").append(rule.getAction().getName()).append("]");
		}

		for (String message : resultMessages) {
			failureMessage.append(";").append(message);
		}
		return failureMessage.toString();
	}

    private void getContextualErrorMessage(DomainRule rule, SortedMap<String, Object> context, StringBuffer failureMessage, Locale userLocale) {
        Claim claim = (Claim) context.get("claim");
        if ("dateLesserBy(claim.failureDate,claim.filedOnDate,45,0)".equals(rule.getOgnlExpression())) {
            failureMessage
                    .append("Claim submitted late - ")
                    .append(duration(claim.getFailureDate(), claim.getFiledOnDate()))
                    .append(" day(s) since failure date and ")
                    .append(duration(claim.getFailureDate(), claim.getFiledOnDate()) - 45)
                    .append(" day(s) late");
        } else if ("claim.claimedItems.{ ? itemReference.referredInventoryItem.type.type.toLowerCase().equals(\"RETAIL\".toLowerCase()) && (applicablePolicy == null) }.size > 0".equals(rule.getOgnlExpression())) {
            Integer outOfWarrantyHours = null;
            Period outOfWarrantyPeriod = null;
            if (claim.getHoursInService() != null && claim.getHoursCovered() != null && (claim.getHoursInService().intValue() > claim.getHoursCovered())) {
                outOfWarrantyHours = claim.getHoursInService().intValue() - claim.getHoursCovered();
            }
            if (claim.getCoverageEndDate() != null && claim.getFailureDate().isAfter(claim.getCoverageEndDate())) {
                outOfWarrantyPeriod = getPeriod(claim.getCoverageEndDate(), claim.getFailureDate());
            }
            if (outOfWarrantyPeriod != null && outOfWarrantyHours != null) {
                failureMessage
                        .append("Failure occurred out with Warranty Period - ")
                        .append(getPeriodString(outOfWarrantyPeriod))
                        .append(" and ")
                        .append(outOfWarrantyHours)
                        .append(" hours out of warranty");
            } else if (outOfWarrantyPeriod != null) {
                failureMessage
                        .append("Failure occurred out with Warranty Period - ")
                        .append(getPeriodString(outOfWarrantyPeriod))
                        .append(" out of warranty");
            } else if (outOfWarrantyHours != null) {
                failureMessage
                        .append("Failure occurred out with Warranty Period - ")
                        .append(outOfWarrantyHours)
                        .append(" hours out of warranty");
            }
	        }else if(null != rule.getOgnlExpression() && rule.getOgnlExpression().contains("(!isInstalledPartBrandSameOfClaimBrand(claim))")){
	        	//failureMessage.append(rule.getFailureMessage()+": ");
	        	failureMessage.append("Claim Contains ");
	        	failureMessage.append(claim.getInstalledPartBrands());
	        	failureMessage.append(":\'{"+claim.getInstalledPartNumbers()+"}\'");
	        	failureMessage.append(" Installed brand parts and unit on claim is ");        	
	        	failureMessage.append(claim.getBrand());
	        }else if(null != rule.getOgnlExpression() && rule.getOgnlExpression().contains("(!isRemovedPartBrandSameOfClaimBrand(claim))")){
	        	//failureMessage.append(rule.getFailureMessage()+": ");
	        	failureMessage.append("Claim Contains ");
	        	failureMessage.append(claim.getReplacedPartsBrand());
	        	failureMessage.append(":\'{"+claim.getReplacedPartNumbers()+"}\'");
	        	failureMessage.append(" Removed brand parts and unit on claim is ");        	
	        	failureMessage.append(claim.getBrand());
	        }else if(null != rule.getOgnlExpression() && rule.getOgnlExpression().contains("(!isCasualPartBrandSameOfClaimBrand(claim))")){
	        	//failureMessage.append(rule.getFailureMessage()+": ");
	        	failureMessage.append("Claim Contains ");
	        	failureMessage.append(claim.getServiceInformation().getCausalBrandPart().getBrand());
	        	failureMessage.append(":\'{"+claim.getServiceInformation().getCausalBrandPart().getItemNumber()+"-"+claim.getServiceInformation().getCausalBrandPart().getItem().getDescription()+"}\'");
	        	failureMessage.append(" Causal brand part and unit on claim is ");        	
	        	failureMessage.append(claim.getBrand());
	        }else {
	            failureMessage.append(rule.getFailureMessageForLocale(userLocale.toString()));
	        }
    }

    private Period getPeriod(CalendarDate start, CalendarDate end) {
        return new Period(
                convertToJodaDate(start),
                convertToJodaDate(end),
                PeriodType.yearMonthDay());
    }

    private LocalDate convertToJodaDate(CalendarDate begin) {
        return new LocalDate(
                begin.breachEncapsulationOf_year(),
                begin.breachEncapsulationOf_month(),
                begin.breachEncapsulationOf_day());
    }

    private String getPeriodString(Period period) {
        StringBuffer sb = new StringBuffer();
        if (period.getYears() > 0) {
            sb.append(period.getYears()).append(" year(s)");
        }
        if (period.getMonths() > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(period.getMonths()).append(" month(s)");
        }
        if (period.getDays() > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(period.getDays()).append(" day(s)");
        }
        return sb.toString();
    }

    public Integer duration(CalendarDate startDate, CalendarDate endDate) {
        return startDate.through(endDate).lengthInDaysInt() - 1; // Since it's inclusive
    }

    @SuppressWarnings("unchecked")
	private String duplicateClaimMessage(DomainRule rule,
			Map<String, Object> evaluationContext) {
		Predicate aPredicate = rule.getPredicate();
		StringBuffer failureMessage = new StringBuffer();
		String failureMessageStr = null;

		failureMessage.append("(").append(rule.getRuleNumber().intValue()).append(")")
		.append(rule.getFailureMessage()).append("; ");
		failureMessage.append("Duplicate claims are: ");
		String expressionString = (String) evaluationContext.get(EXPRESSION_STRING);

		int endIndexOfDuplicacyPredicate = expressionString.indexOf(" &&");
		if(endIndexOfDuplicacyPredicate == -1) {
			endIndexOfDuplicacyPredicate = expressionString.length();
		}

		expressionString = expressionString.substring(DUPLICATE_CLAIM_STR_LEN, endIndexOfDuplicacyPredicate);
		expressionString = "executeDuplicateClaimsQuery(\"select claim.claimNumber from Claim claim"
			+ expressionString;
		List<String> dupClaimNos;

		try {
			Object compiledExpression = Ognl.parseExpression(expressionString);
			dupClaimNos = (List<String>) Ognl.getValue(compiledExpression, evaluationContext);

			for (String dupClaimNo : dupClaimNos) {
				failureMessage.append(dupClaimNo).append(",");
			}

			failureMessageStr = failureMessage.substring(0, failureMessage.length() - 1);
			failureMessageStr = failureMessageStr + "["+rule.getAction().getName()+"]";

		} catch (OgnlException e) {
			throw new RuntimeException(e);
		} finally {
			if (logger.isDebugEnabled()) {
				String format = " Predicate {0}, expression {1}, evaluation context {2},result {3} ";
				String message = MessageFormat.format(format, aPredicate
						.getDomainTerm(), expressionString, evaluationContext,
						failureMessageStr);
				logger.debug(message);
			}
		}
		return failureMessageStr;
	}

	/**
	 * Determine dependencies and make them available in the context.
	 *
	 * @param callback
	 * @param rule
	 * @return
	 */
	private SortedMap<String, Object> determineAndSetDependencies(ExecutionCallback callback, DomainRule rule) {
		RuleExecutionDependenciesIdentifier dependencyIdentifier = new RuleExecutionDependenciesIdentifier();
		SortedMap<String, Object> ruleExecutionContext = new OgnlContextHelper(beanFactory);
		Map<String, Object> transactionContext = callback.getTransactionContext();
		ruleExecutionContext.putAll(transactionContext);
		return ruleExecutionContext;
	}
}
