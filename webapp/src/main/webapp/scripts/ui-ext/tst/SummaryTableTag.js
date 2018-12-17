/**
 * SummaryTable
 * @author janmejay.singh
 */

dojo.require("dijit.form.Button");
dojo.require("dojo.data.util.simpleFetch");
dojo.require("dojo.fx");

var CONSTANTS = {
    prefixForTableHeadId : "prefixForTableHeadId",
    prefixForTableBodyId : "prefixForTableBodyId",
    prefixForTableFilterId : "prefixForTableFilterId",
    prefixForSortImageDiv : "prefixForSortImageDiv",
    headTableId : "headTableId",
    bodyTableId : "bodyTableId",
    bodyUrl : "bodyUrl",
    previewUrl : "previewUrl",
    detailUrl : "detailUrl",
    totalRecordSpanId:"totalRecordSpanId",
    pageNumberSpanId : "pageNumberSpanId",
    totalPagesSpanId : "totalPagesSpanId",
    folder : "folder",
    dummyRowId : "dummyRowId",
    previewPaneId : "previewPaneId",
    minColumnSize : "minColumnSize",
    nextPageButtonId : "nextPageButtonId",
    previousPageButtonId : "previousPageButtonId",
    firstPageButtonId : "firstPageButtonId",
    lastPageButtonId : "lastPageButtonId",
    pageSelectorId : "pageSelectorId",
    togglePreviewVisibilityId : "togglePreviewVisibilityId",
    quickPaginatorTBodyId : "quickPaginatorTBodyId",
    layoutContainerId : "layoutContainerId",
    columnData : "columnData",
    paginatorBarId : "paginatorBarId",
    parentSplitContainerId : "parentSplitContainerId",
    idColumn : "idColumn",
    labelColumn : "labelColumn",
    cssColumnId : "cssColumnId",
    extraParamsVar : "extraParamsVar",
    extraParamsFunctions : "extraParamsFunctions",
    table : "table",
    eventHandler : "eventHandler",
    rootLayoutContainerId : "rootLayoutContainerId",
    buttonContainerId : "buttonContainerId",
    enableTableMinimize : "enableTableMinimize",
    refreshListingTopic : "/refresh/inboxView#${id}/listing",
    refreshFullTopic : "/refresh/inboxView#${id}/full",
    hideCompletedRowTopic : "/refresh/inboxView#${id}/hideCompletedRow",
    hideCompletedRowsTopic : "/refresh/inboxView#${id}/hideCompletedRows",
    minimizeListingTopic : "/listing/inboxView#${id}/minimize",
    restoreListingTopic : "/listing/inboxView#${id}/restore",
    showHidePreviewButtonId : "showHidePreviewButtonId"
};

var SUMMARY_TABLE_UTIL = {
    getRefreshFullTopic : function(/*String(the id of summaryTable)*/ id) {
        return this._getTopic(CONSTANTS.refreshFullTopic, id);
    },
    getRefreshListingTopic : function(/*String(the id of summaryTable)*/ id) {
        return this._getTopic(CONSTANTS.refreshListingTopic, id);
    },
    getHideCompletedRowTopic : function(/*String(the id of summaryTable)*/ id) {
        return this._getTopic(CONSTANTS.hideCompletedRowTopic, id);
    },
    getHideCompletedRowsTopic : function(/*String(the id of summaryTable)*/ id) {
        return this._getTopic(CONSTANTS.hideCompletedRowsTopic, id);
    },
    getMinimizeTopic : function(/*String (the id of SummaryTable)*/ id) {
        return this._getTopic(CONSTANTS.minimizeListingTopic, id);
    },
    getRestoreTopic : function(/*String (the id of SummaryTable)*/ id) {
        return this._getTopic(CONSTANTS.restoreListingTopic, id);
    },
    _getTopic : function(/*String (substitutable name)*/ topicName,/*String(the id of summaryTable)*/ id) {
        if(!id) throw new Error("SUMMARY TABLE UTIL : id is a mandatory parameter");
        return dojo.string.substitute(topicName, {id : id});
    }
}

