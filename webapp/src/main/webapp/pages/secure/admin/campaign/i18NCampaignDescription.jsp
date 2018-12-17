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

<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="adminPayment.css"/>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    
<script>
 dojo.require("twms.widget.TitlePane");
 dojo.require("dijit.layout.LayoutContainer");
 
</script>
</head>
<form  action="saveCampaignDescription.action" name="saveForm" id="saveForm" method="POST">
<u:actionResults/>
<s:hidden name="campaign.id"></s:hidden>
<s:hidden name="redirectToPage1" value="true"/>
<div class="admin_section_heading">
    	<s:text name="label.campaign.createI18NMessage"/>
</div>

<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor">
        <tr style="padding-top:5px;padding-bottom:5px;">
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="columnTitle.campaign.code"/>
            </td>
            
            <td width="30%" height="19" class="labelNormal" >
                <s:textfield name="campaign.code" cssClass="txtField" id="code" readonly="true"/>
            </td>
            <td width="90%"></td>
        </tr>
        <s:iterator value="locales"  status="itr" id="localesItr">
           <tr>
            <td height="3" colspan="3"></td>
          </tr>
          <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="label.common.description"/> <s:property value='description'/>
            </td>
			<td width="70%" height="19" class="labelNormal">
         		<t:textarea cols="40" rows="3" name="campaign.i18nCampaignTexts[%{#itr.index}].description" 
         			value="%{campaign.getDescription(locales[#itr.index].locale)}" />
         		<s:hidden name="campaign.i18nCampaignTexts[%{#itr.index}].locale"
         			 value="%{locales[#itr.index].locale}"/>
           	</td>
            <td width="30%"></td>
          </tr>
        </s:iterator>
</table>

<s:submit cssClass="buttonGeneric"  value="%{getText('label.common.submit')}"/>
    
</form>    
</html>                 
