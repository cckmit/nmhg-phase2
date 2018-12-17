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
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script type="text/javascript">

function closeCurrentTab() {
    closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
}

function getSummaryPage() {
    var frm = document.getElementById('supplierAdminForm');
    frm.action = '<s:property value="%{taskName.equals(@tavant.twms.jbpm.WorkflowConstants@SHIPMENT_FROM_DEALER_TASK)?'supplierRecoveryAdminDirectShipment_summary.action':'supplierRecoveryAdmin_summary.action'}"/>';
    frm.submit();
}

function submitForm(button) {
   		var frm =document.getElementById('supplierRecoveryForm'); 
    	var transition=dojo.byId("transition");
    	var taskName=dojo.byId("taskName");
    	transition.value= button.value;
   		frm.action='warrantyProcessorSRAction_submit.action';
    	frm.submit();
        
    }
    
function setComments() {
var field = dojo.byId("com");
dojo.byId("internalComments").value = field.value;
}

</script>

<style type="text/css">
.totalAmountRightalign {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:7pt;
	text-transform:uppercase;
	background-color:#F5F5F5;
	color:#636363;
	padding-left:2px;
	align: right;
}
.totalAmountTextRightalign {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:7pt;
	background-color:#F5F5F5;
	color:#636363;
	padding-left:2px;
	align: right;
}
</style>




<%-- Set the appropriate transition depending on the Task name--%>

<div dojoType="twms.widget.TitlePane" id="recoveredComponentsPane" title="<s:text name='title.supplier.recoveredComponents'/>" labelNodeClass="section_header">

