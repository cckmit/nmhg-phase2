package tavant.twms.integration.layer.transformer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.util.StringUtils;

import tavant.oagis.ServerResponseHeaderDocumentDTO;
import tavant.oagis.BusinessUnitsDocumentDTO.BusinessUnits;
import tavant.oagis.ServerResponseHeaderDocumentDTO.ServerResponseHeader;
import tavant.oagis.UniqueIdentifierDocumentDTO.UniqueIdentifier;
import tavant.twms.integration.layer.component.SyncResponse;

public class ServerResponseTransformer {
	public String transformResponse(List<SyncResponse> responses) {
		SyncResponse syncResponse = responses.get(0);
		ServerResponseHeaderDocumentDTO doc = ServerResponseHeaderDocumentDTO.Factory
				.newInstance();
		ServerResponseHeader serverResponseHeader = ServerResponseHeader.Factory.newInstance();
		UniqueIdentifier uniqueIdentifier = UniqueIdentifier.Factory.newInstance();
		BusinessUnits businessUnits = BusinessUnits.Factory.newInstance();
		uniqueIdentifier.setName(syncResponse.getUniqueIdName());
		uniqueIdentifier.setValue(syncResponse.getUniqueIdValue());
		Set<String> buNames=null;
		if (CollectionUtils.isNotEmpty(syncResponse.getBusinessUnits())) {
			buNames = syncResponse.getBusinessUnits();
			if (CollectionUtils.isNotEmpty(buNames)) {
				businessUnits.setBUNameArray(Arrays.copyOf(buNames
						.toArray(), buNames.toArray().length,
						String[].class));
				uniqueIdentifier.setBusinessUnits(businessUnits);
			}
		}
		if(CollectionUtils.isEmpty(syncResponse.getBusinessUnits()) && StringUtils.hasText(syncResponse.getBusinessUnitName())){
           uniqueIdentifier.setBusinessUnitName(syncResponse.getBusinessUnitName());
		}
		serverResponseHeader.setErrorType(syncResponse.getErrorType());
		serverResponseHeader.setUniqueIdentifier(uniqueIdentifier);
		doc.setServerResponseHeader(serverResponseHeader);
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		StringBuffer response = new StringBuffer(xmlHeader);
		response.append(doc.xmlText(createXMLOptions()));
		return response.toString();
	}

	public String transformResponse(SyncResponse responses) {
		SyncResponse syncResponse = responses;
		ServerResponseHeaderDocumentDTO doc = ServerResponseHeaderDocumentDTO.Factory
				.newInstance();
		ServerResponseHeader serverResponseHeader = ServerResponseHeader.Factory.newInstance();
		UniqueIdentifier uniqueIdentifier = UniqueIdentifier.Factory.newInstance();
		uniqueIdentifier.setName(syncResponse.getUniqueIdName());
		uniqueIdentifier.setValue(syncResponse.getUniqueIdValue());
		serverResponseHeader.setErrorType(syncResponse.getErrorType());
		serverResponseHeader.setUniqueIdentifier(uniqueIdentifier);
		doc.setServerResponseHeader(serverResponseHeader);
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		StringBuffer response = new StringBuffer(xmlHeader);
		response.append(doc.xmlText(createXMLOptions()));
		return response.toString();
	}

	private XmlOptions createXMLOptions() {
		// Generate the XML document
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(4);
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setUseDefaultNamespace();
		return xmlOptions;
	}
}
