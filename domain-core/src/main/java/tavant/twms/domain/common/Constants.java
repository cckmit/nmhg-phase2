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

/**
 * @author aniruddha.chaturvedi
 *
 */
public class Constants {
	//Constants for the purposes. 
	public static final String PART_RETURNS_PURPOSE = "Part Returns";
	public static final String ITEM_PRICE_PURPOSE = "Item Pricing";
	public static final String MODIFIERS_PURPOSE = "Claim Payment Modifiers";
	public static final String DEALER_RATES_PURPOSE = "Dealer Rates";
	public static final String PRODUCT_STRUCT_PURPOSE = "PRODUCT STRUCTURE";
	public static final String PARTS_INVENTORY_PURPOSE = "Parts Inventory";
	public static final String CLAIM_ASSIGNMENT_PURPOSE = "Claim Assignment";
    public static final String DSM_ASSIGNMENT_PURPOSE = "DSM Assignment";
    public static final String DSM_ADVISOR_ASSIGNMENT_PURPOSE = "DSM Advisor Assignment";
	public static final String CP_ADVISOR_ASSIGNMENT_PURPOSE = "CP Advisor Assignment";
	public static final Long IS_EXCEL_PARSED = 1L;
	public static final String WARNING_IMG_FOR_STOCK_INV = "warningImgForStockInv";
	public static final String WARNING_IMG_FOR_SERVICE_CALL = "warningImgForServiceCall";
	public static final String WARNING_IMG_FOR_CAMPAIGN = "warningImgForCampaign";
	public static final String IMG_FOR_INVENTORY_LABELS = "ImgForInventoryLabels";
	public static final String IMG_FOR_SERVICE_CODE_LABELS = "ImgForServiceCodeLabels";
	public static final String IMG_FOR_FLEET_CUSTOMER_LABELS = "ImgForFleetCustomerLabels";
	public static final String PLATINUM = "Platinum";
	public static final String GOLD = "Gold";
	public static final String SILVER = "Silver";//spelling as 'sliver' since stored in db as this only...
	public static final String BRONZE = "Bronze";
    public static final String MARKET="Market";
    public static final String MARKET_TYPE="Market Type";
    public static final String MARKET_APPLICATION="Market Application";
    public static final String INVALID_ITEM_NO_WARRANTY = "INVALID_ITEM_NO_WARRANTY";
    public static final String VALID_ITEM_STOCK = "VALID_ITEM_STOCK";
    public static final String VALID_ITEM_NO_WARRANTY = "VALID_ITEM_NO_WARRANTY";
    public static final String VALID_ITEM_OUT_OF_WARRANTY = "VALID_ITEM_OUT_OF_WARRANTY";
    public static final String INTERNAL = "INTERNAL";
    
    public static final String USER_TYPE_INTERNAL=  "INTERNAL";
    public static final String USER_TYPE_EXTERNAL=  "EXTENRAL";
    public static final String USER_TYPE_SYSTEM=  "SYSTEM";

    public static final String RULE_ADMIN_ASSIGN_RULES_STATE = "assign";
    public static final String RULE_ADMIN_NOT_ASSIGN_RULES_STATE = "not Assign";
    public static final String RULE_ADMIN_ASSIGN_TO_LOA_SCHEME = "assign To Loa Scheme";
    
    // Supplier Recovery inbox view constants (CR TKTSA-923)
	public static final String NEW = "New";
	public static final String ACCEPTED = "Accepted";
	public static final String DISPUTED = "Disputed";
	public static final String CONFIRM_PART_RETURNS = "Confirm Part Returns";
	public static final String REOPENED_CLAIMS = "Reopened Claims";
	public static final String FOR_RECOVERY = "For Recovery";
	public static final String AWAITING_SUPPLIER_RESPONSE ="Awaiting Supplier Response";
	public static final String NOT_FOR_RECOVERY_REQUEST = "Not For Recovery Request";
	public static final String NOT_FOR_RECOVERY_RESPONSE = "Not For Recovery Response";
	public static final String ON_HOLD = "On Hold";
	public static final String READY_FOR_DEBIT = "Ready For Debit";
	public static final String SUPPLIER_ACCEPTED = "Supplier Accepted";
	public static final String SUPPLIER_RESPONSE = "Supplier Response";
	public static final String TRANSFERRED = "Transferred";
	public static final String AWAITING_SHIPMENT = "Awaiting Shipment";
	public static final String AWAITING_SHIPMENT_TO_WAREHOUSE = "Awaiting Shipment To WareHouse";
	public static final String PART_NOT_IN_WAREHOUSE = "Part Not In Warehouse";
	public static final String SUPPLIER_PARTS_CLAIMED = "Supplier Parts Claimed";
	public static final String SUPPLIER_PARTS_SHIPPED = "Supplier Parts Shipped";
	public static final String SUPPLIER_SHIPMENT_GENERATED = "Supplier Shipment Generated";
    public static final String NOT_FOR_RECOVERY = "Not For Recovery";
    public static final String CONFIRM_DEALER_PART_RETURNS = "Confirm Dealer Part Returns";
    public static final String SHIPMENT_GENERATED = "Shipment Generated";
    public static final String VPRA_GENERATED = "VPRA Generated";
    public static final String REOPENED = "Reopened";
    public static final String SUPPLIER_PARTS_RECEIPT = "Supplier Parts Receipt";
    public static final String SUPPLIER_PARTS_INSPECTION = "Supplier Parts Inspection";
    public static final String PARTS_FOR_RETURN_TO_NMHG = "Parts for Return To NMHG";
    public static final String SHIPMENT_GENERATED_TO_NMHG = "Shipment Generated To NMHG";
    public static final String PARTS_SHIPPED_TO_NMHG = "Parts Shipped to NMHG";
    public static final String ON_HOLD_FOR_PART_RETURN = "On Hold For Part Return";
    public static final String ROUTED_TO_NMHG = "Routed to NMHG";
    public static final String FLAG_FOR_2ND_RECOVERY = "FLAG_FOR_2ND_RECOVERY";


}
