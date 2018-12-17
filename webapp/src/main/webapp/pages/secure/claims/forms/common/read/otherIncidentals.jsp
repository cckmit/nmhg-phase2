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
<s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id ">
<script type="text/javascript">
   dojo.require("dijit.Tooltip");
</script>
  <script type="text/javascript">
			
		dojo.addOnLoad(function() {
			
			/* Freight Duty Invoice */
          <s:if test="claim.serviceInformation.serviceDetail.freightDutyInvoice.fileName != null">
				var downloadLinkAttachFreightDuty = dojo.byId("downloadFreightDutyInvoice");
				dojo.connect(downloadLinkAttachFreightDuty, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.freightDutyInvoice.id'/>" );
						});	
		  </s:if>	
		  
			/* Handling Fee Invoice */
          <s:if test="claim.serviceInformation.serviceDetail.handlingFeeInvoice.fileName != null">
				var downloadLinkAttachHandlingFee = dojo.byId("downloadHandlingFeeInvoice");
				dojo.connect(downloadLinkAttachHandlingFee, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.handlingFeeInvoice.id'/>" );
						});	
		  </s:if>
		  
			/* Meals Invoice */
		<s:if test="claim.serviceInformation.serviceDetail.mealsInvoice.fileName != null">
				var downloadLinkAttachMeals = dojo.byId("downloadMealsInvoice");
				dojo.connect(downloadLinkAttachMeals, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.mealsInvoice.id'/>" );
						});	
		  </s:if>
	         
			/* Per Diem Invoice  */
	      <s:if test="claim.serviceInformation.serviceDetail.perDiemInvoice.fileName != null">
			var downloadLinkAttachPerDiem = dojo.byId("downloadPerDiemInvoice");
			dojo.connect(downloadLinkAttachPerDiem, "onclick",
					function() {
				 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.perDiemInvoice.id'/>" );
					});	
	      </s:if>
				
			/* Parking And TollInvoice  */
		 <s:if test="claim.serviceInformation.serviceDetail.parkingAndTollInvoice.fileName != null">
				var downloadLinkAttachParkingAndToll = dojo.byId("downloadParkingAndTollInvoice");
				dojo.connect(downloadLinkAttachParkingAndToll, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.parkingAndTollInvoice.id'/>" );
						});	
		  </s:if>	
		  
		/*  Rental Charges Invoice */
		<s:if test="claim.serviceInformation.serviceDetail.rentalChargesInvoice.fileName != null">
				var downloadLinkAttachRentalCharges = dojo.byId("downloadRentalChargesInvoice");
				dojo.connect(downloadLinkAttachRentalCharges, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.rentalChargesInvoice.id'/>" );
						});	
		  </s:if>
				
		/* local Purchase Invoice */
	   <s:if test="claim.serviceInformation.serviceDetail.localPurchaseInvoice.fileName != null">
				var downloadLinkAttachLocalPurchase = dojo.byId("downloadLocalPurchase");
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.localPurchaseInvoice.id'/>" );
						});	
		  </s:if>
	   
		/* Tolls Invoice */
	  <s:if test="claim.serviceInformation.serviceDetail.tollsInvoice.fileName != null">
				var downloadLinkAttachTolls = dojo.byId("downloadTollsInvoice");
				dojo.connect(downloadLinkAttachTolls, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.tollsInvoice.id'/>" );
						});	
	 </s:if>
				
    	/* Other FreightDuty Invoice */
	 <s:if test="claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName != null">
				var downloadLinkAttachOtherFreightDuty = dojo.byId("downloadOtherFreightDutyInvoice");
				dojo.connect(downloadLinkAttachOtherFreightDuty, "onclick",
						function() {
					 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.id'/>" );
						});	
		  </s:if>
			/* Others Invoice */
		  <s:if test="claim.serviceInformation.serviceDetail.othersInvoice.fileName != null">
			var downloadLinkAttachOthers = dojo.byId("downloadOthersInvoice");
			dojo.connect(downloadLinkAttachOthers, "onclick",
					function() {
				//below line commented to avoid document download for page onclick
				 //getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.othersInvoice.id'/>" );
					});	
	  </s:if>
	  
	  /* Transportation */
	  <s:if test="claim.serviceInformation.serviceDetail.transportationInvoice.fileName != null">
		var downloadLinkTransportation = dojo.byId("downloadTransportationInvoice");
		dojo.connect(downloadLinkTransportation, "onclick",
				function() {
			 getFileDownloader().download("downloadDocument.action?docId=<s:property value='claim.serviceInformation.serviceDetail.transportationInvoice.id'/>" );
				});	
</s:if>
	  
			});
		</script>
		</s:if>
