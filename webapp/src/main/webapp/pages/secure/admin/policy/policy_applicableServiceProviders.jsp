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

<script type="text/javascript">
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");

    var __rowTemplateDealer = "<tr  name='dealer_row' id='dealer_row_${index}'>" +
        "<td style='padding-left:5px; height:25px'>" +
        "<input type='hidden' name='selectedDealers[${index}]'" +
        " value='${dealerId}' />${dealerName}</td>"+
        "<td align='left' width='9%'><img id='removeDealerIcon_${index}'" +
        " src='image/remove.gif' border='0'" +
        " style='cursor: pointer;margin-left: 25px;'" +
        " title='<s:text name="label.managePolicy.deleteDealer" />'/>" +
        "</td></tr>";

        var __rowTemplateDealerGroup = "<tr name='dealer_group_row' id='dealer_group_row_${index}'>" +
        "<td style='padding-left:5px; height:25px'>" +
        "<input type='hidden' name='selectedDealerGroups[${index}]'" +
        " value='${dealerId}' />${dealerName}</td>"+
        "<td align='left' width='9%'><img id='removeDealerGroupIcon_${index}'" +
        " src='image/remove.gif' border='0'" +
        " style='cursor: pointer;margin-left: 25px;'" +
        " title='<s:text name="label.managePolicy.deleteDealerGroup" />'/>" +
        "</td></tr>";
    var __nextAvailableIndexForDealer = 0, __dealerAutoCompleter, __tbodyDealer;

    var __nextAvailableIndexForDealerGroups = 0, __dealerGroupAutoCompleterExt, __tbodyDealer;

   

    <s:if test="!selectedDealers.empty">
    __nextAvailableIndexForDealer = <s:property value="selectedDealers.size" />;
    </s:if>

    <s:if test="!selectedDealerGroups.empty">
    __nextAvailableIndexForDealerGroups = <s:property value="selectedDealerGroups.size" />;

    </s:if>
    

        function wireDealerRemoval(/*number*/ dealerIndex,
        /*boolean*/ newlyAddedRow) {                   
            dojo.connect(dojo.byId("removeDealerIcon_" + dealerIndex),
            "onclick", function() {
                var rowDealer = dojo.byId("dealer_row_" + dealerIndex);
              if(!newlyAddedRow) {
                    requestDeletion(rowDealer, "selectedDealers");
               }
            
                dojo.dom.destroyNode(rowDealer);
            });
        }

        function wireDealerGroupRemoval(/*number*/ dealerIndex,
                /*boolean*/ newlyAddedRow) {
                    dojo.connect(dojo.byId("removeDealerGroupIcon_" + dealerIndex),
                    "onclick", function() {
                        var rowDealer = dojo.byId("dealer_group_row_" + dealerIndex);
                      if(!newlyAddedRow) {
                            requestDeletion(rowDealer, "selectedDealerGroups");
                      }
                    
                        dojo.dom.destroyNode(rowDealer);
                    });
                }

        function addDealerToPolicy() {
        	 var selectedBox = dijit.byId("dealerAutocompleter").disabled;
             var selectedBoxGroupStd = dijit.byId("dealerGroupAutocompleterStd").disabled;
             if(selectedBox==false)
             {            
            var rowMarkupDealer = dojo.string.substitute(__rowTemplateDealer, {
               index : __nextAvailableIndexForDealer,
               dealerName : __dealerAutoCompleter.getDisplayedValue(),
               dealerId : __dealerAutoCompleter.getValue()
            });
            var trNodeDealer = dojo.html.createNodesFromText(rowMarkupDealer);
            __tbodyDealer .appendChild(trNodeDealer);

            wireDealerRemoval(__nextAvailableIndexForDealer, true);

            __nextAvailableIndexForDealer++;
            
             }
             else if(selectedBoxGroupStd==false)
             {            	
            	 var rowMarkupDealer = dojo.string.substitute(__rowTemplateDealerGroup, {
                     index : __nextAvailableIndexForDealerGroups,
                     dealerName : __dealerGroupAutoCompleterStd.getDisplayedValue().toUpperCase(),
                     dealerId : __dealerGroupAutoCompleterStd.getValue().toUpperCase()
                  });
                  var trNodeDealer = dojo.html.createNodesFromText(rowMarkupDealer);
                  __tbodyDealer .appendChild(trNodeDealer);

                  wireDealerGroupRemoval(__nextAvailableIndexForDealerGroups, true);

                  __nextAvailableIndexForDealerGroups++;
             }
             else
             {            	
                  var rowMarkupDealer = dojo.string.substitute(__rowTemplateDealerGroup, {
                     index : __nextAvailableIndexForDealerGroups,
                     dealerName : __dealerGroupAutoCompleterExt.getDisplayedValue().toUpperCase(),
                     dealerId : __dealerGroupAutoCompleterExt.getValue().toUpperCase()
                  });
                  var trNodeDealer = dojo.html.createNodesFromText(rowMarkupDealer);
                  __tbodyDealer .appendChild(trNodeDealer);

                  wireDealerGroupRemoval(__nextAvailableIndexForDealerGroups, true);

                  __nextAvailableIndexForDealerGroups++;    
             }
            dijit.byId("dlgAddDealer").hide();
        }

        dojo.addOnLoad(function() {
        	  dojo.html.hide(dojo.byId("toggleToDealerName"));
              dojo.html.hide(dojo.byId("dealerGroupText"));
              dojo.html.hide(dijit.byId("dealerGroupAutocompleterStd").domNode);
              dojo.html.hide(dijit.byId("dealerGroupAutocompleterExt").domNode);
              dijit.byId("dealerGroupAutocompleterStd").setDisabled(true);
              dijit.byId("dealerGroupAutocompleterExt").setDisabled(true);
              if(dijit.byId("warrantyType")!=null)
              var warrantyType = dijit.byId("warrantyType").getValue();
	         	var policyDefWarrantyType='<s:property value="policyDefinition.warrantyType"/>';
          	
            if(warrantyType=="POLICY" || (policyDefWarrantyType!=null && policyDefWarrantyType=="POLICY"))
            {
              dijit.byId("dealerAutocompleter").setDisabled(true);
              dojo.html.hide(dojo.byId("toggleToDealerGroup"));
            }
             __tbodyDealer  = dojo.query("table#tblSelectedDealers tbody")[0];

            var validationTopicDealer = "/policyDefn/addDealer";
            __dealerAutoCompleter = dijit.byId("dealerAutocompleter");
            __dealerGroupAutoCompleterStd = dijit.byId("dealerGroupAutocompleterStd");
            __dealerGroupAutoCompleterExt = dijit.byId("dealerGroupAutocompleterExt");
            
            // replaced setValidationNotificationTopics with addNotifyTopic, 
            // setValidationNotificationTopics get's called when ever focus is lost or on any modifications 
            // we need not get all the notifications we should just listen for valid value and enable the button
            __dealerAutoCompleter.addNotifyTopic(validationTopicDealer); // replaced setValidationNotificationTopics with addNotifyTopic
            __dealerGroupAutoCompleterStd.addNotifyTopic(validationTopicDealer);
            __dealerGroupAutoCompleterExt.addNotifyTopic(validationTopicDealer);

            var btnAddDealer = dojo.byId("btnAddDealer");
            
            dojo.subscribe(validationTopicDealer, function(message) { // this will be called if and only if the valiue is valid
                btnAddDealer.disabled = false;
            });

            dojo.connect(dojo.byId("addDealerIcon"), "onclick",
                function() {
            	dojo.html.hide(dojo.byId("toggleToDealerName"));
                dojo.html.hide(dojo.byId("dealerGroupText"));
                dojo.html.hide(dijit.byId("dealerGroupAutocompleterStd").domNode);
                dojo.html.hide(dijit.byId("dealerGroupAutocompleterExt").domNode);
                dijit.byId("dealerGroupAutocompleterStd").setDisabled(true);
                dijit.byId("dealerGroupAutocompleterExt").setDisabled(true);
                dijit.byId("dealerAutocompleter").setDisabled(false);
                dojo.html.show(dojo.byId("dealerNameText"));
                dojo.html.show(dojo.byId("toggleToDealerGroup"));
                dojo.html.show(dijit.byId("dealerAutocompleter").domNode);
                    __dealerAutoCompleter.setDisplayedValue("");
                    __dealerGroupAutoCompleterStd.setDisplayedValue("");
                    __dealerGroupAutoCompleterExt.setDisplayedValue("");
                    btnAddDealer.disabled = true;
                    dijit.byId("dlgAddDealer").show();
                }
            );

            dojo.connect(btnAddDealer, "onclick",
                addDealerToPolicy
            );
        });


        function showDealerGroup() {
        	if(dijit.byId("warrantyType")!=null)
        		var warrantyType = dijit.byId("warrantyType").getValue();
        	var policyDefWarrantyType='<s:property value="policyDefinition.warrantyType"/>';			
        	 if(warrantyType=="STANDARD" || (policyDefWarrantyType!=null && policyDefWarrantyType=="STANDARD"))
        	 {		
        	       dojo.html.show(dijit.byId("dealerGroupAutocompleterStd").domNode);
        	       dijit.byId("dealerGroupAutocompleterStd").setDisabled(false);
        	 }
        	 if(warrantyType=="EXTENDED" || (policyDefWarrantyType!=null && policyDefWarrantyType=="EXTENDED"))
        	  {		
        	        dojo.html.show(dijit.byId("dealerGroupAutocompleterExt").domNode);
        	        dijit.byId("dealerGroupAutocompleterExt").setDisabled(false);
        	  }
        	/*    else
        	    {
        	    	   	 dojo.html.hide(dijit.byId("dealerGroupAutocompleterExt").domNode);
        	             dijit.byId("dealerGroupAutocompleterExt").setDisabled(true);
        	             dojo.html.hide(dijit.byId("dealerGroupAutocompleterStd").domNode);
        	             dijit.byId("dealerGroupAutocompleterStd").setDisabled(true);
        	                        	                 
        	     }  */
        	            dojo.html.show(dojo.byId("dealerGroupText"));
        	            dojo.html.show(dojo.byId("toggleToDealerName"));
        	            dojo.html.hide(dijit.byId("dealerAutocompleter").domNode);
        	            dijit.byId("dealerAutocompleter").setDisabled(true);
        	            dojo.html.hide(dojo.byId("dealerNameText"));
        	            dojo.html.hide(dojo.byId("toggleToDealerGroup"));
        	        }

        	        function showDealerName() {
        	        	if(dijit.byId("warrantyType")!=null)
        	        	var warrantyType = dijit.byId("warrantyType").getValue();
        	         	var policyDefWarrantyType='<s:property value="policyDefinition.warrantyType"/>';
        	            dojo.html.hide(dijit.byId("dealerGroupAutocompleterStd").domNode);
        	            dojo.html.hide(dijit.byId("dealerGroupAutocompleterExt").domNode);
        	            dijit.byId("dealerGroupAutocompleterStd").setDisabled(true);
        	            dijit.byId("dealerGroupAutocompleterExt").setDisabled(true);
        	            dojo.html.hide(dojo.byId("dealerGroupText"));
        	            dojo.html.hide(dojo.byId("toggleToDealerName"));
        	            dojo.html.show(dijit.byId("dealerAutocompleter").domNode);
        	            dijit.byId("dealerAutocompleter").setDisabled(false);
        	            dojo.html.show(dojo.byId("dealerNameText"));
        	            dojo.html.show(dojo.byId("toggleToDealerGroup"));
        	            if(warrantyType=="POLICY" || (policyDefWarrantyType!=null && policyDefWarrantyType=="POLICY"))
        	        	{
        	        		 dojo.html.show(dijit.byId("dealerAutocompleter").domNode);
        	                 dijit.byId("dealerAutocompleter").setDisabled(true);
        	        	} 
        	        }
        	        
