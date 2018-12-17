<script type="text/javascript">
dojo.addOnLoad(
 function() {
 var conElemnt= dojo.byId("${parameters.id?html}");
 var labelQ='<@s.text name="${parameters.tabLabel}"/>'.replace(/"/g,"&#34;");
 if(conElemnt){
  dojo.connect(conElemnt, "onmousedown", function(eventOld) {
   var event= cloneEvenIfIe(eventOld);	
    event.label = labelQ;
   event.url = "${parameters.url}";
   <#if parameters.decendentOf?exists>
    event.decendentOf = "${parameters.decendentOf?html}";
   <#elseif parameters.autoPickDecendentOf>
    if(TWMS_UTILITY_JS) {<#-- If this code throws an error, please look at utility.js functions that r used here -->
     var tab = getFrameAttribute("TST_IS_PREVIEW") ? parent.getTabDetailsForIframe() : getTabDetailsForIframe();
     event.decendentOf = getTabHavingId(tab.tabId).title;
    }
   </#if>
   <#if parameters.catagory?exists>
    event.catagory = "${parameters.catagory?html}";
   </#if>
   <#if parameters.helpCategory?exists>
    event.helpCategory = "${parameters.helpCategory?html}";
   </#if>
   <#if parameters.forceNewTab?exists>
    event.forceNewTab = ${parameters.forceNewTab?string?html};
   <#else>
    event.forceNewTab = false;
   </#if>
   top.requestTab(event);
  });
  }
 }
);
</script>
<${parameters.tagType} id="${parameters.id?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>
><#rt/>