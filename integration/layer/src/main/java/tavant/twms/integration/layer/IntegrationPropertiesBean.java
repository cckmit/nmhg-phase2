package tavant.twms.integration.layer;

public class IntegrationPropertiesBean {

	private boolean creditSubmitEnabled;

	private boolean priceCheckEnabled;

	private boolean extWarrantyPriceCheckEnabled;

	private boolean extWarrantyDebitSubmitEnabled;

	private boolean supplierDebitSubmitEnabled;

	private String priceCheckURL;

	private String extWarrantyPriceCheckURL;

	private String extWarrantyDebitSubmitURL;

	private String supplierDebitSubmitURL;

	private String logicalIdForItalyClaimNotification;

	private String bodIdForItalyClaimNotification;
	
	private String taskIdForItalyClaimNotification;
	
	private String logicalIdForCreditSubmission;

	private String taskIdForCreditSubmission;
	
	private String bodIdForCreditSubmission;

	private String logicalId;

	private String referenceId;
	
	private String task;

	private String buName;

	private String namespace;

	private String priceChkMethod;
	private String priceChkInParam;
	private String priceChkOutParam;

	private String ewPriceChkMethod;
	private String ewPriceChkInParam;
	private String ewPriceChkOutParam;

	private String ewSubmitMethod;
	private String ewSubmitInParam;
	private String ewSubmitOutParam;

	private String supplierDebitMethod;
	private String supplierDebitInParam;
	private String supplierDebitOutParam;

	private String taskIdForTWMS;
	private String logicalIdForTWMS;

	private String twmsToIntegrationServerURL;
	private String integrationServerMethod;
	
	private String webmethodsUserName;
	private String webmethodsPassword;
	
	private String logicalIdForPriceFetch;
	private String bodIdIdForPriceFetch;
	private String taskIdForPriceFetch;
	private String priceFetchAciton;
	private String interfaceNumberForCreditSubmission;
	

	public boolean isCreditSubmitEnabled() {
		return creditSubmitEnabled;
	}

	public void setCreditSubmitEnabled(boolean creditSubmitEnabled) {
		this.creditSubmitEnabled = creditSubmitEnabled;
	}

	public boolean isPriceCheckEnabled() {
		return priceCheckEnabled;
	}

	public void setPriceCheckEnabled(boolean priceCheckEnabled) {
		this.priceCheckEnabled = priceCheckEnabled;
	}

	public boolean isExtWarrantyPriceCheckEnabled() {
		return extWarrantyPriceCheckEnabled;
	}

	public void setExtWarrantyPriceCheckEnabled(boolean extWarrantyPriceCheckEnabled) {
		this.extWarrantyPriceCheckEnabled = extWarrantyPriceCheckEnabled;
	}

	public boolean isExtWarrantyDebitSubmitEnabled() {
		return extWarrantyDebitSubmitEnabled;
	}

	public void setExtWarrantyDebitSubmitEnabled(boolean extWarrantyDebitSubmitEnabled) {
		this.extWarrantyDebitSubmitEnabled = extWarrantyDebitSubmitEnabled;
	}

	public boolean isSupplierDebitSubmitEnabled() {
		return supplierDebitSubmitEnabled;
	}

	public void setSupplierDebitSubmitEnabled(boolean supplierDebitSubmitEnabled) {
		this.supplierDebitSubmitEnabled = supplierDebitSubmitEnabled;
	}

	public String getPriceCheckURL() {
		return priceCheckURL;
	}

	public void setPriceCheckURL(String priceCheckURL) {
		this.priceCheckURL = priceCheckURL;
	}

	public String getExtWarrantyPriceCheckURL() {
		return extWarrantyPriceCheckURL;
	}

	public void setExtWarrantyPriceCheckURL(String extWarrantyPriceCheckURL) {
		this.extWarrantyPriceCheckURL = extWarrantyPriceCheckURL;
	}

