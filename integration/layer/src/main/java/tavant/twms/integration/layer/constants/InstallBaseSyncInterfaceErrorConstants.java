package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class InstallBaseSyncInterfaceErrorConstants {

	public final static String I001 = "I001";
	public final static String I002 = "I002";
	public final static String I003 = "I003";
	public final static String I004 = "I004";
	public final static String I005 = "I005";
	public final static String I006 = "I006";
	public final static String I007 = "I007";
	public final static String I008 = "I008";
	public final static String I009 = "I009";
	public final static String I0010 = "I0010";
	public final static String I0011 = "I0011";
	public final static String I0012 = "I0012";
	public final static String I0013 = "I0013";
	public final static String I0014 = "I0014";
	public final static String I0015 = "I0015";
	public final static String I0016 = "I0016";
	public final static String I0017 = "I0017";
	public final static String I0018 = "I0018";
	public final static String I0019 = "I0019";
	public final static String I0020 = "I0020";
	public final static String I0021 = "I0021";
	public final static String I0022 = "I0022";
	public final static String I0023 = "I0023";
	public final static String I0024 = "I0024";
	public final static String I0025 = "I0025";
	public final static String I0026 = "I0026";
	public final static String I0027 = "I0027";
	public final static String I0028 = "I0028";
	public final static String I0029 = "I0029";
	public final static String I0030 = "I0030";
	public final static String I0031 = "I0031";
	public final static String I0032 = "I0032";
	public final static String I0033 = "I0033";
	public final static String I0034 = "I0034";
	public final static String I0035 = "I0035";
	public final static String I0036 = "I0036";
	public final static String I0037 = "I0037";
	public final static String I0038 = "I0038";
	public final static String I0039 = "I0039";
	public final static String I0040 = "I0040";
	public final static String I0041 = "I0041";
	public final static String I0042 = "I0042";
	public final static String I0043 = "I0043";
	public final static String I0044 = "I0044";
	public final static String I0045 = "I0045";
	public final static String I0046 = "I0046";
	public final static String I0047 = "I0047";
	public final static String I0048 = "I0048";
	public final static String I0049 = "I0049";
	public final static String I0050 = "I0050";
	public final static String I0051 = "I0051";
	public final static String I0052 = "I0052";
	public final static String I0053 = "I0053";
	public final static String I0054 = "I0054";
	public final static String I0055 = "I0055";
	public final static String I0056 = "I0056";
	public final static String I0057 = "I0057";
	public final static String I0058 = "I0058";
	public final static String I0059 = "I0059";
	public final static String I0060 = "I0060";
	public final static String I0061 = "I0061";
	public final static String I0062 = "I0062";
	public final static String I0063 = "I0063";
	public static final String I0064 = "I0064";
	public static final String I0065 = "I0065";
	public static final String I0066 = "I0066";
	public static final String I0067 = "I0067";
	public static final String I0068 = "I0068";
	
	private final static Map<String, String> errorCodeMap = new HashMap<String, String>();
	

	private I18nDomainTextReader i18nDomainTextReader;

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}

	static {
		/**
		 * General Item Error Codes
		 */
		errorCodeMap.put(I001,"installbaseAPI.nullCheck.buName");
		errorCodeMap.put(I002,"installbaseAPI.nullCheck.syncType");
		errorCodeMap.put(I003,"installbaseAPI.nullCheck.itemNumber");
		errorCodeMap.put(I004,"installbaseAPI.error.itemType");
		errorCodeMap.put(I005,"installbaseAPI.error.serialNumber");
		errorCodeMap.put(I006,"installbaseAPI.error.mktGroupCode");
		errorCodeMap.put(I007,"installbaseAPI.error.shipFromOrgCode");
		errorCodeMap.put(I008,"installbaseAPI.error.deliveryDateTime");
		errorCodeMap.put(I009,"installbaseAPI.error.registrationDate");
		errorCodeMap.put(I0010,"installbaseAPI.error.billToPurchas");
		errorCodeMap.put(I0011,"installbaseAPI.error.orderReceivedDate");
		errorCodeMap.put(I0012,"installbaseAPI.error.actualCTSDate");
		errorCodeMap.put(I0013,"installbaseAPI.error.orderType");
		errorCodeMap.put(I0014,"installbaseAPI.error.mdeCapacity");
		errorCodeMap.put(I0016,"installbaseAPI.error.buildPlant");
		errorCodeMap.put(I0015,"installbaseAPI.error.modelPower");
		errorCodeMap.put(I0017,"installbaseAPI.error.customerInfo");
		errorCodeMap.put(I0018,"installbaseAPI.error.customerNumber");
		errorCodeMap.put(I0019,"installbaseAPI.error.customerFirstName");
		errorCodeMap.put(I0020,"installbaseAPI.error.addressLine1");
		errorCodeMap.put(I0021,"installbaseAPI.error.customerCity");
		errorCodeMap.put(I0022,"installbaseAPI.error.customerState");
		errorCodeMap.put(I0023,"installbaseAPI.error.customerCountry");
		errorCodeMap.put(I0024,"installbaseAPI.error.customerSICode");
		errorCodeMap.put(I0025,"installbaseAPI.error.shipToNumber");
		errorCodeMap.put(I0026,"installbaseAPI.error.invalid.shipToNumber");
		errorCodeMap.put(I0027,"installbaseAPI.error.shipToLocation");
		errorCodeMap.put(I0028,"installbaseAPI.error.invalid.shipToLocation");
		errorCodeMap.put(I0029,"installbaseAPI.error.sequenceNumber");
		errorCodeMap.put(I0030,"installbaseAPI.error.serialType");
		errorCodeMap.put(I0031,"installbaseAPI.error.serialTypeDescription");
		errorCodeMap.put(I0032,"installbaseAPI.error.manufacturer");
		errorCodeMap.put(I0033,"installbaseAPI.error.componentPartSerialNumber");
		errorCodeMap.put(I0034,"installbaseAPI.error.componentPartNumber");
		errorCodeMap.put(I0035,"installbaseAPI.error.optionCode");
		errorCodeMap.put(I0036,"installbaseAPI.error.optionLineNumber");
		errorCodeMap.put(I0037,"installbaseAPI.error.optionType");
		errorCodeMap.put(I0038,"installbaseAPI.error.optionDescription");
		errorCodeMap.put(I0039,"installbaseAPI.error.activeInactiveStatus");
		errorCodeMap.put(I0040,"installbaseAPI.error.partGroupCode");
		errorCodeMap.put(I0041,"installbaseAPI.error.partGroupDescription");
		errorCodeMap.put(I0042,"installbaseAPI.error.partQuantity");
		errorCodeMap.put(I0043,"installbaseAPI.error.partStandardCost");
		errorCodeMap.put(I0044,"installbaseAPI.error.item.notFound");
		errorCodeMap.put(I0045,"installbaseAPI.error.inventoryItem.notFound");
		errorCodeMap.put(I0046,"installbaseAPI.error.serviceProvider.notFound");
		errorCodeMap.put(I0047,"installbaseAPI.error.serialNumber.already.exist");
		errorCodeMap.put(I0048,"installbaseAPI.error.serialNumber.already.exist.for.rmi");
		errorCodeMap.put(I0049,"installbaseAPI.error.party.not.exist");
		errorCodeMap.put(I0050,"installbaseAPI.error.creating.plan");
		errorCodeMap.put(I0051,"installbaseAPI.error.source.warehouse.not.exist");
		errorCodeMap.put(I0052,"installbaseAPI.error.billToNumber.not.exist");
		errorCodeMap.put(I0053,"installbaseAPI.error.duplicate.component");
		errorCodeMap.put(I0054,"installbaseAPI.error.failed.dR");
		errorCodeMap.put(I0055,"installbaseAPI.error.dealer.does.not.exist");
		errorCodeMap.put(I0056,"installbaseAPI.error.null.salesOrderNumber");
		errorCodeMap.put(I0057,"installbaseAPI.error.null.brandType");
		errorCodeMap.put(I0058,"installbaseAPI.error.runtime.exception");
		errorCodeMap.put(I0059,"installbaseAPI.nullCheck.itemProductCode");
		errorCodeMap.put(I0060,"installbaseAPI.nullCheck.itemModelCode");
		errorCodeMap.put(I0061,"installbaseAPI.error.product.notFound");
		errorCodeMap.put(I0062,"installbaseAPI.error.invalid.date.time");
		errorCodeMap.put(I0064,"installbaseAPI.error.installing.dealer.mandatory");
		errorCodeMap.put(I0065,"installbaseAPI.error.installing.dealer.not.exists");
		errorCodeMap.put(I0066,"installbaseAPI.error.service.provider.null");
		errorCodeMap.put(I0067,"installbaseAPI.error.customer.county.null");
		errorCodeMap.put(I0068,"installbaseAPI.error.retailed.machine.sale");
	}

	public String getErrorMessage(final String errorCode) {
		String messageKey = errorCodeMap.get(errorCode);
		return messageKey;
	}
	
	public String getPropertyMessage(final String messageKey) {
		return i18nDomainTextReader.getProperty(messageKey);
	}

	public String getPropertyMessageFromErrorCode(final String errorCode) {
		if(StringUtils.hasText(errorCode)){
			return i18nDomainTextReader.getProperty(errorCodeMap.get(errorCode).trim());
		}
		return null;
	}
	public String getPropertyMessage(final String errorCode, String[] args) {
		return i18nDomainTextReader.getText(getErrorMessage(errorCode), args);
	}

}
