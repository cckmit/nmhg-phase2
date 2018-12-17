/**
 * Event handler for SummaryTable... for specific use cases... it can be overridden and the new class can be used.
 * @author janmejay.singh
 * Note: for ppl writing there own event handlers....
 * The preview's iframe has a iframe name usage convention, which allowes the t:openTab to play well with it.
 * in case u override the function's that generate the iframe name, please make sure that u don't break the convention.
 */

dojo.declare("tavant.twms.summaryTable.BasicEventHandler", null, {
    _reader : null,

    selectedRowDataId : null,//holds the dataId of the row that was clicked last time.
	previewPane : null,
    splitContainer : null,
    summaryTable : null,
    rootLayoutContainer : null,

    _enableTableMinimize : false,

    _minimized : false,

    renderPreview : true,

    constructor : function(summaryTable, reader) {
        this._reader = reader;
        dojo.subscribe(summaryTable.rowClickTopic, this, "onRowClick");
		dojo.subscribe(summaryTable.rowDblClickTopic, this, "onRowDblClick");
		dojo.subscribe(summaryTable.clearSelectionTopic, this, "_clearPreview");
        dojo.subscribe(summaryTable.hidePreviewTopic, this, "_hidePreview");
        dojo.subscribe(summaryTable.showPreviewTopic, this, "_showPreview");
        dojo.subscribe(summaryTable.maximizePreview, this, "_maximizePreview");
        dojo.subscribe(summaryTable.restorePreview, this, "_restorePreview");
        this.previewPane = dijit.byId(this._reader.getVar(CONSTANTS.previewPaneId));
        var previewPane = this.previewPane;

        if(previewPane) {
            var previewPaneClass = previewPane.declaredClass;
            if(previewPaneClass == "dojox.layout.ContentPane") {
                previewPane.renderStyles = true;
            } else {
                throw new Error("Preview panes must be of type dojox.layout.ContentPane! You are using " +
                        previewPaneClass + ".");
            }
        }

        this._enableTableMinimize = this._reader.getVar(CONSTANTS.enableTableMinimize);
        this.splitContainer = dijit.byId(this._reader.getVar(CONSTANTS.parentSplitContainerId));
        if (this._enableTableMinimize) {
            this.rootLayoutContainer = dijit.byId(this._reader.getVar(CONSTANTS.rootLayoutContainerId));
            this.tableLayoutContainer = dijit.byId(this._reader.getVar(CONSTANTS.layoutContainerId));
            this.buttonContainer = dijit.byId(this._reader.getVar(CONSTANTS.buttonContainerId));
        }
        this.summaryTable = summaryTable;
    },

    _maximizePreview : function() {/*do something here*/},

    _restorePreview : function() {/*do something here*/},

    getSelectedRowDataId : function() {
		return this.selectedRowDataId;
	},
	
	onRowDblClick : function(event) {/*do something here*/},
	
	onRowClick : function(event) {/*do something here*/},
	
	_clearPreview : function() {/*do something here*/},

    _showPreview : function() {/*do something here*/},

    _hidePreview : function() {/*do something here*/}
});

dojo.declare("tavant.twms.summaryTable.MultiSelectSampleEventHandler", tavant.twms.summaryTable.BasicEventHandler, {

    _selectedRowDataIds : null,

    constructor : function(summaryTable, reader) {
        dojo.subscribe(summaryTable.rowCtrlClickTopic, this, "onRowCtrlClick");
    },

    ctrlClickHandler : function(event) {
        return true;
    },

    onRowCtrlClick : function(event) {
        this._selectedRowDataIds = new Array();
        for(var i in event.selectedRows) {
            this._selectedRowDataIds.push(event.selectedRows[i].dataId);
        }
    }
});