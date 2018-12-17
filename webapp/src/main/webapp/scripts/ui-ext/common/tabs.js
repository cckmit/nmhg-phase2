/**
 *
   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.
 *
 */

dojo.require("twms.widget.TabContainer");
dojo.require("twms.widget.Menu");

dojo.declare("tavant.twms.TabManager", null, {

    tabPane : null,

    /*
     * This array holds the familyId and its tabs.
     * Format : {catagory : "tabs_catagory", tabIds : array_of_tab_ids};
     */
    _openTabCatagories : new Array(),

    /**
     * Used for generating tab ids.
     */
    _tabIndex : 0,

    /**
     * The tabs sometimes need to be refreshed because of pending updates... done frm other screens...
     * this array is supposed to hold all the tab labels.. that need to be informed about them getting focused.
     * When they r focused.. the event is published(and a parameter called needsRefresh == true)... and the tab's
     * content can act upon it... as it wants.
     * The elements are objects of the format {label : "Tab Label", fullRefresh : true/false}
     */
    _dirtyTabs : new Array(),

    constructor : function() {
        this.tabPane = dijit.byId("tabPane");
        dojo.connect(this.tabPane, "selectChild", this, "tabFocused");//FIXME: this ahd be and after advice... i don't know why after advice didn't work.
    },

    /*****Public Methods*****/

    tabFocused : function(/*Tab content pane*/ tab) {
        var win = this.getIframeWindowForTab(tab);
        if(win && win.publishEvent) {
            win.publishEvent("/tab/focused", {needsRefresh : this._isTabDirty(tab, true)});
        }
    },

    markDirty : function(/*String (tab label)*/ dirtyTabLabel,
                                                /*boolean (force preview refresh)*/ forceFullRefresh) {
        var tab = this.getTabHavingLabel(dirtyTabLabel);
        if(tab) {
            var tabStub = this._isTabDirty(tab);
            if(tabStub) {
                tabStub.fullRefresh = forceFullRefresh || tabStub.fullRefresh;
            } else {
                this._dirtyTabs.push({label : dirtyTabLabel,
                                      fullRefresh : forceFullRefresh});
            }
        }
    },

    getIframeWindowForTab : function(tab) {
        var iframe = dojo.query("iframe", tab.domNode)[0];
        if(iframe) {
            return window.frames[iframe.name];
        }
    },

    /**
     * Finds the tab with currect label(which is given) and changes the label of that tab.
     * @param event [currentLabel, newLabel]
     */
    changeTabLabel : function(event) {
        var tab = null;
        if(!event.tab) {
            tab = this.getTabHavingLabel(event.currentLabel);
        } else {
            tab = event.tab;
        }
        if(tab != null) {
            this._changeThisTabsLabel(tab, event.newLabel);
        }
    },

    /**
     * Gets tab having given id, if not found, returns null.
     */
    getTabHavingId : function(id) {
        var tabs = this.tabPane.getChildren();
        for(var i = 0; i < tabs.length; i++) {
            if(id == tabs[i].id) {
                return tabs[i];
            }
        }
        return null;
    },

    loadInTab : function(event) {
        this._openInGivenTab(event.tab, event.label, event.url)
    },

    /**
     * Opens a new tab if a tab be the label requested, doesn't exist.
     * If it does exist, it will just focus the existing one.
     * @param event [label, url]
     */
    openTab : function(event) {
        var tab = null;
        var catagorysFirstTabId = null;
        if(!event.forceNewTab) {//new tab is to be opened even if tab with the same label is there.
            tab = this.getTabHavingLabel(event.label);
        }
        if(event.catagory) {//if catagory attribute is there
            catagorysFirstTabId = this._getFirstTabOf(event.catagory);
        }

        if(tab == null) { //tab with this label is NOT there....
            if(catagorysFirstTabId && !event.forceNewTab) {//tab with the same catagory is there, and new tab was not requested
                tab = this.getTabHavingId(catagorysFirstTabId);
                this._openInGivenTab(tab, event.label, event.url);
            } else {//no tab available in given catagory or new tab was requested
                var tabId = "tab_" + this._tabIndex++;
                tab = this._getNewTab(tabId, event.label, event.url, event.decendentOf);

                if(event.catagory) {
                    this._addTab(event.catagory, tabId);
                }

                this.tabPane.addChild(tab);
                tab.startup();

                this._registerTabForCloseOnMiddleClick(tab); // This should be called *only*
                                                             // after the addChild call above.

                this._attachContextMenu(tab); // This should be called *only*
                                                             // after the addChild call above.
            }
            
            // this is used to open context help
            if(event.helpCategory)
            {	
                tab._helpCategory = event.helpCategory;
            }
        }
        
        this._focusTab(tab);
        if(event.feedbackTabRef) {
            event.feedbackTabRef(tab);
        }
    },

    /**
     * This function will return true when the tab was marked dirty... and will also remove that tabs entry... fromt the list.
     */
    _isTabDirty : function(/*Tab content pane*/ tab, /*boolean (remove from array)*/ splice) {
        var dirtyTabIndex = this._dirtyTabs.length;
        while(dirtyTabIndex--) {
	        var dirtyTab = this._dirtyTabs[dirtyTabIndex];
	        if(dirtyTab.label === tab.title) {
		        return splice ? this._dirtyTabs.splice(dirtyTabIndex, 1)[0] : dirtyTab;
	        }
        }
        return null;
    },

    _registerTabForCloseOnMiddleClick : function (tab) {
        var tabButton = this._getTabButtonForTab(tab);

        dojo.connect(tabButton.innerDiv.parentNode, "onmousedown", this, function(evt) {
            if (isMiddleClickEvent(evt)) {
                if(tab.closable) {
                    dojo.stopEvent(evt);
                    this._closeAndCleanUpTab(tab);
                }
            }
        });
    },

    _closeAndCleanUpTab : function(tab) {
        this.tabPane.closeChild(tab);
    },

    _attachContextMenu : function(tab) {
        var tabButton = this._getTabButtonForTab(tab);

        var menu = new twms.widget.Menu();

        var refreshMenuItem = this._createMenuItem("Refresh", "refreshIcon",
                dojo.hitch(this, function() {
                    this._refreshTab(tab);
                })
        );
        menu.addChild(refreshMenuItem);

        var refreshAllMenuItem = this._createMenuItem("Refresh All", "refreshIcon",
                dojo.hitch(this, function() {
                    this._refreshAllTabs();
                })
        );
        menu.addChild(refreshAllMenuItem);

        var closeMenuItem = this._createMenuItem("Close", "closeOverIcon",
                dojo.hitch(this, function() {
                    this._closeAndCleanUpTab(tab);
                })
        );
        menu.addChild(closeMenuItem);

        var closeOthersMenuItem = this._createMenuItem("Close Others", "closeOverIcon",
                dojo.hitch(this, function() {
                    this.closeAllTabsButThis(tab);
                })
        );
        menu.addChild(closeOthersMenuItem);

        var closeAllMenuItem = this._createMenuItem("Close All", "closeOverIcon",
                dojo.hitch(this, function() {
                    this.closeAllTabs();
                })
        );
        menu.addChild(closeAllMenuItem);

        menu.bindDomNode(tabButton.innerDiv.parentNode);
    },

    _createMenuItem : function(label, iconClass, clickHandler) {
        return new dijit.MenuItem(
            {
                label: label,
                iconClass: iconClass,
                onClick:clickHandler
            }
        );
    },

    _refreshTab : function(tab) {
        var iframe = getIFrameForTab(tab);
        this._loadIFrame(tab,
                         iframe.id,
                         iframe.src,
                         tab.title,
                         true,
                         iframe.id);
    },

    _refreshAllTabs : function() {
        var tabsList = this.tabPane.getChildren();

        dojo.forEach(tabsList,
                function(tab) {
                    if(tab.id && tab.id.indexOf('tab_') === 0){ // do not refresh home tab, as source for this iframe will be null !!!
                        this._refreshTab(tab);
                    }
                },
                this
        );
    },

    closeTab : function(event) {
        this._closeAndCleanUpTab(event.tab);
    },

    focusParent : function(/*tab reference*/ tab) {
        var parentLabel = getDetailsOfTab(tab).folderName;
        if(parentLabel) {//this checks will no longer be required... when we start using decendentOf in all the places.
            var parentTab = this.getTabHavingLabel(parentLabel);
            if(parentTab) this._focusTab(parentTab);//if parent is not closed already...
            delete parentTab;
        }
        delete parentLabel;
    },

    closeAllTabsButThis : function(thisTab) {
        var tabsListOriginal = this.tabPane.getChildren().slice();

        dojo.forEach(tabsListOriginal,
                function(tab) {
                    if (tab != thisTab && tab.closable) {
                        this._closeAndCleanUpTab(tab);
                    }
                }, this
        );
    },

    closeAllTabs : function() {
        var tabsListOriginal = this.tabPane.getChildren().slice();

        dojo.forEach(tabsListOriginal,
                function(tab) {
                    if (tab.closable) {
                        this._closeAndCleanUpTab(tab);
                    }
                }, this
        );
    },

    _getTabButtonForTab : function(tab) {
        return this.tabPane.tablist.pane2button[tab.id];
    },

    reloadIFrame : function(event) {
        var isForPreview = event.iframeId.indexOf("preview#") != -1;

        if(isForPreview) {
            twms.ajax.fireHtmlRequest(event.url,null,function(data){
                event.contentPane.set('content',data);
            },
            null,true);
        } else {
            this._loadIFrame(event.contentPane,
                         event.iframeId,
                         event.url,
                         event.label,
                         event.resetDirtyRecord,
                         event.iframeId,
                         event.feedbackIframeRef);
        }
    },

    onIFrameLoad : function(iframe, tabLabel, isPreview) {
        //HACK: The onLoad function of the iframe is called two extra times, once when the page is loaded as an
        //async request and other when we force a GC on it by setting its src to "javascript:false". This line makes
        // the function return without any processing, in case of these extra calls.

        var iframeSrc = iframe.src;
        if(!iframeSrc || dojo.string.isBlank(iframeSrc) || "javascript:false" == iframeSrc) {
            return;
        }

        var name = iframe.name;
        var containerId = null;
        var indexOfHash = name.indexOf("#");//will be used in case the tab is decendent of some other tab.
           if(indexOfHash != -1) {
               containerId = name.substring(0, indexOfHash);
           } else {
               containerId = name.substring(0, name.length - "_iframe".length);
           }
        var container = dijit.byId(containerId);
        container.loaded = true;
        twms.util.removeLidFrom(iframe.parentNode);

        // the iframe attribute __twmsPageLoaded is set as false in body.ftl and is set to true only in body-close.ftl. Hence, if it's not true, we
        // know that the page was not fully loaded.
        if(!iframe.__twmsPageLoaded) {
            this._handleTabLoadingFailure(iframe);
        }

        if(!isPreview) {//setting the tab reference in the document so that it can be retrived for example for changing the name.
            var tab = this.getTabHavingLabel(tabLabel);
            window.frames[name].document.getMyTab = function() {
                return tab;
            }
        } else {
            var localDoc = window.frames[name].document;
            if(localDoc == null) {//HACK: it is used when the preview gets shown(through the show hide button).
                return;
            }

            for(var i = 0; i < localDoc.forms.length; i++) {
                for(var j = 0; j < localDoc.forms[i].elements.length; j++) {
                    dojo.connect(localDoc.forms[i].elements[j], "onchange", function() {
                        iframe.dirty = true;
                    });
                }
            }
            
            localDoc.isDirty = function() {//this function is supposed to tell me if the preview is dirty.
                return iframe.dirty;
            }
        }
    },

    _handleTabLoadingFailure : function(/*iframe*/ iframe) {
        //TODO:TBD
    },

    /**
     * Gets tab having given label, if not found, returns null.
     */
    getTabHavingLabel : function(label) {
        var tabs = this.tabPane.getChildren();
        for(var i in tabs) if(label === tabs[i].title) return tabs[i];
        return null;
    },

    /*****Private Methods*****/

    _addTab : function(catagory, tabId) {
        for(var i = 0; i < this._openTabCatagories.length; i++) {
            if(this._openTabCatagories[i].catagory == catagory) {
                this._openTabCatagories[i].tabIds.push(tabId);
                return;
            }
        }
        this._openTabCatagories.push({catagory: catagory,
                                 tabIds: new Array(tabId)});
    },

    _removeTab : function(tabId) {
        for(var i = 0; i < this._openTabCatagories.length; i++) {
            for(var j = 0; j < this._openTabCatagories[i].tabIds.length; j++) {
                if(this._openTabCatagories[i].tabIds[j] == tabId) {
                    this._openTabCatagories[i].tabIds.splice(j, 1);
                }
            }
        }
    },

    _getFirstTabOf : function(catagory) {
        if(catagory == null)
            return null;
        for(var i = 0; i < this._openTabCatagories.length; i++) {
            if(this._openTabCatagories[i].catagory == catagory) {
                return this._openTabCatagories[i].tabIds[0];
            }
        }
        return null;
    },

    /**
      * Opens a new url in the tab given.
     */
    _openInGivenTab : function(tab, label, url) {
        this._changeThisTabsLabel(tab, label);
        var iframe = tab.domNode.getElementsByTagName("iframe")[0];
        iframe.dirty = false;
        this._loadUrlInThisIframe(tab, iframe, url);
    },

    _focusTab : function(tab) {
        this.tabPane.selectChild(tab, true);
    },

    _getNewTab : function(id, label, url, decendentOf) {
        var self = this;
        var content = new dijit.layout.ContentPane({
                    id: id,
                    title: label,
                    preventCache: true,
                    refreshOnShow: false,
                    closable: true,
                    onClose: function() {
                            var iframe = this.domNode.getElementsByTagName("iframe")[0];
                            if(iframe) {
                                iframe.src="javascript:false";
                            }
                            self.focusParent(this);
                            self._removeTab(this.id);
                            return true;
                    }}, dojo.doc.createElement('div'));
        var iframeName = id;
        if(decendentOf != null) {
            iframeName = iframeName + "#" + getValidDecendentOfValue(decendentOf);
        } else {
            setTimeout(function() {
                throw new Error("TabManager : 'decendentOf' is a mandatory parameter for opening a tab. " +
                    "Please set it to the LABEL of the tab, that you want to be focused, when this tab is closed.");
            }, 0);
        }
        this._loadIFrame(content, id + "_iframe", url, label, true, iframeName + "_iframe");

        return content;
    },

    _loadIFrame : function(contentPane, iframeId, url, label, resetDirtyRecord, iframeName, feedbackIframeRef) {
        if(iframeName == null) {
            iframeName = iframeId;
        }

        if(!contentPane.domNode.getElementsByTagName("iframe")[0]) {
            if(label) {
                var escapedLabel = dojo.string.escapeJavaScript(label);
                contentPane.set('content',"<iframe class=\"reloadable\" name=\"" + iframeName + "\" id=\"" + iframeId + "\" onload=\"onIFrameLoad(this, '" + escapedLabel + "', false);\" frameborder=\"0\"></iframe>");
            }
            else {
                contentPane.set('content',"<iframe class=\"reloadable\" name=\"" + iframeName + "\" id=\"" + iframeId + "\" onload=\"onIFrameLoad(this, null, true);\" frameborder=\"0\"></iframe>");
            }
        }

        var iframe = contentPane.domNode.getElementsByTagName("iframe")[0];

        if(resetDirtyRecord) {
            iframe.dirty = false;
        }

        this._loadUrlInThisIframe(contentPane, iframe, url);

        if(feedbackIframeRef) {
            feedbackIframeRef(iframe);
        }
    },

    /**
     * Loads a given url in a given iframe.
     */
    _loadUrlInThisIframe : function(tab, iframe, url) {
        //loading iframe using an async request.
        var self = this;
        window.setTimeout(function() {
                              self._initIframeLoading(tab, iframe, url);
                          }, 0);
    },

    /**
     * Is used to load iframes source(url) as an async process, so that it doesn't block the user actions.
     */
    _initIframeLoading : function(tab, iframe, url) {
        twms.util.putLidOn(iframe.parentNode);
        iframe.src = url;
    },

    /**
     * Changes the label of given tab.
     */
    _changeThisTabsLabel : function(tab, newLabel) {
        tab.title = newLabel;
        tab.controlButton.setLabel(newLabel);
    }
});

