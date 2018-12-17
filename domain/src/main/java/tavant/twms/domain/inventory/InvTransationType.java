package tavant.twms.domain.inventory;

public enum InvTransationType {

    IB("IB"), DR("DR"), ETR("TTR"), RMT("RMT"), DR_DELETE("DR_DELETE"),DR_MODIFY("DR_MODIFY"), 
    ETR_MODIFY("TTR_MODIFY"),ETR_DELETE("TTR_DELETE"),DR_RENTAL("DEALER RENTAL"),
    DEALER_TO_DEALER("D2D"),EXTENED_WNTY_PURCHASE("EXTWARPURCHASE"), DEMO("DEMO");

    private String transactionType;

    private InvTransationType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return this.transactionType;
    }
}
