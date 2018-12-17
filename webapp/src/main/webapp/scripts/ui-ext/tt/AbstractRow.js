/**
 * @author : janmejay.singh
 */
dojo.declare("tavant.twms.treeTable.AbstractRow", [tavant.twms.treeTable.DomResolvable], {

    _id : 0,

    _domNode : null,//domNode TR
    _nodeObject : null,//JSON node
    _parentRow : null,//row object
    _controller : null,//Controller(Overall tree controller)
    _depth : 0,

    _validatorChain : null,//holds a NON SHORT-CIRCUITED validator chain.(to be polulated by the class entending this).

    STATIC : {
        INDEX : 0
    },

    _isValid : false,

    _isParent : false,

    constructor : function(/*Object (Controller)*/ controller, /*JSON node*/ nodeObject,
                           /*Row Object(AbstractParentRow/RootRow)*/ parentRow, /*template(t:jsVar output)*/ template,
                           /*int (depth)*/ depth) {
        this._id = this.STATIC.INDEX++;
        this._nodeObject = nodeObject;
        this._parentRow = parentRow;
        this._controller = controller;
        this._depth = depth;
        if (this.__isValidConstructorCall()) {
            this._buildRendering(template.markup, template.script);
        }
        this._validatorChain = new twms.utility.ValidatorChain(false);
        this._bindEvents();
        if (this.__isValidConstructorCall()) {
            this._controller.setNodeObjectIdentifierKey(this._nodeObject);
            this._parentRow.addChild(this);
        }
        if(!this._isParent) {
            this._postCreationCallback();
            this.refresh();
        }
    },

    /**
     * HACK : This function will return true, when the call to the constructor is a valid one(which means it is not the one... that is
     * used by dojo... to mimic inheritance.
     */
    __isValidConstructorCall : function() {
        return (this._controller != null);
    },

    addValidator : function(/*twms.utility.Validatable's instance*/ validatorElem) {
        this._validatorChain.add(validatorElem);
    },

    dropValidator : function(/*twms.utility.Validatable's instance*/ validatorElem) {
        this._validatorChain.drop(validatorElem);
    },

    /**
     * This class extending this can override this function and use it to lookup and hold domNode references....
     * Will probably make sence to guys implementing editable trees.
     */
    _postCreationCallback : function() {
        //DOING NOTHING HERE.
    },

    /**
     * This function must be overridden by the class extending this,
     * if it wants to bind events except the addition and deletion of rows.
     */
    _bindEvents : function() {
        //doing nothing here
    },

    getId : function() {
        return this._id;
    },

    _buildRendering : function(/*String*/ markup, /*String*/ script) {
        var substitutionMap = this._getSubstitutionMap();
        var rowMarkup = dojo.string.substitute(markup, substitutionMap);
        var rowScript = dojo.string.substitute(script, substitutionMap);
        this._domNode = dojo.html.createNodesFromText(rowMarkup, true);
        this._controller.indent(this);
        with(this) {
            eval(rowScript);
        }
    },

    /**
     * @returns substitution map
     * to be overridden
     */
    _getSubstitutionMap : function() {
        return {};
    },

    getDepth : function() {
        return this._depth;
    },

    getDomNode : function() {
        return this._domNode;
    },

    isValid : function() {
        return this._isValid;
    },

    show : function() {
        dojo.html.show(this._domNode);
    },

    hide : function() {
        dojo.html.hide(this._domNode);
    },

    getNodeObjectKey : function() {
        return this._nodeObject._treeTableKey;
    },

    onDeleteButtonClick : function() {
        this._parentRow.removeChild(this);
        dojo.dom.destroyNode(this._domNode);
    },

    /**
     * The class extending this can override this method to ensure all the model changes are refreshed to the ui.
     * the method can be fired from right places to make sure it keeps data shown refreshed.
     */
    refresh : function() {
        this.validate();
    },

    validate : function() {
        var validationErrors = this._validatorChain.validate(this);
        if (validationErrors.length > 0) {
            this._handleValidationErrors(validationErrors);
            return false;
        }
        this._clearValidationErrors();
        return true;
    },

    /**
     * Class extending this shd override this function.
     */
    _handleValidationErrors : function(/*List of validation errors*/ validationErrors) {
        this._isValid = false;
    },

    /**
     * Class extending this shd override this function.
     */
    _clearValidationErrors : function() {
        this._isValid = true;
    },

    destroy: function() {
        // child shud override.
    }
});
