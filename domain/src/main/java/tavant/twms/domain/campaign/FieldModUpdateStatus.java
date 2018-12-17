package tavant.twms.domain.campaign;

public enum FieldModUpdateStatus {

	   
	    SUBMITTED("Submitted"),
	    REJECTED("Rejected"),
	    ACCEPTED("Accepted");

	    private String status;

	    private FieldModUpdateStatus(String fieldModUpdateStatus) {
	        this.status = fieldModUpdateStatus;
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



