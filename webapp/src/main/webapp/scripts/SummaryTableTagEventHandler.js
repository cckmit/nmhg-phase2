/**
 * @author: janmejay.singh
 * This is our extention to the BasicEventHandler that summary table has. This handles preview and detail.
 */
dojo.declare("tavant.twms.summaryTable.BasicTwmsEventHandler", tavant.twms.summaryTable.BasicEventHandler, {
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
        if(this.previewPane) {
            dojo.connect(this.previewPane, "onLoad", this.previewPane, function() {
                dojo.query("button,input[type='submit']", this.domNode).forEach(function(button) {
                    // since otherwise the form would still be submitted if user presses enter inside a form field.
                    button.disabled = true;
                    dojo.html.hide(button);
                });
                dojo.query("button,input[type='button']", this.domNode).forEach(function(button) {
		    // since otherwise the form would still be submitted if user presses enter inside a form field.		    
		    button.disabled = true;
		    dojo.html.hide(button);
                });
            });
        }
    },

    _maximizePreview : function() {
        if (!this._minimized) {
            if (this._enableTableMinimize) {
                this.rootLayoutContainer.removeChild(this.buttonContainer);
                this.splitContainer.removeChild(this.tableLayoutContainer);
                this._minimized = true;
            } else {
                throw new Error("SummaryTableTagEventHandler : Table minimizing/restoring functionality is not enabled.");
            }
        }
    },

    _restorePreview : function() {
        if (this._minimized) {
            if (this._enableTableMinimize) {
                this.rootLayoutContainer.addChild(this.buttonContainer);
                this.splitContainer.addChild(this.tableLayoutContainer);
                this._minimized = false;
            } else {
                throw new Error("SummaryTableTagEventHandler : Table minimizing/restoring functionality is not enabled.");
            }
        }
    },

    getSelectedRowDataId : function() {
		return this.selectedRowDataId;
	},

	onRowDblClick : function(event) {
		var url = this._getDetailUrl(event.folder, event.dataId);
		if(url != null) {
			var tab = getTabHavingId(getTabDetailsForIframe().tabId);
            var self = this;
            parent.publishEvent("/tab/open", {label: event.labelPrefix + " " + event.labelSuffix,
                                              url: url,
                                              decendentOf: tab.title,
                                              feedbackTabRef : function(childTab) {
                                                  self._setSummaryTableDetailsOnFrame(
                                                      childTab.domNode.getElementsByTagName("iframe")[0],
                                                      false
                                                  );
                                              }});
			delete tab;
		}
	},

	onRowClick : function(event) {
        if(this.selectedRowDataId != event.dataId && this.renderPreview) {
            var url = this._getPreviewUrl(event.folder, event.dataId);
            if(url != null) {
				this._loadPreview(url);
				this.selectedRowDataId = event.dataId;
			}
		}
	},

    _setSummaryTableDetailsOnFrame : function(/*domNode (iframe)*/ frame, /*boolean*/ isPreview) {
        frame.TST_ID = this.summaryTable.id;
        frame.TST_IS_PREVIEW = isPreview;
    },

    _getPreviewUrl : function (folder, id) {
        var previewUrl = this._reader.getVar(CONSTANTS.previewUrl);
        if(dojo.string.isBlank(previewUrl)) {
			return null;
		} else {
			return this._prepareUrl(previewUrl, folder, id);
		}
	},

    _loadPreview : function(url) {
        this.previewPane.setHref(url);
    },

    _getDetailUrl : function (folder, id) {
        var detailUrl = this._reader.getVar(CONSTANTS.detailUrl);
        if(dojo.string.isBlank(detailUrl)) {
			return null;
		} else {
			return this._prepareUrl(detailUrl, folder, id);
		}
	},

	_clearPreview : function() {
		this.previewPane.setContent("");
		this.selectedRowDataId = null;
	},

    _showPreview : function() {
        this.splitContainer.addChild(this.previewPane);
        this.renderPreview = true;
    },

    _hidePreview : function() {
        this.splitContainer.removeChild(this.previewPane);
        this.renderPreview = false;
    },

    _prepareUrl : function(/*String*/ baseUrl, folder, id) {
        return new tavant.twms.summaryTable.Url(baseUrl, {
            folderName: folder,
            id: id
        }, this._reader).toGet();
    }
});

dojo.declare("tavant.twms.summaryTable.MultiSelectTwmsEventHandler", tavant.twms.summaryTable.BasicTwmsEventHandler, {

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