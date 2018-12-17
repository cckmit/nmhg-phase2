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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<table id="claim_attachments_table" class="repeat " width="96.9%">
    <thead>
        <tr class="title">
            <th><s:text name="label.common.supportDocs"/></th>
            <th><div class="attachFile" id="claim_attachment_table_add"/></th>
        </tr>
        <tr class="row_head borderForTable">
            <th width="95%"><s:text name="label.common.fileName"/></th><%--TODO: i18N me--%>
            <th><s:text name="label.lov.attachmentType"/></th>
            <th width="5%"><s:text name="label.common.remove"/></th>
        </tr>
    </thead>
    <tbody id="claim_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">
        <input type="hidden" name="attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        <td>
            <a id="attached_file_name_${index}" title="Attachment made by ${user}">${name}</a>
        </td>
                <td>
        			<s:select id="attachmentTypeCode_${index}" name="attachments[${index}].documentType"
						cssClass="processor_decesion"
						list="getLovsForClass('DocumentType')"
						theme="twms" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
        		</td>   
        
                <script type="text/javascript">
        dojo.addOnLoad(function() {
		    	  var documentType = '${type}';	
		    	  if(documentType != '')
		    		  dijit.byId("attachmentTypeCode_${index}").set('value',documentType);
		    	  
        });
		  </script> 
        
        <td>
            <div class="repeat_del" id="attachment_drop_button_${index}"/>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "attachments");
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
        var uploadManager = new twms.claim.AttachmentUploadRenderer(dojo.byId("claim_attchament_table_tbody"),
                                                                    attachmentRowTemplate,
                                                                    <s:property value="JSONifiedAttachmentList" escape="false"/>);
        dojo.connect(dojo.byId("claim_attachment_table_add"), "onclick",
                           dojo.hitch(uploadManager, uploadManager.requestBatchAddition));
    });

    dojo.declare("twms.claim.AttachmentUploadRenderer", null, {

        _claim_attchment_row_index : 0,
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
                    user : dataList[i].user,
                    type : dataList[i].type,
                    index : this._claim_attchment_row_index++
                };
                var rowMarkup = dojo.string.substitute(this._markupTemplate, substitutionMap);
                var rowScript = dojo.string.substitute(this._scriptTemplate,  substitutionMap);
                var row = dojo.html.createNodesFromText(rowMarkup, true);
                dojo.parser.parse(row);
                this._tBody.appendChild(row);
                eval(rowScript);
            }
        }
    });
</script>