dojo.declare("tavant.twms.SummaryTable", null, {
    _reader : null,//this is the object that facilitates the reading of values from summaryTable's assoc array.
    id : "",//the user defined ID.

    columns : null,
	grabbers : null,
	tableLayoutContainer : null,//domNode
    tableLayoutContainerWidget : null,//widget
    splitContainer: null,
	bodyTable : null,
	headTable : null,
	tBody : null,
	previewPane : null,
	detailPrefix : "",
	labelColumn : null,
	nCols : 0,
	state : null,
    totalRecordHolder:null,
    pageNumberHolder : null,
	totalPagesHolder : null,

	paginator : null,
	nextPageButton : null,
	previousPageButton : null,
    lastPageButton : null,
    firstPageButton : null,
    totalPages : 1,
    pageSelector : null,
    _previewAble : true,//this attribute governs whether the preview is shown or not.
    _hasDetail: null,

    filterTimeOut : 650, //mills
	minColumnWidth : 0,
	lastSortedColumn : null,
	filterRequest : null,
	currentSelection : null,//{elem: null, dataId: null}

	bMultiSelect : false,//if this is true, multi select works.
	//ctrlClickHandler : a function needs to be attached here, in case user wants to change the way Ctrl + Click is
	//handeled by default. (returning true, will select the row, and returning false, will ignore the click.
	ctrlClickHandler : null,
    //used as an array, when multi select is enabled, element is {elem: domNodeOfRow, dataId: dataIdOfRow}
    selectedRows : null,//array

	rowClickTopic : "/summaryTable/rowClick",
	rowDblClickTopic : "/summaryTable/rowDblClicked",
	rowCtrlClickTopic : "/summaryTable/rowCtrlClick",
	clearSelectionTopic : "/summaryTable/clearSelection",
    hidePreviewTopic : "/inbox/hidePreview",
    showPreviewTopic : "/inbox/showPreview",
    maximizePreview : "/inbox/maximzePreview",
    restorePreview : "/inbox/restorePreview",

    _rows : 0, //holds number of rows.

    previewVisible : true,
    _showHidePreviewButton : null,
    _showHidePreviewButtonDiv : null,//the div that wraps the text span and has the up/down arrow
    _showHidePreviewButtonTextSpan : null,//the span that wraps the text, used to change the text.

    _quickPaginatorRow : null,
    _paginationButtonsHolder: null,

    STATIC_VALS : {
        MAX_QUICK_PAGINATION_BUTTONS : 5
    },

    hidePreviewByDefault: true,

    /*
	 * The constructor; takes the header information and a number which will be used as the minimum width of all
	 * columns. The colum header data is an associative array and should contain:
	 * {
	 *     id: a string used to identify this column logically; not to be confused with the id
	 *         attribute of a DOM element
	 *     alignment: "left" or "right"; specifies which way the text in this column aligns
	 *     width: initial width of this column in percentage
	 *     title: the label of this column which appears on the header of the table
	 * }
	 */
	constructor : function(/*instance of tavant.twms.summaryTable.ValueReader*/ reader, /*String (id)*/ id) {
        this._reader = reader;
        this.id = id;
        this.columns = new Array();
		this.grabbers = new Array();
        this.currentSelection = {elem: null, dataId: null};
        this.selectedRows = new Array();
        this.tableLayoutContainer = dojo.byId(this._reader.getVar(CONSTANTS.layoutContainerId));
        this.paginator = dojo.byId(this._reader.getVar(CONSTANTS.paginatorBarId));//FIXME: START HERE!!!
        this.headTable = dojo.byId(this._reader.getVar(CONSTANTS.headTableId));
        this._addColumns(this._reader.getVar(CONSTANTS.columnData));
        this.detailPrefix = this._reader.getVar(CONSTANTS.labelColumn).title;
        this.labelColumn = this._reader.getVar(CONSTANTS.labelColumn);
        this.minColumnWidth = this._reader.getVar(CONSTANTS.minColumnSize)/ 100;
        this._installGrabbers();
        this._quickPaginatorRowHolder = dojo.byId(this._reader.getVar(CONSTANTS.quickPaginatorTBodyId));

        this._paginationButtonsHolder = dojo.query(".paginationButtonsHolder", this.paginator)[0];
    },

	/***********Public Functions************/

	/*
	 * Should be invoked after the page has finished loading.
	 */
	onLoad : function() {
        this._initWidgets();
        this.state = new tavant.twms.summaryTable.SummaryTableState(this._reader.getVar(CONSTANTS.bodyUrl),
                        this._reader.getVar(CONSTANTS.folder), this._reader);
        this.state.previewVisible = this.previewVisible;

        if(this.splitContainer) {
            dojo.connect(this.splitContainer, "layout", this, "onResize");
            dojo.connect(this.splitContainer, "endSizing", this, "_manageTableBodyWidthChange");

            this._refreshPreviewShowHideButtonContents();
            this._showHidePreviewButton.onClick = dojo.hitch(this, this._togglePreviewVisibility);

            if (this.hidePreviewByDefault) {
                this.previewVisible = true;
                this._togglePreviewVisibility();
            }
        }else{
            this._loadBody();
        }

        dojo.connect(this.tableLayoutContainer, "layout", this, "onResize");

        // If hidePreviewByDefault is enabled, we would be calling _togglePreviewVisibility() on load and that in turn
        // would call _loadBody() internally. Hence, if this check is not there, we would end up loading the table
        // contents twice.
        if(!this.hidePreviewByDefault) {
            this._loadBody();
        }
    },

    minimize : function() {
        publishEvent(this.maximizePreview);
    },

    restore : function() {
        publishEvent(this.restorePreview);
    },

    /**
     * This function is called when the tab is focused... and this makes sure, that the grabbers or other absolutely
     * positioned elements display don't mess-up as a result of the table data refresh while the table's tab was
     * not focused.
     * NOT a HACK : this function is ditching the thread that is executing the functions listing to the event...
     * that thread is screwed-up(because iframe spawning it gets killed after firing a async ajax call....
     * and no one listens to the response...)
     * so we spawn a new thread and fire our functions from that....
     */
    manageRenderingOnFocus : function(event) {
        var refreshList = dojo.hitch(this, this.refreshTable);
        var refreshFull = dojo.hitch(this, this.refreshTableAndPreview);
        setTimeout(function() {//please read the doc above
            if(event.needsRefresh) {
                event.needsRefresh.fullRefresh ? refreshFull() : refreshList();
            }
        }, 0);
    },

    /*
	 * Enables multiple selection(multiple rows can be selected on Ctrl + Click)
	 */
	enableMultipleSelection : function() {
		this.bMultiSelect = true;
	},

	/*
	 * Gets the current width of column at a given index. First column is at index 0.
	 */
	getColumnWidth : function(/* int */index) {
		return this.columns[index].getWidth();
	},

	/*
	 * Fixes the width of column at a given index to the specified value. First column is at index 0.
	 * The change will make any visual effect only after renderColumns is called.
	 */
	setColumnWidth : function(/* int */ index,
	                          /* string, which ends in a '%' or 'px' */ width) {
		this.columns[index].setWidth(width);
	},

	/*
	 * Fixes the top of all grabbers to the top of the SummaryTable.
	 */
	fixTopOfAllGrabbers : function() {
		for(i = 0; i < this._nGrabbers(); i++) {
			this._setGrabberY( i, this._getHeadTableY());
		}
	},

	/*
	 * After some change in the SummaryTable happens, this method has to be called to make those changes take
	 * visual effect.
	 * fullUpdate = false means that only the label header of a column join the adjancent grabbers; the
	 * filter and body text of the column stays where they were earlier.
	 * fullUpdate = true means the label header, filter and body text of a column join the adjacent
	 * grabbers.
	 */
	renderColumns : function(/* boolean */fullUpdate) {
        this._failSelectionInIE();
		var total = 0;
		for (var i=0; i < this.nCols - 1; i++)	{
			var width = Math.round(this.getColumnWidth(i) * 100);
			total += width;
			this._setColumnHeaderWidth(i, width + "%");
			if (fullUpdate) {
				this._setColumnBodyWidth(i, width + "%");
			}
		}

		this._setColumnHeaderWidth(this.nCols - 1, (100 - total) + "%");
		if (fullUpdate) {
			this._setColumnBodyWidth(this.nCols - 1, (100 - total) + "%");
			this._syncFiltersWithGrabbers();
		}
	},

	/*
	 * Invoked when the SummaryTable is resized i.e. if the browser window resizes or if the split pane
	 * is adjusted.
	 */
	onResize : function() {
		this._layoutGrabbers();
		this._syncFiltersWithGrabbers();
        this._manageTableBodyWidthChange();
    },

	/*
	 * Invoked when the user requests a sort on a column.
	 * field is the logical id of the column.
	 */
	onSort : function(/* tavant.twms.summaryTable.Column*/col,
                      /* string id */ field,/* string value["asc"/"dsc"] */ sortType) {
        this.state.sort(field, sortType);
        this._loadBody();
    },

    resetSort : function() {
        for(var i in this.columns) {
            this.columns[i].removeSort();
        }
        this.state.resetSort();
    },

    /*
	 * Invoked when the user adds/edits the filter on a column.
	 */
	onFilter : function(/* string id */ field,
	                    /* string, new filter value */ value) {
		var self = this;
		this.filterRequest = setTimeout(function() {
				self._fireFilterRequest(field, value);
			}, this.filterTimeOut);
	},

	/*
	 * Used to indicate that the user is still editing one of the filters.
	 * An Ajax request will not be made while the user is typing on a filter.
	 */
	stillTyping : function() {
		clearTimeout(this.filterRequest);
	},

	/*
	 * Invoked when the user requests previous page.
	 */
	previousPage : function() {
		this.state.previousPage();
        this._loadBody();
	},

	/*
	 * Invoked when the user requests next page.
	 */
	nextPage : function() {
		this.state.nextPage();
        this._loadBody();
	},

	/*
	 * Invoked when the user clicks/ctrlClicks on a row.
	 */
	onRowClick : function(event) {
        var target = getExpectedParent(event.target, "tr");
		var rowId = target.dataId;
		event.row = target;
		event.dataId = rowId;
		event.folder = this._reader.getVar(CONSTANTS.folder);
        if(this.bMultiSelect) {
			if(event.ctrlKey) {//it is a Ctrl + Click
				if(this.ctrlClickHandler != null) {
					if(this.ctrlClickHandler(event)) {
						this._selectRow(target);
						this._setCurrentSelection(target, rowId);
					}//else(which means ctrlClickHandler returned false...) we ignore the click.
				} else {//if ctrlClickHandler is not attached.
					this._selectRow(target)
					this._setCurrentSelection(target, rowId);
				}
				event.selectedRows = this.getCurrentSelection();
				dojo.publish(this.rowCtrlClickTopic, [event]);
				return;//so that we don't end up firing a rowClick event aswell.
			} else {//if Ctrl key was not pressed.
				this._resetSelection();
				this._selectRow(target)
				this._setCurrentSelection(target, rowId);
			}
		} else {//if multiple select is not enabled
			this._resetSelection();
			this._selectRow(target)
			this._setCurrentSelection(target, rowId);
		}
        publishEvent(this.rowClickTopic, event);
    },

	/*
	 * Invoked when the user double clicks a row.
	 */
	onRowDblClick : function(event) {
		var target = getExpectedParent(event.target, "tr");
		var rowId = target.dataId;
		var labelSuffix = target.labelSuffix;
		event.labelSuffix = labelSuffix;
		event.dataId = rowId;
		event.folder = this._reader.getVar(CONSTANTS.folder);
		event.labelPrefix = this.detailPrefix;
		dojo.publish(this.rowDblClickTopic, [event]);
	},

	/*
	 * Resets the selection, and publishes an event, which can be listened to, and appropriate action can be taken.
	 */
	clearSelection : function() {
		this._setCurrentSelection(null, null);//it is not equal to resetSelection
		this._resetSelection();

        if(this._previewAble) {
            dojo.publish(this.clearSelectionTopic);
        }
    },

	/*
	 * Hides a row; provides a visual fading effect, and re-renders the table data.
	 */
	hideCompletedRow : function(event) {
        	var dataId = event.dataId;
        	var node = null, nextSelect = null;
        	var _self = this;
        	
		for(i = 1; i < this.tBody.childNodes.length; i++) {
			if(this.tBody.childNodes[i].dataId == dataId) {
				node = this.tBody.childNodes[i];
				nextSelect = i;
            			break;
			}
		}
		if (node == null) {
			return;
		}
		
		dojo.fx.wipeOut({
			node: node,
			duration: 500,
			onEnd: function() {
			  _self._loadBody(nextSelect);
		   	}
		}).play();

		
		var tabId = getTabDetailsForIframe().tabId;
		var tab = getTabHavingId(tabId);
    		parent.publishEvent("/refresh/folderCount", {folderName: tab.label});
	    	delete tabId, tab;
	},

	/*
	 * Hides rows; provides a visual fading effect, and re-renders the table data.
	 */
	hideCompletedRows : function(event) {
        var dataIds = event.dataIds;
        var animObjects = new Array(), nextSelect = null;
		var self = this;
		var animTimeMills = 500;
		for(var i = 1; i < this.tBody.childNodes.length; i++) {
			for(var j = 0; j < dataIds.length; j++) {
				if( (this.tBody.childNodes[i].dataId == dataIds[j] ) && ( animObjects.length < dataIds.length) ) {
					animObjects.push( dojo.fx.html.fadeHide(this.tBody.childNodes[i], animTimeMills, null, null));
					nextSelect = i;
            		break;
				}
			}
		}
		if (animObjects.length == 0) {
			return;
		}
		for(var i = 0; i < animObjects.length; i++) {
			animObjects[i].play();
		}
		setTimeout(function() {
            self._loadBody(nextSelect);
		}, animTimeMills);//beacuse i have to wait for the animations to complete.
		var tabId = getTabDetailsForIframe().tabId;
		var tab = getTabHavingId(tabId);
    	parent.publishEvent("/refresh/folderCount", {folderName: tab.label});
    	delete tabId, tab;
	},

	/*
	 * Refreshes the SummaryTable. The current state of pagination, filtering, sorting, and column width adjustments
	 * are not disturbed.
	 */
	refreshTable : function() {
        this.state.previewVisible = this.previewVisible;
        this._loadBody();
    },

    refreshTableAndPreview : function() {
        this.clearSelection();
        this.refreshTable();
    },

    firstPage : function() {
        this.state.setPage(1);
        this.refreshTable();
    },

    lastPage : function() {
        this.state.setPage(this.totalPages);
        this.refreshTable();
    },

    goToSelectedPage : function(pageNo) {
        if(pageNo == this.state.page) return;
        this.state.setPage(pageNo);
        this.refreshTable();
    },

	getCurrentSelection : function() {
		if(this.bMultiSelect) {
			return this.selectedRows;
		} else {
			return this.currentSelection;
		}
	},

    /*********Private Functions************/

    _manageTableBodyWidthChange : function () {
//        if(dojo._getBorderBox(this.headTable).w != dojo._getBorderBox(this.bodyTable).w) {
//            dojo.style(this.headTable, "width", dojo._getBorderBox(this.bodyTable).w + "px");
//        }
    },

    _togglePreviewVisibility : function() {
        var topicName = this.previewVisible ? this.hidePreviewTopic : this.showPreviewTopic;

        dojo.publish(topicName);
        this.previewVisible = !this.previewVisible;
        this._refreshPreviewShowHideButtonContents();
        this.refreshTable();
    },

    _refreshPreviewShowHideButtonContents : function() {
        var classToBeAdded, classToBeRemoved, label;

        if(this.previewVisible) {
            classToBeRemoved = "previewHidden";
            classToBeAdded = "previewShown";
            label = i18N.summary_table_hide;
        } else {
            classToBeRemoved = "previewShown";
            classToBeAdded = "previewHidden";
            label = i18N.summary_table_show;
        }

        dojo.removeClass(this._showHidePreviewButtonDiv, classToBeRemoved);
        dojo.addClass(this._showHidePreviewButtonDiv, classToBeAdded);
        this._showHidePreviewButtonTextSpan.innerHTML = label;
    },

    _initWidgets : function() {//this is a seprate function, because this is supposed to load the widgets by id,
        //which is only possible after loading of the page.
        this.tableLayoutContainerWidget = dijit.byId(this._reader.getVar(CONSTANTS.layoutContainerId));
        this.bodyTable = dojo.byId(this._reader.getVar(CONSTANTS.bodyTableId));
		this.tBody = dojo.query("tbody", this.bodyTable)[0];

        this.splitContainer = dijit.byId(this._reader.getVar(CONSTANTS.parentSplitContainerId));
        this._previewAble = this.previewVisible = (this.splitContainer != null);

        this._hasDetail = dojo.string.isBlank(summaryTableVars[this.id].detailUrl);

        this.nextPageButton = dijit.byId(this._reader.getVar(CONSTANTS.nextPageButtonId));
		this.previousPageButton = dijit.byId(this._reader.getVar(CONSTANTS.previousPageButtonId));
        this.firstPageButton = dijit.byId(this._reader.getVar(CONSTANTS.firstPageButtonId));
        this.lastPageButton = dijit.byId(this._reader.getVar(CONSTANTS.lastPageButtonId));
        this.totalRecordHolder = dojo.byId(this._reader.getVar(CONSTANTS.totalRecordSpanId));
        this.pageNumberHolder = dojo.byId(this._reader.getVar(CONSTANTS.pageNumberSpanId));
		this.totalPagesHolder = dojo.byId(this._reader.getVar(CONSTANTS.totalPagesSpanId));
        this.pageSelector = dijit.byId(this._reader.getVar(CONSTANTS.pageSelectorId));
        this.pageSelector.onChange = dojo.hitch(this, this.goToSelectedPage);

        var showHidePreviewBtn = dijit.byId(this._reader.getVar(CONSTANTS.showHidePreviewButtonId));

        if(this._previewAble) {
            this._showHidePreviewButton= showHidePreviewBtn;
            this._showHidePreviewButtonDiv = dojo.query("div.paginatorShowHideButtonDiv",
                this._showHidePreviewButton.domNode)[0];
            this._showHidePreviewButtonTextSpan = dojo.query("span.labeledPaginatorButton",
                this._showHidePreviewButtonDiv)[0];
        } else {
            dojo.html.hide(showHidePreviewBtn.domNode);
        }
    },

    _addColumn : function(column) {
		this.columns.push(column);
		this.nCols = this.columns.length;
	},

	_installGrabbers : function() {
		this._addGrabbers();
		this.renderColumns(true);
		this._disableSelections(document.body);
	},

	_addColumns: function(colData) {
		for(var i in colData) {
            this._addColumn( new tavant.twms.summaryTable.Column(colData[i], this, this._reader) );
		}
	},

	_setColumnHeaderWidth : function(index, width) {
		this.columns[index].setHeaderWidth(width);
	},

	_setColumnBodyWidth : function(index, width) {
		this.columns[index].setBodyWidth(width);
	},

	_addGrabbers : function() {
		var _grabberOffset = 0
		for (var i = 0; i < this.columns.length - 1; i++)	{
			_grabberOffset += this.columns[i].getWidth();
			var grabber = new tavant.twms.summaryTable.Grabber(this, i, _grabberOffset);
			this.grabbers.push(grabber);
			this.tableLayoutContainer.appendChild(grabber.domNode);
		}
		delete _grabberOffset;
	},

	_nGrabbers : function() {
		return this.nCols - 1;
	},

	_syncFiltersWithGrabbers : function() {
		for(var i = 0; i < this._nGrabbers(); i++) {
			this._syncFilterWithGrabber(i);
		}
	},

	_syncFilterWithGrabber : function(indexOfGrabber) {
		this._ensureGrabberAwayFromNextCellEdge(indexOfGrabber);
    },

	/**
     * This function ensures the i-th grabber's left end is always little away frm the
     * left edge of i+1-th table cell. This ensures that it won't cut through the text in the table.
     * for IE... it is 2px... and others its 4px.
     */
	_ensureGrabberAwayFromNextCellEdge : function(i) {
		var offsetFrmLeft = this._getBodyCellX(i + 1);
        this._setGrabberX(i, (offsetFrmLeft - (dojo.isIE ? 2 : 4)) + "px");
	},

	_layoutGrabbers : function() {
		var correctHeight =
			dojo.contentBox(this.tableLayoutContainer).h - dojo.contentBox(this.paginator).h;
		for(var i = 0; i < this._nGrabbers(); i++) {
			this.grabbers[i].setHeight( correctHeight + "px" );
			this._setGrabberX( i, this._getBodyCellX(i+1) );
		}
	},

	_clearTBody : function() {
		var oldTBody = dojo.dom.removeNode(this.tBody, true);
		var dummyRow = dojo.query("tr", oldTBody)[0];
		var tBody = document.createElement("tbody");
		tBody.appendChild(dummyRow);
		this.tBody = tBody;
		this._rows = 0;
		return tBody;
	},

	_addNewRow : function(row) {
		this._rows++;
		this.tBody.appendChild(row);
		if(this._rows%2) {//changes the color of alternate rows.
			dojo.addClass(row, "alternateRow");
		}
		dojo.addClass(row, "row");

        if(!(this._previewAble || this._hasDetails)) {
            dojo.addClass(row, "rowStatic");
        } else {
		    dojo.connect(row, "ondblclick", this, "onRowDblClick");
        }

        dojo.connect(row, "onclick", this, "onRowClick");
    },

	_disableSelections : function(node) {//works only in FF not IE....
		// CSS3 draft way
		if (typeof dojo.style(node, "userSelect") == "string")
			dojo.style(node, "userSelect", "none");

		// mozilla extension
		if (typeof dojo.style(node, "MozUserSelect") == "string")
			dojo.style(node, "MozUserSelect", "none");

		// msie/safari extension
		//if(typeof node.onselectstart != "undefined")
		//node.onselectstart = DisabledHandler;
	},

	_failSelectionInIE : function() {// disableSelections hack for IE.
		if(dojo.isIE) {
            //this hack right here.... will make sure that no ugly selection occurs in IE.. when grabber is dragged.
			document.selection.empty();
		}
	},

	_fireFilterRequest : function (field, value) {
		this.state.filter(field, value);
        this._loadBody();
	},

	_getRowIndex : function(dataId) {
		if (dataId == null) {
			return null;
		}
		for(var i = 1; i < this.tBody.childNodes.length; i++) {
			if(this.tBody.childNodes[i].dataId == dataId) {
				return i;
			}
		}
		return null;
	},

	_setBodyData : function(page, selectionIndex) {
        var totalPages = page[3];
        var waitTime = 0;
        var targetNode = this.bodyTable.parentNode;
        var dataAvailable = totalPages > 0;
       
        if(!dataAvailable) {
            waitTime = 3000;
            if(dijit.byId('_summaryTable_button_claimButton'))
            {
            dijit.byId('_summaryTable_button_claimButton').disabled=true;
            }
            twms.util.changeLidMessageFor(targetNode, "No results found.");
            
        }
        
        var data = page[0];
		this._clearTBody();
		this._createRows(data);
		// We attach the TBODY to the TABLE only after all rows are appended to the TBODY.
		// This is more preformant apparently.
		this.bodyTable.appendChild(this.tBody);

		// Try to get the row index that needs to be selected
		if (!selectionIndex) {
			selectionIndex = this._getRowIndex(this.currentSelection.dataId);
			this.currentSelection.elem = this.tBody.childNodes[selectionIndex];
		}
		// If still null, force it to 1
		if (!selectionIndex) {
			selectionIndex = 1;
		}

		var len = this.tBody.childNodes.length;
		if (len > 1) { // the page is empty, there is nothing to select
			if (len <= selectionIndex) { // the selectionIndex has overshot the length of the data in the table
				selectionIndex = len - 1;
			}
            this.onRowClick({target: this.tBody.childNodes[selectionIndex]});
		} else {
			this.clearSelection(); // nothing is selected, so lets clear the preview
		}
		delete len;

		this._setNextPageAvailable( page[1] );
		this._setPreviousPageAvailable( page[2] );
        this._setTotalPageCount(totalPages);

        if (dataAvailable) {
            this.totalRecordHolder.innerHTML=page[4];
            this._renderQuickPaginatorButtons(totalPages, this.state.page);
            if (this.state.page <= totalPages) {
                this._setPageLabel(this.state.page, totalPages);
            } else {
                this.previousPage();
            }
        }
        
        delete totalPages;

		// In FF the grabber cuts through the body text in case the scrollbar appears.
		// So explicitly invoking onResize() if FF.
		if(dojo.isFF) {
			this.onResize();
		}

        setTimeout(function() {
            twms.util.removeLidFrom(targetNode);
        }, waitTime);
        
    },

    _setTotalPageCount: function(/*number*/ totalPageCount) {
        this.totalPages = totalPageCount;
        dojo.publish("/summaryTable/" + this.id + "/totalPages", [{
            totalPageCount: totalPageCount
        }]);

        dojo.html.setDisplay(this._paginationButtonsHolder, (totalPageCount > 0));
    },

     _setTotalRecordsCount: function(/*number*/ totalRecordsCount) {

    },

    _renderQuickPaginatorButtons : function(totalPages, currentPage) {
        //trying to get the value from which it shd start
        var pageData =
                this._getStartAndEndPages(this.STATIC_VALS.MAX_QUICK_PAGINATION_BUTTONS, currentPage, totalPages);
        var pageStart = pageData.start;
        var pageEnd = pageData.end;
        dojo.dom.destroyNode(dojo.query("tr", this._quickPaginatorRowHolder)[0]);
        var tr = document.createElement("tr");
        var self = this;
        var onClickGenerator = function(pageNo) {//this is needed because otherwise the function will pickup page number
            //as maximum number all the time(because of closure being in a loop, which is increamenting the index).
            return function() {
                self.state.setPage(pageNo);
                self.refreshTable();
            };
        }
        for(var i = 0; i <= (pageEnd - pageStart); i++) {
            var td = document.createElement("td");
            td.innerHTML = getI18NNumber(pageStart + i);
            if(currentPage == (pageStart + i)) {
                dojo.addClass(td, "selected");
                this.pageSelector.set('value',currentPage);
            } else {
                var pageNo = (pageStart + i);
                dojo.connect(td, "onclick", onClickGenerator(pageNo));
                delete pageNo;
            }
            tr.appendChild(td);
        }
        this._quickPaginatorRowHolder.appendChild(tr);
    },

    _getStartAndEndPages: function(maxDifference, currentPage, totalPages) {
        var start = 1;
        var end = maxDifference;
        var halfOfMax = Math.floor(maxDifference/2);
        if(maxDifference < (halfOfMax + currentPage)) {
            start = currentPage - halfOfMax;
        }
        if(maxDifference <= (totalPages - start)) {
            end = maxDifference + start - 1;
        } else {
            end = totalPages;
        }
        if((end - start) < (maxDifference - 1)) {
            start -= (maxDifference - 1 - (end - start));
        }
        if(start < 1) {//executed when number of pages is lesser than total number of buttons expected.
            start = 1;
        }
        return {start : start, end : end};
    },

    _createRows : function(data) {
		for(var i = 0; i < data.length; i++) {
			var tr = document.createElement("tr");
			for(var j = 0; j < this.nCols; j++) {
				var td = document.createElement("td");
                this.columns[j].renderer.render(td, data[i][this.columns[j].getId()]);
                td.align = this.columns[j].getAlignment();
                dojo.addClass(td, "colapsableColumn");
                tr.appendChild(td);
			}
			tr.dataId = data[i][(this._reader.getVar(CONSTANTS.idColumn)).id];
            tr.rowData = data[i];
            tr.labelSuffix = data[i][(this._reader.getVar(CONSTANTS.labelColumn)).id];
			this._addNewRow(tr);
            var cssColId = this._reader.getVar(CONSTANTS.cssColumnId);
            if(cssColId) {
				dojo.addClass(tr, data[i][cssColId]);
			}
		}
	},

	_setNextPageAvailable : function(b) {
		this.nextPageButton.set('disabled',!b);
        this.lastPageButton.set('disabled',!b);
    },

	_setPreviousPageAvailable : function(b) {
		this.previousPageButton.set('disabled',!b);
        this.firstPageButton.set('disabled',!b);
    },

	_setPageLabel : function(thisPage, totalPages) {
		this.pageNumberHolder.innerHTML = thisPage;
		this.totalPagesHolder.innerHTML = totalPages;
	},

    _loadBody : function(nextSelection) {
        twms.util.putLidOn(this.bodyTable.parentNode);
        
        var self = this;
        if(!this.state.hasSort() && !this.labelColumn.hidden) {
             if ((document.getElementById("inboxViewSortField")) && (document.getElementById("inboxViewSortField").value != '')) {
                var sortColumnId = document.getElementById("inboxViewSortField").value;
                var sortOrder = document.getElementById("inboxViewSortOrder").value;
                var sortColumn = this._getVisibleColumnById(sortColumnId);
                sortColumn.sortType = (sortOrder == 'true') ? sortColumn.STATIC_VARS.DESC_SORT : sortColumn.STATIC_VARS.ASC_SORT;
                sortColumn.sort({ target : sortColumn.getHeaderCell() });
            } else{
                var labelColumn = this._getVisibleColumnById(this.labelColumn.id);
                labelColumn.sort({ target : labelColumn.getHeaderCell() });
            }            
            return;//because otherwise... it'll end up firing 2 AJAX request... which is a wastage of bandwidth.
		}

        var urlObj = this.state.getUrlWithParams();
        fireJavaScriptRequestForSummaryTag(urlObj.getBaseUrl(), urlObj.getParams(),"javascript",
            function(response) {
        		self._setBodyData(response, nextSelection);
         		if(masterCheckBoxId && dojo.byId(masterCheckBoxId)){
        			dojo.byId(masterCheckBoxId).checked=false;
        		}
                return response;
            }, function(response) {
        	    //alert("error loading table data page! \n" + error.message); //FIXME
        	    //ignoring, can't show this popup to the user.
                return response;
            }
        );
    },

	_setCurrentSelection : function(tr, withDataId) {
		if(this.bMultiSelect) {
			if(this.selectedRows.length == 0) {
				this.selectedRows.push({elem: tr, dataId: withDataId});
				return;
			}
			if(this.selectedRows.length > 1) {
				var index = -1;
				for(var i = 0; i < this.selectedRows.length; i++) {
					if(this.selectedRows[i].dataId == withDataId) {
						index = i;
						break;
					}
				}
				if(index >= 0) {
					this._unselectRow(this.selectedRows[index].elem);
					this.selectedRows.splice(index, 1);
					return;
				}
			}
			if(this.selectedRows[0].dataId != withDataId) {
				this.selectedRows.push({elem: tr, dataId: withDataId});
			}
		} else {
			this.currentSelection.dataId = withDataId;
			this.currentSelection.elem = tr;
		}
	},

	_resetSelection : function() {
		if(this.bMultiSelect) {
			for(var i = 0; i < this.selectedRows.length; i++) {
				this._unselectRow(this.selectedRows[i].elem);
			}
			delete this.selectedRows;
			this.selectedRows = new Array();
		} else {
			this._unselectRow(this.currentSelection.elem);
			//this.currentSelection.elem = null;//this is redundant, so i don't set them to null.
			//this.currentSelection.dataId = null;
		}
	},

	_unselectRow : function(tr) {
        if(tr) {
            dojo.removeClass(tr, "selectedRow");
        }
	},

	_selectRow : function(tr) {
        dojo.addClass(tr, "selectedRow");
    },

	_getBodyCellX : function(index) {
		return this.columns[index].getBodyX();
	},

	_setGrabberX : function(index, xValue) {
		this.grabbers[index].setX(xValue);
	},

	_getGrabberX : function(index) {
		return this.grabbers[index].getX();
	},

	_setGrabberY : function(index, yValue) {
		this.grabbers[index].setY(yValue);
	},

	_getHeadTableY : function() {
		return dojo.marginBox(this.headTable).t + "px";
	},

    _getVisibleColumnById : function(/*String*/ id) {
        for(var i in this.columns) {
            if(this.columns[i].id == id) {
                return this.columns[i];
            }
        }
    }
});

