<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 26, 2008
  Time: 6:42:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");
</script>

<s:hidden id="forSerialized" name="forSerialized" value="false"/>
<s:hidden name="productModelSelected" id="isProductModel" value="false"/>
<s:hidden name="serialNumberSelected" id="isSerialNumber"/>
<s:hidden id="partInstalled" name="claim.partInstalled" value="true"/>
<s:hidden name="claim.itemReference.serialized" value="false"/>
<s:hidden id="partHostedOnMachine" name="partHostedOnMachine" value="true"/>
<s:hidden name="claim"/>

<table class="form" border="0" cellpadding="0" cellspacing="0" style=" border-top: 1px solid #EFEBF7">
    <tbody>

            <s:hidden id='brandType' name='claim.brand' value="%{claim.brand}" />
			<script type="text/javascript">
                        dojo.addOnLoad(function(){
                          <s:if test= "claim == null || claim.id == null">
                             if(dojo.byId("toggleToSerialNumber")){
                                dojo.byId("toggleToSerialNumber").disabled=true;
                             }
                             if(dojo.byId("toggleToPartNumber")){
                                dojo.byId("toggleToPartNumber").disabled=true;
                             }
                             if(dojo.byId("toggleToSerialNumber")){
                               dojo.byId("toggleToSerialNumber").disabled=false;
                             }
                             if(dojo.byId("toggleToPartNumber")){
                               dojo.byId("toggleToPartNumber").disabled=false;
                             }
                             <s:if test="serialNumberSelected">
					        	enableSerialNumber();
					      	 </s:if>
					     	 <s:else>
					      		enablePartNumber();
					      	</s:else>
					       </s:if>
                        });
                    </script>

		<jsp:include flush="true" page="partAndSerialNumber1.jsp" />
		<tr>


			<td style="padding-left:0; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td><s:if test=" (claim==null || claim.id==null) ">
						<input class="checkbox" type="radio" name="partInstalledOn" id="file_serialized_claim"
							value="PART_INSTALLED_ON_HOST" <s:if test="toBeChecked('PART_INSTALLED_ON_HOST')">checked="checked"</s:if>  />
					</s:if><s:else>
					<input class="checkbox" type="radio" name="partInstalledOn" id="file_serialized_claim"
							value="PART_INSTALLED_ON_HOST" disabled="disabled" />
					</s:else>
					 <script type="text/javascript">
	                        dojo.addOnLoad(function(){
	                            dojo.connect(dojo.byId("file_serialized_claim"), 'onclick', function() {
	                                var form = dojo.byId("form");
	                                dojo.byId("forSerialized").value = true;
	                                dojo.byId("isProductModel").value = false;
	                                form.action = "partsClaim.action";
	                                form.submit();
	                            });
	                        })
	                    </script>
					</td>
					<td class="labelStyle"><s:text
						name="label.newClaim.isInstalledOnHost" /></td>
				</tr>
			</table>
		</tr>

		<tr>
			<td style="padding-left:0; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td><s:if test=" (claim==null || claim.id==null) ">
						<input class="checkbox" type="radio" name="partInstalledOn" id="file_parts_non_serialized_claim"
							value="PART_INSTALLED_ON_NON_SERIALIZED_HOST" checked="checked" />
					</s:if> <s:else>
					<input class="checkbox" type="radio" name="partInstalledOn" id="file_parts_non_serialized_claim"
							value="PART_INSTALLED_ON_NON_SERIALIZED_HOST" disabled="disabled" checked="checked" />
					</s:else></td>
					<td class="labelStyle"><s:text
						name="label.newClaim.isInstalledOnNonSerializedHost" /></td>
				</tr>
			</table>
		</tr>

		<tr>
			<td style="padding-left:0; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td><s:if test=" (claim==null || claim.id==null) ">
						<input class="checkbox" type="radio" name="partInstalledOn" id="file_product_model"
							value="PART_INSTALLED_ON_COMPETITOR_MODEL"/>
					</s:if><s:else>
					<input class="checkbox" type="radio" name="partInstalledOn" id="file_product_model"
							value="PART_INSTALLED_ON_COMPETITOR_MODEL" disabled="disabled" />
					</s:else>
					  <script type="text/javascript">
	                        dojo.addOnLoad(function(){
	                            dojo.connect(dojo.byId("file_product_model"), 'onclick', function() {
	                                var form = dojo.byId("form");
	                                dojo.byId("forSerialized").value = true;
	                                dojo.byId("isProductModel").value = true;
	                                form.action = "partsClaim.action";
	                                form.submit();
	                            });
	                        })
	                    </script>
					</td>
					<td class="labelStyle"><s:text
						name="label.newClaim.isInstalledOnCompetitorModel" /></td>
				</tr>
			</table>
		</tr>

		<tr>
			<td style="padding-left:0; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
			<s:if test="%{isShowPartNotInstalledOnPartsClaim()}">
				<tr>
					<td><s:if
						test="(claim==null || claim.id==null) && partsClaimWithoutHostAllowed">
						<input class="checkbox" type="radio" name="partInstalledOn"
							id="file_partsclm_nothosted" value="PART_NOT_INSTALLED" />
						<s:if test="toBeChecked('PART_NOT_INSTALLED')">
							<s:hidden id="partInstalledHidden" name="claim.partInstalled"
								value="false" />
						</s:if>
						<s:else>
							<s:hidden id="partInstalledHidden" name="claim.partInstalled"
								value="true" />
						</s:else>
						<script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("file_partsclm_nothosted"), 'onclick', function() {
                                var form = dojo.byId("form");
                                dojo.byId("forSerialized").value = "false";
                                dojo.byId("partHostedOnMachine").value = "false";
                                dojo.byId("partInstalled").value = "false";
                                form.action = "partsClaim.action";
                                form.submit();
                            });
                        })
                    </script>
					</s:if> <s:else>
						<input class="checkbox" type="radio" name="partInstalledOn"
							id="file_partsclm_nothosted" value="" disabled="disabled" <s:if test="!partsClaimWithoutHostAllowed"> style="display: none;" </s:if>  />
					</s:else></td>
					<s:if test="partsClaimWithoutHostAllowed"><td class="labelStyle"><s:text
						name="label.newClaim.notInstalled" /></td></s:if>
				</tr>
				</s:if>
			</table>
		</tr>
		<tr><td colspan="4" style="height:10px; padding:0; margin:0;"></td></tr>
        <tr>
            <td width="26%">
                <label for="modelNumber" id="modelNumberLabel" class="labelStyle">
                    <s:text name="label.common.baseModelName"/>:
                </label>
            </td>
			<s:if test="claim.id != null" >
				<td id="modelNameDisplay">
	            	<s:property value="claim.itemReference.model.name"  />
				</td>
            </s:if>
            <s:else>
            	<td width="35%">
	                <s:hidden name="claim.itemReference.model" value="%{claim.itemReference.model.id}" id="selectedModelHidden"/>
            	  	<sd:autocompleter id='modelNumber' href='list_claim_model_names.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='selectedModels' showDownArrow='false' indicator='indicator' key='%{claim.itemReference.model.id}' keyName='claim.itemReference.model' value='%{claim.itemReference.model.name}' notifyTopics='modelChanged/setModelId' loadMinimumCount='2' />

	                <script type="text/javascript">
                        dojo.addOnLoad(function() {
		                        <s:if test="isItemNumberDisplayRequired()">
		                        	dijit.byId("modelNumber").setDisabled(true);
		                        </s:if>
		                       dojo.subscribe("modelChanged/setModelId", null, function(data, type, request) {
                               var selectedModelId = dojo.byId("selectedModelHidden").value;
                               if(selectedModelId != data){
                                   dojo.byId("selectedModelHidden").value = dijit.byId("modelNumber").getValue();
                               }
                            });
                        });
                    </script>

	            </td>
            </s:else>
            <td colspan="2"></td>
        </tr>


         	<tr>
				<td colspan="4" style="height: 2px;"></td>
			</tr>
			<tr>
				<td width="26%"><label for="invoiceNumber" id="invoicNumberLabel"
					class="labelStyle"> <s:text
					name="label.claim.invoiceNumber" />: </label></td>
				<td width="35%"><s:textfield name="claim.invoiceNumber" id="invoiceNumber"
					cssStyle="width:145px;  margin-left: -1px;" theme="simple" /></td>

			</tr>

			<tr>
				<td colspan="4" style="height: 2px;"></td>
			</tr>
       		 <tr>
				 <td width="26%"><label id="serialNumberForNonSerializedClaimLabel" class="labelStyle"><s:text name="label.common.serialNumber"/>:</label></td>
            		<s:if test="claim.id != null" >
            			<td width="35%" style="padding-left:0;"><s:property value="claim.itemReference.unszdSlNo" /></td>
           			</s:if>
            		<s:else>
            			<td width="35%" style="padding-left:0;"><s:textfield id="invalidSerialNumber" name="claim.itemReference.unszdSlNo" cssStyle="width:145px;" /></td>
            		</s:else>
  				<td width="15%"><label for="warrantyStartDate" id="warrantyStartDateLabel"
						class="labelStyle"> <s:text
						name="label.common.installationDate" />: </label></td>
			   <td><sd:datetimepicker name='claim.installationDate' value='%{claim.installationDate}' id='warrantyStartDate' /></td>

			 </tr>
			 <tr>
				<td style="height: 2px;" colspan="4"></td>
			 </tr>
			<tr>
				<td width="26%"><label for="hoursInService" id="hoursInServiceLabel"
				class="labelStyle"> <s:text name="label.common.hoursOnTruck"/>: </label></td>
				<td width="35%" style="padding-left:0;"><s:textfield name="claim.hoursInService" id="hoursInService"
				cssStyle="width:145px;" theme="simple" /></td>
				<td nowrap="nowrap" width="15%">
                <label for="hoursOnTruck" id="hoursOnTruckLabel" class="labelStyle">
                    <s:text name="label.common.hoursOnTruckDuringIstallation"/>:
                </label>
            	</td>
            	<td nowrap="nowrap">
                <s:textfield name="claim.hoursOnTruck" value="%{claim.hoursOnTruck}"
                    id="hoursOnTruck" theme="simple" cssStyle="width:145px;"/>
            	</td>


			</tr>
			<tr>
				<td style="height: 2px;" colspan="4"></td>
			 </tr>
		<s:if test="isItemNumberDisplayRequired()">
			<tr>
				<td style="height:2px;" colspan="4"></td>
			 </tr>
			<tr>
				<td width="26%"><label for="itemNumber" id="itemNumberLabel"
					class="labelStyle" > <s:text name="label.common.itemNumber" />:
				</label></td>
				<s:if test="claim.id != null">
					<td style= "margin-right: -45";colspan="2" id="itemNumberDisplay"><s:property
						value="claim.itemReference.referredItem.alternateNumber" /></td>
				</s:if>
				<s:else>
					<td><sd:autocompleter href='list_claim_item_numbers.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimType=Machine' name='claim.itemReference.referredItem' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' value='%{claim.itemReference.referredItem.alternateNumber}' id='itemNumber' listenTopics='itemNumber/queryAddParams' notifyTopics='itemNumberChanged/description' /> <img
						style="display: none;" id="indicator" class="indicator"
						src="image/indicator.gif" alt="Loading..." /> <script
						type="text/javascript">
                    var isOnLoad = true;
                    dojo.addOnLoad(function() {
                        dojo.subscribe("itemNumberChanged/description", null, fillItemDescription);
                    });
                    function fillItemDescription(data, type, request) {
                    var params = {};
                    params["number"] = data;
                    dojo.byId("itemDescriptionDisplay").innerHTML = '';
                    dijit.byId("modelNumber").setDisplayedValue('');
    	            dijit.byId("modelNumber").setValue('');
    	            dojo.byId("selectedModelHidden").value = '';
                    twms.ajax.fireJsonRequest("findItemDescription.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", params, function(details) {
                        if (details) {
                            dojo.byId("itemDescriptionDisplay").innerHTML = details[0];
                            dijit.byId("modelNumber").setDisplayedValue(details[1]);
    	                    dijit.byId("modelNumber").setValue(details[2]);
    	                    dojo.byId("selectedModelHidden").value = details[2];
                        }
                    });
                }
                </script></td>
				</s:else>
				<td class="labelStyle"><s:text name="label.common.description" />
				</td>
				<s:if test="claim.id != null">
					<td id="itemNumberDescription"><s:property
						value="claim.itemReference.referredItem.description" />  </td>
				</s:if>
				<s:else>
					<td id="itemNumberDescription"><span
						id="itemDescriptionDisplay"></span></td>
				</s:else>
			</tr>
		</s:if>

		<s:if test="isDateCodeEnabled()">
	        <tr>
		        <td>
		            <label for="dateCode" id="dateCodeLabel" class="labelStyle">
		                <s:text name="label.common.dateCode"/>:
		            </label>
		        </td>
		        <td>
		            <s:textfield name="claim.dateCode" id="dateCode" cssStyle="width:145px;  margin-left: -1px;" theme="simple"/>
		        </td>
		        <td colspan="2"></td>
	        </tr>
        </s:if>

    </tbody>
</table>
<script type="text/javascript">
  function enablePartNumber() {
    	 if(dijit.byId("serialNumber")){
          dijit.byId("serialNumber").setDisabled(true);
    	 }
    	 if( dojo.byId("selectedPartInvItemHidden")){
          dojo.byId("selectedPartInvItemHidden").value ='';
    	 }
    	 if( dojo.byId("serialNumber")){
          dojo.byId("serialNumber").value ='';
    	 }
          dojo.byId("isSerialNumber").value = false;


    }
    function enableSerialNumber() {
          dojo.byId("isSerialNumber").value = true;
    }
    </script>
<jsp:include flush="true" page="datesAndSmr.jsp" />

