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

<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>

    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="adminPayment.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
		dojo.require("dijit.layout.ContentPane");
    </script>
</head>
<body style="overflow-X: hidden; overflow-Y: auto;">
<s:form action="continue_create_payment_definition.action" method="POST">

	<s:fielderror theme="xhtml"></s:fielderror>
	<s:actionerror theme="xhtml"></s:actionerror>
	
	<s:hidden name="paymentDefinition"> </s:hidden>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
         style="width: 99.2%; background: #F3FBFE; border: #EFEBF7 1px solid; margin-left: 5px; margin-top: 5px;padding: 0px; overflow-x: hidden">
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" class="admin_section_heading"><s:text name="label.common.conditions"/></td>
        </tr>
		<tr>
			<td class="labelStyle" width="20%"><s:text name="label.managePayment.policyCategory"/>:</td>
			<td class="labelNormal">
			<s:if test="appPolicySelected">	
			<s:hidden name="paymentDefinition.criteria.policyDefinition"></s:hidden>			
				<s:if test="paymentDefinition.criteria.policyDefinition.id !=null">
				   <s:property value="%{paymentDefinition.criteria.policyDefinition.code}"></s:property>					
				</s:if>	
				<s:else>
				   <s:text name="label.common.allPolicyTypes"></s:text>	
				</s:else>					
			</s:if>
			<s:else>
				<s:if test="paymentPolicyLabel.isEmpty()">
					<s:text name="label.common.allPolicyTypes"></s:text>		
				</s:if>
				<s:else>
					<s:property value="paymentPolicyLabel"></s:property>
					<s:hidden name="paymentPolicyLabel"></s:hidden>
				</s:else>
			</s:else>
			
			</td>
		</tr>
		<tr>
			<td class="labelStyle" width="20%"><s:text name="label.common.claimType"/>:</td>
			<td class="labelNormal">
			<s:if test="paymentDefinition.criteria.claimType==null">
				<s:text name="label.common.allClaimTypes"></s:text>
			</s:if>
			<s:else>
				<s:property value="%{getText(paymentDefinition.criteria.claimType.displayType)}"></s:property>			
			</s:else>
			<s:if test="paymentDefinition.criteria.claimType!=null">
			<s:hidden name="paymentDefinition.criteria.claimType"></s:hidden>
			</s:if>
			
			<s:else>
			<s:hidden name="paymentDefinition.criteria.claimType" value="null"></s:hidden>
			</s:else>
			</td>
		</tr>
	</table>
	</div>
	
	<jsp:include flush="true" page="paymentDefinitionFormula.jsp"/>
  <div class="buttonWrapperPrimary">
  			<s:if test="paymentDefinition.id ==null">
				<input type="Submit"  value="<s:text name="button.common.save"/>" class="buttonGeneric" />
                <input id="cancel_btn" class="buttonGeneric"
                         type="button" value="<s:text name='button.common.cancel'/>"
                         onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
              </s:if>
  			<s:else>
	  			<input type="Submit"  value="<s:text name="button.common.update"/>" class="buttonGeneric" />
  			</s:else>
	        
</div>
</s:form>
	
</body>
