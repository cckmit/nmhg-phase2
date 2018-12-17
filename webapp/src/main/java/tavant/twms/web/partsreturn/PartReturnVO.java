package tavant.twms.web.partsreturn;

import com.domainlanguage.time.CalendarDate;


public class PartReturnVO {

    private String partNumber ;

    private String description;

    private int numberOfParts;

    private String dueDate;

    private String receiverInstructions;
    
    private String shipperComments;
    

    private String rmaNumber;

    private String vendorPartNumber;

    private String problemPartNumber;

    private CalendarDate vendorRequestedDate;

    private String dueDateMessage;
    
    private String componentSerialNumber;
    
    private String shippingCommentsInClaim;
    
    private String shippingCommentsInRecClaim;
    
   
    
  
    
   
    

    

    public String getShippingCommentsInClaim() {
		return shippingCommentsInClaim;
	}

	public void setShippingCommentsInClaim(String shippingCommentsInClaim) {
		this.shippingCommentsInClaim = shippingCommentsInClaim;
	}

	public String getShippingCommentsInRecClaim() {
		return shippingCommentsInRecClaim;
	}

	public void setShippingCommentsInRecClaim(String shippingCommentsInRecClaim) {
		this.shippingCommentsInRecClaim = shippingCommentsInRecClaim;
	}

	public String getComponentSerialNumber() {
		return componentSerialNumber;
	}

	public void setComponentSerialNumber(String componentSerialNumber) {
		this.componentSerialNumber = componentSerialNumber;
	}

	private String wpraNumber;

    public String getRmaNumber() {
        return rmaNumber;
    }

    public void setRmaNumber(String rmaNumber) {
        this.rmaNumber = rmaNumber;
    }

    public PartReturnVO(String partNumber){
        this.partNumber=partNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfParts() {
        return numberOfParts;
    }

    public void setNumberOfParts(int numberOfParts) {
        this.numberOfParts = numberOfParts;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReceiverInstructions() {
        return receiverInstructions;
    }

    public void setReceiverInstructions(String receiverInstructions) {
        this.receiverInstructions = receiverInstructions;
    }

    public String getWpraNumber() {
        return wpraNumber;
    }

    public void setWpraNumber(String wpraNumber) {
        this.wpraNumber = wpraNumber;
    }

    public String getVendorPartNumber() {
        return vendorPartNumber;
    }

    public void setVendorPartNumber(String vendorPartNumber) {
        this.vendorPartNumber = vendorPartNumber;
    }

    public String getProblemPartNumber() {
        return problemPartNumber;
    }

    public void setProblemPartNumber(String problemPartNumber) {
        this.problemPartNumber = problemPartNumber;
    }

    public CalendarDate getVendorRequestedDate() {
        return vendorRequestedDate;
    }

    public void setVendorRequestedDate(CalendarDate vendorRequestedDate) {
        this.vendorRequestedDate = vendorRequestedDate;
    }

    public String getDueDateMessage() {
        return dueDateMessage;
    }

    public void setDueDateMessage(String dueDateMessage) {
        this.dueDateMessage = dueDateMessage;
    }

	/**
	 * @return the shipperComments
	 */
	public String getShipperComments() {
		return shipperComments;
	}

	/**
	 * @param shipperComments the shipperComments to set
	 */
	public void setShipperComments(String shipperComments) {
		this.shipperComments = shipperComments;
	}


}