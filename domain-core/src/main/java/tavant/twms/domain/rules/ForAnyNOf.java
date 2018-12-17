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

import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 *
 */
@XStreamAlias("ForAnyNOf")
public class ForAnyNOf extends AbstractCollectionUnaryPredicate {
    private int _n;
    
    public ForAnyNOf() {
    }
    
    public ForAnyNOf(DomainSpecificVariable variable, Predicate condition,
                     int _n) {
        super(variable, condition);
        this._n = _n;

        Assert.isTrue(_n > 0,
                "The number of entries for which the predicate must be " +
                        "satisfied, must be positive");
    }

    @Override    
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public int get_n() {
        return _n;
    }

    @Override    
    public String getDomainTerm() {
        return "any, of";
    }
    
    public Predicate getInverse() {
		throw new UnsupportedOperationException("Method getInverse() is not supported for "
				+this.getClass().getName());	}


}
