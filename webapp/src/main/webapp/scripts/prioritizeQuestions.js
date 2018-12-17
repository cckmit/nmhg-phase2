function adjustOrderAfterDragDrop(/*string / domNode*/ formNode, /*string/domNode*/ referenceNode,/*String / index*/ sectionIndex) {
    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId(formNode), "onclick", function() {
            var priorityIndex = 1;
            var refDomNode = dojo.byId(referenceNode+sectionIndex);
            var referenceId = "questionOrder_"+sectionIndex;
            dojo.query(".container input[type=hidden][id^="+referenceId+"]", refDomNode).forEach(function(node) {
                node.value = priorityIndex++;
            });
            dojo.byId("finalSection_"+sectionIndex).value=priorityIndex-1;
        });
    });
}