</script>

<table id="tblSelectedDealers" class="grid borderForTable"
	cellpadding="0" cellspacing="0" style="width: 97%">
	<thead>
		<tr class="row_head">
			<th width="91%" style="border-right:0;"><s:text name="label.managePolicy.addDealerDealerGroup" /></th>
			<th width="9%" align="center" style="border-left:0;"><img id="addDealerIcon"
				src="image/addRow_new.gif" border="0" style="cursor: pointer;"
				title="<s:text name="label.managePolicy.addDealerDealerGroup" />" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="selectedDealers" status="selectedDealer">
			<tr id="dealer_row_<s:property value="%{#selectedDealer.index}"/>">
				<td style="padding-left: 5px; height: 25px"><s:hidden
					name="selectedDealers[%{#selectedDealer.index}]" /><s:property
					value="name" /></td>

				<td align="left" width="9%"><img
					id="removeDealerIcon_<s:property value="%{#selectedDealer.index}"/>"
					src="image/remove.gif" border="0"
					style="cursor: pointer; margin-left: 25px;"
					title="<s:text name="label.managePolicy.deleteDealer" />" /> <script
					type="text/javascript">
                        dojo.addOnLoad(function() {
                            wireDealerRemoval(<s:property value="%{#selectedDealer.index}"/>);
                        });
                    </script></td>
			</tr>
		</s:iterator>


		<s:iterator value="selectedDealerGroups" status="selectedDealerGroup">
			<tr
				id="dealer_group_row_<s:property value="%{#selectedDealerGroup.index}"/>">
				<td style="padding-left: 5px; height: 25px"><s:hidden
					name="selectedDealerGroups[%{#selectedDealerGroup.index}]" /> <s:property
					value="selectedDealerGroups[#selectedDealerGroup.index]" /> <s:property
					value="name" /></td>

				<td align="left" width="9%"><img
					id="removeDealerGroupIcon_<s:property value="%{#selectedDealerGroup.index}"/>"
					src="image/remove.gif" border="0"
					style="cursor: pointer; margin-left: 25px;"
					title="<s:text name="label.managePolicy.deleteDealerGroup" />" /> <script
					type="text/javascript">
                        dojo.addOnLoad(function() {
                            wireDealerGroupRemoval(<s:property value="%{#selectedDealerGroup.index}"/>);
                        });
                    </script></td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div style="display: none;">
<div dojoType="twms.widget.Dialog" id="dlgAddDealer" bgColor="white"
	bgOpacity="0.5" toggle="fade" toggleDuration="250"
	title="<s:text name="label.managePolicy.addDealerDealerGroup" />"
	closeNode="btnCancelDealerAdd" style="width: 40%">
<div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
	style="background: #F3FBFE; height: 120px; border: 1px solid #EFEBF7">
<div dojoType="dijit.layout.ContentPane">
<table align="center" style="margin-top: 10px;">
	<tr>

		<td id="dealerNameText" class="labelStyle" width="20%" nowrap="nowrap">
		<s:text name="label.common.dealerName" />:</td>
		<td id="dealerGroupText" width="20%" nowrap="nowrap"
			class="labelStyle"><s:text name="label.common.dealerGroupLabel" />:</td>


		<td><sd:autocompleter id='dealerAutocompleter' showDownArrow='false' href='list_dealers.action' /></td>

		<td><sd:autocompleter href='list_DealerGroupsInStandard.action' id='dealerGroupAutocompleterStd' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' /> 
		<sd:autocompleter href='list_DealerGroupsInExtended.action' id='dealerGroupAutocompleterExt' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' /></td>
	</tr>
	<tr>
		<td width="20%">
		<div id="toggle" style="cursor: pointer;">
		<div id="toggleToDealerGroup" class="clickable"
			onclick="showDealerGroup()"><s:text name="label.common.dealerGroupLabel" /></div>
		<div id="toggleToDealerName" class="clickable"
			onclick="showDealerName()"><s:text name="label.common.dealerName" /></div>
		</div>
		</td>
	</tr>
	</tr>
</table>

<div align="center" style="margin-top: 20px;">
<button class="buttonGeneric" id="btnAddDealer"><s:text
	name="button.label.add" /></button>
<button class="buttonGeneric" id="btnCancelDealerAdd"><s:text
	name="button.common.cancel" /></button>
</div>
</div>
</div>
</div>
</div>