	public String getExtWarrantyDebitSubmitURL() {
		return extWarrantyDebitSubmitURL;
	}

	public void setExtWarrantyDebitSubmitURL(String extWarrantyDebitSubmitURL) {
		this.extWarrantyDebitSubmitURL = extWarrantyDebitSubmitURL;
	}

	public String getSupplierDebitSubmitURL() {
		return supplierDebitSubmitURL;
	}

	public void setSupplierDebitSubmitURL(String supplierDebitSubmitURL) {
		this.supplierDebitSubmitURL = supplierDebitSubmitURL;
	}

	public String getLogicalId() {
		return logicalId;
	}

	public void setLogicalId(String logicalId) {
		this.logicalId = logicalId;
	}

	public String getLogicalIdForCreditSubmission() {
		return logicalIdForCreditSubmission;
	}

	public void setLogicalIdForCreditSubmission(String logicalIdForCreditSubmission) {
		this.logicalIdForCreditSubmission = logicalIdForCreditSubmission;
	}

	public String getTaskIdForCreditSubmission() {
		return taskIdForCreditSubmission;
	}

	public void setTaskIdForCreditSubmission(String taskIdForCreditSubmission) {
		this.taskIdForCreditSubmission = taskIdForCreditSubmission;
	}

	public String getBodIdForCreditSubmission() {
		return bodIdForCreditSubmission;
	}

	public void setBodIdForCreditSubmission(String bodIdForCreditSubmission) {
		this.bodIdForCreditSubmission = bodIdForCreditSubmission;
	}

	public String getLogicalIdForItalyClaimNotification() {
		return logicalIdForItalyClaimNotification;
	}

	public void setLogicalIdForItalyClaimNotification(
			String logicalIdForItalyClaimNotification) {
		this.logicalIdForItalyClaimNotification = logicalIdForItalyClaimNotification;
	}

	public String getBodIdForItalyClaimNotification() {
		return bodIdForItalyClaimNotification;
	}

	public void setBodIdForItalyClaimNotification(
			String bodIdForItalyClaimNotification) {
		this.bodIdForItalyClaimNotification = bodIdForItalyClaimNotification;
	}

	public String getTaskIdForItalyClaimNotification() {
		return taskIdForItalyClaimNotification;
	}

