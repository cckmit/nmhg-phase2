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

import static tavant.twms.domain.rules.Type.STRING;
import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_DUPLICACY_RULES;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author radhakrishnan.j
 */
public class OGNLExpressionGenerator implements Visitor {

	private final StringBuffer buf = new StringBuffer();

	public OGNLExpressionGenerator() {
		super();
	}

	protected void useLowerCaseIfStringType(Value value) {
		if (STRING.equals(value.getType())) {
			useLowerCase();
		}
	}

	protected void useLowerCase() {
		buf.append(".toLowerCase()");
	}

	protected void visitBinaryPredicateMethod(BinaryPredicate binaryPredicate,
			String methodName, String returnType) {
		visitBinaryPredicateMethod(binaryPredicate, methodName, returnType,
				false);
	}

	protected void visitBinaryPredicateMethod(BinaryPredicate binaryPredicate,
			String methodName, String returnType, boolean negateResult) {
		if (negateResult) {
			buf.append("!(");
		}

		this.visit(new BinaryPredicateAwareMethodInvocation(binaryPredicate,
				methodName, returnType));

		if (negateResult) {
			buf.append(")");
		}
	}

	protected void visit(MethodInvocationTarget visitable) {
		buf.append(visitable.getBeanName());
	}

	public void visit(And visitable) {
		buf.append("(");
		Predicate lhs = visitable.getLhs();
		lhs.accept(this);
		buf.append(" && ");
		Predicate rhs = visitable.getRhs();
		rhs.accept(this);
		buf.append(")");
	}

	public void visit(Constant constant) {
		buf.append(constant.getToken());

		useLowerCaseIfStringType(constant);
	}

	public void visit(Constants constants) {
		String token = constants.getToken();

		if (STRING.equals(constants.getType())
				&& !constants.getLiterals().isEmpty()) {

			int i = 0;

			for (String eachLiteral : constants.getLiterals()) {

				if (i == 0) {
					i++;
				} else {
					buf.append(",");
				}

				buf.append("\"");
				buf.append(eachLiteral);				
				buf.append("\"");
				useLowerCase();
			}
			
		} else {
			buf.append(token);
		}
	}

	public void visit(DomainSpecificVariable domainVariable) {
		buf.append(domainVariable.getToken());
		useLowerCaseIfStringType(domainVariable);
	}

	public void visit(Equals equals) {
		final TypeSystem typeSystem = TypeSystem.getInstance();
		Value lhs = equals.getLhs();

		boolean primitive = typeSystem.isPrimitive(lhs.getType());
		if (primitive) {
			visitBinaryPredicate(equals);
		} else {
			visitBinaryPredicateMethod(equals, "equals", Type.BOOLEAN);
		}
	}

	public void visit(GreaterThan visitable) {
		Value lhs = visitable.getLhs();
		boolean primitive = TypeSystem.getInstance().isPrimitive(lhs.getType());

		if (primitive) {
			visitBinaryPredicate(visitable);
		} else {
			Value rhs = visitable.getRhs();

                        boolean handledInterCurrencyComparison =
                                handleInterCurrencyComparison(lhs, rhs);

			visitBinaryPredicate(new Equals(new CompareTo(lhs, rhs),
					new Constant("1", Type.INTEGER)));

                        if(handledInterCurrencyComparison) {
                            buf.append(")");
                        }
		}
	}

	public void visit(GreaterThanOrEquals visitable) {
		Value lhs = visitable.getLhs();
		boolean primitive = TypeSystem.getInstance().isPrimitive(lhs.getType());

		if (primitive) {
			visitBinaryPredicate(visitable);
		} else {
			Value rhs = visitable.getRhs();

                        boolean handledInterCurrencyComparison =
                            handleInterCurrencyComparison(lhs, rhs);

			visitBinaryPredicate(new GreaterThanOrEquals(
					new CompareTo(lhs, rhs), new Constant("0", Type.INTEGER)));

                        if (handledInterCurrencyComparison) {
                        buf.append(")");
                        }
		}
	}

