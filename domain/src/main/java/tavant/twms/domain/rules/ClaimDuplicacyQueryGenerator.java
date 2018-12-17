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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jun 24, 2007
 * Time: 12:06:38 AM
 */

package tavant.twms.domain.rules;

import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_DUPLICACY_RULES;
import static tavant.twms.domain.rules.PredicateEvaluator.ADD_DATE_DURATION;
import static tavant.twms.domain.rules.PredicateEvaluator.SUBTRACT_DATE_DURATION;
import static tavant.twms.domain.rules.Type.DATE;
import static tavant.twms.domain.rules.Type.STRING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
import tavant.twms.domain.claim.ClaimState;

public class ClaimDuplicacyQueryGenerator extends EmptyVisitor {

	private final StringBuffer queryString = new StringBuffer(100);
	private Stack<String> joinedEntityAlias = new Stack<String>();
	private final int randomNumber = 0;
	private IBusinessObjectModel businessObjectModel;
	private Stack<HashMap<String, String>> parameterForColEvaluation = new Stack<HashMap<String, String>>();
	private static final String DATE_FORMAT = "'YY-mm-dd'";

	@SuppressWarnings("unchecked")
	public ClaimDuplicacyQueryGenerator() {
		businessObjectModel = BusinessObjectModelFactory.getInstance()
				.getBusinessObjectModel(CLAIM_DUPLICACY_RULES);
		queryString.append(" where claim.id != ${claim.id}$ and claim.claimNumber is not null ");
		queryString.append(" and claim.activeClaimAudit.state not in (");
		queryString.append("'").append(ClaimState.DELETED.name()).append("'");
		queryString.append(",").append("'").append(ClaimState.DEACTIVATED.name()).append("'");
		queryString.append(")");
	}

	String buildSelectAndFromClause() {
		return ("select count(*) from Claim claim");
	}

	String buildWhereClause() {
		return queryString.toString();
	}

	public String getQuery() {
		return new StringBuilder(200).append(buildSelectAndFromClause())
				.append(buildWhereClause()).toString();
	}

	@Override
	public void visit(All visitable) {
		visitNAryPredicate(visitable, " and ");
	}

	@Override
	public void visit(Any visitable) {
		visitNAryPredicate(visitable, " or ");
	}

    private void removeTrailingJoiningTerm(String joiningTerm) {
        int queryLength = queryString.length();
        queryString.delete(queryLength - joiningTerm.length(), queryLength);
    }

	private void visitNAryPredicate(NAryPredicate nAryPredicate,
			String joiningTerm) {
		List<Predicate> predicates = nAryPredicate.getPredicates();
		boolean isNotFromCollection = joinedEntityAlias.isEmpty();

		if(predicates.isEmpty()) {
			return;
		}

		if(isNotFromCollection) {
			queryString.append(" and (");
		}

        boolean visitedAtleastOnePredicate = false;

        for (Predicate predicate : predicates) {
            if(!shouldAcceptPredicate(predicate)) {
                continue;
            }

            if(isNotFromCollection && predicate instanceof DomainPredicate) {
                removeTrailingJoiningTerm(joiningTerm);
            }

            predicate.accept(this);

            visitedAtleastOnePredicate = true;

            if (isNotFromCollection) {
		        queryString.append(joiningTerm);
			}
		}

        if(isNotFromCollection) {
            if(visitedAtleastOnePredicate) {
                removeTrailingJoiningTerm(joiningTerm);
            } else {
                queryString.append("1=1");
            }

            queryString.append(")");
        }
	}

    private boolean shouldAcceptPredicate(Predicate predicate) {
        return !(predicate instanceof DomainPredicate) ||
                CLAIM_DUPLICACY_RULES.equals(((DomainPredicate) predicate).getContext());

    }

	@Override
	public void visit(DateGreaterBy dateGreaterThan) {
		visit(dateGreaterThan, true, dateGreaterThan.getComparisionType());
	}

	@Override
	public void visit(DateLesserBy dateLessThan) {
		visit(dateLessThan, false, dateLessThan.getComaprisionType());
	}

