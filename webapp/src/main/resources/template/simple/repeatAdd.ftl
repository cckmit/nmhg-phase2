<script type="text/javascript">
dojo.addOnLoad(function() {
 var add = dojo.byId("${parameters.id}");
 dojo.connect(add, "onclick", function(event) {
  <#-- this might be inefficient, but its the simplest thing that wud work for now -->
  for (var i = 0; i < __${parameters.repeatTableId}_batchSize; i++) {
   __repeatTable_addRow(event, __${parameters.repeatTableId}_markup, __${parameters.repeatTableId}_script, __${parameters.repeatTableId}_indexOfRepeatTable);
  }
 });
 var table = getExpectedParent(add, "table");
 table.index = __${parameters.repeatTableId}_indexInitial;
 <#--Adding code to bind keyboard actions(utility.js function)-->
 bindRepeatTableKeyboardActions(table, add, "__twms_repeat_delete");
 delete add, table;
});
</script>
<span id="${parameters.id}">