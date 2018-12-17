package tavant.twms.domain.claim.payment;

public enum AdditionalPaymentType {
	ACCEPTED_FOR_CP("acceptedForCp"),
	ACCEPTED_FOR_WNTY("acceptedForWnty");
	
	private String type;
    
	private AdditionalPaymentType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