var tabManager = null;

/**
 * Here we subscribe to all the topics about we expect to hear from jsp inside a tab(iFrame).
 */
dojo.addOnLoad(function() {
    tabManager = new tavant.twms.TabManager();
    dojo.subscribe("/tab/open", tabManager, "openTab");
    dojo.subscribe("/tab/close", tabManager, "closeTab");
    dojo.subscribe("/tab/changeLabel", tabManager, "changeTabLabel");
    dojo.subscribe("/iframe/reload", tabManager, "reloadIFrame");
    //this is to be used when reloading something in a tab, and not the iframe reloading event.
    dojo.subscribe("/tab/reload", tabManager, "loadInTab");
    window.tabManager = tabManager;
});

//This function is used on the onload event of the Iframes
function onIFrameLoad(iframe, tabLabel, isPreview) {
    tabManager.onIFrameLoad(iframe, tabLabel, isPreview);
}

/**
 * This method is used by items in the accordion pane. This destinguishes between a Click and Ctrl + Click and fires
 * tab opening events accordingly.
 */
function requestTab(event) {

    var newTabRequestedByUser = false;

    if (isRightClickEvent(event)) {
        return;
    }

    if (isMiddleClickEvent(event)) {
        newTabRequestedByUser = true;
        dojo.stopEvent(event);
    }

    if (event.ctrlKey) {
        newTabRequestedByUser = true;
    }

    ///////refresh folder count of folder to be opened.//FIXME: Optimize this....
    var folderLi = getExpectedParent(event.target, "li");
    if(folderLi != null) {
        publishEvent("/refresh/folderCount", {folderId: folderLi.id});
    }
    /////////////////////////////////////////////////

    event.forceNewTab |= newTabRequestedByUser;
    publishEvent("/tab/open", event);
}

