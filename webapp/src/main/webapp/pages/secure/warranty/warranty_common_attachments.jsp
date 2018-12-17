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
<table id="warranty_common_attachments_table" class="repeat borderForTable">
    <thead>
        <tr class="title">
            <th><s:text name="label.newClaim.supportDocs"/></th>
            <th><div class="repeat_add" id="warranty_common_attachment_table_add" /></th>
        </tr>
        <tr class="row_head">
            <th width="95%"><s:text name="label.newClaim.fileName"/></th><%--TODO: i18N me--%>
            <th width="5%"><s:text name="label.newClaim.remove"/></th>
        </tr>
    </thead>
    <tbody id="warranty_common_attchament_table_tbody"/>
</table>
<u:jsVar varName="commonAttachmentRowTemplate">
    <tr index="${index}"> 
    <input type="hidden" name="commonAttachments[${index}]" id="commonAttachmentId_${index}" value="${id}"/>       
        <td>
            <a id="common_attached_file_name_${index}">${name}</a>
        </td>
        <td>
            <div class="repeat_del" id="common_attachment_drop_button_${index}"/>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("common_attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "commonAttachments");
                        dojo.dom.destroyNode(row);
                    });
                    dojo.connect(dojo.byId("common_attached_file_name_${index}"), "onclick", function(event) {
                        dojo.stopEvent(event);
                        getFileDownloader().download("downloadDocument.action?docId=${id}");
                    });
			        var commonAttachmentId = dojo.byId("commonAttachments");
			        var tr = getExpectedParent(commonAttachmentId, "tr");
			        var hiddenInput = document.createElement("input");
			        hiddenInput.type = "hidden";
			        hiddenInput.id = "commonAttachmentId${index}";
			        hiddenInput.name = "commonAttachments[${index}]";
			        hiddenInput.value = "${id}";
			        tr.appendChild(hiddenInput);
                });
            </script>
        </td>
    </tr>
</u:jsVar>
<script type="text/javascript">
    dojo.declare("twms.warranty.AttachmentUploadRenderer", null, {
			
        _warranty_common_attchment_row_index : 0,
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
                callback : function(/*message that the reloaded page(in the dialog) publishes...*/commonDocDataList) { 
                    showUploadedFiles(commonDocDataList);
                },
            onClose : function() {},
            batchSize : 5
            }; 
            publishEvent("/uploadDocument/dialog/show", message);
        },

        _addRows : function(/*collection of document's id, name and stuff...*/ commonDataList) {        	
            for (var i in commonDataList) {
                var substitutionMap = {
                    id : commonDataList[i].id,
                    name : commonDataList[i].name,
                    index : this._warranty_common_attchment_row_index++
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
        var alreadyPresentDocs = <s:property value="JSONifiedCommonAttachmentList" escape="false"/>;
        var uploadManager = new twms.warranty.AttachmentUploadRenderer(dojo.byId("warranty_common_attchament_table_tbody"),
                                                                    commonAttachmentRowTemplate, alreadyPresentDocs);
        dojo.connect(dojo.byId("warranty_common_attachment_table_add"), "onclick",
                           dojo.hitch(uploadManager,uploadManager.requestBatchAddition));
    });
            

</script>
