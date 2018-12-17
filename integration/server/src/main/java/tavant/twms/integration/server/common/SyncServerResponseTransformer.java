/*
 *   Copyright (c)2006 Tavant Technologies
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

package tavant.twms.integration.server.common;

import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlOptions;

import com.nmhg.synch_response.MTOraStatusBeanDocument;
import com.nmhg.synch_response.OraStatusBean;
import com.nmhg.synch_response.OraStatusBean.Exceptions;
import com.nmhg.synch_response.OraStatusBean.Exceptions.Error;

public class SyncServerResponseTransformer {

	public String transformResponse(List<SyncServerResponse> responses) {
		SyncServerResponse syncServerResponse = responses.get(0);
		MTOraStatusBeanDocument doc = MTOraStatusBeanDocument.Factory.newInstance();
        OraStatusBean oraStatusBean = OraStatusBean.Factory.newInstance();

        if (syncServerResponse.isSuccessful()) {
            oraStatusBean.setStatus(SyncServerResponse.SUCESS_MSG);
        } else {
            oraStatusBean.setStatus(SyncServerResponse.FAILURE_MSG);

            Map<String, String> errorMessageCodesMap = syncServerResponse.getErrorMessages();
            Exceptions exceptions = oraStatusBean.addNewExceptions();
    		for (Map.Entry<String, String> errorMessage : errorMessageCodesMap
    				.entrySet()) {
    			Error error = exceptions.addNewError();
    			error.setErrorCode(errorMessage.getKey());
    			error.setErrorMessage(errorMessage.getValue());
    		}
    		oraStatusBean.setExceptions(exceptions);
        }
		doc.setMTOraStatusBean(oraStatusBean);
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
