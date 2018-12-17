<script type="text/javascript">
<#if (parameters.tagWasUsedBefore == false)>
    dojo.require("dijit.form.NumberSpinner");
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
 dojo.addOnLoad(function() {
  ${parameters.id?html}_domNode = dojo.byId("${parameters.id?html}");
  ${parameters.id?html}_record = ${parameters.id?html}_domNode.value;
  bindOnChange(${parameters.id?html}_domNode, ${parameters.id?html}_compairer, new Array("onclick", "onkeyup", "onfocus", "onblur", "onchange", "timebased")); 
 });
</#if>
</script>
<#if (parameters.readOnly?exists && parameters.readOnly == true)>
<span <#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 widgetId="${parameters.id?html}"<#rt/>
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
<input<#rt/>
 dojoType="dijit.form.NumberSpinner"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 widgetId="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
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
<#if parameters.maxLimit?exists>
 max="${parameters.maxLimit?html}"<#rt/>
</#if>
<#if parameters.minLimit?exists>
 min="${parameters.minLimit?html}"<#rt/>
</#if>
<#if parameters.separator?exists>
 separator="${parameters.separator?html}"<#rt/>
</#if>
/>
</#if>