dojo.require("dijit.form.FilteringSelect");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("twms.widget._ValidationTextBoxMixin");

dojo.provide("twms.widget.Select");

dojo.declare("twms.widget.Select", [dijit.form.FilteringSelect, twms.widget._ValidationTextBoxMixin], {

    initialKey: "",
    initialValue: "",
    ignoreCase: true,
    notifyTopics: "",
    listenTopics: "",
    initialData: null,
    fireOnLoadOnChange:true,
    legacyDataMode: false, // Turn on to support data in the 0.4-style [[label, key]....] format while replacing store.
    sendDisplayedValueOnChange: true,
    autoComplete: false,
    defaultSelectFirstValue: false,
    autoSelectIfSingleResult: false,
    disableIfNoResult: false,
    autoSelectFirstValueOnTab: true,

    //****************** Private Vars. ******************

    _notifyTopicsArray: [],
    _url: null,

    //***************************************************

    postCreate: function() {
        this._processNotifyTopics();

        this._processListenTopics();

        this.inherited("postCreate", arguments);

        this._adjustValidationStyle(true); // Hide the validation icon "properly" (no padding, visible space etc.)

        if(this.store) {
            this._url = this.store.url;
        }

        if(this.initialData) {
            this._reloadUsingItems(this.initialData);
        }

        this._processAndSetInitialValue();
    },

    addNotifyTopic: function(/*string*/topic) {
        this._notifyTopicsArray.push(topic);
    },

    addListenTopic: function(/*string*/topic) {
        dojo.subscribe(topic, this, "_handleMessageFromListenTopic");
    },

    onChange: function(/*object*/ newValue) {
        var valueToBeSent = this.sendDisplayedValueOnChange ? this.get('displayedValue') : newValue;
        this._notify(valueToBeSent, "valuechanged", this);
    },
    
    onDataLoad: function(){
    //Empty handle to end of topic listening
    },
    
    defaultSelectFirstValueIfAny : function() {
        var store = this.store;
        if(!store._loadFinished) {
            store._fetchItems({}, function(items, requestArgs) {}, function(e, keywordArgs){});
        }

        var items = store._arrayOfAllItems;
        
        if(items.length > 0) {
            var value = items[0].key;
            this.setValue(value);       
        }
    },
    
    selectFirstValueIfOnlyOneElement : function() {
        var store = this.store;
        if(!store._loadFinished) {
            store._fetchItems({}, function(items, requestArgs) {}, function(e, keywordArgs){});
        }

        var items = store._arrayOfAllItems;
        
        if(items.length == 1) {
            var value = items[0].key;
            this.setValue(value);       
        }
    },
    
    disabledIfNoElement : function() {
        var store = this.store;
        if(!store._loadFinished) {
            store._fetchItems({}, function(items, requestArgs) {}, function(e, keywordArgs){});
        }

        var items = store._arrayOfAllItems;       
        if(items.length == 0) {
            this.setValue("");
            this.setDisplayedValue("");
            twms.util.adjustVisibilityAndSubmission(this.domNode,false,true);  
        }
        else{
            twms.util.adjustVisibilityAndSubmission(this.domNode,true,true);
        	
        }
    },

    //****************** Private Methods. ******************

    _processNotifyTopics: function() {
        if(!dojo.string.isBlank(this.notifyTopics)) {
            this._notifyTopicsArray = twms.string.toArray(this.notifyTopics, ",", true);
        }
    },

    _processListenTopics: function() {
        if(!dojo.string.isBlank(this.listenTopics)) {
         	
            var _listenTopicsArray = twms.string.toArray(this.listenTopics, ",", true);
            dojo.forEach(_listenTopicsArray, function(topic) {
                dojo.subscribe(topic, this, "_handleMessageFromListenTopic");
            }, this);
        }
    },

    _processAndSetInitialValue: function() {
        if(dojo.string.isBlank(this.initialValue)) {
            if(!dojo.string.isBlank(this.value)) {
                this.initialKey = this.initialValue = this.value;
                this.value = "";
            }
        } else if(dojo.string.isBlank(this.initialKey)) {
            this.initialKey = this.initialValue;
        }

        if(!dojo.string.isBlank(this.initialValue)) {
            if(this.store._isAutoCompleteStore) {
                this._resetUsingLabelAndValue(this.initialValue, this.initialKey);
            }

            dojo.addOnLoad(dojo.hitch(this, function() {  
                if(this.fireOnLoadOnChange){
//                    this._resetUsingLabelAndValue(this.get('displayedValue'),this.get('value'), true); // this required so that on change on select widget is fired
                    this.onChange(this.get('value'));
                }// Since onChange is not fired for value set on load of Select.
            }));
        }
    },

    _notify : function(value, type, e) {
        if(this._notifyTopicsArray) {
            dojo.forEach(this._notifyTopicsArray, function(topic) {
                dojo.publish(topic, [value, type, e]);
            }, this);
        }
    },

    _resetUsingLabelAndValue: function(/*String*/ label, /*String*/ value, /*boolean*/ fireOnChange){
        this.store._setItem({
            key: value,
            label: label
        });

        this.set('value', value, fireOnChange, label, {key: value,label: label}); // dont fire onChange.
    },

    _handleMessageFromListenTopic : function(message) {

        var makeLocal = message.makeLocal;
        var url = message.url;
        var params = message.params;
        this.defaultSelectFirstValue = message.defaultSelectFirstValue;
        this.autoSelectIfSingleResult = message.autoSelectIfSingleResult;
        this.disableIfNoResult = message.disableIfNoResult;

        var store = this.store;

        var itemToAdd = message.addItem;
        if(itemToAdd) {
            if(store._isAutoCompleteStore) {
                this._resetUsingLabelAndValue(itemToAdd.label, itemToAdd.key, true);
            } else if(store.newItem) {
                if(this.legacyDataMode) {
                    itemToAdd = this._createComboItemFromArray(itemToAdd);
                }

                store.newItem(itemToAdd);
                this.setValue(itemToAdd.key);
                return;
            } else {
                throw new Error("twms Select [" + this.id +  "]: My store doesn't support dynamic addition of items.");
            }
        }

        if(!makeLocal) {
            if(params) {
                dojo.mixin(this.query, message.params);
            }

            if(url) {
                store.url = url;
            }

            if(params|| url) {
                this._setValueAfterStoreModification();
            }
            
            return;
        }

        var items;
        var finalParams = {
            searchPrefix: ""
        }
        finalParams[this.name] = "";

        if(message.items) {
            items = message.items;
            this._reloadUsingItems(items);
        } else {
            if(!url) {
                url = this._url;
            }

            if(params) {
                dojo.mixin(finalParams, params);
            }

            this._reloadUsingUrl(url, finalParams);
        }

    },

    filterItems: function(/*v0.4 2d array or v1.x json obj*/items) {
        return items; // no-op; clients or child classes should override as required.
    },

    _reloadUsingUrl: function(/*string*/ url, /*object*/ params) {
        twms.ajax.fireJsonRequest(url, params, dojo.hitch(this, function(items) {
            this._reloadUsingItems(items);
            this._url = url;
        }));
    },

    _reloadUsingItems: function(/*v0.4 2d array or v1.x json obj*/items) {
        var processedItems = this.filterItems(items);

        if(this.legacyDataMode) {
            processedItems = this._createComboDataFromArray(processedItems);
        }

        this._replaceStore({
            data: processedItems
        });
    },

    _replaceStore: function(/*object*/ storeArgs) {
        delete this._lastValueReported;
        delete this.store;
        this.searchAttr = "label";

        this.store = new dojo.store.Memory({ data: storeArgs.data });
        this._setValueAfterStoreModification();
        this.onDataLoad();

    },

    _setValueAfterStoreModification: function() {
        if(this.initialValue && this.initialKey) {
            this.set('value',this.initialKey, false, this.initialValue); // dont fire onChange.
        }else if(this.initialValue) {
            this.set('value','', false, this.initialValue, {key: '',label: this.initialValue}); // dont fire onChange.
        } else if(this.initialKey) {
            this.set('value',this.initialKey, false); // dont fire onChange.
        } else if(this.autoSelectIfSingleResult) {
            this.selectFirstValueIfOnlyOneElement();
        } else if(this.defaultSelectFirstValue) {
            this.defaultSelectFirstValueIfAny();
        } else {
            if(this.textbox)
                this.textbox.value = "";
            this.valueNode.value = "";
        }
        
        if(this.disableIfNoResult){
            this.disabledIfNoElement();
        }

        this.initialValue = this.initialKey = this.value = null;
    },

    _createComboDataFromArray: function(/*array*/ dataArray) {
        var comboData = {
            identifier: "key",
            items: []
        };

        dojo.forEach(dataArray, function(item) {
            comboData.items.push(this._createComboItemFromArray(item));
        }, this);

        return comboData;
    },

    _createComboItemFromArray: function(/*array*/ dataItem) {
        return {
            label : dataItem[0],
            key : dataItem[1]
        };
    },

    // For the case where the user uses arrow keys to select an option.
    _announceOption: function(/*Node*/ node) {
        this.store._optionSelectedByUser = node.item;
        this.inherited("_announceOption", arguments);
    },

    // For the case where the user selects an option by clicking on it.
    _selectOption: function(/*Event*/ evt){
        var item = dojo.getObject("target.item", false, evt);
        if(item) {
            this.store._optionSelectedByUser = item;
        }
        this.inherited("_selectOption", arguments);
    },
    // Copied the following code from FilteringSlect and added a check if first value should be selected or not
    _callbackSetLabel: function(
        /*Array*/ result,
        /*Object*/ query,
        /*Object*/ options,
        /*Boolean?*/ priorityChange){
        // summary:
        //		Callback from dojo.store after lookup of user entered value finishes

        // setValue does a synchronous lookup,
        // so it calls _callbackSetLabel directly,
        // and so does not pass dataObject
        // still need to test against _lastQuery in case it came too late
        if((query && query[this.searchAttr] !== this._lastQuery) || (!query && result.length && this.store.getIdentity(result[0]) != this._lastQuery)){
            return;
        }
        if(!result.length){
            //#3268: don't modify display value on bad input
            //#3285: change CSS to indicate error
            this.set("value", '', priorityChange || (priorityChange === undefined && !this.focused), this.textbox.value, null);
        }else if(result.length == 1 && this.autoSelectIfSingleResult){
            this.set('item', result[0], priorityChange);
        }        
        else if(this.autoSelectFirstValueOnTab){
            this.set('item', result[0], priorityChange);
        }
    }

});