//to change the tab label.... the page loaded in the tab(iframe) should publish an event like this...
//	parent.publishEvent("/tab/changeLabel",{currentLabel: document.getMyTab().label, newLabel: newLabel});

////Menu callbacks.
function createNewClaim() {
    publishEvent("/tab/open", {label: i18N.new_claim, url: "showNewClaimForm.action", forceNewTab: true, decendentOf: i18N.home_tab_label});
}

//Add callback for Register Major Components
function registerMajorComponents(){
	publishEvent("/tab/open", {label: i18N.register_major_components, url: "showRegisterMajorComponentsForm.action", forceNewTab: true, decendentOf: i18N.home_tab_label});
}

//Add callback for Third Party claim creation
function createNewThirdPartyClaims(){
	publishEvent("/tab/open", {label: i18N.new_third_party_claim, url: "showNewThirdPartyClaimForm.action", forceNewTab: true, decendentOf: i18N.home_tab_label});
}

function logConsumerComplaint() {
    publishEvent("/tab/open", {label: i18N.new_consumer_complaint, url: "showNewConsumerComplaintForm.action?complaintType=Consumer", forceNewTab: true, decendentOf: i18N.home_tab_label});
}

function createFieldReport() {
    publishEvent("/tab/open", {label: i18N.new_field_report, url: "showNewConsumerComplaintForm.action?complaintType=FieldReport", forceNewTab: true, decendentOf: i18N.home_tab_label});
}

