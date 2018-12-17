<script type="text/javascript">
<#if (parameters.tagWasUsedBefore == false)>
 dojo.require("dijit.form.ComboBox");
</#if>
<#if (parameters.autoSuggest?exists && (parameters.autoSuggest == true))>
<#assign tagType="select"/>
<#include "/${parameters.templateDir}/simple/twms_autoSuggest.ftl" /><#--auto suggest -->
</#if>
<#if ((parameters.onchange?exists || parameters.publishOnChange?exists) && !(parameters.readOnly?exists && parameters.readOnly == true))>
 var ${parameters.id?html}_record = "";
 function ${parameters.id?html}_compairer(event) {
  if(${parameters.id?html}_record != event.target.value) {
   ${parameters.id?html}_record = event.target.value;
   <#if parameters.onchange?exists>
    ${parameters.onchange?html}(event);
   </#if>
   <#if parameters.publishOnChange?exists>
    event.id = "${parameters.id?html}";
    dojo.publish("${parameters.publishOnChange}", [event]);
   </#if>
  }
 }
</#if>
<#include "/${parameters.templateDir}/simple/twms_combobox_hacks.ftl" />
</script>
<#if (parameters.readOnly?exists && parameters.readOnly == true)>
<span <#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
<#if parameters.readOnlyCssClass?exists>
 class="${parameters.readOnlyCssClass?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
>
<@s.iterator value="parameters.list" id="option">
 <#assign itemKey = stack.findValue(parameters.listKey)/>
 <#assign itemValue = stack.findString(parameters.listValue)/>
 <#if (parameters.nameValue == itemKey)>${itemValue?html}</#if>
</@s.iterator>
</span>
<#else>
<select dojoType="dijit.form.ComboBox"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.buttonImageUri?exists>
 buttonSrc="${parameters.buttonImageUri?html}"<#rt/>
</#if>
 maxListLength="${parameters.maxListLength?html}"<#rt/>
<#if (parameters.autoSuggest?exists && (parameters.autoSuggest == true))><#--auto suggest enabled-->
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?html}"<#rt/>
</#if>
 dataUrl="<@s.property value="parameters.url" escape="false"/>"<#rt/>
 searchDelay="${parameters.searchDelay?html}"<#rt/>
 dataProviderClass="twms.tavant.AutoSuggestTagDataProvider"<#rt/>
/>
<#else>
 autocomplete="false"<#rt/>
>
<@s.iterator value="parameters.list" id="option">
 <#assign itemKey = stack.findValue(parameters.listKey)/>
 <#assign itemKeyStr = itemKey.toString() />
 <#assign itemValue = stack.findString(parameters.listValue)/>
 <option value="${itemKeyStr?html}"<#rt/>
  <#if (parameters.nameValue == itemKey)> 
   selected="selected"<#rt/>
  </#if>
  >${itemValue?html}</option><#lt/>
</@s.iterator>
</select>
</#if>
</#if>