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

package tavant.twms.domain.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.util.Assert;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
import tavant.twms.domain.common.CalendarIterator;
import tavant.twms.domain.common.CustomCalendarInterval;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.rules.AbstractCollectionUnaryPredicate;
import tavant.twms.domain.rules.All;
import tavant.twms.domain.rules.And;
import tavant.twms.domain.rules.Any;
import tavant.twms.domain.rules.Between;
import tavant.twms.domain.rules.BinaryPredicate;
import tavant.twms.domain.rules.Composite;
import tavant.twms.domain.rules.Constant;
import tavant.twms.domain.rules.Constants;
import tavant.twms.domain.rules.Contains;
import tavant.twms.domain.rules.DateGreaterBy;
import tavant.twms.domain.rules.DateLesserBy;
import tavant.twms.domain.rules.DateNotGreaterBy;
import tavant.twms.domain.rules.DateNotLesserBy;
import tavant.twms.domain.rules.DateType;
import tavant.twms.domain.rules.DoesNotContain;
import tavant.twms.domain.rules.DoesNotEndWith;
import tavant.twms.domain.rules.DoesNotStartWith;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainSpecificVariable;
import tavant.twms.domain.rules.EmptyVisitor;
import tavant.twms.domain.rules.EndsWith;
import tavant.twms.domain.rules.Equals;
import tavant.twms.domain.rules.ExpressionToken;
import tavant.twms.domain.rules.Field;
import tavant.twms.domain.rules.FieldTraversal;
import tavant.twms.domain.rules.ForAnyOf;
import tavant.twms.domain.rules.ForEachOf;
import tavant.twms.domain.rules.GreaterThan;
import tavant.twms.domain.rules.GreaterThanOrEquals;
import tavant.twms.domain.rules.EqualsEnum;
import tavant.twms.domain.rules.IsAfter;
import tavant.twms.domain.rules.IsBefore;
import tavant.twms.domain.rules.IsDuringLast;
import tavant.twms.domain.rules.IsDuringNext;
import tavant.twms.domain.rules.IsFalse;
import tavant.twms.domain.rules.IsNoneOf;
import tavant.twms.domain.rules.IsNotDuringLast;
import tavant.twms.domain.rules.IsNotDuringNext;
import tavant.twms.domain.rules.IsNotSet;
import tavant.twms.domain.rules.IsNotWithinLast;
import tavant.twms.domain.rules.IsNotWithinNext;
import tavant.twms.domain.rules.IsOnOrAfter;
import tavant.twms.domain.rules.IsOnOrBefore;
import tavant.twms.domain.rules.IsOneOf;
import tavant.twms.domain.rules.IsSet;
import tavant.twms.domain.rules.IsTrue;
import tavant.twms.domain.rules.IsWithinLast;
import tavant.twms.domain.rules.IsWithinNext;
import tavant.twms.domain.rules.LessThan;
import tavant.twms.domain.rules.LessThanOrEquals;
import tavant.twms.domain.rules.MethodInvocation;
import tavant.twms.domain.rules.Not;
import tavant.twms.domain.rules.NotBetween;
import tavant.twms.domain.rules.NotEquals;
import tavant.twms.domain.rules.NotEqualsEnum;
import tavant.twms.domain.rules.Or;
import tavant.twms.domain.rules.Predicate;
import tavant.twms.domain.rules.StartsWith;
import tavant.twms.domain.rules.Type;
import tavant.twms.domain.rules.UnaryPredicate;
import tavant.twms.domain.rules.Value;
import tavant.twms.domain.rules.Visitable;
import static tavant.twms.domain.rules.Type.STRING;
import tavant.twms.infra.TypedQueryParameter;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.CalendarInterval;
import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

/**
 * @author roopali.agrawal
 *
 */
public class HibernateQueryGenerator extends EmptyVisitor {
    private static final Logger logger = Logger.getLogger(HibernateQueryGenerator.class);

    

    Map<String, String> entityJoins = new HashMap<String, String>();

    Stack joinedEntitiyAlias = new Stack();

    StringBuffer queryString = new StringBuffer();

    String businessObjectContext;

    List<TypedQueryParameter> indexedQueryParameters = new ArrayList<TypedQueryParameter>();

    IBusinessObjectModel businessObjectModel;

    private int randomNumber = 0;

    public HibernateQueryGenerator(String context) {
        Assert.notNull(context, "cannot be null");
        this.businessObjectContext = context;
        this.businessObjectModel = BusinessObjectModelFactory.getInstance().getBusinessObjectModel(
                this.businessObjectContext);
    }

    public HibernateQuery getHibernateQuery() {
        return getHibernateQuery(false);
    }

