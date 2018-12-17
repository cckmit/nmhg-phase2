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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<u:repeatTable id="supplier_table" cssClass="grid borderForTable" width="99%" 
cellspacing="0" cellpadding="0" cssStyle="margin:5px;">
    <thead>
        <tr class="row_head">
        	<th width="30%"><s:text name="columnTitle.sraReview.supplier"/></th>
        	<th width="30%"><s:text name="label.supplierNumber"/></th>
        	<th width="30%"><s:text name="label.address"/></th>
            <th width="5%"><u:repeatAdd id="supplier_adder"><div class="repeat_add"/></u:repeatAdd></th>
        </tr>
    </thead>
    
    <u:repeatTemplate id="suppliers_body"
        value="supplierAttrList">
		<tr index="#index">
        	<td style="border:1px solid #EFEBF7">
                
                
                <script type="text/javascript">
					dojo.addOnLoad(function() {
					    dojo.subscribe("/supplier/changed/#index", null, function(data, type, request) {
					        fillSupplierDetails(#index, data, type);
					    });
					});    
			   </script>	
			  
                <sd:autocompleter id='supplier_#index' href='list_suppliers.action' keyName='supplierAttrList[#index].supplier' key='supplierAttrList[#index].supplier.id' loadMinimumCount='0' showDownArrow='false' value='%{supplierAttrList[#index].supplier.name}' notifyTopics='/supplier/changed/#index' cssStyle='font-size:10px;' />
            </td>
            <td style="border:1px solid #EFEBF7">
            	<span id="number_#index">
                	<s:property 
						value="supplierAttrList[#index].supplier.supplierNumber" />
				</span>	
            </td>
            
            <td style="border:1px solid #EFEBF7">
            	<span id="address_#index">
                	<s:property 
						value="supplierAttrList[#index].supplier.displayAddress" />
				</span>	
            </td>
            <td style="border:1px solid #EFEBF7">
                <u:repeatDelete id="supplier_deleter_#index">
                    <div class="repeat_del"/>
                </u:repeatDelete>
            </td>
      </tr>
    </u:repeatTemplate>
</u:repeatTable>
<script type="text/javascript">

function fillSupplierDetails(index, supplierName, type) {
    if (type != "valuechanged") return;
    twms.ajax.fireJavaScriptRequest("find_supplier.action",
        {
            supplierName: supplierName
        },
        function(details) {
            fillSupplierDetailForARow(details, index)
        }
    );
}

function fillSupplierDetailForARow( details  ,index) {
   dojo.byId("number_"+index).innerHTML = details[0];// name
    dojo.byId("address_"+index).innerHTML = details[1] ;//address
   
}
</script>