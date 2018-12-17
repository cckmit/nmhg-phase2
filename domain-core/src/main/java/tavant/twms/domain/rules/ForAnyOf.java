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
 * Time: 11:10:35 PM
 */

package tavant.twms.domain.rules;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author vikas.sasidharan
 *
 */
@XStreamAlias("forAny")
public class ForAnyOf extends AbstractCollectionUnaryPredicate {

    public ForAnyOf() {
    }

    public ForAnyOf(DomainSpecificVariable collectionValuedVariable,
                    Predicate conditionToBeSatisfied) {
        super(collectionValuedVariable, conditionToBeSatisfied);
    }

    @Override
    public String getDomainTerm() {
        return "label.operators.forAny";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    public Predicate getInverse() {
    	return new ForEachOf(collectionValuedVariable,conditionToBeSatisfied.getInverse());	
    }
}
