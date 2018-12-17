/**
 *
 */
package tavant.twms.web.rules;

import com.domainlanguage.time.CalendarDate;
import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.CLAIM_DUPLICACY_RULES;
import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.getInstance;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.businessobject.AbstractRulesBusinessObjectModel;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.rules.AbstractCollectionUnaryPredicate;
import tavant.twms.domain.rules.AbstractDateDurationPredicate;
import tavant.twms.domain.rules.AbstractNAryPredicate;
import tavant.twms.domain.rules.All;
import tavant.twms.domain.rules.And;
import tavant.twms.domain.rules.Any;
import tavant.twms.domain.rules.BelongsTo;
import tavant.twms.domain.rules.Between;
import tavant.twms.domain.rules.BinaryPredicate;
import tavant.twms.domain.rules.Constant;
import tavant.twms.domain.rules.Constants;
import tavant.twms.domain.rules.Contains;
import tavant.twms.domain.rules.DateAtleastGreaterBy;
import tavant.twms.domain.rules.DateAtleastLesserBy;
import tavant.twms.domain.rules.DateAtmostGreaterBy;
import tavant.twms.domain.rules.DateAtmostLesserBy;
import tavant.twms.domain.rules.DateExactlyGreaterBy;
import tavant.twms.domain.rules.DateExactlyLesserBy;
import tavant.twms.domain.rules.DateGreaterBy;
import tavant.twms.domain.rules.DateLesserBy;
import tavant.twms.domain.rules.DateNotGreaterBy;
import tavant.twms.domain.rules.DateNotLesserBy;
import tavant.twms.domain.rules.DoesNotBelongTo;
import tavant.twms.domain.rules.DoesNotContain;
import tavant.twms.domain.rules.DoesNotEndWith;
import tavant.twms.domain.rules.DoesNotStartWith;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.DomainSpecificVariable;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.EndsWith;
import tavant.twms.domain.rules.Equals;
import tavant.twms.domain.rules.FieldTraversal;
import tavant.twms.domain.rules.ForAnyOf;
import tavant.twms.domain.rules.ForEachOf;
import tavant.twms.domain.rules.GreaterThan;
import tavant.twms.domain.rules.GreaterThanOrEquals;
import tavant.twms.domain.rules.EqualsEnum;
import tavant.twms.domain.rules.IsAReturnWatchedPart;
import tavant.twms.domain.rules.IsAReviewWatchedPart;
import tavant.twms.domain.rules.IsAWatchedDealership;
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
import tavant.twms.domain.rules.IsSameAs;
import tavant.twms.domain.rules.IsSet;
import tavant.twms.domain.rules.IsTrue;
import tavant.twms.domain.rules.IsWithinLast;
import tavant.twms.domain.rules.IsWithinNext;
import tavant.twms.domain.rules.LessThan;
import tavant.twms.domain.rules.LessThanOrEquals;
import tavant.twms.domain.rules.FunctionField;
import tavant.twms.domain.rules.NAryPredicate;
import tavant.twms.domain.rules.NotBetween;
import tavant.twms.domain.rules.NotEquals;
import tavant.twms.domain.rules.NotEqualsEnum;
import tavant.twms.domain.rules.OGNLExpressionGenerator;
import tavant.twms.domain.rules.Or;
import tavant.twms.domain.rules.Predicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.StartsWith;
import tavant.twms.domain.rules.Type;
import tavant.twms.domain.rules.Value;
import tavant.twms.domain.rules.Visitable;


import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TextProvider;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.rules.DateType;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;

import tavant.twms.domain.rules.SimpleField;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AdditionalAttributesRepository;
import tavant.twms.domain.additionalAttributes.AdditionalAttributesService;
import tavant.twms.domain.additionalAttributes.AttributePurpose;

/**
 * @author radhakrishnan.j
 */
@SuppressWarnings("serial")
public class RuleJSONSerializer {
	private static Logger logger = LogManager
			.getLogger(RuleJSONSerializer.class);

	// private DomainTypeSystem domainTypeSystem;
	// private IBusinessObjectModel businessObjectModel;
	private String context;
	private AdditionalAttributesRepository additionalAttributesRepository;
	
	

	private AdditionalAttributesService additionalAttributesService;

	private final Comparator<FieldTraversal> fieldTraversalComparator = new Comparator<FieldTraversal>() {

		private int getFieldTraversalTypeAsInt(FieldTraversal fieldTraversal) {
			// The order is :
			// Simple Field --> Simple Function --> One2One Field
			// --> One2One Function --> One2Many Field
			// --> One2Many Function
			if (fieldTraversal.endsInASimpleField()
					&& !fieldTraversal.endsInAFunction()) {
				return 0;
			} else if (fieldTraversal.endsInAQueryTemplate()) {
				return 1;
			} else if (fieldTraversal.endsInASimpleFunction()) {
				return 2;
			} else if (fieldTraversal.endsInAOne2One()) {
				return 3;
			} else if (fieldTraversal.endsInAOneToOneFunction()) {
				return 4;
			} else if (fieldTraversal.endsInACollection()) {
				return 5;
			} else {
				return 6;
			}
		}

		public int compare(FieldTraversal firstFieldTraversal,
				FieldTraversal secondFieldTraversal) {
			int typeComparison = getFieldTraversalTypeAsInt(firstFieldTraversal)
					- getFieldTraversalTypeAsInt(secondFieldTraversal);

			if (typeComparison == 0) { // same type
				String firstFieldTraversalName = firstFieldTraversal
						.getDomainName();
				String secondFieldTraversalName = secondFieldTraversal
						.getDomainName();
				typeComparison = firstFieldTraversalName
						.compareTo(secondFieldTraversalName);
			}

			return typeComparison;
		}
	};

	private static final String SEPARATOR_TYPE = "_SEPARATOR_";

	private static final String SEPARATOR_LABEL = "------------------------------------------------";

	public RuleJSONSerializer(String context) {
		this.context = context;

		init();
	}

	public void sortFieldTraversalsByType(List<FieldTraversal> fieldTraversals) {
		Collections.sort(fieldTraversals, fieldTraversalComparator);
	}

	private final Map<Class<? extends Predicate>, String> predicateToSynonymMap = new HashMap<Class<? extends Predicate>, String>();

	private final Map<Class, JSONifier> JSONifiers = new HashMap<Class, JSONifier>() {

		@SuppressWarnings("unchecked")
		@Override
		public JSONifier get(Object key) {
			JSONifier _JSONifier = super.get(key);
			if (logger.isDebugEnabled()) {
				Class klass = (Class) key;
				logger.debug(MessageFormat.format(
						" JSONifier for ''{0}'' -> {1}", klass.getSimpleName(),
						_JSONifier));
			}
			return _JSONifier;
		}

	};

	private final Map<String, Objectifier> Objectifiers = new HashMap<String, Objectifier>() {

		@Override
		public Objectifier get(Object key) {
			Objectifier objectifier = super.get(key);
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(
						" Objectifier for ''{0}'' -> {1}", key, objectifier));
			}

