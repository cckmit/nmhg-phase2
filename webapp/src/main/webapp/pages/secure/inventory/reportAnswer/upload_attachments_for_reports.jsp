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


<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.Dialog");
</script>
<table id="reportanswer_attachment_table" class="repeat borderForTable">
    <thead>
        <tr class="title">
            <th><s:text name="label.newClaim.supportDocs"/></th>
             <s:if test="@tavant.twms.web.TWMSWebConstants@SUBMITTED.equals(reportAnswer.status)"> <th><div style="width: 56px;height: 16px;" id="reportanswer_attachment_table_add" /></th></s:if>     
                 <s:else>
                      <th>
                      <div class="repeat_add" id="reportanswer_attachment_table_add" />
                      </th>
                 </s:else> 
        </tr>
        <tr class="row_head">
            <th width="95%"><s:text name="label.newClaim.fileName"/></th><%--TODO: i18N me--%>
             <s:if test="@tavant.twms.web.TWMSWebConstants@SUBMITTED.equals(reportAnswer.status)"><th style="width:16px;height:16px"></th> </s:if>     
            <s:else><th width="5%"><s:text name="label.newClaim.remove"/></th></s:else>
        </tr>
    </thead>
    <tbody id="reportanswer_attachment_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">        
        <td>
            <a id="attached_file_name_${index}">${name}</a>
        </td>
        <td>  <s:if test="@tavant.twms.web.TWMSWebConstants@SUBMITTED.equals(reportAnswer.status)"> <div id="attachment_drop_button_${index}" /></div></s:if>
        <s:else> <div class="repeat_del" id="attachment_drop_button_${index}" /></s:else>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("attached_file_name_${index}"), "onclick", function(event) {
                        dojo.stopEvent(event);
                        getFileDownloader().download("downloadDocument.action?docId=${id}");
                    });
                    dojo.connect(dojo.byId("attachment_drop_button_${index}"), "onclick", function() {
                        var row = getExpectedParent(dojo.byId("attachment_drop_button_${index}"), "tr");
                        dojo.dom.destroyNode(row);
                        var textBoxToDelete = document.getElementById("attachmentsId_${index}");
                        textBoxToDelete.value="null";
                    });
                    var attachmentId = dojo.byId("attachments");
			        var tr = getExpectedParent(attachmentId, "tr");
			        var hiddenInput = document.createElement("input");
			        hiddenInput.type = "hidden";
			        hiddenInput.id = "attachmentsId_${index}";
			        hiddenInput.name = "reportAnswer.attachments[${index}]";
			        hiddenInput.value = "${id}";
			        tr.appendChild(hiddenInput);
                });
            </script>
        </td>
    </tr>
</u:jsVar>
<script type="text/javascript">
    dojo.declare("twms.warranty.AttachmentUploadRenderer", null, {
			
        _warranty_attchment_row_index : 0,
        _tBody : null,

        _markupTemplate: null,
        _scriptTemplate : null,

        constructor : function(/*DomNode (tbody)*/ tBody,
                               /*String (template)*/ template,
                               /*collection of document's id, name and stuff...*/ alreadyUploadedDocList) { 
                                                             
            this._tBody = tBody;
            this._markupTemplate = template.markup;
            this._scriptTemplate = template.script;
            this._addRows(alreadyUploadedDocList);
        },

        requestBatchAddition : function() {                            		                                                                          	
            var showUploadedFiles = dojo.hitch(this, this._addRows);
            var message = {
                callback : function(/*message that the reloaded page(in the dialog) publishes...*/docDataList) { 
                    showUploadedFiles(docDataList);
                },
            onClose : function() {},
            batchSize : 5
            }; 
            publishEvent("/uploadDocument/dialog/show", message);
        },

        _addRows : function(/*collection of document's id, name and stuff...*/ dataList) {        	
            for (var i in dataList) {
                var substitutionMap = {
                    id : dataList[i].id,
                    name : dataList[i].name,
                    index : this._warranty_attchment_row_index++
                };                
                var rowMarkup = dojo.string.substitute(this._markupTemplate, substitutionMap);
                var rowScript = dojo.string.substitute(this._scriptTemplate,  substitutionMap);
                var row = dojo.html.createNodesFromText(rowMarkup, true);
                this._tBody.appendChild(row);
                eval(rowScript);
            }
        }
    });

    dojo.addOnLoad(function() {
        var alreadyPresentDocs = <s:property value="JSONifiedAttachmentList" escape="false"/>;
        var uploadManager = new twms.warranty.AttachmentUploadRenderer(dojo.byId("reportanswer_attachment_table_tbody"),
                                                                    attachmentRowTemplate, alreadyPresentDocs);
        dojo.connect(dojo.byId("reportanswer_attachment_table_add"), "onclick",
                           dojo.hitch(uploadManager,uploadManager.requestBatchAddition));
    });
            

</script>
