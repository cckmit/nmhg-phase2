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
<%--
  @author mritunjay.kumar
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <s:head theme="twms"/>
     <script type="text/javascript">
        dojo.require("dijit.layout.BorderContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("twms.widget.TitlePane");
    </script>
    <style type="text/css">
pre {
	border-width: 0px 0;
	padding: 0em;
}
</style>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
	<u:stylePicker fileName="common.css"/>
 
</head>

<u:body>
 <div dojoType="dijit.layout.BorderContainer"
     style="width: 100%; height:100%;margin: 0; padding: 0; overflow-X: none;">
     
     <div dojoType="dijit.layout.ContentPane" region="center">
  <div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.itemDetail" />"
			labelNodeClass="section_header" >
  
  <div id="item_details" class="policy_section_div" 
  	style="width:100%;overflow-x: auto;">
  	<div style="margin-bottom:30px;">
	   <table width="96%" cellpadding="0" cellspacing="0" class="grid">
			<tr>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.itemNumber" /></td>
					<s:if test="isLoggedInUserADealer()">
					<td ><s:property value="brandItem.itemNumber" /></td>
					</s:if>
					<s:else><td ><s:property value="item.number" /></td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.supersededPartNumber" /></td>
					<td ><s:property value="getSupersededPartNumber(item)" /></td>
					</s:else>
					
			</tr>
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.itemType" /></td>
					<s:if test="isLoggedInUserADealer()"><td><s:property value="brandItem.item.itemType"/></td></s:if>
					<s:else><td><s:property value="item.itemType"/></td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.hysterPartNumber" /></td>
					<td ><s:iterator value="item.brandItems">
					<s:if test="brand.equals('HYSTER') ">
					<s:property value="itemNumber" /> </s:if></s:iterator></td>
					</s:else>
					
					
			</tr>
			<tr>
					<%-- <td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.common.itemModel" /></td>
					<td><pre style="background-color: #F3FBFE"><s:property value="item.model.name"/></pre></td> --%>
					<td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.itemSearch.status" /></td>
					<s:if test="isLoggedInUserADealer()"><td><s:property value="brandItem.item.status" /></td></s:if>
					<s:else><td><s:property value="item.status" /></td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.yalePartNumber" /></td>
					<td ><s:iterator value="item.brandItems">
					<s:if test="brand.equals('YALE') ">
					<s:property value="itemNumber" /> </s:if></s:iterator></td>
					</s:else>
			</tr>
			
			
			
			
			<s:if test="isLoggedInUserADealer()">
			<s:if test="brandItem.item.itemType !=null && brandItem.item.itemType != 'PART'">		
				<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.modelCode" /></td>
						<td><pre style="background-color: #F3FBFE"><s:property value="brandItem.item.model.groupCode"/></pre></td>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.modelDescription" /></td>
						<td><pre style="background-color: #F3FBFE"><s:property value="brandItem.item.model.itemGroupDescription"/></pre></td>
						
				</tr>
				<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.productCode" /></td>
						<td><s:property value="brandItem.item.product.groupCode"/></td>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.productDescription" /></td>
						<td><s:property value="brandItem.item.product.itemGroupDescription"/></td>
						
				</tr>
		 </s:if>
		 </s:if>
		 <s:else>
		 <s:if test="item.itemType !=null && item.itemType != 'PART'">		
				<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.modelCode" /></td>
						<td><pre style="background-color: #F3FBFE"><s:property value="item.model.groupCode"/></pre></td>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.modelDescription" /></td>
						<td><pre style="background-color: #F3FBFE"><s:property value="item.model.itemGroupDescription"/></pre></td>
						
				</tr>
				<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.productCode" /></td>
						<td><s:property value="item.product.groupCode"/></td>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.productDescription" /></td>
						<td><s:property value="item.product.itemGroupDescription"/></td>
						
				</tr>
		 </s:if>
		 </s:else>
				<tr>
				<s:if test="isLoggedInUserADealer()">
				<s:if test="brandItem.item.itemType !=null && brandItem.item.itemType == 'PART'">
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.location" /></td>
					<td><s:property value="brandItem.item.make" /></td>
					</s:if>
						<s:if test="brandItem.item.itemType !=null && 'PART' == brandItem.item.itemType">	
							<s:iterator value="brandItem.item.belongsToItemGroups" status="status">	
								<s:iterator value="scheme.purposes">
									<s:if test="@tavant.twms.domain.common.AdminConstants@WARRANTY_COVERAGE_PURPOSE.toString().equals(name)">					
										<td class="labelStyle" nowrap="nowrap"><s:text
										name="label.common.majorComponentCategory" /></td>
										<td><s:property value="[1].name" /></td>
									</s:if>
							   </s:iterator>	
						 	</s:iterator>			
						</s:if>
						</s:if>
						<s:else>
						<s:if test="item.itemType !=null && item.itemType == 'PART'">
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.location" /></td>
					<td><s:property value="item.make" /></td>
					</s:if>
						<s:if test="item.itemType !=null && 'PART' == item.itemType">	
							<s:iterator value="item.belongsToItemGroups" status="status">	
								<s:iterator value="scheme.purposes">
									<s:if test="@tavant.twms.domain.common.AdminConstants@WARRANTY_COVERAGE_PURPOSE.toString().equals(name)">					
										<td class="labelStyle" nowrap="nowrap"><s:text
										name="label.common.majorComponentCategory" /></td>
										<td><s:property value="[1].name" /></td>
									</s:if>
							   </s:iterator>	
						 	</s:iterator>			
						</s:if>
						<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.utilevPartNumber" /></td>
						<td ><s:iterator value="item.brandItems">
					<s:if test="brand.equals('UTILEV') ">
					<s:property value="itemNumber" /> </s:if></s:iterator></td>
						</s:else>
						
					<s:if test="isLoggedInUserADealer()">
			<s:if test="brandItem.item.itemType !=null && 'PART' == brandItem.item.itemType">
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.itemForReturn" /></td>
						<td><s:property value="isPartForReturn(brandItem.item)" /></td>
					
						<authz:ifUserInRole roles="admin, processors">
							<td class="labelStyle" nowrap="nowrap"><s:text name="label.itemForReview" /></td>
							<td><s:property value="isPartForReview(brandItem.item)" /></td>
						</authz:ifUserInRole>
					</tr>
			</s:if>
			</s:if>
			<s:else>
			<s:if test="item.itemType !=null && 'PART' == item.itemType">
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.itemForReturn" /></td>
						<td><s:property value="isPartForReturn(item)" /></td>
					
						<authz:ifUserInRole roles="admin, processors">
							<td class="labelStyle" nowrap="nowrap"><s:text name="label.itemForReview" /></td>
							<td><s:property value="isPartForReview(item)" /></td>
						</authz:ifUserInRole>
					</tr>
			</s:if>
			</s:else>	
					
				</tr>
				<s:if test="isLoggedInUserADealer()">
			<s:if test="brandItem.item.divisionCode != null">
				<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.divisionCode" /></td>
						<td><s:property value="brandItem.item.divisionCode"/></td>
				</tr>
			</s:if>		
			</s:if>
			<s:else>
			<s:if test="item.divisionCode != null">
				<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.item.divisionCode" /></td>
						<td><s:property value="item.divisionCode"/></td>
				</tr>
			</s:if>		
			</s:else>	
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.businessUnitName" /></td>
					<s:if test="isLoggedInUserADealer()"><td><s:property value="brandItem.item.businessUnitInfo" /></td></s:if>
					<s:else><td><s:property value="item.businessUnitInfo" /></td></s:else>
			</tr>
	</table>
	</div>	
     </div>	
     </div>
    
      <div align="center" class="spacingAtTop">
         <s:submit cssClass="buttonGeneric" value="%{getText('button.common.close')}" onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
      </div>  
   </div>
</div>
</u:body>
</html>