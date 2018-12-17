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
<table id="warranty_attachments_table" class="repeat borderForTable">
    <thead>
        <tr class="row_head">
            <th width="95%"><s:text name="label.newClaim.fileName"/></th>
            <th width="5%"><div class="repeat_add" id="warranty_attachment_table_add" /></th>
        </tr>        
    </thead>
    <tbody id="warranty_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">        
        <td>
            <a id="attached_file_name_${index}">${name}</a>
        </td>
        <td>
            <div class="repeat_del" id="attachment_drop_button_${index}"/>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "inventoryItemMappings[<s:property value="inventoryItemIndex" />].attachments");
                        dojo.dom.destroyNode(row);
                    });
                    dojo.connect(dojo.byId("attached_file_name_${index}"), "onclick", function(event) {
                        dojo.stopEvent(event);
                        getFileDownloader().download("downloadDocument.action?docId=${id}");
                    });
                    var invItemIndex = '<s:property value="inventoryItemIndex" />';
			        var attachmentId = dojo.byId("attachments_"+invItemIndex);
			        var parentNode = attachmentId.parentNode;
                                var hiddenInput = document.createElement("input");
			        hiddenInput.type = "hidden";
			        hiddenInput.id = "attachmentsId_${index}";
			        hiddenInput.name = "inventoryItemMappings[<s:property value="inventoryItemIndex" />].attachments[${index}]";
			        hiddenInput.value = "${id}";
                                console.debug("#### b: " + parentNode.childNodes.length);
			        parentNode.appendChild(hiddenInput);
                                console.debug("#### a: " + parentNode.childNodes.length);
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
        var uploadManager = new twms.warranty.AttachmentUploadRenderer(dojo.byId("warranty_attchament_table_tbody"),
                                                                    attachmentRowTemplate, alreadyPresentDocs);
        dojo.connect(dojo.byId("warranty_attachment_table_add"), "onclick",
                           dojo.hitch(uploadManager,uploadManager.requestBatchAddition));
    });
            

</script>
