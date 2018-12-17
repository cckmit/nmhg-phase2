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

<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%> 
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<table id="recovery_claim_attachments_table" class="grid borderForTable" width="97%">
    <thead>
        <tr class="row_head">
            <th width="40%"><s:text name="label.common.fileName"/></th><%--TODO: i18N me--%>                         
             <th><s:text name="label.lov.attachmentType"/></th> 
             <s:if test="recoveryClaim.claim.state.state != 'draft'">
            <authz:ifUserInRole roles="recoveryProcessor,processor,dealerWarrantyAdmin">
            <th>
	        <s:text name="label.common.ShareWithSupplier"/> 
			</th>
			</authz:ifUserInRole>
			<authz:ifUserInRole roles="recoveryProcessor, supplier">
            <th>
	        <s:text name="label.common.ShareWithDealer"/> 
			</th>
            </authz:ifUserInRole>  
             </s:if>              
			 <th width="9%"><div class="attachFile" id="recovery_claim_attachment_table_add"/></th>
            
        </tr>
    </thead>
    <tbody id="recovery_claim_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">
        <input type="hidden" name="attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        <%-- <input type="hidden" name="recoveryClaim.claim.attachments[${index}]" id="attachmentIdClaim_${index}" value="${id}"/> --%>
        <td>        
            <a id="attached_file_name_${index}" title="Attachment made by ${user}">${name}</a>
        </td>
       
		<td>
          <s:if test="!isTaskNameofCurrentRecoveryClaim()">
        	 <s:select id="attachmentTypeCode_${index}" name="attachments[${index}].documentType"
						cssClass="processor_decesion"
						list="getLovsForClass('RecoveryClaimDocumentType',recoveryClaim)"
						theme="twms" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
						
				<script type="text/javascript">
				dojo.addOnLoad(function() {
			    	  var recoveryClaimDocumentType = '${type}';	
			    	  if(recoveryClaimDocumentType != '')
			    		  dijit.byId("attachmentTypeCode_${index}").set('value',recoveryClaimDocumentType);
			    	  
	        });
		    	  </script> 
		    	  </s:if>		    	  
				<s:else> ${description} </s:else> 
				<authz:ifDealer>
					 ${description} 
				</authz:ifDealer>
        </td>
         <authz:ifUserInRole
			roles="recoveryProcessor,processor">
			<td align="center">	
			<s:if test="!isTaskNameofCurrentRecoveryClaim()">		
			<s:checkbox name="attachments[${index}].isSharedWithSupplier" id="sharedWithSupplier_${index}"/>	
			<s:hidden name="attachments[${index}].isSharedWithSupplier" id="saveFalseValueSharedWithSupplier_${index}" value="0"/>
				 <script type="text/javascript">
						var flag = '${shareWithSupplier}';
						if (flag == 'true') {
							dojo.byId("sharedWithSupplier_${index}").checked = true;
						}
					</script> 
				</s:if>		    	  
			<s:else> ${shareWithSupplier} </s:else> 
					
					</td>
		</authz:ifUserInRole>
		<authz:ifUserInRole roles="recoveryProcessor,supplier">
			<td align="center">
			
			<s:if test="!isTaskNameofCurrentRecoveryClaim()">		
			<s:checkbox name="attachments[${index}].isSharedWithDealer" id="sharedWithDealer_${index}"/>	
			<s:hidden name="attachments[${index}].isSharedWithDealer" id="saveFalseValueSharedWithDealer_${index}" value="0"/>
			<script type="text/javascript">
						var flag = '${shareWithDealer}';
						if (flag == 'true') {
							dojo.byId("sharedWithDealer_${index}").checked = true;
						}
					</script>
					</s:if>		    	  
			<s:else> ${shareWithDealer} </s:else> 
					</td>
		</authz:ifUserInRole>   
			
		<td>
            <div class="repeat_del" id="attachment_drop_button_${index}"> </div>
            </td>
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
        
        
    </tr>
