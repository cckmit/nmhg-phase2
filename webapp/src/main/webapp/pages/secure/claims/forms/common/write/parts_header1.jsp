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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<s:hidden name="claim"/>
<s:hidden name="serialNumberSelected" id="isSerialNumber"/>
<s:hidden name="productModelSelected" id="isProductModel"/>
<s:hidden name="claim.itemReference.serialized" value="true" id="serializedId"/>
<s:hidden name="claim.partItemReference.serialized" value="false" id="partSerializedId"/>

<table class="form" style="border-top:1px solid #EFEBF7; border-bottom:none; border-left:none; border-right:none;" cellpadding="0" cellspacing="0">
    <tbody>


      <s:if test="claim.brand == null && claim != null && claim.itemReference != null && claim.itemReference.referredInventoryItem !=null">
         <s:hidden id='brandType' name='claim.brand' value= "%{claim.itemReference.referredInventoryItem.brandType}" />
      </s:if>
      <s:else>
        <s:hidden id='brandType' name='claim.brand' value= "%{claim.brand}" />
      </s:else>

    	<jsp:include flush="true" page="partAndSerialNumber1.jsp" />
    	<jsp:include page="part_installation_selection.jsp" flush="true"/>
			<tr>
				<td colspan="4" style="height:10px; padding:0; margin:0;"></td>
			</tr>
		<s:if test="(claim==null || claim.id==null) && nonSerializedClaimAllowed">
	        <tr>
	            <td colspan="4" id="partsNonserializedLink" style="display:none;" nowrap="nowrap">
                   <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("file_parts_non_serialized_claim"), 'onclick', function() {
                                var form = dojo.byId("form");
                                var selectedBu = dojo.byId("selectedBusinessUnit");
                                form.action = "partsClaimNonSerialized.action?forSerialized=false";
                                form.submit();
                            });
                        })
                    </script>
	             </td>
			</tr>
        </s:if>

         <s:if test="claim == null || claim.id == null">
			<tr>
				<td nowrap="nowrap" width="26%">
					<label for="purchaseDate" id="purchaseDateLabel" class="labelStyle"><s:text name="label.common.dateOfPurchase" />: </label>
				</td>
				<td nowrap="nowrap" width="35%">
					<sd:datetimepicker name='claim.purchaseDate' value='%{claim.purchaseDate}' id='purchaseDate' />
				</td>
				<td colspan="2"></td>
			</tr>
		</s:if>
		<s:else>
			<s:if test="(!claim.partItemReference.serialized && !claim.partInstalled) || partInstalledOn.equals('PART_NOT_INSTALLED')">
				<tr>
					<td nowrap="nowrap" width="15%"><label for="purchaseDate"
						id="purchaseDateLabel" class="labelStyle"> <s:text
						name="label.common.dateOfPurchase" />: </label></td>
					<td nowrap="nowrap"><sd:datetimepicker name='claim.purchaseDate' value='%{claim.purchaseDate}' id='purchaseDate' />aaa</td>
				</tr>
			</s:if>

		</s:else>

        <tr>
			<s:if test="claim == null || claim.id == null">
				<td width="26%" id="eqpSerialNumberLabel"><label class="labelStyle">
				<s:text name="label.newClaim.equipmentSerialNo" />: </label></td>
				<td width="26%" id="productModelLabel"><label class="labelStyle">
				<s:text name="label.common.competitorModel" />: </label></td>
			</s:if>
			<s:else>
			<s:if test="claim.itemReference.referredInventoryItem.serialNumber != null">
			<td width="26%" id="eqpSerialNumberLab"><label class="labelStyle">
				<s:text name="label.newClaim.equipmentSerialNo" />: </label></td>
				</s:if>
				<s:elseif test="!claim.getCompetitorModelDescription().isEmpty()">
			<td width="26%" id="productModelLab"><label class="labelStyle">
				<s:text name="Specify Product Model" />: </label></td>

				</s:elseif>
			</s:else>

			<s:if test="claim.id != null">
				<td><s:if
					test="claim.itemReference.referredInventoryItem.serialNumber != null">
					<s:property
						value="claim.itemReference.referredInventoryItem.serialNumber" />
				</s:if> <s:else>

 				    <s:textfield name="claim.competitorModelDescription" value="%{claim.competitorModelDescription}"/>

				</s:else></td>
	      </s:if>
            <s:else>
      	         <td>
	            	  <s:hidden name="claim.itemReference.referredInventoryItem" value="%{claim.itemReference.referredInventoryItem.id}" id="selectedInvItemHidden"/>
                    <sd:autocompleter id='eqpSerialNumber' href='list_claim_sl_nos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimType=%{claimType}&dealerBrandsOnly=true' loadOnTextChange='true' loadMinimumCount='2' showDownArrow='false' name='selectedItems' keyValue='%{claim.itemReference.referredInventoryItem.id}' value='%{claim.itemReference.referredInventoryItem.serialNumber}' notifyTopics='invItemChanged/setSerialNumber' listenTopics="/dealer/modified"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.subscribe("invItemChanged/setSerialNumber", null, function(data, type, request) {
                               var selectedInvId = dojo.byId("selectedInvItemHidden").value;
                               var eqpSerialNumber = dojo.byId("eqpSerialNumber").value;
                               if(eqpSerialNumber != data){
                                   dojo.byId("selectedInvItemHidden").value = dijit.byId("eqpSerialNumber").getValue();
                               }
                               twms.ajax.fireJsonRequest("get_inventory_brand.action",{
                                           inventoryId : data
                                       },function(brand) {
                                    	   if(inventoryId != '')
                                           dojo.byId("brandType").value=eval(brand)[0];
                               			}
                                     );
                            });
                        });
                    </script>

                    <script type="text/javascript">
					        dojo.addOnLoad(function() {
					        	  var eqpSerialNumberSelect = dijit.byId("eqpSerialNumber");
		                            eqpSerialNumberSelect.sendDisplayedValueOnChange = false;
							});
    				</script>

                  <%--  <s:select id="productModel" name="claim.claimCompetitorModel" cssStyle="width:145px;" cssClass="processor_decesion"
                						list="competitorModels" listKey="code" listValue="description"
                						headerKey="" headerValue="%{getText('label.common.selectHeader')}" value="%{claim.claimCompetitorModel.code}"/> --%>

                						<s:textfield name="claim.competitorModelDescription" value="%{claim.competitorModelDescription}"
                    id="productModel" theme="simple" cssStyle="width:145px;"/>
						 <%-- <script type="text/javascript">
					        dojo.addOnLoad(function() {
                                var productModelSelect = dijit.byId("productModel");
                                 productModelSelect.sendDisplayedValueOnChange = false;
                                dojo.connect(productModelSelect, "onChange", function(value){

					            	dojo.byId("isProductModel").value = "true";

					            });
							});
    					</script>   --%><script type="text/javascript">
					    dojo.addOnLoad(function() {

					        <s:if test="productModelSelected">

					        showProductModel();
					       </s:if>
					      <s:else>

					      showEqSerialNumber();
					      dojo.html.hide(dojo.byId("productModel"));

					      dojo.html.hide(dojo.byId("competitormodelbrand"));
			                 dojo.html.hide(dojo.byId("competitormodelbrandlable"));
			                 dojo.html.hide(dojo.byId("competitormodetruckserialnumber"));
			                 dojo.html.hide(dojo.byId("competitormodetruckserialnumberlable"));



					      </s:else>
							dojo.connect(dojo.byId("toggleToProductModel"), "onclick", function() {

								 dojo.html.show(dojo.byId("competitormodelbrand"));
				                    dojo.html.show(dojo.byId("competitormodelbrandlable"));
				                    dojo.html.show(dojo.byId("competitormodetruckserialnumber"));
				                    dojo.html.show(dojo.byId("competitormodetruckserialnumberlable"));
								dojo.byId("partInstalledHidden").value="true";
					            dojo.byId("serializedId").value=false;
			                    dojo.html.show(dojo.byId("hoursInServiceLabel"));
			                    dojo.byId("hoursInService").disable=false;
			                    dojo.html.show(dojo.byId("hoursInService"));
			                    dojo.html.show(dojo.byId("hoursOnTruck"));
			                    dojo.html.show(dojo.byId("hoursOnTruckLabel"));
			                    dojo.byId("hoursOnTruck").disable=false;
			                    dojo.html.show(dojo.byId("hoursOnPartLabel"));
			        			dojo.byId("hoursOnPart").disable = false;
						        dojo.html.show(dojo.byId("hoursOnPart"));
						    	dojo.byId("partInstalledHidden").value="true";
						    	dojo.html.show(dojo.byId("warrantyLabelId"));
						 		dojo.html.show(dojo.byId("installationLabelId"));
						 		dojo.html.hide(dojo.byId("purchaseDateLabel"));
						        dojo.html.hide(dijit.byId("purchaseDate").domNode);
						        dijit.byId("purchaseDate").setDisabled(true);
						    	showProductModel();

						    	 dojo.html.show(dojo.byId("productModelLabel"));
							        dojo.html.show(dojo.byId("productModel"));
					        });
					    });
					</script></td>
			</s:else>


		</tr>
		<tr >
		<td nowrap="nowrap" width="15%">
                <label for="invoiceNumber" id="invoiceNumberLabel" class="labelStyle">
                    <s:text name="label.claim.invoiceNumber"/>:
                </label>
            </td>
             <td nowrap="nowrap">
                <s:textfield name="claim.invoiceNumber" value="%{claim.invoiceNumber}"
                    id="invoiceNumber" theme="simple" cssStyle="width:145px;"/>
            </td>
            </tr>
             <tr >

            <td nowrap="nowrap" width="15%">
                <label for="competitormodelbrand" id="competitormodelbrandlable" class="labelStyle">
                    <s:text name="label.newClaim.competitorModelBrand"/>:
                </label>
            </td>
             <td nowrap="nowrap">
                <s:textfield name="claim.competitorModelBrand" value="%{claim.competitorModelBrand}"
                    id="competitormodelbrand" theme="simple" cssStyle="width:145px;"/>
              </td>
            <td nowrap="nowrap" width="15%">
                <label for="competitormodetruckserialnumber" id="competitormodetruckserialnumberlable" class="labelStyle">
                    <s:text name="label.newClaim.competitorModelTruckSerialNumber"/>:
                </label>
            </td>
             <td nowrap="nowrap">
                <s:textfield name="claim.competitorModelTruckSerialnumber" value="%{claim.competitorModelTruckSerialnumber}"
                    id="competitormodetruckserialnumber" theme="simple" cssStyle="width:145px;"/>
            </td>
            </tr>
            <tr>
             <td nowrap="nowrap" width="15%" id="warrantyLabelId" style="width: 230px;">
                <label  for="warrantyStartDate" id="warrantyStartDateLabel"
						class="labelStyle"> <s:text name="label.common.installationDate" />: </label></td>
			<td id="installationLabelId"><sd:datetimepicker name='claim.installationDate' value='%{claim.installationDate}' id='warrantyStartDate' /></td>
		</tr>
		<tr>
		<td width="26%"><label
				for="hoursInService" id="hoursInServiceLabel" class="labelStyle">
			      <s:text name="label.common.hoursOnTruck" />: </label>
			</td>
			<td width="35%" style="padding-left:3px;">
			    <s:textfield name="claim.hoursInService" id="hoursInService" cssStyle="width:145px;" theme="simple" />
			</td>



		</tr>
		<tr>
		    <td nowrap="nowrap" width="15%">
                <label for="hoursOnTruck" id="hoursOnTruckLabel" class="labelStyle">
                    <s:text name="label.common.hoursOnTruckDuringIstallation"/>:
                </label>
            </td>
            <td nowrap="nowrap">
                <s:textfield name="claim.hoursOnTruck" value="%{claim.hoursOnTruck}"
                    id="hoursOnTruck" theme="simple" cssStyle="width:145px;" />
            </td>
		</tr>
		<tr>
			 <td nowrap="nowrap" width="15%">
                <label for="hoursOnPart" id="hoursOnPartLabel" class="labelStyle">
                    <s:text name="label.common.hoursOnPart"/>:
                </label>
            </td>
            <td nowrap="nowrap">
                <s:textfield name="claim.hoursOnPart" value="%{claim.hoursOnPart}" readonly="true"
                    id="hoursOnPart" theme="simple" cssStyle="width:145px;background-color:#F2F2F2;"/>
            </td>

		</tr>
		<tr>
			<td colspan="4" style="height:2px;"></td>
		</tr>
    </tbody>
