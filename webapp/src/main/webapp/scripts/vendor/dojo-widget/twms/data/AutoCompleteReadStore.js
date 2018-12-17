
dojo.require("dojox.data.QueryReadStore");
dojo.provide("twms.data.AutoCompleteReadStore");

dojo.declare("twms.data.AutoCompleteReadStore", dojox.data.QueryReadStore, {

    _optionSelectedByUser: null,
    searchPrefixMinimumLength: 3,
    _isAutoCompleteStore: true,
    searchPrefixParamAlias: "",
    _searchPrefixParamAliasSet: false,
    includeSearchPrefixParamAlias:true,

    constructor: function(params) {
        twms.data.AutoCompleteReadStore.superclass.constructor.apply(this, arguments);
        if(params.searchPrefixMinimumLength > 0) {
            this.searchPrefixMinimumLength = params.searchPrefixMinimumLength;
        }

        if(params.includeSearchPrefixParamAlias != null){
            this.includeSearchPrefixParamAlias =  params.includeSearchPrefixParamAlias;
        }

        if (this.includeSearchPrefixParamAlias) {
            // We are storing it as a member var, to avoid the isBlank call happening every time in fetch(..).
            this._searchPrefixParamAliasSet = !dojo.string.isBlank(params.searchPrefixParamAlias);
            if (this._searchPrefixParamAliasSet) {
                this.searchPrefixParamAlias = params.searchPrefixParamAlias;
            }
        }
    },

    _setItem : function(/*object*/ item) {
        var wrappedItem = {
            i: {
                key: item.key,
                label: item.label
            },
            r:this
        };
        this._items = [wrappedItem];
        this._itemsByIdentity = {};
        this._itemsByIdentity[item.key] = wrappedItem;
        this._identifier = "key";
        this._lastServerQuery = {
            searchPrefix: item.label.toUpperCase()
        }
    },

    fetchItemByIdentity: function(/*object*/ keyWordArgs) {
        if(dojo.string.isBlank(keyWordArgs.identity)) {
            return;
        }

        return this.inherited("fetchItemByIdentity", arguments);
    },

    fetch:function(request) {
        var searchPrefix = this.formatSearchPrefix(request.query.label);
        if (this.shouldFetchItems(searchPrefix)) {
            request.serverQuery = {
                searchPrefix: searchPrefix, // Just to make life easy for the action
                start:request.start,
                count:request.count
            };

            if(this.includeSearchPrefixParamAlias && this._searchPrefixParamAliasSet) {
                request.serverQuery[this.searchPrefixParamAlias] = searchPrefix;
            }

            return twms.data.AutoCompleteReadStore.superclass.fetch.apply(this, [request]);
        } else {
            var scope = request.scope || dojo.global;
            request.onComplete.call(scope, [], request);
            return request;
        }
    },

    formatSearchPrefix : function(/*string*/ searchPrefix) {
        var lastIndex = searchPrefix.length - 1;
        if(searchPrefix.charAt(lastIndex) === "*") {
            searchPrefix = searchPrefix.substring(0, lastIndex);
        }

        return searchPrefix.toUpperCase();
    },

    shouldFetchItems : function(/*string*/ searchPrefix) {
        return searchPrefix.length >= this.searchPrefixMinimumLength;
    },

    _fetchItems: function(request, fetchHandler, errorHandler) {
        var serverQuery = request.serverQuery;

        if(request.query) {
            dojo.mixin(serverQuery, request.query);
            delete serverQuery.label;
        }

        var _directlyReturnedItems;

        // Here were are avoiding an unnecessary server request for the cases where the user chooses either the
        // autocompleted value or one of the values from the list, and then tabs/clicks out. The default implementation
        // of ComboBox & Store *always* fire an extra (unnecessary) query when the combobox loses focus.

        var searchPrefix = serverQuery.searchPrefix;

        if(this._optionSelectedByUser != null &&
        	this._optionSelectedByUser.i.label.toUpperCase() === searchPrefix) {
            _directlyReturnedItems = [this._optionSelectedByUser];
        }else if(this._items &&  this._items.length > 0){         
            // improvisation is the order of the day :). Do not fire query if the search prefix matches with any of the items
            // instead of checking only against the first item
            _directlyReturnedItems = dojo.filter(this._items, function(item){
                return item.i.label.toUpperCase() === searchPrefix;
            });
        }
        
        if(_directlyReturnedItems && _directlyReturnedItems.length > 0) {
            fetchHandler(_directlyReturnedItems, request);
            return;
        }

        return this.inherited("_fetchItems", arguments);
    }
});