    protected HibernateQuery getHibernateQuery(boolean isSubQuery) {
        HibernateQuery query = new HibernateQuery();
        IBusinessObjectModel model = BusinessObjectModelFactory.getInstance()
                .getBusinessObjectModel(this.businessObjectContext);
        String alias = model.getTopLevelAlias();
        StringBuffer selectClause = new StringBuffer("select ");
        selectClause.append(alias);

        query.setSelectClause(selectClause.toString());
        query.setQueryWithoutSelect(getParameterizedQueryString(isSubQuery));
        // todo
        query.setOrderByClause(null);
        query.setParameters(this.indexedQueryParameters);
        if(logger.isInfoEnabled())
        {
            logger.info("HQL query is " + query);
        }
        return query;
    }

    /*
     * private boolean hasHavingClause(){ return (havingClauseGenerator!=null &&
     * !"".equals(havingClauseGenerator.queryString.toString())); }
     */

    private String getParameterizedQueryString(boolean isSubquery) {
        StringBuffer finalQuery = new StringBuffer();
        IBusinessObjectModel model = BusinessObjectModelFactory.getInstance()
                .getBusinessObjectModel(this.businessObjectContext);
        String alias = model.getTopLevelAlias();

        finalQuery.append(" from ");
        // if(!isSubquery)
        finalQuery.append(model.getTopLevelTypeName());
        finalQuery.append(" ");
        finalQuery.append(alias);
        /*
         * if(hasHavingClause()) { finalQuery.append(" where "+alias+".id in (
         * select "+alias+".id from "+model.getTopLevelTypeName()+" "+alias);
         * entityJoins.putAll(havingClauseGenerator.entityJoins); }
         */for (Map.Entry<String, String> join : this.entityJoins.entrySet()) {
            finalQuery.append(" join ");
            finalQuery.append(join.getKey());
            finalQuery.append(" ");
            finalQuery.append(join.getValue());
        }

        if (!("".equals(this.queryString.toString()))) {
            finalQuery.append(" where ( ");
            finalQuery.append(this.queryString.toString());
            finalQuery.append(" ) ");
        }
        // Dealer should be able to do search only his claims.

        if (!isSubquery) {
            User loggedinUser = new SecurityHelper().getLoggedInUser();
            if (loggedinUser != null && loggedinUser.hasOnlyRole("dealer")
                    && loggedinUser.getCurrentlyActiveOrganization()!=null) {
                if (BusinessObjectModelFactory.CLAIM_SEARCHES.equals(this.businessObjectContext)) {
                    finalQuery.append(" and ").append(alias).append(".forDealer.id = ? ");
                } else if (BusinessObjectModelFactory.INVENTORY_SEARCHES
                        .equals(this.businessObjectContext)) {
                    String inventoryOwnerQuery = "from InventoryItem inventoryItem1 join inventoryItem1." +
                            "transactionHistory th where 1=CASE WHEN inventoryItem1.type.type='STOCK' THEN CASE WHEN " +
                            "(th.buyer.id=? and th.transactionDate=(select max(it.transactionDate) from " +
                            "InventoryTransaction it where it.transactedItem=inventoryItem1 group by " +
                            "it.transactedItem)) THEN 1 ELSE 0 END WHEN inventoryItem1.type.type !='STOCK' THEN 1 " +
                            "ELSE 0 END";
                    finalQuery.append(" and ").append(alias).append(".id in (select  inventoryItem1.id ")
                            .append(inventoryOwnerQuery).append(")");
                } else if (BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES.equals(this.businessObjectContext)){
                    finalQuery.append(" and ").append(alias).append(".supplier in (?) ");
                }

                this.indexedQueryParameters.add(new TypedQueryParameter(
                        loggedinUser.getCurrentlyActiveOrganization(), Hibernate.entity(Organization.class)));
            }
        }

        /*
         * if(hasHavingClause()){ finalQuery.append(" GROUP BY "+alias+".id
         * HAVING ");
         * finalQuery.append(havingClauseGenerator.queryString.toString());
         * finalQuery.append(" )"); }
         */
        return finalQuery.toString();
    }

    @Override
    public void visit(All visitable) {
        List<Predicate> aggregateFunctions = new ArrayList<Predicate>();

        List<Predicate> simpleFunctions = new ArrayList<Predicate>();

        filterFunctionPredicates(visitable.getPredicates(), simpleFunctions, aggregateFunctions);

        All allPredicates = new All(visitable.getPredicates());
        visitAll(allPredicates);

        visitSimpleFunctions(simpleFunctions, false);

        visitAggregateFunctions(aggregateFunctions, false);
    }

    private String getIdString() {
        return this.businessObjectModel.getTopLevelAlias() + ".id";
    }