	public void visit(IsNoneOf visitable) {
		visitable.getLhs().accept(this);
		buf.append(visitable.getToken());
		buf.append("{");
		visitable.getRhs().accept(this);		
		buf.append("}");
	}

	public void visit(IsOnOrBefore isOnOrBefore) {
		IsAfter isAfter = new IsAfter(isOnOrBefore.getLhs(), isOnOrBefore
				.getRhs());
		Not isNotAfter = new Not(isAfter);
		this.visit(isNotAfter);
	}

	public void visit(IsOneOf visitable) {					
		visitable.getLhs().accept(this);
		buf.append(visitable.getToken());
		visitable.getRhs().accept(this);		
		buf.append("}");			
	}

	public void visit(LessThan visitable) {
		Value lhs = visitable.getLhs();
		boolean primitive = TypeSystem.getInstance().isPrimitive(lhs.getType());

		if (primitive) {
			visitBinaryPredicate(visitable);
		} else {
			Value rhs = visitable.getRhs();

                        boolean handledInterCurrencyComparison =
                            handleInterCurrencyComparison(lhs, rhs);

			visitBinaryPredicate(new Equals(new CompareTo(lhs, rhs),
					new Constant("-1", Type.INTEGER)));

                        if (handledInterCurrencyComparison) {
                        buf.append(")");
                    }
		}
	}

	public void visit(LessThanOrEquals visitable) {
		Value lhs = visitable.getLhs();
		boolean primitive = TypeSystem.getInstance().isPrimitive(lhs.getType());

		if (primitive) {
			visitBinaryPredicate(visitable);
		} else {
			Value rhs = visitable.getRhs();

                        boolean handledInterCurrencyComparison =
                            handleInterCurrencyComparison(lhs, rhs);

			visitBinaryPredicate(new LessThanOrEquals(new CompareTo(lhs, rhs),
					new Constant("0", Type.INTEGER)));

                    if (handledInterCurrencyComparison) {
                        buf.append(")");
                    }
		}
	}

	public void visit(Not visitable) {
		visitUnaryPredicate(visitable);
	}

	public void visit(IsNotSet visitable) {
		buf.append("(");
		DomainSpecificVariable operand = visitable.getOperand();
		String expression = operand.getToken();
		final String typeName = operand.getType();

		/*
		 * TypeSystem typeSystem = TypeSystem.getInstance(); Type type =
		 * typeSystem.getType(typeName);
		 */

		Type type = operand.getDomainTypeSystem().getType(typeName);

		StringBuffer defaultValueCheck = new StringBuffer(50);
                if(operand.isCollection()){
                    defaultValueCheck.append(".isEmpty()");
                }else if (type.supportsLiteral()) {
                     LiteralSupport literalSupport = (LiteralSupport) type;
                     String defaultValue = literalSupport.getLiteralForDefaultValue();

                     if (defaultValue != null) {
                         if (Type.STRING.equals(typeName)) {
                             defaultValueCheck.append(".trim()");
                         }

                         defaultValueCheck.append(" == ");
                         defaultValueCheck.append(defaultValue);
                     }
                 }

		buf.append(expression);
		buf.append(" == null");

		if (defaultValueCheck.length() > 0) {
			buf.append(" || ");
			buf.append(expression);
			buf.append(defaultValueCheck);
		}

		buf.append(")");
	}

	public void visit(IsSet isSet) {
		IsNotSet isNotSet = new IsNotSet(isSet.getOperand());
		Not not = new Not(isNotSet);
		not.accept(this);
	}

	public void visit(NotEquals visitable) {
		visit(new Not(new Equals(visitable.lhs, visitable.rhs)));
	}

	public void visit(Or visitable) {
		buf.append("(");
		Predicate lhs = visitable.getLhs();
		lhs.accept(this);
		buf.append(" || ");
		Predicate rhs = visitable.getRhs();
		rhs.accept(this);
		buf.append(")");
	}

	public void visit(DomainPredicate visitable) {
		visitable.getPredicate().accept(this);
	}

