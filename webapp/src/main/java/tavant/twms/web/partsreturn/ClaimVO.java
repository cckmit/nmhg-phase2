/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.partsreturn;

import com.domainlanguage.time.CalendarDate;

import java.math.BigDecimal;

/**
 * @author Deepak.patel
 *
 */
public class ClaimVO {
	private String claimNumber;
	private String claimType;
	private String  fpiNumber;
	private String recoveryClaimNumber;
	private String machineSerialNumber;
	private String model;
	private String workOrderNumber;
	private String fault;
	private String cause;
	private String workPerformed;
	private String additionalNotes;
    private String failureDate;
    private String installDate;
    private BigDecimal hourOnMeter;
    private String problemDesc;
    private String dealerName;
    private String componentSerialNumber;
    private String brand;
    private BigDecimal hourOnPart;
    private String partFittedDate;
    private String shippingInstruction;
    private String supplierName;


	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/*public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}*/
	public String getAdditionalNotes() {
		return additionalNotes;
	}
	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}
	public String getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getMachineSerialNumber() {
		return machineSerialNumber;
	}
	public void setMachineSerialNumber(String machineSerialNumber) {
		this.machineSerialNumber = machineSerialNumber;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getWorkOrderNumber() {
		return workOrderNumber;
	}
	public void setWorkOrderNumber(String workOrderNumber) {
		this.workOrderNumber = workOrderNumber;
	}
	public String getFault() {
		return fault;
	}
	public void setFault(String fault) {
		this.fault = fault;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getWorkPerformed() {
		return workPerformed;
	}
	public void setWorkPerformed(String workPerformed) {
		this.workPerformed = workPerformed;
	}

    public String getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(String failureDate) {
        this.failureDate = failureDate;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public BigDecimal getHourOnMeter() {
        return hourOnMeter;
    }

    public void setHourOnMeter(BigDecimal hourOnMeter) {
        this.hourOnMeter = hourOnMeter;
    }



    public String getProblemDesc() {
        return problemDesc;
    }

    public void setProblemDesc(String problemDesc) {
        this.problemDesc = problemDesc;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getComponentSerialNumber() {
        return componentSerialNumber;
    }

    public void setComponentSerialNumber(String componentSerialNumber) {
        this.componentSerialNumber = componentSerialNumber;
    }
	/**
	 * @return the recoveryClaimNumber
	 */
	public String getRecoveryClaimNumber() {
		return recoveryClaimNumber;
	}
	/**
	 * @param recoveryClaimNumber the recoveryClaimNumber to set
	 */
	public void setRecoveryClaimNumber(String recoveryClaimNumber) {
		this.recoveryClaimNumber = recoveryClaimNumber;
	}
	/**
	 * @return the claimType
	 */
	public String getClaimType() {
		return claimType;
	}
	/**
	 * @param claimType the claimType to set
	 */
	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}
	/**
	 * @return the discripition
	 */
	/**
	 * @return the fpiNumber
	 */
	public String getFpiNumber() {
		return fpiNumber;
	}
	/**
	 * @param fpiNumber the fpiNumber to set
	 */
	public void setFpiNumber(String fpiNumber) {
		this.fpiNumber = fpiNumber;
	}
	/**
	 * @return the hourOnPart
	 */
	public BigDecimal getHourOnPart() {
		return hourOnPart;
	}
	/**
	 * @param hourOnPart the hourOnPart to set
	 */
	public void setHourOnPart(BigDecimal hourOnPart) {
		this.hourOnPart = hourOnPart;
	}
	/**
	 * @return the partFittedDate
	 */
	public String getPartFittedDate() {
		return partFittedDate;
	}
	/**
	 * @param partFittedDate the partFittedDate to set
	 */
	public void setPartFittedDate(String partFittedDate) {
		this.partFittedDate = partFittedDate;
	}
	/**
	 * @return the shippingInstruction
	 */
	public String getShippingInstruction() {
		return shippingInstruction;
	}
	/**
	 * @param shippingInstruction the shippingInstruction to set
	 */
	public void setShippingInstruction(String shippingInstruction) {
		this.shippingInstruction = shippingInstruction;
	}
	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}
	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}


}