</table>
<script src="scripts/vendor/jqGrid/js/jquery-1.5.2.min.js" type="text/javascript"></script>

<script type="text/javascript" language="javascript">
jQuery(document).ready(function(){
	jQuery("#hoursOnTruck,#hoursInService").keyup(function(){
		        var intRegex = /^\d+$/;
		        var floatRegex = /^((\d+(\.\d *)?)|((\d*\.)?\d+))$/;
                var hoursOnTruckVal=parseFloat(jQuery('#hoursInService').val());
                var hoursOnTruckWhenVal=parseFloat(jQuery('#hoursOnTruck').val());
                if((intRegex.test(hoursOnTruckVal) || floatRegex.test(hoursOnTruckVal)) && (intRegex.test(hoursOnTruckWhenVal) || floatRegex.test(hoursOnTruckWhenVal))) {
                if (jQuery("#hoursOnTruck").val().length > 0 && jQuery("#hoursInService").val().length > 0 ){
                jQuery('#hoursOnPart').val(hoursOnTruckVal - hoursOnTruckWhenVal);
                }
                }
                else if(!(jQuery("#hoursInService").val().length == 0 || jQuery("#hoursInService").val()=="")){
		        	if(intRegex.test(hoursOnTruckVal) || floatRegex.test(hoursOnTruckVal)){
		        		hoursOnTruckVal=jQuery("#hoursInService").val();
		        	 jQuery('#hoursOnPart').val(hoursOnTruckVal-0);
		        }
		        }
		        else if(!(jQuery("#hoursOnTruck").val().length == 0 || jQuery("#hoursOnTruck").val()=="")){
		        	if(intRegex.test(hoursOnTruckWhenVal) || floatRegex.test(hoursOnTruckWhenVal)){
		        	 jQuery('#hoursOnPart').val(hoursOnTruckWhenVal);
		        	}
		        }
		        else if ((jQuery("#hoursInService").val().length == 0 || jQuery("#hoursInService").val()=="") || (jQuery("#hoursOnTruck").val().length == 0 || jQuery("#hoursOnTruck").val()=="") ){
                jQuery('#hoursOnPart').val("");
                }

            });
        });
    </script>
    <script type="text/javascript">
    dojo.addOnLoad(function() {

         <s:if test="(claim==null || claim.id==null) ">

                 dojo.html.hide(dojo.byId("purchaseDateLabel"));
                 dojo.html.hide(dijit.byId("purchaseDate").domNode);
                 dijit.byId("purchaseDate").setDisabled(true);

         </s:if>
    	 if (dojo.byId("partNotInstalled") && dojo.byId("partNotInstalled").checked)
    	 {
    		 onPartNotInstalled();
    	 }

        <s:if test="claim !=null && (claim.partItemReference.referredInventoryItem!=null || serialNumberSelected)">
        dojo.byId("partSerializedId").value = true;
        </s:if>
        <s:else>
        dojo.byId("partSerializedId").value = false;
        </s:else>
    	<s:if test="partHostedOnMachine == 'true' ||  ( claim !=null && claim.partInstalled )">
    	showSerialNumberSection();
    	if(dojo.byId("partInstalled").checked)
    	{
          dojo.byId("serializedId").value = true;
         /*  dojo.html.hide(dojo.byId("purchaseDateLabel")); */
        /* dojo.html.hide(dijit.byId("purchaseDate").domNode);
        dijit.byId("purchaseDate").setDisabled(true);    */
    	}
    	else
    	{
        dojo.byId("serializedId").value = false;
    	}
        if (dojo.byId("toggleToProductModel") && dojo.byId("toggleToProductModel").checked)
        {
        dojo.byId("isProductModel").value = true;
        }
        else{
        dojo.byId("isProductModel").value = false;
        }
        dojo.html.show(dojo.byId("hoursOnTruckLabel"));
        dojo.byId("hoursOnTruck").disable = false;
        dojo.html.show(dojo.byId("hoursOnTruck"));
        dojo.html.show(dojo.byId("hoursInServiceLabel"));
        dojo.byId("hoursInService").disable = false;
        dojo.html.show(dojo.byId("hoursInService"));
        dojo.html.show(dojo.byId("hoursOnPartLabel"));
        dojo.byId("hoursOnPart").disable = false;
        dojo.html.show(dojo.byId("hoursOnPart"));

    </s:if>
        if (dojo.byId("partInstalled")) {

            dojo.connect(dojo.byId("partInstalled"), "onclick", function() {
                if (dojo.byId("partInstalled").checked) {
                	onPartInstalled();
                }
            });
        }
        if (dojo.byId("partNotInstalled")) {

            dojo.connect(dojo.byId("partNotInstalled"), "onclick", function() {
                if (dojo.byId("partNotInstalled").checked) {
                	onPartNotInstalled();
                }
            });
        }
    });
    function hideSerialNumberSection(){
    	 if(dijit.byId("eqpSerialNumber")){
    	        dojo.html.hide(dijit.byId("eqpSerialNumber").domNode);

    	        }
    	        if(dojo.byId("eqpSerialNumberLabel")){
    	        dojo.html.hide(dojo.byId("eqpSerialNumberLabel"));
    	        }
    	        if(dijit.byId("productModel")){
    	            dojo.html.hide(dijit.byId("productModel").domNode);

    	        }
    	        if(dojo.byId("productModelLabel")){
    	            dojo.html.hide(dojo.byId("productModelLabel"));
    	        }



    }
    function showSerialNumberSection(){
        if (dojo.byId("toggleToProductModel").checked)
        {
         	 showProductModel();
        }
        else if(dojo.byId("partInstalled").checked)
        {
       		showEqSerialNumber();
        }
    }


    function enablePartNumber() {

        dojo.byId("partSerializedId").value=false;

        <s:if test=" (claim==null || claim.id==null) ">


        if( dojo.byId("partNotInstalled") && dojo.byId("partNotInstalled").checked)
        {
        dojo.html.show(dojo.byId("purchaseDateLabel"));
        dijit.byId("purchaseDate").setDisabled(false);
        dojo.html.show(dijit.byId("purchaseDate").domNode);
        }
        </s:if>

  }

  function showEqSerialNumber() {
	  dojo.byId("isProductModel").value = false;
	  if(dijit.byId("eqpSerialNumber")){
	    dojo.html.show(dijit.byId("eqpSerialNumber").domNode);
	  }

	  if(dijit.byId("productModel")){
	  dojo.html.hide(dijit.byId("productModel").domNode);
	  } <s:if test=" (claim==null || claim.id==null) ">
      dojo.html.show(dojo.byId("eqpSerialNumberLabel"));

      dojo.html.hide(dojo.byId("productModelLabel"));
      dojo.byId("productModel").value="";
	  </s:if>
		dojo.html.hide(dojo.byId("competitormodelbrand"));
        dojo.html.hide(dojo.byId("competitormodelbrandlable"));
        dojo.html.hide(dojo.byId("competitormodetruckserialnumber"));
        dojo.html.hide(dojo.byId("competitormodetruckserialnumberlable"));
     }
	function showProductModel() {
	  dojo.byId("isProductModel").value = true;
	  if(dijit.byId("eqpSerialNumber")){
	  dijit.byId("eqpSerialNumber").setDisabled(false);
      dijit.byId("eqpSerialNumber").setValue("");
      dojo.html.hide(dijit.byId("eqpSerialNumber").domNode);
	  }
	  if(dijit.byId("productModel")){
	  dojo.html.show(dijit.byId("productModel").domNode);
	  }<s:if test=" (claim==null || claim.id==null) ">
      dijit.byId("eqpSerialNumber").setValue("");
      dojo.byId("eqpSerialNumber").value="";
      dojo.byId("selectedInvItemHidden").value="";
      dojo.html.show(dojo.byId("productModelLabel"));
      dojo.html.hide(dojo.byId("eqpSerialNumberLabel"));
      </s:if>
	}

	function onPartInstalled()
	{
		 dojo.html.hide(dojo.byId("productModel"));
		showSerialNumberSection();
        dojo.byId("serializedId").value=true;
        dojo.html.show(dojo.byId("hoursInServiceLabel"));
        dojo.byId("hoursInService").disable=false;
        dojo.html.show(dojo.byId("hoursInService"));
        dojo.html.show(dojo.byId("hoursOnTruckLabel"));



        dojo.byId("hoursOnTruck").disable=false;
        dojo.html.show(dojo.byId("hoursOnTruck"));
        dojo.html.show(dojo.byId("hoursOnPartLabel"));
		dojo.byId("hoursOnPart").disable = false;
        dojo.html.show(dojo.byId("hoursOnPart"));
    	dojo.byId("partInstalledHidden").value="true";
        dojo.html.hide(dojo.byId("purchaseDateLabel"));
        dijit.byId("purchaseDate").setDisabled(true);
        dijit.byId("purchaseDate").setValue("");
        dojo.html.hide(dijit.byId("purchaseDate").domNode);
        dojo.html.show(dojo.byId("warrantyLabelId"));
		dojo.html.show(dojo.byId("installationLabelId"));

		dojo.html.hide(dojo.byId("competitormodelbrand"));
        dojo.html.hide(dojo.byId("competitormodelbrandlable"));
        dojo.html.hide(dojo.byId("competitormodetruckserialnumber"));
        dojo.html.hide(dojo.byId("competitormodetruckserialnumberlable"));
	}

	function onPartNotInstalled()
	{
		try
		{


			dojo.html.hide(dojo.byId("eqpSerialNumberLabel"));
			  dojo.html.hide(dojo.byId("competitormodelbrand"));
		        dojo.html.hide(dojo.byId("competitormodelbrandlable"));
		        dojo.html.hide(dojo.byId("competitormodetruckserialnumber"));
		        dojo.html.hide(dojo.byId("competitormodetruckserialnumberlable"));
		dijit.byId("eqpSerialNumber").setDisabled(true);
		dojo.html.hide(dijit.byId("eqpSerialNumber").domNode);
		dijit.byId("eqpSerialNumber").setValue("");
		dojo.html.hide(dojo.byId("eqpSerialNumberLabel"));
		dojo.html.hide(dojo.byId("warrantyLabelId"));
		dojo.html.hide(dojo.byId("installationLabelId"));

		}
		catch(e)
		{
			console.debug("Equipment Serial Number not applicable");
		}
		dojo.html.hide(dojo.byId("warrantyLabelId"));
		dojo.html.hide(dojo.byId("installationLabelId"));
		if(dojo.byId("partInstalledHidden")){
    	dojo.byId("partInstalledHidden").value="false";
		}
        if(dojo.byId("selectedInvItemHidden")){
        	dojo.byId("selectedInvItemHidden").value="";
        }
        dojo.byId("serializedId").value=false;
        dojo.html.hide(dojo.byId("hoursInServiceLabel"));
        dojo.byId("hoursInService").disable=true;
        dojo.byId("hoursInService").value="";
        dojo.html.hide(dojo.byId("hoursInService"));
        dojo.html.hide(dojo.byId("hoursOnTruckLabel"));



        dojo.byId("hoursOnTruck").disable=true;
        dojo.byId("hoursOnTruck").value="";
        dojo.html.hide(dojo.byId("hoursOnTruck"));
        dojo.html.hide(dojo.byId("hoursOnPartLabel"));
        dojo.html.hide(dojo.byId("hoursOnPart"));
		dojo.byId("hoursOnPart").disable = true;
		dojo.byId("hoursOnPart").value="";
		try
		{
		dojo.html.hide(dijit.byId("productModel").domNode);
		dojo.html.hide(dojo.byId("productModelLabel"));
		}
		catch(e)
		{
		console.debug("Product Model Not Captured");
		}
        dojo.html.hide(dojo.byId("hoursOnPart"));
       	try
		{
        if(dojo.byId("isSerialNumber").value != 'true' && dojo.byId("partNotInstalled") &&  dojo.byId("partNotInstalled").checked)
        {
       dojo.html.show(dojo.byId("purchaseDateLabel"));
       dijit.byId("purchaseDate").setDisabled(false);
        dojo.html.show(dijit.byId("purchaseDate").domNode);
        }
		}
		catch(e)
		{
			console.debug("Serial number is null");
		}
	}

	 function populateDescriptionForPart() {
		 // Inside parts_header1.jsp
   	   	 var currentBrandPartNo = dijit.byId("brandPartNumber").getValue();
		 console.debug("Brand Part Number read is " + "[" + currentBrandPartNo + "]");
		 
		 if (currentBrandPartNo) {
		   	  twms.ajax.fireHtmlRequest("list_description_for_part.action", {"number": currentBrandPartNo}, function(data) {
		       	  dijit.byId("desc_for_part").setContent(eval(data)[0]);
		       	  dojo.byId("partNumber").value = eval(data)[2];
		   	  });
		 }
		 else { // Clear existing description from previous value
			 dijit.byId("desc_for_part").setContent("");
	      	 dojo.byId("partNumber").value = "";
		 }
     }

</script>

<jsp:include flush="true" page="datesAndSmr.jsp" />