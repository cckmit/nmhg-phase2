package tavant.twms.integration.layer.constants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18nDomainTextReader;

public class ItemSyncInterfaceErrorConstants {

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
	public final static String I0045 = "I0045";
	public final static String I0046 = "I0046";
	public final static String I0047 = "I0047";
	public final static String I0048 = "I0048";
	public final static String I0049 = "I0049";
	public final static String I0050 = "I0050";
	public final static String I0051 = "I0051";
	public final static String I0052 = "I0052";
	public final static String I0053 = "I0053";
	
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
		errorCodeMap.put(I001,"itemAPI.nullCheck.itemNumber");
		errorCodeMap.put(I002,"itemAPI.nullCheck.itemType");
		errorCodeMap.put(I003,"itemAPI.nullCheck.itemStatus");
		errorCodeMap.put(I004,"itemAPI.nullCheck.itemDesciption");
		errorCodeMap.put(I005,"itemAPI.nullCheck.itemManufacturer");
		errorCodeMap.put(I006,"itemAPI.nullCheck.itemDivisionCode");
		errorCodeMap.put(I007,"itemAPI.nullCheck.itemProductCode");
		errorCodeMap.put(I008,"itemAPI.nullCheck.itemModelCode");
		errorCodeMap.put(I009,"itemAPI.nullCheck.itemSeriesStatusEnabled");
		errorCodeMap.put(I0010,"itemAPI.itemType.unSupported");
		errorCodeMap.put(I0011,"itemAPI.error.itemSyncResponse");
		errorCodeMap.put(I0012,"itemAPI.error.reasonForItemSync");
		errorCodeMap.put(I0013,"itemAPI.nullCheck.itemTypeSupplier");
		errorCodeMap.put(I0014,"itemAPI.supplierNumber.notFound");
		errorCodeMap.put(I0015,"itemAPI.alreadyExists.seriesCode");
		errorCodeMap.put(I0016,"itemAPI.invalid.divisionCodeinSLMS");
		errorCodeMap.put(I0017,"itemAPI.exception.connectionTimeOut");
		errorCodeMap.put(I0018,"itemAPI.itemType.canNotBeChanged");
		errorCodeMap.put(I0019,"itemAPI.invalid.productCode");
		errorCodeMap.put(I0020,"itemAPI.invalid.modelCode");
		errorCodeMap.put(I0021,"itemAPI.exception.extractingResponse");
		errorCodeMap.put(I0022,"itemAPI.nullCheck.modelCode");
		errorCodeMap.put(I0023,"itemAPI.invalid.partClassCode");
		errorCodeMap.put(I0024,"itemAPI.itemGroupCode");
		errorCodeMap.put(I0025,"itemAPI.unSupported.itemType");
		errorCodeMap.put(I0026,"itemAPI.invalid.groupCode");
		errorCodeMap.put(I0027,"itemAPI.invalid.divisionCode");
		errorCodeMap.put(I0028,"itemAPI.itemNumberNotFound.fetchItemForOEM");
		errorCodeMap.put(I0029,"itemAPI.transform.bodXmlToSoap");
		errorCodeMap.put(I0030,"itemAPI.nullCheck.supplierNumber");
		errorCodeMap.put(I0031,"itemAPI.notFound.productType");
		errorCodeMap.put(I0032,"itemAPI.series.alreadyExists");
		errorCodeMap.put(I0033,"itemAPI.notFound.oppositeSeriesCode");
		//errorCodeMap.put(I0034,"itemAPI.exception.hibernate");
		errorCodeMap.put(I0035,"Node info is null for the given Product code");
		errorCodeMap.put(I0036,"itemAPI.exception.brand.item.number.does.not.exist");
		errorCodeMap.put(I0037,"itemAPI.exception.item.number.does.not.exist.for.brand");
		errorCodeMap.put(I0038,"itemAPI.exception.while.syncing.item.sync");
		errorCodeMap.put(I0039,"itemAPI.nullcheck.productfamilycode");
		errorCodeMap.put(I0040,"itemAPI.doseNotExists.superSessionItemNumber");
		errorCodeMap.put(I0045,"itemAPI.nullCheck.serviceCategory");
		errorCodeMap.put(I0046,"itemAPI.nullCheck.nMHGLocation");
		errorCodeMap.put(I0047,"itemAPI.nullCheck.fromDate");
		errorCodeMap.put(I0048,"itemAPI.nullCheck.toDate");
		errorCodeMap.put(I0050,"itemAPI.invalid.brand.item.number");
		errorCodeMap.put(I0051,"itemAPI.invalid.divisioncode.mandatory");
		errorCodeMap.put(I0052,"itemAPI.invalid.brandItems.items.not.unique");
		errorCodeMap.put(I0053,"itemAPI.supplier.item.number.mandatory");
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
	public String getPropertyMessage(final String errorMessageKey, String[] args) {
		return i18nDomainTextReader.getText(getErrorMessage(errorMessageKey), args);
	}

}
