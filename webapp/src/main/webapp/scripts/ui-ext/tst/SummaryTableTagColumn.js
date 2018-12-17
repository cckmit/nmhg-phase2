/**
 * This is used by the class tavant.twms.SummaryTable.
 * @author janmejay.singh
 */ 

dojo.declare("tavant.twms.summaryTable.Column", null, {
    _reader : null,

    renderer : null,//renderer instance... which is used to render the column value.

    id : "",
	width : 0,
	label : "",
	alignment : "left",
	headerCell : null,
	bodyCell: null,
	filterInput: null,
	sortImageBox : null,
	labelDiv : null,
	table : null,
	wrapped : false,
	labelText : "",
    sortType : "",//can have value "asc"/"dsc", will be set in the constructor,
    disableSorting: true,

    STATIC_VARS : {
        DEFAULT_SORT_TYPE : "dsc",
        ASC_SORT : "asc",
        DESC_SORT : "dsc"
    },

    constructor : function(colData, table, reader) {
        this._reader = reader;

        this.id = colData.id;
		this.width = colData.width;
		this.label = colData.title;
		this.alignment = colData.alignment;
        this.renderer = new colData.rendererClass(this); 
        this.table = table;
        this.headerCell = dojo.byId(this._reader.getVar(CONSTANTS.prefixForTableHeadId) + this.id);
        this.bodyCell = dojo.byId(this._reader.getVar(CONSTANTS.prefixForTableBodyId) + this.id);
		this.filterInput = dojo.byId(this._reader.getVar(CONSTANTS.prefixForTableFilterId) + this.id);
		this.sortImageBox = dojo.byId(this._reader.getVar(CONSTANTS.prefixForSortImageDiv) + this.id);
		this.labelDiv = this._getLabelDiv();
		this.labelText = dojo.string.trim(new String(this.labelDiv.innerHTML));
		this.disableSorting = colData.disableSorting;
		
        if(!this.disableSorting) {
            dojo.connect(this.headerCell, "onclick", this, "sort");
        }
		
		if(this.filterInput) {
		dojo.connect(this.filterInput,"onkeyup", this, "filter");

        if(dojo.isIE) {//HACK: without this.... it won't render correctly in IE... some margin problems..
            var header = getExpectedParent(this.filterInput, "th");
            dojo.style(header, "paddingTop", 0 + "px");
            dojo.style(header, "paddingLeft", 0 + "px");
            dojo.style(this.filterInput, "marginTop", "-2px");
            dojo.style(this.filterInput, "marginLeft", "-1px");
            dojo.style(this.filterInput, "borderBottom", "none");
            delete header;
			}
        }
        this.sortType = this.STATIC_VARS.DEFAULT_SORT_TYPE;
    },
	
    filter : function(event) {
		if (!isIgnorableKeyStroke(event.keyCode)){
    		this.table.stillTyping();
            this.table.onFilter(event.target.getAttribute("field"), event.target.value);
		} 
	},
	
	sort : function(event) {
		var elem = event.target;
		// The attribute we are looking for is on the TH. The TH contains two DIVs. 
		// If the user had clicked on one of the DIVs we walk up the dom tree to get to the TH.
        var hadSort = this.sortType;
        if(!event.ctrlKey) {
            this.table.resetSort();
        }
        elem = getExpectedParent(elem, "th");
        this.sortType = (hadSort == this.STATIC_VARS.ASC_SORT) ? this.STATIC_VARS.DESC_SORT : this.STATIC_VARS.ASC_SORT;
        this.table.onSort(this, elem.getAttribute("field"), this.sortType);
        (this.sortType == this.STATIC_VARS.ASC_SORT) ? this.markSortedUp() : this.markSortedDown();
        delete elem;
    },
	
	getWidth : function() {
		return this.width;
	},
	
	setWidth : function(width) {
		this.width = width;
	},
	
	setHeaderWidth : function(width) {	
	if(parseInt(width)<0)
	{
	   width=0;	
	   }
		dojo.style(this.headerCell, 'width', width);
	},
	
	setBodyWidth : function(width) {
	if(parseInt(width)<0)
	{
	   width=0;	
	   }
		dojo.style(this.bodyCell, "width", width);
	},
	
	getHeadX : function() {
		return dojo.marginBox(this.headerCell).l;
	},
	
	getHeadY : function() {
		return dojo.marginBox(this.headerCell).t;
	},
	
	getBodyX : function() {
		return dojo.marginBox(this.bodyCell).l;
	},
	
	getBodyY : function() {
		return dojo.marginBox(this.bodyCell).l;
	},
	
	markSortedUp : function() {
		dojo.addClass(this.sortImageBox, "sortedColumnHeadUp");
        dojo.removeClass(this.sortImageBox, "sortedColumnHeadDown");
    },
	
	markSortedDown : function() {
		dojo.addClass(this.sortImageBox, "sortedColumnHeadDown");
        dojo.removeClass(this.sortImageBox, "sortedColumnHeadUp");
    },
	
	getId : function() {
		return this.id;
	},
	
	removeSort : function() {
		dojo.removeClass(this.sortImageBox, "sortedColumnHeadUp");
		dojo.removeClass(this.sortImageBox, "sortedColumnHeadDown");
        this.sortType = this.STATIC_VARS.DEFAULT_SORT_TYPE;
    },
	
	getLabel : function() {
		return this.label;
	},
	
	getAlignment : function() {
		return this.alignment;
	},
	
	getHeaderCell : function() {
		return this.headerCell;
	},

    _getLabelDiv : function() {
        return dojo.query("td:last-child",
                   dojo.query("tr:last-child",
                       dojo.query("tbody",
                           dojo.query("table:last-child", this.headerCell)[0])[0])[0])[0];
    }
});

dojo.declare("tavant.twms.summaryTable.DefaultCellRenderer", null, {

    _column : null,

    constructor : function(/*Object [Column]*/ column) {
        this._column = column;
    },

    render : function(/*cell*/ td, /*data(to be rendered)*/ value) {
        td.innerHTML = value;
        td.title = value;
    }
});