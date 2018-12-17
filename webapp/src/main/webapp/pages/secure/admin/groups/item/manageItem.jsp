<%--

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

--%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<!DOCTYPE html>
<html>
<head>
<s:head theme="twms" />
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<link rel="stylesheet" type="text/css" media="all" href="scripts/vendor/jqGrid/css/ui.jqgrid.css"/>
<link rel="stylesheet" type="text/css" media="all"	href="scripts/vendor/jqGrid/css/cupertino/jquery-ui-1.7.2.custom.css"/>

<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<script src="scripts/vendor/jqGrid/js/jquery-1.5.2.min.js" type="text/javascript"></script>
<script src="scripts/vendor/jqGrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="scripts/vendor/jqGrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
<script type="text/javascript">
    var selectedItems = new Array();
    jQuery(document).ready(function() {
    <s:if test="itemGroup != null && itemGroup.id != null">
        jQuery("#list").jqGrid({
            url:'get_items_for_item_group.action?id='+'<s:property value="itemScheme.id"/>&itemGroup=<s:property value="itemGroup.id"/>',
            datatype: 'json',
            mtype: 'GET',
            colNames:['<s:text name="label.common.partNumber"/>','<s:text name="label.common.description"/>'],
            colModel:[
                {name:'item.number',label:'item.number', editable: false, sortable:true, search:true},
                {name:'item.description',label:'description', editable: false, sortable:false, search:false}                
            ],
            pager: '#pager',
            rowNum:10,
            height:'125px',
            imgpath: 'css/smoothness/images',
            sortname: 'item.number',
            sortorder: 'asc',
            multiselect: true,            
            viewrecords: true,
            autowidth:true,
            jsonReader: {
                repeatitems: false,
                id: "id"
            },
            onSelectRow: function(id, status) {
                handleSelect(id, status);
            },
            onSelectAll: function(ids, status) {
                for (var i = 0; i < ids.length; i++) {
                    handleSelect(ids[i], status);
                }
            },
            gridComplete:function() {
                var grid = jQuery("#list");
                for (var i = 0; i < selectedItems.length; i++) {
                    grid.setSelection(selectedItems[i]);
                }
            },
            caption: '<s:text name="label.manageGroup.manageMemberItems"/>'
        });
        jQuery("#list").filterToolbar({useparammap:true,searchOnEnter:false});
    </s:if>
        jQuery("#addedList").jqGrid({
            datatype: 'local',
            colNames:['','<s:text name="label.common.partNumber"/>','<s:text name="label.common.description"/>'],
            colModel:[
                {name:'item.id',label:'item.id', hidden:true, editable: false, sortable:false, search:false},
                {name:'item.number',label:'item.number', editable: false, sortable:false, search:false},
                {name:'item.description',label:'description', editable: false, sortable:false, search:false}                
            ],
            pager:"#addedListPager",
            imgpath: 'css/smoothness/images',
            viewrecords: true,
            scrollerbar:true,
            height:'100px',
            autowidth:true,
            localReader: { id: "item.id"},
            caption: 'Newly Added Parts'
        });
    });

    function handleSelect(id, status) {
        if (status) {
            if (jQuery.inArray(id, selectedItems) == -1) {
                selectedItems.push(id);
            }
        } else {
            selectedItems = jQuery.grep(selectedItems, function(value) {
                return value != id;
            });
        }
    }    
</script>

<script type="text/javascript">
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.BorderContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.form.ComboBox");
</script>
<script type="text/javascript" src="scripts/groupSearch.js"></script>
</head>
<u:body>
<s:form name="baseForm" id="baseForm">
<u:actionResults/>
<s:if test="itemScheme.id!=null">
    <s:hidden name="itemSchemeId" value="%{itemScheme.id}" id="schemeId"/>
</s:if>
<s:else>
    <s:hidden name="id" value="%{itemScheme.id}" id="schemeId"/>
</s:else>
<s:hidden name="itemGroup.id" id="groupId"/>
<div class="admin_section_div" style="margin:5px;">
    <div class="admin_section_heading">
        <s:text name="%{sectionTitle}"/>
    </div>
    
        <div class="mainTitle" style="margin-top:5px;">
            <s:text name="label.manageGroup.itemGroupDetails"/>
        </div>
    
        <div class="borderTable">&nbsp;</div>

        <table class="grid" cellspacing="0" cellpadding="0" style="margin-top:-10px;">
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/>:</td>
                <td class="admin_selections"><s:textfield name="itemGroup.name" size="50" maxlength="200"/></td>
            </tr>
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
                <td class="admin_selections"><s:textfield name="itemGroup.description" size="50" maxlength="1000"/></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td colspan="2">
                    <s:if test="!itemScheme.purposeProductStructure">
                        <input type="button" class="buttonGeneric" value="<s:property value='buttonLabel'/>"
                                  onclick="updateItemGroup()"/>
                    </s:if>
                    <input type="button" id="closeButton2" class="buttonGeneric"
                           value="<s:text name='button.common.cancel'/>"/>
                </td>
              
            </tr>
        </table>

    <s:if test="%{itemGroup.id == null}">
        
            <div class="mainTitle ">
                <table>
                    <tr>
                        <td class="warColHeader" width="40%">
                            <s:text name="label.manageGroup.consistingOfGroups"/>
                              <input type="radio" name="groupType"
                                   onclick="setGroupType('manage_group.action')">                            
                        </td>
                        <td class="warColHeader">
                            <s:text name="label.manageGroup.consistingOfItems"/>
                            <input type="radio" name="groupType" checked/>
                        </td>
                    </tr>
                </table>
            </div>
        
    </s:if>
    <div id="divForItems">
        <br/>
	    <table id="list" class="CenterlizedDiv" style="width:90%;"></table>
	    <div id="pager"></div>
                  
         <table id="addedList" style="width:90%;overflow: auto"></table>
	    <div id="addedListPager"></div>
      <div class="spacingAtTop" align="center">
          <s:if test="!itemScheme.purposeProductStructure">
              <s:if test="itemGroup != null && itemGroup.id != null">
               <input type="button" class="buttonGeneric"
                   value="<s:text name="button.manageGroup.removeItems"/>"
                   onclick="removeItems()"/>
               </s:if>
            <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addItems"/>"
                   onclick="showDialog()"/>
          </s:if>
        <input type="button" id="closeButton1" class="buttonGeneric" value="<s:text name='button.common.cancel'/>"/>
      </div>
