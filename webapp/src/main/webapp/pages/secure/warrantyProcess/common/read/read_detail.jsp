<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 4:39:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" language="java" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="preview.css"/>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.TitlePane");
    dojo.require("twms.widget.Dialog");    
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.Select");
</script>
<script type="text/javascript" src="scripts/commonWarranty.js"></script>
</head>
<u:body>
<s:form name="warranty_processing" action="warranty_transition_dealer" method="post" id="warrantyForm" theme="twms">
<s:hidden name="id" />
<s:hidden name="status" id="actionDecider"/>
<s:hidden name="selectedBusinessUnit"/>
<s:hidden name="warrantyTaskInstance"/>
<s:hidden name="transactionType" id="typeOfTransaction" value="%{transactionType}"/>
<s:hidden name="warrantyTaskInstanceId" />
<s:if test="marketingInformation!=null && marketingInformation.id!=null">
    <s:hidden name="marketingInformation"/>
</s:if>
<s:iterator value="inventoryItemMappings" status="inventoryItemMappings">
	<s:hidden name="inventoryItemMappings[%{#inventoryItemMappings.index}].inventoryItem" value="%{inventoryItem.id}"/>
	<s:hidden name="inventoryItemMappings[%{#inventoryItemMappings.index}].warrantyDeliveryDate" />
</s:iterator>
<div dojoType="dijit.layout.LayoutContainer" style="overflow-y:auto">
    <div dojoType="dijit.layout.ContentPane" >

<u:actionResults/>
			<div style="background: #F3FBFE; border: 1px solid #EFEBF7; margin: 5px; padding-bottom: 10px;">
			<table class="form" cellpadding="0" cellspacing="0">
				<tr style="width: 50%">
					<td id="dealerNameText" class="labelStyle"><s:text
						name="label.common.dealerName" />:</td>
					<td><s:property value="%{warranty.forDealer.name}" /></td>
				</tr>
			</table>
			</div>
            <jsp:include flush="true" page="equipment_info.jsp"/>
            <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.common.customerInfo"/>"
                 id="customer_info"labelNodeClass="section_header"  open="true">
                <jsp:include flush="true" page="/pages/secure/warranty/customer_details_readonly.jsp"></jsp:include>
            </div>
        </div>
        
        <s:if test="isAdditionalInformationDetailsApplicable() || marketingInformation!=null">
	        <div dojoType="dijit.layout.ContentPane" >
	            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.marketingInformation"/>"
	                 id="marketing_info" labelNodeClass="section_header" open="true">
	                <div style="width:99%">
	                <jsp:include flush="true" page="/pages/secure/warranty/warranty_marketinginfo_preview.jsp"/>
	                </div>
	            </div>
	        </div>
	    </s:if>    

        <div dojoType="dijit.layout.ContentPane">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.WarrantyAuditHistory"/>"
                 id="warranty_audit_info" labelNodeClass="section_header" open="true">
                <s:push value="warrantyTaskInstance.warrantyAudit">
                   <div style="width:99%"> <jsp:include flush="true" page="warranty_audit_history.jsp"/></div>
                </s:push>
            </div>
        </div>

        <s:if test="warrantyTaskInstance.status.status!='Deleted'">
        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.comments"/>"
                 id="comments"labelNodeClass="section_header"  open="true">
                <table width="80%">
                <tr>
                    <td width="40%" nowrap="nowrap" >
                        <s:text name="label.common.comments"/>:
                    </td>
                    <td width="60%">
                        <t:textarea name="warrantyAudit.externalComments"
                           cols="80" rows="4" cssStyle="width:70%" value=""></t:textarea>
                    </td>
                </tr>
             </table>
            </div>
        </div>
        </s:if>

        <s:if test="warrantyTaskInstance.status.status!='Deleted'">
            <div>
                <table align="center" width="60%" border="0" cellpadding="0" cellspacing="0" class="buttons">
                    <tbody>
                        <tr>
                            <s:if test="warrantyTaskInstance.status.status=='Forwarded'">
                                <td align="center">
                                    <input type="button" value='<s:text name="label.common.reply"/>' id="replyWarranty"/>
                                </td>
                            </s:if>
                            <s:if test="warrantyTaskInstance.status.status=='Rejected'">
                                <td align="center">
                                    <input type="button" value='<s:text name="button.common.delete"/>'
                                           id="deleteWarranty"/>
                                </td>

                            </s:if>
                        </tr>
                    </tbody>
                </table>
            </div>
        </s:if>
        <script type="text/javascript">
            dojo.addOnLoad(function() {
                if (dojo.byId("replyWarranty")) {
                    dojo.connect(dojo.byId("replyWarranty"), "onclick", function() {
                        dojo.byId("actionDecider").value = 'REPLIED';
                        dojo.byId("warrantyForm").submit();
                    });
                }
                if (dojo.byId("deleteWarranty")) {
                    dojo.connect(dojo.byId("deleteWarranty"), "onclick", function() {
                        dojo.byId("actionDecider").value = 'DELETED';
                        dojo.byId("warrantyForm").action="warranty_transition_admin.action";
                        dojo.byId("warrantyForm").submit();
                    });
                }
            });
        </script>

    </div>
</div>
</s:form>
</u:body>
</html>