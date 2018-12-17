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
	    <s:form action="save_LOA_Scheme">
		    <div class="policy_section_div">
				<s:form action="">
					<table class="grid" width="100%"  border="0" cellspacing="0" cellpadding="0" >
				   		 <tr height="5"/>
			             <div id="loa_scheme_header" class="section_header" >
			                 <s:text name="title.loa.scheme.details"/>
			             </div>  
			              <tr>
					            <td colspan="2" nowrap="nowrap" class="labelStyle">
					                <s:text name="label.manageLOAScheme.loaSchemeCode"/>
					            </td>
					            <td width="30%" class="labelNormal">
					                <s:textfield name="limitOfAuthorityScheme.code" cssClass="txtField" id="loaSchemeCode"/>
					            </td>
					            <td width="70%"></td>
					        </tr>
					        <tr>
					            <td colspan="2" nowrap="nowrap" class="labelStyle">
					                <s:text name="label.manageLOAScheme.loaSchemeName"/>
					            </td>
					            <td width="30%" class="labelNormal">
					                <s:textfield name="limitOfAuthorityScheme.name" cssClass="txtField" id="loaSchemeName"/>
					            </td>
					            <td width="30%"></td>
					        </tr>
					        <tr>
					            <td colspan="2" nowrap="nowrap" class="labelStyle">
					                <s:text name="label.manageLOAScheme.loaSchemeDescription"/>
					            </td>
					
					            <td width="30%" class="labelNormal">
					                <s:textfield name="limitOfAuthorityScheme.description" cssClass="txtField" id="loaSchemeDesc"/>
					            </td>
					        </tr>
					        <tr height="9"><td></td></tr> 
				      </table>  
				      <u:repeatTable id="loa_scheme_level_table" cssClass="grid borderForTable" width="97%">
						    <thead>
						       <tr class="row_head">
					            <th width="25%"><s:text name="label.manageLOAScheme.level"/></th>
					            <th width="25%"><s:text name="label.manageLOAScheme.levelName"/></th>
					            <th width="21%"><s:text name="label.manageLOAScheme.login"/></th>
					            <th width="17%"><s:text name="label.manageLOAScheme.approvalLimit"/></th>
					             <th width="14%">  <div align="center">  
					             <u:repeatAdd id="loa_levels_adder">   
					                 <div class="repeat_add"/>               
					                  </div>  
					               </div>  </u:repeatAdd>    
					             </th>          
 					          </tr>  
						    </thead>
						  
						    
						   
				 
	                        <u:repeatTemplate id="loa_levels_body"  value="limitOfAuthorityScheme.loaLevels"  >
	                          <tr index="#index">
	                            <td><s:textfield name="limitOfAuthorityScheme.loaLevels[#index].loaLevel" cssClass="txtField" id="loa_levels_body_#index"/></td>
	                            <td><s:textfield name="limitOfAuthorityScheme.loaLevels[#index].name" cssClass="txtField" id="loaSchemeLevelName"/></td>
					            <td>
					                <sd:autocompleter id='name_#index' href='list_loa_scheme_users.action' name='limitOfAuthorityScheme.loaLevels[#index].loaUser' keyName='limitOfAuthorityScheme.loaLevels[#index].loaUser' value='%{limitOfAuthorityScheme.loaLevels[#index].loaUser.name}' key='%{limitOfAuthorityScheme.loaLevels[#index].loaUser.id}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' delay='500' /> 
			                   
			                    
			                    
	                         	<td valign="top">
				            	<div class="limit">
				            	  
				            	  <s:iterator value="currencyList" status="currIter">
									<t:money id="currIter_#index_%{#currIter.index}" name="limitOfAuthorityScheme.loaLevels[#index].approvalLimits[%{#currIter.index}]"
										value="%{approvalLimits[#currIter.index]}" size="10" defaultSymbol="%{currencyCode}"/>
									 </s:iterator> 
									    
				              	</div>
					            </td>  </td> 
									<td valign="top" align="center"><u:repeatDelete id="loa_levels_deleter_#index">
					                <div class="repeat_del"/>
					               
					            </td>              
	                         </tr></u:repeatDelete>
	                        </u:repeatTemplate> 
	                     </u:repeatTable>
	                       <div class="spacer10"></div>  
	                     <div id="submit" align="center">
	                        <input id="submit_btn" class="buttonGeneric" type="submit"
				                value="<s:text name='button.common.save'/>" />
		                    
		                    <input id="cancel_btn" class="buttonGeneric" type="button" 
		                       value="<s:text name='button.common.cancel'/>"
				               onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />       
	                    </div>
	                    <div class="spacer10"></div>
				</s:form>
			</div>
		</s:form>		
	</u:body>
</html>
