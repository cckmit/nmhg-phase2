package tavant.twms.domain.policy;

public enum AdditionalMarketingInfoType {

    DropDown("DROPDOWN"), 
    FreeText("FREETEXT"),
    Number("NUMBER"),
    String("STRING"),
    Year("YEAR");

	private String type;

	private AdditionalMarketingInfoType(String type) {
	    this.type = type;
	}

	public static AdditionalMarketingInfoType typeFor(String type) {
	    if (DropDown.type.equals(type)) {
	        return DropDown;
	    } else if (FreeText.type.equals(type)) {
	        return FreeText;
	    } else if (Number.type.equals(type)) {
	        return Number;    
	    } else if (String.type.equals(type)) {
	        return String;
	    } else if (Year.type.equals(type)) {
	        return Year;
	    } else {
	        throw new IllegalArgumentException("Cannot understand the Additional Marketing Info Type");
	    }
	}

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
