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
<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    

    
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript">
   		dojo.require("dojox.layout.ContentPane");
        function validate(inputComponent) {

        }
        
		function disablePercentage(varIndex){
			var percentageLable = dojo.byId("percentageLable_"+varIndex);
			if(dojo.byId("flat_"+varIndex).checked){
				 dojo.html.hide(percentageLable);
			}
			else{
				dojo.html.show(percentageLable);
			}   
		}      
		dojo.addOnLoad ( function() {
		var i = 0;
			for(i=0;i<8;i++){
				if(dojo.byId("percentageLable_"+i) != null) {
					var percentageLable = dojo.byId("percentageLable_"+i);
					if(dojo.byId("flat_"+i).checked){
						 dojo.html.hide(percentageLable);
					}
					else{
						dojo.html.show(percentageLable);
					}
				} 
			}
      	});
		
		dojo.addOnLoad(function(){
			dojo.html.hide(dojo.byId("loadingIndicationDiv"));
			dojo.connect(dijit.byId("dealerAutoComplete"), "onBlur", function() {
                populateServicingLocationsForDealer(dijit.byId("dealerAutoComplete").getValue());
            });
		});
		
		function populateServicingLocationsForDealer(dealerCriterion, modifier) {
            dojo.html.hide(dojo.byId("servicingLocationData"));
            dojo.html.show(dojo.byId("loadingIndicationDiv"));
            dojo.html.hide(dojo.byId("selectedServicingLocation"));
            twms.ajax.fireHtmlRequest("list_servicing_locations_for_dealer.action", {dealerCriterion : dealerCriterion}, function(data) {
                dijit.byId("servicingLocationData").setContent(data);
                dojo.html.show(dojo.byId("servicingLocationData"));
                dojo.html.hide(dojo.byId("loadingIndicationDiv"));
            });
        }

    </script>
   <style>
   .labelStyle{
   padding-left:5px;
   }
   
   </style> 