	public void visit(ForAnyOf visitable) {
		DomainSpecificVariable collectionValuedVariable = visitable
				.getCollectionValuedVariable();
		String collection = collectionValuedVariable.getToken();
		StringBuffer tempBuf = new StringBuffer();
		tempBuf.append(collection);
		tempBuf.append(".");
		tempBuf.append("{");

		boolean initialiseBufForCol = true;

		List<Predicate> leafPredicates = visitable.getLeafPredicates();
		leafPredicates = orderPredicates(leafPredicates);

		for (Iterator<Predicate> iter = leafPredicates.iterator(); iter
				.hasNext();) {
			Predicate leafPredicate = iter.next();
			// Start of Changes by jitesh.jain@tavant.com
			// This condition has been modified to handle multiple List objects
			// create OGNL expression accordingly
            if (leafPredicate instanceof AbstractCollectionUnaryPredicate) {
            	buf.append(collection);
				buf.append(".");
				buf.append("{ ? ");
				leafPredicate.accept(this);
				initialiseBufForCol = false;
                if (iter.hasNext()) {
					buf.append(" && ");
				}
            // End Of Changes
            }else if (leafPredicate instanceof MethodInvocation) {
				this.visit(((MethodInvocation) leafPredicate), tempBuf
						.toString(), false);
				if (iter.hasNext()) {
					buf.append(" && ");
				}
			} else if (leafPredicate instanceof AbstractDateDurationPredicate) {
				this.visit(((AbstractDateDurationPredicate) leafPredicate),
						tempBuf.toString(), false);
				if (iter.hasNext()) {
					buf.append(" && ");
				}
			} else {
				if (initialiseBufForCol) {
					buf.append(collection);
					buf.append(".");
					buf.append("{ ? ");
					initialiseBufForCol = false;
				}
				leafPredicate.accept(this);
				if (iter.hasNext()) {
					buf.append(" && ");
				}
			}
		}
		
		if (!initialiseBufForCol) {
			buf.append(" }.size > 0");
		}
	}

	public void visit(@SuppressWarnings("unused")
	ForAnyNOf visitable) {
		throw new UnsupportedOperationException();
	}

	public void visit(ForEachOf visitable) {
		DomainSpecificVariable collectionValuedVariable = visitable
				.getCollectionValuedVariable();
		String collection = collectionValuedVariable.getToken();
		StringBuffer tempBuf = new StringBuffer();
		tempBuf.append(collection);
		tempBuf.append(".");
		tempBuf.append("{");

		boolean initialiseBufForCol = true;

		List<Predicate> leafPredicates = visitable.getLeafPredicates();
		leafPredicates = orderPredicates(leafPredicates);

		for (Iterator<Predicate> iter = leafPredicates.iterator(); iter
				.hasNext();) {
			Predicate leafPredicate = iter.next();
			// Start of Changes by jitesh.jain@tavant.com
			// This condition has been modified to handle multiple List objects
			// create OGNL expression accordingly
			if (leafPredicate instanceof AbstractCollectionUnaryPredicate) {
            	buf.append(collection);
				buf.append(".");
				buf.append("{ ? ");
				buf.append("!");
				buf.append("(");
				leafPredicate.accept(this);
				initialiseBufForCol = false;
                if (iter.hasNext()) {
					buf.append(" && ");
				}
            // End of Changes
            }else if (leafPredicate instanceof MethodInvocation) {
				this.visit(((MethodInvocation) leafPredicate), tempBuf
						.toString(), true);
				if (iter.hasNext()) {
					buf.append(" && ");
				}
			} else if (leafPredicate instanceof AbstractDateDurationPredicate) {
				this.visit(((AbstractDateDurationPredicate) leafPredicate),
						tempBuf.toString(), true);
				if (iter.hasNext()) {
					buf.append(" && ");
				}
			} else {
				if (initialiseBufForCol) {
					buf.append(collection);
					buf.append(".");
					buf.append("{ ? ");
					buf.append("!");
					buf.append("(");
					initialiseBufForCol = false;
				}
				leafPredicate.accept(this);
				if (iter.hasNext()) {
					buf.append(" && ");
				}
			}
		}
		if (!initialiseBufForCol) {
			buf.append(") }.size==0");
		}
	}

