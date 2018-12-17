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
<u:repeatTable id="items_table" 
cssClass="grid borderForTable" theme="twms" width="99%" cellspacing="0" cellpadding="0" cssStyle="margin:5px;">
    <thead>
        <tr class="row_head">
        	<th width="30%"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
        	<th width="65%"><s:text name="label.common.description"/></th>
            <th width="5%"><u:repeatAdd id="items_adder"  theme="twms"><img id="addProductIcon" src="image/addRow_new.gif" border="0"
                 style="cursor: pointer;" title="<s:text name="label.common.addRow"/>"/></u:repeatAdd></th>
        </tr>
    </thead>
               
			   <u:repeatTemplate id="items_body" value="itemAtrrList">
			  <tr index="#index">
            <td><div style="position:relative;top:-6px;"><sd:autocompleter id='part_#index' name='itemAtrrList[#index].item' keyName='itemAtrrList[#index].item' href='list_part_for_association.action' showDownArrow='false' value="%{itemAtrrList[#index].item.number}" notifyTopics='/itemno/changed/#index' cssStyle="position:relative;"/></div>
		     <script type="text/javascript">
					dojo.addOnLoad(function() {
					 dojo.subscribe("/itemno/changed/#index", null, function(data, type, request) {
			                          fillPartDetails(#index, data, type);
					    });
					});    
			  </script>	
		  
		    </td>
            <td>
            	<span >
                	<s:property 
						value="itemAtrrList[#index].item.description" />
				</span>	
            </td>
            <td>
            <s:hidden name="itemAtrrList[#index]" value="%{itemAtrrList[#index].id}"/> 
                <u:repeatDelete id="items_deleter_#index" theme="twms">
                    <img id="deleteConfiguration" src="image/remove.gif" border="0" style="cursor: pointer;"
                              title="<s:text name="label.common.deleteRow" />"/>
                </u:repeatDelete>
            </td>
      </tr>
       
    </u:repeatTemplate>
</u:repeatTable>
 
<script type="text/javascript">
function fillPartDetails(index, number, type) {
    if (type != "valuechanged") {
        return;
    }
    twms.ajax.fireJavaScriptRequest("list_oem_part_details.action",
        {
            number: number
        },
        function(details) {
            var tr = findOemPartRow(index);
            fillOemPartDetailForARow(tr, details)
            delete tr;
        }
    );
}
function fillOemPartDetailForARow(tr, details, index) {
   if(tr != null){
        var descriptionField = dojo.query("> td:nth-child(2) > span", tr)[0];
        descriptionField.innerHTML = details[0]; //description
        delete descriptionField;
     }
}

function findOemPartRow(index) {
    var tbody = dojo.byId("items_body");
    var matches = dojo.query("> tr[index=" + index + "]", tbody);

    return (matches.length == 1) ? matches[0] : null;
}
</script>