<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="label.uom.mapping"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%;overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="margin:5px;height:98%">
		<u:actionResults />
		<s:form name="uomForm">	
		<div style="background:#F3FBFE;border:1px solid #EFEBF7">		
			<div class="policy_section_heading"><s:text name="label.uom.updatemapping"/> </div>
			
			<table border="0" cellspacing="0" cellpadding="0" class="grid">
					<tr>						
						<td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.uom.baseUom"/> : </td>
						<td><s:property value="uomMappings.baseUom.type" />						 
						 	<s:hidden name="uomMappings" value="%{uomMappings.id}" />
						</td>					
					</tr>
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.uom.mappedUom"/> : </td>							
						<td ><s:property value="uomMappings.mappedUom"/></td>
					</tr>
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.uom.mappingFraction"/> : </td>							
						<td ><s:textfield name ="uomMappings.mappingFraction" value="%{uomMappings.mappingFraction}" /></td>
				    </tr>				
			</table>
		</div>
		<div id="seperator"></div>
		<div style="background:#F3FBFE;border:1px solid #EFEBF7">
		    <div class="policy_section_heading"><s:text name="label.uom.I18NUomMappings"/> </div>
					
			<table width="50%" border="0" cellspacing="0" cellpadding="0" class="grid">			
				<s:iterator value="locales" status="iter1">
					<tr>
				    	<td class="labelStyle" nowrap="nowrap" width="20%">UOM Display Value in <s:property value="description"/>:
					      <s:hidden name="uomMappings.i18nUomMappings[%{#iter1.index}].locale" value="%{locale}" /> 
					    </td>
        			     <td>
        			     <s:set name="i18NlocaleValue" value=""/>
							<s:iterator value="uomMappings.i18nUomMappings" >
								<s:if test='locales[#iter1.index].locale.equals(locale)' >
									<s:set name="i18NlocaleValue" value="mappedUom" />
								</s:if>							
						</s:iterator>
						  <s:textfield value="%{i18NlocaleValue}" name="uomMappings.i18nUomMappings[%{#iter1.index}].mappedUom"/>
						</td>			
					</tr>
				</s:iterator>									  
			</table>
			
			</div>


			<div align="center" style="margin:10px 0px 0px 0px;">
				<s:submit value="Delete" action="deleteUomMapping" cssClass="buttonGeneric"/> 
				<s:submit value="Update" action="updateUomMapping" cssClass="buttonGeneric"/> 	    
			
			</div>
			
								  
			
		</s:form>			
		
		
	</div>
    </div>
</u:body>
</html>