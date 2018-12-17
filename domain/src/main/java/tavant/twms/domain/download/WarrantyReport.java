package tavant.twms.domain.download;

public interface WarrantyReport {

	public static final String DOWNLOAD_CONTEXT_CLAIM_DATA = "ClaimData";
	
	public static final String DOWNLOAD_CONTEXT_CLAIM_DETAIL_DATA = "ClaimDetailData";

	public static final String DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA = "ClaimPartsData";

	public static final String DOWNLOAD_CONTEXT_EXT_WNTY_CLAIM_DATA = "ExtWntyClaimData";
	
	public static final String DOWNLOAD_CONTEXT_RECOVERY_REPORT = "RecoveryReport";
	
	public static final String DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT = "RecoveryPartsReport";

	public static final String DOWNLOAD_CONTEXT_UNDERWRITER_CLAIM_DATA = "UnderwriterClaimData";
	
	public static final String DOWNLOAD_CONTEXT_MACHINE_RETAIL_REPORT = "MachineRetailReport";
	
	public static final String DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT = "ClaimFinancialReportData";
	
	public static final String DOWNLOAD_CONTEXT_EWP_REPORT = "EWPReport";
	
	public static final String DOWNLOAD_CONTEXT_CUSTOMER_DATA = "CustomerData";
	
	public static final String DOWNLOAD_CONTEXT_PENDING_EXTENSIONS = "PendingExtensions";

	public static final String TYPE_OF_DOWNLOAD_MACHINE_RETAIL = "MachineRetail";
	
	public static final String TYPE_OF_DOWNLOAD_UNDER_WRITER_CLAIM = "UnderWriterData";
	
	public static final String TYPE_OF_DOWNLOAD_EXT_WNTY_CLM_PARTS = "ExtWarrantyClaimParts";
	
	public static final String NO_RECORD_FOUND = "There is no record found for the given Criteria.";
	
	public static final String EMPTY_STR = "";

	public static final String DELIMITER_CSV = ",";
	
	public static final String SQL_APPEND_STR = "'";
	
	public static final String SQL_CONCAT_STR = "|| ";
	
	public static final String DEFAULT_CURRENCY_FOR_CLAIM_FINANCIAL_REPORT = "USD"; 

}