dojo.require("dojox.layout.ContentPane");
dojo.require("dijit.layout.LayoutContainer");
dojo.require("twms.widget.Dialog");

dojo.provide("twms.widget.MultipleInventoryPicker");

dojo.declare("twms.widget.MultipleInventoryPicker", [dijit._Widget, dijit._Templated], {

    cssClass : "multipleInventoryPicker",
    searchTitle : "Select Inventories",
    searchInputUrl : "setupInventorySearch.action",
    searchLinkLabel : i18N.select_additional_inventories,
    searchLinkClass : "multiInventorySearchTrigger",
    widgetsInTemplate: true,
    searchHandlerUrl: "searchInventoriesForSelection.action",
    selectionHandlerUrl: "handleInventorySelection.action",
    selectedItemsContentPane : "selectedInventoriesPane",
    useInnerHTML : false,
    searchHandlerParams: "",
    selectionHandlerParams: "",
    searchInvType:"",
    searchActionType:"",
    isMultiLineUser:"",
    isRestrictedBuListDisplayed:"",

    templatePath: dojo.moduleUrl("twms", "widget/templates/MultipleInventoryPicker.html"),

    postCreate : function() {
        dojo.connect(this.linkNode, "onclick", this, "_showPicker");
        dojo.connect(this.searchInputNode, "onLoad", this, "_bindInventorySearchForm");
        dojo.connect(this.searchResultsNode, "onLoad", this, "_bindInventorySelectionForm");

        if(!dojo.string.isBlank(this.searchHandlerParams)) {
            this.searchHandlerParams = eval(this.searchHandlerParams);
        }

        if(!dojo.string.isBlank(this.selectionHandlerParams)) {
            this.selectionHandlerParams = eval(this.selectionHandlerParams);
        }

        // We used to do this automatically by setting the href for the ContentPane, inside the template itself. But
        // that bombs in IE, when you open the parent again and try to perform a search. Turned out that it was
        // happening because the ContentPane content was not being reloaded in that case.
        this.searchInputNode.setHref(this.searchInputUrl);
    },

    _showPicker : function() {
        this.dialogNode.show();
        //console.debug(dojo.byId("restrictedBuSearchList"));
        if(this.searchInvType=='STOCK' && dojo.byId("endCustomerSearch")){
        	dojo.html.hide(dojo.byId("endCustomerSearch"));
        }
        if(this.searchActionType=='HIDEDTD' && dojo.byId("factoryOrderNumberSearch")){
        	dojo.html.hide(dojo.byId("factoryOrderNumberSearch"));
        }
        if(this.isMultiLineUser=='TRUE' && dojo.byId("buSearchList")){
        	dojo.html.show(dojo.byId("buSearchList"));
        }
        if(this.isRestrictedBuListDisplayed=='TRUE' && dojo.byId("restrictedBuSearchList")){
            dojo.html.show(dojo.byId("restrictedBuSearchList"));
        }
        dojo.publish("/multipleInventorySearch/setSelectedParams");
    },

    _hidePicker : function() {
        this.dialogNode.hide();
    },

    _bindInventorySearchForm : function() {
        this._formBind("multiInventorySearchForm", {
            searchStatusTopic: "/multipleInventorySearch/searchStatus",
            resultsTargetContentPane: "searchResultsNode",
            urlParams: this.searchHandlerParams,
            formActionUrl: this.searchHandlerUrl,
            useInnerHTML: false
        });
    },

    _bindInventorySelectionForm : function() {
		this.searchHandlerParams.selectedItemsIds = selectedItems;
        this._formBind("multiInventorySearchResultsForm", {
            searchStatusTopic: "/multipleInventorySearchResults/searchStatus",
            resultsTargetContentPane: this.selectedItemsContentPane,
            formActionUrl: this.selectionHandlerUrl,
            urlParams: this.selectionHandlerParams,
            submissionCallback: dojo.hitch(this, function() {
                this._hidePicker();
            }),
            useInnerHTML: false
        });
    },

    _formBind : function(/*string*/ formId, /*object*/ options) {

        var targetForm = dojo.byId(formId);

        if(!dojo.string.isBlank(options.formActionUrl)) {
            targetForm.action = options.formActionUrl;
        }

        var targetContentPane = dijit.byId(options.resultsTargetContentPane);

        var bind = new dojo.io.FormBind({
            formNode: targetForm,
            content: options.urlParams,
            load: function(data, e) {
                dojo.body().style.cursor = "auto";

                var validationFailed = (data == "<true>");

                if(options.useInnerHTML) {
                    targetContentPane.domNode.innerHTML = (validationFailed) ? "" : data;
                } else {
                    targetContentPane.setContent(data);
               }

                dojo.publish(options.searchStatusTopic, [{
                    validationFailed : validationFailed
                }]);

                if(options.submissionCallback) {
                    options.submissionCallback();
                }
            },
            error : function(error) {
                dojo.body().style.cursor = "auto";
            }
        });

	    bind.onSubmit = function(formNode) {
            dojo.body().style.cursor = "wait";
            targetContentPane.destroyDescendants();
            targetContentPane.domNode.innerHTML =
                "<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'>Loading...</div></div>";
            return true;
	    }

        return bind;
    }
});
