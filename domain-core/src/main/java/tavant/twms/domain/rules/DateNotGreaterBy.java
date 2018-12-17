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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author vikas.sasidharan
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("notGreaterBy")
public class DateNotGreaterBy extends AbstractDateDurationPredicate {
	public DateNotGreaterBy(DomainSpecificVariable lhs, Constant duration,
			int durationType) {
		super(lhs, duration, durationType);
	}

	public DateNotGreaterBy(DomainSpecificVariable oneValue,
			DomainSpecificVariable dateToCompare, Constant anotherValue,
			int durationType) {
		super(oneValue, dateToCompare, anotherValue, durationType);
	}

	public DateNotGreaterBy() {
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String getDomainTerm() {
		return "label.operators.isNotGreaterBy";
	}

	public Predicate getInverse() {
		return new Not(this);
	}
}
