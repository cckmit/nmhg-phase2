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
  <script type="text/javascript">  
		dojo.require("twms.widget.Tree");
        dojo.require("twms.widget.TreeModel");
		dojo.require("twms.data.EmptyFileWriteStore");
      	dojo.require("dijit.form.Button");
      	dojo.require("twms.widget.Dialog");
      	dojo.require("dojox.layout.ContentPane");   
    </script>


<u:repeatTable id="inv_table1" cssClass="grid borderForTable" width="99%" 
cssStyle="margin:5px;" cellspacing="0" cellpadding="0">
    <thead>
        <tr class="row_head">
        	<th width="15%"><s:text name="columnTitle.common.product"/></th>
        	<th width="15%"><s:text name="columnTitle.common.model"/></th>
            <th width="9%"><u:repeatAdd id="inv_adder"><img id="addProductIcon" src="image/addRow_new.gif" border="0"
                 style="cursor: pointer;" title="<s:text name="label.common.addRow"/>"/></u:repeatAdd></th>
        </tr>
    </thead>
    
    <u:repeatTemplate id="inv_body1"
        value="claimedInvAttList">  
		<tr index="#index">
		  <s:hidden name="claimedInvAttList[#index]" value="%{claimedInvAttList[#index].id}" id="claimedInvAttList#index"/>     
			<td style="border:1px solid #EFEBF7">
			<s:hidden id ="association_#index" name="claimedInvAttList[#index].associated" value="true" ></s:hidden>
			 	   <sd:autocompleter name='claimedInvAttList[#index].product.name' keyName='claimedInvAttList[#index].product' href='list_product_for_inventory_association.action' showDownArrow='false' loadMinimumCount='3' value='%{claimedInvAttList[#index].product.name}' key='%{claimedInvAttList[#index].product.id}' id='product_#index' />
		    </td>
            <td style="border:1px solid #EFEBF7">
            	<sd:autocompleter name='claimedInvAttList[#index].itemGroup' keyName='claimedInvAttList[#index].itemGroup' href='list_model_for__inventory_association.action' loadMinimumCount='0' value='%{claimedInvAttList[#index].itemGroup.name}' key='%{claimedInvAttList[#index].itemGroup.id}' id='model_#index' listenTopics='/product/queryAddParams/#index' />
				
				<script type="text/javascript">
				  	dojo.addOnLoad(function() {
				  		var url= "list_model_for__inventory_association.action";
                        var productName = '<s:property value="%{claimedInvAttList[#index].product.name}"/>';
                        var productId = '<s:property value="%{claimedInvAttList[#index].product.id}"/>';
                        var product= dijit.byId("product_#index");
                        dojo.connect(dijit.byId("product_#index"), "onChange", function() {
                            dojo.publish("/product/queryAddParams/#index", [{
                                url: url,
                                params: {
                                    productId: product.getValue()  ? product.getValue() : productId 
                                }
                            }]);
                        });
                    
	               });
			  </script>									        	
            
           
            <td style="border:1px solid #EFEBF7">
                  <u:repeatDelete id="inv_tableDeleter1_#index" theme="twms">
                    <img id="deleteConfiguration" src="image/remove.gif" border="0" style="cursor: pointer;"
                              title="<s:text name="label.common.deleteRow" />"/>
                </u:repeatDelete>
            </td>
      </tr>
       <s:hidden name="claimedInvAttList[#index]" value="%{claimedInvAttList[#index].id}" />        
    </u:repeatTemplate>
</u:repeatTable>
<script type="text/javascript">

 function  createHiddenElement(labelSuffix,idSuffix,tdSuffix,index){
 	var parentTD= dojo.byId(tdSuffix +index);
 	var td = getExpectedParent(parentTD, "td");
    var hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name="claimedInvAttList["+ index +"]." + labelSuffix;		   	   		   
    hiddenInput.id = idSuffix + index;
    td.appendChild(hiddenInput);
  }
  
  function setHiddenValue(index){
  	dojo.byId("association_"+ index).value = false;
  }
</script>
