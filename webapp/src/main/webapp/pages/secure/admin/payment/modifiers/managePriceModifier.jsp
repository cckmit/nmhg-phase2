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
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript">
        function validate(inputComponent) {

        }

    </script>
</head>
<u:body>
<form name="baseForm" id="baseForm" style="width:99%">
<u:actionResults/>
 <s:hidden name="id" />
<s:hidden name="itemGroupSelected" id="isItemGroup"></s:hidden> 
<div class="admin_section_div">
    <div class="admin_section_heading"><s:text name="label.manageRates.itemPriceModifierConfig" /></div>
    <div class="admin_subsection_div">
    	<div class="admin_section_subheading"><s:text name="label.manageRates.conditions" /></div>
    	
		<table width="100%" border="0" cellpadding="0" cellspacing="0"  class="admin_selections">
                              <tr>
                                
								
					 				<td  width="10%" style="color:grey; font-weight:bold; padding-left:5px;">
					   				<div id="itemLabel">
							   			<s:text name="label.common.item"/>:
						   			</div>
						   			<div id="itemGroupLabel">
							   			<s:text name="label.common.itemGroupLabel"/>:
							   		</div>
						   			<div id="toggleToItemGroup" class="clickable">
							   			<s:text name="toggle.common.toItemGroup"/>
						   			</div>
						   			<div id="toggleToItem" class="clickable">
							   			<s:text name="toggle.common.toItem"/>
							   		</div>
							   	</td>
						     	<td width="90%" >
						    			<div id="item" >
						    			<s:if test="%{itemPrice.id == null}">
						      			<sd:autocompleter href='list_part_return_ItemCriterions.action' id='itemAutoComplete' name='partCriterion' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='false' />
						                 </s:if>
						                 <s:else>
						                   <sd:autocompleter href='list_part_return_ItemCriterions.action' id='itemAutoComplete' name='partCriterion' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='false' value='%{itemPrice.itemCriterion.itemIdentifier}' />
						                 </s:else>
						               
						         </div>
						         <div id="itemGroup">
						         
						           <s:if test="%{itemPrice.id == null}">
						         	<sd:autocompleter href='list_part_return_ItemGroupsForItemPrices.action' id='itemGroupAutoComplete' name='itemGroupName' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='false' />
						           </s:if>
						           <s:else>
						             <sd:autocompleter href='list_part_return_ItemGroupsForItemPrices.action' id='itemGroupAutoComplete' name='itemGroupName' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='false' value='%{itemPrice.itemCriterion.itemIdentifier}' />
						           </s:else>
						           
						         </div>
						        </td>
							  </tr>
                              <tr>
                                <td width="25%" nowrap="nowrap" class="tableDataWhiteBgBold" ><s:text name="label.manageRates.warrantyType" />:</td>
                                <td width="75%" >
                                 <s:select name="itemPrice.forCriteria.warrantyType" theme="twms" list="warrantyTypes"
                          			id="warrantyType" cssStyle="width:130px;" listkey="type" listValue="%{getText(displayValue)}" headerKey="" 
                         			 headerValue='%{getText("dropdown.common.all")}' />
				    				<script type="text/javascript">
				    					dojo.byId("warrantyType").value = "<s:property value="itemPrice.forCriteria.warrantyType" />";
				    				</script>

                                </td>
                              </tr>
                            </table>
		
		
		
    </div>
    <div class="admin_subsection_div">
	<div class="admin_section_subheading"><s:text name="label.manageRates.modifierValue"/></div>
		<u:repeatTable id="myTable" cssClass="admin_entry_table" cellpadding="1" cellspacing="0" width="100%" theme="twms">
			<thead>
				<tr class="admin_table_header">
					<th width="62%"><s:text name="label.manageRates.repairDate"/></th>
					<th width="35%"><s:text name="label.manageRates.priceModifier"/></th>
					<th width="3%" >
					<div align="center" style="padding-right:3px">
						<u:repeatAdd id="adder" theme="twms">
				        	<img id="addPrice"  align="center" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px;" title="<s:text name="label.manageRates.addPriceEntry" />" />
				       	</u:repeatAdd>
					</div>
					</th>
				</tr>
			</thead>
			<u:repeatTemplate id="mybody" value="priceEntries" index="myindex" theme="twms">
			    <tr index="#myindex">
			    	<td width="62%">
			    		<s:hidden name="priceEntries[#myindex]"></s:hidden>
			    		<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td width="8%" style="color:grey; font-weight:bold;"><s:text name="label.common.from"/>:</td>
								<td width="40%">
									<sd:datetimepicker name='priceEntries[#myindex].duration.fromDate' value='%{priceEntries[#myindex].duration.fromDate}' id='startDate_#myindex' />	
								</td>
							<td width="4%" style="color:grey; font-weight:bold;"><s:text name="label.common.to"/>:</td>
								<td width="48%" >
									<sd:datetimepicker name='priceEntries[#myindex].duration.tillDate' value='%{priceEntries[#myindex].duration.tillDate}' id='endDate_#myindex' />
								</td>
							</tr>
						</table>
					</td>		
					<td valign="top" width="35%" style="border-left:1px solid #DCD5CC;">
						<s:textfield name="priceEntries[#myindex].scalingFactor" />
					</td>
					<td valign="top" width="3%" style="border-left:1px solid #DCD5CC;">
					<div align="center" style="padding-right:3px">
						<u:repeatDelete id="deleter_#myindex" theme="twms">
			            	<img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer;padding-right:4px;" title="<s:text name="label.manageRates.deletePriceEntry" />"/>
			            </u:repeatDelete>
						</div>
					</td>
				</tr>
			</u:repeatTemplate>
		</u:repeatTable>
	</div>
    <br>
</div>
    <div align="center">
    	<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
    	<s:if test="%{itemPrice.id == null}">
	    	<s:submit cssClass="buttonGeneric" value="%{getText('button.common.save')}"  action="save_item_price_modifier"/>
    	</s:if>
    	<s:else>
    		<s:submit cssClass="buttonGeneric" value="%{getText('button.common.save')}"  action="update_item_price_modifier"/>	
    	</s:else>
    </div>

</form>
<script type="text/javascript">
	dojo.addOnLoad(function() {
		var toggler = dojo.byId("toggleToItem");
		if(toggler != null) {
		<s:if test="%{itemPrice.itemCriterion.itemGroup.name!=null}">
			showItemGroup();
	    </s:if>
	    <s:else>
	    showItem();
	   </s:else>
	    dojo.connect(toggler, "onclick", function() {
	        showItem();
	    });
	    dojo.connect(dojo.byId("toggleToItemGroup"), "onclick", function() {
	        showItemGroup();
	    });
	  }
	});
	
	<s:if test="!actionMessages.isEmpty()">
          dojo.addOnLoad(function() {
		    manageTableRefresh("itemPriceConfigTable");
        });
    </s:if>
</script>
<authz:ifPermitted resource="warrantyAdminItemPriceModifiersReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>