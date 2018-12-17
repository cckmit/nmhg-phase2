package tavant.twms.domain.rules;

import tavant.twms.domain.common.AdminConstants;

import static tavant.twms.domain.common.AdminConstants.ITEM_REVIEW_WATCHLIST;

public class WarrantyPredicateEvaluationActionFactory extends PredicateEvaluationActionFactory {

    public PredicateEvaluationAction getActionForBelongsTo(DomainSpecificVariable domainVariable, Value value) {
        BelongsToAction action = new BelongsToAction();
        action.setDomainVariable(domainVariable);
        action.setWatchListName(value);
        return action;
    }

    public PredicateEvaluationAction getActionForDoesNotBelongTo(DomainSpecificVariable domainVariable, Value value) {
        BelongsToAction action = new BelongsToAction();
        action.setDomainVariable(domainVariable);
        action.setWatchListName(value);
        return action;
    }

    public PredicateEvaluationAction getActionForIsAWatchedPart(DomainSpecificVariable domainVariable) {
        PartWatchAction action = new PartWatchAction();
        action.setDomainVariable(domainVariable);
        return action;
    }

    public PredicateEvaluationAction getActionForIsAReturnWatchedPart(DomainSpecificVariable domainVariable) {
        PartWatchAction action = new PartWatchAction();
        action.setDomainVariable(domainVariable);
        action.setItemWatchListType(AdminConstants.PART_RETURNS_PURPOSE);
        return action;
    }

    public PredicateEvaluationAction getActionForIsAReviewWatchedPart(DomainSpecificVariable domainVariable) {
        PartWatchAction action = new PartWatchAction();
        action.setDomainVariable(domainVariable);
        action.setItemWatchListType(ITEM_REVIEW_WATCHLIST);
        return action;
    }

    public PredicateEvaluationAction getActionForIsAWatchedDealership(DomainSpecificVariable domainVariable) {
        DealershipWatchAction action = new DealershipWatchAction();
        action.setDomainVariable(domainVariable);
        return action;
    }
}