			return objectifier;
		}

	};

	private PredicateAdministrationService predicateAdministrationService;

	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	private void init() {
		if (context == null) {
			throw new IllegalStateException(
					"Context cannot be null in RuleJSONSerializer");
		}
		IBusinessObjectModel busObject = getInstance().getBusinessObjectModel(
				context);
		Collection<Type> tempCollection = busObject.getDomainTypeSystem()
				.listAllTypes();
		try {
			for (Type type : tempCollection) {
				Set<Class<? extends Predicate>> supportedPredicates = type
						.supportedPredicates();
				for (Class<? extends Predicate> predicateKlass : supportedPredicates) {
					predicateToSynonymMap.put(predicateKlass, predicateKlass
							.newInstance().getDomainTerm());
				}
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		JSONifiers.put(DomainPredicate.class,
				new DomainPredicateShallowJSONifier());
		JSONifiers.put(DomainRule.class, new DomainRuleJSONifier());
		JSONifiers.put(Or.class, new OrJSONifier());
		JSONifiers.put(And.class, new AndJSONifier());
		JSONifiers.put(All.class, new AllJSONifier());
		JSONifiers.put(Any.class, new AnyJSONifier());
		JSONifiers.put(DomainSpecificVariable.class,
				new DomainSpecificVariableJSONifier());
		JSONifiers.put(Equals.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(NotEquals.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(DateExactlyGreaterBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateExactlyGreaterBy();
					}
				});
		JSONifiers.put(DateAtleastGreaterBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtleastGreaterBy();
					}
				});
		JSONifiers.put(DateAtmostGreaterBy.class,
			new AbstractDateDurationPredicateJSONifier() {
				@Override
				protected AbstractDateDurationPredicate getPredicateInstance() {
					return new DateAtmostGreaterBy();
				}
			});
		JSONifiers.put(DateGreaterBy.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateGreaterBy();
					}

				});
		JSONifiers.put(DateNotGreaterBy.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateNotGreaterBy();
					}

				});
		JSONifiers.put(GreaterThan.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(GreaterThanOrEquals.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(LessThanOrEquals.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(IsBefore.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(IsAfter.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(DateExactlyLesserBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateExactlyLesserBy();
					}
				});
		JSONifiers.put(DateAtleastLesserBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtleastLesserBy();
					}
				});
		JSONifiers.put(DateAtmostLesserBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtmostLesserBy();
					}
				});
		JSONifiers.put(DateLesserBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateLesserBy();
					}
				});
		JSONifiers.put(DateNotLesserBy.class,
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateNotLesserBy();
					}
				});
		JSONifiers.put(LessThan.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(IsOnOrBefore.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(IsOnOrAfter.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers
				.put(StartsWith.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(DoesNotStartWith.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(EndsWith.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(DoesNotEndWith.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(Contains.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(DoesNotContain.class,
				new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(Constant.class, new ConstantJSONifier());
		JSONifiers.put(Constants.class, new ConstantsJSONifier());
		JSONifiers.put(IsOneOf.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(IsNoneOf.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(EqualsEnum.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(NotEqualsEnum.class, new BinaryOperatorPredicateJSONifier());
		JSONifiers.put(ForAnyOf.class, new ForAnyPredicateJSONifier());
		JSONifiers.put(ForEachOf.class, new ForEachPredicateJSONifier());
		JSONifiers.put(IsNotSet.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				IsNotSet isNotSet = (IsNotSet) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", isNotSet.getDomainTerm());
				DomainSpecificVariable operand = isNotSet.getOperand();
				jsonObject.put("left", JSONifiers.get(operand.getClass())
						.toJSON(operand));

				return jsonObject;
			}
		});

		JSONifiers.put(IsSameAs.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				IsSameAs isSameAs = (IsSameAs) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", isSameAs.getDomainTerm());
				DomainSpecificVariable operand = isSameAs.getOperand();
				jsonObject.put("left", JSONifiers.get(operand.getClass())
						.toJSON(operand));

				return jsonObject;
			}
		});

		JSONifiers.put(IsSet.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				IsSet isSet = (IsSet) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", isSet.getDomainTerm());
				DomainSpecificVariable operand = isSet.getOperand();
				jsonObject.put("left", JSONifiers.get(operand.getClass())
						.toJSON(operand));

				return jsonObject;
			}
		});

		JSONifiers.put(IsTrue.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				IsTrue isTrue = (IsTrue) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", isTrue.getDomainTerm());
				DomainSpecificVariable operand = isTrue.getOperand();
				jsonObject.put("left", JSONifiers.get(operand.getClass())
						.toJSON(operand));

				return jsonObject;
			}
		});

		JSONifiers.put(IsFalse.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				IsFalse isFalse = (IsFalse) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", isFalse.getDomainTerm());
				DomainSpecificVariable operand = isFalse.getOperand();
				jsonObject.put("left", JSONifiers.get(operand.getClass())
						.toJSON(operand));

				return jsonObject;
			}
		});

		JSONifiers.put(IsAReturnWatchedPart.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				IsAReturnWatchedPart predicate = (IsAReturnWatchedPart) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				Visitable argument = predicate.arguments()[0];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));

				return jsonObject;
			}
		});

		JSONifiers.put(IsAReviewWatchedPart.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				IsAReviewWatchedPart predicate = (IsAReviewWatchedPart) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				Visitable argument = predicate.arguments()[0];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));

				return jsonObject;
			}
		});

		JSONifiers.put(BelongsTo.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				BelongsTo predicate = (BelongsTo) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", predicate.getDomainTerm());
				Visitable argument = predicate.arguments()[0];
				Visitable watchedList = predicate.arguments()[1];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));
				jsonObject.put("right", JSONifiers.get(Constant.class).toJSON(
						watchedList));

				return jsonObject;
			}
		});
		
		JSONifiers.put(DoesNotBelongTo.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				DoesNotBelongTo predicate = (DoesNotBelongTo) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", predicate.getDomainTerm());
				Visitable argument = predicate.arguments()[0];
				Visitable watchedList = predicate.arguments()[1];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));
				jsonObject.put("right", JSONifiers.get(Constant.class).toJSON(
						watchedList));

				return jsonObject;
			}
		});

		JSONifiers.put(IsAReturnWatchedPart.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				IsAReturnWatchedPart predicate = (IsAReturnWatchedPart) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", predicate.getDomainTerm());
				Visitable argument = predicate.arguments()[0];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));
				return jsonObject;
			}
		});

		JSONifiers.put(IsAReviewWatchedPart.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				IsAReviewWatchedPart predicate = (IsAReviewWatchedPart) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", predicate.getDomainTerm());
				Visitable argument = predicate.arguments()[0];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));
				return jsonObject;
			}
		});

		JSONifiers.put(IsAWatchedDealership.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				JSONObject jsonObject = new JSONObject();
				IsAWatchedDealership predicate = (IsAWatchedDealership) aPredicate;
				jsonObject.put("type", "EXPRESSION");
				jsonObject.put("name", predicate.getDomainTerm());
				Visitable argument = predicate.arguments()[0];
				// Visitable watchedList = predicate.arguments()[1];
				jsonObject.put("left", JSONifiers.get(argument.getClass())
						.toJSON(argument));
				// jsonObject.put("right",
				// JSONifiers.get(Constant.class).toJSON(watchedList));

				return jsonObject;
			}
		});

		JSONifiers.put(Between.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				Between between = (Between) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", between.getDomainTerm());
				jsonObject.put("type", "EXPRESSION");
				Value lhs = between.getLhs();
				String startingRhs = between.getStartingRhs().getLiteral();
				String endingRhs = between.getEndingRhs().getLiteral();

                // Fix the date issue for UK users where date was not
                // getting retained while clicking show search query
                if (DateType.DATE.equalsIgnoreCase(between.getLhs().getType())) {

                    startingRhs = getLocaleFormattedDateForDisplay(startingRhs);
                    endingRhs = getLocaleFormattedDateForDisplay(endingRhs);
                }

				jsonObject.put("left", JSONifiers.get(lhs.getClass()).toJSON(lhs));
				JSONArray rhsArray = new JSONArray();
				rhsArray.put(startingRhs);
				rhsArray.put(endingRhs);
				JSONObject rhsJSON = new JSONObject();
				rhsJSON.put("value", rhsArray);
				jsonObject.put("right", rhsJSON);
				return jsonObject;
			}

		});

		JSONifiers.put(NotBetween.class, new JSONifier() {

			public JSONObject toJSON(Object aPredicate) throws JSONException {
				NotBetween notBetween = (NotBetween) aPredicate;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", notBetween.getDomainTerm());
				jsonObject.put("type", "EXPRESSION");
				Value lhs = notBetween.getLhs();
				Constant startingRhs = notBetween.getStartingRhs();
				Constant endingRhs = notBetween.getEndingRhs();
				jsonObject.put("left", JSONifiers.get(lhs.getClass()).toJSON(
						lhs));
				JSONArray rhsArray = new JSONArray();
				rhsArray.put(startingRhs.getLiteral());
				rhsArray.put(endingRhs.getLiteral());
				JSONObject rhsJSON = new JSONObject();
				rhsJSON.put("value", rhsArray);
				jsonObject.put("right", rhsJSON);
				return jsonObject;
			}

		});

		JSONifiers.put(IsDuringLast.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsDuringLast();
					}
				});
		JSONifiers.put(IsNotDuringLast.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotDuringLast();
					}
				});

		JSONifiers.put(IsDuringNext.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsDuringNext();
					}
				});

		JSONifiers.put(IsNotDuringNext.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotDuringNext();
					}
				});

		JSONifiers.put(IsWithinLast.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsWithinLast();
					}
				});

		JSONifiers.put(IsNotWithinLast.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotWithinLast();
					}
				});

		JSONifiers.put(IsWithinNext.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsWithinNext();
					}
				});

		JSONifiers.put(IsNotWithinNext.class,
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotWithinNext();
					}
				});

		Objectifiers.put("EXPRESSION", new UnaryOrBinaryPredicateObjectifier());
		Objectifiers.put("for any", new ForAnyPredicateJSONifier());

		Objectifiers.put("for each", new ForEachPredicateJSONifier());

		Objectifiers.put("anybinarypredicate",
				new BinaryOperatorPredicateJSONifier());

		Objectifiers
				.put("RULE_FRAGMENT", new DomainPredicateShallowJSONifier());

		Objectifiers.put("OPERATOR", new OperatorObjectifier());
		Objectifiers.put("and", new OrAndObjectifier());
		Objectifiers.put("or", new OrAndObjectifier());
		Objectifiers.put("all", new AllAnyObjectifier());
		Objectifiers.put("any", new AllAnyObjectifier());

		Objectifiers.put("VARIABLE", new DomainSpecificVariableJSONifier());
		Objectifiers.put("CONSTANT", new ConstantJSONifier());
		Objectifiers.put("CONSTANTS", new ConstantsJSONifier());

		// FIXME: Using 'user readable' strings as keys might not be a such good
		// idea.
		// FIXME: May be the "real readable" strings could be maintained
		// separately, and these words server as keys :-?

		Objectifiers.put(new Equals().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new Equals(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new NotEquals().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new NotEquals(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new EqualsEnum().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new EqualsEnum(lhsValue, rhsValue);
					}

				});
		
		Objectifiers.put(new NotEqualsEnum().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new NotEqualsEnum(lhsValue, rhsValue);
					}

				});
		
		Objectifiers.put(new IsSameAs().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject operand = jsonObject.getJSONObject("left");
						DomainSpecificVariable operandValue = (DomainSpecificVariable) Objectifiers
								.get(operand.getString("type")).fromJSON(
										operand, objectificationContext);

						return new IsSameAs(operandValue);
					}

				});

		Objectifiers.put(new IsNotSet().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						return new IsNotSet(lhsValue);
					}

				});

		Objectifiers.put(new IsSet().getDomainTerm().trim(), new Objectifier() {

			public Object fromJSON(JSONObject jsonObject,
					ObjectificationContext objectificationContext)
					throws JSONException, InstantiationException,
					IllegalAccessException {
				JSONObject _lhs = jsonObject.getJSONObject("left");
				DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
						.get(_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

				return new IsSet(lhsValue);
			}

		});

		Objectifiers.put(new LessThan().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {

						JSONObject _lhs = jsonObject.getJSONObject("left");
						String lhsType = _lhs.getString("type");

						Value lhsValue = (Value) Objectifiers.get(lhsType)
								.fromJSON(_lhs, objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new LessThan(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new LessThanOrEquals().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new LessThanOrEquals(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new GreaterThan().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {

						JSONObject _lhs = jsonObject.getJSONObject("left");
						String lhsType = _lhs.getString("type");

						Value lhsValue = (Value) Objectifiers.get(lhsType)
								.fromJSON(_lhs, objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new GreaterThan(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new GreaterThanOrEquals().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {

						JSONObject _lhs = jsonObject.getJSONObject("left");
						String lhsType = _lhs.getString("type");

						Value lhsValue = (Value) Objectifiers.get(lhsType)
								.fromJSON(_lhs, objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new GreaterThanOrEquals(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new IsBefore().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new IsBefore(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new IsOnOrBefore().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new IsOnOrBefore(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new IsAfter().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new IsAfter(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new IsOnOrAfter().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new IsOnOrAfter(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new IsOneOf().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get("CONSTANTS")
								.fromJSON(_rhs, objectificationContext);

						return new IsOneOf(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new IsNoneOf().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						Value lhsValue = (Value) Objectifiers.get(
								_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get("CONSTANTS")
								.fromJSON(_rhs, objectificationContext);

						return new IsNoneOf(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new StartsWith().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Constant rhsValue = (Constant) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new StartsWith(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new DoesNotStartWith().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Constant rhsValue = (Constant) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new DoesNotStartWith(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new EndsWith().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Constant rhsValue = (Constant) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new EndsWith(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new DoesNotEndWith().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Constant rhsValue = (Constant) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new DoesNotEndWith(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new Contains().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Constant rhsValue = (Constant) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new Contains(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new DoesNotContain().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Constant rhsValue = (Constant) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new DoesNotContain(lhsValue, rhsValue);
					}
				});

		Objectifiers.put(new BelongsTo().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new BelongsTo(lhsValue, rhsValue);
					}

				});
		
		Objectifiers.put(new DoesNotBelongTo().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						JSONObject _rhs = jsonObject.getJSONObject("right");
						Value rhsValue = (Value) Objectifiers.get(
								_rhs.getString("type")).fromJSON(_rhs,
								objectificationContext);

						return new DoesNotBelongTo(lhsValue, rhsValue);
					}

				});

		Objectifiers.put(new IsAReturnWatchedPart().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						return new IsAReturnWatchedPart(lhsValue);
					}

				});

		Objectifiers.put(new IsAReviewWatchedPart().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						return new IsAReviewWatchedPart(lhsValue);
					}

				});

		Objectifiers.put(new IsAWatchedDealership().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						return new IsAWatchedDealership(lhsValue);
					}

				});

		Objectifiers.put(new IsTrue().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						return new IsTrue(lhsValue);
					}

				});

		Objectifiers.put(new IsFalse().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						return new IsFalse(lhsValue);
					}

				});

		Objectifiers.put(new Between().getDomainTerm().trim(), new Objectifier() {

			public Object fromJSON(JSONObject jsonObject,
					ObjectificationContext objectificationContext)
					throws JSONException, InstantiationException,
					IllegalAccessException {
				JSONObject _lhs = jsonObject.getJSONObject("left");
				DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
						.get(_lhs.getString("type")).fromJSON(_lhs,
								objectificationContext);

				String lhsType = lhsValue.getType();
				JSONArray rhsArray = jsonObject.getJSONObject("right")
						.getJSONArray("value");
				String _startingRhs = rhsArray.getString(0);
				Constant startingRhsValue = new Constant(_startingRhs, lhsType);
				String _endingRhs = rhsArray.getString(1);
				Constant endingRhsValue = new Constant(_endingRhs, lhsType);

				return new Between(lhsValue, startingRhsValue, endingRhsValue);
			}
		});

		Objectifiers.put(new NotBetween().getDomainTerm().trim(),
				new Objectifier() {

					public Object fromJSON(JSONObject jsonObject,
							ObjectificationContext objectificationContext)
							throws JSONException, InstantiationException,
							IllegalAccessException {
						JSONObject _lhs = jsonObject.getJSONObject("left");
						DomainSpecificVariable lhsValue = (DomainSpecificVariable) Objectifiers
								.get(_lhs.getString("type")).fromJSON(_lhs,
										objectificationContext);

						String lhsType = lhsValue.getType();
						JSONArray rhsArray = jsonObject.getJSONObject("right")
								.getJSONArray("value");
						String _startingRhs = rhsArray.getString(0);
						Constant startingRhsValue = new Constant(_startingRhs,
								lhsType);
						String _endingRhs = rhsArray.getString(1);
						Constant endingRhsValue = new Constant(_endingRhs,
								lhsType);

						return new NotBetween(lhsValue, startingRhsValue,
								endingRhsValue);
					}
				});

		Objectifiers.put(new DateExactlyGreaterBy().getDomainTerm().trim(),
			new AbstractDateDurationPredicateJSONifier() {
				@Override
				protected AbstractDateDurationPredicate getPredicateInstance() {
					return new DateExactlyGreaterBy();
				}
			});
		Objectifiers.put(new DateAtleastGreaterBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtleastGreaterBy();
					}
				});
		Objectifiers.put(new DateAtmostGreaterBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {
					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtmostGreaterBy();
					}
				});
		Objectifiers.put(new DateGreaterBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateGreaterBy();
					}
				});
		
		Objectifiers.put(new DateNotGreaterBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateNotGreaterBy();
					}
				});
		Objectifiers.put(new DateExactlyLesserBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateExactlyLesserBy();
					}
				});
		Objectifiers.put(new DateAtleastLesserBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtleastLesserBy();
					}
				});
		Objectifiers.put(new DateAtmostLesserBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateAtmostLesserBy();
					}
				});
		Objectifiers.put(new DateLesserBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateLesserBy();
					}
				});
		
		Objectifiers.put(new DateNotLesserBy().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new DateNotLesserBy();
					}
				});

		Objectifiers.put(new IsDuringLast().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsDuringLast();
					}
				});
		Objectifiers.put(new IsNotDuringLast().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotDuringLast();
					}
				});

		Objectifiers.put(new IsDuringNext().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsDuringNext();
					}
				});

		Objectifiers.put(new IsNotDuringNext().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotDuringNext();
					}
				});

		Objectifiers.put(new IsWithinLast().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsWithinLast();
					}
				});

		Objectifiers.put(new IsNotWithinLast().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotWithinLast();
					}
				});

		Objectifiers.put(new IsWithinNext().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsWithinNext();
					}
				});

		Objectifiers.put(new IsNotWithinNext().getDomainTerm().trim(),
				new AbstractDateDurationPredicateJSONifier() {

					@Override
					protected AbstractDateDurationPredicate getPredicateInstance() {
						return new IsNotWithinNext();
					}
				});
	}

    /**
     * Method converts date into User's locale format
     *
     * @param dateInDefaultFormat
     * @return
     */
    private String getLocaleFormattedDateForDisplay(String dateInDefaultFormat) {

        CalendarDate calendarDate =
                CalendarDate.from(dateInDefaultFormat, TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);

        return calendarDate.toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
    }

    public JSONArray _JSONifiedListOfVaribles(
			List<DomainSpecificVariable> listOfFields) throws JSONException {
		JSONArray _variables = new JSONArray();
		JSONifier _JSONifier = JSONifiers.get(DomainSpecificVariable.class);
		for (DomainSpecificVariable domainSpecificVariable : listOfFields) {
			domainSpecificVariable.setContext(context);
			JSONObject _variable;

			String accessedFromType = domainSpecificVariable
					.getAccessedFromType();

			if (void.class.getSimpleName().equals(accessedFromType)) {
				_variable = new JSONObject();
				_variable.put("type", SEPARATOR_TYPE);
			} else {
				_variable = _JSONifier.toJSON(domainSpecificVariable);
			}

			_variables.put(_variable);
		}
		return _variables;
	}

	public JSONObject toJSON(DomainRule aRule) throws JSONException {
		return JSONifiers.get(DomainRule.class).toJSON(aRule);
	}

	public JSONObject toJSON(DomainPredicate aNamedPredicate)
			throws JSONException {
		return JSONifiers.get(DomainPredicate.class).toJSON(aNamedPredicate);
	}

	public JSONObject toJSON(Predicate aNamedPredicate) throws JSONException {
		Class<? extends Predicate> klass = aNamedPredicate.getClass();
		if (logger.isDebugEnabled()) {
			logger.debug(MessageFormat.format(" toJSON({0})", klass
					.getSimpleName()));
		}
		return JSONifiers.get(klass).toJSON(aNamedPredicate);
	}

	public JSONArray toJSONArray(List<DomainPredicate> namedPredicates)
			throws JSONException {
		JSONArray _JSONArray = new JSONArray();
		for (DomainPredicate aNamedPredicate : namedPredicates) {
			_JSONArray.put(toJSON(aNamedPredicate));
		}
		return _JSONArray;
	}

	public Predicate fromJSON(String jsonString,
			ObjectificationContext objectificationContext) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			// Get the node type.
			String nodeType = jsonObject.getString("type");
			Predicate predicate = (Predicate) Objectifiers.get(nodeType)
					.fromJSON(jsonObject, objectificationContext);

			if (objectificationContext.isError()) {
				objectificationContext.setJsonObjectWithErrors(jsonObject);
			}

			return predicate;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public class OneToOnePredicateJSONifier implements JSONifier, Objectifier {

		@SuppressWarnings("unused")
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			return null;
		}

		@SuppressWarnings("unused")
		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException {
			return null;
		}
	}

	public abstract class AbstractCollectionPredicateJSONifier implements
			JSONifier, Objectifier {

		public JSONObject toJSON(Object aPredicate) throws JSONException {
			AbstractCollectionUnaryPredicate forEachOrAnyOf = (AbstractCollectionUnaryPredicate) aPredicate;
			DomainSpecificVariable collectionValuedVariable = forEachOrAnyOf
					.getCollectionValuedVariable();
			JSONObject _left = JSONifiers.get(
					collectionValuedVariable.getClass()).toJSON(
					collectionValuedVariable);

			JSONObject thisNode = new JSONObject();
			thisNode.put("type", "EXPRESSION");
			thisNode.put("name", forEachOrAnyOf.getDomainTerm().trim());
			thisNode.put("collectionSelector", getCollectionSelector());
			thisNode.put("left", _left);

			JSONArray subConditions = new JSONArray();
			Predicate operand = forEachOrAnyOf.getOperand();

			if (operand instanceof NAryPredicate) {
				NAryPredicate anyOrAll = (NAryPredicate) operand;
				String conjunction = StringUtils.trimWhitespace(anyOrAll
						.getDomainTerm());
				thisNode.put("subConditionConjunction", conjunction);

				for (Predicate subCondition : anyOrAll.getPredicates()) {
					subConditions.put(JSONifiers.get(subCondition.getClass())
							.toJSON(subCondition));
				}
			} else {
				// For legacy/canned predicates which don't have an any/all
				// explicitly specified.
				thisNode.put("subConditionConjunction", "all");
				subConditions.put(JSONifiers.get(operand.getClass()).toJSON(
						operand));
			}

			thisNode.put("subConditions", subConditions);

			return thisNode;
		}

		public abstract String getCollectionSelector();

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {

			AbstractCollectionUnaryPredicate forAnyOrEach = getCollectionPredicateInstance();
			JSONObject _leftJSON = jsonObject.getJSONObject("left");
			String nodeType = _leftJSON.getString("type");

			Objectifier objectifier = Objectifiers.get(nodeType);
			DomainSpecificVariable fromJSON = (DomainSpecificVariable) objectifier
					.fromJSON(_leftJSON, objectificationContext);
			forAnyOrEach.setCollectionValuedVariable(fromJSON);

			String subConditionConjunction = jsonObject
					.getString("subConditionConjunction");

			JSONObject _wrappedJSON = new JSONObject();
			_wrappedJSON.put("name", subConditionConjunction);
			_wrappedJSON.put("nodes", jsonObject.getJSONArray("subConditions"));

			objectifier = Objectifiers.get(subConditionConjunction);
			forAnyOrEach.setConditionToBeSatisfied((Predicate) objectifier
					.fromJSON(_wrappedJSON, objectificationContext));
			return forAnyOrEach;
		}

		protected abstract AbstractCollectionUnaryPredicate getCollectionPredicateInstance();
	}

	public class ForEachPredicateJSONifier extends
			AbstractCollectionPredicateJSONifier {

		@Override
		public String getCollectionSelector() {
			return "for each";
		}

		@Override
		protected AbstractCollectionUnaryPredicate getCollectionPredicateInstance() {
			return new ForEachOf();
		}
	}

	public class ForAnyPredicateJSONifier extends
			AbstractCollectionPredicateJSONifier {

		@Override
		public String getCollectionSelector() {
			return getTextProvider().getText("label.operators.forAny");
		}

		@Override
		protected AbstractCollectionUnaryPredicate getCollectionPredicateInstance() {
			return new ForAnyOf();
		}
	}

	public abstract class AbstractDateDurationPredicateJSONifier implements
			JSONifier, Objectifier {

		public JSONObject toJSON(Object aPredicate) throws JSONException {
			AbstractDateDurationPredicate dateDurationPredicate = (AbstractDateDurationPredicate) aPredicate;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", dateDurationPredicate.getDomainTerm());
			jsonObject.put("type", "EXPRESSION");
			Value lhs = dateDurationPredicate.getLhs();
			Constant rhs = dateDurationPredicate.getRhs();
			int durationType = dateDurationPredicate.getDurationType();
			jsonObject.put("left", JSONifiers.get(lhs.getClass()).toJSON(lhs));
			JSONObject rhsJSON = new JSONObject();
			JSONArray rhsArray = new JSONArray();
			rhsArray.put(rhs.getLiteral());
			rhsArray.put(durationType);
			Value dateToCompare = dateDurationPredicate.getDateToCompare();
			if (dateToCompare != null) {
				rhsArray.put(JSONifiers.get(dateToCompare.getClass()).toJSON(
						dateToCompare));
			}
			rhsJSON.put("value", rhsArray);
			jsonObject.put("right", rhsJSON);
			return jsonObject;
		}

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			AbstractDateDurationPredicate dateDurationPredicate = getPredicateInstance();
			JSONObject _left = jsonObject.getJSONObject("left");
			JSONArray rhsArray = jsonObject.getJSONObject("right")
					.getJSONArray("value");
			Objectifier objectifier = Objectifiers.get(_left.getString("type"));
			DomainSpecificVariable _leftVariable = (DomainSpecificVariable) objectifier
					.fromJSON(_left, objectificationContext);
			dateDurationPredicate.setLhs(_leftVariable);
			String _right = rhsArray.getString(0);
			Constant constant = new Constant(_right, "string");
			dateDurationPredicate.setRhs(constant);
			dateDurationPredicate.setDurationType(rhsArray.getInt(1));
			String _dateToCompareStr = rhsArray.optString(2, null);
			if (_dateToCompareStr != null) {
				JSONObject _dateToCompare = new JSONObject(_dateToCompareStr);
				objectifier = Objectifiers
						.get(_dateToCompare.getString("type"));
				DomainSpecificVariable _dateToCompareVariable = (DomainSpecificVariable) objectifier
						.fromJSON(_dateToCompare, objectificationContext);
				dateDurationPredicate.setDateToCompare(_dateToCompareVariable);
			}
			return dateDurationPredicate;
		}

		protected abstract AbstractDateDurationPredicate getPredicateInstance();
	}

	@SuppressWarnings("unchecked")
	private class AndJSONifier implements JSONifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			// We are recommending the use of All instead of And. And, anyway,
			// the UI would work only with All. So, convert the And to an All.
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(" toJSON({0})", aPredicate
						.getClass().getSimpleName()));
			}

			And and = (And) aPredicate;
			List predicates = new ArrayList<Predicate>(2);
			predicates.add(and.getLhs());
			predicates.add(and.getRhs());

			All all = new All(predicates);

			return JSONifiers.get(All.class).toJSON(all);
		}
	}

	private class AllJSONifier implements JSONifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(" toJSON({0})", aPredicate
						.getClass().getSimpleName()));
			}

			All all = (All) aPredicate;

			if (all.isForOneToOne()) {
				JSONObject thisNode = new JSONObject();
				thisNode.put("type", "EXPRESSION");
				thisNode.put("name", all.getDomainTerm());

				DomainSpecificVariable oneToOneVariable = all
						.getOneToOneVariable();
				thisNode.put("left", JSONifiers.get(
						DomainSpecificVariable.class).toJSON(oneToOneVariable));

				JSONArray subConditions = new JSONArray();
				thisNode.put("subConditionConjunction", "all");
				thisNode.put("collectionSelector", "for each");

				for (Predicate subCondition : all.getPredicates()) {
					subConditions.put(JSONifiers.get(subCondition.getClass())
							.toJSON(subCondition));
				}

				thisNode.put("subConditions", subConditions);

				return thisNode;
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "OPERATOR");
			jsonObject.put("name", " all ");

			RuleJSONSerializer rjs = getClaimDuplicacySerializerIfRequired(all
					.isQueryPredicate());

			JSONArray predicates = new JSONArray();
			for (Predicate p : all.getPredicates())
			{
				if(p!=null && rjs.JSONifiers.get(p.getClass())!=null){
					predicates.put(rjs.JSONifiers.get(p.getClass()).toJSON(p));
				}
			}
			jsonObject.put("nodes", predicates);

			return jsonObject;
		}
	}

	private class AnyJSONifier implements JSONifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(" toJSON({0})", aPredicate
						.getClass().getSimpleName()));
			}

			Any any = (Any) aPredicate;

			if (any.isForOneToOne()) {
				JSONObject thisNode = new JSONObject();
				thisNode.put("type", "EXPRESSION");
				thisNode.put("name", any.getDomainTerm());

				thisNode.put("left", any.getOneToOneVariable());

				JSONArray subConditions = new JSONArray();
				thisNode.put("subConditionConjunction", "all");

				for (Predicate subCondition : any.getPredicates()) {
					subConditions.put(JSONifiers.get(subCondition.getClass())
							.toJSON(subCondition));
				}

				return thisNode;
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "OPERATOR");
			jsonObject.put("name", " any ");

			RuleJSONSerializer rjs = getClaimDuplicacySerializerIfRequired(any
					.isQueryPredicate());

			JSONArray predicates = new JSONArray();
			for (Predicate p : any.getPredicates()) {
				predicates.put(rjs.JSONifiers.get(p.getClass()).toJSON(p));
			}

			jsonObject.put("nodes", predicates);
			return jsonObject;
		}
	}

	@SuppressWarnings("unchecked")
	private class OrJSONifier implements JSONifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			// We are recommending the use of Any instead of Or. And, anyway,
			// the UI would work only with Any. So, convert the Or to a
			// Any.
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(" toJSON({0})", aPredicate
						.getClass().getSimpleName()));
			}

			Or or = (Or) aPredicate;
			List predicates = new ArrayList<Predicate>(2);
			predicates.add(or.getLhs());
			predicates.add(or.getRhs());

			Any any = new Any(predicates);

			return JSONifiers.get(Any.class).toJSON(any);
		}
	}

	private class OrAndObjectifier implements Objectifier {

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			String operatorName = jsonObject.getString("name");
			JSONObject _left = jsonObject.getJSONObject("left");
			JSONObject _right = jsonObject.getJSONObject("right");
			Objectifier objectifier = Objectifiers.get(_left.getString("type"));
			Predicate _leftPredicate = (Predicate) objectifier.fromJSON(_left,
					objectificationContext);

			Predicate _rightPredicate = (Predicate) Objectifiers.get(
					_right.getString("type")).fromJSON(_right,
					objectificationContext);

			String trim = operatorName.trim();
			if (trim.equals("or")) {
				return new Or(_leftPredicate, _rightPredicate);
			} else if (trim.equals("and")) {
				return new And(_leftPredicate, _rightPredicate);
			} else {
				throw new RuntimeException(MessageFormat.format(
						" Unknown operator {0}", operatorName));
			}
		}

	}

	private class OperatorObjectifier implements Objectifier {
		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			String operatorName = jsonObject.getString("name");
			String opName = operatorName.trim().toLowerCase();
			return Objectifiers.get(opName).fromJSON(jsonObject,
					objectificationContext);
		}

	}

	/**
	 * This essentially replaces the 'and' or 'or' operators. This is basically
	 * an 'Nary' extension of these operators.
	 * 
	 * @author radhakrishnan.j
	 */
	private class AllAnyObjectifier implements Objectifier {

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			String operatorName = jsonObject.getString("name");
			Boolean isForOneToOne = jsonObject.optBoolean("isForOneToOne");
			Boolean isDuplicateCheck = jsonObject
					.optBoolean("isDuplicateCheck");
			JSONArray nodes = jsonObject.getJSONArray("nodes");
			List<Predicate> predicates = new ArrayList<Predicate>();
			final int numNodes = nodes.length();

			RuleJSONSerializer rjs = getClaimDuplicacySerializerIfRequired(isDuplicateCheck);

			for (int i = 0; i < numNodes; i++) {
				JSONObject node = (JSONObject) nodes.get(i);
				predicates.add((Predicate) rjs.Objectifiers.get(
						node.getString("type")).fromJSON(node,
						objectificationContext));
			}

			operatorName = operatorName.trim();

			AbstractNAryPredicate nAryPredicate;

			if ("all".equals(operatorName)) {
				nAryPredicate = new All(predicates);
			} else if ("any".equals(operatorName)) {
				nAryPredicate = new Any(predicates);
			} else {
				throw new RuntimeException(MessageFormat.format(
						" Unknown operator {0}", operatorName));
			}

			nAryPredicate.setQueryPredicate(isDuplicateCheck);
			nAryPredicate.setForOneToOne(isForOneToOne);

			if (isForOneToOne) {
				String oneToOneVariableExpr = jsonObject
						.optString("oneToOneVariable");
				String[] oneToOneVariableParts = oneToOneVariableExpr
						.split("#");
				String accessedFromType = oneToOneVariableParts[0];
				String fieldPath = oneToOneVariableParts[1];
				IBusinessObjectModel busObject = getInstance()
						.getBusinessObjectModel(context);
				FieldTraversal fieldTraversal = busObject.getField(
						accessedFromType, fieldPath);

				nAryPredicate.setOneToOneVariable(fieldTraversal
						.getDomainSpecificVariable());
			}

			return nAryPredicate;
		}

	}

	// This serializer is used in a very different context.
	private class DomainRuleJSONifier implements JSONifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			DomainRule rule = (DomainRule) aPredicate;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", rule.getName());
			jsonObject.put("type", "RULE");
			jsonObject.put("id", rule.getId());

			// No need to traverse the domain predicate.
			return jsonObject;
		}
	}

	// The keyworkd 'RULE' has an overloaded meaning!!
	private class DomainPredicateShallowJSONifier implements JSONifier,
			Objectifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			DomainPredicate domainPredicate = (DomainPredicate) aPredicate;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", domainPredicate.getName());
			jsonObject.put("type", "RULE_FRAGMENT");
			jsonObject.put("id", domainPredicate.getId());

			DetailedDescriptionGenerator descriptionGenerator = new DetailedDescriptionGenerator(
					domainPredicate);
			domainPredicate.accept(descriptionGenerator);

			jsonObject.put("description", descriptionGenerator
					.getDetailedDescription());
			return jsonObject;
		}

		@SuppressWarnings("unused")
		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException {
			DomainPredicate domainPredicate = predicateAdministrationService.findById(jsonObject.getLong("id"));
            domainPredicate.getPredicate(); // Needed to initialize the predicate from predicateAsXML
            return domainPredicate;
		}
	}

	private class DomainSpecificVariableJSONifier implements JSONifier,
			Objectifier {

		private final Map<Class, String> operatorAliases = new HashMap<Class, String>();

		{
			operatorAliases.put(DateGreaterBy.class, "is greater than");
			operatorAliases.put(DateLesserBy.class, "is less than");

			
		}

		public JSONObject toJSON(Object aPredicate) throws JSONException {
			DomainSpecificVariable domainSpecificVariable = (DomainSpecificVariable) aPredicate;
			BusinessUnit loggedInUserBU = new SecurityHelper()
			.getDefaultBusinessUnit();
			

			boolean isClaimDuplicacyVar = CLAIM_DUPLICACY_RULES.equals(context);

			domainSpecificVariable.setContext(context);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", domainSpecificVariable.getAccessedFromType()
					+ "#" + domainSpecificVariable.getFieldName());
			StringBuilder domainName = new StringBuilder(domainSpecificVariable
					.getDomainName());
			String claimsQueryKey = "";
			// StringBuilder domainNameSubString;
			StringBuilder localiseddomainString;

			if (domainName.indexOf("'") != -1) {
				claimsQueryKey = domainName
						.substring(domainName.indexOf("'") + 3);

				// is the key a BU specific one that needs to be picked from BU
				// specific messages properties
				if (AbstractRulesBusinessObjectModel.buSpecificKeyNames
						.contains(claimsQueryKey)) {
					if (loggedInUserBU != null
							|| SelectedBusinessUnitsHolder
									.getSelectedBusinessUnit() != null) {
						// either the person is a single line or performing
						// action in a BU specific context.SO
						// pick key from BU specific messages keys
						localiseddomainString = new StringBuilder(
								getTextProvider().getText(claimsQueryKey));
					} else {
						String buNamesOfUser = new SecurityHelper()
								.getLoggedInUser().getAllBUNames();
						localiseddomainString = new StringBuilder(
								getTextProvider().getText(claimsQueryKey,
										new String[] { buNamesOfUser }));
					}
				} else {
					localiseddomainString = new StringBuilder(getTextProvider()
							.getText(claimsQueryKey));
				}

			} else {

				// is the key a BU specific one that needs to be picked from BU
				// specific messages properties
				if (AbstractRulesBusinessObjectModel.buSpecificKeyNames
						.contains(domainName.toString())) {
					if (loggedInUserBU != null
							|| SelectedBusinessUnitsHolder
									.getSelectedBusinessUnit() != null) {
						// either the person is a single line or performing
						// action in a BU specific context.SO
						// pick key from BU specific messages keys
						localiseddomainString = new StringBuilder(
								getTextProvider()
										.getText(domainName.toString()));
					} else {
						String buNamesOfUser = new SecurityHelper()
								.getLoggedInUser().getAllBUNames();
						localiseddomainString = new StringBuilder(
								getTextProvider().getText(
										domainName.toString(),
										new String[] { buNamesOfUser }));
					}
				} else {
					localiseddomainString = new StringBuilder(getTextProvider()
							.getText(domainName.toString()));
				}
			}
			jsonObject.put("name", localiseddomainString);
			jsonObject.put("type", "VARIABLE");
			jsonObject.put("datatype", domainSpecificVariable.getType());

			jsonObject.put("isCollection", domainSpecificVariable
					.isCollection());
			jsonObject.put("isEntity", !isClaimDuplicacyVar
					&& domainSpecificVariable.isEntity());
			jsonObject.put("isSimpleVariable", domainSpecificVariable
					.isSimpleVariable());
			jsonObject.put("baseName", domainSpecificVariable
					.getBaseDomainName());
			jsonObject.put("primaryAccessedName", domainSpecificVariable
					.getPrimaryAccessedName());
			JSONArray _allowedOperators = new JSONArray();
			jsonObject.put("allowedOperators", _allowedOperators);

			// TypeSystem typeSystem = TypeSystem.getInstance();
			IBusinessObjectModel busObject = getInstance()
					.getBusinessObjectModel(context);
			Type coreType = busObject.getDomainTypeSystem().getType(
					domainSpecificVariable.getType());

			populateOperators(_allowedOperators, domainSpecificVariable,
					coreType, isClaimDuplicacyVar);

			if (coreType instanceof DomainType) {
				DomainType domainType = (DomainType) coreType;
				if(domainType.getName().equals("ClaimAttributes"))
                {
					if(domainType.getDomainName().equalsIgnoreCase(AdminConstants.JOB_CODE_ATTRIBUTES)){
						addClaimAttributesToBOM(busObject, domainType,AttributePurpose.JOB_CODE_PURPOSE);
					}
					else if(domainType.getDomainName().equalsIgnoreCase(AdminConstants.PART_ATTRIBUTES)){
						addClaimAttributesToBOM(busObject, domainType,AttributePurpose.PART_SOURCING_PURPOSE);
					}
					else if(domainType.getDomainName().equalsIgnoreCase(AdminConstants.CLAIMED_ITEM_ATTRIBUTES)){
						addClaimAttributesToBOM(busObject, domainType,AttributePurpose.CLAIMED_INVENTORY_PURPOSE);
					}
					else{
						addClaimAttributesToBOM(busObject, domainType,AttributePurpose.CLAIM_PURPOSE);
					}
			        
		       }
				SortedMap<String, FieldTraversal> fieldsForType = busObject
						.getDataElementsForType(domainType);

				JSONArray fieldsInVariable = new JSONArray();
				jsonObject.put("fields", fieldsInVariable);

				List<FieldTraversal> fields = new ArrayList<FieldTraversal>(
						fieldsForType.values());
				sortFieldTraversalsByType(fields);

				int numFields = fields.size();
				int firstOne2OneIndex = -1;
				int firstOne2ManyIndex = -1;

				for (int i = 0; i < numFields; i++) {
					FieldTraversal field = fields.get(i);

					if (field.endsInAOne2One() && firstOne2OneIndex == -1) {
						firstOne2OneIndex = i;
						// For separator.
						JSONArray fieldJSON = new JSONArray();
						fieldJSON.put(SEPARATOR_LABEL);
						fieldJSON.put(SEPARATOR_TYPE + i); // Since dojo now
															// enforces unique
															// keys.
						fieldsInVariable.put(fieldJSON);
					} else if (field.endsInACollection()
							&& firstOne2ManyIndex == -1) {
						firstOne2ManyIndex = i;
						// For separator.
						JSONArray fieldJSON = new JSONArray();
						fieldJSON.put(SEPARATOR_LABEL);
						fieldJSON.put(SEPARATOR_TYPE + i); // Since dojo now
															// enforces unique
															// keys.
						fieldsInVariable.put(fieldJSON);
					}

					DomainSpecificVariable dsv = field
							.getDomainSpecificVariable();
					dsv.setContext(context);
					JSONArray fieldJSON = new JSONArray();
					fieldJSON.put(getTextProvider().getText(
							field.getDomainName(false)));
					fieldJSON.put(dsv.getPrimaryAccessedName());

					fieldsInVariable.put(fieldJSON);
				}
			}

			return jsonObject;
		}

		private void addClaimAttributesToBOM(IBusinessObjectModel busObject,
				DomainType claimAttributes,AttributePurpose attributePurpose) {			
			List<AdditionalAttributes> additionalAttributes = additionalAttributesRepository.findAll();
			if(!additionalAttributes.isEmpty())
			  {
				  for(AdditionalAttributes attributes : additionalAttributes)
			      {
			            if("Number".equalsIgnoreCase(attributes.getAttributeType()))
			            {	
			            	FunctionField field = new FunctionField(attributes.getAttributeName(), 
			            			 						"getNumericAttrValueFor(\""+ attributes.getAttributeName()+"\")", 
			            			 							Type.INTEGER,false, FunctionField.Types.SIMPLE.getBaseType());
			            	 if(!claimAttributes.getFields().contains(field)){
			            		 claimAttributes.getFields().add(field);
			            	 }
			            }
	                            
			            if("Date".equalsIgnoreCase(attributes.getAttributeType()))
			            {	
			            	FunctionField field = new FunctionField(attributes.getAttributeName(), 
			            			 						"getDateAttrValueFor(\""+ attributes.getAttributeName()+"\")", 
			            			 							Type.DATE,false, FunctionField.Types.SIMPLE.getBaseType());
			            	 if(!claimAttributes.getFields().contains(field)){
			            		 claimAttributes.getFields().add(field);
			            	 }
			            }
			            if("Text".equalsIgnoreCase(attributes.getAttributeType()) ||"Text Area".equalsIgnoreCase(attributes.getAttributeType()))
			            {	
			            	FunctionField field = new FunctionField(attributes.getAttributeName(), 
			 						"getTextAttrValueFor(\""+ attributes.getAttributeName()+"\")", 
			 							Type.STRING,false, FunctionField.Types.SIMPLE.getBaseType());
				        	 if(!claimAttributes.getFields().contains(field)){
				        		 claimAttributes.getFields().add(field);
				        	 } 
			            }
			      }
				  busObject.addDataElementsForType(claimAttributes, "");
			  }
		}
		
		private void populateOperators(JSONArray _allowedOperators,
				DomainSpecificVariable dsv, Type coreType,
				boolean isForDuplicacy) {

			Set<Class<? extends Predicate>> supportedPredicates = getSupportedPredicates(
					coreType, dsv, isForDuplicacy);

			SortedMap<String, Class<? extends Predicate>> sortedMap = new TreeMap<String, Class<? extends Predicate>>();

			for (Class<? extends Predicate> predicateClass : supportedPredicates) {
				/** Perf Fix - Begin **/
                String simpleName = predicateClass.getSimpleName();
                // For String type fields " starts with " should be the first predicate shown in the UI for
                // performance reasons. Before this hack " contains " was shown as the first predicate. The HQL
                // generated using " starts with " is better performant than " contains ".
                //
                // Since the class name is used to sort the predicats " starts with " is shown last in the drop down.
                // Hence replacing the name StartsWith with BeginsWith.
                if ("StartsWith".equals(simpleName)) {
                    simpleName = "BeginsWith";
                }
                sortedMap.put(simpleName, predicateClass);
                /** Perf Fix - End **/
			}

			for (Class<? extends Predicate> predicateClass : sortedMap.values()) {
				// JSONObject operatorJSON = new JSONObject();
				// operatorJSON.put("name",predicateToSynonymMap.get(predicate));
				// operatorJSON.put("hasRhs",
				// !UnaryPredicate.class.isAssignableFrom(predicate) );
				// _allowedOperators.put(operatorJSON);
				String synonym = predicateToSynonymMap.get(predicateClass);
				if (synonym == null) {
					try {
						synonym = predicateClass.newInstance().getDomainTerm();
						predicateToSynonymMap.put(predicateClass, synonym);
					} catch (Exception e) {
						String errorMessage = "Exception while putting entry "
								+ "for [" + predicateClass
								+ "] into predicateToSynonym map.";
						logger.error(errorMessage);
						throw new RuntimeException(errorMessage, e);
					}
				}

				synonym = synonym.trim();

				String alias = getAliasForOperator(coreType, predicateClass,
						isForDuplicacy);

				StringBuilder aliasLocalised;
                
                if (alias == null) {
					alias = synonym;
				}

                aliasLocalised = new StringBuilder(getTextProvider().getText(alias));
                aliasLocalised.insert(0, " ");
                aliasLocalised.append(" ");

                JSONArray lvPair = new JSONArray();
				lvPair.put(aliasLocalised);
				lvPair.put(synonym);
				_allowedOperators.put(lvPair);
			}
		}

		private String getAliasForOperator(Type coreType,
				Class<? extends Predicate> predicateClass, boolean forDuplicacy) {
			if (forDuplicacy) {
				return operatorAliases.get(predicateClass);
			}

			return coreType.getAliasIfAnyForOperator(predicateClass);
		}

		private Set<Class<? extends Predicate>> getSupportedPredicates(
				Type coreType, DomainSpecificVariable dsv,
				boolean isForDuplicacy) {

			Set<Class<? extends Predicate>> predicates = new HashSet<Class<? extends Predicate>>(
					3);
			if (isForDuplicacy) {
				if (!dsv.isCollection()) {
					predicates.add(IsSameAs.class);
				}

				if (dsv.isSimpleVariable()) {
					if (Type.DATE.equals(dsv.getType())) {
						predicates.add(DateExactlyGreaterBy.class);
						predicates.add(DateAtleastGreaterBy.class);
						predicates.add(DateAtmostGreaterBy.class);
						predicates.add(DateExactlyLesserBy.class);
						predicates.add(DateAtleastLesserBy.class);
						predicates.add(DateAtmostLesserBy.class);
						//predicates.add(DateGreaterBy.class);
						//predicates.add(DateLesserBy.class);
					} else if (Type.INTEGER.equals(dsv.getType())) {
						predicates.add(GreaterThan.class);
						predicates.add(LessThan.class);
					}
				}
				return predicates;
			} else {
				if (dsv.isCollection()) {                                        
                                        predicates.add(IsSet.class);
                                        predicates.add(IsNotSet.class);
					return predicates;
				}
			}
			return coreType.supportedPredicates();
		}

		@SuppressWarnings("unused")
		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException {
			String variableName = jsonObject.getString("id");
			String[] containingTypeNameAndFieldName = variableName.split("#");

			IBusinessObjectModel busObject = getInstance()
					.getBusinessObjectModel(context);
			FieldTraversal fieldTraversal = busObject.getField(
					containingTypeNameAndFieldName[0],
					containingTypeNameAndFieldName[1]);
			DomainSpecificVariable dsv = fieldTraversal
					.getDomainSpecificVariable();
			dsv.setContext(context);
			return dsv;
		}
	}

	private class UnaryOrBinaryPredicateObjectifier implements Objectifier {

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			Objectifier objectifier = Objectifiers.get(jsonObject
					.getString("name"));
			if (objectifier == null) {
				objectifier = Objectifiers.get("anybinarypredicate");
			}
			return objectifier.fromJSON(jsonObject, objectificationContext);
		}
	}

	private class BinaryOperatorPredicateJSONifier implements JSONifier,
			Objectifier {

		public JSONObject toJSON(Object aPredicate) throws JSONException {
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format(" toJSON({0})", aPredicate
						.getClass().getSimpleName()));
			}

			BinaryPredicate predicate = (BinaryPredicate) aPredicate;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", predicate.getDomainTerm());
			jsonObject.put("type", "EXPRESSION");
			Class<? extends Visitable> klass = predicate.getLhs().getClass();
			jsonObject.put("left", JSONifiers.get(klass).toJSON(
					predicate.getLhs()));
			klass = predicate.getRhs().getClass();
			jsonObject.put("right", JSONifiers.get(klass).toJSON(
					predicate.getRhs()));
			return jsonObject;
		}

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			String operatorName = jsonObject.getString("name");
			return Objectifiers.get(operatorName.trim()).fromJSON(jsonObject,
					objectificationContext);
		}
	}

	private class ConstantJSONifier implements JSONifier, Objectifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			Constant constant = (Constant) aPredicate;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "CONSTANT");

            // Fix the date issue for UK users where date was not
            // getting retained while clicking show search query
            String literal = constant.getLiteral();
            if (DateType.DATE.equalsIgnoreCase(constant.getType())) {
                literal = getLocaleFormattedDateForDisplay(literal);
            }

			jsonObject.put("value", literal);
			jsonObject.put("datatype", constant.getType());
			return jsonObject;
		}

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException {
			// return new Constant(
			// jsonObject.getString("value"),jsonObject.getString("datatype") );
			// Post-construction validation friendly construction.
			Constant constant = new Constant();
                        String literal=jsonObject.getString("value");
                        if (DateType.DATE.equals(jsonObject.getString("datatype"))) {
                            CalendarDate calendarDate = CalendarDate.from(literal,
					TWMSDateFormatUtil.getDateFormatForLoggedInUser());
                            literal=calendarDate
					.toString(TWMSDateFormatUtil.DEFAULT_DATE_PATTERN);
                        }
			constant.setLiteral(literal);
			constant.setType(jsonObject.getString("datatype"));

			OGNLExpressionGenerator expressionGenerator = new OGNLExpressionGenerator();
			constant.accept(expressionGenerator);

			try {
				Ognl.getValue(expressionGenerator.getExpressionString(), null);
			} catch (OgnlException e) {
				jsonObject.put("error", MessageFormat.format(
						"''{0}'' is an invalid value for values of type {1}",
						constant.getLiteral(), constant.getType()));
				objectificationContext.setError(true);
			}

			return constant;
		}

	}

	private class ConstantsJSONifier implements JSONifier, Objectifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException {
			Constants constants = (Constants) aPredicate;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", "CONSTANTS");
			jsonObject.put("value", StringUtils
					.collectionToCommaDelimitedString(constants.getLiterals()));
			jsonObject.put("datatype", constants.getType());
			return jsonObject;
		}

		@SuppressWarnings("unchecked")
		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException {
			// return new
			// Constants(StringUtils.commaDelimitedListToSet(jsonObject.getString("value")
			// ),jsonObject.getString("datatype") );
			// Post-construction validation friendly construction.
			Constants constants = new Constants();
			Set commaDelimitedListToSet = StringUtils
					.commaDelimitedListToSet(jsonObject.getString("value"));
			ArrayList<String> literals = new ArrayList<String>();
			literals.addAll(commaDelimitedListToSet);
			constants.setLiterals(literals);
			constants.setType(jsonObject.getString("datatype"));

			Set<String> invalidValues = new HashSet<String>();

			for (String literal : literals) {
				try {
					OGNLExpressionGenerator expressionGenerator = new OGNLExpressionGenerator();
					constants.accept(expressionGenerator);
					Ognl.getValue(expressionGenerator.getExpressionString(),
							null);
				} catch (OgnlException e) {
					objectificationContext.setError(true);
					invalidValues.add(literal);
				}
			}

			if (!invalidValues.isEmpty()) {
				jsonObject.put("error", MessageFormat.format(
						"''{0}'' {1} for values of type {2}", invalidValues,
						invalidValues.size() == 1 ? "is an invalid value"
								: "are invalid values", constants.getType()));
			}
			return constants;
		}
	}

	static interface JSONifier {
		public JSONObject toJSON(Object aPredicate) throws JSONException;
	}

	static interface Objectifier {
		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException;
	}

	@SuppressWarnings("unused")
	private class ToBeDone implements Objectifier {
		private final String nodeType;

		public ToBeDone(String nodeType) {
			super();
			this.nodeType = nodeType;
		}

		public Object fromJSON(JSONObject jsonObject,
				ObjectificationContext objectificationContext)
				throws JSONException, InstantiationException,
				IllegalAccessException {
			throw new UnsupportedOperationException(MessageFormat.format(
					" Objectifier yet to be implemented for {0}", nodeType));
		}

	}

	static class ObjectificationContext {
		private String contextName;

		private ActionSupport actionSupport;

		private boolean error;

		private JSONObject jsonObjectWithErrors;

		public String getContextName() {
			return contextName;
		}

		public void setContextName(String contextName) {
			this.contextName = contextName;
		}

		public ActionSupport getActionSupport() {
			return actionSupport;
		}

		public void setActionSupport(ActionSupport actionSupport) {
			this.actionSupport = actionSupport;
		}

		public boolean isError() {
			return error;
		}

		public void setError(boolean error) {
			this.error = error;
		}

		public JSONObject getJsonObjectWithErrors() {
			return jsonObjectWithErrors;
		}

		public void setJsonObjectWithErrors(JSONObject jsonObjectWithErrors) {
			jsonObjectWithErrors = jsonObjectWithErrors;
		}

	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	protected RuleJSONSerializer getClaimDuplicacySerializerIfRequired(
			boolean isDuplicateCheck) {
		if(isDuplicateCheck){
			RuleJSONSerializer rjs = new RuleJSONSerializer(CLAIM_DUPLICACY_RULES);
			rjs.setTextProvider(this.getTextProvider());
                        rjs.setPredicateAdministrationService(
                                predicateAdministrationService);
			return rjs;
		}
		return this;
	}

	private TextProvider textProvider;

	public TextProvider getTextProvider() {
		return textProvider;
	}

	public void setTextProvider(TextProvider textProvider) {
		this.textProvider = textProvider;
	}
	public void setAdditionalAttributesService(
			AdditionalAttributesService additionalAttributesService) {
		this.additionalAttributesService = additionalAttributesService;
	}
	
	public AdditionalAttributesRepository getAdditionalAttributesRepository() {
		return additionalAttributesRepository;
	}

	public void setAdditionalAttributesRepository(
			AdditionalAttributesRepository additionalAttributesRepository) {
		this.additionalAttributesRepository = additionalAttributesRepository;
	}
}
