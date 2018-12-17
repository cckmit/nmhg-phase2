package tavant.twms.external;

public class ExtWarrantyPriceCheckResponse extends ExtWarrantyRequest{
	
	private String statusCode;
	
	private String errorMessage;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
}
