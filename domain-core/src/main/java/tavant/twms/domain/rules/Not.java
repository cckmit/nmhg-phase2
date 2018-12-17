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

import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@XStreamAlias("not")
@Entity
@Table(name = "predicate_not")
@Inheritance(strategy = InheritanceType.JOINED)
public class Not implements UnaryPredicate, ExpressionToken, Composite {
    @ManyToOne
    private Predicate aPredicate;

    // for frameworks.
    public Not() {
    }

    // for programmers.
    public Not(Predicate anotherPredicate) {
        Assert.notNull(anotherPredicate, " predicate to be negated can't be null");
        this.aPredicate = anotherPredicate;
    }

    public String getToken() {
        return "!";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Predicate getOperand() {
        return this.aPredicate;
    }

    public void validate(ValidationContext validationContext) {
    }

    public String getDomainTerm() {
        return " is not true ";
    }

    public Predicate getAPredicate() {
        return this.aPredicate;
    }

    public Predicate getInverse() {
        return this.aPredicate;
    }

    public List<Predicate> getLeafPredicates() {
        List<Predicate> leafPredicates = new ArrayList<Predicate>();
        if (this.aPredicate instanceof NestablePredicate) {
            leafPredicates.addAll(((NestablePredicate) this.aPredicate).getLeafPredicates());
        } else {
            leafPredicates.add(this.aPredicate);
        }
        return leafPredicates;
    }
}
