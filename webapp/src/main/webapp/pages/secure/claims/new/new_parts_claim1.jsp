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
<%@page pageEncoding="UTF-8" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
<head>
    <title><s:text name="title.newClaim.partsClaim"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>


    <script type="text/javascript" src="scripts/ClaimForm.js"></script>
    <script type="text/javascript" src="scripts/PartsClaimForm.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");                
        dojo.require("twms.widget.Select");
        dojo.require("dojox.layout.ContentPane"); 
        var _lastSeenDealership;
        function getDealerBrands(value, validatableStateId){
    
    
    if(!dojo.string.isBlank("abc") && _lastSeenDealership != value) {
    
    	_lastSeenDealership = value;
    
        twms.ajax.fireJsonRequest("list_dealer_brands.action",{
            forDealer:value
        },function(data) {
        
                    var brandsAvailable = data.items.length > 0;
					if(brandsAvailable){
					    var topicName = "dealer_brand"; 
                        dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
						}]);  
						if(validatableStateId)
						{
                    dojo.html.setDisplay(validatableStateId.domNode, brandsAvailable);
                    }
                    delete data;
			}
      }		);
}}       
    </script>
	<style>
	.official .dijitTextBox,
	.official .dijitComboBox,
	.official .dijitSpinner,
	.official .dijitInlineEditor input,
	.official .dijitTextArea {
		margin:0 2px 0 0;
	}
	input{margin-left:2px;}
	</style>
</head>

<u:body>
<u:actionResults/>
<div dojoType="dijit.layout.ContentPane" layoutAlign="client"  style="overflow:hidden">

<h3><s:text name="label.viewClaim.page1of2"/></h3>

