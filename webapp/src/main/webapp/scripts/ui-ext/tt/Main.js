/**
 * @author : janmejay.singh
 */
dojo.declare("tavant.twms.treeTable.Controller", null, {

    _tBody : null,
    _componentId : "",
    _rootRow : null,
    _nodeAgent : null,
    _keyIndex : 0,

    _unitIndentation : 15,

    _tree : null,//JSON tree

    _indentCssClass : null,

    /*events*/
    _onValidationErrors : null,
    _returnBy : null,
    _serializeOn : null,
    _loadOn : null,
    _onTreeRendered : null,
    
    /**
     * Event map is an assoc array... which has following properties...
     * {
     *   loadOn : "/some/event",
     *   serializeOn : "/some/event",
     *   returnBy : "/some/event",
     *   onValidationErrors : "/some/event"
     * }
     */
    constructor : function(/*String (id)*/ componentId, /*domNode (table)*/ table, /*Node Agent Class*/ nodeAgentClass,
                           /*Root Row Class*/ rootRowClass, /*event map*/ eventMap, /*indentCssClass*/ indentCssClass,
                           /*thead template(map)*/ headContent) {
        this._componentId = componentId;
        this._tBody = dojo.query("tbody", table)[0];
        this._nodeAgent = new nodeAgentClass(this);
        this._onValidationErrors = eventMap.onValidationErrors;
        this._returnBy = eventMap.returnBy;
        this._serializeOn = eventMap.serializeOn;
        this._loadOn = eventMap.loadOn;
        this._onTreeRendered = eventMap.onTreeRendered;
        this._indentCssClass = indentCssClass;
        this._rootRow = new rootRowClass(this, table, headContent, this._nodeAgent);
        dojo.subscribe(this._loadOn, this, "reRenderTree");
        dojo.subscribe(this._serializeOn, this, "serializeAndReturn");
    },

    /**
     * event publisher must put the tree as the message.
     */
    reRenderTree : function(/*loadOn event message(JSON tree)*/ tree) {
        this._rootRow.setRootNode(tree);
        publishEvent(this._onTreeRendered);
    },

    serializeAndReturn : function() {
        if(this._rootRow.isValid()) {
            publishEvent(this._returnBy, this._rootRow.getSerializedTree());
        } else {
            publishEvent(this._onValidationErrors);
        }
    },

    indent : function(/*Object (AbstractRow)*/ row) {
        var indentation = this._unitIndentation*row.getDepth() + "px";

        dojo.html.getElementsByClass(this._indentCssClass, row.getDomNode()).forEach(function(indentable) {
            dojo.style(indentable, "paddingLeft", indentation);
        });
    },

    getNewRowId : function(/*Row object*/ row) {
        return this._componentId + "_" + row.nodeObject._treeTableKey;
    },

    /*Framework's private function : extenders or users should NOT use this.*/
    setNodeObjectIdentifierKey : function(/*JSON node*/ nodeObject) {
        nodeObject._treeTableKey = this._keyIndex++;
    },

    getEmptyStub : function(/*String (node type)*/ nodeType) {
        return this._nodeAgent.getStub(nodeType);
    },

    addNewChildUnder : function(/*JSON node*/ nodeObject, /*AbstractParentRow*/ parentRow) {
        return this._nodeAgent.getRowInstance(nodeObject, parentRow);
    }
});

dojo.declare("tavant.twms.treeTable.DefaultNodeAgent", null, {

    _controller : null,//controller object

    constructor : function(/*Object (controller)*/ controller) {
        this._controller = controller;
    },

    /**
     * Should return a JSON node with some default values, that the row actually expects.
     */
    getStub : function(/*String (row type)*/ rowType) {
        //DOING NOTHING HERE : the class extending this, should override this method.
        return {};
    },

    /**
     * Should return a row instance, for given JSON node and parent row.
     */
    getRowInstance : function(/*JSON node*/ nodeObject, /*AbstractParentRow*/ parentRow) {
        //DOING NOTHING HERE : the class extending this, should override this method.
        return null;
    },

    /**
     * This function should be overridden by the class extending this, and should return the Row type of the given JSON object.
     */
    getRowTypeValue : function(/*JSON node*/ nodeObject) {
        return "";
    },

    /**
     * This function should be overridden by the class extending this,
     * and should return collection or collections of child nodes.
     */
    getChildCollections : function(/*JSON node*/ nodeObject) {
        return [];
    }
});

dojo.declare("tavant.twms.treeTable.DomResolvable", null, {

    constructor : function() {/*doing nothing here*/},

    /**
     * The class extending this, should override this function and make sure the domNode against which
     * all searches have to be made is returned.
     */
    getDomNode : function() {
        return null;
    },

    getElementHavingClass : function(/*String of classes(css classes that the node being searched for shd has)*/ classStr) {
        return getElementByClass(classStr,this.getDomNode());
    }
});