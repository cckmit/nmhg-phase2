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
<table id="campaign_attachments_table" class="repeat borderForTable">
    <thead>
        <tr class="admin_section_heading">
            <th><s:text name="label.newClaim.supportDocs"/></th>
            <th></th>
            <th><div class="repeat_add" id="campaign_attachment_table_add"/></th>
        </tr>
        <tr class="row_head">
            <th width="75%"><s:text name="label.newClaim.fileName"/></th><%--TODO: i18N me--%>
            <th width="20%"><s:text name="label.customReportAnswer.mandatory"/></th>            
            <th width="5%"><s:text name="label.newClaim.remove"/></th>
        </tr>
    </thead>
    <tbody id="campaign_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">
        <input type="hidden" name="campaign.attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        <td>
            <a id="attached_file_name_${index}">${name}</a>
        </td>
       	<td align="center">       
	        <s:checkbox name="campaign.attachments[${index}].mandatory" id="mandatory_${index}"/>
	        <script type="text/javascript">
	        	var flag = '${mandatory}';
	        	if(flag=='true'){
					dojo.byId("mandatory_${index}").checked=true;	
	        	}
            </script>	        
	 	</td>
        <td>
            <div class="repeat_del" id="attachment_drop_button_${index}"/>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "campaign.attachments");
                        dojo.dom.destroyNode(row);
                    });
                    dojo.connect(dojo.byId("attached_file_name_${index}"), "onclick", function(event) {
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
        var alreadyPresentDocs = <s:property value="JSONifiedAttachmentList" escape="false"/>;
        var uploadManager = new twms.campaign.AttachmentUploadRenderer(dojo.byId("campaign_attchament_table_tbody"),
                                                                    attachmentRowTemplate,
                                                                    alreadyPresentDocs);
        dojo.connect(dojo.byId("campaign_attachment_table_add"), "onclick",
                           dojo.hitch(uploadManager, uploadManager.requestBatchAddition));
    });
            
    dojo.declare("twms.campaign.AttachmentUploadRenderer", null, {

        _campaign_attchment_row_index : 0,
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
                    mandatory : dataList[i].mandatory,
                    index : this._campaign_attchment_row_index++
                };
                var rowMarkup = dojo.string.substitute(this._markupTemplate, substitutionMap);
                var rowScript = dojo.string.substitute(this._scriptTemplate,  substitutionMap);
                var row = dojo.html.createNodesFromText(rowMarkup, true);
                this._tBody.appendChild(row);
                eval(rowScript);
            }
        }
    });
</script>