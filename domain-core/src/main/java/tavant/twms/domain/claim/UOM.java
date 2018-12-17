package tavant.twms.domain.claim;

public enum UOM {
	KM("km"),
	MILES("miles"),
    EA("Each"),
    HR("HR");
	private String uom;
	
    private UOM(String uom) {
		this.uom = uom;
	}
    
	public String getUom() {
		return this.uom;
	}
	
	@Override
    public String toString() {
        return this.uom;
    }
}
