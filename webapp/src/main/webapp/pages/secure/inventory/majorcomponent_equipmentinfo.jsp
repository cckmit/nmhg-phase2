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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
	 dojo.require("twms.widget.TitlePane");
</script>


<table width="100%" cellspacing="0" cellpadding="0" class="grid">
<tbody>
	<tr>
		<td class="labelStyle" nowrap = "nowrap"><s:text name="label.common.serialNumber" />:</td>
		<td>
			<s:property value="%{getInventorySerialNumberForMajorComponent(id)}" />
		</td>
	</tr>
	<tr>
		<td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.majorComponent.componentSerialNo"/>:</td>
        <td class=""  width="37%">
            <s:property value="inventoryItem.serialNumber"/>            
        </td>
        <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.common.warrantyStartDate"/>:</td>
        <td class="" width="35%">
            <s:property value="inventoryItem.wntyStartDate"/>  
        </td>
        
    </tr>
    <tr>
        <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.itemNumber"/>:</td>
        <td class="">
            <s:property value="inventoryItem.ofType.number"/>
        </td>
        <td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="columnTitle.common.warrantyEndDate"/>:</td>
        <td><s:property value="inventoryItem.wntyEndDate"/></td>
    </tr>
     
    <tr>
        <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.seriesTypeDescription"/>:</td>
        <td class="">
            <s:if test="inventoryItemCompositon!=null && inventoryItemCompositon.size()>0">
         <s:iterator value="inventoryItemCompositon" status="status">
         <s:if test="serialTypeDescription!=null && part.serialNumber==inventoryItem.serialNumber">
            <s:property value="serialTypeDescription"/>
        </s:if> 
        </s:iterator> 
        </s:if>
        </td> 
        <td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.dateOfDelivery"/>:</td>
		<td class=""><s:property value="inventoryItem.deliveryDate"/></td>
	</tr>
	<tr>	       
        <s:if test="partOf.serialNumber != null">
          <td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="columnTitle.common.installedOnUnit"/>:</td>
          <td>       
			<u:openTab cssClass="link" url="inventoryDetail.action?id=%{partOf.id}" 
				    id="1234" tabLabel="EquipmentInfo %{partOf.serialNumber}" autoPickDecendentOf="true" >
                 <s:property value="partOf.serialNumber" />                        	
            </u:openTab>           
         </td>
        </s:if>             
     </tr>         
 </tbody> 
 </table>
  <s:if test="partOf == null">
    <jsp:include page="../registerMajorComponent/ownerData.jsp"/>       
 </s:if> 
  <s:if test="inventoryItem.warranty.certifiedInstaller != null || inventoryItem.warranty.nonCertifiedInstaller != null "> 
    <jsp:include page="../registerMajorComponent/installerData.jsp"/>
  </s:if>         

    
 


