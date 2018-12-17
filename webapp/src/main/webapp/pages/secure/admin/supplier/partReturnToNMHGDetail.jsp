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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: <s:text name="title.common.warranty" /> ::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="partreturn.css"/>
    <style type="text/css">
        .addRow {
            margin-top: -14px;
            height: 14px;
            text-align: right;
            padding-right: 17px;
        }
    </style>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
</head>
 <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });        
        
</script>
<u:body>
  <u:actionResults/>
<form action="duePartsForSupplier_submit.action" method="post">
<br/>
   <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.recoveryClaimDetails"/>: <s:property value="fetchedRecoveryClaim.recoveryClaimNumber"/>"
             labelNodeClass="section_header" open="true">
    <table class="grid borderForTable" style="width: 97%" cellspacing="0" cellpadding="0">
    <s:hidden name="recoveryClaim.id" value="%{fetchedRecoveryClaim.id}" />
            <s:hidden name="id" value="%{fetchedRecoveryClaim.id}" />
            <s:hidden name="transitionTaken"
            			value='%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}' />
       <s:iterator value="recoverablePartsBeans"
       				id="recoverablePartBean" status="partsStatus">
       	<s:set id="oEMPartReplaced" value="#recoverablePartBean.recoverablePart.oemPart" name="oEMPartReplaced"/>
        <s:iterator value="recoveryPartTaskBeans" status ="taskIterator">
           <input type="hidden"
              name="uiRecoverablePartsBeans[<s:property value="%{#partsStatus.index}"/>].recoveryPartTaskBeans[<s:property value="%{#taskIterator.index}"/>].task"
              value="<s:property value="task.id"/>"/>
        </s:iterator>
        <input type="hidden" name="uiRecoverablePartsBeans[<s:property value="%{#partsStatus.index}"/>].recoverablePart"
              				value="<s:property value="recoverablePartsBeans[#partsStatus.index].recoverablePart.id"/>"/>
       	<tbody>
       	 <s:if test="#partsStatus.index==0">
       	   <tr class="row_head">
                <th><input id="selectAll_<s:property value="fetchedRecoveryClaim.id" />" type="checkbox"
                             	<s:if test="selected"> checked="checked" </s:if>
                             	value="checkbox" style="border:none"/>
                          <script>
                              var multiCheckBox = dojo.byId("selectAll_<s:property value="fetchedRecoveryClaim.id" />");
                              var multiCheckBoxControl = new CheckBoxListControl(multiCheckBox);

                              // this var is defined in parent jsp.
                             // __masterCheckBoxControls.push(multiCheckBoxControl);
                          </script>
                </th>
                <th><s:text name="label.common.partNumber" /></th>
                <th><s:text name="label.common.description" /></th>
                <th><s:text name="label.supplierPartNumber" /></th>
                <th><s:text name="label.partReturn.toBeShipped" /></th>
               <%-- <th><s:text name="label.partReturn.shipementGenerated"/></th>
                <th><s:text name="label.partReturn.cannotBeShipped"/></th>
                <th><s:text name="label.partReturn.shipped"/></th>
                <th><s:text name="label.partReturn.total"/></th>--%>
                <th><s:text name="label.partReturn.ship"/></th>
                <th><s:text name="label.common.returnlocation" /></th>
                <th><s:text name="label.partReturn.cannotShip" /></th>
             </tr>
             </s:if>
			 <tr>
					<td><s:checkbox
                              		name="uiRecoverablePartsBeans[%{#partsStatus.index}].selected"
                         		     value="selected"
                         		     id="part_checkbox_%{#partsStatus.index}" />  
                         		     <script>
                        			    var selectElementId = "<s:property value="%{#partsStatus.index}" />";
                                                    multiCheckBoxControl.addListElement(dojo.byId(selectElementId));
                        			  </script>                      	
                    </td>
					<td><s:property
							value="#oEMPartReplaced.itemReference.referredItem.number" /></td>
					<td><s:property
							value="#oEMPartReplaced.itemReference.unserializedItem.description" /></td>
					<td><s:property value="recoverablePart.supplierItem.number" /></td>
					<td><s:property value="toBeShipped" /></td>
					<%--<td><s:property value="shipmentGenerated" /></td>
					<td><s:property value="cannotShip" /></td>
					<td><s:property value="shipped" /></td>
					<td><s:property value="totalNoOfParts" /></td>--%>
					<td><s:textfield name="uiRecoverablePartsBeans[%{#partsStatus.index}].ship" size="3" value="%{ship}"/></td>
					<td><s:property
							value="recoverablePart.getRetrunLocationForSupplier()" /></td>
					<td><s:textfield name="uiRecoverablePartsBeans[%{#partsStatus.index}].cannotShip" size="3" value="%{cannotShip}"/></td>

			 </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>


     <div class="separator" ></div>
        <div class="detailsHeader"><s:text name="label.comments"/></div>
    	<div class="inspectionResult" style="width:99%">
            <div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
                <t:textarea name="comments" cols="140"/>
            </div>
        </div>


<div class="buttonWrapperPrimary">
	    <s:submit value="%{getText('button.partReturnConfiguration.generateShipment')}" cssClass="buttonGeneric" />
</div>
</form>
</u:body>
</html>