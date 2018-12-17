<script type="text/javascript">
 dojo.addOnLoad(function() {
  var dropButton = getElementHavingClass("tt_dropButton, ${parameters.identifierCssClass}");
  dojo.connect(dropButton, "onclick", function() {
   onDeleteButtonClick();
  });
 });
</script>
<span class="tt_dropButton ${parameters.identifierCssClass}">