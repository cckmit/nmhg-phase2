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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<link rel="stylesheet" type="text/css" media="all" href="scripts/vendor/jqGrid/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" media="all" href="scripts/vendor/jqGrid/css/cupertino/jquery-ui-1.7.2.custom.css" />
<script src="scripts/vendor/jqGrid/js/jquery-1.5.2.min.js" type="text/javascript"></script>
<script src="scripts/vendor/jqGrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="scripts/vendor/jqGrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
<script type="text/javascript">
    var selectedItems = new Array();
    
    jQuery(document).ready(function() {
    	
    <s:if test="supplier != null && supplier.id != null">
        jQuery("#supplieritemslist").jqGrid({
            url:'get_locationitems_for_supplier.action?supplier=<s:property value="supplier.id"/>',
            datatype: 'json',
            mtype: 'GET',
            colNames:[
                      '<s:text name="label.gridcolumn.oem_part_no"/>',
                      '<s:text name="label.gridcolumn.oem_part_description"/>',
                      '<s:text name="label.gridcolumn.supplier_part_no"/>',
                      '<s:text name="label.gridcolumn.supplier_part_description"/>',
                      '<s:text name="label.gridcolumn.suplierlocattioncode"/>',
                      '<s:text name="label.gridcolumn.itemfromdate"/>',
                      '<s:text name="label.gridcolumn.itemtodate"/>',
                      '<s:text name="label.gridcolumn.suppllieritemstatus"/>'
                      ],
            colModel:[
              
                {name:"number",label:'supplierItemLocation.itemMapping.fromItem.number', editable: false, sortable:true, search:true},
                {name:'supplierItemLocation.itemMapping.fromItem.description',label:'supplierItemLocation.itemMapping.fromItem.description', editable: false, sortable:false, search:false},
                {name:'supplierItemLocation.itemMapping.toItem.number',label:'supplierItemLocation.itemMapping.toItem.number', editable: false, sortable:true, search:false},
                {name:'supplierItemLocation.itemMapping.toItem.description',label:'supplierItemLocation.itemMapping.toItem.description', editable: false, sortable:false, search:false},
                {name:'supplierItemLocation.locationCode',label:'supplierItemLocation.locationCode', editable: false, sortable:false, search:false},
                {name:'supplierItemLocation.fromDate',label:'supplierItemLocation.fromDate', editable: false, sortable:false, search:false},
                {name:'supplierItemLocation.toDate',label:'supplierItemLocation.toDate', editable: false, sortable:false, search:false},
                {name:'supplierItemLocation.status',label:'supplierItemLocation.status', editable: false, sortable:false, search:false}
            ],
            pager: '#itemspager',
            rowNum:20,
            imgpath: 'css/smoothness/images',
            sortname: 'supplierItemLocation.itemMapping.fromItem.number',
            sortorder: 'asc',
            multiselect: true,
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
                var grid = jQuery("#supplieritemslist");
                for (var i = 0; i < selectedItems.length; i++) {
                    grid.setSelection(selectedItems[i]);
                }
                var count = $("#supplieritemslist").getGridParam("reccount");
                if(count==0)
               	 $("#hideRemove").hide();
               else
               	 $("#hideRemove").show();
            },
            caption: '<s:text name="label.managesupplier.itemsCovered"/>'
        });
        jQuery("#supplieritemslist").filterToolbar({useparammap:true,searchOnEnter:false});
        jQuery("#supplieritemslist").parents('div.ui-jqgrid-bdiv').css("max-height","300px");
        
    </s:if>
        
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
    
  
    function removeItems(){
        if(selectedItems.length > 0){
            var url = "remove_supplier_items.action?";
            for (var i = 0; i < selectedItems.length; i++) {
                url = url + "removedItems[" + i + "]=" + selectedItems[i] + "&";
            }
            dojo.byId("removedItems").value = selectedItems;
            var form =  document.forms[0];
            form.action = url;
            form.method = 'post';
            form.submit();
        }
    }

   function showDialog(){
	   if( dijit.byId("dlgAddItem")!=null)
	   dijit.byId("dlgAddItem").reset();
	   dijit.byId("dlgAddItem").show();
	   
   }
   
   function closeDialog(){
	   if( dijit.byId("dlgAddItem")!=null){
		   dijit.byId("dlgAddItem").hide();
	      
	   }	   
  }
   
   function addSupplierItem(){
		   var form =  document.getElementById('add_supplier_item'); console.debug(form);
	       form.action="add_supplier_location_item";
	       form.submit();
	  
 }


</script>
<s:hidden name="removedItems" id="removedItems" value="" />
<div class="admin_section_div" style="margin: 0px;">
<div id="supplieritems" style="width: 99%;height: auto;">
        <table id="supplieritemslist" class="CenterlizedDiv" style="width: 80%; height: 40%"></table>
		<div id="itemspager"></div>
		<s:if test="supplier != null && supplier.id != null">
        <div align="center">
           <span id="hideRemove"> 
           <input type="button"  class="buttonGeneric" value="<s:text name="label.campaign.removeSerialNumbers"/>" onclick="removeItems()"  />
		   </span>
	       <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addItems"/>" onclick="showDialog()"/> 
       </div>
       </s:if>
</div>
</div>