<s:form method="post" theme="twms" validate="true" id="form"
        name="savePartsClaimDraft" action="savePartsClaimDraft.action">
        <s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" value = "%{currentBusinessUnit.name}"/>
		</s:if>
		<s:else>
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>
		</s:else>
		
		<s:hidden name="dealerNumberSelected" id="isDealerNumber"/>
		
  <div style="border:1px solid #EFEBF7;background:#F3FBFE;margin:5px;width:100%">
    <table class="form" cellpadding="0" cellspacing="0" style="border:none;">
    <tr><td colspan="4" style="height:10px; padding:0; margin:0;"></td></tr>
    	<tr>
   			<td width="26%">
               <label for="BU" class="labelStyle"> <s:text name="label.common.businessUnit"/>:</label>
            </td>
            <td width="35%">
               <s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
					<s:property value="currentBusinessUnit.name"/>
				</s:if>
				<s:else>
					<s:property value="selectedBusinessUnit"/>
				</s:else>
            </td>
            <td colspan="2"></td>
   		</tr>
        <tr>
        <td width="26%">     
            <s:if test="isLoggedInUserADealer()">
                <label for="dealer" class="labelStyle">
                	<s:text name="label.common.dealer"/>:
                </label>
            </s:if>
                <s:else>
                <div id="dealerNameLabel" class="labelStyle">
                    <s:text name="label.common.dealerName"/>:
                 </div>
                 <s:if test="claim == null || claim.id == null">
                  <div id="dealerNumberLabel" class="labelStyle">
                     <s:text name="label.common.dealerNumber"/>:
                 </div> 
                 <div id="toggle" style="cursor:pointer;">
                     <div id="toggleToDealerNumber" class="clickable">
                         <s:text name="toggle.common.toDealerNumber" />
                     </div>
                     <div id="toggleToDealerName" class="clickable">
                         <s:text name="toggle.common.toDealerName"/>
                     </div>
                 </div>
				</s:if>   
            </s:else>            
        </td>
        <td nowrap="nowrap" style="text-transform:uppercase;" width="35%">
            <s:if test="claim == null || claim.id == null">
            
                <s:if test="isLoggedInUserADealer()">
                <s:property value="getLoggedInUser().getCurrentlyActiveOrganization().name"/>
                        <s:hidden name="dealer" value="%{getLoggedInUser().getCurrentlyActiveOrganization()}"/>
                        <s:hidden name="claim.forDealer" value="%{getLoggedInUser().getCurrentlyActiveOrganization().id}" id="dealer"/>
		            <s:hidden name="claim.filedBy" value="%{getLoggedInUser().getCurrentlyActiveOrganization()}"/>
		            <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.publish("/dealer/modified", [{
                                params: {
                                    "dealerName" : '<s:property value="%{getLoggedInUser().getCurrentlyActiveOrganization().id}"/>',
                                    "forDealer" : '<s:property value="%{getLoggedInUser().getCurrentlyActiveOrganization().id}"/>'
                                }
                            }]);
                        });
                    </script>
                </s:if>
                <s:else>
                     <div id="dealerName">
                        <sd:autocompleter id='dealerNameAutoComplete' href='list_claim_dealers.action' name='claim.forDealer' value='%{claim.forDealer.name}' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}" loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' />
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
                            <script type="text/javascript">
					        dojo.addOnLoad(function() {
                                var dealerSelect = dijit.byId("dealerNameAutoComplete");
                                dealerSelect.sendDisplayedValueOnChange = false;
                                dojo.connect(dealerSelect, "onChange", function(value){
					            	dojo.byId("dealerIdForSearch").value=value;	
					            	dojo.byId("isDealerNumber").value = "false";				            						            	
					            });
							});
    					</script>    					
    					<script type="text/javascript">
                                dojo.addOnLoad(function() {
                                    dojo.connect(dijit.byId("dealerNameAutoComplete"), "onChange", function(newValue) {
                                    getDealerBrands(newValue, dijit.byId("brandtype"));
                                        dojo.publish("/dealer/modified", [{
                                            params: {
                                                "dealerName" : newValue,
                                                "forDealer" : newValue
                                            }
                                        }]);
                                    });
                                });
                            </script> 
    				</div>
    				<div id="dealerNumber">
    				    <sd:autocompleter id='dealerNumberAutoComplete' href='list_claim_dealer_numbers.action' name='claim.forDealer' value='%{claim.forDealer.serviceProviderNumber}' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}" loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' />
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
                            <script type="text/javascript">
					        dojo.addOnLoad(function() {
                                var dealerSelect = dijit.byId("dealerNumberAutoComplete");
                                dealerSelect.sendDisplayedValueOnChange = false;
                                dojo.connect(dealerSelect, "onChange", function(value){
					            	dojo.byId("dealerIdForSearch").value=value;
					            	dojo.byId("isDealerNumber").value = "true";					            	
					            });
							});
    					</script>
    					<script type="text/javascript">
    					
                                dojo.addOnLoad(function() {
                                    dojo.connect(dijit.byId("dealerNumberAutoComplete"), "onChange", function(newValue) {
                                    getDealerBrands(newValue, dijit.byId("brandtype"));
                                        dojo.publish("/dealer/modified", [{
                                            params: {
                                                "dealerName" : newValue,
                                                "forDealer" : newValue
                                            }
                                        }]);
                                    });
                                });
                                
                                 
                            </script>  
    				</div> 
    				<script type="text/javascript">
					    dojo.addOnLoad(function() {
					        <s:if test="dealerNumberSelected">
					          showDealerNumber();
					      </s:if>
					      <s:else>
					          showDealerName();
					      </s:else>
					
					        dojo.connect(dojo.byId("toggleToDealerName"), "onclick", function() {
					            dijit.byId('dealerNumberAutoComplete').setValue("");
					            showDealerName();
					        });
					        dojo.connect(dojo.byId("toggleToDealerNumber"), "onclick", function() {
					            dijit.byId('dealerNameAutoComplete').setValue("");
					            showDealerNumber();
					        });					         
					    });
					</script>				
                </s:else>
            </s:if>
            <s:else>
                <s:hidden name="claim.forDealer" value="%{claim.forDealer.id}" id="dealer"/>
                <s:hidden name="claim.filedBy"/>
                <s:property value="claim.forDealer.name"/>
            </s:else>
        </td>
        <td width="15%"  style="width: 210px;">
            <label for="type" class="labelStyle"><s:text name="label.common.claimType"/>:</label>
        </td>
        <td>
            <s:select id="type" name="claimType" list="claimTypes" cssStyle="width:145px;"
            	listKey="type" listValue="%{getText(displayType)}" disabled="%{claim.id != null}" />
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    var form = dojo.byId("form");
                    dijit.byId("type").onChange = function(value) {
                        if(value == "Machine" || value=="Campaign" || value == "Attachment") {
                            form.action = "chooseClaimTypeAndDealer.action";
                            form.submit();
                        }
                    };
                });
            </script>
        </td>
        </tr>
        <tr><td colspan="4" style="height:10px; padding:0; margin:0;"></td></tr>
    </table>

    <s:if test="displayBrandDropDown()">
	    <jsp:include flush="true" page="../forms/common/write/parts_header.jsp" />
	</s:if>
	<s:else>
	    <jsp:include flush="true" page="../forms/common/write/parts_header1.jsp" />
	</s:else>
</div>
<table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons">
    <tbody>
        <tr>
            <td align="center">
                <input type="submit" value="<s:text name="button.common.continue" />" onclick="this.disabled=true; this.form.submit();"/>
            </td>
        </tr>
    </tbody>
</table>
</s:form>
</div>
<jsp:include flush="true" page="../forms/common/write/multiCarSearch.jsp"/>
</u:body>
</html>