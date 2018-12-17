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


<table id="campaign_attachments_table" class="repeat">
    <thead>
		<tr>
            <th width="50%" nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.common.supportDocs"/></label></th>
            <th width="50%" nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.customReportAnswer.mandatory"/></label></th>            
        </tr>
    </thead>
    <tbody id="campaign_attchament_table_tbody"/>
</table>
<u:jsVar varName="campaignAttachmentRowTemplate">
    <tr index="${index}">
        <td>
            <a id="attached_campaign_file_name_${index}">${name}</a>
        </td>
       	<td align="center">       
	        <s:checkbox disabled="true" name="campaign.attachments[${index}].mandatory" id="mandatory_${index}"/>
	        <script type="text/javascript">
	        	var flag = '${mandatory}';
	        	if(flag=='true'){
					dojo.byId("mandatory_${index}").checked=true;	
	        	}
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("attached_campaign_file_name_${index}"), "onclick", function(event) {
                        dojo.stopEvent(event);
                        getFileDownloader().download("downloadDocument.action?docId=${id}");
                    });
                    
                });
	        	
            </script>	        
	 	</td>
    </tr>
</u:jsVar>
<script type="text/javascript">
    
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
    
    dojo.addOnLoad(function() {
        
        var uploadManager = new twms.campaign.AttachmentUploadRenderer(dojo.byId("campaign_attchament_table_tbody"),
                                                                    campaignAttachmentRowTemplate,
                                                                    <s:property value="JSONifiedCampaignAttachmentList" escape="false"/>);
        
    });
            
   
</script>