<div style="100%">
	<s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id ">
       <div class="mainTitle" style="margin-top:5px;"> 
	    <s:text name="label.newClaim.incidentals"/>:</div>
	      <div class="borderTable" >&nbsp;</div>
            <table class="grid" cellsapcing="0" cellpadding="0" style="margin-top:-10px;">
              <tr> 
               <s:if test="claim.itemDutyConfig">
                <td width="22%" class="labelStyle" nowrap="nowrap"> <s:text name="label.newClaim.itemFreightDuty"/>:</td>
                <td width="35%"><s:property value="claim.serviceInformation.serviceDetail.itemFreightAndDuty"/></td>
                <td>
                 <s:if test="claim.serviceInformation.serviceDetail.freightDutyInvoice.fileName != null">
                     <a id="downloadFreightDutyInvoice" href="#"><s:property value="claim.serviceInformation.serviceDetail.freightDutyInvoice.fileName"/></a>
	             </s:if>
                 
                </td>
               </s:if>
            </tr>
            
            <tr>
            <s:if test="claim.mealsConfig">
                <td class="labelStyle" nowrap="nowrap"><s:text name="label.newClaim.meals"/>:</td>
                <td><s:property value="claim.serviceInformation.serviceDetail.mealsExpense"/> </td>
                <td>
                
                 <s:if test="claim.serviceInformation.serviceDetail.mealsInvoice.fileName != null">
                <a id="downloadMealsInvoice" href="#"><s:property value="claim.serviceInformation.serviceDetail.mealsInvoice.fileName"/></a>
                </s:if>
                
               </td>
            </s:if>
            </tr>
            <tr>
				<s:if test="claim.perDiemConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.perDiem" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.perDiem" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.perDiemInvoice.fileName != null">
							<a id="downloadPerDiemInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.perDiemInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>
            <tr>
				<s:if test="claim.parkingConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.parkingToll" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.parkingAndTollExpense" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.parkingAndTollInvoice.fileName != null">
							<a id="downloadParkingAndTollInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.parkingAndTollInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>
	       <tr>
				<s:if test="claim.rentalChargesConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.rentalCharges" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.rentalCharges" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.rentalChargesInvoice.fileName != null">
							<a id="downloadRentalChargesInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.rentalChargesInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>   
           <tr>
				<s:if test="claim.localPurchaseConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.localPurchase" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.localPurchaseExpense" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.localPurchaseInvoice.fileName != null">
							<a id="downloadLocalPurchase" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.localPurchaseInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>
	            <tr>
				<s:if test="claim.tollsConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.tolls" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.tollsExpense" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.tollsInvoice.fileName != null">
							<a id="downloadTollsInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.tollsInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>
	        <tr>
				<s:if test="claim.otherFreightDutyConfig">
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
							name="label.newClaim.otherFreightDuty" />:</td>
					<td  width="27%"><s:property
							value="claim.serviceInformation.serviceDetail.otherFreightDutyExpense" /></td>
					<td  width="10%"><s:if
							test="claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName != null">
							<a id="downloadOtherFreightDutyInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>
			
			   <tr>
				<s:if test="claim.otherFreightDutyConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.otherFreightDuty" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.otherFreightDutyExpense" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName != null">
							<a id="downloadOtherFreightDutyInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.otherFreightDutyInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>
	            <tr>
	            <s:if test="true">
				<%-- <s:if test="claim.transportation"> --%>
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.newClaim.transportation" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.transportationAmt" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.transportationInvoice.fileName != null">
							<a id="downloadTransportationInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.transportationInvoice.fileName" /></a>
						</s:if></td>
						<td width="20%"><label for="travel_duty" class="labelStyle"><s:text name="label.newClaim.noInvoiceAvailable"/></label></td>
						<td>
						<s:checkbox name="task.claim.serviceInformation.serviceDetail.invoiceAvailable" value="%{task.claim.serviceInformation.serviceDetail.invoiceAvailable}" disabled="true"/>
						</td>
				</s:if>
			</tr>   
			
			 <tr>
				<s:if test="claim.handlingFeeConfig">
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.section.handlingFee" />:</td>
					<td><s:property
							value="claim.serviceInformation.serviceDetail.handlingFee" /></td>
					<td><s:if
							test="claim.serviceInformation.serviceDetail.handlingFeeInvoice.fileName != null">
							<a id="downloadHandlingFeeInvoice" href="#"><s:property
									value="claim.serviceInformation.serviceDetail.handlingFeeInvoice.fileName" /></a>
						</s:if></td>
				</s:if>
			</tr>                    
                        
         </s:if>
    </table>
	</div>
