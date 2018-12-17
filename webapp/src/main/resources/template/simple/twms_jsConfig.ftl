<#if (parameters.tagWasUsedBefore == false)>
<script type="text/javascript">
if(typeof dojo != "undefined") {
	throw "'djConfig' was used after dojo definition. It must be used before including dojo.js/dojo.js.uncompressed.js ...";
}
djConfig = {
<#if parameters.debug?exists>
 isDebug : ${parameters.debug?html},
</#if>
<#if parameters.allowQueryConfig?exists>
 allowQueryConfig : ${parameters.allowQueryConfig?html},
</#if>
<#if parameters.baseScriptUri?exists>
 baseScriptUri : "${parameters.baseScriptUri?html}",
</#if>
<#if parameters.baseRelativePath?exists>
 baseRelativePath : "${parameters.baseRelativePath?html}",
</#if>
<#if parameters.libraryScriptUri?exists>
 libraryScriptUri : "${parameters.libraryScriptUri?html}",
</#if>
<#if parameters.iePreventClobber?exists>
 iePreventClobber : ${parameters.iePreventClobber?html},
</#if>
<#if parameters.ieClobberMinimal?exists>
 ieClobberMinimal : ${parameters.ieClobberMinimal?html},
</#if>
<#if parameters.locale?exists>
 locale : "${parameters.locale?html}",
</#if>
<#if parameters.preventBackButtonFix?exists>
 preventBackButtonFix : ${parameters.preventBackButtonFix?html},
</#if>
<#if parameters.searchIds?exists>
 searchIds : [${parameters.searchIds?html}],
</#if>
<#if parameters.parseWidgets?exists>
 parseWidgets : ${parameters.parseWidgets?html}
</#if>
};
<#--javascript required to get the onchange stuff in tags working)-->
function bindOnChange(domNode/*DomNode*/, compairer/*function that compairs*/, events/*array of events*/) {
 for(var i = 0; i < events.length; i++) {
  if(events[i] != "timebased") {
   dojo.connect(domNode, events[i], compairer);
  } else {
   repeatAt500MS(function() {compairer({target: domNode, type: "timebased"});});
  }
 }
}
/****bindAllOnChange is used for radio buttons****/
function bindAllOnChange(domNodes/*array of DomNodes*/, compairer/*function that compairs*/, events/*array of events*/, /*domNode*/dummyHiddenNode) {
 var timeBasedRequested = false;
 for(var j = 0; j < domNodes.length; j++) {
  for(var i = 0; i < events.length; i++) {
   if(events[i] != "timebased") {
    dojo.connect(domNodes[j], events[i], compairer);
   } else {
    timeBasedRequested = true;
   }
  }
 }
 if(timeBasedRequested) {
  repeatAt500MS(function() {compairer({target: dummyHiddenNode, type: "timebased"});});
 }
}
function repeatAt500MS(functionObj) {
 setTimeout(function() { repeatAt500MS(functionObj); functionObj();}, 500);
}
</script>
</#if>