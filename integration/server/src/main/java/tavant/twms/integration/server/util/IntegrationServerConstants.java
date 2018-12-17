package tavant.twms.integration.server.util;

import java.util.regex.Pattern;

public interface IntegrationServerConstants {
	String CREDIT_SUBMISSION_JOB_UNIQUE_IDENTIFIER = "Claim";
	String ITEM_SYNC_JOB_UNIQUE_IDENTIFIER = "Item";
	String CUSTOMER_SYNC_JOB_UNIQUE_IDENTIFIER = "Customer";
	String INSTALLBASE_SYNC_JOB_UNIQUE_IDENTIFIER = "InstallBase";
	String TECHNICIAN_SYNC_JOB_UNIQUE_IDENTIFIER = "Technician";
	String BATCH_CLAIM_SYNC_JOB_UNIQUE_IDENTIFIER = "BatchClaim";
	String BOOKING_SYNC_JOB_UNIQUE_IDENTIFIER = "Bookings";
	String INSTALLATION_SYNC_JOB_UNIQUE_IDENTIFIER = "Installation";
	String CREDITNOTIFICATION_SYNC_JOB_UNIQUE_IDENTIFIER = "WarrantyClaimCreditNotification";
	String COMMON_SYNC_JOB_UNIQUE_IDENTIFIER = "Customer:WarrantyClaimCreditNotification:InstallBase";
	String ITALY_QA_NOTIFICATION_JOB_UNIQUE_IDENTIFIER = "ItalyClaim";
	String ITALY_QA_NOTIFICATION_JOB_STATUS = "SUCCESS";
	String PROCESSING_STATUS_FAILURE = "FAILURE";
	//to avoide creating axis2 folders in temp folder we need to point the temp location to inavlid location .
	String AXIS2_TEMP_LOCATION = "Default Location";
	String NMHG_EMEA = "EMEA";
	String NMHG_US = "AMER";
	String DIVISON_CODE_US = "003";
	String DIVISON_CODE_EMEA = "056";
	Pattern BU_NAME_REGEX = Pattern.compile("(<BUName>)(.*?)(</BUName>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	Pattern DIVISION_CODE_REGEX = Pattern.compile(
			"(<DivisionCode>)(.*?)(</DivisionCode>)", Pattern.DOTALL
					| Pattern.CASE_INSENSITIVE);
	Pattern RESPONSE_CODE_PATTERN = Pattern.compile("<status>(.*?)</status>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Pattern ERROR_MESSAGE_PATTERN = Pattern.compile(
			"<errorMessage>(.*?)</errorMessage>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);
	String CURRENCY_EXCHANGE_RATE = "CurrencyExchangeRate";

}
