/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.domain.upload.controller;

/**
 * @author jhulfikar.ali
 *
 */
public class DataUploadConfig {

	private int uploadRecordsLimit;
	
	private int exportRecordsLimit;
	
	private boolean exclusiveLockingMode;
	
	private String schemaName;
	
	public String tempLocation;

	public int getUploadRecordsLimit() {
		return uploadRecordsLimit;
	}

	public void setUploadRecordsLimit(int uploadRecordsLimit) {
		this.uploadRecordsLimit = uploadRecordsLimit;
	}

	public int getExportRecordsLimit() {
		return exportRecordsLimit;
	}

	public void setExportRecordsLimit(int exportRecordsLimit) {
		this.exportRecordsLimit = exportRecordsLimit;
	}

	public boolean isExclusiveLockingMode() {
		return exclusiveLockingMode;
	}

	public void setExclusiveLockingMode(boolean exclusiveLockingMode) {
		this.exclusiveLockingMode = exclusiveLockingMode;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getTempLocation() {
		return tempLocation;
	}

	public void setTempLocation(String tempLocation) {
		this.tempLocation = tempLocation;
	}
	
}
