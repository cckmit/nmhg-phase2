<#if (parameters.tagWasUsedBefore == false)>
<script type="text/javascript" src="scripts/ui-ext/tt/Main.js"></script>
<script type="text/javascript" src="scripts/ui-ext/tt/AbstractRow.js"></script>
<script type="text/javascript" src="scripts/ui-ext/tt/AbstractParentRow.js"></script>
<script type="text/javascript" src="scripts/ui-ext/tt/RootRow.js"></script>
</#if>
<table id="${parameters.id}" <#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>>
 <thead <#if parameters.headCssClass?exists>class="${parameters.headCssClass}"</#if>/>
 <tbody <#if parameters.bodyCssClass?exists>class="${parameters.bodyCssClass}"</#if>/>
</table>
<script type="text/javascript">
 dojo.addOnLoad(function() {
  var eventMap = {
   loadOn : "${parameters.loadOn}"
   <#if parameters.serializeOn?exists>
   ,serializeOn : "${parameters.serializeOn}"
   </#if>
   <#if parameters.returnBy?exists>
   ,returnBy : "${parameters.returnBy}"
   </#if>
   <#if parameters.onValidationErrors?exists>
   ,onValidationErrors : "${parameters.onValidationErrors}"
   </#if>
   <#if parameters.onTreeRendered?exists>
   ,onTreeRendered : "${parameters.onTreeRendered}"
   </#if>
  };
  new tavant.twms.treeTable.Controller("${parameters.id}", dojo.byId("${parameters.id}"), ${parameters.nodeAgent}, <#rt/>
      ${parameters.rootRowClass}, eventMap, "${parameters.indentCssClass}", ${parameters.headTemplateVar});
  <#if parameters.publishOnInstantiation?exists>
   publishEvent("${parameters.publishOnInstantiation}");
  </#if>
 });
</script>