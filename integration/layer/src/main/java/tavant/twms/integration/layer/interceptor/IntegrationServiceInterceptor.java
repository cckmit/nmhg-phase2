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
 *
 */

/**
 * Created Mar 7, 2007 10:11:51 AM
 * @author kapil.pandit
 */

package tavant.twms.integration.layer.interceptor;

import java.util.HashMap;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import tavant.globalsync.foc.FocDocument;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.transformer.ServerResponseTransformer;
import tavant.twms.integration.layer.transformer.SyncResponseTransformer;
import tavant.twms.integration.layer.transformer.global.FocClaimTransformer;

import com.nmhg.batchclaim_response.MTClaimSubmissionResponseDocument;
import com.nmhg.itemsynch_response.MTItemSyncResponseDocument;
import com.tavant.globalsync.failurecodesresponse.FailureCodesResponseSyncDocument;

public class IntegrationServiceInterceptor implements MethodInterceptor {

	private static final Logger logger = Logger
			.getLogger(IntegrationServiceInterceptor.class.getName());

	SyncResponseTransformer syncResponseTransformer;

	ServerResponseTransformer serverResponseTransformer;
	
	FocClaimTransformer focClaimTransformer;

	@SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation invocation) throws Throwable {

		String bodType = getBodType(invocation);

		// Process/sync the bod
		Object response = invocation.proceed();

		String result = null;
		String serverHeader = "";
		String responses[] = new String[2];

		if (response instanceof MTItemSyncResponseDocument) {
			MTItemSyncResponseDocument itemSyncResponseDocumentDTO =  (MTItemSyncResponseDocument) response;
            result = itemSyncResponseDocumentDTO.xmlText(createXMLOptions());
			SyncResponse syncResponse = new SyncResponse();
			syncResponse.setUniqueIdName("OrderNo");
			syncResponse.setUniqueIdValue(itemSyncResponseDocumentDTO.getMTItemSyncResponse().getApplicationArea().getBODId());
			serverHeader = serverResponseTransformer.transformResponse(syncResponse);
		} else if (response instanceof MTClaimSubmissionResponseDocument) {
			MTClaimSubmissionResponseDocument batchClaimSyncResponseDocumentDTO =  (MTClaimSubmissionResponseDocument) response;
            result = batchClaimSyncResponseDocumentDTO.xmlText(createXMLOptions());
			SyncResponse syncResponse = new SyncResponse();
			syncResponse.setUniqueIdName("ReferenceId");
			if (batchClaimSyncResponseDocumentDTO
					.getMTClaimSubmissionResponse() != null
					&& batchClaimSyncResponseDocumentDTO
							.getMTClaimSubmissionResponse()
							.getApplicationArea() != null
					&& batchClaimSyncResponseDocumentDTO
							.getMTClaimSubmissionResponse()
							.getApplicationArea().getSender() != null)
			syncResponse.setUniqueIdValue(batchClaimSyncResponseDocumentDTO.getMTClaimSubmissionResponse().getApplicationArea().getSender().getReferenceId());
			serverHeader = serverResponseTransformer.transformResponse(syncResponse);
		} else if (response instanceof FailureCodesResponseSyncDocument) {
			FailureCodesResponseSyncDocument failureCodesResponseSyncDocument =  (FailureCodesResponseSyncDocument) response;
            result = failureCodesResponseSyncDocument.xmlText(createXMLOptions());
			SyncResponse syncResponse = new SyncResponse();
			syncResponse.setUniqueIdName("OrderNo");
			syncResponse.setUniqueIdValue(failureCodesResponseSyncDocument.getFailureCodesResponseSync().getApplicationArea().getBODId());
			serverHeader = serverResponseTransformer.transformResponse(syncResponse);
		} else 	if (response instanceof FocDocument) {
			FocDocument focDocument =  (FocDocument) response;
			result = focClaimTransformer.transform(focDocument);
		} else if (response instanceof String) {
			result = (String) response;
		} else if(response instanceof HashMap){ 
			return response;
		} else {
			try{
			result = syncResponseTransformer.transformResponse((List<SyncResponse>)response);
			serverHeader = serverResponseTransformer.transformResponse((List<SyncResponse>)response);
			}catch(Exception e){
				return response;
			}
		}

		responses[0] = result;
		responses[1] = serverHeader;
        if (logger.isDebugEnabled()) {
            logger.debug("Sending response for " + bodType + "\n " + result + "\n " + serverHeader);
        }
		return responses;
	}

	private String getBodType(MethodInvocation invocation) {
		String name = invocation.getMethod().getName();
		String firstChar = name.substring(0, 1);
		return name.replaceFirst(firstChar, firstChar.toUpperCase());
	}

    private static XmlOptions createXMLOptions() {
        // Generate the XML document
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSavePrettyPrintIndent(4);
        xmlOptions.setSaveAggressiveNamespaces();
        xmlOptions.setUseDefaultNamespace();
        return xmlOptions;
    }

	public void setSyncResponseTransformer(
			SyncResponseTransformer syncResponseTransformer) {
		this.syncResponseTransformer = syncResponseTransformer;
	}

	public void setServerResponseTransformer(
			ServerResponseTransformer serverResponseTransformer) {
		this.serverResponseTransformer = serverResponseTransformer;
	}

	public void setFocClaimTransformer(FocClaimTransformer focClaimTransformer) {
		this.focClaimTransformer = focClaimTransformer;
	}
}
