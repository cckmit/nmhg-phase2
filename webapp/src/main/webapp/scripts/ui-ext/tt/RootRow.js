/**
 * @author : janmejay.singh
 */
dojo.declare("tavant.twms.treeTable.RootRow", [tavant.twms.treeTable.AbstractParent, tavant.twms.treeTable.DomResolvable], {

    _tBody : null,
    _tHead : null,
    _nodeObject : null,
    _nodeAgent : null,

    /**
     * @see parent's constructor.
     */
    constructor : function(controller, /*domNode table*/ table, /*map (t:jsVar output)*/ headContent,
        /*Node Agent*/ nodeAgent) {
        this._tBody = dojo.query("tBody", table)[0];
        this._tHead = dojo.query("tHead", table)[0];
        this._nodeAgent = nodeAgent;
        this._render(headContent);
    },

    setRootNode : function(/*JSON tree root*/ rootNode) {
    	this.removeAllChildRows();
        this._nodeObject = rootNode;
        dojo.dom.removeChildren(this._tBody, /*destroy*/true);
        this._renderTree(this._nodeObject, this);
    },

    _render : function(/*map (t:jsVar output)*/ headContent) {
        var headDomElements = dojo.html.createNodesFromText(headContent.markup, true);
        this._tHead.appendChild(headDomElements);
        with(this) {
            eval(headContent.script);
        }
    },

    _addChildToDomGraph : function(row) {
        if(row.getDomNode())
            this._tBody.appendChild(row.getDomNode());
    },

    getDomNode : function() {
        return this._tHead;
    },

    getSerializedTree : function() {
        return dojo.toJson(this._nodeObject);
    },

    _renderTree : function(/*JSON object*/ tree, /*RootRow/AbstractParentRow object*/ parentRow) {
        var childCollections = this._nodeAgent.getChildCollections(tree);
        for(var i in childCollections) {
            for(var j in childCollections[i]) {
                var row = this._nodeAgent.getRowInstance(childCollections[i][j], parentRow);
                this._renderTree(childCollections[i][j], row);
            }
        }
    },

    isValid : function() {
        return this._areChildrenValid();
    }
});
