<#include "controlheader.ftl" />
<tr>
<td class="admin_data_table" id="displayName_${parameters.index}">
<@s.property value="${parameters.paramName}.displayName" />
</td>
<span dojoType="dijit.Tooltip" connectId="displayName_${parameters.index}">
    <@s.property value="${parameters.paramName}.description" />
</span>
<td class="admin_data_table"><#rt/>
<#if parameters.type?if_exists = "multiselect" >  
	<@s.select list="${parameters.paramName}.paramOptions" listKey="id" listValue="displayValue" name="paramList[${parameters.index}].dbParameterValues" value="paramList[${parameters.index}].dbParameterValues" multiple="true"/>
<#rt/>	
<#elseif parameters.type?if_exists = "radio" >    
       <input type="radio" name="paramList[${parameters.index}].dbParameterValues"  value="true" <#rt/>
	<#if parameters.val?if_exists = "true" >
	checked="checked" <#rt/>
	</#if>
    /> Yes
   <input type="radio" name="paramList[${parameters.index}].dbParameterValues"  value="false" <#rt/>
    <#if parameters.val?if_exists = "false" >
    checked="checked" <#rt/>
    </#if>  
    /> No    	    
<#elseif parameters.type?if_exists = "textbox" >
    <#if parameters.val?if_exists != "" > 
      <@s.textfield name="paramList[${parameters.index}].dbParameterValues" value="${parameters.val}"/>
    <#else>
       <@s.textfield name="paramList[${parameters.index}].dbParameterValues" />	
    </#if>   
<#elseif parameters.type?if_exists = "select">
	<@s.select list="${parameters.paramName}.paramOptions" listKey="id" listValue="displayValue" name="paramList[${parameters.index}].dbParameterValues" value="paramList[${parameters.index}].dbParameterValues" />
</#if>
<@s.hidden name="paramList[${parameters.index}].id" value="${parameters.paramId}" />
</td>
</tr>

<#include "controlfooter.ftl" />