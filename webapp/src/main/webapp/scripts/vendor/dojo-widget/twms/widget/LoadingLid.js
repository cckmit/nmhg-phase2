dojo.provide("twms.widget.LoadingLid");
dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.declare("twms.widget.LoadingLid", [dijit._Widget, dijit._TemplatedMixin], {
    targetNode: "",
    lidClass: "loadingLid",
    lidIndicationClass: "loadingLidIndication",
    lidMessageClass: "loadingLidMessage",
    compactLidClass: "loadingLidCompact",
    loadingMessage: "Please Wait...",
    templatePath: dojo.moduleUrl("twms", "widget/templates/LoadingLid.html"),

    _compactMode : null,
    _isShowing: null,
    _fadeAnimObj : null,
    _loadingMessageDimensions: null,
    _targetNodeOriginalPositionSetting: null,

    postCreate: function() {
        this.setTargetNode(this.targetNode);

        this._fadeAnimObj = dojo.fadeOut({
            node: this.domNode
        });

        dojo.connect(this._fadeAnimObj, "onEnd", this, function(propValues) {
            this._cleanUpAndQuit();
        });
    },

    setTargetNode: function(/* domNode||string */ targetNode) {
        if(targetNode) {
            this.targetNode = dojo.byId(targetNode);
            this._targetNodeOriginalPositionSetting =
                dojo.style(targetNode, "position");
        }
    },

    setMessage: function(/*string*/ message) {
        this.loadingMessage = this.lidMessageContainer.innerHTML = message;
    },

    show: function() {

        if(this._isShowing) {
            return;
        }

        dojo.style(this.targetNode, "position", "relative");

        this._compactMode = !this._placeLid();

        dojo.html.setDisplay(this.lidIndication, !this._compactMode);
        dojo.html.show(this.domNode);

        if(this._compactMode) {
            dojo.addClass(this.domNode, this.compactLidClass);
        }

        this._isShowing = true;
    },

    dispose: function(/*boolean*/ skipAnimation) {
        if(!this._isShowing) {
            return;
        }

        if(skipAnimation) {
            this._cleanUpAndQuit();
        } else {
            this._fadeAnimObj.play();
        }
    },

    isShowing: function() {
        return this._isShowing;
    },

    _cleanUpAndQuit: function() {
        dojo.style(this.targetNode, "position",
            this._targetNodeOriginalPositionSetting);
        this._targetNodeOriginalPositionSetting = null;
        this.targetNode = null;
        
        this.destroyRecursive();
    },

    // NOT USED CURRENTLY
    _hide : function() {
        var thisNode = this.domNode;
        dojo.body().appendChild(thisNode);

        if(this._compactMode) {
            dojo.removeClass(thisNode, this.compactLidClass);
        }

        dojo.html.hide(thisNode);

        this._isShowing = false;
    },

    _placeLid: function() {
        this.targetNode.appendChild(this.domNode);

        return this._tryPlacingMessage(dojo.contentBox(this.targetNode));
    },

    _tryPlacingMessage: function(coords) {
        // Compute and cache the dimensions of the loading message div. We need this information to figure out whether
        // the entire lid can be fit inside the target node. If not, we degrade to a compact lid which only shows a
        // single small loading icon.
        if(!this._loadingMessageDimensions) {
            this._loadingMessageDimensions = dojo.marginBox(this.lidIndication);
        }

        var loadingMessageDims = this._loadingMessageDimensions;

        var loadingIconX = (coords.w - loadingMessageDims.w) / 2;
        var loadingIconY = (coords.h - loadingMessageDims.h) / 2;

        if(loadingIconX > 0 && loadingIconY > 0) {
            dojo.style(this.lidIndication, {
             "left" : loadingIconX+"px",
             "top" : loadingIconY+"px"
            });
            return true;
        }

        return false;
    }
});