package tavant.twms.domain.orgmodel;

public enum UserType {
    INTERNAL("Internal"), DEALER("Dealer"), SYSTEM(
            "System");

    private String type;

    public static UserType typeFor(String type) {
        if (INTERNAL.type.equalsIgnoreCase(type)) {
            return INTERNAL;
        } else if (DEALER.type.equalsIgnoreCase(type)) {
            return DEALER;
        } else if (SYSTEM.type.equalsIgnoreCase(type)) {
            return SYSTEM;
        } else {
            throw new IllegalArgumentException("Cannot understand user type");
        }
    }

    private UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
