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
 * Date: Jun 26, 2007
 * Time: 12:19:49 PM
 */

package tavant.twms.domain.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractNAryPredicate implements NAryPredicate, Visitable {

    protected List<Predicate> predicates = new ArrayList<Predicate>();

    private boolean isForOneToOne;

    private DomainSpecificVariable oneToOneVariable;

    private boolean isQueryPredicate;

    public AbstractNAryPredicate(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    public AbstractNAryPredicate() {
    }

    public List<Predicate> getPredicates() {
        return this.predicates;
    }

    public void addPredicate(Predicate p) {
        this.predicates.add(p);
    }

    public boolean removePredicate(Predicate p) {
        return this.predicates.remove(p);
    }

    public boolean isQueryPredicate() {
        return this.isQueryPredicate;
    }

    public void setQueryPredicate(boolean queryPredicate) {
        this.isQueryPredicate = queryPredicate;
    }

    public abstract void accept(Visitor visitor);

    public abstract String getDomainTerm();

    public boolean isForOneToOne() {
        return this.isForOneToOne;
    }

    public void setForOneToOne(boolean forOneToOne) {
        this.isForOneToOne = forOneToOne;
    }

    public DomainSpecificVariable getOneToOneVariable() {
        return this.oneToOneVariable;
    }

    public void setOneToOneVariable(DomainSpecificVariable oneToOneVariable) {
        this.oneToOneVariable = oneToOneVariable;
    }

    public abstract Predicate getInverse();

    protected List<Predicate> getNegatedPredicates() {
        List<Predicate> negatePredicates = new ArrayList<Predicate>();
        for (Predicate p : this.predicates) {
            negatePredicates.add(p.getInverse());
        }
        return negatePredicates;
    }

    @Override
    public String toString() {
        StringBuffer text = new StringBuffer(50);
        text.append(getDisplayedName());
        text.append(" (");
        for (Iterator iter = this.predicates.iterator(); iter.hasNext();) {
            text.append(iter.next());
            if (iter.hasNext()) {
                text.append(",");
            }
        }
        text.append(")");

        return text.toString();
    }

    protected abstract String getDisplayedName();

    public List<Predicate> getLeafPredicates() {
        List<Predicate> leafPredicates = new ArrayList<Predicate>();
        for (Predicate predicate : this.predicates) {
        	// Start of Changes by jitesh.jain@tavant.com
			// This condition has been modified to handle multiple List objects
			// create OGNL expression accordingly
            if(predicate instanceof AbstractCollectionUnaryPredicate){
            	return this.predicates;
            // End Of changes	
            }else if (predicate instanceof NestablePredicate) {
                leafPredicates.addAll(((NestablePredicate) predicate).getLeafPredicates());
            } else {
                leafPredicates.add(predicate);
            }
        }
        return leafPredicates;
    }
}