/*
   Captures the state of a paginated table. There are three GUI states in a paginated
   table - the filter state, the sort state and the pagination state.
 */
dojo.declare("tavant.twms.summaryTable.SummaryTableState", null, {
    base: null,     /* The base URL from which to get data for the table */
    folder: null,   /* */
    sorts: [],      /* An array of arrays, like: [ [first_sort_column, asc_or_dsc] .. ]*/
    filters: {},    /* Map, like: { a_column, filter_on_that_column, ..} */
    page: 1,        /* The page that is being shown */
    previewVisible : true,
    _reader : null,//summary table's reader
    totalRecords:null,

    constructor: function(base, folder, reader) {
        this.base = base;
        this.folder = folder;
        this._reader = reader;
    },

    sort: function(col, sortType) {
		this.page = 1;
        for(var i in this.sorts) {
            if(this.sorts[i][0] == col) {
                this.sorts[i][1] = sortType;
            }
        }
        this.sorts.push([col, sortType]);
    },

    resetSort : function() {
        this.sorts = [];
    },

    hasSort : function() {
    	if(this.sorts.length > 0)
    		return true;
    },

    nextPage: function() {
        this.page++;
    },

    previousPage: function() {
    	if(this.page > 1)
	        this.page--;
    },

    setPage: function(/*int*/ pageNo) {
        if(pageNo > 0) {
            this.page = pageNo;
        }
    },

    setTotalRecords: function(/*long*/ totalRecords) {
       this.totalRecords=totalRecords;
    },

    filter: function(col, filter) {
        this.filters[col] = filter;
        this.page = 1;
    },

    clear: function() {
        this.page = 1;
        this.filters = {};
        this.sorts = [];
    },

    goToFirstPage: function() {
        this.page = 1;
        // keep sort and filtering state as is
    },

    getUrlWithParams: function() {
        return this.populateCriteriaOnUrl(this.base).toPost();
    },

    populateCriteriaOnUrl : function(/*String (url)*/ baseUrl) {

        var url = new tavant.twms.summaryTable.Url(baseUrl, {
            "folderName": this.folder
        }, this._reader);

        if (this.page != null) {
            url.addParam("page", this.page);
        }

        var i = 0;
        dojo.forEach(this.sorts, function(sort) {
            url.addParam("sort" + i, sort[0]);
            url.addParam("as" + i, sort[1]);
            i++;
        });

        i = 0;
        for(var j in this.filters) {
            if (this.filters[j] != "") {
                url.addParam("column" + i, j);
                url.addParam("filter" + i, this.filters[j]);
            }
            i++;
        };

        if(this.previewVisible) {
            url.addParam("previewVisible", true);
        }

        return url; // object
    },

    toString: function() {
        return "PaginatedTable with base = " + this.base +
               ", folder = " + this.folder +
               ", sorts = " + this.sorts +
               ", filters = " + this.filters +
               ", page = " + this.page;
    }
});

