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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>
<script type="text/javascript">
	dojo.require("dijit.Tooltip");
	var hoverMessage = new dijit.Tooltip({
		connectId: ["myElement1","myElement2"]
	});
</script>

<script type="text/javascript">
	function attachForIncidentals(/*Function*/callback) {
		dojo.publish("/uploadDocument/dialog/show", [ {
			callback : callback
		} ]);
	}
	function _getIncidentalsFileHolder(attachDownloadLinkInvoice) {
		return getElementByClass("documentName", attachDownloadLinkInvoice);
	}
	function _getIncidentalsUploadDropButton(attachDownloadLinkInvoice) {
		return getElementByClass("dropUpload", attachDownloadLinkInvoice);
	}

	function downloadfile(invoice, docId, docName) {
		dojo.html.hide(invoice.linkId);
		_getIncidentalsFileHolder(invoice.downloadId).innerHTML = docName;
		invoice.downloadId.url = "downloadDocument.action?docId=" + docId;
		dojo.html.show(invoice.deleteImageId);
		dojo.html.show(invoice.downloadId);
		if (dojo.byId(invoice.hiddenId.id) == null) {
			dojo.create("input", {
				type : "hidden",
				name : invoice.hiddenId.name,
				value : docId,
				id : invoice.hiddenId.id
			}, invoice.hiddenId.parentId);
		}
		dojo.byId(invoice.hiddenId.id).value = docId;
		if (docName.length == 0) {
			dojo.html.hide(invoice.downloadId);
			dojo.html.show(invoice.linkId);
		}
	}
	function dropAttachedInvoiceForIncidentals(incidentals, invoiceName) {
		var invoice = new invoiceObject(incidentals, invoiceName);
		invoice.hiddenId = new myHiddenObject("parentInvoice_" + incidentals,
				"hidden_" + incidentals, "",
				"task.claim.serviceInformation.serviceDetail." + invoiceName);
		invoice.downloadId.url = "";
		dojo.html.hide(invoice.downloadId);
		dojo.html.show(invoice.linkId);
		dojo.html.hide(invoice.deleteImageId);
		invoice.hiddenId.value = "";
		dojo.byId(invoice.hiddenId.id).value = "";
	}

	function invoiceObject(forInvoice) {
		this.linkId = dojo.byId("linkId_" + forInvoice);
		this.downloadId = dojo.byId("downloadId_" + forInvoice);
		this.hiddenId = myHiddenObject;
		this.deleteImageId = dojo.byId("deleteImageId_" + forInvoice);
	}

	/* This is our hidden field id */
	function myHiddenObject(parentId, id, value, name) {
		this.parentId = parentId;
		this.id = id;
		this.value = value;
		this.name = name;
	}

	function attachDoc(incidentals, invoiceName) {
		var invoice = new invoiceObject(incidentals, invoiceName);
		invoice.hiddenId = new myHiddenObject("parentInvoice_" + incidentals,
				"hidden_" + incidentals, "",
				"task.claim.serviceInformation.serviceDetail." + invoiceName);
		attachForIncidentals(function(doc) {
			downloadfile(invoice, doc[0].id, doc[0].name);
		});
	}

	function downloadUploadedFile(hiddenId) {
		getFileDownloader().download(
				"downloadDocument.action?docId=" + dojo.byId(hiddenId).value);
	}
     
	function recalculateTranspotation(){	
		var claimId=<s:property value="task.claim"/>;		
		
			if(dojo.byId('transportationCheckBox')!=null && dojo.byId('transportationCheckBox').checked == true)
				{						
				 var url = 'getTranspotationRate.action';  							
				 twms.ajax.fireJavaScriptRequest(url, {
					 claim: claimId                     
                 }, function(details) {
                	 if(dojo.byId("transportation_duty")!=null)
                		 {
                		var distanceInMile= parseFloat(dojo.byId("base_travel_distance").value);                	
                		var transporationRate=details[0];
                		var transportationAmount=distanceInMile*transporationRate;
                     	dojo.byId("transportation_duty").value = transportationAmount; 
                     	dijit.byId("transportation_duty").set("readOnly", true);
                      	 document.getElementsByName("task.claim.serviceInformation.serviceDetail.transportationAmt")[1].value=transportationAmount;
                		 }
               });				
		}
			else
				{
				if(dojo.byId("transportation_duty")!=null)
					dojo.byId("transportation_duty").value = ''; 
				 document.getElementsByName("task.claim.serviceInformation.serviceDetail.transportationAmt")[1].value='';
					dijit.byId("transportation_duty").set("readOnly", false);
				}
	}
	
