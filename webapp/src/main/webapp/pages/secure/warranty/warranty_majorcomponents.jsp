

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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<s:if test="inventoryItemMappings.size()==1">

<div dojoType="dijit.layout.ContentPane" >
    <div dojoType="twms.widget.TitlePane"
         title="<s:text name="label.majorComponents"/>"
         id="warranty_majorcomp_info_title" labelNodeClass="section_header" open="true">
	<u:repeatTable id="warranty_major_components_table"
		cssClass="grid borderForTable" width="97%">
		<thead>

			<tr class="row_head">
				<th><s:text name="label.serialNumber" /></th>
				<th><s:text name="label.common.partNumber" /></th>
				<th><s:text name="columnTitle.common.description" /></th>
				<th><s:text name="label.installDate" /></th>
				<th width="9%"><u:repeatAdd id="major_comp_adder">
					<div class="repeat_add" />
				</u:repeatAdd></th>
			</tr>
		</thead>
		<u:repeatTemplate id="major_comps_body"
			value="inventoryItemMappings[0].inventoryItem.composedOf">

			<tr index="#index">
				<s:if test="(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source!=null)&&(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
					<td><s:label
						name="inventoryItemMappings[0].inventoryItem.composedOf[#index].part.serialNumber"
						id="Parts_serialNumber_#index"
						value="%{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.serialNumber}" /></td>
				</s:if>
				<s:else>
					<td><s:textfield
						name="inventoryItemMappings[0].inventoryItem.composedOf[#index].part.serialNumber"
						id="Parts_serialNumber_#index" size="12" /></td>
				</s:else>
				<s:if test="(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source!=null)&&(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
					<td><s:label id="Part_#index_itemNo"
						name="inventoryItemMappings[0].inventoryItem.composedOf[#index].part.ofType"
						value="%{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.ofType.number}" /></td>
				</s:if>
				<s:else>
                    <td><sd:autocompleter id='Part_#index_itemNo' delay='1000' cssStyle='width:65px' href='list_major_components.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='inventoryItemMappings[0].inventoryItem.composedOf[#index].part.ofType' value='%{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.ofType.number}' loadOnTextChange='true' showDownArrow='false' notifyTopics='/oem_part/itemno/changed/#index' />

					</td>
				</s:else>
				<s:if test="(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source!=null)&&(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
					<td>
					<s:label id="Part_Desc#index_itemNo" name="inventoryItemMappings[0].inventoryItem.composedOf[#index].part.ofType.description"
						value="%{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.ofType.description}" />
					</td>
				</s:if>
				<s:else>
					<td><span id="desc_for_part_#index"> </span>
					 <script type="text/javascript">                      	
	   			dojo.addOnLoad( function() {
	               dojo.subscribe("/oem_part/itemno/changed/#index", null, function(data, type, request) {	
		               		                
	                   fillPartDetails(#index, data, type);
	              });               
	         	});
		    </script>
					</td>
				</s:else>
				<s:if test="(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source!=null)&&(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
					<td><s:label
						value="{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.installationDate"
						value="%{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.installationDate}" /></td>
				</s:if>
				<s:else>
					<td><sd:datetimepicker name='inventoryItemMappings[0].inventoryItem.composedOf[#index].part.installationDate' value='%{inventoryItemMappings[0].inventoryItem.composedOf[#index].part.installationDate}' id='part_install_date_#index' />
					</td>
				</s:else>

				<s:if test="(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source!=null)&&(inventoryItemMappings[0].inventoryItem.composedOf[#index].part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
					
					<td></td>
					
				</s:if>
				<s:else>
					<td><u:repeatDelete
						id="major_comps_deleter_#index">
						<div class="repeat_del" />
					</u:repeatDelete></td>
				</s:else>
			</tr>


		</u:repeatTemplate>
	</u:repeatTable>




	<script type="text/javascript">
			
function fillPartDetails(index, number, type, forDealer) {
    if (type != "valuechanged") {
        return;
    }
    twms.ajax.fireJavaScriptRequest("list_description_for_part.action",{    		
            number: number
        }, function(details) {
        	console.debug(details);
        	console.debug(details[1]);
            var tr = findPartRow(index);
            
            if(tr) {
                fillPartDetailForARow(tr, details, index, number);
                delete tr;
            }
        }
    );
}
	    function findPartRow(index) {
			    var tbody = dojo.byId("major_comps_body");
			    var matches = dojo.query("> tr[index=" + index + "]", tbody);
			  
			    return (matches.length == 1) ? matches[0] : null;
		}
      	 
      	function fillPartDetailForARow(tr, details, index,number) {
			if(tr != null){
				
		        var descriptionField = dojo.query("> td:nth-child(3) > span", tr)[0];
		        
		        descriptionField.innerHTML = details; //description
		             
		    }
		 } 
      	 
</script>
</div></div>
</s:if>