function viewConsumerComplaint() {
    publishEvent("/tab/open", {label: i18N.view_consumer_complaint, url: "viewComplaints.action?folderName=Consumer", decendentOf: i18N.home_tab_label});
}

function viewFieldReport() {
    publishEvent("/tab/open", {label: i18N.view_field_report, url: "viewComplaints.action?folderName=FieldReport", decendentOf: i18N.home_tab_label});
}

function openClaimSearch() {
    publishEvent("/tab/open", {label: i18N.search_claim, url: "claimSearch.action", decendentOf: i18N.home_tab_label});
}

function openInventorySearch() {
    publishEvent("/tab/open", {label: i18N.search_inventory, url: "inventorySearch.action", decendentOf: i18N.home_tab_label});
}

function openPolicyCreation() {
    publishEvent("/tab/open", {label: i18N.new_policy, url: "new_policy.action", decendentOf: i18N.home_tab_label});
}

function createPaymentDefinition() {
    publishEvent("/tab/open", {label: i18N.create_payment_definition, url: "new_payment_definition.action", decendentOf: i18N.home_tab_label});
}

function registerNewWarranty() {
    publishEvent("/tab/open", {label: i18N.new_warranty_registration, url: "create_warranty.action", decendentOf: i18N.home_tab_label});
}

