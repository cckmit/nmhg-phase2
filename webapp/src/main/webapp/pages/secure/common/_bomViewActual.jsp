<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<style type="text/css"><%--Common to both trees--%>
    th, td {
        white-space: nowrap !important;
        text-align: left;
    }
    table.bomTable {
        width : 98%;
        margin : 2px;
        border-collapse: collapse;
    }
    table.bomTable th, table.bomTable td {
        border: 1px solid #EFEBF7;
    }

    table.bomTable thead tr {
        background: #F3FBFE;
    }
</style>
<script type="text/javascript">
    dojo.require("twms.widget.<s:property value="#dialog"/>")
    dojo.require("dijit.layout.TabContainer");
</script>
<div style="display: none">
    <div dojoType="twms.widget.<s:property value="#dialog"/>" id="bomTreeBrowser" bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250">
        <div dojoType="dijit.layout.LayoutContainer" style="width:600px; height:500px;
            background: #F3FBFE; border: 1px solid #EFEBF7; border-top : none !important; padding: 0px; margin: 0px;">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top"
                 style="height: 23px; background: url(image/menubg_new.gif) repeat-x;">
                <span class="TitleBar" style="float: left; display:inline">
                    <s:text name="title.common.components"/>
                </span>
                <img id="closeBomBrowser" style="float: right; margin-top: 2px;padding-right:2px" src="image/CloseRuleWizard.gif"/>
            </div>
            <div dojoType="dijit.layout.TabContainer" layoutAlign="client" style="background: #FFF" id="bomTreeTabContainer">
                <div dojoType="dijit.layout.ContentPane"closable="false" selected="true" title="<s:text name="label.newClaim.componentSerialNumbers"/>" id="serialTab">
                    <center>
                        <u:treeTable id="serialNoTree" loadOn="/bomBrowser/serialTree/load"
                            nodeAgent="twms.inventory.tree.serial.NodeAgent"
                            headTemplateVar="serialNoTreeHead" cssClass="bomTable"/>
                    </center>
                    <style type="text/css">
                        .serialRowHead th.serialNoColHead {
                            width: 40%;
                        }
                        .serialRowHead th.itemNoColHead {
                            width: 15%;
                        }
                        .serialRowHead th.descriptionColHead {
                            width: 45%
                        }
                    </style>
                    <u:jsVar varName="serialNoTreeHead">
                        <tr class="serialRowHead">
                            <th class="serialNoColHead">
                                <s:text name="columnTitle.common.serialNo"/>
                            </th>
                            <th class="itemNoColHead">
                                <s:text name="columnTitle.common.itemNumber"/>
                            </th>
                            <th class="descriptionColHead">
                                <s:text name="columnTitle.common.description"/>
                            </th>
                        </tr>
                    </u:jsVar>
                    <u:jsVar varName="serialNoTreeRow">
                        <tr>
                            <td class="serialNoColHead indentable">
                                <span class="unfoldButton"><img src="image/icon_expand.gif"
                                                                title="<s:text name='label.common.unfold'/>"/></span>
                                <span class="foldButton"><img src="image/icon_collapse.gif"
                                                              title="<s:text name='label.common.fold'/>"/></span>
                                <span class="dummyFoldButton"><img src="image/icon_expand_dummy.gif"/></span>
                                %{slNumber}
                            </td>
                            <td class="itemNoColHead">
                                %{number}
                            </td>
                            <td class="descriptionColHead">
                                %{description}
                            </td>
                        </tr>
                    </u:jsVar>
                </div>
                <div dojoType="dijit.layout.ContentPane"closable="false" title="<s:text name="title.common.components"/>" id="itemTab">
                    <div dojoType="dijit.layout.LayoutContainer" style="width:100%; height:100%">
                        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
                            <center>
                                <u:treeTable id="componentTree" loadOn="/bomBrowser/itemTree/load"
                                    nodeAgent="twms.inventory.tree.component.NodeAgent"
                                    headTemplateVar="componentTreeHead" cssClass="bomTable"/>
                            </center>
                        </div>
                    </div>
                    <style type="text/css">
                        .componentRowHead th.itemNoColHead {
                            width: 40%;
                        }
                        .componentRowHead th.quantityColHead {
                            width: 10%;
                        }
                        .componentRowHead th.descriptionColHead {
                            width: 50%
                        }
                    </style>
                    <u:jsVar varName="componentTreeHead">
                        <tr class="componentRowHead">
                            <th class="itemNoColHead">
                                <s:text name="columnTitle.common.itemNumber"/>
                            </th>
                            <th class="descriptionColHead">
                                <s:text name="columnTitle.common.description"/>
                            </th>
                            <th class="quantityColHead">
                                <s:text name="columnTitle.common.quantity"/>
                            </th>
                        </tr>
                    </u:jsVar>
                    <u:jsVar varName="componentTreeRow">
                        <tr>
                            <td class="serialNoColHead indentable">
                                <span class="unfoldButton"><img src="image/icon_expand.gif"
                                                                title="<s:text name='message.unfold'/>"/></span>
                                <span class="foldButton"><img src="image/icon_collapse.gif"
                                                              title="<s:text name='message.fold'/>"/></span>
                                <span class="dummyFoldButton"><img src="image/icon_expand_dummy.gif"/></span>
                                %{number}
                            </td>
                            <td class="itemNoColHead">
                                %{description}
                            </td>
                            <td class="descriptionColHead">
                                %{quantity}
                            </td>
                        </tr>
                    </u:jsVar>
                </div>
                <script type="text/javascript">
                dojo.declare("twms.inventory.tree.component.Row", tavant.twms.treeTable.AbstractParentRow, {
                    _getSubstitutionMap : function() {
                        return {
                            number : this._nodeObject.number,
                            description : this._nodeObject.description,
                            quantity : this._nodeObject.quantity
                        };
                    }
                });
                dojo.declare("twms.inventory.tree.component.NodeAgent", tavant.twms.treeTable.DefaultNodeAgent, {
                    getRowInstance : function(/*JSON node*/ nodeObject, /*AbstractParentRow*/ parentRow) {
                        return new twms.inventory.tree.component.Row(this._controller, nodeObject, parentRow,
                                componentTreeRow, parentRow.getDepth() + 1);
                    },
                    getChildCollections : function(/*JSON node*/ nodeObject) {
                        return [nodeObject.items];
                    }
                });
                dojo.declare("twms.inventory.tree.serial.Row", tavant.twms.treeTable.AbstractParentRow, {
                    _getSubstitutionMap : function() {
                        return {
                            slNumber : this._nodeObject.slNumber,
                            number : this._nodeObject.number,
                            description : this._nodeObject.description
                        };
                    }
                });
                dojo.declare("twms.inventory.tree.serial.NodeAgent", tavant.twms.treeTable.DefaultNodeAgent, {
                    getRowInstance : function(/*JSON node*/ nodeObject, /*AbstractParentRow*/ parentRow) {
                        return new twms.inventory.tree.serial.Row(this._controller, nodeObject, parentRow,
                                serialNoTreeRow, parentRow.getDepth() + 1);
                    },
                    getChildCollections : function(/*JSON node*/ nodeObject) {
                        return [nodeObject.items];
                    }
                });

                dojo.declare("twms.bomBrowser.TabManager", null, {

                    _browser : null,
                    _tabContainer : null,
                    _serialTab : null,
                    _itemTab : null,

                    _serialTreeCache : null,
                    _itemTreeCache : null,
                    _serialItemRelation : null,

                    _loadSerialTreeOn : null,
                    _loadItemTreeOn : null,

                    /**
                     * Expected format for referenceMap is :
                     * {
                     *  browser : dijit.Dialog,
                     *  tabContainer : dijit.layout.TabContainer,
                     *  serialTab : dijit.layout.ContentPane,
                     *  itemTab : dijit.layout.ContentPane,
                     *  closeButton : domNode(close)
                     * };
                     * Expected format for eventMap is :
                     * {
                     *  loadSerialTreeOn : String(event topic),
                     *  loadItemTreeOn : String(event topic)
                     * }
                     * showOn is a string(topic on which the tree should show up).
                     */
                    constructor : function(/*Assoc array*/ referenceMap, /*Assoc array*/ eventMap, /*String*/ showOn) {
                        this._browser = referenceMap.browser;
                        this._tabContainer = referenceMap.tabContainer;
                        this._serialTab = referenceMap.serialTab;
                        this._itemTab = referenceMap.itemTab;
                        dojo.connect(referenceMap.closeButton, "onclick", this, "_closeBrowser");

                        this._loadSerialTreeOn = eventMap.loadSerialTreeOn;
                        this._loadItemTreeOn = eventMap.loadItemTreeOn;
                        dojo.subscribe(showOn, this, "requestBrowser");
                        this._serialTreeCache = {};
                        this._itemTreeCache = {};
                        this._serialItemRelation = {};
                        this._closeBrowser();
                    },

                    /**
                     * Event message is expected to be in the format...
                     * { serialNo : someSerialNo } or { itemNo : someItemNo}.
                     */
                    requestBrowser : function(eventMessage) {
                        if (eventMessage.serialNo) {
                            this._showTreesForSerialNo(eventMessage.serialNo,
                                                       dojo.hitch(this, "_renderSerialTree"),
                                                       dojo.hitch(this, "_renderItemTree"));
                        } else if (eventMessage.itemNo) {
                            this._showTreeForItemNo(eventMessage.itemNo,
                                                    dojo.hitch(this, "_renderItemTree"));
                        } else {
                            throw new Error("twms.bomBrowser.TabManager<requestBrowser> : event message must have either " +
                                       "serialNo or itemNo.");
                        }
                    },

                    _renderItemTree : function(tree) {
                        publishEvent(this._loadItemTreeOn, tree);
                    },

                    _renderSerialTree : function(tree) {
                        publishEvent(this._loadSerialTreeOn, tree);
                    },

                    _showTreesForSerialNo : function(/*String*/ serialNo,
                                                               /*Function*/ feedSerialTreeTo, /*Function*/ feedItemTreeTo) {
                        var itemNo = this._findInGivenMap(serialNo, this._serialItemRelation);
                        if (itemNo) {
                            feedSerialTreeTo(this._findInGivenMap(serialNo, this._serialTreeCache));
                            feedItemTreeTo(this._findInGivenMap(itemNo, this._itemTreeCache));
                            this._openBrowser(true, true);
                        } else {
                            var self = this;
                            twms.ajax.fireJsonRequest("fetchItemBom.action", {serialNo : serialNo}, function(data) {
                                self._cacheSerialTree(data);
                                self._cacheItemTree(data);
                                self._rememberSerialItemRelation(data);
                                feedSerialTreeTo(data.serialTree);
                                feedItemTreeTo(data.itemTree);
                                self._openBrowser(true, true);
                            });
                        }
                    },

                    _showTreeForItemNo : function(/*String*/ itemNo, /*Function*/ feedItemDataTo) {
                        var tree = this._findInGivenMap(itemNo, this._itemTreeCache);
                        if (tree) {
                            feedItemDataTo(tree);
                            this._openBrowser(false, true);
                        } else {
                            var self = this;
                            twms.ajax.fireJsonRequest("fetchItemBom.action", {itemNo : itemNo}, function(data) {
                                self._cacheItemTree(data);
                                feedItemDataTo(data.itemTree);
                                self._openBrowser(false, true);
                            });
                        }
                    },

                    _findInGivenMap : function(/*String*/ key, /*Collection(Map)*/ cache) {
                        for (var id in cache) {
                            if (id === key) {
                                return cache[id];
                            }
                        }
                        return null;
                    },

                    _rememberSerialItemRelation : function(data) {
                        this._serialItemRelation[data.serialNo] = data.itemNo;
                    },

                    _cacheSerialTree : function(data) {
                        this._serialTreeCache[data.serialNo] = data.serialTree;
                    },

                    _cacheItemTree : function(data) {
                        this._itemTreeCache[data.itemNo] = data.itemTree;
                    },

                    _openBrowser : function(/*boolean*/ showSerialTree, /*boolean*/ showItemTree) {
                        if (showSerialTree) {
                            this._tabContainer.addChild(this._serialTab);
                            this._serialTab.show();
                        }
                        if (showItemTree) {
                            this._tabContainer.addChild(this._itemTab);
                            this._itemTab.show();
                        }
                        this._browser.show();
                        this._tabContainer.resize();
                    },

                    _closeBrowser : function() {
                        this._browser.hide();
                        this._tabContainer.removeChild(this._serialTab);
                        this._tabContainer.removeChild(this._itemTab);
                    }
                });

                dojo.addOnLoad(function() {
                    bomTreeManager = new twms.bomBrowser.TabManager({
                        browser : dijit.byId("bomTreeBrowser"),
                        tabContainer : dijit.byId("bomTreeTabContainer"),
                        serialTab : dijit.byId("serialTab"),
                        itemTab : dijit.byId("itemTab"),
                        closeButton : dojo.byId("closeBomBrowser")
                    }, {
                        loadSerialTreeOn : "/bomBrowser/serialTree/load",
                        loadItemTreeOn : "/bomBrowser/itemTree/load"
                    },
                    "/bomBrowser/show");
                });
                </script>
            </div>
        </div>
    </div>
</div>