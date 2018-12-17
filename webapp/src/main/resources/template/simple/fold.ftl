<#if !parameters.tagWasUsedBefore>
 <script type="text/javascript" src="scripts/ui-ext/fold/Fold.js"></script>
</#if>
<script type="text/javascript">
dojo.addOnLoad(function() {
  new tavant.twms.FoldManager(dojo.byId("${parameters.id}"), "${parameters.foldableClass}", <#if parameters.shownInitially>true<#else>false</#if>);
});
</script>
<${parameters.tagType} id="${parameters.id?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>
><#rt/>
${parameters.label?html}
</${parameters.tagType}>