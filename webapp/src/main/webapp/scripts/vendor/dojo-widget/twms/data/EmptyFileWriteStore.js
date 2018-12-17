dojo.require("dojo.data.ItemFileWriteStore");

dojo.provide("twms.data.EmptyFileWriteStore");

dojo.declare("twms.data.EmptyFileWriteStore", dojo.data.ItemFileWriteStore, {
    _EMPTY_STORE_JSON : null,

    constructor: function(/* object */ keywordParameters){
        this._EMPTY_STORE_JSON = {
            identifier: 'id',
            label: 'title',
            items: []
        };

        twms.data.EmptyFileWriteStore.superclass.constructor.call(this, {
            data: this._EMPTY_STORE_JSON
        });
    }
});