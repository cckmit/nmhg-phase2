<script type="text/javascript">
 dojo.addOnLoad(function() {
  var addButton = getElementHavingClass("tt_addButton, ${parameters.identifierCssClass}");
  dojo.connect(addButton, "onclick", function() {
   onAddButtonClick("${parameters.rowType}");
  });
 });
</script>
<span class="tt_addButton ${parameters.identifierCssClass}">