/*
 * Captures a URL and its parameters. One can create one of these, pass it around and add params to it. Then,
 * whenever you need to fire a POST request, you can call the getUrl() and getParams() functions.
 */
dojo.declare("tavant.twms.summaryTable.Url", null, {
    _baseUrl: null,
    _params: {},
    _reader : null,

    constructor: function(baseUrl, params, reader) {
        if (!dojo.string.isBlank(baseUrl)) {
            this._baseUrl = baseUrl;
        } else {
            this._baseUrl = "";
        }

        if (params) {
            this._params = params;
        }

        this._reader = reader;
    },

    addParam: function(key, value) {
        this._params[key] = value;
    },

    /**
     * this is something very specific to SummaryTable... it reads the 'extraParamsVar' and 'extraParamsFunctions'
     * and adds all the variables to params.
     */
    handleExtraParams : function() {
        var extraParams = this._reader.getVar(CONSTANTS.extraParamsVar);
        if(extraParams == null) {
            return;
        }
        for(var i in extraParams) {
            this.addParam(i, extraParams[i]);
        }
    },

    handleExtraParamsFunctions : function() {
        var extraParamsFunctions = this._reader.getVar(CONSTANTS.extraParamsFunctions);
        if(extraParamsFunctions == null) {
            return;
        }
        var extraParamsFunctionArgsData = this._getSelectionData();
        if (extraParamsFunctionArgsData) {
            for (var i in extraParamsFunctions) {
                this.addParam(i, extraParamsFunctions[i](extraParamsFunctionArgsData));
            }
        }
    },

    _getSelectionData : function() {
        var table = this._reader.getVar(CONSTANTS.table);
        var selectionData = table.getCurrentSelection();
        var inProcessableState =
                selectionData && (table.bMultiSelect ? (getMapLength(selectionData) > 0) : selectionData.elem);
        var data = null;
        if (inProcessableState) {
            if (table.bMultiSelect) {
                data = new Array();
                for (var i in selectionData) {
                    data.push(selectionData[i].elem.rowData);
                }
            } else {
                data = selectionData.elem.rowData;
            }
        }
        return data;
    },

    _processExtraParams: function() {
        this.handleExtraParams();
        this.handleExtraParamsFunctions();
    },

    toPost: function() {
        this._processExtraParams();
        return this;
    },

    toGet: function() {
        this._processExtraParams();
        return this._baseUrl + "?" + dojo.objectToQuery(this._params);
    },

    getBaseUrl: function() {
        return this._baseUrl; //string
    },

    getParams: function() {
        return this._params;
    }
});

