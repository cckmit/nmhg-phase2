<script type="text/javascript">
<#if (parameters.tagWasUsedBefore == false)>
 dojo.require("dijit.form.DateTextBox");
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
 dojo.addOnLoad(function() {
  ${parameters.id?html}_domNode = document.getElementsByName("${parameters.name?default("")?html}")[0];
  ${parameters.id?html}_widget = dijit.byId("${parameters.id?html}");
 <#if ((parameters.onchange?exists || parameters.publishOnChange?exists) && !(parameters.readOnly?exists && parameters.readOnly == true))>
  ${parameters.id?html}_record = ${parameters.id?html}_domNode.value;
  bindOnChange(${parameters.id?html}_domNode, ${parameters.id?html}_compairer, new Array("timebased"));
 </#if>
  repeatAt500MS(function() {//HACK: makes sure... that date displayed and date to be submitted are in sync.
  	${parameters.id?html}_widget.onInputChange();
  }); 
 });
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
 ${parameters.nameValue}
</#if>
</span>
<#else>
<input type="text"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
 dojoType="dijit.form.DateTextBox"<#rt/>
 containerToggle="fade" containerToggleDuration="400"<#rt/>
 saveFormat="MM/dd/yyyy"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.get("size")?exists>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.maxLength?exists>
 maxlength="${parameters.maxLength?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="${parameters.nameValue}"<#rt/>
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
/>
</#if>