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

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * @author radhakrishnan.j
 */
@XStreamAlias("any")
public class Any extends AbstractNAryPredicate {

    public Any() {
    }

    public Any(List<Predicate> predicates) {
        super(predicates);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getDomainTerm() {
        return " any ";
    }

    public Predicate getInverse() {
        return new All(getNegatedPredicates());
    }

    /**
     * For use in toString()
     * @see AbstractNAryPredicate#toString()
     * @return
     */
    protected String getDisplayedName() {
        return "Any Of";
    }
}