</script>

<div class="sub_section">

	<s:hidden name="task.claim.oemConfig" value="true" />
	<s:hidden name="task.claim.nonOemConfig" value="true" />
	<div class="mainTitle">
		<s:text name="label.newClaim.incidentals" />
	</div>
	<div class="borderTable">&nbsp;</div>
	<table class="grid" cellspacing="0" cellpadding="0"
		style="width: 95%; margin-top: -10px;">

		<tr>
			<s:if test="task.claim.itemDutyConfig">
				<s:hidden name="task.claim.itemDutyConfig" value="true" />
				<td width="20%"><label for="travel_duty" class="labelStyle"><s:text
							name="label.newClaim.itemFreightDuty" />:</label></td>
				<td width="27%"><t:money id="travel_duty"
						name="task.claim.serviceInformation.serviceDetail.itemFreightAndDuty"
						value="%{task.claim.serviceInformation.serviceDetail.itemFreightAndDuty}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_freightDuty"><s:if
						test="task.claim.serviceInformation.serviceDetail.freightDutyInvoice.fileName != null">
						<a href="#" id="linkId_freightDuty" style="cursor:pointer"
							onclick="attachDoc('freightDuty', 'freightDutyInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_freightDuty" href="#"
							onclick="downloadUploadedFile('hidden_freightDuty')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.freightDutyInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_freightDuty"
							onclick="dropAttachedInvoiceForIncidentals('freightDuty', 'freightDutyInvoice')" />
						</a>
						<s:hidden id="hidden_freightDuty"
							name="task.claim.serviceInformation.serviceDetail.freightDutyInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.freightDutyInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_freightDuty" style="cursor:pointer"
							onclick="attachDoc('freightDuty', 'freightDutyInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
								<s:hidden id="hidden_freightDuty"
							name="task.claim.serviceInformation.serviceDetail.freightDutyInvoice"
							value="" />	
						<a id="downloadId_freightDuty" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_freightDuty')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.freightDutyInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_freightDuty"
							onclick="dropAttachedInvoiceForIncidentals('freightDuty', 'freightDutyInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.itemDutyConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.itemFreightAndDuty"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.itemFreightAndDuty"
					value="0.00" />
			</s:else>

		</tr>

		<tr>
			<s:if test="task.claim.mealsConfig">
				<s:hidden name="task.claim.mealsConfig" value="true" />
				<td width="20%"><label for="meals_expense" class="labelStyle"><s:text
							name="label.newClaim.meals" />:</label></td>
				<td width="27%"><t:money id="meals_expense"
						name="task.claim.serviceInformation.serviceDetail.mealsExpense"
						value="%{task.claim.serviceInformation.serviceDetail.mealsExpense}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_meals"><s:if
						test="task.claim.serviceInformation.serviceDetail.mealsInvoice.fileName != null">
						<a href="#" id="linkId_meals" style="display: none"
							onclick="attachDoc('meals', 'mealsInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_meals" href="#"
							onclick="downloadUploadedFile('hidden_meals')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.mealsInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_meals"
							onclick="dropAttachedInvoiceForIncidentals('meals', 'mealsInvoice')" />
						</a>
						<s:hidden id="hidden_meals"
							name="task.claim.serviceInformation.serviceDetail.mealsInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.mealsInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_meals" style="cursor:pointer"
							onclick="attachDoc('meals', 'mealsInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
								<s:hidden id="hidden_meals"
							name="task.claim.serviceInformation.serviceDetail.mealsInvoice"
							value="" />
						<a id="downloadId_meals" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_meals')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.mealsInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_meals"
							onclick="dropAttachedInvoiceForIncidentals('meals', 'mealsInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.mealsConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.mealsExpense"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.mealsExpense"
					value="0.00" />
			</s:else>
		</tr>
		<tr>
			<s:if test="task.claim.perDiemConfig">
				<s:hidden name="task.claim.perDiemConfig" value="true" />
				<td width="20%"><label for="per_diem" class="labelStyle"><s:text
							name="label.newClaim.perDiem" />:</label></td>
				<td width="27%"><t:money id="per_diem"
						name="task.claim.serviceInformation.serviceDetail.perDiem"
						value="%{task.claim.serviceInformation.serviceDetail.perDiem}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_perDiem"><s:if
						test="task.claim.serviceInformation.serviceDetail.perDiemInvoice.fileName != null">
						<a href="#" id="linkId_perDiem" style="display: none"
							onclick="attachDoc('perDiem', 'perDiemInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_perDiem" href="#"
							onclick="downloadUploadedFile('hidden_perDiem')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.perDiemInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_perDiem"
							onclick="dropAttachedInvoiceForIncidentals('perDiem', 'perDiemInvoice')" />
						</a>
						<s:hidden id="hidden_perDiem"
							name="task.claim.serviceInformation.serviceDetail.perDiemInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.perDiemInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_perDiem" style="cursor:pointer"
							onclick="attachDoc('perDiem', 'perDiemInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
									<s:hidden id="hidden_perDiem"
							name="task.claim.serviceInformation.serviceDetail.perDiemInvoice"
							value="" />
						<a id="downloadId_perDiem" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_perDiem')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.perDiemInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_perDiem"
							onclick="dropAttachedInvoiceForIncidentals('perDiem', 'perDiemInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.perDiemConfig" value="false" />
				<s:hidden name="task.claim.serviceInformation.serviceDetail.perDiem"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden name="task.claim.serviceInformation.serviceDetail.perDiem"
					value="0.00" />
			</s:else>

		</tr>

		<tr>
			<s:if test="task.claim.parkingConfig">
				<s:hidden name="task.claim.parkingConfig" value="true" />
				<td width="20%"><label for="tol_expense" class="labelStyle"><s:text
							name="label.newClaim.parkingToll" />:</label></td>
				<td width="27%"><t:money id="tol_expense"
						name="task.claim.serviceInformation.serviceDetail.parkingAndTollExpense"
						value="%{task.claim.serviceInformation.serviceDetail.parkingAndTollExpense}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_parking"><s:if
						test="task.claim.serviceInformation.serviceDetail.parkingAndTollInvoice.fileName != null">
						<a href="#" id="linkId_parking" style="display: none"
							onclick="attachDoc('parking', 'parkingAndTollInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_parking" href="#"
							onclick="downloadUploadedFile('hidden_parking')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.parkingAndTollInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_parking"
							onclick="dropAttachedInvoiceForIncidentals('parking', 'parkingAndTollInvoice')" />
						</a>
						<s:hidden id="hidden_parking"
							name="task.claim.serviceInformation.serviceDetail.parkingAndTollInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.parkingAndTollInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_parking" style="cursor:pointer"
							onclick="attachDoc('parking', 'parkingAndTollInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
								<s:hidden id="hidden_parking"
							name="task.claim.serviceInformation.serviceDetail.parkingAndTollInvoice"
							value="" />
						<a id="downloadId_parking" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_parking')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.parkingAndTollInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_parking"
							onclick="dropAttachedInvoiceForIncidentals('parking', 'parkingAndTollInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.parkingConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.parkingAndTollExpense"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.parkingAndTollExpense"
					value="0.00" />
			</s:else>
		</tr>
		<tr>
			<s:if test="task.claim.rentalChargesConfig">
				<s:hidden name="task.claim.rentalChargesConfig" value="true" />
				<td width="20%"><label for="rental_charges" class="labelStyle"><s:text
							name="label.newClaim.rentalCharges" />:</label></td>
				<td width="27%"><t:money id="rental_charges"
						name="task.claim.serviceInformation.serviceDetail.rentalCharges"
						value="%{task.claim.serviceInformation.serviceDetail.rentalCharges}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_rental"><s:if
						test="task.claim.serviceInformation.serviceDetail.rentalChargesInvoice.fileName != null">
						<a href="#" id="linkId_rental" style="display: none"
							onclick="attachDoc('rental', 'rentalChargesInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_rental" href="#"
							onclick="downloadUploadedFile('hidden_rental')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.rentalChargesInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_rental"
							onclick="dropAttachedInvoiceForIncidentals('rental', 'rentalChargesInvoice')" />
						</a>
						<s:hidden id="hidden_rental"
							name="task.claim.serviceInformation.serviceDetail.rentalChargesInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.rentalChargesInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_rental" style="cursor:pointer"
							onclick="attachDoc('rental', 'rentalChargesInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
									<s:hidden id="hidden_rental"
							name="task.claim.serviceInformation.serviceDetail.rentalChargesInvoice"
							value="" />
						<a id="downloadId_rental" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_rental')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.rentalChargesInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_rental"
							onclick="dropAttachedInvoiceForIncidentals('rental', 'rentalChargesInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.rentalChargesConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.rentalCharges"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.rentalCharges"
					value="0.00" />
			</s:else>
		</tr>


		<tr>
			<s:if test="task.claim.localPurchaseConfig">
				<s:hidden name="task.claim.localPurchaseConfig" value="true" />
				<td width="20%"><label for="local_purchase" class="labelStyle"><s:text
							name="label.newClaim.localPurchase" />:</label></td>
				<td width="27%"><t:money id="local_purchase"
						name="task.claim.serviceInformation.serviceDetail.localPurchaseExpense"
						value="%{task.claim.serviceInformation.serviceDetail.localPurchaseExpense}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_local"><s:if
						test="task.claim.serviceInformation.serviceDetail.localPurchaseInvoice.fileName != null">
						<a href="#" id="linkId_local" style="display: none"
							onclick="attachDoc('local', 'localPurchaseInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_local" href="#"
							onclick="downloadUploadedFile('hidden_local')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.localPurchaseInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_local"
							onclick="dropAttachedInvoiceForIncidentals('local', 'localPurchaseInvoice')" />
						</a>
						<s:hidden id="hidden_local"
							name="task.claim.serviceInformation.serviceDetail.localPurchaseInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.localPurchaseInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_local" style="cursor:pointer"
							onclick="attachDoc('local', 'localPurchaseInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
								<s:hidden id="hidden_local"
							name="task.claim.serviceInformation.serviceDetail.localPurchaseInvoice"
							value="" />
						<a id="downloadId_local" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_local')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.localPurchaseInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_local"
							onclick="dropAttachedInvoiceForIncidentals('local', 'localPurchaseInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.localPurchaseConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.localPurchaseExpense"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.localPurchaseExpense"
					value="0.00" />
			</s:else>
		</tr>
		<tr>
			<s:if test="task.claim.tollsConfig">
				<s:hidden name="task.claim.tollsConfig" value="true" />
				<td width="20%"><label for="tolls" class="labelStyle"><s:text
							name="label.newClaim.tolls" />:</label></td>
				<td width="27%"><t:money id="tolls"
						name="task.claim.serviceInformation.serviceDetail.tollsExpense"
						value="%{task.claim.serviceInformation.serviceDetail.tollsExpense}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_tolls"><s:if
						test="task.claim.serviceInformation.serviceDetail.tollsInvoice.fileName != null">
						<a href="#" id="linkId_tolls" style="display: none"
							onclick="attachDoc('tolls', 'tollsInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_tolls" href="#"
							onclick="downloadUploadedFile('hidden_tolls')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.tollsInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_tolls"
							onclick="dropAttachedInvoiceForIncidentals('tolls', 'tollsInvoice')" />
						</a>
						<s:hidden id="hidden_tolls"
							name="task.claim.serviceInformation.serviceDetail.tollsInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.tollsInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_tolls" style="cursor:pointer"
							onclick="attachDoc('tolls', 'tollsInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
									<s:hidden id="hidden_tolls"
							name="task.claim.serviceInformation.serviceDetail.tollsInvoice"
							value="" />
						<a id="downloadId_tolls" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_tolls')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.tollsInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_tolls"
							onclick="dropAttachedInvoiceForIncidentals('tolls', 'tollsInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.tollsConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.tollsExpense"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.tollsExpense"
					value="0.00" />
			</s:else>
		</tr>
		<tr>
			<s:if test="task.claim.otherFreightDutyConfig">
				<s:hidden name="task.claim.otherFreightDutyConfig" value="true" />
				<td width="20%"><label for="other_freight_duty"
					class="labelStyle"><s:text
							name="label.newClaim.otherFreightDuty" />:</label></td>
				<td width="27%"><t:money id="other_freight_duty"
						name="task.claim.serviceInformation.serviceDetail.otherFreightDutyExpense"
						value="%{task.claim.serviceInformation.serviceDetail.otherFreightDutyExpense}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_otherFreight"><s:if
						test="task.claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName != null">
						<a href="#" id="linkId_otherFreight" style="display: none"
							onclick="attachDoc('otherFreight', 'otherFreightDutyInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_otherFreight" href="#"
							onclick="downloadUploadedFile('hidden_otherFreight')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_otherFreight"
							onclick="dropAttachedInvoiceForIncidentals('otherFreight', 'otherFreightDutyInvoice')" />
						</a>
						<s:hidden id="hidden_otherFreight"
							name="task.claim.serviceInformation.serviceDetail.otherFreightDutyInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_otherFreight" style="cursor:pointer"
							onclick="attachDoc('otherFreight', 'otherFreightDutyInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
							<s:hidden id="hidden_otherFreight"
							name="task.claim.serviceInformation.serviceDetail.otherFreightDutyInvoice"
							value="" />
						<a id="downloadId_otherFreight" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_otherFreight')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_otherFreight"
							onclick="dropAttachedInvoiceForIncidentals('otherFreight', 'otherFreightDutyInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.otherFreightDutyConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.otherFreightDutyExpense"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.otherFreightDutyExpense"
					value="0.00" />
			</s:else>
		</tr>
		<tr>
			<s:if test="true">
				<s:hidden name="task.claim.transportation" value="true" /> 
				<td width="20%"><label for="travel_duty" class="labelStyle"><s:text
							name="label.newClaim.transportation" />:</label></td>
				<td width="27%">
					 <t:money id="transportation_duty" 
		                name="task.claim.serviceInformation.serviceDetail.transportationAmt"
		                value="%{task.claim.serviceInformation.serviceDetail.transportationAmt}"
		                defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}" size="18"/>  

				</td>
				<td id="parentInvoice_transportation"><s:if
						test="task.claim.serviceInformation.serviceDetail.transportationInvoice.fileName != null">
						<a href="#" id="linkId_transportation" style="display: none"
							onclick="attachDoc('transportation', 'transportationInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_transportation" href="#"
							onclick="downloadUploadedFile('hidden_transportation')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.transportationInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_transportation"
							onclick="dropAttachedInvoiceForIncidentals('transportation', 'transportationInvoice')" />
						</a>
						<s:hidden id="hidden_transportation"
							name="task.claim.serviceInformation.serviceDetail.transportationInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.transportationInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_transportation" style="cursor:pointer"
							onclick="attachDoc('transportation', 'transportationInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>					
						<s:hidden id="hidden_transportation"
							name="task.claim.serviceInformation.serviceDetail.transportationInvoice"
							value="" />
						<a id="downloadId_transportation" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_transportation')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.transportationInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_transportation"
							onclick="dropAttachedInvoiceForIncidentals('transportation', 'transportationInvoice')" />
						</a>
					</s:else></td>
				<td width="20%"><label for="travel_duty" class="labelStyle"><s:text name="label.newClaim.noInvoiceAvailable"/></label></td>
				<td width="10%">
					<div onmouseover="dijit.Tooltip.defaultPosition=['above','below']" id="hoverMessage">
						<s:checkbox id='transportationCheckBox' name="task.claim.serviceInformation.serviceDetail.invoiceAvailable" value="%{task.claim.serviceInformation.serviceDetail.invoiceAvailable}" onchange="recalculateTranspotation()"/>
						<div class="dijitHidden"><span data-dojo-type="dijit.Tooltip" data-dojo-props="connectId:'hoverMessage'">
								<s:text name="label.newClaim.messageForNoInvoice"/><s:property value="getConfigValueForTransportation()"/><s:text name="label.newClaim.message"/>
								</span>
						</div>	
					</div>	
				</td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.transportation" value="false" />
				<s:hidden name="task.claim.serviceInformation.serviceDetail.transportationAmt" 
							  value="%{task.claim.forDealer.preferredCurrency.symbol}"/> 
					<s:hidden name="task.claim.serviceInformation.serviceDetail.transportationAmt" 
							  value="0.00"/> 
			</s:else> 

		</tr>
