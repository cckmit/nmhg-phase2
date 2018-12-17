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
 * Date: Apr 26, 2007
 * Time: 10:48:18 PM
 */

package tavant.twms.domain.rules;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Between implements Predicate, Visitable {

	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	protected Value lhs;

	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	protected Constant startingRhs;

	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	protected Constant endingRhs;

	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	protected boolean inclusive = true;

	public Between() {
		super();
	}

	public Between(Value lhs, Constant startingRhs, Constant endingRhs) {
		Assert.notNull(lhs, " cannot be null");
		Assert.notNull(startingRhs, " cannot be null");
		Assert.notNull(endingRhs, " cannot be null");

		Assert.isTrue(validateOperandTypes(lhs, startingRhs, endingRhs),
				" (lhs=" + startingRhs.getType() + ", starting rhs="
						+ startingRhs.getType() + ", ending rhs="
						+ endingRhs.getType() + ") type incompatible values");
		this.lhs = lhs;
		this.startingRhs = startingRhs;
		this.endingRhs = endingRhs;
	}

	private boolean validateOperandTypes(Value lhs, Value startingRhs,
			Value endingRhs) {
		boolean areRhsValuesOfSameType = startingRhs.getType().equals(
				endingRhs.getType());
		boolean areLhsAndRhsOfSameType = lhs.getType().equals(
				startingRhs.getType());

		return areRhsValuesOfSameType && areLhsAndRhsOfSameType;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Value getLhs() {
		return lhs;
	}

	public void setLhs(Value lhs) {
		this.lhs = lhs;
	}

	public Constant getStartingRhs() {
		return startingRhs;
	}

	public void setStartingRhs(Constant startingRhs) {
		this.startingRhs = startingRhs;
	}

	public Constant getEndingRhs() {
		return endingRhs;
	}

	public void setEndingRhs(Constant endingRhs) {
		this.endingRhs = endingRhs;
	}

	public boolean isInclusive() {
		return inclusive;
	}

	public void setInclusive(boolean inclusive) {
		this.inclusive = inclusive;
	}

	public void validate(ValidationContext validationContext) {
		if (lhs == null) {
			validationContext.addError("lhs is null");
		}
		if (startingRhs == null) {
			validationContext.addError("starting rhs is null");
		}

		if (startingRhs == null) {
			validationContext.addError("starting rhs is null");
		}

		if (lhs != null && startingRhs != null && endingRhs != null
				&& !(validateOperandTypes(lhs, startingRhs, endingRhs))) {
			validationContext.addError(" all of (lhs=" + lhs + ",starting rhs="
					+ startingRhs + ",ending rhs=" + endingRhs
					+ ") need to be of same type");
		}

	}

	public String getDomainTerm() {
		return "label.operators.between";
	}

	public Predicate getInverse() {
		NotBetween notBw = new NotBetween(lhs, startingRhs, endingRhs);
		notBw.setInclusive(!inclusive);
		return notBw;
	}
}
