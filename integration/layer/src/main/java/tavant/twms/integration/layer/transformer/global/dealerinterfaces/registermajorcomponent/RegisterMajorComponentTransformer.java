package tavant.twms.integration.layer.transformer.global.dealerinterfaces.registermajorcomponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;

import com.tavant.dealerinterfaces.majorcomponentregistration.majorcomponentregistrationrequest.MajorComponentRegistrationDocument;
import com.tavant.dealerinterfaces.warrantyregistration.response.EachErrorCodeDTO;
import com.tavant.dealerinterfaces.warrantyregistration.response.ErrorCodesTypeDTO;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO.WarrantyRegistrationResponse;

public class RegisterMajorComponentTransformer implements BeanFactoryAware {

	private final Logger logger = Logger.getLogger("dealerAPILogger");

	private BeanFactory beanFactory;
	
	private ApplicationSettingsHolder applicationSettings;
	
	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.beanFactory = arg0;
	}

	public MajorComponentRegistrationDocument convertXMLToRequestDTO(String inputXML) throws XmlException{
		MajorComponentRegistrationDocument majorComponentRegistrationDocDTO = null;
		if (!StringUtils.isBlank(inputXML)) {
			try {
				majorComponentRegistrationDocDTO = MajorComponentRegistrationDocument.Factory.parse(inputXML);	
				// Create an XmlOptions instance and set the error listener.
				XmlOptions validateOptions = new XmlOptions();
				ArrayList errorList = new ArrayList();
				validateOptions.setErrorListener(errorList);
				
				if(!majorComponentRegistrationDocDTO.validate(validateOptions)) {
				
					XmlError error = (XmlError) errorList.get(0);					
					throw new XmlException(error.getMessage());
				}				
			} catch (XmlException xe) {
				logger.error("Error in XML parsing or validation", xe);
				throw xe;
			}
		}
		return majorComponentRegistrationDocDTO;
	}
	
	public void setErrorResponseDTO(WarrantyRegistrationResponseDocumentDTO majorComponentRegResDocDTO,
			Map<String, String[]> errorCodesMap, String errorCode) {
		WarrantyRegistrationResponse majorComponentRegistrationResponse = WarrantyRegistrationResponse.Factory
				.newInstance();
		ErrorCodesTypeDTO errorCodesTypeDTO = ErrorCodesTypeDTO.Factory.newInstance();
		setErrorCodes(errorCodesTypeDTO, errorCodesMap, errorCode);
		majorComponentRegistrationResponse.setErrorCodes(errorCodesTypeDTO);
		majorComponentRegistrationResponse.setStatus(dealerInterfaceErrorConstants
				.getPropertyMessage(DealerInterfaceErrorConstants.FAILURE));
		majorComponentRegistrationResponse.setTWMSURL(getApplicationSettings().getExternalUrl());
		majorComponentRegResDocDTO.setWarrantyRegistrationResponse(majorComponentRegistrationResponse);
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
		} else if (errorCode.equals("DAPI01")) {
			
			eachErrorCodeDTO = new EachErrorCodeDTO[2];
			eachErrorCodeDTO[0] = EachErrorCodeDTO.Factory.newInstance();
			eachErrorCodeDTO[0].setErrorCode(errorCode);
			eachErrorCodeDTO[0].setErrorMessage(dealerInterfaceErrorConstants.getErrorMessage(errorCode));
			
			eachErrorCodeDTO[1] = EachErrorCodeDTO.Factory.newInstance();
			eachErrorCodeDTO[1].setErrorCode(errorCode);
			eachErrorCodeDTO[1].setErrorMessage(new ArrayList<String>(errorCodesMap.keySet()).get(0));
		} else {
			eachErrorCodeDTO = new EachErrorCodeDTO[1];
			eachErrorCodeDTO[0] = EachErrorCodeDTO.Factory.newInstance();
			eachErrorCodeDTO[0].setErrorCode(errorCode);
			eachErrorCodeDTO[0].setErrorMessage(dealerInterfaceErrorConstants.getErrorMessage(errorCode));
		}
		errorCodesTypeDTO.setEachErrorCodeArray(eachErrorCodeDTO);

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

}
