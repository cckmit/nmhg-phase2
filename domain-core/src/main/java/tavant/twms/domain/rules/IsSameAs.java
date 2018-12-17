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
 * Date: June 30, 2007
 * Time: 12:36:34 AM
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
@XStreamAlias("isSameAs")
@Entity
@Table(name="predicate_is_same_as")
@Inheritance(strategy= InheritanceType.JOINED)
public class IsSameAs implements Predicate {
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    private DomainSpecificVariable operand;

    public IsSameAs() {
        // for frameworks.
    }

    //for programmers.
    public IsSameAs(DomainSpecificVariable anotherOperand) {
        Assert.notNull(anotherOperand," operand to checked for 'is " +
                "same as', can't be null");
        operand = anotherOperand;
    }

    public void accept(Visitor visitor) {
       visitor.visit(this);
    }

    public DomainSpecificVariable getOperand() {
        return operand;
    }

    public String getDomainTerm() {
        return "label.operators.isSameAs";
    }

    public void setOperand(DomainSpecificVariable operand) {
        this.operand = operand;
    }
    
    public Predicate getInverse() {
		return new Not(this);
	}
    
}
