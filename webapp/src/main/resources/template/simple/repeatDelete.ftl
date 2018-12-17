<script type="text/javascript">
dojo.addOnLoad(function() {
 var deleteSpan = dojo.byId("${parameters.id}");
 dojo.connect(deleteSpan, "onclick", function(event) {
 var row = getExpectedParent(deleteSpan, "tr");
 var rowIndex = dojo.attr(row,'index');
 var hiddenField = dojo.toDom("<input type='hidden' id='remove_${parameters.id}' value='${parameters.collectionName}[" + rowIndex + "]' name='__remove.${parameters.collectionName}'/>");
 row.parentNode.appendChild(hiddenField);
  requestDeletion(row, "${parameters.collectionName}");
  dojo.dom.removeNode(row);
  delete row;
 });
 delete deleteSpan;
});
</script>
<span id="${parameters.id}" class="__twms_repeat_delete">