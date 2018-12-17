dojo.provide("twms.widget.NList");
dojo.require("twms.widget.LoadingLid");

dojo.declare("twms.widget.NList", [dijit._Widget], {
    rowTemplateUrl : null,
    paramsVar: null,
    collectionName: null,
    nextAvailableIndex : 0,
    TBODY_IGNORE_INDICATOR_CLASS : "nListIgnoreLookup",

    _dummyContentPane: null,
    _parentTbody: null,
    _parentContainer: null, // for showing Loading Lid. Can't put it on the table.
    _tableRowDummyContainerMarkupPrefix : "<table><tbody class='",
    _tableRowDummyContainerMarkupSuffix: "</tbody></table>",
    _addOnLoadPattern : /dojo\.addOnLoad\s*\(/g,
    _nListConnects: null,
    _contentPaneLoadConnects: null,

    postCreate: function() {
        this._nListConnects = [];
        this._contentPaneLoadConnects = [];

        this._tableRowDummyContainerMarkupPrefix += this.TBODY_IGNORE_INDICATOR_CLASS + "'>";
        this._findParentTBody();
        var _parentTable = this._parentTbody.parentNode;
        this._parentContainer = getExpectedParent(_parentTable, "div");

        if (!this.paramsVar) {
            this.paramsVar = {};
        }

        this._wireAdditionTrigger(_parentTable.tHead);
        this._wireAllDeletionTriggers(this._parentTbody);
    },

    destroyRecursive : function() {
        if (this._dummyContentPane) {
            this._dummyContentPane.destroyRecursive();
        }

        twms.util.disconnectAll(this._nListConnects);

        this.inherited(arguments);
    },

    _findParentTBody: function() {
        var _tBody = this.domNode;

        for(;;) {
            _tBody = getExpectedParent(_tBody, "tbody");

            if(_tBody == null || !dojo.hasClass(_tBody, this.TBODY_IGNORE_INDICATOR_CLASS)) {
                break;
            } else if(_tBody != null) {
                _tBody = _tBody.parentNode;
            }
        }

        this._parentTbody = _tBody;
    },

    _wireAdditionTrigger: function(/*domNode*/ parentTableHead) {
        var additionTriggerNodes = dojo.query(".nList_add", parentTableHead);

        if (additionTriggerNodes.length > 0) {
            this._nListConnects.push(dojo.connect(additionTriggerNodes[0], "onclick", this, function() {
                if (!dojo.isIE && this._parentContainer) {
                    twms.util.putLidOn(this._parentContainer);
                }

                var paramsVar = this.paramsVar;
                paramsVar.nListIndex = this.nextAvailableIndex;
                paramsVar.nListName = this.collectionName + "[" + this.nextAvailableIndex + "]";

                this.nextAvailableIndex++;
                
                twms.ajax.fireHtmlRequest(this.rowTemplateUrl, this.paramsVar,
                        dojo.hitch(this, this._processTemplate));
            }));
        }
    },

    _processTemplate : function(template) {
        if (!dojo.isIE && this._parentContainer) {
            setTimeout(dojo.hitch(this, function() {twms.util.removeLidFrom(this._parentContainer);}), 500);

        }

        

        var rootNode = this._parseScriptsAndWidgets(template);
        this._parentTbody.appendChild(rootNode);
        this._wireAllDeletionTriggers(rootNode);
    },

    _parseScriptsAndWidgets: function(/*string*/ markup) {
        markup = markup.replace(this._addOnLoadPattern, "_container_._connectOnLoad(");
        if (!this._dummyContentPane) {
            this._createAndInitDummyContentPane();
        }

        var contentPane = this._dummyContentPane;

        twms.util.disconnectAll(this._contentPaneLoadConnects);

        this._contentPaneLoadConnects = [];

        contentPane.setContent(this._tableRowDummyContainerMarkupPrefix + markup +
                               this._tableRowDummyContainerMarkupSuffix);
        return dojo.query("." + this.TBODY_IGNORE_INDICATOR_CLASS + " > tr", contentPane.domNode)[0];
    },

    _createAndInitDummyContentPane: function() {
        var _self = this;
        
        dojo.require("dojox.layout.ContentPane");
        this._dummyContentPane = new dojox.layout.ContentPane({
            scriptHasHooks:true,
            _connectOnLoad: function(/*function*/ loadCallback) {
                _self._contentPaneLoadConnects.push(dojo.connect(this, "onLoad", loadCallback));
            }
        });

        dojo.body().appendChild(this._dummyContentPane.domNode);
        dojo.html.hide(this._dummyContentPane.domNode);
    },

    _wireAllDeletionTriggers: function(/*domNode*/ rootNode) {
        var query = (rootNode === this._parentTbody) ? "> tr > td > div.nList_delete" : "> td > div.nList_delete";
        var deletionTriggerNodes = dojo.query(query, rootNode);

        if (deletionTriggerNodes.length > 0) {
            deletionTriggerNodes.forEach(function(deletionTrigger) {
                this._nListConnects.push(dojo.connect(deletionTrigger, "onclick", this, function(evt) {
                    var trNode = getExpectedParent(evt.target, "tr");

                    requestDeletion(trNode, this.collectionName);
                    dojo.dom.destroyNode(trNode);

                    delete trNode;
                }));

            }, this);
        }
    }

});
