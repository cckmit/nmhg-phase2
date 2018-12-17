function adjustPriorityAfterDragDrop(/*string / domNode*/ formNode, /*string/domNode*/ referenceNode) {
    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId(formNode), "onclick", function() {
            var priorityIndex = 1;
            var refDomNode = dojo.byId(referenceNode);
            dojo.query(".container input[type=hidden][id^=ruleGroupPriority]", refDomNode).forEach(function(node) {
                node.value = priorityIndex++;
            });
        });
    });
}