	public void setTaskIdForItalyClaimNotification(
			String taskIdForItalyClaimNotification) {
		this.taskIdForItalyClaimNotification = taskIdForItalyClaimNotification;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getBuName() {
		return buName;
	}

	public void setBuName(String buName) {
		this.buName = buName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getEwSubmitMethod() {
		return ewSubmitMethod;
	}

	public void setEwSubmitMethod(String ewSubmitMethod) {
		this.ewSubmitMethod = ewSubmitMethod;
	}

	public String getEwSubmitInParam() {
		return ewSubmitInParam;
	}

	public void setEwSubmitInParam(String ewSubmitInParam) {
		this.ewSubmitInParam = ewSubmitInParam;
	}

	public String getEwSubmitOutParam() {
		return ewSubmitOutParam;
	}

	public void setEwSubmitOutParam(String ewSubmitOutParam) {
		this.ewSubmitOutParam = ewSubmitOutParam;
	}

	public String getSupplierDebitMethod() {
		return supplierDebitMethod;
	}

	public void setSupplierDebitMethod(String supplierDebitMethod) {
		this.supplierDebitMethod = supplierDebitMethod;
	}

	public String getSupplierDebitInParam() {
		return supplierDebitInParam;
	}

	public void setSupplierDebitInParam(String supplierDebitInParam) {
		this.supplierDebitInParam = supplierDebitInParam;
	}

	public String getSupplierDebitOutParam() {
		return supplierDebitOutParam;
	}

	public void setSupplierDebitOutParam(String supplierDebitOutParam) {
		this.supplierDebitOutParam = supplierDebitOutParam;
	}

	public void setPriceChkMethod(String priceChkMethod) {
		this.priceChkMethod = priceChkMethod;
	}

	public void setPriceChkInParam(String priceChkInParam) {
		this.priceChkInParam = priceChkInParam;
	}

	public void setPriceChkOutParam(String priceChkOutParam) {
		this.priceChkOutParam = priceChkOutParam;
	}

	public void setEwPriceChkMethod(String ewPriceChkMethod) {
		this.ewPriceChkMethod = ewPriceChkMethod;
	}

	public void setEwPriceChkInParam(String ewPriceChkInParam) {
		this.ewPriceChkInParam = ewPriceChkInParam;
	}

	public void setEwPriceChkOutParam(String ewPriceChkOutParam) {
		this.ewPriceChkOutParam = ewPriceChkOutParam;
	}

	public String getPriceChkMethod() {
		return priceChkMethod;
	}

	public String getPriceChkInParam() {
		return priceChkInParam;
	}

	public String getPriceChkOutParam() {
		return priceChkOutParam;
	}

	public String getEwPriceChkMethod() {
		return ewPriceChkMethod;
	}

	public String getEwPriceChkInParam() {
		return ewPriceChkInParam;
	}

	public String getEwPriceChkOutParam() {
		return ewPriceChkOutParam;
	}

	public String getTaskIdForTWMS() {
		return taskIdForTWMS;
	}

	public void setTaskIdForTWMS(String taskIdForTWMS) {
		this.taskIdForTWMS = taskIdForTWMS;
	}
	
	public String getLogicalIdForTWMS() {
		return logicalIdForTWMS;
	}

	public void setLogicalIdForTWMS(String logicalIdForTWMS) {
		this.logicalIdForTWMS = logicalIdForTWMS;
	}

	public String getTwmsToIntegrationServerURL() {
		return twmsToIntegrationServerURL;
	}

	public void setTwmsToIntegrationServerURL(String twmsToIntegrationServerURL) {
		this.twmsToIntegrationServerURL = twmsToIntegrationServerURL;
	}

	public String getIntegrationServerMethod() {
		return integrationServerMethod;
	}

	public void setIntegrationServerMethod(String integrationServerMethod) {
		this.integrationServerMethod = integrationServerMethod;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getWebmethodsUserName() {
		return webmethodsUserName;
	}

	public void setWebmethodsUserName(String webmethodsUserName) {
		this.webmethodsUserName = webmethodsUserName;
	}

	public String getWebmethodsPassword() {
		return webmethodsPassword;
	}

	public void setWebmethodsPassword(String webmethodsPassword) {
		this.webmethodsPassword = webmethodsPassword;
	}
	public String getLogicalIdForPriceFetch() {
		return logicalIdForPriceFetch;
	}

	public void setLogicalIdForPriceFetch(String logicalIdForPriceFetch) {
		this.logicalIdForPriceFetch = logicalIdForPriceFetch;
	}

	public String getBodIdIdForPriceFetch() {
		return bodIdIdForPriceFetch;
	}

	public void setBodIdIdForPriceFetch(String bodIdIdForPriceFetch) {
		this.bodIdIdForPriceFetch = bodIdIdForPriceFetch;
	}

	public String getTaskIdForPriceFetch() {
		return taskIdForPriceFetch;
	}

	public void setTaskIdForPriceFetch(String taskIdForPriceFetch) {
		this.taskIdForPriceFetch = taskIdForPriceFetch;
	}

	public String getPriceFetchAciton() {
		return priceFetchAciton;
	}

	public void setPriceFetchAciton(String priceFetchAciton) {
		this.priceFetchAciton = priceFetchAciton;
	}

	public String getInterfaceNumberForCreditSubmission() {
		return interfaceNumberForCreditSubmission;
	}

	public void setInterfaceNumberForCreditSubmission(
			String interfaceNumberForCreditSubmission) {
		this.interfaceNumberForCreditSubmission = interfaceNumberForCreditSubmission;
	}
	
	
}
