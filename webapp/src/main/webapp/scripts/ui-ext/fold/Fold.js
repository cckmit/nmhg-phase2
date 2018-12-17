/**
 * @author : janmejay.singh
 */
dojo.declare("tavant.twms.FoldManager", null, {
    hidden : false,
    foldables : null,/*will hold an array of domNodes*/
    constructor : function(/*domNode*/root,/* string */ foldableClass,/*boolean*/ shownInitially) {
        this.foldables = dojo.query("."+foldableClass, root.parentNode);
        if(!shownInitially) {
            this.hideAll();
        }
        dojo.connect(root, "onclick", dojo.hitch(this, this.toggleVisibility));
    },
    hideAll : function() {
        dojo.forEach(this.foldables, function(node) {
            dojo.fadeOut({node: node,
                          duration: 0,
                          easing: function() {node.style.display = "none"}}).play();
        });
        this.hidden = true;
    },
    showAll : function() {
        dojo.forEach(this.foldables, function(node) {
            dojo.fadeOut({node: node,
                          duration: 0,
                          easing: function() {node.style.display = ""}}).play();
        });
        this.hidden = false;
    },
    toggleVisibility : function() {
        this.hidden ? this.showAll() : this.hideAll();
    }
});