	public void visit(All visitable) {
        if(visitable.isQueryPredicate()) {
            handleQueryPredicate(visitable);
        }

        visitNAryPredicate(visitable, " && ");
	}

    public void visit(Any visitable) {
        if(visitable.isQueryPredicate()) {
            handleQueryPredicate(visitable);
        }
        buf.append("(");
        visitNAryPredicate(visitable, " || ");
        buf.append(")");
	}

    private void visitNAryPredicate(AbstractNAryPredicate visitable, String conjunction) {
        boolean isQueryPredicate = visitable.isQueryPredicate();
        boolean visitedAtleastOnePredicate = false;

        for (Predicate predicate : visitable.getPredicates()) {
            if(shouldVisitPredicate(predicate, isQueryPredicate)) {

                if(!visitedAtleastOnePredicate && isQueryPredicate) {
                    buf.append(conjunction);
                }

                visitedAtleastOnePredicate = true;
				predicate.accept(this);

                buf.append(conjunction);
			}
		}

        if(visitedAtleastOnePredicate) {
            // To handle the trailing conjunction.
            int expressionLength = buf.length();
            buf.delete(expressionLength - conjunction.length(), expressionLength);
        }
    }

    private boolean shouldVisitPredicate(Predicate predicate, boolean rootIsQueryPredicate) {
        boolean visitPredicate = (predicate != null);

        // Handle any non-duplicacy predicates that were added to a duplicacy root predicate, using the "Add Existing
        // Condition" option.
        if(rootIsQueryPredicate && visitPredicate) {
            if(predicate instanceof DomainPredicate) {
                String domainPredicateContext = ((DomainPredicate) predicate).getContext();
                visitPredicate = visitPredicate & !CLAIM_DUPLICACY_RULES.equals(domainPredicateContext);
            } else {
                visitPredicate = false;
            }
        }

        return visitPredicate;
    }

	private void handleQueryPredicate(All all) {
        visitQuery(DuplicateCheckQueryGenerator.getInstance().getQuery(all));
	}

	private void handleQueryPredicate(Any any) {
        visitQuery(DuplicateCheckQueryGenerator.getInstance().getQuery(any));
    }

	private void visitQuery(String baseQuery) {
		StringBuffer ognlExpression = new StringBuffer(200);

		buf.append(ognlExpression.append(
				PredicateEvaluator.QUERY_EXECUTION_METHOD).append("(\"").append(
				baseQuery).append("\")").toString());
	}

	public void visit(Contains contains) {
		visitBinaryPredicateMethod(contains, "indexOf", Type.BOOLEAN);
		buf.append(" != -1");
	}

	public void visit(DoesNotContain doesNotContain) {
		visitBinaryPredicateMethod(doesNotContain, "indexOf", Type.BOOLEAN);
		buf.append(" == -1");
	}

	public void visitBinaryPredicate(BinaryPredicate visitable) {
		ExpressionToken ognlExpressionable = (ExpressionToken) visitable;
		boolean isAComposite = visitable instanceof Composite;
		if (isAComposite) {
			buf.append("(");
		}
		visitable.getLhs().accept(this);
		buf.append(ognlExpressionable.getToken());
		visitable.getRhs().accept(this);
		if (isAComposite) {
			buf.append(")");
		}
	}

	public void visitUnaryPredicate(UnaryPredicate visitable) {
		ExpressionToken ognlExpressionable = (ExpressionToken) visitable;
		buf.append(ognlExpressionable.getToken());
		buf.append("(");
		visitable.getOperand().accept(this);
		buf.append(")");
	}