</u:jsVar>
<u:jsVar varName="attachmentRowTemplateSupplier">
    <tr index="${index}">
        <input type="hidden" name="attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        <td>
        	<a id="supplier_view_attached_file_name_${index}" title="Attachment made by ${user}">${name}</a>
        </td>
        	
		<td>
		  <s:select id="attachmentTypeCode_${index}" name="attachments[${index}].documentType"
						cssClass="processor_decesion"
						list="getLovsForClass('RecoveryClaimDocumentType',recoveryClaim)"
						theme="twms" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
        
        <script type="text/javascript">
        dojo.addOnLoad(function() {
		    	  var recoveryClaimDocumentType = '${type}';	
		    	  if(recoveryClaimDocumentType != '')
		    		  dijit.byId("attachmentTypeCode_${index}").set('value',recoveryClaimDocumentType);
		    	  
        });
		  </script> 
		  <authz:ifDealer>
					 ${description} 
				</authz:ifDealer>
		 
		</td>		
		 <authz:ifUserInRole
			roles="recoveryProcessor,processor">
			<td align="center"><s:checkbox
					name="attachments[${index}].isSharedWithSupplier"
					id="shared_With_Supplier_${index}"/>				
			
					</td>
		</authz:ifUserInRole>
		 <authz:ifUserInRole roles="recoveryProcessor,supplier">
			<td align="center"><s:checkbox
					name="attachments[${index}].isSharedWithDealer"
					id="shared_With_Dealer_${index}"/></td>
		</authz:ifUserInRole>  
		<authz:ifUserInRole roles="recoveryProcessor, supplier">
        	<td>
        		<div class="repeat_del" id="supplier_view_attachment_drop_button_${index}"/>
        	</td>  
        </authz:ifUserInRole>      
        <script type="text/javascript">
            
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("supplier_view_attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "attachments");
                        dojo.dom.destroyNode(row);
                    });
                    dojo.connect(dojo.byId("supplier_view_attached_file_name_${index}"), "onclick", function(event) {
                        dojo.stopEvent(event);
                        getFileDownloader().download("downloadDocument.action?docId=${id}");
                    });
                });
            </script>
            
    </tr>
</u:jsVar>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        var alreadyPresentDocs = <s:property value="JSONifiedRecoveryClaimAttachmentList" escape="false"/>;
        var uploadManager = new twms.recoveryClaim.AttachmentUploadRenderer(dojo.byId("recovery_claim_attchament_table_tbody"),
                                                                    attachmentRowTemplate,
                                                                    attachmentRowTemplateSupplier,
                                                                    <s:property value="JSONifiedRecoveryClaimAttachmentList" escape="false"/>);
        if(dojo.byId("recovery_claim_attachment_table_add")){
        	dojo.connect(dojo.byId("recovery_claim_attachment_table_add"), "onclick",
                    dojo.hitch(uploadManager, uploadManager.requestBatchAddition));
        }        
    });

    dojo.declare("twms.recoveryClaim.AttachmentUploadRenderer", null, {

        _claim_attchment_row_index : 0,
        _tBody : null,

        _markupTemplate: null,
        _scriptTemplate : null,
        _markupTempleteHidden: null,
        _scriptTempleteHidden: null,

        constructor : function(/*DomNode (tbody)*/ tBody,
                               /*String (template)*/ template,
                               /*String (template)*/ hiddenTemplate,
                               /*collection of document's id, name and stuff...*/ alreadyUploadedDocList) {
            this._tBody = tBody;
            this._markupTemplate = template.markup;
            this._scriptTemplate = template.script;
            this._markupTemplateHidden = hiddenTemplate.markup;
            this._scriptTempleteHidden = hiddenTemplate.script;
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
            publishEvent("/uploadDocumentForRecoveryClaim/dialog/show", message);
        },

        _addRows : function(/*collection of document's id, name and stuff...*/ dataList) {
            for (var i in dataList) {
                var substitutionMap = {
                    id : dataList[i].id,
                    name : dataList[i].name,
                    user : dataList[i].user,
                    share : dataList[i].share,
                    shareWithSupplier : dataList[i].shareWithSupplier,
                    shareWithDealer : dataList[i].shareWithDealer, 
                    type : dataList[i].type,
                    description : dataList[i].description,
                    index : this._claim_attchment_row_index++,
                    hidden : dataList[i].hidden                    
                };
                var rowMarkup = null;
                var rowScript = null;
                if(dataList[i].hidden){
                	rowMarkup = dojo.string.substitute(this._markupTemplateHidden, substitutionMap);
                    rowScript = dojo.string.substitute(this._scriptTempleteHidden,  substitutionMap);
                }else{
                	rowMarkup = dojo.string.substitute(this._markupTemplate, substitutionMap);
                    rowScript = dojo.string.substitute(this._scriptTemplate,  substitutionMap);	
                }
                
                var row = dojo.html.createNodesFromText(rowMarkup, true);
                dojo.parser.parse(row);
                this._tBody.appendChild(row);
                eval(rowScript);
            }
        }
    });
</script>