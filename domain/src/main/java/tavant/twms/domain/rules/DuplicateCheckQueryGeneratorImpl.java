package tavant.twms.domain.rules;

public class DuplicateCheckQueryGeneratorImpl extends DuplicateCheckQueryGenerator {

    @Override
    public String getQuery(Any any) {
        ClaimDuplicacyQueryGenerator queryGenerator = new ClaimDuplicacyQueryGenerator();
        queryGenerator.visit(any);
        return queryGenerator.getQuery();
    }

    @Override
    public String getQuery(All all) {
        ClaimDuplicacyQueryGenerator queryGenerator = new ClaimDuplicacyQueryGenerator();
        queryGenerator.visit(all);
        return queryGenerator.getQuery();
    }
}