dojo.declare("tavant.twms.summaryTable.PaginatorDataStore", null, {
    _pages: [],
    _totalPages: 0,
    _isLoaded: false,
    _fetchItems: function(	/* Object */ keywordArgs,
                            /* Function */ findCallback,
                            /* Function */ errorCallback) {
        var matchingPages= [];
        if(this._isLoaded) {
            var prefix = keywordArgs.query.label;
            var prefixLength = prefix.length;
            if(prefix.charAt(prefixLength-1) == '*') {
                prefix = prefix.substring(0, prefixLength - 1);
            }

            dojo.forEach(this._pages, function(page) {
                if(dojo.string.startsWith(page.label, prefix)) {
                    matchingPages.push(page);
                }
            });
        }

        findCallback(matchingPages, keywordArgs);
    },

    getIdentity: function(/* item */ item){
        //	summary:
        //		See dojo.data.api.Identity.getIdentity()
        return item.id;
    },

    fetchItemByIdentity: function(/* Object */ keywordArgs){
        var item = this._isLoaded ? this._getItemByIdentity(keywordArgs.identity) : null;

        if(keywordArgs.onItem){
            var scope =  keywordArgs.scope ? keywordArgs.scope : dojo.global;
            keywordArgs.onItem.call(scope, item);
        }
    },

    _getItemByIdentity: function(/*object*/ identity) {
        if(identity > 0 && identity <= this._pages.length) {
            return this._pages[identity-1];
        } else {
            return null;
        }
    },

    isItemLoaded: function(/* anything */ something){
        return (typeof something != "undefined");
    },

    getValue: function(	/* item */ item,
                /* attribute-name-string */ attribute,
                /* value? */ defaultValue){
        //	summary:
        //		See dojo.data.api.Read.getValue()
        var values = this.getValues(item, attribute);
        return (values.length > 0) ? values[0]:defaultValue; // mixed
    },

    getValues: function(/* item */ item, /* attribute-name-string */ attribute) {
        //	summary:
        //		See dojo.data.api.Read.getValues()
        return [item[attribute]] || []; // Array
    },

     _loadItems: function() {
         var totalPages = this._totalPages;

         if(this._pages.length != totalPages) {
            this._pages = new Array(totalPages);
            for(var i = 1; i <= totalPages; i++) {
                this._pages[i-1] = {id : i, label: String(i)};
            }
        }

        this._isLoaded = true;
    },

    setTotalPages: function(/*int*/ totalPageCount) {
        this._totalPages = totalPageCount;
        this._loadItems();
    }
});

