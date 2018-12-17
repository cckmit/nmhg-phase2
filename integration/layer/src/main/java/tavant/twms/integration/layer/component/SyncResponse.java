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

package tavant.twms.integration.layer.component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class SyncResponse {
    String businessId;
    boolean successful=true;
    String errorCode;
    String exception;
    
    String uniqueIdName;
    String uniqueIdValue;
    String errorType;
    private String businessUnitName;
    private Set<String> businessUnits;
	public static String ERROR_CODE_SYSTEM_ERROR = "SYSTEM_ERROR";
    public static String ERROR_CODE_VALIDATION_ERROR = "VALIDATION_ERROR";
    public static String ERROR_CODE_BUSINESS_PROCESS_ERROR  = "BUSINESS_PROCESS_ERROR";
    private String logicalId;
    private String task;
    private String referenceId;
    private String interfaceNumber;
    private String bodId;
    private Calendar creationDateTime;
    private Map<String, String> errorMessages;

	public static String SUCESS_MSG = "SUCCESS";
    public static String FAILURE_MSG = "FAILURE";

    
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

	public String getUniqueIdName() {
		return uniqueIdName;
	}

	public void setUniqueIdName(String uniqueIdName) {
		this.uniqueIdName = uniqueIdName;
	}

	public String getUniqueIdValue() {
		return uniqueIdValue;
	}

	public void setUniqueIdValue(String uniqueIdValue) {
		this.uniqueIdValue = uniqueIdValue;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

    public String getBusinessUnitName() {
        return businessUnitName;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }
    
    
    public Set<String> getBusinessUnits() {
		return businessUnits;
	}

	public void setBusinessUnits(Set<String> businessUnits) {
		this.businessUnits = businessUnits;
	}
	

    public String getLogicalId() {
		return logicalId;
	}

	public void setLogicalId(String logicalId) {
		this.logicalId = logicalId;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getInterfaceNumber() {
		return interfaceNumber;
	}

	public void setInterfaceNumber(String interfaceNumber) {
		this.interfaceNumber = interfaceNumber;
	}

	public String getBodId() {
		return bodId;
	}

	public void setBodId(String bodId) {
		this.bodId = bodId;
	}

    public Calendar getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Calendar creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

	public Map<String, String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(Map<String, String> errorMessages) {
		this.errorMessages = errorMessages;
	}
}
