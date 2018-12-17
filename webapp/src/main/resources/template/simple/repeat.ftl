<#if (parameters.tagWasUsedBefore == false)>
<script type="text/javascript">
var __rowAddCallbacks = [];
<#rt/>
_undefSafeMixin("twms.repeattable", {
    addOnRowAdd: function(/*function*/ callback) {
        __rowAddCallbacks.push(callback);
    }
});
<#rt/>
function __repeatTable_addRow(event, markup, script, indexParam) {<#rt/>
 var table = getExpectedParent(event.target, "table");<#rt/><#--getExpectedParent() comes frm utility.js-->
 if (table.index == null) {
  table.index = 0;
 } else {
  table.index = ((table.index/1) + 1);
 }
 <#-- to execute javascript that comes with the new markup -->
 var script = __substituteIndex(script, indexParam, table.index);
 if((script != null) && (dojo.string.trim(new String(script)).length > 0)) {
    eval(script);
 }
 var markup = __substituteIndex(markup, indexParam, table.index);
 var row =  dojo.html.createNodesFromText(markup, true);
 dojo.parser.parse(row);
 dojo.query("tbody", table)[0].appendChild(row);
 <#rt/>
 <#-- Invoke onTemplateLoad callbacks -->
 for(var i=0; i < __rowAddCallbacks.length; i++) {
    __rowAddCallbacks[i]();
 }
 <#-- Clear callbacks -->
 __rowAddCallbacks = [];
 <#-- done-->
 delete script, table, row;<#rt/>
}<#rt/>
</script>
</#if>
<table type="repeat" id="${parameters.id?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>
<#if parameters.cellpadding?exists>
 cellpadding="${parameters.cellpadding}"<#rt/>
</#if>
<#if parameters.cellspacing?exists>
 cellspacing="${parameters.cellspacing}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle}"<#rt/>
</#if>
<#if parameters.width?exists>
 width="${parameters.width}"<#rt/>
</#if>
>