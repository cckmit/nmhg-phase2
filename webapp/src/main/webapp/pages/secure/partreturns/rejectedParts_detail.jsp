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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<script type="text/javascript" src="scripts/partReturnPopup.js"></script>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="claimForm.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
</head>

<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	 dojo.require("twms.widget.TitlePane");
</script>
<u:body>
     <script type="text/javascript">
      // Used in claim_view.jsp
      var __masterCheckBoxControls = new Array();

      dojo.addOnLoad(function(){
          var masterCheckBox = dojo.byId("masterSelectAll");

          dojo.connect(masterCheckBox, "onclick", function() {
              var isMasterChecked = masterCheckBox.checked;

              dojo.forEach(__masterCheckBoxControls, function(control) {
                  control.forceStateChange(isMasterChecked);
              });
          });
      });
    </script>
     <s:iterator value="loggedInUser.businessUnits" status="userBUs">
        <s:if test="isReturnRequestAllowed(name)">
           <form action="partReturnRequestToPartShipper.action" method="post">
        </s:if>
        <s:else>
            <form action="removePartReturnTaskForDealer.action" method="post">
        </s:else>
    </s:iterator>
    <s:hidden id="identifier" name="id"/>
    <u:actionResults wipeMessages="false"/>
    <div style="margin: 15px 0 5px 0; padding: 0 0 0 10px">
        <input type="checkbox" id="masterSelectAll" checked="checked" />
        <s:text name="label.partReturnConfiguration.selectAllParts" />
    </div>

    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">
	  <s:set name="partsCounter" value="0"/>
	  <s:iterator value="claimWithPartBeans" status="claimIterator">
	  <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" />
	  <jsp:include page="tables/claim_details.jsp"/>    </div>
	  <div dojoType="twms.widget.TitlePane" title="<s:text name="rejectedParts.details"/>" labelNodeClass="section_header" >
			<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" >
			   <thead>
			   <tr class="row_head">
			   <th width="2%" valign="middle" align="center" >
                  <input id="selectAll_<s:property value="claim.id" />" type="checkbox"
                        <s:if test="selected"> checked="checked" </s:if>
                        value="checkbox" style="border:none"/>
                  <script>
                      var multiCheckBox = dojo.byId("selectAll_<s:property value="claim.id" />");
                      var multiCheckBoxControl = new CheckBoxListControl(multiCheckBox);

                      // this var is defined in parent jsp.
                      __masterCheckBoxControls.push(multiCheckBoxControl);
                  </script>
	            </th>
				<th width="15%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
			    <th width="30%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
			    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
			    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.received" /></th>
			    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.requestForReturn" /></th>
	            <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.partReturnNotRequired" /></th>
			    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.inboxView.claimPartReturnStatus" /></th>

			    </tr>
			    </thead>
			  <s:iterator value="partReplacedBeans" status="partIterator">
			    <tr class="tableDataWhiteText">
			    <td width="2%" valign="middle" align="center">
                    <s:checkbox
                        name="partReplacedBeans[%{#partIterator.index}].selected"
                         value="selected"
                         id="%{#claimIterator.index}_%{#partIterator.index}" />


                    <script>
                        var selectElementId = "<s:property value="%{#claimIterator.index}" />_<s:property value="%{#partIterator.index}" />";
                                 multiCheckBoxControl.addListElement(dojo.byId(selectElementId));
                    </script>
                </td>

                <s:iterator value="partReturnTasks" status ="taskIterator">

                  <input type="hidden"
                         name="partReplacedBeans[<s:property value="%{#partIterator.index}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task"
                         value="<s:property value="task.id"/>"/>

                  </s:iterator>
                   <input type="hidden"  size="3" name="partReplacedBeans[<s:property value="%{#partIterator.index}"/>].partReplacedId"
      				value="<s:property value="oemPartReplaced.id"/>"/>
      				<s:set name="totalParts" value="0"/>
      				    <s:set name="indexCounter" value="0"/>
                  <s:set name="rejectCounter" value="0"/>
                  <s:set name="rejectReason" value=""/>
                  <s:set name="acceptCounter" value="0"/>
                  <s:set name="acceptReason" value=""/>
      				<s:iterator value="oemPartReplaced.partReturns" status ="partStatusIte">
                      <s:set name="indexCounter" value="%{#indexCounter + 1}"/>
                      <s:if test="(oemPartReplaced.partReturns.get(#partStatusIte.index).actionTaken.status).equals('Part Accepted')">
                           <s:set name="acceptCounter" value="%{#acceptCounter + 1}"/>
                           <s:set name="totalParts" value="%{#totalParts + 1}"/>
                           <s:set name="acceptReason" value="%{oemPartReplaced.partReturns.get(#partStatusIte.index).inspectionResult.acceptanceReason.description}"/>
                      </s:if>
                      <s:if test="(oemPartReplaced.partReturns.get(#partStatusIte.index).actionTaken.status).equals('Part Rejected')">
                            <s:set name="rejectCounter" value="%{#rejectCounter + 1}"/>
                            <s:set name="totalParts" value="%{#totalParts + 1}"/>
                            <s:set name="rejectReason" value="%{oemPartReplaced.partReturns.get(#partStatusIte.index).inspectionResult.failureReason.description}"/>
                      </s:if>
                  </s:iterator>
			      <td width="15%" style="padding-left:3px;"><s:property value="oemPartReplaced.brandItem.itemNumber"/>  </td>
				  <td width="30%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
			      <td width="6%" style="padding-left:3px;"><s:property value="shipped" /></td>
			      <td width="6%" style="padding-left:3px;"><s:property value="%{#totalParts}" /></td>
			      <td width="6%" style="padding-left:3px;">
			         <input type="hidden"  size="3" name="partReplacedBeans[<s:property value="%{#partIterator.index}"/>].toBeShipped"	value="<s:property value="%{#totalParts}" />"/>
                     <input type="text" id="part_#taskIterator.index_ship" size="3" name="partReplacedBeans[<s:property value="%{#partIterator.index}"/>].ship" value="<s:property value="ship"/>"/></td>
			      </td>
                  <td width="6%" style="padding-left:3px;"><input type="text" id="part_#taskIterator.index_ship" size="3" name="partReplacedBeans[<s:property value="%{#partIterator.index}"/>].cannotShip"
      				value="<s:property value="cannotShip"/>"/></td>

                  <td width="15%" style="padding-left:3px;">
                  <s:text name="label.rejectedPartsInbox.acceptedParts" /> (<s:property value="%{#acceptCounter}" />) :  <s:property value="%{#acceptReason}" /><br />
                  <s:text name="label.rejectedPartsInbox.rejectedParts" /> (<s:property value="%{#rejectCounter}" />) : <s:property value="%{#rejectReason}" />
                  </td>
			      <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
			    </tr>
			  </s:iterator>
			</table>
           </div>
        </div>
	  </s:iterator>

	<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
    <div class="separator" ></div>
    <s:iterator value="loggedInUser.businessUnits" status="userBUs">
        <s:if test="isReturnRequestAllowed(name)">
            <div class="detailsHeader"><s:text name="label.comments"/></div>
            <div class="inspectionResult" style="width:99%">
                 <div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
                    <t:textarea name="comments" cols="140"/>
                </div>
            </div>

            <div class="buttonWrapperPrimary">
                 <s:submit value="%{getText('button.partReturnConfiguration.requestForPartBack')}" cssClass="buttonGeneric" />
            </div>
        </s:if>
        <s:else>
             <div class="buttonWrapperPrimary">
                <s:submit value="%{getText('label.newClaim.remove')}" cssClass="buttonGeneric" />
            </div>
        </s:else>
    </s:iterator>
    </authz:ifNotPermitted>
    </form>
</u:body>
</html>