dojo.require("twms.widget.Select");

/**
 * Please include this file where common resource management is required.
 * @author : janmejay.singh
 */

dojo.declare("twms.resources.ActionObject", null, {

    COUNTER : {//makes sure.. everytime... a new instance of ActionObject gets a unique ID.... to be used for comparision
        count : 0
    },

    id : 0,//just serves the perpose of comparision.

    treeNode : null,

    resourceType : "",

    node : null,

    validValueSet : function(/*value*/ value) {},

    invalidValueSet : function(/*label*/ label) {},

    dataProvider : null,//object<? extends twms.spTree.AbstractComboBoxDataProvider>

    getValue : function() {return null;},

    preReleaseCallback : function() {},

    constructor : function(/*JSON tree node ("Assembly"/"ServiceProcedure" instance)*/treeNode,
                           /*String (ResourceType)*/ resourceType, /*domNode*/ node, /*Function*/ getValue,
                           /*Function*/ preReleaseCallback, /*Function*/ validValueSet, /*Function?*/ invalidValueSet,
                           /*DataProvider?*/ dataProvider) {
        this.treeNode = treeNode;
        this.resourceType = resourceType;
        this.node = node;
        this.getValue = getValue;
        this.preReleaseCallback = preReleaseCallback;
        this.validValueSet = validValueSet;
        if(invalidValueSet) {
            this.invalidValueSet = invalidValueSet;
        }

        this.dataProvider = dataProvider;
        this.id = this.COUNTER.count++;
    }
});

dojo.declare("twms.resources.AbstractResource", null, {

    _resourceType : "AbstractResource",

    _actionObject : null,

    _bufferedValue : null,//this holds the value that user has choosen... but hasn't accepted
    _bufferedValueIsInvalid : false,//is buffered value invalid???

    constructor : function() {/*doing nothing here*/},

    getType : function() {
        return this._resourceType;
    },

    focus : function() {
        //classes extending this guy shd override this, and shd focus on there base element.
    },

    bind : function(/*ActionObject*/ actionObj) {
        this._actionObject = actionObj;
        this._bufferedValue = this._actionObject.getValue();
    },

    unbind : function() {
        this._actionObject.preReleaseCallback();
        this._actionObject = null;
    },

    _supressEvent : function(event) {
        dojo.stopEvent(event);
    },

    setValue : function(/*boolean*/ useBufferedValue) {
        if (useBufferedValue) {
            if (this._bufferedValueIsInvalid) {
                this._actionObject.invalidValueSet(this._bufferedValue,
                        dojo.hitch(this._actionObject, this._actionObject.validValueSet));
            } else {
                this._actionObject.validValueSet(this._bufferedValue);
            }
        } else {
            this._actionObject.validValueSet(this.getBaseValue());
        }
    },

    getBaseValue : function() {
        return this._actionObject.getValue();
    },

    _setValidBufferedValue : function(value) {
        this._setBufferedValue(value, false);
    },

    _setInvalidBufferedValue : function(value) {
        this._setBufferedValue(value, true);
    },

    _setBufferedValue : function(value, /*boolean(is invalid)*/ isInvalid) {
        this._bufferedValue = value;
        this._bufferedValueIsInvalid = isInvalid;
    },

    // to be overridden
    getDomNode : function() {
        return null;
    },

    getActionObject : function() {
        return this._actionObject;
    }
});

