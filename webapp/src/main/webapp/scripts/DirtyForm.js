dojo.declare("tavant.twms.DirtyFormManager", null, {
	
	aFormHandlers : new Array(),
	bOverallClean : true,
	initialLabel : /*String... holds initial label of the tab*/ "",
	tab : /*Tab widget*/ null,
	
	constructor : function(/*Widget tab*/ tabId) {
		this.tab = getTabHavingId(tabId);
		this.initialLabel = this.tab.label;
		dojo.subscribe("/formStateChange", this, "_makeDirtyCheck");
	},
	
	registerHandler : function(/*FormHandler instance*/ handle) {
		for(var i = 0; i < this.aFormHandlers.length; i++) {
			if(this.aFormHandlers[i].id == handle.id) {
				return;
			}
		}
		this.aFormHandlers.push(handle);
	},
	
	_makeDirtyCheck : function() {
		this.bOverallClean = true;
		for(var i = 0; i < this.aFormHandlers.length; i++) {
			if(this.aFormHandlers[i].bClean == false) {
				this.bOverallClean = false;
				break;
			}
		}
		if(this.bOverallClean) {
			this._markClean();
		} else {
			this._markDirty();
		}
	},
	
	_markDirty : function() {
		parent.publishEvent("/tab/changeLabel", {tab: this.tab,
												 newLabel: (this.initialLabel + "*")});
	},
	
	_markClean : function() {
		if(dojo.string.endsWith(this.initialLabel, "*")) {
			this.initialLabel = this.initialLabel.substring(0, this.initialLabel.length - 1);
		}
		parent.publishEvent("/tab/changeLabel", {tab: this.tab,
												 newLabel: this.initialLabel});
	}
});

dojo.declare("tavant.twms.FormHandler", null, {
	
	bClean : true,
	id : /*String formId*/ null,
	
	constructor : function(/*DomNode*/ form) {
		this.id = form.id;
		for(var j = 0; j < form.elements.length; j++) {
			if(form.elements[j].getAttribute("readonly")) {
				dojo.connect(form.elements[j], "onchange", this, "_markDirty");
				dojo.connect(form.elements[j], "onkeyup", this, "_markDirty");
				if(dojo.isIE) {//HACK: IE doesn't fire onchange on actualy changing the field, it fires it on onblur, this fixed the problem by using onclick.
					dojo.connect(form.elements[j], "onclick", this, "_markDirtyIfCkeckboxOrRadioButton");
				}
			}
		}
	},
	
	registerResetButton : function(/*DomNode*/ resetButton) {
		dojo.connect(resetButton, "onclick", this, "_markClean");
	},
	
	registerSubmitButton : function(/*DomNode*/ submitButton) {
		dojo.connect(submitButton, "onclick", this, "_markDirty");
	},
	
	_markDirty : function(event) {
		if(event && event.keyCode) {
			if(isIgnorableKeyStroke(event.keyCode, true)) {
				return;
			}
		}
		this.bClean = false;
		this._publishFormStateChangeEvent();
	},
	
	_markClean : function() {
		this.bClean = true;
		this._publishFormStateChangeEvent();
	},
	
	_publishFormStateChangeEvent : function() {
		dojo.publish("/formStateChange");
	},
	
	_markDirtyIfCkeckboxOrRadioButton : function(event) {
		if(event.target.type == "checkbox" || event.target.type == "radio" ) {
			this._markDirty();
		}
	}
});