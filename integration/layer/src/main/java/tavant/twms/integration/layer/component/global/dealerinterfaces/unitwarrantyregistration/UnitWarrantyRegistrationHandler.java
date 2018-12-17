package tavant.twms.integration.layer.component.global.dealerinterfaces.unitwarrantyregistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.domain.policy.Warranty;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitwarrantyregistration.UnitWarrantyRegistrationTransformer;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.tavant.dealerinterfaces.warrantyregistration.request.UnitWarrantyRegistrationRequestDocument;
import com.tavant.dealerinterfaces.warrantyregistration.request.UnitWarrantyRegistrationRequestDocument.UnitWarrantyRegistrationRequest;
import com.tavant.dealerinterfaces.warrantyregistration.response.EachErrorCodeDTO;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO.WarrantyRegistrationResponse;
import com.tavant.dealerinterfaces.warrantyregistration.response.ErrorCodesTypeDTO;

public class UnitWarrantyRegistrationHandler {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	private ApplicationSettingsHolder applicationSettings;

	private UnitWarrantyRegistrationTransformer unitWarrantyRegistrationTransformer;

	private UnitWarrantyRegistrationProcessor unitWarrantyRegistrationProcessor;

	public UnitWarrantyRegistrationProcessor getUnitWarrantyRegistrationProcessor() {
		return unitWarrantyRegistrationProcessor;
	}