dojo.declare("twms.resources._ComboBox", twms.resources.AbstractResource, {

    _comboBox : null,
    _comboBoxListenTopic: null,
    _resourceType : "ComboBox",
    _lastSeenUrl: null,
    _lastSeenParams: null, // flattened JSON object

    constructor : function() {
        this._comboBox = new twms.widget.Select({
            autoComplete : false,
            legacyDataMode: true,
            pageSize:20,
            store: new dojo.store.Memory()
        });

        var combo = this._comboBox;

        var comboBoxValidationTopic = "/treeTable/comboBoxValidation_" + combo.id;

        dojo.subscribe(comboBoxValidationTopic, this, function(validationStatus) {
            if(!validationStatus.isValid) {
               this._setInvalidBufferedValue(validationStatus.displayedValue);
            }
        });

        combo.setValidationNotificationTopics(comboBoxValidationTopic);

        this._comboBoxListenTopic = "/treeTable/comboBoxListen_" + combo.id;

        combo.addListenTopic(this._comboBoxListenTopic);

        dojo.connect(combo, "onChange", this, function(newValue) {
            this._setValidBufferedValue({
                value: newValue,
                label: combo.getDisplayedValue()
            });
        });
    },

    focus : function() {
        this._comboBox.focus();
    },

    bind : function(actionObject) {
        this.inherited("bind", arguments);
        var dataProvider = actionObject.dataProvider;

        var combo = this._comboBox;

        combo.textbox.value = "";

        dojo.connect(combo, "onDataLoad", this, function() {
            combo.textbox.value = "";
            combo.setDisabled(false);
        });

        // If the combobox has not been initialized even once or if it is marked to be reloaded for every bind,
        // reload it with data.
        if(this._reloadRequired(dataProvider)) {
            combo.textbox.value = "Loading...";
            combo.setDisabled(true);
            combo.filterItems = dojo.hitch(dataProvider, dataProvider.filterAndParse);

            var message = {
                makeLocal: true
            };

            if(dataProvider.initialData) {
                message.items = dataProvider.initialData;
            } else {
                message.url = dataProvider.url;
                message.params = dataProvider.queryParams;
            }

            combo.initialValue = this._actionObject.getValue().label; // Wud be used after the store has been replaced.

            dojo.publish(this._comboBoxListenTopic, [message]);
        } else {
            combo.setValue(this._actionObject.getValue().value);
        }
    },

    unbind : function() {
        this.inherited("unbind", arguments);
    },

    getDomNode : function() {
        return this._comboBox.domNode;
    },

    toString : function() {
        return "Object : twms.resources._ComboBox";
    },

    _reloadRequired: function(dataProvider) {
        return true;
        /*if(!this._comboBox.store) {
            return true;
        }

        return dataProvider.reloadOnBind &&
               (this._lastSeenUrl != dataProvider.url || this._lastSeenParams != dataProvider.queryParams);*/
    }
});

dojo.declare("twms.resources.FreeText", twms.resources.AbstractResource, {

    _freeText : null,

    _resourceType : "FreeText",

    constructor : function() {
        this._freeText = document.createElement("input");
        dojo.style(this._freeText, "width", "45px");
        dojo.connect(this._freeText, "onkeyup", this, "_freeTextValueSet");
        dojo.connect(this._freeText, "onclick", this, "_supressEvent");
    },

    focus : function() {
        this._freeText.focus();
    },

    _freeTextValueSet : function() {
        this._setValidBufferedValue(this._freeText.value);
    },

    bind : function(actionObject) {
        this.inherited("bind", arguments);
        this._freeText.value = this._actionObject.getValue();
    },

    unbind : function() {
        this._freeText.value = "";
        this.inherited("unbind", arguments);
    },

    getDomNode : function() {
        return this._freeText;
    },

    toString : function() {
        return "Object : twms.resources.FreeText";
    }
});

dojo.declare("twms.resources.ResourceRepository", null, {

    _wrapperManager : null,
    _resourceCollection : null,

    constructor : function(/*WrapperManager*/ wrapperManager, /*Collection of AbstractResource Classes*/ resourceClasses) {
        this._wrapperManager = wrapperManager;
        this._resourceCollection = new Array();
        for (var i in resourceClasses) {
            this._resourceCollection.push(new resourceClasses[i]());
        }
    },

    allocateResource : function(/*ActionObject*/ actionObject) {
        var resourceType = actionObject.resourceType;
        for (var i in this._resourceCollection) {
            if (this._resourceCollection[i].getType() === resourceType) {
                this._wrapperManager.releaseIfLocked();
                this._resourceCollection[i].bind(actionObject);
                this._wrapperManager.wrap(this._resourceCollection[i]);
            }
        }
    }
});

