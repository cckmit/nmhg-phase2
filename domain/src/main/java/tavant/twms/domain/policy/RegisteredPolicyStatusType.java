package tavant.twms.domain.policy;

public enum RegisteredPolicyStatusType {
    ACTIVE("Active"), INACTIVE("InActive"), SUSPENDED("Suspended"), TERMINATED("Terminated"),INPROGRESS("InProgress"),
    EXPIRED("Expired");

    private final String status;

    private RegisteredPolicyStatusType(String status) {
        this.status = status;
    }

    public static RegisteredPolicyStatusType typeFor(String type) {
        if (ACTIVE.status.equals(type)) {
            return ACTIVE;
        } else if (INACTIVE.status.equals(type)) {
            return INACTIVE;
        } else if (SUSPENDED.status.equals(type)) {
            return SUSPENDED;
        } else if (TERMINATED.status.equals(type)) {
            return TERMINATED;
        } else if (INPROGRESS.status.equals(type)) {
            return INPROGRESS;
        }
        else if (EXPIRED.status.equals(type)) {
            return EXPIRED;
        }
        else {
            throw new IllegalArgumentException("Unrecognized type");
        }
    }


    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return this.status;
    }

}
