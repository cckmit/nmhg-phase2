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

<!-- Fix for SLMSPROD-903 : Contents of file /pages/secure/admin/supplier/detail/itemsCovered.jsp 
     have been copied to this file as it is buttons and associated functions have been
     removed as this is a read only page. -->

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="base.css" />
<link rel="stylesheet" type="text/css" media="all" href="scripts/vendor/jqGrid/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" media="all" href="scripts/vendor/jqGrid/css/cupertino/jquery-ui-1.7.2.custom.css" />
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<script src="scripts/vendor/jqGrid/js/jquery-1.5.2.min.js" type="text/javascript"></script>
<script src="scripts/vendor/jqGrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="scripts/vendor/jqGrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
<script type="text/javascript">
    var selectedItems = new Array();
    jQuery(document).ready(function() {
    <s:if test="contract != null && contract.id != null">
        jQuery("#list").jqGrid({
            url:'get_items_for_contract.action?contract=<s:property value="contract.id"/>',
            datatype: 'json',
            mtype: 'GET',
            colNames:['<s:text name="columnTitle.duePartsInspection.supplier_part_no"/>','<s:text name="label.partNumber"/>','<s:text name="label.sra.partSouce.supPartDescription"/>'],
            colModel:[
                {name:'item_number',label:'item.number', editable: false, sortable:true, search:true},
                {name:'item_oemItemNumber',label:'item.oemItemNumber', editable: false, sortable:false, search:false},
                {name:'item.description',label:'item.description', editable: false, sortable:false, search:false}
            ],
            pager: '#pager',
            rowNum:20,
            imgpath: 'css/smoothness/images',
            sortname: 'item_number',
            sortorder: 'asc',
            multiselect: false,
            autoheight:true,
            height: "100%",
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
            caption: '<s:text name="label.contractAdmin.itemsCovered"/>'
        });
        jQuery("#list").filterToolbar({useparammap:true,searchOnEnter:false});
        jQuery("#list").parents('div.ui-jqgrid-bdiv').css("max-height","300px");
    </s:if>
        jQuery("#addedList").jqGrid({
            datatype: 'local',
            colNames:['','<s:text name="columnTitle.duePartsInspection.supplier_part_no"/>','<s:text name="label.partNumber"/>','<s:text name="label.sra.partSouce.supPartDescription"/>'],
            colModel:[
                {name:'item.id',label:'item.id', hidden:true, editable: false, sortable:false, search:false},
                {name:'item_number',label:'item.number', editable: false, sortable:false, search:false},
                {name:'item.oemItemNumber',label:'item.oemItemNumber', editable: false, sortable:false, search:false},
                {name:'item.description',label:'item.description', editable: false, sortable:false, search:false}
            ],
            pager:"#addedListPager",
            imgpath: 'css/smoothness/images',
            viewrecords: true,
            scrollerbar:true,
            autowidth:true,
            autoheight:true,
            height: "100%",
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
<s:hidden name="searchItemType" id="searchItemType" value="OPTION" />
<s:hidden name="addedItems" id="addedItems" value="" />
<s:hidden name="removedItems" id="removedItems" value="" />
<div class="admin_section_div" style="margin: 5px;">
	<div id="divForPolicies" style="width: 90%; height: 40%;">
		<br />
		<table id="list" class="CenterlizedDiv" style="width: 90%; height: 40%"></table>
		<div id="pager"></div>

		<table id="addedList" style="width: 90%; overflow: auto"></table>
		<div id="addedListPager"></div>
    </div>

	

	
</div>