    void visitAll(All visitable) {
        List<Predicate> predicates = visitable.getPredicates();
        for (Iterator<Predicate> iter = predicates.iterator(); iter.hasNext();) {
            iter.next().accept(this);
            if (iter.hasNext()) {
                this.queryString.append(" and ");
            }
        }
    }

    @Override
    public void visit(Any visitable) {
        List<Predicate> aggregateFunctions = new ArrayList<Predicate>();

        List<Predicate> simpleFunctions = new ArrayList<Predicate>();

        filterFunctionPredicates(visitable.getPredicates(), simpleFunctions, aggregateFunctions);

        Any allPredicates = new Any(visitable.getPredicates());
        visitAny(allPredicates);

        visitSimpleFunctions(simpleFunctions, true);

        visitAggregateFunctions(aggregateFunctions, true);

    }

    private void visitAggregateFunctions(List<Predicate> aggregateFunctions, boolean forAny) {
        String joinCondition = forAny ? " or " : " and ";
        if (!aggregateFunctions.isEmpty()) {
            int index = 0;
            for (Predicate p : aggregateFunctions) {
                if (index == 0) {
                    if (this.queryString != null && !this.queryString.toString().equals("")) {
                        this.queryString.append(joinCondition);
                    }

                } else {
                    this.queryString.append(joinCondition);
                }
                index++;
                // All aggregatePredicates = new All(aggregateFunctions);
                HibernateGroupByQueryGenerator anotherGenerator = new HibernateGroupByQueryGenerator(
                        this.businessObjectContext);
                p.accept(anotherGenerator);
                String subquery = prepareSubqueryString(anotherGenerator);

                this.queryString.append(getIdString() + " in (" + subquery + ")");
                this.indexedQueryParameters.addAll(anotherGenerator.indexedQueryParameters);
            }
        }
    }

    // todo-a temporary fix to have separate aliases for subquery.
    // Generating appropriate aliases for each query is a bigger change and will
    // need modification to
    // BusinessObjectModel,the way field expressions are created.
    private String prepareSubqueryString(HibernateQueryGenerator anotherGenerator) {
        HibernateQuery hQuery = anotherGenerator.getHibernateQuery(true);
        String subquery = hQuery.getSelectClause() + hQuery.getQueryWithoutSelect();
        String oldAlias = this.businessObjectModel.getTopLevelAlias();
        String newAlias = oldAlias + this.hashCode();
        // replace table name alias
        subquery = subquery.replace(" " + oldAlias + " ", " " + newAlias + " ");
        // replace alias in field name expressions
        subquery = subquery.replace(oldAlias + ".", newAlias + ".");
        return subquery;
    }

    private void visitSimpleFunctions(List<Predicate> simpleFunctions, boolean forAny) {
        String joinCondition = forAny ? " or " : " and ";
        if (!simpleFunctions.isEmpty()) {
            int index = 0;
            for (Predicate p : simpleFunctions) {
                if (index == 0) {
                    if (this.queryString != null && !this.queryString.toString().equals("")) {
                        this.queryString.append(joinCondition);
                    }

                } else {
                    this.queryString.append(joinCondition);
                }
                index++;
                // All aggregatePredicates = new All(aggregateFunctions);
                HibernateQueryGenerator anotherGenerator = new HibernateQueryGenerator(
                        this.businessObjectContext);
                p.accept(anotherGenerator);
                String subquery = prepareSubqueryString(anotherGenerator);
                this.queryString.append(getIdString() + " in (" + subquery + ")");
                this.indexedQueryParameters.addAll(anotherGenerator.indexedQueryParameters);
            }
        }
    }

    void visitAny(Any visitable) {
        List<Predicate> predicates = visitable.getPredicates();
        for (Iterator<Predicate> iter = predicates.iterator(); iter.hasNext();) {
            iter.next().accept(this);
            if (iter.hasNext()) {
                this.queryString.append(" OR ");
            }
        }
    }

    private Visitable getLHSVisitable(Predicate p) {
        Visitable lhs = null;
        if (p instanceof BinaryPredicate) {
            BinaryPredicate bp = (BinaryPredicate) p;
            lhs = bp.getLhs();
        } else if (p instanceof Between) {
            Between bp = (Between) p;
            lhs = bp.getLhs();
        } else if (p instanceof NotBetween) {
            NotBetween bp = (NotBetween) p;
            lhs = bp.getLhs();
        }
        return lhs;
    }

