<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

<table id="dr_attachments_table" class="grid borderForTable" width="97%">
    <thead>
        <tr class="row_head">
            <th><s:text name="label.common.fileName"/></th><%--TODO: i18N me--%>
            <th><s:text name="label.lov.attachmentType"/></th>
            <th width="9%"><div class="attachFile" id="dr_attachment_table_add_<s:property value = "%{#nListIndex}" />"/> </th>
            
        </tr>
    </thead>
    <tbody id="dr_attchament_table_tbody_<s:property value = "%{#nListIndex}" />"/>
</table>
<u:jsVar varName="attachmentRowTemplate_%{#nListIndex}">
    <tr index="${index}">
        <input type="hidden" name="inventoryItemMappings[<s:property value = "%{#nListIndex}" />].attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        
        <td>
            <a id="attached_file_name_<s:property value = "%{#nListIndex}" />_${index}" title="Attachment made by ${user}">${name}</a>
        </td>
        <td>
          
          
          <s:select id="attachmentTypeCode_%{#nListIndex}_${index}" name="inventoryItemMappings[%{#nListIndex}].attachments[${index}].unitDocumentType"
						cssClass="processor_decesion"
						list="getLovsForClass('UnitDocumentType',warranty)"
						theme="twms" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
        </td>    
        <script type="text/javascript">
        dojo.addOnLoad(function() {
		    	  var unitDocumentType = '${type}';	
		    	  if(unitDocumentType != '')
		    		  dijit.byId("attachmentTypeCode_<s:property value = "%{#nListIndex}" />_${index}").set('value',unitDocumentType);
		    	  
        });
		  </script>                       
        
     
        <td>
            <div class="repeat_del" id="attachment_drop_button_<s:property value = "%{#nListIndex}" />_${index}" />
            <script type="text/javascript">
            
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("attachment_drop_button_<s:property value = "%{#nListIndex}" />_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "commonAttachments");
                        dojo.dom.destroyNode(row);
                    });
                    dojo.connect(dojo.byId("attached_file_name_<s:property value = "%{#nListIndex}" />_${index}"), "onclick", function(event) {
                        dojo.stopEvent(event);
                        getFileDownloader().download("downloadDocument.action?docId=${id}");
                    });
                });
            </script>
        </td>
    </tr>
</u:jsVar>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        var alreadyPresentDocs_<s:property value="%{#nListIndex}" escape="false"/> = <s:property value="%{getJSONifiedAttachmentList(#nListIndex)}" escape="false"/>;
        var uploadManager = new twms.upload.NListAttachmentUploadRenderer(dojo.byId("dr_attchament_table_tbody_<s:property value = "%{#nListIndex}" />"),
                                                                    attachmentRowTemplate_<s:property value = "%{#nListIndex}" />,
                                                                    alreadyPresentDocs_<s:property value="%{#nListIndex}" escape="false"/>,
                                                                    <s:property value="%{#nListIndex}" escape="false"/>            );
        dojo.connect(dojo.byId("dr_attachment_table_add_<s:property value = "%{#nListIndex}" />"), "onclick",
                           dojo.hitch(uploadManager, uploadManager.requestBatchAddition));
    });
</script>