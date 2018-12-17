
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<script>
 dojo.require("twms.widget.TitlePane");
 dojo.require("dijit.layout.LayoutContainer");
 
</script>
</head>
<u:actionResults/>
<form  action="updateI18nNonOemDescriptionMsg.action" name="saveForm" id="saveForm" method="POST">
<s:hidden name="campaign.id"></s:hidden>
<s:hidden name="redirectToPage1" value="true"/>
<div class="admin_section_heading">
    	<s:text name="label.campaign.updateI18nNonOemMessage"/>
</div>
<s:hidden name="index"/>
  <table width="50%" cellspacing="0" cellpadding="0" bgcolor="white" style="margin-top:15px">  
	 	<s:iterator value="locales"  status="itr" id="localesItr">
    	   <tr width="50%">
            <td height="3" colspan="3"></td>
          </tr>
          <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="label.common.description"/> <s:property value='description'/>
            </td>
			<td width="70%" height="19" class="labelNormal">
	         	<t:textarea cols="40" rows="3" name="campaign.nonOEMpartsToReplace[%{index}].i18nNonOemPartsDescription[%{#itr.index}].description" />
	         	<s:hidden  name="campaign.nonOEMpartsToReplace[%{index}].i18nNonOemPartsDescription[%{#itr.index}].locale" value="%{locales[#itr.index].locale}" />
           	</td>
            <td width="30%"></td>
          </tr> 
          </s:iterator>	
  	 </table> 
<s:submit cssClass="buttonGeneric"  value="%{getText('label.common.submit')}"/>
    
</form>    
</html>                 