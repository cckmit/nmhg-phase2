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

    var __rowTemplateNationalAccountName = "<tr  name='user_name_row' id='national_account_name_row_${index}'>" +
        "<td style='padding-left:5px; height:25px'>" +
        "<input type='hidden' name='selectedNationalAccounts[${index}]'" +
        " value='${id}' />${name}</td>"+
        "<td align='left' width='9%'><img id='removeNationalAccountNameIcon_${index}'" +
        " src='image/remove.gif' border='0'" +
        " style='cursor: pointer;margin-left: 25px;'" +
        " title='<s:text name="label.campaign.deleteNationalAccountName" />'/>" +
        "</td></tr>";

        var __rowTemplateNationalAccountNumber = "<tr name='user_number_row' id='national_account_number_row_${index}'>" +
        "<td style='padding-left:5px; height:25px'>" +
        "<input type='hidden' name='selectedNationalAccounts[${index}]'" +
        " value='${id}' />${serviceProviderNumber}</td>"+
        "<td align='left' width='9%'><img id='removeNationalAccountNumberIcon_${index}'" +
        " src='image/remove.gif' border='0'" +
        " style='cursor: pointer;margin-left: 25px;'" +
        " title='<s:text name="label.campaign.deleteNationalAccountNumber" />'/>" +
        "</td></tr>";
    var __nextAvailableIndexForNationalAccount = 0, __nationalAccountNameAutoCompleter, __tbodyNationalAccount;

    var __nationalAccountNumberAutoCompleter, __tbodyNationalAccount;

   

    <s:if test="!selectedNationalAccounts.empty">
    __nextAvailableIndexForNationalAccount = <s:property value="selectedNationalAccounts.size" />;
    </s:if>

        function wireNationalAccountNameRemoval(/*number*/ nationalAccountIndex,
        /*boolean*/ newlyAddedRow) {                   
            dojo.connect(dojo.byId("removeNationalAccountNameIcon_" + nationalAccountIndex),
            "onclick", function() {
                var rowNationalAccount = dojo.byId("national_account_name_row_" + nationalAccountIndex);
              if(!newlyAddedRow) {
                    requestDeletion(rowNationalAccount, "selectedNationalAccounts");
               }
            
                dojo.dom.destroyNode(rowNationalAccount);
            });
        }

        function wireNationalAccountNumberRemoval(/*number*/ nationalAccountIndex,
                /*boolean*/ newlyAddedRow) {
                    dojo.connect(dojo.byId("removeNationalAccountNumberIcon_" + nationalAccountIndex),
                    "onclick", function() {
                        var rowNationalAccount = dojo.byId("national_account_number_row_" + nationalAccountIndex);
                      if(!newlyAddedRow) {
                            requestDeletion(rowNationalAccount, "selectedNationalAccounts");
                      }
                    
                        dojo.dom.destroyNode(rowNationalAccount);
                    });
                }

        function addNationalAccountToCampaign() {
        	 var selectedBox = dijit.byId("nationalAccountNameAutoCompleter").disabled;
             var selectedBoxGroupStd = dijit.byId("nationalAccountNumberAutoCompleter").disabled;
             if(selectedBox==false)
             {            
            var rowMarkupNationalAccount = dojo.string.substitute(__rowTemplateNationalAccountName, {
               index : __nextAvailableIndexForNationalAccount,
               name : __nationalAccountNameAutoCompleter.getDisplayedValue(),
               id : __nationalAccountNameAutoCompleter.getValue()
            });
            var trNodeAccount = dojo.html.createNodesFromText(rowMarkupNationalAccount);
            __tbodyNationalAccount .appendChild(trNodeAccount);

            wireNationalAccountNameRemoval(__nextAvailableIndexForNationalAccount, true);

            __nextAvailableIndexForNationalAccount++;
            
             }
             else if(selectedBoxGroupStd==false)
             {            	
            	 var rowMarkupNationalAccount = dojo.string.substitute(__rowTemplateNationalAccountNumber, {
                     index : __nextAvailableIndexForNationalAccount,
                     serviceProviderNumber : __nationalAccountNumberAutoCompleter.getDisplayedValue().toUpperCase(),
                     id : __nationalAccountNumberAutoCompleter.getValue().toUpperCase()
                  });
                  var trNodeAccount = dojo.html.createNodesFromText(rowMarkupNationalAccount);
                  __tbodyNationalAccount .appendChild(trNodeAccount);

                  wireNationalAccountNumberRemoval(__nextAvailableIndexForNationalAccount, true);

                  __nextAvailableIndexForNationalAccount++;
             }
            
            dijit.byId("addAccountName").hide();
        }

        dojo.addOnLoad(function() {
        	  dojo.html.hide(dojo.byId("toggleToAccountName"));
              dojo.html.hide(dojo.byId("accountNumberText"));
              if(dijit.byId("nationalAccountNumberAutoCompleter")){
              dojo.html.hide(dijit.byId("nationalAccountNumberAutoCompleter").domNode);
              dijit.byId("nationalAccountNumberAutoCompleter").setDisabled(true);
              dijit.byId("nationalAccountNameAutoCompleter").setDisabled(true);
              dojo.html.hide(dojo.byId("toggleToAccountNumber"));
            
             __tbodyNationalAccount  = dojo.query("table#tblSelectedAccounts tbody")[0];

            var validationTopicDealer = "/policyDefn/addDealer";
            __nationalAccountNameAutoCompleter = dijit.byId("nationalAccountNameAutoCompleter");
            __nationalAccountNumberAutoCompleter = dijit.byId("nationalAccountNumberAutoCompleter");
          
            __nationalAccountNameAutoCompleter.setValidationNotificationTopics(validationTopicDealer);
            __nationalAccountNumberAutoCompleter.setValidationNotificationTopics(validationTopicDealer);
           

            var btnAddAccount = dojo.byId("btnAddAccount");
            
            dojo.subscribe(validationTopicDealer, function(message) {
                btnAddAccount.disabled = !message.isValid;
            });

            dojo.connect(dojo.byId("addAccountIcon"), "onclick",
                function() {
            	dojo.html.hide(dojo.byId("toggleToAccountName"));
                dojo.html.hide(dojo.byId("accountNumberText"));
                dojo.html.hide(dijit.byId("nationalAccountNumberAutoCompleter").domNode);
                dijit.byId("nationalAccountNumberAutoCompleter").setDisabled(true);
                dojo.html.show(dojo.byId("accountNameText"));
                dojo.html.show(dojo.byId("toggleToAccountNumber"));
                dojo.html.show(dijit.byId("nationalAccountNameAutoCompleter").domNode);
                dijit.byId("nationalAccountNameAutoCompleter").setDisabled(false);
                    __nationalAccountNameAutoCompleter.setDisplayedValue("");
                    __nationalAccountNumberAutoCompleter.setDisplayedValue("");
                    btnAddAccount.disabled = true;
                    dijit.byId("addAccountName").show();
                }
            );

            dojo.connect(btnAddAccount, "onclick",
                addNationalAccountToCampaign
            );
              }
        });


        function showAccountNumber() {
        	
        	
        	                 dojo.html.show(dijit.byId("nationalAccountNumberAutoCompleter").domNode);
        	                 dijit.byId("nationalAccountNumberAutoCompleter").setDisabled(false);
        	                 dojo.html.show(dojo.byId("accountNumberText"));
        	                 dojo.html.show(dojo.byId("toggleToAccountName"));
        	                 dojo.html.hide(dijit.byId("nationalAccountNameAutoCompleter").domNode);
        	                 dijit.byId("nationalAccountNameAutoCompleter").setDisabled(true);
        	                 dojo.html.hide(dojo.byId("accountNameText"));
        	                 dojo.html.hide(dojo.byId("toggleToAccountNumber"));
        	        }

        	        function showAccountName() {
      
        	            dojo.html.hide(dijit.byId("nationalAccountNumberAutoCompleter").domNode);
        	            dijit.byId("nationalAccountNumberAutoCompleter").setDisabled(true);
        	            dojo.html.hide(dojo.byId("accountNumberText"));
        	            dojo.html.hide(dojo.byId("toggleToAccountName"));
        	            dojo.html.show(dijit.byId("nationalAccountNameAutoCompleter").domNode);
        	            dijit.byId("nationalAccountNameAutoCompleter").setDisabled(false);
        	            dojo.html.show(dojo.byId("accountNameText"));
        	            dojo.html.show(dojo.byId("toggleToAccountNumber"));
        	          
        	        }
        	        
