package tavant.twms.domain.claim;

public enum MatchReadMultiplyFactor {

	OWNER_NAME(1),
	OWNER_CITY(1),
	OWNER_STATE(1),
	OWNER_ZIPCODE(1),
	OWNER_COUNTRY(1);
	
	private long multiplyFactor;

	private MatchReadMultiplyFactor(long multiplyFactor) {
		this.multiplyFactor = multiplyFactor;
	}

	public long getMultiplyFactor() {
		return this.multiplyFactor;
	}

	public void setMultiplyFactor(long multiplyFactor) {
		this.multiplyFactor = multiplyFactor;
	}

	@Override
    public String toString() {
        return String.valueOf(this.multiplyFactor);
    }
}
