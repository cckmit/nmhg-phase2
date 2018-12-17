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

import java.util.HashSet;
import java.util.Set;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("DateType")
public class DateType extends AbstractType implements LiteralSupport {

	public static enum DurationType {
		DAY(0), WEEK(1), MONTH(2);

		private int type;

		private DurationType(int type) {
			this.type = type;
		}

		public static DurationType getDurationTypeForType(int durationType) {

			if (durationType == DAY.getType()) {
				return DAY;
			} else if (durationType == WEEK.getType()) {
				return WEEK;
			} else if (durationType == MONTH.getType()) {
				return MONTH;
			} else {
				throw new IllegalArgumentException("Unknown duration type : "
						+ durationType);
			}
		}

		public static Duration getDurationForTypeAndLength(int durationLength,
				int durationType) {
			return getDurationTypeForType(durationType).getDuration(
					durationLength);
		}

		public int getType() {
			return type;
		}

		public Duration getDuration(int duration) {
			switch (type) {
			case 0:
				return Duration.days(duration);
			case 1:
				return Duration.weeks(duration);
			case 2:
				return Duration.months(duration);
			default:
				throw new IllegalArgumentException("Unknown date duration : "
						+ duration);
			}
		}
	}

	Set<Class<? extends Predicate>> predicates;

	@Override
	public Set<Class<? extends Predicate>> supportedPredicates() {
		return predicates;
	}

	public DateType() {
		predicates = new HashSet<Class<? extends Predicate>>();
		setDefaultPredicates();
		predicates.add(IsBefore.class);
		predicates.add(IsOnOrBefore.class);
		predicates.add(IsAfter.class);
		predicates.add(IsOnOrAfter.class);
		predicates.add(IsWithinLast.class);
		predicates.add(IsNotWithinLast.class);
		predicates.add(IsWithinNext.class);
		predicates.add(IsNotWithinNext.class);
		predicates.add(IsDuringLast.class);
		predicates.add(IsNotDuringLast.class);
		predicates.add(IsDuringNext.class);
		predicates.add(IsNotDuringNext.class);
		predicates.add(Between.class);
		predicates.add(GreaterThan.class);
		predicates.add(LessThan.class);
		predicates.add(NotBetween.class);
		predicates.add(DateGreaterBy.class);
		predicates.add(DateNotGreaterBy.class);
		predicates.add(DateLesserBy.class);
		predicates.add(DateNotLesserBy.class);
		addOperatorAlias(GreaterThan.class, "label.operators.isGreaterThan");
		addOperatorAlias(LessThan.class, "label.operators.isLessThan");
		addOperatorAlias(DateGreaterBy.class, "label.operators.isGreaterThanDateOf");
		addOperatorAlias(DateNotGreaterBy.class, "label.operators.isNotGreaterThanDateOf");
		addOperatorAlias(DateLesserBy.class, "label.operators.isLessThanDateOf");
		addOperatorAlias(DateNotLesserBy.class, "label.operators.isNotLessThanDateOf");
	}

	public Object getJavaObject(String literal) {
		return CalendarDate.from(literal.trim(), "M/d/yyyy");
	}

	public String getName() {
		return Type.DATE;
	}

	public String getEvaluableExpression(String literal) {
		return "@com.domainlanguage.time.CalendarDate@from(\"" + literal
				+ "\",\"M/d/yyyy\")";
	}

	public boolean supportsLiteral() {
		return true;
	}

	public boolean isLiteralValid(String literal) {
		try {
			getJavaObject(literal);
			return true;
		} catch (RuntimeException e) {
			logger.error(" '" + literal + "' is not a valid '" + getName()
					+ "' literal");
		}
		return false;
	}

	public void setPredicates(Set<Class<? extends Predicate>> predicates) {
		this.predicates = predicates;
	}

	public String getLiteralForDefaultValue() {
		return "@com.domainlanguage.time.Clock@today()";
	}

}