</div>

<div id="dialogBoxContainer" style="display:none;width: 100%">
    <div dojoType="twms.widget.Dialog" id="searchItemsDialog" title="<s:text name="label.common.searchItems"/>"
     style="width:90%;height:70%;">
<!--        <div dojoType="dijit.layout.ContentPane" style="overflow:auto;width:100%;height:100%;">-->
            <div dojoType="dijit.layout.ContentPane" region="top" style="border-bottom:1px solid #EFEBF7;" >
                <table width="99%" class="admin_subsection_div">
                    <tr>
                        <td valign="bottom"><s:text name="label.common.partNumber"/> : <s:textfield id="p1"
                                                                                                    name="itemNumber"/>&nbsp;</td>
                        <td valign="bottom"><s:text name="label.common.description"/> : <s:textfield id="p2"
                                                                                                     name="itemDescription"/>&nbsp;</td>
                        <td>
                            <input type="button" class="buttonGeneric"
                                   value="<s:property value="%{getText('button.common.search')}"/>"
                                   id="itemSearchButton"
                                   onclick="searchItems('search_items_for_itemgroup.action', 0)">
                        </td>
                    </tr>
                </table>
            </div>
            <div dojoType="dijit.layout.ContentPane" region="client" id="searchResults">
            </div>
            <div dojoType="dijit.layout.ContentPane" region="bottom" style="border-top:1px solid #EFEBF7">
                <table width="100%" class="buttonWrapperPrimary">
                    <tr>
                        <td align="center">
                            <s:if test="%{itemGroup.id}">
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.common.update')}"/>"
                                       onclick="addSelectedItems()"/>
                            </s:if>
                            <s:else>
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.manageGroup.addItems')}"/>"
                                       onclick="addSelectedItems()"/>
                            </s:else>
                        </td>
                        <td align="center">
                            <input type="button" name="Submit3" value="<s:text name="button.common.close"/>"
                                   class="buttonGeneric" onclick="closeDialog();"/>
                        </td>
                    </tr>
                </table>
            </div>
<!--        </div>-->
    </div>
</div>
<!--  Make this hidden -->
<div id="addedItemsHere" style="display:none"></div>

<script type="text/javascript">

    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId('closeButton1'), "onclick", function() {
            var thisTabId = getTabDetailsForIframe().tabId;
            var thisTab = getTabHavingId(thisTabId);
            closeTab(thisTab);
        });

        dojo.connect(dojo.byId('closeButton2'), "onclick", function() {
            var thisTabId = getTabDetailsForIframe().tabId;
            var thisTab = getTabHavingId(thisTabId);
            closeTab(thisTab);
        }); 
    });

    
    function removeItems(){
        if(selectedItems.length > 0){
            var url = "remove_items_from_group.action?";
            for (var i = 0; i < selectedItems.length; i++) {
                url = url + "removedItems[" + i + "]=" + selectedItems[i] + "&";
            }
            var form = dojo.byId('baseForm');
            form.action = url;
            form.method = 'post';
            form.submit();
        }
    }
    function addSelectedItems(){
        closeDialog();
        var elts;
        handleSelectedItems();
        jQuery("#addedList").jqGrid('clearGridData',true);
        elts = eval('[' + selectedItemsToAdd + ']');
        if(elts.length > 0){
            for(var i = 0; i < elts.length; i++){
                jQuery("#addedList").jqGrid('addRowData', 
                elts[i].id, 
                {"item.id":elts[i].id,"item.number": elts[i].itemNumber,"item.description":elts[i].itemDescription});
            }
        }
    }
    function updateItemGroup(){
        var  url = 'save_item.action?';
        var newlyAddedItems = jQuery("#addedList").getDataIDs();
            for(var i = 0; i < newlyAddedItems.length; i++){
                url = url + 'addedItems[' + i + ']=' + newlyAddedItems[i] + '&';
            }
            var form = dojo.byId('baseForm');
            form.method = 'post';
            form.action = url;
            form.submit();
    }
</script>
</s:form>
</u:body>
</html>