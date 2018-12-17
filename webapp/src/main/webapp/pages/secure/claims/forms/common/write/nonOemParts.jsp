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
 <div class="mainTitle" style="margin-top:10px;margin-bottom:5px;">
	<s:text name="label.newClaim.outsidePartsServices"/></div>
	<div style={color:red} id="OEMPartInNonOEMPartError">
	<s:text name="error.claim.oemPartInNonOem" />
	</div>
<u:repeatTable id="outside_parts_replaced_table" cssClass="grid borderForTable" width="97%">
    <thead>
             <tr class="row_head">
        	<th ><s:text name="label.common.description"/></th>
            <th><s:text name="label.common.quantity"/></th>
            <th ><s:text name="label.newClaim.unitPrice"/></th>
            <th ><s:text name="label.newClaim.invoice"/></th>
            <s:if test="task.claim.forMultipleItems">
            	<th><s:text name="label.common.claim/inventoryLevel"/></th>
            </s:if>
            <th width="9%"><u:repeatAdd id="outside_parts_adder"><div class="repeat_add"></div></u:repeatAdd></th>
        </tr>
    </thead>
    <u:repeatTemplate id="outside_parts_replaced_body"
        value="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced">
        <tr index="#index">
        	<td>
                <s:hidden name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index]"/>
                <s:textfield size="50" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].description"
                    id="nonoem_description_#index" onblur="checkNonOEMPartNumber(dojo.byId('nonoem_description_#index').value)" />
            </td>
            <td>
                <s:textfield size="3" cssStyle="text-align: right;padding-right: 1px" id="nonoempart_qty_#index"
                    name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].numberOfUnits"/>
            </td>
            <td>
            <t:money id="unit_price_#index"
                    name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].pricePerUnit"
                    value="%{task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].pricePerUnit}"
                    defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}" size="10"/>
            </td>     
            <s:hidden name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].invoice"
                id="hiddenNonOemInvoice_#index"/>
            <td>
                <a href="#" id="invoice_#index"><s:text name="label.newClaim.attachInvoice"/></a>
                <a id="downloadInvoice_#index" href="#">
                    <span class="documentName"><s:property value="invoice.fileName"/></span>
                    <img class="dropUpload" src="image/remove.gif" id="deleteForUpload_#index"/>
                </a>
                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        var downloadLink = dojo.byId("downloadInvoice_#index");
                        var attachInvoiceLink = dojo.byId("invoice_#index");
                        var attachedInvoiceId = dojo.byId("hiddenNonOemInvoice_#index");
                        dojo.html.hide(downloadLink);
                        <s:if test="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].invoice != null">
                            showFileDownloadLink(attachInvoiceLink, downloadLink, attachedInvoiceId,
                                    <s:property value="invoice.id"/>, "<s:property value="invoice.fileName"/>");
                        </s:if>
                        dojo.connect(_getUploadDropButton(downloadLink), "onclick", function() {
                            dropAttachedInvoice(attachInvoiceLink, downloadLink, attachedInvoiceId);
                        });
                        dojo.connect(attachInvoiceLink, "onclick", function() {
                            attachInvoice(function(doc) {
                                showFileDownloadLink(attachInvoiceLink, downloadLink, attachedInvoiceId, doc[0].id, doc[0].name);
                            });
                        });
                        dojo.connect(_getFileHolder(downloadLink), "onclick", function() {
                            getFileDownloader().download(downloadLink.url);
                        });
                    });
                    
                    dojo.addOnLoad(function()
					{
						dojo.connect(dojo.byId("nonoempart_qty_#index"), "onchange", function(evt){
							var oemPartIndex = #index;					 
							if(dojo.byId("nonoempart_qty_"+oemPartIndex) && dojo.byId("nonoempart_qty_"+oemPartIndex).value)
							{
									dojo.byId("nonoempart_qty_"+oemPartIndex).value = Trim(dojo.byId("nonoempart_qty_"+oemPartIndex).value);	 					
								}
							}); 
					});
                </script>
            </td>
            <s:if test="task.claim.forMultipleItems">
            <td>
            <table border="0">            
            <s:if test="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced.empty ||
            task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel==null">
            <tr>
            <td  style="border:none">            
            <span id="level_for_part_#index_true">
            <input type="radio"  value="true" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="accordion_jsp.accordionPane.inventory"/>
            </span> 
            </td>
            </tr>
            <tr>
            <td  style="border:none">            
            <span id="level_for_part_#index_false">
            <input type="radio" checked="checked" value="false" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="claim.prieview.ContentPane.claim"/>
            </span>
            </td>
            </tr>
            </s:if>
            <s:else>            
            <s:if test="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel">
            <tr>
            <td  style="border:none">
            <span id="level_for_part_#index_true">
            <input type="radio" checked="checked"  value="true" id="inventory_level_#index" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="accordion_jsp.accordionPane.inventory"/>
            </span>
            </td>
            </tr>
            <tr> 
            <td  style="border:none">           
            <span id="level_for_part_#index_false">
            <input type="radio" id="claim_level_#index" value="false" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="claim.prieview.ContentPane.claim"/>
            </span>
            </td>
            </tr>                        
            </s:if>
            <s:else>
            <tr>
            <td  style="border:none">
            <span id="level_for_part_#index_true">
            <input type="radio"   value="true" id="inventory_level_#index" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="accordion_jsp.accordionPane.inventory"/>
            </span>
            </td>  
            </tr>
            <tr> 
            <td  style="border:none">         
            <span id="level_for_part_#index_false">
            <input type="radio" checked="checked" id="claim_level_#index" value="false" name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="claim.prieview.ContentPane.claim"/>
            </span>
            </td>
            </tr> 
            </s:else>            
            </s:else>
            </table>
            </td>            
            </s:if>
            <s:else>
            <s:hidden name="task.claim.serviceInformation.serviceDetail.nonOEMPartsReplaced[#index].inventoryLevel" value="false"/>
            </s:else>
            <td>            
                <u:repeatDelete id="outside_parts_deleter_#index">
                    <div class="repeat_del"></div>
                  <script type="text/javascript">
                  dojo.addOnLoad(function() {
                	  dojo.connect(dojo.byId('outside_parts_deleter_#index'), "onclick", function(evt){
                			dojo.html.hide(dojo.byId("OEMPartInNonOEMPartError"));
                	  });
                  });                  
                   </script>
                </u:repeatDelete>
            </td>
        </tr>
    </u:repeatTemplate>
