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
 * Date: Mar 27, 2007
 * Time: 1:08:26 AM
 */

package tavant.twms.domain.rules;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Entity;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author vikas.s
 *
 */
@XStreamAlias("isFalse")
@Entity
@Table(name="predicate_is_false")
@Inheritance(strategy= InheritanceType.JOINED)
public class IsFalse implements Predicate {
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    private DomainSpecificVariable operand;

    //for frameworks.
    public IsFalse() {
    }

    //for programmers.
    public IsFalse(DomainSpecificVariable anotherOperand) {
        Assert.notNull(anotherOperand," operand to checked for being false," +
                " can't be null");
        operand = anotherOperand;
    }

    public void accept(Visitor visitor) {
       visitor.visit(this);
    }

    public DomainSpecificVariable getOperand() {
        return operand;
    }

    public void validate(ValidationContext validationContext) {
    }

    public String getDomainTerm() {
        return "label.operators.isFalse";
    }

    public void setOperand(DomainSpecificVariable operand) {
        this.operand = operand;
    }
    
    public Predicate getInverse() {
		return new IsTrue(operand);
	}

}
