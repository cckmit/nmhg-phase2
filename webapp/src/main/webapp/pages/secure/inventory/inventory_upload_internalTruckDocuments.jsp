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

<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.Dialog");
</script>
<form id="saveInventoryForm" name="saveInventoryForm" method="POST" action="inventory_save.action?id=<s:property value='inventoryItem.id'/>">
<table id="claim_attachments_table" class="grid borderForTable" width="97%">
    <thead>
        <tr class="row_head">
            <th><s:text name="label.common.fileName"/></th><%--TODO: i18N me--%>
            <th><s:text name="label.lov.attachmentType"/></th>
            <th><s:text name="label.userId" /></th>
            <th><s:text name="label.inventory.uploadedDate" /></th>
			 <th width="9%">
			 <div class="attachFile" id="claim_attachment_table_add" >
			 </div></th>
            
        </tr>
    </thead>
    <tbody id="claim_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">
        <input type="hidden" name="internalTruckDocs[${index}]" id="attachmentId_${index}" value="${id}"/>
         <td>
            <a id="attached_file_name_${index}">${name}</a>
        </td>
        <td>
          <s:select id="attachmentTypeCode_${index}" name="internalTruckDocs[${index}].documentType"
						cssClass="processor_decesion"
						list="documentTypes"
						listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
        </td>    
        <script type="text/javascript">
        dojo.addOnLoad(function() {
		    	  var documentType = '${type}';
		    	  if(documentType != '')
		    		  dojo.byId("attachmentTypeCode_${index}").value = documentType;
        });
		  </script>   
		  <td>
		  ${user}
		  </td>
		  <td>
		  ${date}
		  </td>
        <td>
            <div class="repeat_del" id="attachment_drop_button_${index}" name="attachment_drop_down"></div>
            <script type="text/javascript">
            
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "internalTruckDocs");
                        dojo.dom.destroyNode(row);
                        var list = document.getElementsByName("attachment_drop_down"); 
                        if(list.length==0)
                        {
                        	document.getElementById('saveRecord').style.display='none';   
                        }
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
	var attachment = dojo.byId("attachmentId_${index}");
	if(attachment==null){
		document.getElementById('saveRecord').style.display='none';
	}
});
</script>

  <div>
  	<s:submit align="middle" cssClass="button" id="saveRecord" value="%{getText('button.common.save')}" action="inventory_save"></s:submit>
    </div>

<script type="text/javascript">
    dojo.addOnLoad(function() {
        var alreadyPresentDocs = <s:property value="JSONifiedInternalTruckDocsList" escape="false"/>;
        var uploadManager = new twms.inventoryItem.warranty.AttachmentUploadRenderer(dojo.byId("claim_attchament_table_tbody"),
                                                                    attachmentRowTemplate,
                                                                    <s:property value="JSONifiedInternalTruckDocsList" escape="false"/>);
        if(alreadyPresentDocs!=''){
        	document.getElementById('saveRecord').style.display='';
        }else{
        	document.getElementById('saveRecord').style.display='none';
        }
        dojo.connect(dojo.byId("claim_attachment_table_add"),"onclick",
        	dojo.hitch(uploadManager, uploadManager.requestBatchAddition));
        });
        	
    dojo.declare("twms.inventoryItem.warranty.AttachmentUploadRenderer", null, {

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
                    if(docDataList!=''){
                    	document.getElementById('saveRecord').style.display='';
                    }
                    
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
                    share : dataList[i].share,
                    type : dataList[i].type,
                    date:dataList[i].date,
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
</form>
