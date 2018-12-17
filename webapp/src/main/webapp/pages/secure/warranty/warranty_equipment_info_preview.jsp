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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="common.css"/>
<script type="text/javascript">
	dojo.require("dojox.layout.ContentPane");

</script>

<table  cellspacing="0" cellpadding="0" id="equipment_details_preview_table" width="100%" class="grid borderForTable" style="clear: both;">
    <thead>
        <tr  class="row_head">
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader non_editable" width="5%"><s:text name="label.common.product"/></th>
            <th class="warColHeader non_editable" width="5%"><s:text name="label.common.model"/></th>
            <th class="warColHeader non_editable" width="5%"><s:text name="label.common.itemCondition"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.deliveryDate"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.hoursOnMachine"/></th>
            <th class="warColHeader non_editable" width="25%" ><s:text name="warranty.transfer.coverage"/></th>
            <th class="warColHeader non_editable" ><s:text name="label.common.shipmentDate"/></th>
            <th class="warColHeader non_editable" ><s:text name="label.modifyDRETR.currentOwnerInfo"/></th>
            <th class="warColHeader non_editable" width="25%"><s:text name="label.warranty.supportDocs"/></th>
        </tr>
    </thead>
    <tbody>        
        <tr>
            <td>
                <u:openTab autoPickDecendentOf="true" id="equipment_%{inventoryItem.id}"
                tabLabel="Serial Number %{inventoryItem.serialNumber}"
                url="inventoryDetail.action?id=%{inventoryItem.id}">
                    <u style="cursor: pointer;">
                        <s:property value="inventoryItem.serialNumber" />
                    </u>
                </u:openTab>
            </td>
            <td>
                <s:property value="inventoryItem.ofType.product.name"/>
            </td>
            <td>
                <s:property value="inventoryItem.ofType.model.name" />
            </td>
            <td>
                <s:property value="inventoryItem.conditionType.itemCondition" />
            </td>
           <td>            
	            <s:if test="inventoryItem.deliveryDate != null">            
	               <s:property value="%{inventoryItem.deliveryDate}" />
	            </s:if>
	            <s:else>             
	               <s:property value="%{inventoryItem.shipmentDate}" />
	            </s:else>            
            </td>         
            <td>
                <s:textfield name="inventoryItem.hoursOnMachine"
                             size="10" readonly="true" cssClass="numeric" id="hoursOnMachine"/>
            </td>
            <td>
                <div dojoType="dojox.layout.ContentPane" executeScripts="true" scriptSeparation="false"
                     id="policyDetails">
                    <jsp:include page="policy_list_preview.jsp" />    
                </div>
            </td>
            <td>
                <s:property value="inventoryItem.shipmentDate" />
            </td>
            <td>
                <s:text name="label.name" />
                <span id="oldCustomerName">
                    <s:property value="inventoryItem.warranty.customer.name" />
                </span>
            </td>
            <td align="centre">
                <div dojoType="dojox.layout.ContentPane" executeScripts="true" scriptSeparation="false"
                     id="attachments_<s:property value="#inventoryItemMappings.index" />">
                     <s:if test="warranty.attachments.empty">
                         <s:text name="error.warranty.noAttachments" />
                     </s:if>
                     <s:else>
						<s:iterator value="warranty.attachments" status="attachments">
							<a id="attached_file_name_<s:property value="#attachments.index" />">
							    <s:property	value="warranty.attachments[#attachments.index].fileName" />
						    </a>
							<script type="text/javascript">
						     dojo.addOnLoad(function(){
			                    dojo.connect(dojo.byId("attached_file_name_<s:property value="#attachments.index" />"), "onclick", function(event) {
			                        dojo.stopEvent(event);
			                        getFileDownloader().download("downloadDocument.action?docId=<s:property	value="warranty.attachments[#attachments.index].id" />");
			                    });	
			                 });							    
							</script>
						</s:iterator>
					</s:else>     
                </div>
            </td>           
        </tr>
   </tbody>
</table>