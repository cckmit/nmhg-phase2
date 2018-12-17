<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 26, 2008
  Time: 6:38:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title><s:text name="title.newClaim.machineClaimNonSerialized"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="multiCar.css"/>

    <style type="text/css">
        table.form {
             margin-bottom: 5px;
            border: 1px solid #EFEBF7;
			background-color:#F3FBFE;
			margin-left:5px;
			width:99%;
        }
        label {
            color: #959494;
            font-weight: bold;
        }
    </style>

    <script type="text/javascript" src="scripts/ClaimForm.js"></script>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount");
		});
        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
    </script>
</head>

<u:body >
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow:auto" >
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:if test="!hasActionMessages()">
<u:actionResults/>
</s:if>
<h3><s:text name="label.viewClaim.page1of2"/></h3>
<s:form method="post" theme="twms" validate="true" id="form"
        name="saveNonSerializedClaimDraft" action="saveNonSerializedClaimDraft.action">
<s:hidden name="dealerNumberSelected" id="isDealerNumber"/>
<s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
	<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" value = "%{currentBusinessUnit.name}"/>
</s:if>
<s:else>
	<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>
</s:else>
    <div style="border:1px solid #EFEBF7;background:#F3FBFE;margin:5px;">
    <table class="form" cellpadding="0" cellspacing="0" style="border:none">
        <tr><td style="padding:0;margin:0 ">&nbsp;</td></tr>
        <tr>
   			<td width="26%">
               <label for="BU" class="labelStyle"> <s:text name="label.common.businessUnit"/>:</label>
            </td>
           <td colspan="3" style="padding-left:5px;">
               <s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
					<s:property value="currentBusinessUnit.name"/>
				</s:if>
				<s:else>
					<s:property value="selectedBusinessUnit"/>
				</s:else>
            </td>
   		</tr>
		<tr>
            <td class="labelStyle" width="26%">

                <s:if test="isLoggedInUserADealer()">
                <label for="dealer" class="labelStyle"><s:text name="label.common.dealer"/>:</label>
                </s:if>
                <s:else>
                <div id="dealerNameLabel" class="labelStyle">
                    <s:text name="label.common.dealerName"/>:
                 </div>
                 <s:if test="claim == null || claim.id == null">
                  <div id="dealerNumberLabel" class="labelStyle">
                     <s:text name="label.common.dealerNumber"/>:
                 </div>
                 <div id="toggle" style="cursor:pointer;">
                     <div id="toggleToDealerNumber" class="clickable">
                         <s:text name="toggle.common.toDealerNumber" />
                     </div>
                     <div id="toggleToDealerName" class="clickable">
                         <s:text name="toggle.common.toDealerName"/>
                     </div>
                 </div>
				</s:if>
                </s:else>
            </td>
            <td style="text-transform:uppercase; padding-left:5px;" width="35%">
                <s:if test="claim == null || claim.id == null">

                    <s:if test="isLoggedInUserADealer()">
                      <s:property value="getLoggedInUser().getCurrentlyActiveOrganization().name"/>
                        <s:hidden name="dealer" value="%{getLoggedInUser().getCurrentlyActiveOrganization()}"/>
                        <s:hidden name="claim.forDealer" value="%{getLoggedInUser().getCurrentlyActiveOrganization().id}" id="dealer"/>
                        <script type="text/javascript">
                         dojo.addOnLoad(function() {
                            dojo.publish("/dealer/modified", [{
                                    params: {
                                        "dealerName" : '<s:property value="getLoggedInUser().getCurrentlyActiveOrganization().id"/>',
                                        "forDealer" : '<s:property value="getLoggedInUser().getCurrentlyActiveOrganization().id"/>'
                                    }
                                }]);
                        });
                         </script>
                    </s:if>
                    <s:else>
                    <div id="dealerName" style="margin-left:0px;">
                        <sd:autocompleter id='dealerNameAutoComplete' href='list_claim_dealers.action' name='claim.forDealer' value='%{claim.forDealer.name}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}" />
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
    					<script type="text/javascript">
    					var _lastSeenDealership;
                                dojo.addOnLoad(function() {
                                  var dealerSelect = dijit.byId("dealerNameAutoComplete");
                                dealerSelect.sendDisplayedValueOnChange = false;
                                    dojo.connect(dijit.byId("dealerNameAutoComplete"), "onChange", function(newValue) {
                                    dojo.publish("/dealer/modified", [{
                                            params: {
                                                "dealerName" : newValue,
                                                "forDealer" : newValue
                                            }
                                        }]);
                                    });
                                });
                            </script>
    				</div>
    				<div id="dealerNumber">
    				    <sd:autocompleter id='dealerNumberAutoComplete' href='list_claim_dealer_numbers.action' name='claim.forDealer' value='%{claim.forDealer.serviceProviderNumber}' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}" loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' />
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
    					<script type="text/javascript">

                                dojo.addOnLoad(function() {
                               var dealerSelect = dijit.byId("dealerNumberAutoComplete");
                                dealerSelect.sendDisplayedValueOnChange = false;
                                    dojo.connect(dijit.byId("dealerNumberAutoComplete"), "onChange", function(newValue) {
                                    dojo.publish("/dealer/modified", [{
                                        params: {
                                            "dealerName" : newValue,
                                             "forDealer" : newValue
                                        }
                                    }]);
                                    });
                                });

                            </script>
    				</div>
    				<script type="text/javascript">
					    dojo.addOnLoad(function() {
					        <s:if test="dealerNumberSelected">
					          showDealerNumber();
					      </s:if>
					      <s:else>
					          showDealerName();
					      </s:else>

					        dojo.connect(dojo.byId("toggleToDealerName"), "onclick", function() {
					            dijit.byId('dealerNumberAutoComplete').setValue("");
					            showDealerName();
					        });
					        dojo.connect(dojo.byId("toggleToDealerNumber"), "onclick", function() {
					            dijit.byId('dealerNameAutoComplete').setValue("");
					            showDealerNumber();
					        });
					    });
					</script>
                    </s:else>
                </s:if>
                <s:else>
                    <s:hidden name="claim.forDealer" value="%{claim.forDealer.id}" id="dealer"/>
                    <s:hidden name="claim.filedBy"/>
                    <s:property value="claim.forDealer.name"/>
                </s:else>
            </td>
            <td width="15%" style="width: 218px;">
                <label for="type" class="labelStyle"><s:text name="label.common.claimType"/>:</label>
            </td>
            <td style="padding-top:5px;" valign="top">
                <s:select id="type" name="claimType" list="claimTypes" cssStyle="width:145px;"
                	    listKey="type" listValue="%{getText(displayType)}" disabled="%{claim.id != null}" />
                <script type="text/javascript">
                dojo.addOnLoad(function() {
                    var form = dojo.byId("form");
                    dijit.byId("type").onChange = function(value) {
                        if(value == "Parts" || value == "Campaign" || value == "Attachment") {
                            form.action = "chooseClaimTypeAndDealer.action";
                            form.submit();
                        }
                    };
                });
				</script>
            </td>
        </tr>
		<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
    </table>

    <jsp:include flush="true" page="../forms/common/write/non_serialized_header.jsp" />
</div>
    <div class="spacer5"></div>
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons">
        <tbody>
            <tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.continue')}" />
                </td>
            </tr>
        </tbody>
    </table>
</s:form>
</div>
</div>
</u:body>
</html>