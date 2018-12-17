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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>

    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="adminPayment.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <style>
    .labelStyle{
    padding-left:15px;
    }
    .label{
	color:#FF0000;
	padding-left:15px;
	}
    </style>
</head>
<u:body>

<s:form action="create_payment_definition" theme="twms" validate="true" method="post" id="baseForm">
	<u:actionResults/>
	<s:hidden name="appPolicySelected" id="isAppPolicySelected"></s:hidden>
	<s:hidden name="paymentDefinition" value="%{paymentDefinition.id}"></s:hidden>
	<div style="overflow-y:auto">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="width:99.2%;
		margin-left: 5px; margin-top: 5px;padding: 0px; overflow-x: hidden;overflow-y:auto;">
     <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background:#F3FBFE;border:1px solid #EFEBF7">
        <tr>
          <td colspan="4" nowrap="nowrap" class="admin_section_heading"><s:text name="label.common.conditions"/></td>
        </tr>
		<tr>
		<td style="padding:0px">&nbsp;</td>
		</tr>
		<tr id="policyCategory">
			<s:if test="appPolicySelected">
				<script type="text/javascript">
					dojo.addOnLoad ( function() {
						dojo.byId('appPolicy').style.display="none";
	      			});
      			</script>
			</s:if>

    		<td class="labelStyle" width="23%">
   				<s:text name="label.managePayment.policyName"/>:<br/>
	   			(<a id="toggleToDealer" href="javascript:void(0)" onClick="showPolicy()" class="clickable"><s:text name="toggle.common.toPolicyLabel"/></a>)
    		</td>
    		<td class="labelNormal">
    		    <sd:autocompleter href='list_applicable_policies.action' id='policyText' name='partialPolicyCategory' loadOnTextChange='true' loadMinimumCount='2' showDownArrow='false' autoComplete='false' keyName='paymentDefinition.criteria.policyDefinition' value='%{paymentDefinition.criteria.policyDefinition.code}' />

    		</td>
    	</tr>
    	<tr id="appPolicy">
    		<s:if test="!appPolicySelected">
    			<script type="text/javascript">
					dojo.addOnLoad ( function() {
						dojo.byId('policyCategory').style.display="none";
	      			});
      			</script>
      		</s:if>
    		<td class="labelStyle" width="23%">
   				<s:text name="label.managePayment.policyLabel"/>:<br/>
	   			(<a href="javascript:void(0)" onClick="showPolicyList()" class="clickable"><s:text name="toggle.common.toPolicyName"/></a>)
    		</td>
    		<td class="labelNormal">
      			<div style="margin-left:7px;">
      			<sd:autocompleter href='list_policy_categories.action' id='categoryText' name='partialPolicyCategory' loadOnTextChange='true' loadMinimumCount='2' showDownArrow='false' autoComplete='false' keyName='paymentPolicyLabel' value='%{paymentDefinition.criteria.label.name}' />
                                 </div>
    		</td>
    	</tr>
		<tr>
			<td class="labelStyle" width="23%"><s:text name="label.common.claimType"/>:</td>
			<td class="labelNormal">
    			<select id="claimType" name="paymentDefinition.criteria.claimType" >
    			    <option value="ALL"><s:text name="dropdown.common.all"/></option>
	    			<s:iterator value="claimTypes">
                        <option value='<s:property value="type.toUpperCase()" />'><s:text name="getText(getDisplayType())"/></option>
                    </s:iterator>
                </select>
                <script type="text/javascript">
                        <s:if test="paymentDefinition.id==null">
                            dojo.byId("claimType").value="ALL";
                        </s:if>
                        <s:else>
                        dojo.byId("claimType").value = "<s:property value="paymentDefinition.criteria.claimType.type.toUpperCase()" />";
                        </s:else>
                </script>
			</td>
		</tr>
		<tr>
		<td style="padding:0px">&nbsp;</td>
		</tr>
	</table>
	</div>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="width: 99.2%; background:#F3FBFE; border: #EFEBF7 1px solid; margin-left: 5px; margin-top: 5px;padding: 0px; overflow-x: hidden ;overflow-y: auto;">
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" class="admin_section_heading"><s:text name="label.managePayment.paymentDefinition"/></td>
        </tr>
        <tr>
          <td colspan="4" nowrap="nowrap" height="5"></td>
        </tr>
     </table>
     <table width="43%" border="0">
      <tr>
       <td class="mainTitle"><s:text name="label.costCategory"/></td><td class="mainTitle"><s:text name="label.managePolicy.priority"/></td>
     </tr>
     <tr></tr>
     </table>
        <s:set name="notSelectedCount" value="paymentDefinition.paymentSections.size()" scope="page"></s:set>
        <s:iterator value="allSections" id="section" status="sectionStatus">
           <div class="bgColorBorder">
           <table width="100%" border="0" cellspacing="0" cellpadding="0">
        	<s:if test="paymentDefinition.doesCoverSection(name)">
                <s:set name="selectedCount" value="%{#sectionStatus.index}" scope="page"></s:set>
	        	<s:property value="#selectedCount"></s:property>
	        	<tr>
                  <td class="mainTitle" width="3%" align="center">
		          <input type="checkbox" name="paymentDefinition.paymentSections[<s:property value="#attr['selectedCount']"/>].section" value="<s:property value="%{#section.id}"></s:property>"

		          <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
		              disabled="disabled"
                      checked="checked"
                  </s:if>
                  <s:if test="%{!isSectionConfigured(name)}">
                      disabled="disabled"
                  </s:if>
                  <s:else>
                      checked="checked"
                  </s:else>
                  onchange="uncheckSections('paymentSections<s:property value="#attr['selectedCount']"/>');"/>
				  <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
					<input type="hidden" name="paymentDefinition.paymentSections[<s:property value="#attr['selectedCount']"/>].section" value="<s:property value="%{#section.id}"></s:property>" />
				  </s:if>
		          </td>
		          <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
			          <td id="tdsection<s:property value="%{#sectionStatus.index}"/>" class="sectionOpenCPD" onclick="expandSection('section<s:property value="%{#sectionStatus.index}"/>');"><s:text name="%{getI18NMessageKey(name)}"/></td>
		          </s:if>
		          <s:else>
		          	<td id="tdsection<s:property value="%{#sectionStatus.index}"/>" class="sectionCloseCPD" onclick="expandSection('section<s:property value="%{#sectionStatus.index}"/>');"><s:text name="%{getI18NMessageKey(name)}"/></td>
		          </s:else>
                <s:if test="%{isSectionConfigured(name)}">
                <s:hidden id="paymentSections%{#attr['selectedCount']}" name="paymentDefinition.paymentSections[%{#attr['selectedCount']}]" value="%{getPaymentSectionId(name)}" ></s:hidden>
                </s:if>
                </tr>
		        <tr><td colspan="2" width="100%">
					<div id="section<s:property value="%{#sectionStatus.index}"/>" <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">style="display:block"</s:if><s:else>style="display:none"</s:else>>
			        	<table width="100%" border="0" cellspacing="0" cellpadding="0">
					        <s:iterator value="getPaymentVaribles(name)" id="paymentVariable" status="variableStatus">
					        <tr>
						        <td class="labelStyle" nowrap="nowrap" width="27%"><s:property value="top.displayName"></s:property></td>
						        <td class="label">
						        <s:set value="%{paymentDefinition.getPaymentVariableLevel(#section.name,top.id)}" name="paymentVariableLevel" />
						        	<s:hidden name="paymentDefinition.paymentSections[%{#attr['selectedCount']}].paymentVariableLevels[%{#variableStatus.index}]"
						        	value="%{#paymentVariableLevel.id}"></s:hidden>
						        	<s:textfield title="modifier labels(Enter numeric values)" name="paymentDefinition.paymentSections[%{#attr['selectedCount']}].paymentVariableLevels[%{#variableStatus.index}].level"
						        	value="%{#paymentVariableLevel.level}"></s:textfield>
						        	<s:hidden name="paymentDefinition.paymentSections[%{#attr['selectedCount']}].paymentVariableLevels[%{#variableStatus.index}].paymentVariable"
						        		value="%{#paymentVariable.id}"></s:hidden>
								</td>
					        </tr>
					        </s:iterator>
					        <s:if test="getPaymentVaribles(name).size == 0">
					        <tr><td class="label" colspan="2" >
					        <s:text name="message.managePayment.noPaymentVariableDefined"/>
					        </td>
					        </tr>
					        </s:if>
						</table>
					</div>
		        </td></tr>
        	</s:if>
            <s:else>
        		<tr>
		          <td class="mainTitle" width="3%" align="center">
		          <input type="checkbox" name="paymentDefinition.paymentSections[<s:property value="%{#sectionStatus.index}"/>].section" value="<s:property value="%{#section.id}"></s:property>"
		          <s:if test="paymentDefinition.doesCoverSection(name)">checked="checked"</s:if>
                  <s:if test="%{!isSectionConfigured(name)}">
                      disabled="disabled"
                  </s:if>/></td>
		          <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS">
			          <td id="tdsection<s:property value="%{#sectionStatus.index}"/>" class="sectionOpenCPD" onclick="expandSection('section<s:property value="%{#sectionStatus.index}"/>');"><s:text name="%{getI18NMessageKey(name)}"/></td>
		          </s:if>
		          <s:else>
		          	<td id="tdsection<s:property value="%{#sectionStatus.index}"/>" class="sectionCloseCPD" onclick="expandSection('section<s:property value="%{#sectionStatus.index}"/>');"><s:text name="%{getI18NMessageKey(name)}"/></td>
		          </s:else>
		        </tr>
		        <tr><td colspan="2" width="100%">
					<div id="section<s:property value="%{#sectionStatus.index}"/>" <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">style="display:block"</s:if><s:else>style="display:none"</s:else>>
			        	<table width="100%" border="0" cellspacing="0" cellpadding="0">
					        <s:iterator value="getPaymentVaribles(name)" id="paymentVariable" status="variableStatus">
					        <tr>
						        <td class="labelStyle" nowrap="nowrap" width="20%"><s:property value="top.displayName"></s:property></td>
						        <td class="label">
						        	<s:textfield title="modifier labels(Enter numeric values)" name="paymentDefinition.paymentSections[%{#sectionStatus.index}].paymentVariableLevels[%{#variableStatus.index}].level"
						        	value="%{paymentDefinition.getLevelForSectionVariable(#section.name,top.id)}"></s:textfield>
						        	<s:hidden name="paymentDefinition.paymentSections[%{#sectionStatus.index}].paymentVariableLevels[%{#variableStatus.index}].paymentVariable"
						        		value="%{#paymentVariable.id}"></s:hidden>
								</td>
					        </tr>
					        </s:iterator>
					        <s:if test="getPaymentVaribles(name).size == 0">
					        <tr><td class="label" colspan="2" >
					        <s:text name="message.managePayment.noPaymentVariableDefined"/>
					        </td>
					        </tr>
					        </s:if>
						</table>
					</div>
		        </td></tr>
		        <s:set name="notSelectedCount" value="#attr['notSelectedCount']+1" scope="page"></s:set>
        	</s:else>

		    </table>
		    </div>
			<table width="100%" cellpadding="0" cellspacing="0" border="0">
		    <tr>
          <td nowrap="nowrap" height="5"></td>
        </tr>
		</table>
        </s:iterator>

     </div>
    <div class="buttonWrapperPrimary">
     	<s:submit cssClass="buttonGeneric" value="%{getText('button.common.continue')}" />
	 </div>
   </div>
</s:form>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    function expandSection(section) {
        var partStyle = document.getElementById(section).style.display;
        if (partStyle == "none") {
            document.getElementById(section).style.display = "block";
            document.getElementById("td" + section).className = "sectionOpenCPD";
        } else {
            document.getElementById(section).style.display = "none";
            document.getElementById("td" + section).className = "sectionCloseCPD";
        }
    }
    function uncheckSections(partsSection) {
        document.getElementById(partsSection).value = "";
    }
    function showPolicy() {
        dojo.html.hide(dojo.byId("policyCategory"));
        dojo.html.show(dojo.byId("appPolicy"));
        dojo.byId("isAppPolicySelected").value = "false";
    }

    function showPolicyList() {
        dojo.html.show(dojo.byId("policyCategory"));
        dojo.html.hide(dojo.byId("appPolicy"));
        dojo.byId("isAppPolicySelected").value = "true";
    }
</script>

</u:body>
<authz:ifPermitted resource="warrantyAdminConfigureClaimPaymentDefinitionReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>