<%-- 		<tr>
			<s:if test="task.claim.othersConfig">
				<s:hidden name="task.claim.othersConfig" value="true" />
				<td width="20%"><label for="others" class="labelStyle"><s:text
							name="label.newClaim.others" />:</label></td>
				<td width="27%"><t:money id="others"
						name="task.claim.serviceInformation.serviceDetail.othersExpense"
						value="%{task.claim.serviceInformation.serviceDetail.othersExpense}"
						defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}"
						size="18" /></td>
				<td id="parentInvoice_others"><s:if
						test="task.claim.serviceInformation.serviceDetail.othersInvoice.fileName != null">
						<a href="#" id="linkId_others" style="display: none"
							onclick="attachDoc('others', 'othersInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_others" href="#"
							onclick="downloadUploadedFile('hidden_others')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.othersInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_others"
							onclick="dropAttachedInvoiceForIncidentals('others', 'othersInvoice')" />
						</a>
						<s:hidden id="hidden_others"
							name="task.claim.serviceInformation.serviceDetail.othersInvoice"
							value="%{task.claim.serviceInformation.serviceDetail.othersInvoice.id}" />
					</s:if> <s:else>
						<a href="#" id="linkId_others" style="cursor:pointer"
							onclick="attachDoc('others', 'othersInvoice')"><s:text
								name="label.newClaim.attachInvoice" /></a>
						<a id="downloadId_others" href="#" style="display: none"
							onclick="downloadUploadedFile('hidden_others')"> <span
							class="documentName"><s:property
									value="task.claim.serviceInformation.serviceDetail.othersInvoice.fileName" /></span>
							<img class="dropUpload" src="image/remove.gif"
							id="deleteImageId_others"
							onclick="dropAttachedInvoiceForIncidentals('others', 'othersInvoice')" />
						</a>
					</s:else></td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.othersConfig" value="false" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.othersExpense"
					value="%{task.claim.forDealer.preferredCurrency.symbol}" />
				<s:hidden
					name="task.claim.serviceInformation.serviceDetail.othersExpense"
					value="0.00" />
			</s:else>
		</tr> --%>
        
         <tr>
        	<s:if test="task.claim.handlingFeeConfig">
	        <s:hidden name="task.claim.handlingFeeConfig" value="true"/>
	        <td width="20%">
	            <label for="handling_fee" class="labelStyle"><s:text name="label.section.handlingFee"/>:</label>
	        </td>
	        <td width="27%">
	            <t:money id="handling_fee"
	                name="task.claim.serviceInformation.serviceDetail.handlingFee"
	                value="%{task.claim.serviceInformation.serviceDetail.handlingFee}"
	                defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}" size="18"/>
	        </td>
	        <td id="parentInvoice_handlingFee">
                <s:if test="task.claim.serviceInformation.serviceDetail.handlingFeeInvoice.fileName != null">
                   <a href="#" id="linkId_handlingFee" style="display:none" onclick="attachDoc('handlingFee', 'handlingFeeInvoice')"><s:text name="label.newClaim.attachInvoice"/></a>
                   <a id="downloadId_handlingFee" href="#" onclick="downloadUploadedFile('hidden_handlingFee')">
                      <span class="documentName" ><s:property value="task.claim.serviceInformation.serviceDetail.handlingFeeInvoice.fileName" /></span>
                      <img class="dropUpload" src="image/remove.gif" id="deleteImageId_handlingFee" onclick="dropAttachedInvoiceForIncidentals('handlingFee', 'handlingFeeInvoice')"/>
                   </a>
                   <s:hidden id="hidden_handlingFee" name="task.claim.serviceInformation.serviceDetail.handlingFeeInvoice" value="%{task.claim.serviceInformation.serviceDetail.handlingFeeInvoice.id}"/>
                </s:if>
                <s:else>
                  <a href="#" id="linkId_handlingFee" style="display:block" onclick="attachDoc('handlingFee', 'handlingFeeInvoice')"><s:text name="label.newClaim.attachInvoice"/></a>
                  <s:hidden id="hidden_handlingFee"
							name="task.claim.serviceInformation.serviceDetail.handlingFeeInvoice"
							value="" />
                  <a id="downloadId_handlingFee" href="#" style="display:none" onclick="downloadUploadedFile('hidden_handlingFee')">
                     <span class="documentName" ><s:property value="task.claim.serviceInformation.serviceDetail.handlingFeeInvoice.fileName" /></span>
                     <img class="dropUpload" src="image/remove.gif" id="deleteImageId_handlingFee" onclick="dropAttachedInvoiceForIncidentals('handlingFee', 'handlingFeeInvoice')"/>
                  </a>
                </s:else>

            </td>
	        </s:if>
	        <s:else>
	        	<s:hidden name="task.claim.handlingFeeConfig" value="false"/>
	        	<s:hidden name="task.claim.serviceInformation.serviceDetail.handlingFee" 
						  value="%{task.claim.forDealer.preferredCurrency.symbol}"/>
				<s:hidden name="task.claim.serviceInformation.serviceDetail.handlingFee" 
						  value="0.00"/>
	        </s:else>
	        </tr>
	</table>

</div>