    private void filterFunctionPredicates(List<Predicate> predicates,
            List<Predicate> functionPredicates, List<Predicate> aggregateFunctionPredicates) {
        // List<Predicate> functionPredicates = new ArrayList<Predicate>();
        for (Predicate p : predicates) {
            Visitable lhs = getLHSVisitable(p);
            if (lhs != null) {
                // BinaryPredicate bp = (BinaryPredicate) p;
                // Visitable lhs = bp.getLhs();
                if (lhs instanceof DomainSpecificVariable) {
                    DomainSpecificVariable dsv = (DomainSpecificVariable) lhs;
                    if (dsv.field().targetField() instanceof QueryTemplate) {
                        QueryTemplate ff = (QueryTemplate) dsv.field().targetField();
                        if (ff.getGroupBy() != null) {
                            // aggregate functions
                            aggregateFunctionPredicates.add(p);
                        } else {
                            functionPredicates.add(p);
                        }
                    }
                }
            }
        }
        for (Predicate p : functionPredicates) {
            predicates.remove(p);
        }
        for (Predicate p : aggregateFunctionPredicates) {
            predicates.remove(p);
        }
    }

    @Override
    public void visit(Constant constant) {
        if (Type.STRING.equals(constant.getType())) {
            this.queryString.append("?");
            String value = constant.getLiteral();
            this.indexedQueryParameters.add(new TypedQueryParameter(value.toUpperCase(),
                    Hibernate.STRING));
        } else if (Type.INTEGER.equals(constant.getType())) {
            this.queryString.append("?");
            this.indexedQueryParameters.add(new TypedQueryParameter(Integer.parseInt(constant
                    .getLiteral()), Hibernate.INTEGER));
        } else if (Type.LONG.equals(constant.getType())) {
            this.queryString.append("?");
            this.indexedQueryParameters.add(new TypedQueryParameter(Long.parseLong(constant
                    .getLiteral()), Hibernate.LONG));
        } else if (Type.BIGDECIMAL.equals(constant.getType())) {
            this.queryString.append("?");
            this.indexedQueryParameters.add(new TypedQueryParameter(new BigDecimal(constant
                    .getLiteral()), Hibernate.BIG_DECIMAL));
        } else if (Type.DATE.equals(constant.getType())) {
            this.queryString.append("?");
            CalendarDate cd = CalendarDate.from(constant.getLiteral().trim(),"M/d/yyyy" 
            		/*TWMSDateFormatUtil.getDateFormatForLoggedInUser()*/);

            Date date = new Date(cd.startAsTimePoint(TimeZone.getDefault()).asJavaUtilDate()
                    .getTime());
            // todo-need to verify it.
            this.indexedQueryParameters.add(new TypedQueryParameter(date, Hibernate.DATE));
        }/*
             * else if (Type.MONEY.equals(constant.getType())) {
             * queryString.append("?"); Money money=(Money)(new
             * MoneyType().getJavaObject(constant.getLiteral()));
             * indexedQueryParameters.add(new
             * QueryParameter(money,Hibernate.custom(MoneyUserType.class))); }
             */else {
            this.queryString.append(constant.getToken());
        }

    }

    private void visitStringPatternMatchingOperation(BinaryPredicate binaryPredicate,
            boolean isNot, boolean matchStart, boolean matchEnd) {
        Visitable lhs = binaryPredicate.getLhs();
        lhs.accept(this);
        if (isNot) {
            this.queryString.append(" not");
        }

        this.queryString.append(" like ");

        Visitable rhs = binaryPredicate.getRhs();
        Constant cnst = (Constant) rhs;

        StringBuffer tmp = new StringBuffer();

        if (matchEnd) {
            tmp.append("%");
        }

        tmp.append(cnst.getLiteral());

        if (matchStart) {
            tmp.append("%");
        }

        cnst.setLiteral(tmp.toString());
        rhs.accept(this);

    }

    @Override
    public void visit(Contains contains) {
        visitStringPatternMatchingOperation(contains, false, true, true);
    }

    @Override
    public void visit(DoesNotContain doesNotContain) {
        visitStringPatternMatchingOperation(doesNotContain, true, true, true);
    }

    @Override
    public void visit(DomainPredicate visitable) {
        visitable.getPredicate().accept(this);

    }

    @Override
    public void visit(DomainSpecificVariable visitable) {
        Field targetField = visitable.field().targetField(); 
        boolean shouldConvertToUpperCase = shouldConvertToUpperCase(visitable);
        
        if(shouldConvertToUpperCase) {
            this.queryString.append("upper(");
        }
        
        if (targetField instanceof QueryTemplate) {
            QueryTemplate queryTemplate = (QueryTemplate) targetField;
            FieldTraversal field = visitable.field();
            String templateExpr = field.getExpression();

            String[] joins = queryTemplate.getJoinedEntityNames();
            if (joins != null) {
                for (int i = 0; i < joins.length; i++) {
                    String join = joins[i];
                    int index = join.lastIndexOf(".");
                    String alias = join.substring(index + 1);
                    this.entityJoins.put(join, alias);
                    templateExpr = templateExpr.replace(queryTemplate.getAliasNames()[i], alias);    
                }
            }

            this.queryString.append(templateExpr);

        } else {
            if (!this.joinedEntitiyAlias.empty()) {
                this.queryString.append(this.joinedEntitiyAlias.peek());
                this.queryString.append(".");
            }
            this.queryString.append(visitable.getToken());
        }

        if(shouldConvertToUpperCase) {
            this.queryString.append(")");
        }
    }
    