	public void visit(MethodInvocation visitable) {
		Visitable invokeOn = visitable.invokeOn();
		Visitable[] arguments = visitable.arguments();
		String methodName = visitable.methodName();

		invokeOn.accept(this);
		buf.append(".");
		buf.append(methodName);
		buf.append("(");

		OGNLExpressionGenerator gen = new OGNLExpressionGenerator();

		for (int i = 0; i < arguments.length; i++) {
			arguments[i].accept(gen);
			if (i + 1 < arguments.length) {
				gen.buf.append(",");
			}
		}

		String argsExpression = gen.buf.toString();

		if (visitable instanceof BelongsTo || visitable instanceof DoesNotBelongTo) {
			argsExpression = argsExpression.replaceAll("\\.toLowerCase\\(\\)",
					"");
		}

		buf.append(argsExpression);

		buf.append(")");
	}

	public String getExpressionString() {
		return buf.toString();
	}

	public void visit(Addition addition) {
		final Value lhs = addition.getLhs();

		if (Type.BIGDECIMAL.equals(lhs.getType())) {
			visitBinaryPredicateMethod(addition, "add", Type.BIGDECIMAL);
		} else {
			buf.append("(");
			addition.getLhs().accept(this);
			buf.append(" + ");
			addition.getRhs().accept(this);
			buf.append(")");
		}
	}

	public void visit(IsTrue isTrue) {
		buf.append("(");
		isTrue.getOperand().accept(this);
		buf.append(")");
	}

	public void visit(IsFalse isFalse) {
		buf.append("(!");
		isFalse.getOperand().accept(this);
		buf.append(")");
	}

	public void visit(Between between) {
		Value lhs = between.getLhs();
		Value startingRhs = between.getStartingRhs();
		Value endingRhs = between.getEndingRhs();
		Boolean isInclusive = between.isInclusive();

		String operandType = lhs.getType();

		Value leftComparisonLhs;
		Value leftComparisonRhs;
		Value rightComparisonLhs;
		Value rightComparisonRhs;

		if (Type.INTEGER.equals(operandType) || Type.LONG.equals(operandType)) {
			leftComparisonLhs = lhs;
			leftComparisonRhs = startingRhs;
			rightComparisonLhs = lhs;
			rightComparisonRhs = endingRhs;
		} else if (Type.BIGDECIMAL.equals(operandType)
				|| Type.MONEY.equals(operandType)
				|| Type.DATE.equals(operandType)) {
			leftComparisonLhs = new CompareTo(lhs, startingRhs);
			leftComparisonRhs = new Constant("0", "integer");
			rightComparisonLhs = new CompareTo(lhs, endingRhs);
			rightComparisonRhs = new Constant("0", "integer");
		} else {
			throw new IllegalArgumentException("Operands of unknown value "
					+ "type [" + operandType
					+ "] specified for \"between\" operator.");
		}

		Predicate leftComparator = (isInclusive) ? new GreaterThanOrEquals(
				leftComparisonLhs, leftComparisonRhs) : new GreaterThan(
				leftComparisonLhs, leftComparisonRhs);
		Predicate rightComparator = (isInclusive) ? new LessThanOrEquals(
				rightComparisonLhs, rightComparisonRhs) : new LessThan(
				rightComparisonLhs, rightComparisonRhs);

		this.visit(new And(leftComparator, rightComparator));
	}

	public void visit(NotBetween notBetween) {
		Between between = new Between(notBetween.getLhs(), notBetween
				.getStartingRhs(), notBetween.getEndingRhs());
		between.setInclusive(notBetween.isInclusive());
		this.visit(new Not(between));
	}

	protected void visitDateDurationPredicate(
			AbstractDateDurationPredicate dateDurationPredicate,
			String methodName, boolean negateResult) {

		if (negateResult) {
			buf.append("!(");
		}

		buf.append(methodName);
		buf.append("(");
		buf.append(dateDurationPredicate.getLhs().getToken());
		if (dateDurationPredicate.getDateToCompare() != null) {
			buf.append(",");
			buf.append(dateDurationPredicate.getDateToCompare().getToken());
		}
		buf.append(",");
		buf.append(dateDurationPredicate.getRhs());

		buf.append(",");
		buf.append(dateDurationPredicate.getDurationType());
		buf.append(")");

		if (negateResult) {
			buf.append(")");
		}
	}