</script>

<table id="tblSelectedAccounts" class="repeat borderForTable" width="97%">
	<thead>
		<tr class="admin_section_heading">
			<th width="88%" style="border-right:0;"><s:text name="Add National Account Name/Number" /></th>
			<th width="9%" align="center" style="border-left:0;"><img id="addAccountIcon"
				src="image/addRow_new.gif" border="0" style="cursor: pointer;"
				title="<s:text name="label.campaign.addNationalAccountName&Number" />" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="selectedNationalAccounts" status="selectedAccountName">
			<tr id="national_account_name_row_<s:property value="%{#selectedAccountName.index}"/>">
				<td style="padding-left: 5px; height: 25px"><s:hidden
					name="selectedNationalAccounts[%{#selectedAccountName.index}]" /><s:property
					value="name" /></td>

				<td align="left" width="9%"><img
					id="removeNationalAccountNameIcon_<s:property value="%{#selectedAccountName.index}"/>"
					src="image/remove.gif" border="0"
					style="cursor: pointer; margin-left: 25px;"
					title="<s:text name="Delete National Account Name" />" /> <script
					type="text/javascript">
                        dojo.addOnLoad(function() {
                            wireNationalAccountNameRemoval(<s:property value="%{#selectedAccountName.index}"/>);
                        });
                    </script></td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div style="display: none;">
<div dojoType="twms.widget.Dialog" id="addAccountName" bgColor="white"
	bgOpacity="0.5" toggle="fade" toggleDuration="250"
	title="<s:text name="label.campaign.addNationalAccountName" />"
	closeNode="btnCancelAccountAdd" style="width: 40%">
<div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
	style="background: #F3FBFE; height: 120px; border: 1px solid #EFEBF7">
<div dojoType="dijit.layout.ContentPane">
<table align="center" style="margin-top: 10px;">
	<tr>

		<td id="accountNameText" class="labelStyle" width="20%" nowrap="nowrap">
		<s:text name="Add National Account Name" />:</td>
		<td id="accountNumberText" width="20%" nowrap="nowrap"
			class="labelStyle"><s:text name="label.campaign.addNationalAccountNumber" />:</td>


		 <td><sd:autocompleter id='nationalAccountNameAutoCompleter' showDownArrow='false' href='list_nationalAccountNames.action' /></td>
		
         <td><sd:autocompleter href='list_nationalAccountNumbers.action' id='nationalAccountNumberAutoCompleter' name='accountNumber' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' /> </td>
	</tr>
	<tr>
		<td width="20%">
		<div id="toggle" style="cursor: pointer;">
		<div id="toggleToAccountNumber" class="clickable"
			onclick="showAccountNumber()"><s:text name="label.campaign.NationalAccountNumber" /></div>
		<div id="toggleToAccountName" class="clickable"
			onclick="showAccountName()"><s:text name="label.campaign.NationalAccountName" /></div>
		</div>
		</td>
	</tr>
	</tr>
</table>

<div align="center" style="margin-top: 20px;">
<button class="buttonGeneric" id="btnAddAccount"><s:text
	name="button.label.add" /></button>
<button class="buttonGeneric" id="btnCancelAccountAdd"><s:text
	name="button.common.cancel" /></button>
</div>
</div>
</div>
</div>
</div>