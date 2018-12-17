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
 * Date: May 3, 2007
 * Time: 6:28:36 PM
 */

package tavant.twms.domain.rules;

import org.springframework.util.Assert;

public abstract class AbstractDateDurationPredicate implements BinaryPredicate {

	public static int COMPARE_TYPE_EXACTLY = 0;
	public static int COMPARE_TYPE_ATMOST = 1;
	public static int COMPARE_TYPE_ATLEAST = 2;
	
	protected DomainSpecificVariable lhs;
	protected Constant rhs;
	protected int durationType;
	protected DomainSpecificVariable dateToCompare;

	protected AbstractDateDurationPredicate(DomainSpecificVariable lhs,
			Constant duration, int durationType) {
		Assert.notNull(lhs, "cannot be null");
		Assert.notNull(duration, "cannot be null");
		Assert.notNull(durationType, "cannot be null");
		try {
			Integer.parseInt(duration.getLiteral());
		} catch (NumberFormatException e) {
			Assert.isTrue(false, "(rhs=" + duration.getType()
					+ ") should be of type integer");
		}
		/*
		 * Assert.isTrue(Type.INTEGER.equals(anotherValue.getType()), "(rhs=" +
		 * anotherValue.getType() + ") should be of type integer");
		 */
		Assert.isTrue(lhs.getType().equals(Type.DATE),
				" this check applies ONLY to date types");

		this.lhs = lhs;
		rhs = duration;
		this.durationType = durationType;
	}

	protected AbstractDateDurationPredicate() {
	}

	public AbstractDateDurationPredicate(DomainSpecificVariable lhs,
			DomainSpecificVariable dateToCompare, Constant duration,
			int durationType) {
		this(lhs, duration, durationType);
		// dateToCompare may be null!
		this.dateToCompare = dateToCompare;
	}

	public DomainSpecificVariable getLhs() {
		return lhs;
	}

	public Constant getRhs() {
		return rhs;
	}

	public void setLhs(DomainSpecificVariable lhs) {
		this.lhs = lhs;
	}

	public void setRhs(Constant rhs) {
		this.rhs = rhs;
	}

	public void validate(ValidationContext validationContext) {
		if (lhs == null) {
			validationContext.addError("lhs is null");
		}
		if (rhs == null) {
			validationContext.addError("rhs is null");
		}
		if (lhs != null && !(lhs.getType().equals(Type.DATE))) {
			validationContext.addError(" (lhs=" + lhs
					+ ") need to be of type DATE");
		}
		if (dateToCompare != null
				&& !(dateToCompare.getType().equals(Type.DATE))) {
			validationContext.addError(" (dateToCompare=" + dateToCompare
					+ ") need to be of type DATE");
		}
		if (rhs != null && !Type.INTEGER.equals(rhs.getType())) {
			validationContext.addError("rhs is not of type integer");
		}
	}

	public abstract String getDomainTerm();

	public int getDurationType() {
		return durationType;
	}

	public void setDurationType(int durationType) {
		this.durationType = durationType;
	}

	public DomainSpecificVariable getDateToCompare() {
		return dateToCompare;
	}

	public void setDateToCompare(DomainSpecificVariable dateToCompare) {
		this.dateToCompare = dateToCompare;
	}
}
