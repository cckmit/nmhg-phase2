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

<s:form action="create_payment_definition.action" theme="twms" validate="true" method="post" id="baseForm">
	<u:actionResults/>
    <s:hidden name="appPolicySelected" id="isAppPolicySelected"></s:hidden>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
         style="width: 99.2%; background:#F3FBFE; border: #EFEBF7 1px solid; margin-left: 5px;margin-top: 5px; padding: 0px; overflow-x: auto">
     <table width="100%" >

        <tr>
          <td colspan="4" nowrap="nowrap" class="admin_section_heading"><s:text name="label.common.conditions"/></td>
        </tr>

        <tr id="policyCategory" style="display:none;" class="labelStyle">
    		<td class="labelStyle" width="20%" nowrap="nowrap">
   				<s:text name="label.managePayment.policyName"/>:<br/>
	   			(<a id="toggleToDealer" href="javascript:void(0)" onclick="showPolicy()"><s:text name="toggle.common.toPolicyLabel"/></a>)
    		</td>
    		<td class="labelNormal" width="80%">
    		    <sd:autocompleter href='list_applicable_policies.action' id='policyText' name='partialPolicyCategory' loadOnTextChange='true' loadMinimumCount='2' showDownArrow='false' autoComplete='false' keyName='paymentDefinition.criteria.policyDefinition' />      			
    		</td>
    	</tr>
    	<tr id="appPolicy">
    		<td class="labelStyle" nowrap="nowrap" width="20%">
   				<s:text name="label.managePayment.policyLabel"/>:<br/>
	   			(<a href="javascript:void(0)" onclick="showPolicyList()"><s:text name="toggle.common.toPolicyName"/></a>)
    		</td>
    		<td class="labelNormal" width="80%">
      			<sd:autocompleter href='list_policy_categories.action' id='categoryText' name='partialPolicyCategory' loadOnTextChange='true' loadMinimumCount='2' showDownArrow='false' autoComplete='false' keyName='paymentPolicyLabel' />
    		</td>
    	</tr>
     	<tr>
			<td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.common.claimType"/>:</td>
			<td class="labelNormal" width="80%">
    			<select id="claimType" name="paymentDefinition.criteria.claimType" style="width:15%;">
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
	</table>
	</div>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
         style="width: 99.2%; background: #F3FBFE; border:#EFEBF7 1px solid; margin: 5px; padding: 0px; overflow-x: auto; overflow-y: hidden; ">
     <table width="100%">

        <tr>
          <td colspan="4" nowrap="nowrap" class="admin_section_heading"><s:text name="label.managePayment.paymentDefinition"/></td>
        </tr>
     </table>
	     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" height="4"></td>
        </tr>
     </table> 
      <table width="43%" border="0">
      <tr>
       <td class="mainTitle"><s:text name="label.costCategory"/></td><td class="mainTitle"><s:text name="label.managePolicy.priority"/></td>
     </tr>
     <tr></tr>
     </table>
        <s:iterator value="allSections" id="section" status="sectionStatus">
           <div class="bgColorBorder">
        	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		        <tr>
		          <td class="mainTitle" width="3%" align="center">
		          <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
		          	<input type="checkbox" 
		          		name="paymentDefinition.paymentSections[<s:property value="%{#sectionStatus.index}"/>].section" 
		          		value="<s:property value="%{#section.id}"/>" disabled="disabled" checked="checked"/>
		          	<s:hidden name="paymentDefinition.paymentSections[%{#sectionStatus.index}].section"
		          		value="%{#section.id}"/>
		          </s:if>
		          <s:else>
		          	<input type="checkbox" name="paymentDefinition.paymentSections[<s:property value="%{#sectionStatus.index}"/>].section" 
		          		value="<s:property value="%{#section.id}"/>"
                              <s:if test="%{!isSectionConfigured(name)}">
                                  disabled="disabled"
                              </s:if>
                    />
                  </s:else>
		          </td>	
		          <s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">		          
			          <td id="tdsection<s:property value="%{#sectionStatus.index}"/>" class="sectionOpenCPD" onclick="expandSection('section<s:property value="%{#sectionStatus.index}"/>');"><s:text name="%{getI18NMessageKey(name)}"/></td>
		          </s:if>
		          <s:else>
		          	<td id="tdsection<s:property value="%{#sectionStatus.index}"/>" class="sectionCloseCPD" onclick="expandSection('section<s:property value="%{#sectionStatus.index}"/>');"><s:text name="%{getI18NMessageKey(name)}"/></td>
		          </s:else>
		        </tr>
		        <tr><td colspan="2" width="100%">
					<div id="section<s:property value="%{#sectionStatus.index}"/>" 
						<s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">style="display:block"</s:if><s:else>style="display:none"</s:else>>
			        	<table width="100%" border="0" cellspacing="0" cellpadding="0">
					        <s:iterator value="getPaymentVaribles(name)" id="paymentVariable" status="variableStatus">
					        <tr>
						        <td class="labelStyle" nowrap="nowrap" width="27%"><s:property value="top.displayName"></s:property></td>
						        <td class="label">
						        	<s:textfield title="modifier labels(Enter numeric values) "
									name="paymentDefinition.paymentSections[%{#sectionStatus.index}].paymentVariableLevels[%{#variableStatus.index}].level"></s:textfield>
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
		    </table>    
		    </div>
		    	     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" height="4"></td>
        </tr>
     </table> 

        </s:iterator>        
     </div>   
		<tr>
    		<td>	   
		     <div class="buttonWrapperPrimary">
			       <s:submit cssClass="buttonGeneric" value="%{getText('button.common.continue')}" />
			 </div>    
		   </td>
		</tr>
	    
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

        function showPolicy() {
            dojo.html.hide(dojo.byId("policyCategory"));
            dojo.html.show(dojo.byId("appPolicy"));
            dojo.byId("isAppPolicySelected").value = "false";
//            dijit.byId("categoryText").textInputNode.value = "";
        }

        function showPolicyList() {
            dojo.html.show(dojo.byId("policyCategory"));
            dojo.html.hide(dojo.byId("appPolicy"));
            dojo.byId("isAppPolicySelected").value = "true";
//            dijit.byId("policyText").textInputNode.value = "";
        }
    </script>
    
<authz:ifPermitted resource="warrantyAdminConfigureClaimPaymentDefinitionReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>

</u:body>
</html>
