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
<%@taglib prefix="authz" uri="authz"%>
<table id="claim_attachments_table" class="grid borderForTable" style="width:97%">
    <thead>
        <tr class="row_head">
            <th width="50%"><s:text name="label.newClaim.fileName"/></th>
            <th><s:text name="label.lov.attachmentType"/></th>
            <authz:ifUserInRole roles="processor, dsm,recoveryProcessor">
            <th><s:text name="label.common.ShareWithDealer"/>
            </th> 
            <th>
	        <s:text name="label.common.eligibilityToShare"/> 
           </th> </authz:ifUserInRole>
			<th></th>
        </tr>
    </thead>
    <tbody id="claim_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr>
        <input type="hidden" name="task.claim.attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        <td>
            <a id="attached_file_name_${index}" title="Attachment made by ${user}">${name}</a>
        </td>
       
       <td>${description}</td>
        <authz:ifUserInRole roles="processor,dsm,recoveryProcessor">
        <td>${shareWithDealer}</td>
        <td>${shareWithSupplier}</td>
        </authz:ifUserInRole>
        <td>
<%--             <div class="repeat_del" id="attachment_drop_button_${index}"></div> --%>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
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
            	var isLoggedInDealer="<s:property value='isLoggedInUserADealer()'/>";
            	var isSharedWithDealer=dataList[i].shareWithDealer;
            	var isLoggedInSupplier="<s:property value='isLoggedInUserASupplier()'/>";
            	var isSharedWithSupplier=dataList[i].shareWithSupplier;
                var substitutionMap = {
                    id : dataList[i].id,
                    name : dataList[i].name,
                    shareWithDealer : dataList[i].shareWithDealer,
                    share : dataList[i].share,
                    user : dataList[i].user,
                    shareWithSupplier : dataList[i].shareWithSupplier,
                    type : dataList[i].type,
                    description : dataList[i].description,
                    index : this._claim_attchment_row_index++
                };
                var rowMarkup = dojo.string.substitute(this._markupTemplate, substitutionMap);
                var rowScript = dojo.string.substitute(this._scriptTemplate,  substitutionMap);
                var row = dojo.html.createNodesFromText(rowMarkup, true);
                dojo.parser.parse(row);
                if((isLoggedInDealer=='true' && isSharedWithDealer=='true')
                    ||(isLoggedInSupplier=='true' && isSharedWithSupplier=='true')
                   || (isLoggedInSupplier=='false' && isLoggedInDealer=='false')){
                 this._tBody.appendChild(row);
                 eval(rowScript);
                }
            }
        }
    });
</script>