	public void visit(AbstractDateDurationPredicate dateDurationPredicate,
			boolean isGreaterThan, int comparisionType) {

		String methodExpr = getEscapedMethodExpr(dateDurationPredicate,
				isGreaterThan);
		//todo:this is an oracle specific function.Not supported by std hql functions
 	 	//A temp fix to make it work in Mysql is to create a user function called to_date.      
 	 	methodExpr="to_date( "+methodExpr+" ,"+DATE_FORMAT+")";

		if (isGreaterThan) {
			queryString.append(methodExpr);
		} else {
			dateDurationPredicate.getLhs().accept(this);
		}

		if(comparisionType == AbstractDateDurationPredicate.COMPARE_TYPE_EXACTLY) {
			queryString.append(" = ");
		}else if(comparisionType == AbstractDateDurationPredicate.COMPARE_TYPE_ATLEAST) {
			queryString.append(" >= ");
		}else if(comparisionType == AbstractDateDurationPredicate.COMPARE_TYPE_ATMOST) {
			queryString.append(" <= ");
		}

		if (isGreaterThan) {
			dateDurationPredicate.getLhs().accept(this);
		} else {
			queryString.append(methodExpr);
		}
		
		if(comparisionType == AbstractDateDurationPredicate.COMPARE_TYPE_ATMOST) {
			queryString.append(" and ")
			.append(getOgnlExpressionForValue(dateDurationPredicate.getLhs(),true,true));
			if(isGreaterThan) {
				queryString.append(" >= ");
			}else {
				queryString.append(" <= ");
			}
			dateDurationPredicate.getLhs().accept(this);
		}
	}

