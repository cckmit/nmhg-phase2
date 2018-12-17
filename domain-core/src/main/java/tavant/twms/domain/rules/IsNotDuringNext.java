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
 * Date: May 4, 2007
 * Time: 5:31:45 PM
 */

package tavant.twms.domain.rules;


import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * @author <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class IsNotDuringNext extends AbstractDateDurationPredicate {

    public IsNotDuringNext(DomainSpecificVariable oneValue, Constant anotherValue,
                         int durationType) {
        super(oneValue, anotherValue, durationType);
    }

    public IsNotDuringNext() {
    }

    public IsNotDuringNext(DomainSpecificVariable oneValue,
			DomainSpecificVariable dateToCompare, Constant anotherValue, int durationType) {
		super(oneValue, dateToCompare, anotherValue, durationType);
	}

	@Override
    public void validate(ValidationContext validationContext) {
        super.validate(validationContext);

        if (DateType.DurationType.DAY.getType() == getDurationType()) {
            validationContext.addError("not during next check can take " +
                    "only calendar week or month as rhs");
        }
    }

    public String getDomainTerm() {
        return "label.operators.isNotDuringTheNext";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    
    public BinaryPredicate getInverse() {
		return new IsDuringNext(lhs, dateToCompare, rhs, durationType);
	}
}