</head>
<u:body>
<form name="baseForm" id="baseForm">
<s:actionerror theme="xhtml"/>
<s:fielderror theme="xhtml"/>
<s:actionmessage theme="xhtml"/>
<s:hidden name="id" />
<s:hidden name="paymentVariableId" value="%{modifier.forPaymentVariable.id}" />
<s:hidden name="modifier.forPaymentVariable.id" />
<s:hidden name="dealerGroupSelected" id="isDealerGroup"></s:hidden>
<div class="admin_section_div" style="margin:5px;width:100%">
    <div class="admin_section_heading"><s:text name="label.managePayment.modifier" /></div>
   			
    	<div class="mainTitle" style="margin:10px 0px 0px 0px">
    	<s:text name="label.common.conditions" />
    	</div>
    	<div class="borderTable">&nbsp;</div>
    	
    	<div style="margin-top:-10px;">
		<table width="100%" class="grid" cellspacing="0" cellpadding="0">
			<tr>
				<td class="labelStyle">					
					<s:text name="label.managePayment.modifierName" />:
				</td>
				<td>
					<s:property value = "modifier.forPaymentVariable.name" />
				</td>				
			</tr>
			
			<tr>
				<td class="labelStyle">
					<s:text name="label.managePayment.section" />:
				</td>
				<td>
					<s:text name = "%{getMessageKey(modifier.forPaymentVariable.section.name)}" />
				</td>
			</tr>
		
    		<tr>
        		<td class="admin_data_table" width="20%">
        			<div id="dealerLabel">
				   			<s:text name="label.common.dealer"/>:
			   			</div>
			   			<div id="dealerGroupLabel">
				   			<s:text name="label.common.dealerGroupLabel"/>
				   		</div>
			   			<div id="toggle" style="cursor:pointer;">
				   			<div id="toggleToDealerGroup" class="clickable">
					   			<s:text name="toggle.common.toDealerGroup"/>
				   			</div>
				   			<div id="toggleToDealer" class="clickable">
					   			<s:text name="toggle.common.toDealer"/>
					   		</div>
				   		</div>
        		</td>
        		<td width="80%">
        			<div id="dealer">
          			<sd:autocompleter href='list_part_return_Dealers.action' id='dealerAutoComplete' name='dealerCriterion' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
              </div>
              <div id="dealerGroup">
              	<sd:autocompleter href='list_part_return_DealerGroupsInModifiers.action' id='dealerGroupAutoComplete' name='dealerGroupName' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
              </div>
        		</td>
        		<tr>
        			<td class="admin_data_table" width="20%" id="servicingLocationLabel">
        			<s:text name="label.modifier.servicingLocation" />:</td>
        			<td id="selectedServicingLocation">
					<s:if test="modifier.servicingLocation != null">
						<s:text name = "modifier.servicingLocation.getShipToCodeAppended()" />
					</s:if>
					<s:else>
						<s:label value="--" />
					</s:else>
					</td>
					<td>
        				<div dojoType="dojox.layout.ContentPane" id="servicingLocationData"></div>
        				<div id="loadingIndicationDiv" style="width: 100%; height: 88%;">
            				<div class='loadingLidThrobber'>
               					<div class='loadingLidThrobberContent'>Loading...</div>
            				</div>
        				</div>
              		</td>
              		<td></td>
        		</tr>
				<tr>
					<td class="labelStyle">
        			<s:text name="label.common.warrantyType" />:
        		</td>
        		<td >
        		   <s:select name="modifier.forCriteria.warrantyType" theme="twms" list="warrantyTypes"
                          id="warrantyType" cssStyle="width:130px;" listkey="type" listValue="%{getText(displayValue)}" headerKey="" 
                          headerValue='%{getText("dropdown.common.all")}' />
    				<script type="text/javascript">
    					dojo.byId("warrantyType").value = "<s:property value="modifier.forCriteria.warrantyType" />";
    				</script>
        		</td>
			</tr>
      		<tr>
    	    	<td class="labelStyle">
    	    		<s:text name="label.common.claimType" />:
    	    	</td>
    	    	<td >    	    	    
    				<select id="claimType" name="modifier.forCriteria.claimType" style="width:15%;">
    					    <option value="ALL"><s:text name="dropdown.common.all"/></option>
    					 <s:iterator value="claimTypes">
                            <option value='<s:property value="type.toUpperCase()" />'><s:text name="getText(getDisplayType())"/></option>
                         </s:iterator>
    				</select>
    				<script type="text/javascript">
                        <s:if test="modifier.id==null">
                            dojo.byId("claimType").value="ALL";
                        </s:if>
                        <s:else>
                        dojo.byId("claimType").value = "<s:property value="modifier.forCriteria.claimType.type.toUpperCase()" />";
                        </s:else>
    				</script>   				
    		
    			</td>
    			
    		</tr>
			<tr>
			<td class="labelStyle">
    				<s:text name="label.common.products" />:
        		</td>
        		<td >
        			<sd:autocompleter href='list_part_return_Products.action' name='productType' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
        		</td>
			</tr>
			<tr>
	                    <td class="admin_data_table">
	                        <s:text name="label.warrantyAdmin.customerType"/>:
	                    </td>
	                    <td >
	                        <s:select list="customerTypes" name="modifier.customerType" listKey="key" listValue="value" headerKey="ALL" headerValue="All"  />
	                        
	                    </td>
            </tr>
            	<tr>
            	
            	<s:if test="modifier.forPaymentVariable.section.name =='Oem Parts'">
	                    <td class="admin_data_table">
	                        <s:text name="label.warrantyAdmin.landedCost"/>:
	                    </td>
	                    <td >                    	                      
	     	 <input type="checkbox" name="landedCost" value="true" <s:if test="modifier.landedCost">checked="checked"</s:if>/>

	                    </td>
	                    </s:if>
            </tr>
            
			
			
    	</table>
		
    </div>
   
	<div  class="mainTitle" style="margin:10px 0px 0px 0px">
	<s:text name="label.common.rates"/></div>
	
		<u:repeatTable id="myTable" cssClass="grid borderForTable" cellpadding="0" cssStyle="margin:5px;" cellspacing="0" width="100%" theme="twms">
			<thead>
				<tr class="labelStyle">
					<th width="62%" class="colHeader"><s:text name="label.managePayment.validityDate"/></th>
					<th width="36%" class="colHeader"><s:text name="label.managePayment.percentage"/></th>
					<th width="2%" class="colHeader">
						<u:repeatAdd id="adder" theme="twms">
				        	<img id="addEntry"  align="center" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.managePayment.add" />" />
				       	</u:repeatAdd>
					</th>
				</tr>
			</thead>
			<u:repeatTemplate id="mybody" value="entries" index="myindex" theme="twms">
			    <tr index="#myindex">
			    	<td width="62%">
			    		<s:hidden name="entries[#myindex]"></s:hidden>
			    		<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td width="8%"><s:text name="label.common.from"/>:</td>
								<td width="40%">
									<sd:datetimepicker name='entries[#myindex].duration.fromDate' value='%{entries[#myindex].duration.fromDate}' id='startDate_#myindex' />
								</td>
								<td width="4%"><s:text name="label.common.to"/>:</td>
								<td width="48%">
									<sd:datetimepicker name='entries[#myindex].duration.tillDate' value='%{entries[#myindex].duration.tillDate}' id='endDate_#myindex' />
								</td>
							</tr>
						</table>
					</td>
					<td valign="top" width="36%" style="border-left:1px solid #DCD5CC;" align="center">
							<s:textfield name="entries[#myindex].percentage" size="6" />
							<span id="percentageLable_#myindex">%</span>
							<s:checkbox id="flat_#myindex" name="entries[#myindex].isFlatRate"  onclick="disablePercentage(#myindex)"/>						
	                        <s:text name ="label.admin.travelFlatRate" />
					</td>
					<td valign="top" width="2%" style="border-left:1px solid #DCD5CC;">
						<u:repeatDelete id="deleter_#myindex" theme="twms">
			            	<img id="deleteEntry" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
			            </u:repeatDelete>
					</td>
				</tr>
			</u:repeatTemplate>
		</u:repeatTable>

    <br>
