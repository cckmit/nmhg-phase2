<#if (!parameters.readOnly?exists || parameters.readOnly == false)>
<script type="text/javascript">
 dojo.addOnLoad(function() {
  var chkBxListener_${parameters.id?html} = dojo.byId("${parameters.id?html}_twmsCheckBoxListener");
  var ${parameters.id?html} = dojo.byId("${parameters.id?html}");
  dojo.connect(${parameters.id?html}, "onchange", function(event) {
   chkBxListener_${parameters.id?html}.value = ${parameters.id?html}.checked;
  });
 });
<#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
 var ${parameters.id?html}_record = false;
 function ${parameters.id?html}_compairer(event) {
  if(${parameters.id?html}_record != event.target.checked) {
   ${parameters.id?html}_record = event.target.checked;
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
  ${parameters.id?html}_record = ${parameters.id?html}_domNode.checked;
  bindOnChange(${parameters.id?html}_domNode, ${parameters.id?html}_compairer, new Array("onclick", "onkeyup", "timebased")); 
 });
</#if>
</script>
</#if>
<input type="checkbox"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if (parameters.nameValue?exists && parameters.nameValue.equals("true"))>
 checked="true"<#rt/>
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
<#if (parameters.readOnly?exists && parameters.readOnly == true)>
 disabled="true"<#rt/>
</#if>
/>
<input type="hidden"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}_twmsCheckBoxListener"<#rt/>
</#if>
<#if (parameters.nameValue?exists)>
 value="${parameters.nameValue?html}"<#rt/>
</#if>
/>