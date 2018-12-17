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

package tavant.twms.integration.layer.transformer;

import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlOptions;

import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.util.CalendarUtil;

import com.nmhg.synch_response.MTOraStatusBeanDocument;
import com.nmhg.synch_response.OraStatusBean;
import com.nmhg.synch_response.OraStatusBean.ApplicationArea;
import com.nmhg.synch_response.OraStatusBean.ApplicationArea.Sender;
import com.nmhg.synch_response.OraStatusBean.Exceptions;
import com.nmhg.synch_response.OraStatusBean.Exceptions.Error;

public class SyncResponseTransformer {

    public String transformResponse(List<SyncResponse> responses) {
        SyncResponse syncResponse = responses.get(0);
        MTOraStatusBeanDocument doc = MTOraStatusBeanDocument.Factory.newInstance();
        OraStatusBean oraStatusBean = OraStatusBean.Factory.newInstance();

        if (syncResponse.isSuccessful()) {
            oraStatusBean.setStatus(SyncResponse.SUCESS_MSG);
        } else {
            oraStatusBean.setStatus(SyncResponse.FAILURE_MSG);

            Map<String, String> errorMessageCodesMap = syncResponse.getErrorMessages();
            Exceptions exceptions = oraStatusBean.addNewExceptions();
    		for (Map.Entry<String, String> errorMessage : errorMessageCodesMap
    				.entrySet()) {
    			Error error = exceptions.addNewError();
    			error.setErrorCode(errorMessage.getKey());
    			error.setErrorMessage(errorMessage.getValue());
    		}
    		oraStatusBean.setExceptions(exceptions);
        }
        ApplicationArea applicationAreaType=ApplicationArea.Factory.newInstance();
        Sender sender=Sender.Factory.newInstance();
        applicationAreaType.setSender(sender);
        oraStatusBean.setApplicationArea(applicationAreaType);
        oraStatusBean.getApplicationArea().setBODId(syncResponse.getBodId());
        if(syncResponse.getCreationDateTime()!=null){
        oraStatusBean.getApplicationArea().setCreationDateTime(CalendarUtil
				.convertToDateTimeToString(syncResponse.getCreationDateTime().getTime()));
        }
        oraStatusBean.getApplicationArea().getSender().setLogicalId(syncResponse.getTask());
        oraStatusBean.getApplicationArea().getSender().setReferenceId(syncResponse.getReferenceId());
        oraStatusBean.getApplicationArea().getSender().setTask(syncResponse.getLogicalId());
        oraStatusBean.getApplicationArea().setInterfaceNumber(syncResponse.getInterfaceNumber());
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