	public void setUnitWarrantyRegistrationProcessor(UnitWarrantyRegistrationProcessor unitWarrantyRegistrationProcessor) {
		this.unitWarrantyRegistrationProcessor = unitWarrantyRegistrationProcessor;
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

	public UnitWarrantyRegistrationTransformer getUnitWarrantyRegistrationTransformer() {
		return unitWarrantyRegistrationTransformer;
	}

	public void setUnitWarrantyRegistrationTransformer(
			UnitWarrantyRegistrationTransformer unitWarrantyRegistrationTransformer) {
		this.unitWarrantyRegistrationTransformer = unitWarrantyRegistrationTransformer;
	}

	public void setErrorResponseDTO(WarrantyRegistrationResponseDocumentDTO unitWarrantyRegistrationReponseDocumentDTO,
			Map<String, String[]> errorCodesMap, String errorCode) {

		WarrantyRegistrationResponse unitWarrantyRegistrationResponse = WarrantyRegistrationResponse.Factory
				.newInstance();

		ErrorCodesTypeDTO errorCodesTypeDTO = ErrorCodesTypeDTO.Factory.newInstance();

		setErrorCodes(errorCodesTypeDTO, errorCodesMap, errorCode);

		unitWarrantyRegistrationResponse.setErrorCodes(errorCodesTypeDTO);
		unitWarrantyRegistrationResponse.setStatus(dealerInterfaceErrorConstants
				.getPropertyMessage(DealerInterfaceErrorConstants.FAILURE));
		unitWarrantyRegistrationResponse.setTWMSURL(getApplicationSettings().getExternalUrl());

		unitWarrantyRegistrationReponseDocumentDTO.setWarrantyRegistrationResponse(unitWarrantyRegistrationResponse);

	}

	private void setErrorCodes(ErrorCodesTypeDTO errorCodesTypeDTO, Map<String, String[]> errorCodesMap,
			String errorCode) {
		EachErrorCodeDTO[] eachErrorCodeDTO = null;
		if (errorCode == null) {
			int totalErrorCodes = errorCodesMap.size();
			eachErrorCodeDTO = new EachErrorCodeDTO[totalErrorCodes];
			Iterator<String> iterator = errorCodesMap.keySet().iterator();
			int count = 0;
			while (iterator.hasNext()) {
				String keyObj = iterator.next();
				String[] args = errorCodesMap.get(keyObj);
				eachErrorCodeDTO[count] = EachErrorCodeDTO.Factory.newInstance();
				if (args == null) {
					eachErrorCodeDTO[count].setErrorMessage(dealerInterfaceErrorConstants.getPropertyMessage(keyObj));
				} else {
					eachErrorCodeDTO[count].setErrorMessage(dealerInterfaceErrorConstants.getPropertyMessage(keyObj,
							args));
				}
				eachErrorCodeDTO[count].setErrorCode(dealerInterfaceErrorConstants.getErrorCode(keyObj));
				count++;
			}
		} else if (errorCode.equals(DealerInterfaceErrorConstants.DAPI01)) {
			
			eachErrorCodeDTO = new EachErrorCodeDTO[2];
			eachErrorCodeDTO[0] = EachErrorCodeDTO.Factory.newInstance();
			eachErrorCodeDTO[0].setErrorCode(errorCode);
			eachErrorCodeDTO[0].setErrorMessage(dealerInterfaceErrorConstants.getErrorMessage(errorCode));
			
			eachErrorCodeDTO[1] = EachErrorCodeDTO.Factory.newInstance();
			eachErrorCodeDTO[1].setErrorCode(errorCode);			
			eachErrorCodeDTO[1].setErrorMessage(errorCodesMap.keySet().iterator().next());
		} else {
			
			eachErrorCodeDTO = new EachErrorCodeDTO[1];
			eachErrorCodeDTO[0] = EachErrorCodeDTO.Factory.newInstance();
			eachErrorCodeDTO[0].setErrorCode(errorCode);
			eachErrorCodeDTO[0].setErrorMessage(dealerInterfaceErrorConstants.getErrorMessage(errorCode));
		}
		errorCodesTypeDTO.setEachErrorCodeArray(eachErrorCodeDTO);

	}

	public void processWarrantyRegistration(
			UnitWarrantyRegistrationRequestDocument unitWarrantyRegistrationRequestDocument,
			WarrantyRegistrationResponseDocumentDTO unitWarrantyRegistrationResponseDocumentDTO) {

		try {
			UnitWarrantyRegistrationRequest unitWarrantyRegistrationRequest = unitWarrantyRegistrationRequestDocument
					.getUnitWarrantyRegistrationRequest();

			Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();

			// fetching BU and setting
			String businessUnitName = unitWarrantyRegistrationRequest.getBUName().toString();
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(businessUnitName);

			dealerInterfaceErrorConstants.getI18nDomainTextReader().setLoggedInUserLocale(businessUnitName);

			// validations and populating warranty object
			Warranty warranty = unitWarrantyRegistrationTransformer.validateAndPopulateWarrantyObject(
					unitWarrantyRegistrationRequest, errorCodeMap);

			// processing warranty object if no errors in the given XML input
			if (errorCodeMap.isEmpty()) {

				if (warranty.isDraft()) { // checking for draft

					unitWarrantyRegistrationProcessor.saveDraft(warranty, errorCodeMap);

				} else { // submitting warranty

					unitWarrantyRegistrationProcessor.register(warranty, errorCodeMap);
				}

				if (errorCodeMap.isEmpty()) {
					// setting response DTO
					WarrantyRegistrationResponse unitWarrantyRegistrationResponse = WarrantyRegistrationResponse.Factory
							.newInstance();
					unitWarrantyRegistrationResponse.setStatus(dealerInterfaceErrorConstants
							.getPropertyMessage(DealerInterfaceErrorConstants.SUCESS));

					unitWarrantyRegistrationResponse.setTWMSURL(getApplicationSettings().getExternalUrl());
					unitWarrantyRegistrationResponseDocumentDTO
							.setWarrantyRegistrationResponse(unitWarrantyRegistrationResponse);

				} else {

					setErrorResponseDTO(unitWarrantyRegistrationResponseDocumentDTO, errorCodeMap, null);
				}

			} else { // setting errorCodes if input XML not valid

				setErrorResponseDTO(unitWarrantyRegistrationResponseDocumentDTO, errorCodeMap, null);
			}

		} catch (Exception e) {			
			logger.error("Error in Delivery Report: "+e);
			setErrorResponseDTO(unitWarrantyRegistrationResponseDocumentDTO, null, DealerInterfaceErrorConstants.DAPI02);

		}

	}
}