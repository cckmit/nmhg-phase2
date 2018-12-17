<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<s:head theme="twms"/>
     <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
</head>
<style>
.grid1 {
font-family:Arial, Helvetica, sans-serif;
font-size:9pt;
width:60%;
margin-top:10px;
overflow:auto;
}

</style>
 
<u:body>
<u:actionResults/>
<s:form action="saveAdditionalLabourEligibility" id="baseForm">
<div class="policy_section_div"> 
<div id="manage_additional_header" class="section_header"><s:text name="title.additionalLabourEligibility"/></div>  
<div class="grid1"> 
<u:repeatTable id="additional_labour_table" cssClass="borderForTable">
    <thead>
           <tr class="row_head">
           <th width="25%"> <s:text name="label.manageALE.specifyDealers"/> </th>
           <th width="5%" align="center">
           			<div align="center"><u:repeatAdd id="outside_parts_adder">
	                   <div class="repeat_add"></div>
	               	</u:repeatAdd>
	               	</div>
	       </th>
        </tr>
    </thead>
    
    <u:repeatTemplate id="outside_parts_replaced_body"
        value="serviceProviders">
        <tr index="#index">
         <script type="text/javascript">
        dojo.addOnLoad(function() {
            dojo.html.hide(dijit.byId("dealerNumberAutoComplete_#index").domNode);
            dijit.byId("dealerNumberAutoComplete_#index").setDisabled(true);
            dojo.html.hide(dojo.byId("toggleToDealerName_#index"));
            dojo.html.hide(dojo.byId("dealerNumber_#index"));
           	dojo.byId("dealerNameAutoComplete_#index").value = "<s:property value="serviceProviders[#index].name"/>";
            dojo.byId("dealerNumberAutoComplete_#index").value = "<s:property value="serviceProviders[#index].serviceProviderNumber"/>";
        });
        </script>
  
        	<td width="25%">
                        <table cellpadding="0" cellspacing="0" border="0">
                          <tr>
                               <td align="left" style="border:0;" width="20%">
	                             <div id="toggle">
		                            <div id="dealerName_#index" style="width:90px;">
		                                <s:text name="label.common.dealerName"/>
		                            </div>
		                            <div id="dealerNumber_#index" style="width:90px;">
		                                <s:text name="label.common.dealerNumber"/>
		                            </div>
		                         </div> 
	                          <td align="left" style="border:0;" width="20%">
	                           <sd:autocompleter id='dealerNameAutoComplete_#index' href='list_warranty_reg_dealer_name_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='serviceProviders[#index]' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' delay='500' cssStyle='width:200px;' />
	                           <div align="left">
	                           <sd:autocompleter id='dealerNumberAutoComplete_#index' href='list_warranty_reg_dealer_number_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='serviceProviders[#index]' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' delay='500' cssStyle='width:200px;' />
	                           </div>
	                           <img style="display: none;" id="indicator" class="indicator"
	                             src="image/indicator.gif" alt="Loading..."/>
                              </td>
                              <td align="center" style="border:0;" width="30%">
                                <div id="toggle" style="cursor:pointer; text-align:center;">
                                  <div id="toggleToDealerNumber_#index" class="clickable" onclick="showDealerNumber(#index)">
                                      <s:text name="label.manageALE.specifyDealerNumber"/>
                                  </div>
                                  <div id="toggleToDealerName_#index" class="clickable" onclick="showDealerName(#index)">
                                      <s:text name="label.manageALE.specifyDealerName"/>
                                  </div>
                                </div>
                                <s:hidden name="serviceProviders[#index]" /> 
                             </td>
                           </tr>
                         </table>
             </td>
            <td width="5%" align="center">            
			                <u:repeatDelete id="outside_parts_deleter_#index">
			                    <div class="repeat_del"></div>
			                </u:repeatDelete>
            </td>
        </tr>
    </u:repeatTemplate>
    
</u:repeatTable>
			<div class="submit_space">
                 <div id="submit" align="center">
				       <input id="submit_btn" class="buttonGeneric" type="submit"
							value="<s:text name='button.common.save'/>" />
					   <input id="cancel_btn" class="buttonGeneric" type="button" 
					        value="<s:text name='button.common.cancel'/>"
							onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />        
	            </div>    
                </div>
    
 </div>
 </div>
</s:form>
</u:body>
<script type="text/javascript">



        function showDealerNumber(/*Number*/index) {
            dojo.html.show(dijit.byId("dealerNumberAutoComplete_"+index).domNode);
            dijit.byId("dealerNumberAutoComplete_"+index).setDisabled(false);
            dojo.html.show(dojo.byId("toggleToDealerName_"+index));
            dojo.html.show(dojo.byId("dealerNumber_"+index));
            dojo.html.hide(dijit.byId("dealerNameAutoComplete_"+index).domNode);
            dijit.byId("dealerNameAutoComplete_"+index).setDisabled(true);
            dojo.html.hide(dojo.byId("toggleToDealerNumber_"+index));
            dojo.html.hide(dojo.byId("dealerName_"+index));
        }

        function showDealerName(/*Number*/index) {
            dojo.html.hide(dijit.byId("dealerNumberAutoComplete_"+index).domNode);
            dijit.byId("dealerNumberAutoComplete_"+index).setDisabled(true);
            dojo.html.hide(dojo.byId("toggleToDealerName_"+index));
            dojo.html.hide(dojo.byId("dealerNumber_"+index));
            dojo.html.show(dijit.byId("dealerNameAutoComplete_"+index).domNode);
            dijit.byId("dealerNameAutoComplete_"+index).setDisabled(false);
            dojo.html.show(dojo.byId("toggleToDealerNumber_"+index));
            dojo.html.show(dojo.byId("dealerName_"+index));
        }
</script>
<authz:ifPermitted resource="warrantyAdminManageAdditionalLaborEligibilityReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>