    private boolean shouldConvertToUpperCase(DomainSpecificVariable visitable) {
    	return (STRING.equals(visitable.getType()) && !visitable.isCaseSensitive())
                && !(visitable.field().targetField() instanceof QueryTemplate);
    }

    @Override
    public void visit(Equals visitable) {
        visitBinaryPredicate(visitable, "=");
    }

    /*
     * public void visit(ForAnyOf visitable) { DomainSpecificVariable
     * collectionValuedVariable = visitable .getCollectionValuedVariable();
     * String joinedEntityName = collectionValuedVariable.getToken();
     *
     * int index = joinedEntityName.lastIndexOf("."); String alias =
     * joinedEntityName.substring(index + 1);
     *
     * entityJoins.put(joinedEntityName, alias);
     *
     * joinedEntitiyAlias.push(alias);
     * visitable.getConditionToBeSatisfied().accept(this);
     * joinedEntitiyAlias.pop(); }
     */

    @Override
    public void visit(ForAnyOf visitable) {
        DomainSpecificVariable collectionValuedVariable = visitable.getCollectionValuedVariable();
        String joinedEntityName = collectionValuedVariable.getToken();

        int index = joinedEntityName.lastIndexOf(".");
        String alias = joinedEntityName.substring(index + 1);

        // entityJoins.put(joinedEntityName, alias);
        this.joinedEntitiyAlias.push(alias);
        this.queryString.append("(true = any (select case when ");
        visitable.getConditionToBeSatisfied().accept(this);
        this.randomNumber++;
        String oldClaimAlias = this.businessObjectModel.getTopLevelAlias();
        String newClaimAlias = oldClaimAlias + this.hashCode() + this.randomNumber;
        String newJoinedEntityName = joinedEntityName.replace(oldClaimAlias + ".", newClaimAlias
                + ".");

        this.queryString.append(" then true else false end from "
                + this.businessObjectModel.getTopLevelTypeName() + " " + newClaimAlias + " join "
                + newJoinedEntityName + " " + alias + " where " + oldClaimAlias + ".id = "
                + newClaimAlias + ".id " + ")) ");
        this.joinedEntitiyAlias.pop();
    }

    @Override
    public void visit(ForEachOf visitable) {
        DomainSpecificVariable collectionValuedVariable = visitable.getCollectionValuedVariable();
        String joinedEntityName = collectionValuedVariable.getToken();

        int index = joinedEntityName.lastIndexOf(".");
        String alias = joinedEntityName.substring(index + 1);

        // entityJoins.put(joinedEntityName, alias);
        this.joinedEntitiyAlias.push(alias);
        this.queryString.append("(true = all (select case when ");
        visitable.getConditionToBeSatisfied().accept(this);
        String oldClaimAlias = this.businessObjectModel.getTopLevelAlias();
        this.randomNumber++;
        String newClaimAlias = oldClaimAlias + this.hashCode() + this.randomNumber;
        String newJoinedEntityName = joinedEntityName.replace(oldClaimAlias + ".", newClaimAlias
                + ".");

        this.queryString.append(" then true else false end from "
                + this.businessObjectModel.getTopLevelTypeName() + " " + newClaimAlias + " join "
                + newJoinedEntityName + " " + alias + " where " + oldClaimAlias + ".id = "
                + newClaimAlias + ".id " + ")) ");
        this.joinedEntitiyAlias.pop();
    }