dojo.extend(tavant.twms.summaryTable.PaginatorDataStore, dojo.data.util.simpleFetch);

dojo.declare("tavant.twms.summaryTable.ValueReader", null, {

    nameSpace : "",

    constructor : function(/*String*/ nameSpace) {
        this.nameSpace = nameSpace;
    },

    getVar : function(/*String*/ varName) {
        return summaryTableVars[this.nameSpace][varName];
    }

});

function connectSummaryTableButtonCallback(/*String*/ summaryTableId,
                                           /*dijit.form.Button widget*/ button, /*Function obj*/ callback) {
    button.onClick = function(event) {
        var currentSelection = summaryTableVars[summaryTableId].table.getCurrentSelection();
        var dataToPass = new Array();
        if(summaryTableVars[summaryTableId].table.bMultiSelect) {//multiple selection is enabled
            for(var i in currentSelection) dataToPass.push(currentSelection[i].dataId);
        } else {//multiple selection is disabled.
            dataToPass.push(currentSelection.dataId);
        }
        callback(event, dataToPass);
    };
}

function fireJavaScriptRequestForSummaryTag(/*String (url)*/ url,/*Object (param map)*/ params, /*string*/ mimeType,
        /*Function*/ loadCallback, /*Function?*/ errorCallback,
        /*boolean*/ useGet) {

	if(this.xhrFunc){
		this.xhrFunc.cancel();

	}
	  
	  this.xhrFunc = useGet ? dojo.xhrGet : dojo.xhrPost({
            url : url,
            content: params,
            load : function(response) {
                loadCallback(response);
                return response;
            },
            error : function(response) {
                if (errorCallback) {
                	errorCallback('error', response);
                } else {
                	throw new Error("Ajax request failed : ", response);
                }
                return response;
                //FIXME: do something better here...
            },
            handleAs: mimeType
        });
    };