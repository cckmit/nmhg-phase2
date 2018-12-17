package tavant.twms.domain.rules;

public abstract class DuplicateCheckQueryGenerator {

    public static DuplicateCheckQueryGenerator instance;

    protected DuplicateCheckQueryGenerator() {
        instance = this;
    }

    public static DuplicateCheckQueryGenerator getInstance() {
        return instance;
    }

    public abstract String getQuery(Any any);

    public abstract String getQuery(All all);
}
