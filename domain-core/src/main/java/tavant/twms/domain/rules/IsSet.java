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
 * Date: Mar 7, 2007
 * Time: 1:45:10 AM
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
@XStreamAlias("isSet")
@Entity
@Table(name="predicate_is_set")
@Inheritance(strategy= InheritanceType.JOINED)
public class IsSet implements Predicate {
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    private DomainSpecificVariable operand;

    //for frameworks.
    public IsSet() {
    }

    //for programmers.
    public IsSet(DomainSpecificVariable anotherOperand) {
        Assert.notNull(anotherOperand," operand to checked for being set," +
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
        return "label.operators.isSet";
    }

    public void setOperand(DomainSpecificVariable operand) {
        this.operand = operand;
    }
    
    public Predicate getInverse() {
		return new IsNotSet(operand);
	}
    
}
