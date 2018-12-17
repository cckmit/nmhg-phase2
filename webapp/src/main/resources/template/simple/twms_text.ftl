<#if !(parameters.readOnly?exists && parameters.readOnly == true)>
<script type="text/javascript">
<#if (parameters.autoSuggest?exists && (parameters.autoSuggest == true))>
<#if (parameters.tagWasUsedBefore == false)>
 dojo.require("dijit.form.ComboBox");
</#if>
<#assign tagType="text"/>
<#include "/${parameters.templateDir}/simple/twms_autoSuggest.ftl" /><#--auto suggest -->
</#if>
<#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
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
<#if (parameters.autoSuggest?exists && (parameters.autoSuggest == true))>
 <#include "/${parameters.templateDir}/simple/twms_combobox_hacks.ftl" />
<#else>
<#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
 dojo.addOnLoad(function() {
  ${parameters.id?html}_domNode = dojo.byId("${parameters.id?html}");
  ${parameters.id?html}_record = ${parameters.id?html}_domNode.value;
  bindOnChange(${parameters.id?html}_domNode, ${parameters.id?html}_compairer, new Array("onkeyup", "timebased")); 
 });
</#if>
</#if>
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
><#rt/>
<#if parameters.nameValue?exists>
${parameters.nameValue}<#rt/>
</#if>
</span>
<#else>
<input type="text"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if (parameters.autoSuggest?exists && (parameters.autoSuggest == true))><#--auto suggest enabled-->
<#rt/>
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?html}"<#rt/>
</#if>
 dojoType="dijit.form.ComboBox"<#rt/>
 dataUrl="<@s.property value="parameters.url" escape="false"/>"<#rt/>
 searchDelay="${parameters.searchDelay?html}"<#rt/>
 dataProviderClass="twms.tavant.AutoSuggestTagDataProvider"<#rt/>
<#else>
<#if parameters.get("size")?exists>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.maxLength?exists>
 maxlength="${parameters.maxLength?html}"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#rt/>
</#if>
/>
</#if>