function warrantyTransfer() {
    publishEvent("/tab/open", {label: i18N.warranty_transfer, url: "show_warranty_transfer.action", decendentOf: i18N.home_tab_label});
}

function createCustomer() {
    publishEvent("/tab/open", {label: i18N.create_customer, url: "show_customer.action?hideSelect=true", decendentOf: i18N.home_tab_label});
}

function uploadClaim() {
    publishEvent("/tab/open", {label: i18N.upload_claim, url: "import_claim_setup.action", decendentOf: i18N.home_tab_label});
}

function uploadDocument() {
    publishEvent("/tab/open", {label: i18N.upload_document, url: "document_upload_setup.action", decendentOf: i18N.home_tab_label});
}

function openNewClaimForm() {
    publishEvent("/tab/open", {label: "New Claim", url: "showNewClaimForm.action", decendentOf: i18N.home_tab_label});
}

function openCampaignItemSearch() {
    publishEvent("/tab/open", {label: "Campaign Item Search", url: "campaignItemsSearch.action", decendentOf: i18N.home_tab_label});
}
////////////////
/**
 * This function is written here outside of everything... because it will be used by the tst:summaryTable's event handler as well...
 */
function getValidDecendentOfValue(/*String*/ actualLabel) {
    return actualLabel.replace(/\s/g, "_");
}