</u:repeatTable>

<script type="text/javascript">
function attachInvoice(/*Function*/ callback) {
    dojo.publish("/uploadDocument/dialog/show", [{callback : callback}]);
}
function showFileDownloadLink(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
                              /*domNode [input type="hidden"]*/ attachedInvoiceId, /*Long*/docId, /*String*/fileName) {
    dojo.html.hide(attachInvoiceLink);
    _getFileHolder(downloadLink).innerHTML = fileName;
    downloadLink.url = "downloadDocument.action?docId=" + docId;
    attachedInvoiceId.value=docId;
    dojo.html.show(downloadLink);
    if(fileName.length==0){
        dojo.html.hide(downloadLink);
        dojo.html.show(attachInvoiceLink);
    }
}
function dropAttachedInvoice(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
                             /*domNode [input type="hidden"]*/ attachedInvoiceId) {
    downloadLink.url="";
    attachedInvoiceId.value = "";
    dojo.html.hide(downloadLink);
    dojo.html.show(attachInvoiceLink);
}
function _getFileHolder(downloadLink) {
    return getElementByClass("documentName", downloadLink);
}
function _getUploadDropButton(downloadLink) {
    return getElementByClass("dropUpload", downloadLink);
}
function checkNonOEMPartNumber(description){
	twms.ajax.fireJsonRequest("checkOEMPartInNonOEMPart.action?nonOEMPartDescription="+description, {
    }, function(details) {
    	if(details){
    		dojo.html.show(dojo.byId("OEMPartInNonOEMPartError"));
    	}
    	else{
    		dojo.html.hide(dojo.byId("OEMPartInNonOEMPartError"));
    	}
    });
}
dojo.addOnLoad(function(){
	dojo.html.hide(dojo.byId("OEMPartInNonOEMPartError"));
});
</script>