<table border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" >
    <tr>
        <td width="10%" class="colHeaderTop" style="word-wrap:break-word"><s:text name="columnTitle.shipmentGenerated.part_no"/></td>
        <td width="6%" class="colHeaderTop"><s:text name="columnTitle.common.quantity"/></td>
        <td width="10%" class="colHeaderTop"><s:text name="columnTitle.common.description"/></td>
        <td width="5%" class="colHeaderTop"><s:text name="label.recovery.return"/></td>
        <td width="20%" class="colHeaderTop"><s:text name="label.recovery.contract"/></td>
        <td width="10%" class="colHeaderTop"><s:text name="columnTitle.partShipperPartsShipped.tracking_number"/></td>
        <td width="10%" class="colHeaderTop"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
        <td width="15%" class="colHeaderTop"><s:text name="columnTitle.listContracts.supplier_name"/></td>
    </tr>
    <s:set name="partsToBeShownCount" value="0" scope="page"></s:set>
    <s:hidden name="id"/>
    <s:hidden name="recoveryClaim"/> 
    <s:iterator
        value="recoveryClaim.recoveryClaimInfo.recoverableParts"
        id="recoverablePart" status="partsStatus">
        <s:set id="oEMPartReplaced" value="#recoverablePart.oemPart" name="oEMPartReplaced"/>
            <tr>
                <td width="10%" ><s:property 
                    value="#oEMPartReplaced.itemReference.referredItem.number" /></td>
                <td width="6%" ><s:property
                    value="#recoverablePart.quantity" /></td>
                <td width="10%" ><s:property
                    value="#oEMPartReplaced.itemReference.unserializedItem.description" /></td>
                <td width="5%" align="center">
               <s:checkbox disabled="!supplierPartReturnModificationAllowed"
                name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded"
                id="val_%{#partsStatus.index}"/>
                <s:hidden name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded" 
                id="hide_%{#partsStatus.index}" />
               
                <script type="text/javascript">
                 dojo.addOnLoad(function(){
                 	dojo.connect(dojo.byId("val_<s:property value="%{#partsStatus.index}"/>"),"onclick",function(){
                 		if( !dojo.byId("val_<s:property value="%{#partsStatus.index}"/>").checked){
                 			dojo.byId("hide_<s:property value="%{#partsStatus.index}"/>").value="false";
                 		}                 		
                 	});
                 });
                </script>
                </td>
                <td width="20%" cssStyle="width:65px;"><s:property
                    value="recoveryClaim.contract.name"/>
                   </td>
                   <td width="10%" cssStyle="width:65px;"><s:if test="!supplierPartReturns.isEmpty() && supplierPartReturns[0].supplierShipment!=null"><s:property 
                   value="supplierPartReturns[0].supplierShipment.trackingId"/></s:if></td>
                   <td width="10%" cssStyle="width:65px;"><s:property 
                   value="supplierPartReturns[0].rgaNumber"/>
                   <s:if test="!supplierPartReturns.isEmpty() && supplierPartReturns[0].rgaNumber!=null">
                   		<s:hidden name="rgaNumbersProvided[%{#partsStatus.index}]" value="%{supplierPartReturns[0].rgaNumber}" id="rgaNumbersProvided_%{#partsStatus.index}" ></s:hidden>
                   </s:if>
                   </td>
                   <td width="40%" ><s:property
                    value="#recoverablePart.supplierItem.ownedBy.name"/>
                   </td> 
            </tr>
            
            <s:set name="partsToBeShownCount" value="#attr['partsToBeShownCount']+1" scope="size"></s:set>
        </s:iterator>
    		
											
</table>

</div>
<div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
        labelNodeClass="section_header" open="true" id="partsAuditHistoryPane">
        <s:push value="recoveryClaim">
        <jsp:include flush="true" page="../../claims/forms/common/read/partsAuditHistory.jsp"/>
        </s:push>   
</div>
<div id="separator"></div>
<div dojoType="twms.widget.TitlePane" id="recoveryCommentsPane" title="<s:text name='label.common.recoveryComments'/>" labelNodeClass="section_header">
	<table>
		<tr>  	
           <td width="30%" class="labelStyle"><s:text name="label.common.externalComments"/>:</td>
           <td width="70%" class="labelNormalTop">
           <s:if test="hasActionErrors()">
           <t:textarea rows="4" cols="105" name="recoveryClaim.externalComments"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
           </s:if>
           <s:else>
              <t:textarea rows="4" cols="105" name="recoveryClaim.externalComments" value=""
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
          </s:else>
          </td>
        </tr>
		<tr>  	
           <td width="30%" class="labelStyle"><s:text name="label.common.internalComments"/>:</td>
           <td width="70%" class="labelNormalTop">
           <s:if test="hasActionErrors()">
           <t:textarea rows="4" cols="105" name="recoveryClaim.comments"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
           </s:if>
           <s:else>
              <t:textarea rows="4" cols="105" name="recoveryClaim.comments" value=""
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
          </s:else>
          </td>
        </tr>
	</table>
	
	<table>
	   	<tr>
	   		<td width="66%" class="labelStyle">
	   			<s:text name="title.attributes.accountabilityCode"/> :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   		</td>
	   		<td>
	   			<tda:lov name="recoveryClaim.claim.accountabilityCode" className="AccountabilityCode" id="accountabilityCode" 
	   				businessUnitName="recoveryClaim.businessUnitInfo.name"></tda:lov>
	   		</td>
	   	</tr>
   </table>
</div>
<s:hidden id="transition" name="transition"/>
<s:hidden id="taskName" name="taskName" value="Not For Recovery"/>
<div id="separator"></div>
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
<div class="buttonWrapperPrimary"><input type="button"
    value="Cancel" class="buttonGeneric" onclick="closeCurrentTab();" /> 
    <input type="button" value="Reject"
    class="buttonGeneric" onclick="submitForm(this);" />
    <input type="button" value="Accept"
    class="buttonGeneric" onclick="submitForm(this);" /> </div>
</authz:ifNotPermitted>


<script type="text/javascript">
    function sendThisRequest(id) {
        var url = "contract_detail.action?id=" + id;
        var tabLabel = 'Contract Code ' + id;
        top.publishEvent("/tab/open", {label: tabLabel, url: url,  decendentOf: getMyTabLabel() });
    }
</script>

