<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript">
function deleteLoaScheme(){
	console.debug("here");
    var form=document.forms[0];
    var url='./delete_LOA_Scheme.action';
	form.action=url;
	form.submit();
}
</script>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<s:head theme="twms"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="base.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style>
.money_symbol{margin:2px; width:40px; float:left; text-align:right;}
div.limit{width:140px; text-align:center;}
div.limit input{width:50px; margin:2px;}
</style>
</head>

<u:body>
  <u:actionResults/>
  <s:form action="update_LOA_Scheme" id="baseForm">
    <s:hidden name="limitOfAuthorityScheme"/>
    <div style="background: #F3FBFE; border: 1px solid #EFEBF7; margin-left:6px; margin-top:6px">
      <div id="loa_scheme_header" class="section_header" >
        <s:text name="title.loa.scheme.details"/>
      </div>
      <table class="grid" width="100%"  border="0" cellspacing="0" cellpadding="0" >
        <tr>
          <td height="19" colspan="2" nowrap="nowrap" class="labelStyle"><s:text name="label.manageLOAScheme.loaSchemeCode"/>
          </td>
          <td width="30%" height="19" class="labelNormal"><s:textfield name="limitOfAuthorityScheme.code" cssClass="txtField" readonly = "true" id="code" value="%{limitOfAuthorityScheme.code}"/>
          </td>
          <td width="70%"></td>
        </tr>
        <tr>
          <td height="19" colspan="2" nowrap="nowrap" class="labelStyle"><s:text name="label.manageLOAScheme.loaSchemeName"/>
          </td>
          <td width="30%" height="19" class="labelNormal"><s:textfield name="limitOfAuthorityScheme.name" cssClass="txtField" readonly = "true" id="loaSchemeName" value="%{limitOfAuthorityScheme.name}"/>
          </td>
          <td width="70%"></td>
        </tr>
        <tr>
          <td height="19" colspan="2" nowrap="nowrap" class="labelStyle"><s:text name="label.manageLOAScheme.loaSchemeDescription"/>
          </td>
          <td width="30%" height="19" class="labelNormal"><s:textfield name="limitOfAuthorityScheme.description" cssClass="txtField"
					                             id="loaSchemeDesc" value="%{limitOfAuthorityScheme.description}"/>
          </td>
        </tr>
      </table>
      <u:repeatTable id="loa_scheme_level_table" cssClass="grid borderForTable" width="97%" cssStyle="margin:5px;">
        <thead>
          <tr class="row_head">
            <th width="25%"><s:text name="label.manageLOAScheme.level"/></th>
            <th width="25%"><s:text name="label.manageLOAScheme.levelName"/></th>
            <th width="21%"><s:text name="label.manageLOAScheme.login"/></th>
            <th width="17%"><s:text name="label.manageLOAScheme.approvalLimit"/></th>
            <th width="14%">
            	<div align="center">
                	<u:repeatAdd id="loa_levels_adder"><div class="repeat_add">
                	</div>
                </u:repeatAdd>
              </div></th>
          </tr>
        </thead>
        <u:repeatTemplate id="loa_levels_body" value="limitOfAuthorityScheme.loaLevels">
          <tr index="#index">
            <td valign="top"><s:hidden name="limitOfAuthorityScheme.loaLevels[#index]"/>
              <s:textfield name="limitOfAuthorityScheme.loaLevels[#index].loaLevel" cssClass="txtField"
					                             id="loa_levels_body_#index" value="%{limitOfAuthorityScheme.loaLevels[#index].loaLevel}"/></td>
            <td valign="top"><s:textfield name="limitOfAuthorityScheme.loaLevels[#index].name" cssClass="txtField"
					                             id="loaSchemeLevelName" value="%{limitOfAuthorityScheme.loaLevels[#index].name}"/>
            </td>
            <td valign="top"><sd:autocompleter id='name_#index' href='list_loa_scheme_users.action' name='limitOfAuthorityScheme.loaLevels[#index].loaUser' keyName='limitOfAuthorityScheme.loaLevels[#index].loaUser' value='%{limitOfAuthorityScheme.loaLevels[#index].loaUser.name}' key='%{limitOfAuthorityScheme.loaLevels[#index].loaUser.id}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' delay='500' />
            </td>
            <td valign="top">
            	<div class="limit">
            	<s:if test="approvalLimits != null">
            	   <s:iterator value="approvalLimits" status="iter" id="approvalLimits">
	                	<t:money id="iter_%{#index}_%{#iter.index}" name="limitOfAuthorityScheme.loaLevels[#index].approvalLimits[%{#iter.index}]" value="%{approvalLimits[#iter.index]}" size="5" defaultSymbol="%{currencyCode}"/>
	              	</s:iterator>
            	</s:if>
            	<s:else>
	            	<s:iterator value="currencyList" status="iter" id="approvalLimit">
	                	<t:money id="iter_%{#index}_%{#iter.index}" name="limitOfAuthorityScheme.loaLevels[#index].approvalLimits[%{#iter.index}]" value="%{approvalLimits[#iter.index]}" size="5" defaultSymbol="%{currencyCode}"/>
	              	</s:iterator>
              	</s:else>
              	</div>
            </td>
            <td valign="top" align="center"><u:repeatDelete id="loa_levels_deleter_#index">
                <div class="repeat_del">
                </div>
              </u:repeatDelete>
            </td>
          </tr>
        </u:repeatTemplate>
      </u:repeatTable>
	  <div class="spacer3"></div>
      <div align="center">
        <input id="submit_btn" class="buttonGeneric" type="submit"
				                value="<s:text name='button.common.save'/>" />
        <input id="cancel_btn" class="buttonGeneric" type="button" 
		                       value="<s:text name='button.common.cancel'/>"
				               onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
	    <input id="delete_btn" class="buttonGeneric" type="button"
				                value="delete" onclick="javascript:deleteLoaScheme();" />
      </div>
	  <div class="spacer7"></div>
    </div>
  </s:form>
</u:body>
<authz:ifPermitted resource="warrantyAdminManageLOASchemesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