</div>
<div align="center">
	<s:if test="%{modifier.id == null}">
    	<s:submit cssClass="buttonGeneric" value="%{getText('button.common.create')}"  action="create_payment_modifier"/>
	</s:if>
	<s:else>
		<s:submit cssClass="buttonGeneric" value="%{getText('button.common.update')}"  action="update_payment_modifier"/>	
		<s:submit cssClass="buttonGeneric" value="%{getText('button.common.delete')}"  action="delete_payment_modifier"/>	
	</s:else>
</div>
</form>
<script type="text/javascript">
	dojo.addOnLoad(function() {
	    <s:if test="dealerGroupSelected">
	      showDealerGroup();
      </s:if>
      <s:else>
      	showDealer();
      </s:else>
	
	    dojo.connect(dojo.byId("toggleToDealer"), "onclick", function() {
	        showDealer();
	        dojo.html.show(dojo.byId("servicingLocationData"));
	        dojo.html.show(dojo.byId("servicingLocationLabel"));
	    });
	    dojo.connect(dojo.byId("toggleToDealerGroup"), "onclick", function() {
			dojo.html.hide(dojo.byId("loadingIndicationDiv"));
			dojo.html.hide(dojo.byId("servicingLocationData"));
			dojo.html.hide(dojo.byId("selectedServicingLocation"));
			 dojo.html.hide(dojo.byId("servicingLocationLabel"));
	        showDealerGroup();
	    });
	});
</script>
</u:body>
</html>