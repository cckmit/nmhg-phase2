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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Table(name = "predicate_and")
@XStreamAlias("and")
@Inheritance(strategy = InheritanceType.JOINED)
public class And implements BinaryPredicate, Composite, NestablePredicate {
    @ManyToOne
    protected Predicate lhs;

    @ManyToOne
    protected Predicate rhs;

    public And(Predicate onePredicate, Predicate anotherPredicate) {
        this.lhs = onePredicate;
        this.rhs = anotherPredicate;
    }

    public And() {
        super();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Predicate getLhs() {
        return this.lhs;
    }

    public void setLhs(Predicate lhs) {
        this.lhs = lhs;
    }

    public Predicate getRhs() {
        return this.rhs;
    }

    public void setRhs(Predicate rhs) {
        this.rhs = rhs;
    }

    public void validate(ValidationContext validationContext) {
        if (this.lhs == null) {
            validationContext.addError("lhs cannot be null");
        }
        if (this.rhs == null) {
            validationContext.addError("rhs cannot be null");
        }
    }

    @Override
    public String toString() {
        StringBuffer text = new StringBuffer(50);
        text.append("(");
        text.append(this.lhs.toString());
        text.append(") and (");
        text.append(this.rhs.toString());
        text.append(")");

        return text.toString();
    }

    public String getDomainTerm() {
        return " and ";
    }

    public Predicate getInverse() {
        return new Or(this.lhs.getInverse(), this.rhs.getInverse());
    }

    public List<Predicate> getLeafPredicates() {
        List<Predicate> leafPredicates = new ArrayList<Predicate>();
        if (this.lhs instanceof NestablePredicate) {
            leafPredicates.addAll(((NestablePredicate) this.lhs).getLeafPredicates());
        } else {
            leafPredicates.add(this.lhs);
        }
        if (this.rhs instanceof NestablePredicate) {
            leafPredicates.addAll(((NestablePredicate) this.rhs).getLeafPredicates());
        } else {
            leafPredicates.add(this.rhs);
        }
        return leafPredicates;
    }

}
