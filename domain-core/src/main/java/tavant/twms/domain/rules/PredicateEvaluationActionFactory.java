package tavant.twms.domain.rules;

public abstract class PredicateEvaluationActionFactory {

    public static PredicateEvaluationActionFactory instance;

    public static PredicateEvaluationActionFactory getInstance() {
        return instance;
    }

    public PredicateEvaluationActionFactory() {
        instance = this;
    }

    public abstract PredicateEvaluationAction getActionForBelongsTo(DomainSpecificVariable domainVariable, Value value);

    public abstract PredicateEvaluationAction getActionForDoesNotBelongTo(DomainSpecificVariable domainVariable, Value value);

    public abstract PredicateEvaluationAction getActionForIsAWatchedPart(DomainSpecificVariable domainVariable);

    public abstract PredicateEvaluationAction getActionForIsAReturnWatchedPart(DomainSpecificVariable domainVariable);

    public abstract PredicateEvaluationAction getActionForIsAReviewWatchedPart(DomainSpecificVariable domainVariable);

    public abstract PredicateEvaluationAction getActionForIsAWatchedDealership(DomainSpecificVariable domainVariable);
}
