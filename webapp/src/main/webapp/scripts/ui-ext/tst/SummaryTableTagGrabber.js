/**
 * This is used by the class tavant.twms.SummaryTable.
 * @author janmejay.singh
 */

dojo.declare("tavant.twms.summaryTable.Grabber", null, {
	
	domNode : null,
	leftColumnIndex : 0,
	rightColumnIndex : 0,
	inDrag : false,
	table: null,
	
	constructor : function(parentTable, index, offset) {
		this.table = parentTable;
		this.domNode = document.createElement("div");
        this.domNode.className = "columnGrabber";
        this.setColumnIndex(index);
		this.setX(Math.round(offset * 100) + "%");
		this.attachMouseEvents();
		  if(dojo.isChrome){
	            dojo.style(this.domNode,"top","50px");
	        }
	},
	
	attachMouseEvents : function() {
		dojo.connect(this.domNode, "onmousedown", this, "onMouseDown");
		dojo.connect(this.domNode, "onmouseup", this, "onMouseUp");
		dojo.connect(this.domNode, "onmousemove", this, "onMouseMove");															
	},
	
	onMouseDown : function(event) {
		var grabber = this.domNode;

		if (this.isInDrag()) {
			grabber.onmouseup(event);
		}

		this.setInDrag(true);

		grabber.totalWidth = document.body.clientWidth;

		// figure out how much we can go to the left and right
		grabber.minOffset = 0;
		for (var i=0; i < this.getLeftColumnIndex(); i++) {
			grabber.minOffset += this.table.getColumnWidth(i);
		}
		grabber.maxOffset = grabber.minOffset +
					 this.table.getColumnWidth(this.getLeftColumnIndex()) +
					 this.table.getColumnWidth(this.getRightColumnIndex());
		grabber.minOffset += this.table.minColumnWidth;
		grabber.maxOffset -= this.table.minColumnWidth;

		// widen the div so that the mouse doesn't escape even when moving quickly
		this._saveGrabberData();
        this._widenGrabber();

        this.onMouseMove(event);
        delete grabber;
		return true;
	},

	onMouseUp : function() {
		this.setInDrag(false);

		this._restoreGrabberData();
		this.table.renderColumns(true);
		return true;
	},

	onMouseMove : function(event) {
		if (! this.isInDrag()) {
			return true;
		}
		var newOffset = event.clientX/this.domNode.totalWidth;

		if (newOffset < this.domNode.minOffset || newOffset > this.domNode.maxOffset)  {
			return;
		}

		var leftColumnOffset = 0;
		for (var i=0; i < this.getLeftColumnIndex(); i++) {
			leftColumnOffset += this.table.getColumnWidth(i);
		}
		var rightColumnOffset = leftColumnOffset +
			this.table.getColumnWidth(this.getLeftColumnIndex()) +
			this.table.getColumnWidth(this.getRightColumnIndex());

		this.table.setColumnWidth(this.getLeftColumnIndex(), newOffset - leftColumnOffset);
		this.table.setColumnWidth(this.getRightColumnIndex(), rightColumnOffset - newOffset);
		this._correctGrabberToNewOffset(newOffset);
		//this.table.renderColumns(false);//this has been commented out, in order to avoid resizing of table head
		//when grabber moves. now the head and the body both resize in one shot when the grabber is dropped(after resize).
		return true;
	},

	_saveGrabberData : function() {
		this.domNode.savedWidth = dojo.style(this.domNode, "width") + "px";
        this.domNode.savedMarginLeft = dojo.style(this.domNode, "marginLeft") + "px";
		this.domNode.savedLeft = dojo.style(this.domNode, "left") + "px";
		this.domNode.savedBackgroundPosition = this.domNode.style.backgroundPosition;
		// NOT
		// grabber.savedBackgroundPosition = dojo.style(grabber, "backgroundPosition");
		// which doesnt seem to work in IE.
	},

	_restoreGrabberData : function() {
		dojo.style(this.domNode, "width", this.domNode.savedWidth);
		dojo.style(this.domNode, "marginLeft", this.domNode.savedMarginLeft);
		this.domNode.style.backgroundPosition = this.domNode.savedBackgroundPosition;//NOT TO BE ALTERED... else it won't work in IE
		dojo.style(this.domNode, "left", this.domNode.savedLeft);
	},

	_widenGrabber : function() {
		dojo.style(this.domNode, "width", "100%");
		dojo.style(this.domNode, "marginLeft", "0px");
		dojo.style(this.domNode, "left", "0px");
	},

	_correctGrabberToNewOffset : function(newOffset) {
		this.domNode.style.backgroundPosition = Math.round(newOffset * 100) + "% 0%";//NOT TO BE ALTERED... else it won't work in IE.
		this.domNode.savedLeft = Math.round(newOffset * 100) + "%";
	},
		
	setX : function(xValue) {
		dojo.style(this.domNode, "left", xValue);
	},
	
	getX : function() {
		return dojo.coords(this.domNode).l;
	},
	
	setY : function(yValue) {
		dojo.style(this.domNode, "top", yValue);
	},
	
	getY : function() {
		return dojo.coords(this.domNode).t;
	},
	
	setColumnIndex : function(index) {
		this.setLeftColumnIndex(index);
		this.setRightColumnIndex(index + 1);
	},
	
	setLeftColumnIndex : function(index) {
		this.leftColumnIndex = index;
	},
	
	setRightColumnIndex : function(index) {
		this.rightColumnIndex = index;
	},
	
	setInDrag : function(inDrag) {
		this.inDrag = inDrag;
	},
	
	getLeftColumnIndex : function() {
		return this.leftColumnIndex;
	},
	
	getRightColumnIndex : function() {
		return this.rightColumnIndex;
	},
	
	isInDrag : function() {
		return this.inDrag;
	},
	
	setHeight : function(height) {
		dojo.style(this.domNode, "height", height);
	},
	
	getHeight : function() {
		dojo.style(this.domNode, "height");
	}
});