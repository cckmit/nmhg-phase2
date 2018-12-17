package tavant.twms.integration.adapter;

public class ErrorSummaryDTO {

	Long id;
	
	String errorMessage;

	long numberOfRecords;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
