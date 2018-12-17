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
package tavant.twms.domain.common;

import java.math.BigDecimal;

import tavant.twms.infra.BigDecimalFactory;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class AdminConstants {
	// AdminConstants for the purposes.
	public static final String PART_RETURNS_PURPOSE = "Part Returns";
	public static final String ITEM_PRICE_PURPOSE = "Item Pricing";
	public static final String MODIFIERS_PURPOSE = "Claim Payment Modifiers";
	public static final String DEALER_RATES_PURPOSE = "Dealer Rates";
	public static final String PRODUCT_STRUCT_PURPOSE = "PRODUCT STRUCTURE";
	public static final String ORGANISTAION_HIERARCHY_PURPOSE = "Organisation Hierarchy";
	public static final String DEALER_WATCHLIST = "Dealer Watchlist";
//	public static final String ITEM_RETURN_WATCHLIST = "Item Return Watchlist"; Use PART_RETURNS_PURPOSE instead
    public static final String ITEM_REVIEW_WATCHLIST = "Item Review Watchlist";
	public static final String STANDARD = "Standard Coverage";
	public static final String EXTENDED = "Extended Coverage";
	public static final String WARRANTY_COVERAGE_PURPOSE = "Warranty Coverage";
	public static final String TERRITORY_EXCLUSION="Territory Exclusion";
	public static final String ROUND_UP_JOB_CODE = "Round_Off-CC";
	public static final String FAIURE_REPORT_PURPOSE = "Failure Reports";
	public static final String DAYS_TO_REPAIR_PURPOSE = "Extended days for repair limit";
	public static final String DAYS_TO_FILE_CLAIM_PURPOSE = "Extended days for days to file limit";
	public static final String DEFECTIVE_RETURN = "Defective Return";
	public static final String INTERNAL = "INTERNAL";
	public static final String EXTERNAL = "EXTERNAL";
	public static final String DEALER_USER = "DEALER USER";	
	public static final String OEM = "OEM";
	public static final Long OEM_ID = 1L;
	public static final String PASSWORD = "tavant";
	public static final String UPLOAD_WARRANTY_CLAIM="UploadWarrantyClaim";
    public static final String WEBSERVICE="WebService";
    public static final String SUCCESS = "success";
	public static final String NONE = "none";
	public static final String JOB_CODE_ATTRIBUTES="JobCodeAttributes";
	public static final String PART_ATTRIBUTES="PartAttributes";
    public static final String CLAIM_ATTRIBUTES="ClaimAttributes";
	public static final String CLAIMED_ITEM_ATTRIBUTES="ClaimedItemAttributes";
	public static final String MARKET_TYPE="Market Type";
	public static final String MARKET_APPLICATION="Market Application";
	public static final String NATIONAL_ACCOUNT="NationalAccount";
	public static final String DEALER_RENTAL="DEALER RENTAL";
    public static final String ENTERPRISE_USER = "ENTERPRISE USER";
    public static final String CUSTOMER_TYPE_DEMO = "Demo";
    public static final String END_CUSTOMER = "EndCustomer";
    public static final String RECOVERY_CLAIM_STATE_CLOSED = "closed";
    public static final String RENTAL = "Rental";
    public static final String TRANSACTION_TYPE_DEALER_RENTAL = "DealerRental";
    public static final String DEMO = "Demo";
    public static final String ON_ACCEPT = "ON ACCEPT";
    public static final String ON_SUBMIT = "ON SUBMIT";
    public static final String CLAIM_DUPLICACY_RULES = "ClaimDuplicacyRules";
    public static final String SUPPLIER_USER = "SUPPLIER USER";
    public static final String DO_NOT_AUTO_INITIATE = "MANUAL";
    public static final String UPLOAD_FAILED_ERROR_CODE ="UPLOAD FAILED DUE TO A TECHNICAL PROBLEM";
    public static final String NMHGEMEA = "EMEA";
    public static final String NMHGAMERICA = "AMER";
	public static final String SSO = "DEALERSSO";
	public static final String INTEGRATION_ERRORS = "INTEGRATION_ERRORS";
	public static final String AESENCRYPTION = "AESENCRYPTION";	
	public static final String ACTIVE = "active";
	public static final String INACTIVE = "inactive";
    public static final String CUSTOMER = "CUSTOMER";
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String CMS = "CMSINFO";
	public static final String OEM_PARTS = "Oem Parts";
	public static final String NON_OEM_PARTS = "Non Oem Parts";
	public static final String LABOR = "Labor";
    public static final String TOTAL_AMOUNT = "Total Amount";
	public static final String PDC = "PDC";
	public static final String EPO = "EPO";
	public static final String WARRANTYCLAIM="WarrantyClaim";
	public static final String RECOVERYCLAIM="RecoveryClaim";
	public static final String FLEETCLAIM="FleetClaim";
	public static final String PRIMARY="YES";
	public static final String SECONDARY="NO";
	public static final String LATIN_AMERICAN_DEALERS="Latin American Dealers";
	public static final String PRODUCT_CERTIFICATE_LEVEL="PR";
	public static final String CORE_CERTIFICATE_LEVEL="CO";
	public static final String NMHGAMER = "AMER";
	public static final BigDecimal CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE = BigDecimalFactory.bigDecimalOf(85);
	public static final BigDecimal NON_CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE = BigDecimalFactory.bigDecimalOf(80);
	public static final String FULL_SERVICE_CONTRACT = "NA";
	public static final String DISPUTED_FOR_INITIAL_RESPONSE = "Disputed for Initial Response";
	public static final String DISPUTED_FOR_FINAL_RESPONSE = "Disputed for Final Response";
	public static final String ITDR = "ITDR";
	public static final String PDI = "PDI";
	public static final String AUTHORIZATION = "AUTHORIZATION";
	public static final String ETR = "ETR";
	public static final String CUSTOMER_USER="CUSTOMER USER";
	public static final String DELETED="DELETED";
	public static final String COMPLETED="COMPLETED";
	public static final String NMHG_ONLY="Only NMHG";
	public static final String DEALER_OWNED="Dealer Owned";
	public static final String BOTH="Both";
	
	
}