	private String getEscapedMethodExpr(
			AbstractDateDurationPredicate dateDurationPredicate,
			boolean isGreaterThan) {

		return new StringBuffer(100)
				// we prefix the single quote for escaping the date represented
				// by this ognl expr.
				.append("'${").append(
						isGreaterThan ? SUBTRACT_DATE_DURATION
								: ADD_DATE_DURATION).append("(").append(
						getOgnlExpressionForValue(dateDurationPredicate
								.getLhs(), false, false)).append(", ").append(
						dateDurationPredicate.getRhs().getLiteral()).append(
						", ").append(dateDurationPredicate.getDurationType())
				.append(")}$'").toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void visit(IsSameAs isSameAs) {
		boolean isFromCollection = !joinedEntityAlias.isEmpty();

		DomainSpecificVariable dsv = isSameAs.getOperand();
		if (!getManyDSVCollectionMap().containsKey(dsv.getFieldName())
				&& !isFromCollection) {
			
			dsv.accept(this);
			queryString.append(" = ");
			String parameter = getOgnlExpressionForValue(dsv, true, true);
			queryString.append(parameter);
		} else if (isFromCollection
				&& !getManyDSVCollectionMap().containsKey(dsv.getFieldName())) {
			HashMap<String, String> mapToPush = new HashMap<String, String>();
			String fieldName = dsv.getFieldName();
			if (!dsv.isSimpleVariable()) {
				fieldName = fieldName + ".id";
			}
			mapToPush.put(fieldName, " = ");
			parameterForColEvaluation.push(mapToPush);
		} else {
			queryString.append(getManyDSVCollectionMap()
					.get(dsv.getFieldName()));
		}
	}

	@Override
	public void visit(Constant constant) {
		queryString.append(constant.getLiteral());
	}

	@Override
	public void visit(DomainSpecificVariable dsv) {
		boolean isFromCollection = !joinedEntityAlias.isEmpty();
		boolean isStringType = STRING.equals(dsv.getType());
		if (!isFromCollection) {
			if (isStringType) {
				queryString.append("lower(");
			}
			queryString.append(dsv.getToken());
			if (!dsv.isSimpleVariable()) {
				queryString.append(".id");
			}

			if (isStringType) {
				queryString.append(")");
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void visit(GreaterThan greaterThan) {
		boolean isFromCollection = !joinedEntityAlias.isEmpty();
		if (isFromCollection) {
			String operator = " >= " + ((Constant)greaterThan.getRhs()).getLiteral() + " + ";
			HashMap<String, String> mapToPush = new HashMap<String, String>();
			mapToPush.put(((DomainSpecificVariable) greaterThan.getLhs())
					.getFieldName(), operator);
			parameterForColEvaluation.push(mapToPush);
		} else {
			visit(greaterThan, true);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void visit(LessThan lessThan) {
		boolean isFromCollection = !joinedEntityAlias.isEmpty();
		if (isFromCollection) {
			String operator = " + " + ((Constant)lessThan.getRhs()).getLiteral() + " <= " ;
			HashMap<String, String> mapToPush = new HashMap<String, String>();
			mapToPush.put(((DomainSpecificVariable) lessThan.getLhs())
					.getFieldName(), operator);
			parameterForColEvaluation.push(mapToPush);
		} else {
			visit(lessThan, false);
		}
	}

	@SuppressWarnings("unchecked")
	private void visit(BinaryPredicate lessThanOrGreaterThan,
			boolean isGreaterThan) {

		String ognlExpression = getOgnlExpressionForValue(lessThanOrGreaterThan
				.getLhs(), true, false);

		if (isGreaterThan) {
			queryString.append(ognlExpression);
		} else {
			lessThanOrGreaterThan.getLhs().accept(this);
		}

		queryString.append(" - ");

		if (isGreaterThan) {
			lessThanOrGreaterThan.getLhs().accept(this);
		} else {
			queryString.append(ognlExpression);
		}

		queryString.append(" >= ");

		lessThanOrGreaterThan.getRhs().accept(this);
	}

	protected String getOgnlExpressionForValue(Visitable visitable,
			boolean escape, boolean isSameAs) {
		DomainSpecificVariable dsv = (DomainSpecificVariable) visitable;
		boolean isStringType = STRING.equals(dsv.getType());
		boolean isDateType = DATE.equals(dsv.getType());
		boolean isFromCollection = !joinedEntityAlias.isEmpty();

		OGNLExpressionGenerator ognlGenerator = new OGNLExpressionGenerator();
		dsv.accept(ognlGenerator);

		StringBuffer ognlExpression = new StringBuffer(100);
		
		//todo:to_date function is specific to oracle.
 	 	//A temp fix to make it work in Mysql is to create a user function called to_date.
		if (isDateType && isSameAs) {
			ognlExpression.append("to_date('");
		}

		if (escape) {
			if (isStringType) {
				ognlExpression.append("'");
			}
			ognlExpression.append("${");
		}

		ognlExpression.append(ognlGenerator.getExpressionString());

		if (!dsv.isSimpleVariable()) {
			ognlExpression.append(".id");
		}

		if (escape) {
			ognlExpression.append("}$");
			if (isStringType) {
				ognlExpression.append("'");
			}
		}

		if (isDateType && isSameAs) {
			ognlExpression.append("'");
		}
		
		if (isDateType && isSameAs) {
			ognlExpression.append(",").append(DATE_FORMAT).append(")");
		}

		return ognlExpression.toString();
	}

	@Override
	public void visit(ForAnyOf visitable) {
		DomainSpecificVariable collectionValuedVariable = visitable
				.getCollectionValuedVariable();
		String collectionVariable = collectionValuedVariable.getToken();
		joinedEntityAlias.push(collectionVariable);
		visitable.getConditionToBeSatisfied().accept(this);
		int index = collectionVariable.lastIndexOf(".");
		String colAlias = collectionVariable.substring(index + 1);
		String newColAlias = colAlias + this.hashCode() + randomNumber;
		String oldAlias = businessObjectModel.getTopLevelAlias() + this.hashCode() + randomNumber + 1;
		String newAlias = oldAlias + this.hashCode() + randomNumber;
		String newJoinedEntityName = collectionVariable.replace(businessObjectModel.getTopLevelAlias() + ".",
				newAlias + ".");
		String topLevelTypeName = businessObjectModel.getTopLevelTypeName();

		queryString.append(" claim.id in(");
		queryString.append(" select distinct ").append(newAlias).append(
				".id from ");
		queryString.append(topLevelTypeName).append(" ").append(newAlias);
		queryString.append(" join ").append(newJoinedEntityName).append(" ")
				.append(newColAlias);
		queryString.append(", ").append(topLevelTypeName).append(" ").append(
				oldAlias).append(" join ");
		queryString.append(collectionVariable.replace(businessObjectModel.getTopLevelAlias() + ".",	oldAlias + ".")).append(" ").append(colAlias);
		queryString.append(" where ");

		queryString.append(prepareQueryForClaimDuplicacyCol(colAlias,
				newColAlias));
		queryString.append(" and ").append(oldAlias).append(".id <> ");
		queryString.append(newAlias).append(".id and ").append(oldAlias)
				.append(".id = ${");
		queryString.append(businessObjectModel.getTopLevelAlias()).append(".id}$))");
		joinedEntityAlias.pop();
	}

	@Override
	public void visit(ForEachOf visitable) {
		DomainSpecificVariable collectionValuedVariable = visitable
				.getCollectionValuedVariable();
		String collectionVariable = collectionValuedVariable.getToken();
		joinedEntityAlias.push(collectionVariable);
		visitable.getConditionToBeSatisfied().accept(this);
		int index = collectionVariable.lastIndexOf(".");
		String colAlias = collectionVariable.substring(index + 1);
		String newColAlias = colAlias + this.hashCode() + randomNumber;
		String oldAlias = businessObjectModel.getTopLevelAlias() + this.hashCode() + randomNumber + 1;
		String newAlias = oldAlias + this.hashCode() + randomNumber;
		String newJoinedEntityName = collectionVariable.replace(businessObjectModel.getTopLevelAlias() + ".",
				newAlias + ".");
		String innerAlias = newAlias + randomNumber;
		String topLevelTypeName = businessObjectModel.getTopLevelTypeName();
		queryString.append(" claim.id in(");
		queryString.append(" select distinct ").append(innerAlias).append(
				".id from ");
		queryString.append(topLevelTypeName).append(" ").append(innerAlias);
		queryString.append(" where (select count(distinct ");
		queryString.append(colAlias).append(") from ").append(topLevelTypeName)
				.append(" ");
		queryString.append(oldAlias).append(" join ");
		queryString.append(collectionVariable.replace(businessObjectModel.getTopLevelAlias() + ".",	oldAlias + ".")).append(" ").append(colAlias)
				.append(" where ").append(oldAlias).append(".id =");
		queryString.append("${").append(businessObjectModel.getTopLevelAlias()).append(".id}$) <= ");
		queryString.append("( select count(distinct ");
		queryString.append(newColAlias).append(") from ").append(
				topLevelTypeName).append(" ").append(oldAlias).append(" join ");
		queryString.append(collectionVariable.replace(businessObjectModel.getTopLevelAlias() + ".",	oldAlias + ".")).append(" ").append(colAlias);
		queryString.append(", ").append(topLevelTypeName).append(" ").append(
				newAlias);
		queryString.append(" join ").append(newJoinedEntityName).append(" ")
				.append(newColAlias).append(" where ");

		queryString.append(prepareQueryForClaimDuplicacyCol(colAlias,
				newColAlias));
		queryString.append(" and ").append(oldAlias).append(".id <> ");
		queryString.append(newAlias).append(".id and ");
		queryString.append(innerAlias).append(".id = ").append(newAlias)
				.append(".id and ");
		queryString.append(oldAlias).append(".id = ${");
		queryString.append(businessObjectModel.getTopLevelAlias()).append(".id}$))");
		joinedEntityAlias.pop();
	}

	private String getDuplicateClaimedItemsExpr() {
		return new StringBuffer(100).append("${").append(
				"getDuplicateClaimedItems").append("(").append(
				"claim.claimedItems").append(")}$").toString();
	}

	private Map<String, String> getManyDSVCollectionMap() {
		Map<String, String> manyDSVCollectionMap = new HashMap<String, String>();
		manyDSVCollectionMap.put("claim.claimedItems",
				getDuplicateClaimedItemsExpr());
		return manyDSVCollectionMap;
	}

	private String prepareQueryForClaimDuplicacyCol(String colAlias,
			String newColAlias) {
		StringBuffer tempQueryString = new StringBuffer(100);
		while (parameterForColEvaluation != null
				&& !parameterForColEvaluation.isEmpty()) {
			HashMap<String, String> paramColEvaluationMap = parameterForColEvaluation
					.pop();
			String keyFromMap = null;
			String valueFromMap = null;
			for (Map.Entry<String, String> entry : paramColEvaluationMap
					.entrySet()) {
				keyFromMap = entry.getKey();
				valueFromMap = entry.getValue();
			}
			tempQueryString.append(" ( ");
			if(!keyFromMap.endsWith(".id") && valueFromMap.trim().equals("=")) 
				tempQueryString.append(" ( ").append(colAlias).append(".").append(keyFromMap)
					.append(" IS NULL and ").append(newColAlias).append(".").append(keyFromMap)
					.append(" IS NULL ) or ");
			tempQueryString.append(colAlias).append(".").append(keyFromMap)
					.append(" ");
			tempQueryString.append(valueFromMap).append(" ");
			tempQueryString.append(newColAlias).append(".").append(keyFromMap);
			tempQueryString.append(" ) and ");
		}

        tempQueryString.append("1=1"); //so that the query wont break because of the trailing "and" expression
                                       // -or- even because of there being no parameters present.

        return tempQueryString.toString();
	}
}