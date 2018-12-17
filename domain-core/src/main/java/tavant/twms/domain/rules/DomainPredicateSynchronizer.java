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

import java.util.List;

import org.hibernate.Session;

/**
 * @author radhakrishnan.j
 *
 */
public class DomainPredicateSynchronizer extends OGNLExpressionGenerator {
    private Session session;

    public DomainPredicateSynchronizer(Session session) {
        super();
        this.session = session;
    }

    @Override
    public void visit(All visitable) {
        super.visit(visitable);
        visitNAryPredicate(visitable);
    }

    @Override
    public void visit(Any visitable) {
        super.visit(visitable);
        visitNAryPredicate(visitable);
    }

    @Override
    public void visit(ForAnyNOf visitable) {
        super.visit(visitable);
        getLatestForCollectionUnaryOperator(visitable);
    }

    private void getLatestForCollectionUnaryOperator(
            AbstractCollectionUnaryPredicate visitable) {
        Predicate corePredicate = visitable.getConditionToBeSatisfied();
        Predicate latestFromStore = latestFromStoreIfPersisted(corePredicate);
        visitable.setConditionToBeSatisfied(latestFromStore);
    }

    @Override
    public void visit(ForAnyOf visitable) {
        super.visit(visitable);
        getLatestForCollectionUnaryOperator(visitable);
    }

    @Override
    public void visit(ForEachOf visitable) {
        super.visit(visitable);
        getLatestForCollectionUnaryOperator(visitable);
    }

    @Override
    public void visit(And visitable) {
        super.visit(visitable);
        visitable.setLhs( latestFromStoreIfPersisted(visitable.getLhs()));
        visitable.setRhs( latestFromStoreIfPersisted(visitable.getRhs()));
    }

    @Override
    public void visit(Or visitable) {
        super.visit(visitable);
        visitable.setLhs( latestFromStoreIfPersisted(visitable.getLhs()));
        visitable.setRhs( latestFromStoreIfPersisted(visitable.getRhs()));
    }

    protected void replaceWithLatestFromStore(List<Predicate> predicates) {
        int position=0;
        for(Predicate predicate : predicates ) {
            predicates.set(position, latestFromStoreIfPersisted(predicate));
            position++;
        }
    }

    protected Predicate latestFromStoreIfPersisted(Predicate predicate) {
        /*if( predicate instanceof DomainPredicate ) {
            return (DomainPredicate)session.get(DomainPredicate.class,
                    ((DomainPredicate)predicate).getId());

        } else {
            return predicate;
        }*/
    	// ToDo..We need to make sure session has latest object of Domain Predicate 
    	// or else we should fetch from DB
    	return predicate;
    }

    protected void visitNAryPredicate(NAryPredicate visitable) {
        List<Predicate> predicates = visitable.getPredicates();
        replaceWithLatestFromStore(predicates);
    }

}