<#if ((parameters.onchange?exists || parameters.publishOnChange?exists) && !(parameters.readOnly?exists && (parameters.readOnly == true)))>
<script type="text/javascript">
 var ${parameters.id?html}_record = "";
 var ${parameters.id?html}_radio_domNodes = null;
 var ${parameters.id?html}_dummyHiddenField = null;
 function ${parameters.id?html}_compairer(event) {
  var selectedVal = ${parameters.id?html}_findSelectedValue();
  if(selectedVal != ${parameters.id?html}_record) {
   ${parameters.id?html}_record = selectedVal;
   ${parameters.id?html}_dummyHiddenField.value = selectedVal;
   <#if parameters.onchange?exists>
    ${parameters.onchange?html}(event);
   </#if>
   <#if parameters.publishOnChange?exists>
    event.id = "${parameters.id?html}";
    dojo.publish("${parameters.publishOnChange}", [event]);
   </#if>
  } 
 }
 dojo.addOnLoad(function() {
  ${parameters.id?html}_radio_domNodes = document.getElementsByName("${parameters.name?default("")?html}");
  ${parameters.id?html}_record = ${parameters.id?html}_findSelectedValue();
  ${parameters.id?html}_dummyHiddenField = dojo.byId("${parameters.id?html}");
  bindAllOnChange(${parameters.id?html}_radio_domNodes, ${parameters.id?html}_compairer, new Array("onkeyup", "onclick", "timebased"), ${parameters.id?html}_dummyHiddenField); 
 });
 function ${parameters.id?html}_findSelectedValue() {
  for(var i = 0; i < ${parameters.id?html}_radio_domNodes.length; i++) {
   if(${parameters.id?html}_radio_domNodes[i].checked == true) {
    return ${parameters.id?html}_radio_domNodes[i].value;
   }
  }
 }
</script>
</#if>
<#if (parameters.readOnly?exists && parameters.readOnly == true)>
<span <#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.readOnlyCssClass?exists>
 class="${parameters.readOnlyCssClass?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
>
<#if parameters.nameValue?exists>
 <@s.property value="parameters.nameValue"/>
</#if>
</span>
<#else>
<table>
<#assign index=0/>
<@s.iterator value="parameters.list">
	<#assign index=(index + 1)/>
    <#if parameters.listLabel?exists>
        <#assign itemLabel = stack.findValue(parameters.listLabel)/>
    <#else>
        <#assign itemLabel = stack.findValue('top')/>
    </#if>
    <#assign itemLabelStr = itemLabel.toString() />
    <#if parameters.listValue?exists>
        <#assign itemValue = stack.findString(parameters.listValue)/>
    <#else>
        <#assign itemValue = stack.findString('top')/>
    </#if>
<tr><td>
<input type="radio" name="${parameters.name?default("")?html}" id="${parameters.id?html}_${index?html}"<#rt/>
<#if (parameters.nameValue == itemLabelStr)>
 checked="checked"<#rt/>
</#if>
<#if itemLabel?exists>
 value="${itemValue?html}"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
/><#rt/>
</td><td>
<label for="${parameters.id?html}_${index?html}"><#rt/>
    <@s.text name="${itemLabelStr}"/><#rt/>
</label>
</td></tr>
</@s.iterator>
<input type="hidden" id="${parameters.id?html}"<#rt/>
<#if parameters.nameValue?exists>
 value="${parameters.nameValue}"<#rt/>
</#if>
/>
</table>
</#if>