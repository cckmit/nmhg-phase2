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

<table id="claim_attachments_table" class="grid borderForTable" width="97%">
    <thead>
        <tr class="row_head">
            <th><s:text name="label.common.fileName"/></th><%--TODO: i18N me--%>
            <th><s:text name="label.lov.attachmentType"/></th>
           
			<th width="9%"><div class="attachFile" id="claim_attachment_table_add"/> </th>
            
        </tr>
    </thead>
    <tbody id="claim_attchament_table_tbody"/>
</table>
<u:jsVar varName="attachmentRowTemplate">
    <tr index="${index}">
        <input type="hidden" name="claim.attachments[${index}]" id="attachmentId_${index}" value="${id}"/>
        
        <td>
            <a id="attached_file_name_${index}" title="Attachment made by ${user}">${name}</a>
        </td>
        <td>
        
          <s:select id="attachmentTypeCode_${index}" name="claim.attachments[${index}].documentType"
						cssClass="processor_decesion"
						list="getLovsForClass('DocumentType',claim)"
						theme="twms" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
            <script type="text/javascript">
        dojo.addOnLoad(function() {
		    	  var documentType = '${type}';	
		    	  if(documentType != '')
		    		  dijit.byId("attachmentTypeCode_${index}").set('value',documentType);
		    	  
        });
		  </script> 
        
         
        </td>    
                            
        
  
        <td>
            <div class="repeat_del" id="attachment_drop_button_${index}"/>
            <script type="text/javascript">
            
                dojo.addOnLoad(function() {
                    var dropButton = dojo.byId("attachment_drop_button_${index}");
                    dojo.connect(dropButton, "onclick", function() {
                        var row = getExpectedParent(dropButton, "tr");
                        requestDeletion(row, "claim.attachments");
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
<authz:ifDealer>
	  <s:if test="legalDisclaimerAllowed"> 
	    	<div id="disclaimer" class="mainTitle" style="margin:10px 0px 5px 0px">
			 <s:text name="title.viewClaim.legalDisclaimer"/>
			 </div>
			 <div class="borderTable">&nbsp;</div>
	            <table border ="0" class="grid">
	            	<tr>
	            		<td  align="left" width="60%"><center>
	            			<s:text name="message.legalDisclaimer">
	            			<s:param ><s:property value="getLoggedInUser().getCompleteName()"/></s:param>
            			</s:text>
            			</center>
	            		</td>
	            	</tr>
	            	<br/>
	            	<tr>
	            		<td align="center" width="80%">            		
	            		<input type="radio" name="checkAcceptance" id="actionDecidedAccept"  />
	            		<s:text name="legal.disclaimer.accept"/>					
						<input type="radio" name ="checkAcceptance" id="actionDecidedReject" checked="checked"/>
						<s:text name="legal.disclaimer.reject"/>		
	            		</td>             		           		  
	            	</tr>
	            </table>
	       
	        <script type="text/javascript">
	        dojo.addOnLoad(function(){
	                	dojo.html.hide(dojo.byId("submitButton"));                	
	                	dojo.connect(dojo.byId("actionDecidedAccept"), "onclick", function() {
	                       if(dojo.byId("actionDecidedAccept").checked){
	                		dojo.html.show(dojo.byId("submitButton"));                		
	                		}
	                    });
	                    dojo.connect(dojo.byId("actionDecidedReject"), "onclick", function() {
	                       if(dojo.byId("actionDecidedReject").checked){
	                		dojo.html.hide(dojo.byId("submitButton"));                		
	                		}
	                    });
	                })
	        </script>
	    </div>
	   </s:if>     
    </authz:ifDealer>
<div align="center">
		<s:submit id="submitButton" name="button.common.submit" cssClass="buttonGeneric"/>
	</div>
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
            	var isLoggedInDealer="<s:property value='isLoggedInUserADealer()'/>";
            	var isSharedWithDealer=dataList[i].shareWithDealer;
            	var isLoggedInSupplier="<s:property value='isLoggedInUserASupplier()'/>";
            	var isSharedWithSupplier=dataList[i].shareWithSupplier;
                var substitutionMap = {
                    id : dataList[i].id,
                    name : dataList[i].name,
                    user : dataList[i].user,
                    share : dataList[i].share,
                    type : dataList[i].type,
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