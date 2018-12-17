package tavant.twms.integration.adapter;

public class SummaryDTO {

	String syncType;

	long processed;

	long sucessful;

	long failed;

	long inProgress;

	long toBeProcessed;
	
	String status;
	
	long noOfRecords;
	
	public void addProcessed(long processed){
		this.processed=this.processed+processed;
	}

	public long getFailed() {
		return failed;
	}

	public void setFailed(long failed) {
		this.failed = failed;
	}

	public long getInProgress() {
		return inProgress;
	}

	public void setInProgress(long inProgress) {
		this.inProgress = inProgress;
	}

	public long getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(long noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	public long getProcessed() {
		return processed;
	}

	public void setProcessed(long processed) {
		this.processed = processed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getSucessful() {
		return sucessful;
	}

	public void setSucessful(long sucessful) {
		this.sucessful = sucessful;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	public long getToBeProcessed() {
		return toBeProcessed;
	}

	public void setToBeProcessed(long toBeProcessed) {
		this.toBeProcessed = toBeProcessed;
	}
	
	

}
