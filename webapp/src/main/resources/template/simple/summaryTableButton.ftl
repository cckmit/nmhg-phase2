<div class="inboxButtonWrapper" style="float: ${parameters.align}">
 <button dojoType="dijit.form.Button" id="_summaryTable_button_${parameters.id}" <#rt/>
  activeImg="<@s.url includeParams='none' value='/ui-ext/tst/image/inbox_button/twmsActive-' includeParams="none" encode='false'/>" <#rt/>
  inactiveImg="<@s.url includeParams='none' value='/ui-ext/tst/image/inbox_button/twmsButton-' includeParams="none" encode='false'/>" <#rt/>
  pressedImg="<@s.url includeParams='none' value='/ui-ext/tst/image/inbox_button/twmsPressed-' includeParams="none" encode='false'/>" <#rt/>
  disabledImg="<@s.url includeParams='none' value='/ui-ext/tst/image/inbox_button/twmsDisabled-' includeParams="none" encode='false'/>" <#rt/>
  <#if (parameters.disabled?exists && parameters.disabled)>
   disabled="true"<#rt/>
  </#if>>
  <div class="<#rt/>
   <#if parameters.cssClass?exists><#rt/>
    ${parameters.cssClass} <#rt/>
   </#if><#rt/>
   inboxButton">
   <span class="inboxButtonText">
    ${parameters.label}
   </span>
  </div>
 </button>
</div>
<script type="text/javascript">
 dojo.addOnLoad(function() {
  connectSummaryTableButtonCallback("${parameters.summaryTableId}", dijit.byId("_summaryTable_button_${parameters.id}"), ${parameters.onclick}); 
 });
</script>