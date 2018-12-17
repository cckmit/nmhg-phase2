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
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
		<tr>
			<td style="height:10px; padding:0; margin:0;" colspan="4"></td>
		</tr>
		

		<tr>
			<td colspan="4" style="height:2px;"></td>
		</tr>
		<tr>
			
				<td width="26%">
					<label class="labelStyle"><s:text name="label.newClaim.partSerialNo" />: </label>
				</td>					
				<td width="35%">
					<s:textfield name="claim.partSerialNumber" value="%{claim.partSerialNumber}"
                    id="invoiceNumber" theme="simple" cssStyle="width:145px;"></s:textfield>
                 
    					
				</td>
				<s:if test="claim == null || claim.id == null">	
		    <script type="text/javascript">
					   		 dojo.addOnLoad(function() {					    	   
					      		enablePartNumber();						        	       			         
					    });
						</script>
			</s:if>
			<td width="20%" class="labelStyle"><s:text name="label.common.historicalClaimNumber" />:</td>
            <td width="35%">
                <s:textfield name="claim.histClmNo"  cssStyle="width:145px;" id="histClmNo" theme="simple" />
            </td>
        </tr>
        <tr>
        <tr>
			<td colspan="4" style="height:5px;"></td>
		</tr>
        
		<tr>
			<s:if test="claim == null || claim.id == null">
				<td width="26%">
				<label class="labelStyle"><s:text name="label.common.partNumber" />: </label></td>
				<td width="35%">
					<sd:autocompleter id='brandPartNumber' href='list_items_parts_claim.action?selectedBusinessUnit=%{selectedBusinessUnit}&dealerBrandsOnly=true'
					name='claim.brandPartItem' required='false' loadOnTextChange='true'
					keyName='claim.brandPartItem' loadMinimumCount='2' showDownArrow='false'
					indicator='indicator' key='%{claim.claim.brandPartItem.id}' listenTopics="/brand/changed,/dealer/modified" keyValue="%{claim.brandPartItem.id}" value='%{claim.brandPartItem.itemNumber}' />
					<img style="display: none;" id="indicator" class="indicator"
					src="image/indicator.gif" alt="Loading..." />
					<s:hidden id="partNumber" name="claim.partItemReference.referredItem" value="%{claim.partItemReference.referredItem.alternateNumber}" />
					 	<script type="text/javascript">
					        dojo.addOnLoad(function() {						       
                                var partNumberSelect = dijit.byId("brandPartNumber");
                                partNumberSelect.sendDisplayedValueOnChange = false;
                                dojo.connect(partNumberSelect, "onChange", function(value){
					            populateDescriptionForPart();
					            });
							});
    					</script> 
                  </td>
				<td class="labelStyle" nowrap="nowrap" width="15%" style="width: 210px;"><s:text
					name="label.common.description" />:</td>
				<td>
					<div dojoType="dojox.layout.ContentPane" id="desc_for_part" style="width: 100%; height: 100%;"></div>
				</td>
			</s:if>
			<s:else>
				<td width="26%"><label class="labelStyle"><s:text name="label.common.partNumber" />: </label></td>
				<td width="35%"><s:property	value="%{claim.brandPartItem.itemNumber}" /></td>
				<td class="labelStyle" nowrap="nowrap" width="15%" style="width: 210px;"><s:text name="label.common.description" />:</td>
				<td><s:label name="claim.partItemReference.referredItem.description" id="desc_for_part" /></td>
			</s:else>
		</tr>
		<tr>
			<td colspan="4" style="height:5px;"></td>
		</tr>