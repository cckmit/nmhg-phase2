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
<%@page pageEncoding="UTF-8" %>
		<tr>
			<td style="padding-left: 3px; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td><s:if test=" (claim==null || claim.id==null) ">
						<input class="checkbox" type="radio" name="partInstalledOn" id="partInstalled"
							value="PART_INSTALLED_ON_HOST" <s:if test="toBeChecked('PART_INSTALLED_ON_HOST') || partInstalledOn==null">checked="checked"</s:if> />
					</s:if> <s:elseif test="claim.partInstalled && claim.itemReference.referredInventoryItem.serialNumber != null">
						<input class="checkbox" type="radio" name="partInstalledOn" id="partInstalled"
							value="PART_INSTALLED_ON_HOST" checked="checked" disabled="disabled" />
					</s:elseif><s:else>
					<input class="checkbox" type="radio" name="partInstalledOn" id="partInstalled"
							value="true" disabled="disabled" />
					</s:else></td>
					<td class="labelStyle"><s:text
						name="label.newClaim.isInstalledOnHost" /></td>					
				</tr>
			</table>
			</td>
		</tr>
		
		<tr>
			<td style="padding-left: 3px; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td>
					<s:if test="toBeChecked('PART_NOT_INSTALLED')"><s:hidden id="partInstalledHidden" name="claim.partInstalled" value="false" /></s:if>
							<s:else>
							<s:hidden id="partInstalledHidden" name="claim.partInstalled" value="true" />
							</s:else>
					<s:if
						test="(claim==null || claim.id==null) && nonSerializedClaimAllowed">	
						<input class="checkbox" type="radio" name="partInstalledOn" id="file_parts_non_serialized_claim"
							value="PART_INSTALLED_ON_NON_SERIALIZED_HOST" <s:if test="toBeChecked('PART_INSTALLED_ON_NON_SERIALIZED_HOST')">checked="checked"</s:if> />
					</s:if> <s:else>
					<input class="checkbox" type="radio" name="partInstalledOn" id="file_parts_non_serialized_claim"
							value="" disabled="disabled" <s:if test="!nonSerializedClaimAllowed"> style="display: none;" </s:if> />
					</s:else></td>
					 <s:if test="nonSerializedClaimAllowed"><td class="labelStyle"><s:text
						name="label.newClaim.isInstalledOnNonSerializedHost" /></td></s:if>				
				</tr>
			</table>
			</td>
		</tr>
		
		<tr>
			<td style="padding-left: 3px; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td><s:if test=" (claim==null || claim.id==null) ">
						<input class="checkbox" type="radio" name="partInstalledOn" id="toggleToProductModel"
							value="PART_INSTALLED_ON_COMPETITOR_MODEL" <s:if test="toBeChecked('PART_INSTALLED_ON_COMPETITOR_MODEL')">checked="checked"</s:if> />
					</s:if> <s:elseif test="claim.partInstalled && claim.competitorModelBrand!=null">
						<input class="checkbox" type="radio" name="partInstalledOn" id="toggleToProductModel"
							value="PART_INSTALLED_ON_COMPETITOR_MODEL" checked="checked" disabled="disabled" />
					</s:elseif> <s:else>
					<input class="checkbox" type="radio" name="partInstalledOn" id="toggleToProductModel"
							value="" disabled="disabled" />
					</s:else></td>
					<td class="labelStyle"><s:text
						name="label.newClaim.isInstalledOnCompetitorModel" /></td>					
				</tr>
			</table>
			</td>
		</tr>	
		
		<tr>
			<s:if test="!partsClaimWithoutHostAllowed">
				<s:hidden name="claim.partInstalled" value="true" />
			</s:if>
			<td style="padding-left: 3px; padding-top: 5px;" colspan="4">
			<table cellpadding="0" cellspacing="0" border="0">
			<s:if test="%{isShowPartNotInstalledOnPartsClaim()}">
				<tr>
					<td>
					<s:if test="(claim==null || claim.id==null)">
						<s:if test="partsClaimWithoutHostAllowed">					
						<input class="checkbox" type="radio" name="partInstalledOn" id="partNotInstalled" value="PART_NOT_INSTALLED" 
							<s:if test="toBeChecked('PART_NOT_INSTALLED')">checked="checked"</s:if> />
						<td class="labelStyle">
						<s:text name="label.newClaim.notInstalled" />	</td>						
						<script type="text/javascript">
                        <s:if test="claim!=null && claim.partInstalled && !productModelSelected">
                        try
                        {
                            dojo.byId("partInstalled").checked=true;
                        }
                        catch(e)
                        {
                            console.debug(e);
                        }
                        </s:if>    
                        
                    /* dojo.addOnLoad(function() {        
                            var partsWithOutHost = '<s:property value="partHostedOnMachine" />';                        	
                        	if(partsWithOutHost == 'true'){
                        		dojo.byId("partInstalled").checked=true;
                        	} 
                        });	 */
                                  
                    </script>
                    	</s:if>
					</s:if> 
					<s:else>
						<input class="checkbox" type="radio" name="partInstalledOn"
							id="partNotInstalled" value="PART_NOT_INSTALLED"
							disabled="disabled"
							<s:if test="!claim.partInstalled">checked="checked"</s:if> />
						<td class="labelStyle"><s:text
								name="label.newClaim.notInstalled" /></td>
					</s:else>

				</td>
					
				</tr>
				</s:if>
			</table>
			</td>
		</tr>