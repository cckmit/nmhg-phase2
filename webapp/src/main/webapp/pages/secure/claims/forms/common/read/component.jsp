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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>

<s:if test="!partsReplacedInstalledSectionVisible">
	<jsp:include flush="true" page="oempartreplaced.jsp"/>
</s:if>	

<s:if test="!partsClaim || claim.partInstalled">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" >
		<div style="margin:5px">
		<div class="mainTitle" style="padding-bottom:5px;margin-top:5px;">
		<s:text name="label.newClaim.outsidePartsServices"/>
		</div>
		
			<table class="grid borderForTable" style="margin-left:0px; width:97%" cellpadding="0" cellspacing="0">
				<tr class="row_head">
					<th><s:text name="label.common.description"/></th>
					<th><s:text name="label.common.quantity"/></th>
					<s:if test="claim.forMultipleItems">
					<th width="10%"><s:text name="label.common.claim/inventoryLevel"/></th>
					</s:if>
					<authz:ifUserNotInRole roles="supplier">
					 <s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id
					 	|| claim.filedBy.id == loggedInUser.id">
                        <th><s:text name="label.newClaim.unitPrice"/></th>
                    </s:if>
                    </authz:ifUserNotInRole>
                    <s:if test="!loggedInUserADealer">
                    <th><s:text name="label.newClaim.invoice"/></th>
                    </s:if>
				</tr>
				<s:iterator value="claim.serviceInformation.serviceDetail.nonOEMPartsReplaced">
					<tr>
						<td class="labelStyle" width="20%" nowrap="nowrap"><s:property value="description"/></td>
						<td class="numeric"><s:property value="numberOfUnits"/> </td>
						<s:if test="claim.forMultipleItems">
						<td class="labelStyle" nowrap="nowrap">
				            <s:if test="inventoryLevel">
				            <s:text name="accordion_jsp.accordionPane.inventory"/>
				            </s:if>
				            <s:else>
				            <s:text name="claim.prieview.ContentPane.claim"/>
				            </s:else>
            			</td>
            			</s:if>
            			<authz:ifUserNotInRole roles="supplier">
                            <s:if test="loggedInUserAnInternalUser || claim.forDealer.id == loggedInUsersDealership.id
                            	|| claim.filedBy.id == loggedInUser.id">
	                            <td class="numeric"><s:property value="pricePerUnit"/></td>
                            </s:if>
                        </authz:ifUserNotInRole>
                        <td>
                        <s:if test="!loggedInUserADealer">
                <a href="#" id="invoice_<s:property value='%{invoice.id}' />"><s:text name="label.newClaim.attachInvoice"/></a>
                <a id="downloadInvoice_<s:property value='%{invoice.id}' />" href="#">
                    <span class="documentName"><s:property value="invoice.fileName"/></span>
                </a>
                </s:if>
                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        var downloadLink = dojo.byId("downloadInvoice_<s:property value='invoice.id' />");
                        var attachInvoiceLink = dojo.byId("invoice_<s:property value='invoice.id' />");
                        var attachedInvoiceId = "<s:property value='invoice.id'/>";
                        
                        dojo.html.hide(downloadLink);
                        <s:if test="invoice != null">
                            showFileDownloadLink(attachInvoiceLink, downloadLink, attachedInvoiceId,
                                    <s:property value="invoice.id"/>, '<s:property value="invoice.fileName"/>');
                        </s:if>
                        dojo.connect(attachInvoiceLink, "onclick", function() {
                            attachInvoice(function(doc) {
                                showFileDownloadLink(attachInvoiceLink, downloadLink, attachedInvoiceId, doc.id, doc.name);
                            });
                        });
                        dojo.connect(_getFileHolder(downloadLink), "onclick", function() {
                            getFileDownloader().download(downloadLink.url);
                        });
                    });
                    
                    function showFileDownloadLink(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
                              /*domNode [input type="hidden"]*/ attachedInvoiceId, /*Long*/docId, /*String*/fileName) {
					    dojo.html.hide(attachInvoiceLink);
					    if(downloadLink){
                            _getFileHolder(downloadLink).innerHTML = fileName;
                            downloadLink.url = "downloadDocument.action?docId=" + docId;
                            attachedInvoiceId.value=docId;
                            dojo.html.show(downloadLink);
					    }
					}
					
					function _getFileHolder(downloadLink) {
					    return getElementByClass("documentName", downloadLink);
					}
                </script>
            </td>
					</tr>
				</s:iterator>
		</table>
	</div>
	</div>
</s:if>
<s:if test="(claim.type.type != 'Parts' || claim.partInstalled) && claim.miscPartsConfig">	           
<jsp:include flush="true" page="miscpartreplaced.jsp"/>
</s:if>
