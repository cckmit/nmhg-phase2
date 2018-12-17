<script type="text/javascript">
 dojo.addOnLoad(function() {
  var __twms_${parameters.id}_target = dojo.byId("${parameters.ofId}");
  dojo.subscribe("${parameters.on}", this, function(event) {
  <#if parameters.ifTargetVal?exists>
   if(event.target.value == "${parameters.ifTargetVal}") {
  </#if>
    __twms_${parameters.id}_target.value = "${parameters.to}";
  <#if parameters.ifTargetVal?exists>
   }
  </#if>
  });
 });
</script>