	public void visit(DoesNotEndWith doesNotEndWith) {
		visitBinaryPredicateMethod(doesNotEndWith, "endsWith", Type.BOOLEAN,
				true);
	}

	public void visit(DoesNotStartWith doesNotStartWith) {
		visitBinaryPredicateMethod(doesNotStartWith, "startsWith",
				Type.BOOLEAN, true);
	}

	public void visit(EndsWith endsWith) {
		visitBinaryPredicateMethod(endsWith, "endsWith", Type.BOOLEAN);
	}

	public void visit(StartsWith startsWith) {
		visitBinaryPredicateMethod(startsWith, "startsWith", Type.BOOLEAN);
	}

	public void visit(IsBefore isBefore) {
		visitBinaryPredicateMethod(isBefore, "isBefore", Type.BOOLEAN);
	}

	public void visit(IsAfter isAfter) {
		visitBinaryPredicateMethod(isAfter, "isAfter", Type.BOOLEAN);
	}

	public void visit(IsOnOrAfter isOnOrAfter) {
		IsBefore isBefore = new IsBefore(isOnOrAfter.getLhs(), isOnOrAfter
				.getRhs());
		Not isNotBefore = new Not(isBefore);
		this.visit(isNotBefore);
	}

	public void visit(IsDuringLast isDuringLast) {
		visitDateDurationPredicate(isDuringLast, "isDuringLast", false);
	}

	public void visit(IsNotDuringLast isNotDuringLast) {
		visitDateDurationPredicate(isNotDuringLast, "isDuringLast", true);
	}

	public void visit(IsDuringNext isDuringNext) {
		visitDateDurationPredicate(isDuringNext, "isDuringNext", false);
	}

	public void visit(IsNotDuringNext isNotDuringNext) {
		visitDateDurationPredicate(isNotDuringNext, "isDuringNext", true);
	}

	public void visit(IsWithinLast isWithinLast) {
		visitDateDurationPredicate(isWithinLast, "isWithinLast", false);
	}

	public void visit(IsNotWithinLast isNotWithinLast) {
		visitDateDurationPredicate(isNotWithinLast, "isWithinLast", true);
	}

	public void visit(IsWithinNext isWithinNext) {
		visitDateDurationPredicate(isWithinNext, "isWithinNext", false);
	}

	public void visit(IsNotWithinNext isNotWithinNext) {
		visitDateDurationPredicate(isNotWithinNext, "isWithinNext", true);
	}

	@SuppressWarnings("unused")
	public void visit(DateGreaterBy dateGreaterBy) {
		visitDateDurationPredicate(dateGreaterBy, "dateGreaterBy", false);
	}

	@SuppressWarnings("unused")
	public void visit(DateLesserBy dateLesserBy) {
		visitDateDurationPredicate(dateLesserBy, "dateLesserBy", false);
	}

	@SuppressWarnings("unused")
	public void visit(IsSameAs isSameAs) {
		// no op
	}

	public void visit(MethodInvocation visitable, String tempBuf,
			boolean forEach) {
		Visitable invokeOn = visitable.invokeOn();
		Visitable[] arguments = visitable.arguments();
		String methodName = visitable.methodName();

		invokeOn.accept(this);
		buf.append(".");
		buf.append(methodName);
		buf.append("(");

		OGNLExpressionGenerator gen = new OGNLExpressionGenerator();

		for (int i = 0; i < arguments.length; i++) {
			if (i == 0) {
				gen.buf.append(tempBuf);
			}
			arguments[i].accept(gen);
			if (i == 0) {
				gen.buf.append("}");
			}
			if (i + 1 < arguments.length) {
				gen.buf.append(",");
			}
		}
		gen.buf.append(",").append(forEach);

		String argsExpression = gen.buf.toString();

		if (visitable instanceof BelongsTo) {
			argsExpression = argsExpression.replaceAll("\\.toLowerCase\\(\\)",
					"");
		}

		buf.append(argsExpression);

		buf.append(")");
	}