dojo.declare("twms.resources.WrapperManager", null, {

    _wrapper : null,//wrapper is the domNode that holds other domNodes such as component holder and stuff.
    _componentHolder : null,//wrapper's component holder domNode
    _bufferedValue : null,//this variable holds the value that was selected in the widget(but is not allocated yet).

    _resource : null,//the current component. if this is not null, it means its wrapped around some resource

    /**
     * Wrapper CSS map shd be in this format :
     * {
     *  COMPONENT_HOLDER : "componentHolderCssClass",
     *  ACCEPT_BUTTON : "accpetButtonCssClass",
     *  RESET_BUTTON : "resetButtonCssClass"
     * }
     */
    constructor : function(/*String*/ wrapperMarkup, /*Map*/ wrapperCssMap) {
        this._wrapper = dojo.html.createNodesFromText(wrapperMarkup, true);
        this._componentHolder = getElementByClass(wrapperCssMap.COMPONENT_HOLDER, this._wrapper);
        dojo.connect(getElementByClass(wrapperCssMap.ACCEPT_BUTTON, this._wrapper), "onclick", this, "onAccept");
        dojo.connect(getElementByClass(wrapperCssMap.RESET_BUTTON, this._wrapper), "onclick", this, "onReset");
        dojo.connect(this._wrapper, "onkeyup", this, "_handleKeyEvents");
    },

    _handleKeyEvents : function(event) {
        switch(event.keyCode) {
            case 13 : //means RETURN
                this.onAccept();
                break;
            case 27 : //means ESC
                this.onReset();
                break;
        }
    },

    _isLocked : function() {
        return (this._resource != null);
    },

    releaseIfLocked : function() {
        if (this._isLocked()) {
            this.onAccept();
        }
    },

    wrap : function(/*AbstractResource*/ resource) {
        this._resource = resource;
        this._componentHolder.appendChild(this._resource.getDomNode());
        var hostNode = resource.getActionObject().node;
        dojo.dom.removeChildren(hostNode, true);
        hostNode.innerHTML = "";
        // To fix the issue of the width getting set to zero, in IE.
        dojo.style(this._componentHolder, "width", "100%");
        hostNode.appendChild(this._wrapper);
        this._resource.focus();
    },

    /**
     * unbinds all the functions... and detaches the resource from the wrapper.
     */
    _unwrap : function() {

        if(this._resource._resourceType === SP_TREE_VARS.RESOURCE_TYPE.COMBO_BOX) {
            // remove the combobox's domNode, so that it doesnt get destroyed along with the other children of the host.
            dojo.dom.removeNode(this._resource.getDomNode());
        }

        dojo.dom.removeChildren(this._componentHolder, true);
        dojo.dom.removeNode(this._wrapper, true);

        this._resource.unbind();
        this._resource = null;
    },

    _setValueAndUnwrap : function(useBufferedValue) {
        if (this._resource) {
            this._resource.setValue(useBufferedValue);
            this._unwrap();
        }
    },

    onAccept : function(event) {
        this._smudgeEvent(event);
        this._setValueAndUnwrap(true);
    },

    onReset : function(event) {
        this._smudgeEvent(event);
        this._setValueAndUnwrap(false);
    },

    _smudgeEvent : function(event) {
        if (event) {
            dojo.stopEvent(event);
        }
    }
});

dojo.declare("twms.resources.ResourceManager", null, {

    _resourceRepository : null,
    _wrapperManager : null,

    constructor : function() {
        this._wrapperManager = new twms.resources.WrapperManager(commonResource_componentWrapper.markup,
                                                        RESOURCE_MANAGER_CSS_CLASSES);
        var resourceClasses = [twms.resources._ComboBox, twms.resources.FreeText];
        this._resourceRepository = new twms.resources.ResourceRepository(this._wrapperManager, resourceClasses);
    },

    requestEditor : function(/*ActionObject*/ actionObj) {
        var activeResource = this._wrapperManager._resource;
        var activeActionObj = activeResource ? activeResource.getActionObject() : null;

        if(activeActionObj && activeActionObj.id == actionObj.id) {
            return;
        }
        
        this._resourceRepository.allocateResource(actionObj);
    },

    releaseHost : function() {
        this._wrapperManager.onReset();
    }
});
