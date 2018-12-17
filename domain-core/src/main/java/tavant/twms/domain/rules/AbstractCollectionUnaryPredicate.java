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
 * Time: 11:19:08 PM
 */

package tavant.twms.domain.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public abstract class AbstractCollectionUnaryPredicate implements UnaryPredicate {
    protected DomainSpecificVariable collectionValuedVariable;

    protected Predicate conditionToBeSatisfied;

    public AbstractCollectionUnaryPredicate() {
        super();
    }

    public AbstractCollectionUnaryPredicate(DomainSpecificVariable collectionValuedVariable,
            Predicate conditionToBeSatisfied) {

        Assert.notNull(collectionValuedVariable, "A collection valued variable must be specified");
        Assert.isTrue(collectionValuedVariable.isCollection(),
                      "Specified variable is not collection valued");
        Assert.notNull(conditionToBeSatisfied, "Condition to be satisfied must not be null");

        this.collectionValuedVariable = collectionValuedVariable;
        this.conditionToBeSatisfied = conditionToBeSatisfied;
    }

    public Predicate getOperand() {
        return this.conditionToBeSatisfied;
    }

    public void validate(ValidationContext validationContext) {
        if (this.collectionValuedVariable == null) {
            validationContext.addError("collection valued variable is null");
        }
        if (this.conditionToBeSatisfied != null) {
            validationContext.addError("condition to be satisifed is null");
        }
    }

    public DomainSpecificVariable getCollectionValuedVariable() {
        return this.collectionValuedVariable;
    }

    public void setCollectionValuedVariable(DomainSpecificVariable collectionValuedVariable) {
        this.collectionValuedVariable = collectionValuedVariable;
    }

    public Predicate getConditionToBeSatisfied() {
        return this.conditionToBeSatisfied;
    }

    public void setConditionToBeSatisfied(Predicate conditionToBeSatisfied) {
        this.conditionToBeSatisfied = conditionToBeSatisfied;
    }

    public abstract String getDomainTerm();

    public abstract void accept(Visitor visitor);

    public List<Predicate> getLeafPredicates() {
        List<Predicate> leafPredicates = new ArrayList<Predicate>();
        if (this.conditionToBeSatisfied instanceof NestablePredicate) {
            leafPredicates.addAll(((NestablePredicate) this.conditionToBeSatisfied)
                    .getLeafPredicates());
        } else {
            leafPredicates.add(this.conditionToBeSatisfied);
        }
        return leafPredicates;
    }

}
