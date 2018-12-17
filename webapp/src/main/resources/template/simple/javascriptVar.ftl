<#if parameters.ajaxMode == false>
<script type="text/javascript">
var ${parameters.varName} = </#if>{
 markup : "${parameters.markup}",
 script : "${parameters.script}"
}<#if parameters.ajaxMode == false>;
</script>
</#if>