    @Override
    public void visit(GreaterThan visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(GreaterThanOrEquals visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(IsFalse isFalse) {
        isFalse.getOperand().accept(this);
        this.queryString.append(" = ?");
        this.indexedQueryParameters.add(new TypedQueryParameter(Boolean.FALSE, Hibernate.BOOLEAN));
    }

	// todo-check this
	@Override
	public void visit(IsNotSet isNotSet) {
		visitCheckNullPredicate(isNotSet," is  empty"," is  null");
	}

	@Override
	public void visit(IsSet isSet) {
		visitCheckNullPredicate(isSet," is  not empty"," is not null");
	}
	
	public void visitCheckNullPredicate(Predicate  visitable,String ifListondition,String ifFieldCondition){
		this.queryString.append("(");
		DomainSpecificVariable operand=null;
		if(visitable instanceof IsSet){
			 operand = ((IsSet) visitable).getOperand();
		}else {
			 operand = ((IsNotSet) visitable).getOperand();
		}
		Field targetField = operand.field().targetField();
		if (targetField instanceof QueryTemplate) {
			HibernateQueryGenerator anotherGenerator = new HibernateQueryGenerator(this.businessObjectContext);
			operand.accept(anotherGenerator);
			anotherGenerator.queryString
					.append(operand.field().endsInACollection() ? ifListondition : ifFieldCondition);
			String subquery = prepareSubqueryString(anotherGenerator);
			this.queryString.append(getIdString() + " in (" + subquery + ")");
			
		} else {
			operand.accept(this);
			this.queryString.append(operand.field().endsInACollection() ? ifListondition:ifFieldCondition);
		}
		this.queryString.append(")");
	}

    @Override
    public void visit(IsTrue isTrue) {
        isTrue.getOperand().accept(this);
        this.queryString.append(" = ?");
        this.indexedQueryParameters.add(new TypedQueryParameter(Boolean.TRUE, Hibernate.BOOLEAN));
    }

    @Override
    public void visit(LessThan visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(LessThanOrEquals visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(DoesNotStartWith visitable) {
        visitStringPatternMatchingOperation(visitable, true, true, false);
    }

    @Override
    public void visit(StartsWith startsWith) {
        visitStringPatternMatchingOperation(startsWith, false, true, false);
    }

    @Override
    public void visit(EndsWith endsWith) {
        visitStringPatternMatchingOperation(endsWith, false, false, true);
    }

    @Override
    public void visit(DoesNotEndWith doesNotEndWith) {
        visitStringPatternMatchingOperation(doesNotEndWith, true, false, true);
    }

    @Override
    public void visit(MethodInvocation visitable) {
        throw new UnsupportedOperationException(
                "Visit method is not supported for MethodInvocation type.");
    }

    @Override
    public void visit(Not visitable) {
        Predicate pred = visitable.getAPredicate();
        Predicate another = pred.getInverse();
        another.accept(this);
    }

    public void visitUnaryPredicate(UnaryPredicate visitable) {
        ExpressionToken ognlExpressionable = (ExpressionToken) visitable;
        this.queryString.append(ognlExpressionable.getToken());
        this.queryString.append("(");
        visitable.getOperand().accept(this);
        this.queryString.append(")");
    }

    public void visit(NotEquals visitable) {
        visitBinaryPredicate(visitable, "!=");
    }

    public void visit(Or visitable) {
        this.queryString.append("(");
        Predicate lhs = visitable.getLhs();
        lhs.accept(this);
        this.queryString.append(" OR ");
        Predicate rhs = visitable.getRhs();
        rhs.accept(this);
        this.queryString.append(")");
    }

    public void visit(And visitable) {
        this.queryString.append("(");
        Predicate lhs = visitable.getLhs();
        lhs.accept(this);
        this.queryString.append(" AND ");
        Predicate rhs = visitable.getRhs();
        rhs.accept(this);
        this.queryString.append(")");
    }

    public void visitBinaryPredicate(BinaryPredicate visitable) {
        visitBinaryPredicate(visitable, ((ExpressionToken) visitable).getToken());
    }

    private void visitBinaryPredicate(BinaryPredicate visitable, String token) {

        boolean isAComposite = visitable instanceof Composite;
        if (isAComposite) {
            this.queryString.append("(");
        }
        visitable.getLhs().accept(this);

        this.queryString.append(token);

        visitable.getRhs().accept(this);
        if (isAComposite) {
            this.queryString.append(")");
        }
    }

    public void visit(Between between) {
        Value lhs = between.getLhs();
        Value startingRhs = between.getStartingRhs();
        Value endingRhs = between.getEndingRhs();
        Boolean isInclusive = between.isInclusive();

        String operandType = lhs.getType();
        Predicate leftComparator = (isInclusive) ? new GreaterThanOrEquals(lhs, startingRhs)
                : new GreaterThan(lhs, startingRhs);
        Predicate rightComparator = (isInclusive) ? new LessThanOrEquals(lhs, endingRhs)
                : new LessThan(lhs, endingRhs);

        new And(leftComparator, rightComparator).accept(this);
    }

    public void visit(NotBetween notBetween) {
        Value lhs = notBetween.getLhs();
        Value startingRhs = notBetween.getStartingRhs();
        Value endingRhs = notBetween.getEndingRhs();
        Boolean isInclusive = notBetween.isInclusive();

        String operandType = lhs.getType();

        Value leftComparisonLhs;
        Value leftComparisonRhs;
        Value rightComparisonLhs;
        Value rightComparisonRhs;
        leftComparisonLhs = lhs;
        leftComparisonRhs = startingRhs;
        rightComparisonLhs = lhs;
        rightComparisonRhs = endingRhs;

        Predicate leftComparator = (isInclusive) ? new LessThanOrEquals(leftComparisonLhs,
                leftComparisonRhs) : new LessThan(leftComparisonLhs, leftComparisonRhs);
        Predicate rightComparator = (isInclusive) ? new GreaterThanOrEquals(rightComparisonLhs,
                rightComparisonRhs) : new GreaterThan(rightComparisonLhs, rightComparisonRhs);

        new Or(leftComparator, rightComparator).accept(this);
    }

    public void visit(IsWithinLast isWithinLast) {
        Duration duration = DateType.DurationType.getDurationForTypeAndLength(Integer
                .parseInt(isWithinLast.getRhs().getLiteral()), isWithinLast.getDurationType());
        TimePoint tp = Clock.now();
        TimePoint finalTp = tp.minus(duration);
        CalendarDate cd = CalendarDate.from(finalTp, TimeZone.getDefault());
        CalendarDate thisDate = CalendarDate.from(tp, TimeZone.getDefault());
        Constant finalDate = new Constant(cd.toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        Between between = new Between(isWithinLast.getLhs(), finalDate, new Constant(thisDate
                .toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date"));
        between.accept(this);
    }

    public void visit(IsNotWithinLast isWithinLast) {
        Duration duration = DateType.DurationType.getDurationForTypeAndLength(Integer
                .parseInt(isWithinLast.getRhs().getLiteral()), isWithinLast.getDurationType());
        TimePoint tp = Clock.now();
        TimePoint finalTp = tp.minus(duration);
        CalendarDate cd = CalendarDate.from(finalTp, TimeZone.getDefault());
        CalendarDate thisDate = CalendarDate.from(tp, TimeZone.getDefault());
        Constant finalDate = new Constant(cd.toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        NotBetween between = new NotBetween(isWithinLast.getLhs(), finalDate, new Constant(thisDate
                .toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date"));
        between.accept(this);
    }

    public void visit(IsWithinNext isWithinNext) {
        Duration duration = DateType.DurationType.getDurationForTypeAndLength(Integer
                .parseInt(isWithinNext.getRhs().getLiteral()), isWithinNext.getDurationType());
        TimePoint tp = Clock.now();
        TimePoint finalTp = tp.plus(duration);
        CalendarDate cd = CalendarDate.from(finalTp, TimeZone.getDefault());
        CalendarDate thisDate = CalendarDate.from(tp, TimeZone.getDefault());
        Constant finalDate = new Constant(cd.toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        Between between = new Between(isWithinNext.getLhs(), new Constant(thisDate
                .toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date"), finalDate);
        between.accept(this);
    }

    public void visit(IsNotWithinNext isWithinNext) {
        Duration duration = DateType.DurationType.getDurationForTypeAndLength(Integer
                .parseInt(isWithinNext.getRhs().getLiteral()), isWithinNext.getDurationType());
        TimePoint tp = Clock.now();
        TimePoint finalTp = tp.plus(duration);
        CalendarDate cd = CalendarDate.from(finalTp, TimeZone.getDefault());
        CalendarDate thisDate = CalendarDate.from(tp, TimeZone.getDefault());
        Constant finalDate = new Constant(cd.toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        NotBetween between = new NotBetween(isWithinNext.getLhs(), new Constant(thisDate
                .toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date"), finalDate);
        between.accept(this);
    }

    public void visit(IsDuringLast isDuringLast) {
        CalendarInterval interval = CustomCalendarInterval.getCalendarInterval(Integer
                .parseInt(isDuringLast.getRhs().getLiteral()), isDuringLast.getDurationType(),
                CalendarIterator.LAST);
        checkBetweenDate(isDuringLast.getLhs(), interval);
    }

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.domain.rules.EmptyVisitor#visit(tavant.twms.domain.rules.IsDuringNext)
     */
    public void visit(IsDuringNext isDuringNext) {
        CalendarInterval interval = CustomCalendarInterval.getCalendarInterval(Integer
                .parseInt(isDuringNext.getRhs().getLiteral()), isDuringNext.getDurationType(),
                CalendarIterator.NEXT);
        checkBetweenDate(isDuringNext.getLhs(), interval);
    }

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.domain.rules.EmptyVisitor#visit(tavant.twms.domain.rules.IsNotDuringLast)
     */
    public void visit(IsNotDuringLast isNotDuringLast) {
        CalendarInterval interval = CustomCalendarInterval.getCalendarInterval(Integer
                .parseInt(isNotDuringLast.getRhs().getLiteral()),
                isNotDuringLast.getDurationType(), CalendarIterator.LAST);
        checkNotBetweenDate(isNotDuringLast.getLhs(), interval);
    }

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.domain.rules.EmptyVisitor#visit(tavant.twms.domain.rules.IsNotDuringNext)
     */
    public void visit(IsNotDuringNext isNotDuringNext) {
        CalendarInterval interval = CustomCalendarInterval.getCalendarInterval(Integer
                .parseInt(isNotDuringNext.getRhs().getLiteral()),
                isNotDuringNext.getDurationType(), CalendarIterator.NEXT);
        checkNotBetweenDate(isNotDuringNext.getLhs(), interval);
    }

    private void checkBetweenDate(Value lhs, CalendarInterval interval) {
        Constant startDate = new Constant(interval.start().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        Constant endDate = new Constant(interval.end().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        Between between = new Between(lhs, startDate, endDate);
        between.accept(this);
    }

    private void checkNotBetweenDate(Value lhs, CalendarInterval interval) {
        Constant startDate = new Constant(interval.start().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        Constant endDate = new Constant(interval.end().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()), "date");
        NotBetween between = new NotBetween(lhs, startDate, endDate);
        between.accept(this);
    }

    @Override
    public void visit(IsAfter isAfter) {
        GreaterThan gt = new GreaterThan(isAfter.getLhs(), isAfter.getRhs());
        gt.accept(this);
    }

    @Override
    public void visit(IsBefore isBefore) {
        LessThan gt = new LessThan(isBefore.getLhs(), isBefore.getRhs());
        gt.accept(this);
    }

    @Override
    public void visit(IsOnOrAfter isOnOrAfter) {
        GreaterThanOrEquals gt = new GreaterThanOrEquals(isOnOrAfter.getLhs(), isOnOrAfter.getRhs());
        gt.accept(this);
    }

    @Override
    public void visit(IsOnOrBefore isOnOrBefore) {
        LessThanOrEquals gt = new LessThanOrEquals(isOnOrBefore.getLhs(), isOnOrBefore.getRhs());
        gt.accept(this);
    }

    @Override
    public void visit(IsNoneOf visitable) {

        this.queryString.append("(");
        visitable.getLhs().accept(this);

        this.queryString.append(" not in (");

        visitable.getRhs().accept(this);

        this.queryString.append("))");

    }

    @Override
    public void visit(IsOneOf visitable) {
        this.queryString.append("(");
        visitable.getLhs().accept(this);

        this.queryString.append(" in (");

        visitable.getRhs().accept(this);

        this.queryString.append("))");
    }

    @Override
    public void visit(EqualsEnum visitable) {
        this.queryString.append("( upper(");
        visitable.getLhs().accept(this);
        this.queryString.append(")").append(visitable.getToken()).append(" (");
        visitable.getRhs().accept(this);
        this.queryString.append(") )");
    }
    
	public void visit(NotEqualsEnum visitable) {
		this.queryString.append("( upper(");
        visitable.getLhs().accept(this);
        this.queryString.append(")").append(visitable.getToken()).append(" (");
        visitable.getRhs().accept(this);
        this.queryString.append(") )");
	}
    
    @Override
    public void visit(Constants constants) {
        List<String> literals = constants.getLiterals();
        int counter = 0;
        for (String literal : literals) {
            if (counter++ != 0) {
                this.queryString.append(",");
            }
            new Constant(literal, constants.getType()).accept(this);
        }
    }
    
    @Override
	public void visit(DateLesserBy dateLesserBy) {
		dateLesserBy.getDateToCompare().accept(this);
		this.queryString.append("-");
		dateLesserBy.getLhs().accept(this);
		this.queryString.append("<=");
		dateLesserBy.getRhs().accept(this);
	}
    
    @Override
    public void visit(DateNotLesserBy dateNotLesserBy) {
		dateNotLesserBy.getDateToCompare().accept(this);
		this.queryString.append("-");
		dateNotLesserBy.getLhs().accept(this);
		this.queryString.append(">=");
		dateNotLesserBy.getRhs().accept(this);
	}
    
    @Override
    public void visit(DateGreaterBy dateGreaterBy) {
    	dateGreaterBy.getLhs().accept(this);
		this.queryString.append("-");
		dateGreaterBy.getDateToCompare().accept(this);
		this.queryString.append(">=");
		dateGreaterBy.getRhs().accept(this);
	}
    
    @Override
    public void visit(DateNotGreaterBy dateNotGreaterBy) {
    	dateNotGreaterBy.getLhs().accept(this);
		this.queryString.append("-");
		dateNotGreaterBy.getDateToCompare().accept(this);
		this.queryString.append("<=");
		dateNotGreaterBy.getRhs().accept(this);
	}
}
