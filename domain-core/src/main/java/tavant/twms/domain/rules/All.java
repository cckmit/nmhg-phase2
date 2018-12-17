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

import java.util.Iterator;
import java.util.List;

/**
 * @author radhakrishnan.j
 *
 */
@XStreamAlias("all")
public class All extends AbstractNAryPredicate {

    public All(){
    }
    
    public All(List<Predicate> predicates){
        super(predicates);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getDomainTerm() {
        return " all ";
    }

    public Predicate getInverse() {
        return new Any(getNegatedPredicates());
    }

    @Override
    public String toString() {
        StringBuffer text = new StringBuffer(50);
        text.append("All of (");
        for (Iterator iter = predicates.iterator(); iter.hasNext();) {
            text.append(iter.next());
            if(iter.hasNext()) {
                text.append(",");
            }
        }
        text.append(")");
        
        return text.toString();
    }

    /**
     * For use in toString()
     * @see AbstractNAryPredicate#toString()
     * @return
     */
    protected String getDisplayedName() {
        return "All Of";
    }

}
