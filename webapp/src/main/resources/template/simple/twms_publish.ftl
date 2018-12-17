<script type="text/javascript">
 dojo.addOnLoad(function() {
  dojo.subscribe("${parameters.on}", this, function(event) {
   if(event.target.value == "${parameters.ifTargetVal}") {
    dojo.publish("${parameters.publish}", [event]);
   }
  });
 });
</script>