	private List<Predicate> orderPredicates(List<Predicate> predicates) {

		List<Predicate> orderedPredicates = new ArrayList<Predicate>(predicates
				.size());
		List<Predicate> methodInvPredicates = new ArrayList<Predicate>(
				predicates.size());
		List<Predicate> otherPredicates = new ArrayList<Predicate>(predicates
				.size());

		for (Predicate predicate : predicates) {
			if (predicate instanceof MethodInvocation
					|| predicate instanceof AbstractDateDurationPredicate) {
				methodInvPredicates.add(predicate);
			} else {
				otherPredicates.add(predicate);
			}
		}
		for (Predicate methodInvPredicate : methodInvPredicates) {
			orderedPredicates.add(methodInvPredicate);
		}
		for (Predicate otherredicate : otherPredicates) {
			orderedPredicates.add(otherredicate);
		}
		return orderedPredicates;
	}

	public void visit(AbstractDateDurationPredicate visitable, String tempBuf,
			boolean forEach) {
		boolean negateResult = isNegatableDateDurationPredicate(visitable);
		if (negateResult) {
			buf.append("!(");
		}
		buf.append(getMethodNameForDateDurationPredicate(visitable));
		buf.append("(");

		buf.append(tempBuf);
		buf.append(visitable.getLhs().getToken());
		buf.append("}");
		if (visitable.getDateToCompare() != null) {
			buf.append(",");
			if (!visitable.getDateToCompare().getAccessedFromType().equals(
					"Claim")) {
				buf.append(tempBuf);
			}
			buf.append(visitable.getDateToCompare().getToken());
			if (!visitable.getDateToCompare().getAccessedFromType().equals(
					"Claim")) {
				buf.append("}");
			}
		}
		buf.append(",");
		buf.append(visitable.getRhs());
		buf.append(",");
		buf.append(visitable.getDurationType());
		buf.append(",").append(forEach);
		buf.append(")");
		if (negateResult) {
			buf.append(")");
		}
	}

	private String getMethodNameForDateDurationPredicate(
			AbstractDateDurationPredicate visitable) {
		if (visitable instanceof DateGreaterBy) {
			return "dateGreaterBy";
		} else if (visitable instanceof DateLesserBy) {
			return "dateLesserBy";
		} else if (visitable instanceof IsDuringLast
				|| visitable instanceof IsNotDuringLast) {
			return "isDuringLast";
		} else if (visitable instanceof IsDuringNext
				|| visitable instanceof IsNotDuringNext) {
			return "isDuringNext";
		} else if (visitable instanceof IsWithinLast
				|| visitable instanceof IsNotWithinLast) {
			return "isWithinLast";
		} else if (visitable instanceof IsWithinNext
				|| visitable instanceof IsNotWithinNext) {
			return "isWithinNext";
		}
		return null;
	}

	private boolean isNegatableDateDurationPredicate(
			AbstractDateDurationPredicate visitable) {
		if (visitable instanceof IsNotDuringLast
				|| visitable instanceof IsNotDuringNext
				|| visitable instanceof IsNotWithinLast
				|| visitable instanceof IsNotWithinNext) {
			return true;
		}
		return false;
	}

    public boolean handleInterCurrencyComparison(Value lhs, Value rhs) {
        boolean isForMoney = Type.MONEY.equals(rhs.getType());

        if(isForMoney) {
            buf.append("(");
            lhs.accept(this);
            buf.append(".breachEncapsulationOfCurrency().equals(");
            rhs.accept(this);
            buf.append(".breachEncapsulationOfCurrency()) && ");
        }

        return isForMoney;
    }
    
	public void visit(EqualsEnum visitable) {
		// Not Supported
	}

	public void visit(NotEqualsEnum visitable) {
		// Not Supported
	}

	public void visit(DateNotGreaterBy dateNotGreaterBy) {
		visitDateDurationPredicate(dateNotGreaterBy, "dateNotGreaterBy", false);
		
	}

	public void visit(DateNotLesserBy dateNotLesserBy) {
		visitDateDurationPredicate(dateNotLesserBy, "dateNotLesserBy", false);
	}
}
