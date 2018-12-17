/**
 * @author : janmejay.singh
 */
dojo.declare("tavant.twms.treeTable.AbstractParent", null, {

    _childRows : null,
    _controller : null,//Controller(Overall tree controller)

    _isParent : true,

    constructor : function(/*Object (Controller)*/ controller) {
        this._childRows = new Array();
        this._controller = controller;
    },

    addChild : function(/*AbstractRow object*/ row) {
        this._childRows.push(row);
        this._addChildToDomGraph(row);
    },

    onAddButtonClick : function(/*String (node type)*/ nodeType) {
        var stub = this._controller.getEmptyStub(nodeType);
        this._performModelAdd(stub);
        this._controller.addNewChildUnder(stub, this);
    },

    removeChild : function(/*AbstractRow object*/ row) {
        for(var i in this._childRows) {
            if(this._childRows[i].getId() === row.getId()) {
                this._performModelDrop(row._nodeObject);
                row.destroy();
                this._childRows.splice(i, 1);
            }
        }
    },
        
    removeAllChildRows : function() {
    	if(this._childRows) {
	    	for(var i in this._childRows) {
	            this._performModelDrop(this._childRows[i]._nodeObject);
	            this._childRows[i].destroy();
	        }
	    	this._childRows=new Array();
    	}
    },

    _performModelAdd : function(/*JSON stub*/ nodeObject) {
        //doing nothing.(child shd override)
    },

    _performModelDrop : function(/*JSON stub*/ nodeObject) {
        //doing nothing.(child shd override)
    },

    /**
     * The class extending this should override this method, to do some positioning stuff and add the node at the right place.
     */
    _addChildToDomGraph : function(/*AbstractRow object*/ row) {
        //doing nothing here
    },

    getDepth : function() {
        return 0;
    },

    _areChildrenValid : function() {
        for(var i in this._childRows) {
            if(!this._childRows[i].isValid()) {
                return false;
            }
        }
        return true;
    }
});

dojo.declare("tavant.twms.treeTable.AbstractParentRow",
        [tavant.twms.treeTable.AbstractParent, tavant.twms.treeTable.AbstractRow], {

    _foldButton : null,
    _unfoldButton : null,
    _dummyFoldButton : null,

    _folded : false,

    _isParent : true,

    /**
     * @see parent's constructor.
     */
    constructor : function(controller, nodeObject, parentRow, template, depth) {
        this._setupFoldUnfoldButtonRef();
        this._bindParentRowEvents();
        this._postCreationCallback();
        this.refresh();
    },

    addChild : function(/*AbstractRow object*/ row) {
        tavant.twms.treeTable.AbstractParentRow.superclass.addChild.call(this, row);
        this.refresh();
    },

    removeChild : function(/*AbstractRow object*/ row) {
        tavant.twms.treeTable.AbstractParentRow.superclass.removeChild.call(this, row);
        this.refresh();
    },

    _bindParentRowEvents : function() {
        dojo.connect(this._foldButton, "onclick", this, "fold");
        dojo.connect(this._unfoldButton, "onclick", this, "unfold");
    },

    _isFoldable : function() {
        return this._childRows.length > 0;
    },

    refresh : function() {
        tavant.twms.treeTable.AbstractRow.prototype.refresh.apply(this, []);
        if(this._isFoldable()) {
            this._setFoldButtonVisibility(!this._folded);
            dojo.html.hide(this._dummyFoldButton);
        } else {
            dojo.html.hide(this._foldButton);
            dojo.html.hide(this._unfoldButton);
            dojo.html.show(this._dummyFoldButton);
        }
    },

    _setupFoldUnfoldButtonRef : function() {
        this._foldButton = getElementByClass("foldButton", this.getDomNode());
        this._unfoldButton = getElementByClass("unfoldButton", this.getDomNode());
        this._dummyFoldButton = getElementByClass("dummyFoldButton", this.getDomNode());
    },

    /**
     * The class extending this should override this method, to do some positioning stuff and add the row to the domGraph.
     */
    _addChildToDomGraph : function(/*AbstractRow object*/ row) {
        dojo.dom.insertAfter(row.getDomNode(), this.getDomNode());
    },

    fold : function(event) {
        if(event) event.stopPropagation();
        this._setFoldButtonVisibility(false);
        if(!this._folded) {
            this._hideChildren();
            this._folded = true;
        }
    },

    unfold : function(event) {
        if(event) event.stopPropagation();
        this._setFoldButtonVisibility(true);
        if(this._folded) {
            this._showChildren();
            this._folded = false;
        }
    },

    show : function() {
        tavant.twms.treeTable.AbstractRow.prototype.show.apply(this, []);
        if(!this._folded) {
            this._showChildren();
        }
    },

    hide : function() {
        if(!this._folded) {
            this._hideChildren();
        }
        tavant.twms.treeTable.AbstractRow.prototype.hide.apply(this, []);
    },

    _showChildren : function() {
        for(var i in this._childRows) {
            this._childRows[i].show();
        }
    },

    _hideChildren : function() {
        for(var i in this._childRows) {
            this._childRows[i].hide();
        }
    },

    _setFoldButtonVisibility : function(/*boolean*/ show) {
        if(show) {
            dojo.html.show(this._foldButton);
            dojo.html.hide(this._unfoldButton);
        } else {
            dojo.html.show(this._unfoldButton);
            dojo.html.hide(this._foldButton);
        }
    },

    isValid : function() {
        return tavant.twms.treeTable.AbstractRow.prototype.isValid.apply(this, []) && this._areChildrenValid();
    }
});
