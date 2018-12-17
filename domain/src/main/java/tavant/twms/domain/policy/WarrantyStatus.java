package tavant.twms.domain.policy;

public enum WarrantyStatus {

    DRAFT("Draft"),
    DRAFT_DELETE("Draft Deleted"),
    SUBMITTED("Submitted"),
    FORWARDED("Forwarded"),
    REPLIED("Replied"),
    REJECTED("Rejected"),
    RESUBMITTED("Resubmitted"),
    DELETED("Deleted"),
    ACCEPTED("Accepted");

    private String status;

    private WarrantyStatus(String warrantyStatus) {
        this.status = warrantyStatus;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
