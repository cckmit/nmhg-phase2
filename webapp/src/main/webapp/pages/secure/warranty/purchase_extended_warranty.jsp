<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">    
    <s:head theme="twms"/>    
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="common.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %> 
       
    <script>
    
    	dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dojox.layout.ContentPane");
         
        function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
        dojo.addOnLoad(function() {
 		    bindExtendedWarrantyPurchaseForm(); 		    
 		    if(dojo.byId("purchaseConfCancel")) {
	  			dojo.connect(dojo.byId("purchaseConfCancel"),"onclick",function(){
					dijit.byId("purchase_confirmation").hide();
				});
  			}
			dojo.subscribe("/multipleInventorySearchResults/searchStatus", null, "bindExtendedWarrantyPurchaseForm");
			
    	});
      
      function bindExtendedWarrantyPurchaseForm() {        
      
           	
      	new dojo.io.FormBind({
        	formNode: dojo.byId("extendedWarrantyPurchase"),
        	load: function(data, e) {        			
        		dijit.byId("purchase_confirmation").show();
	        	var parentDiv = dojo.byId("purchase_confirmation_div");
	        	parentDiv.innerHTML = data;        	
        	}
      	});
      }
     
	 dojo.require("twms.widget.MultipleInventoryPicker");
    </script>
</head>
<u:body >
	<u:actionResults/>	
<div class="twmsActionResults" id="ewpWarningSection" style="display:none">
    <div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
        <h4 class="twmsActionResultActionHead"><s:text name="label.common.warnings"/></h4>     
        <ol>
                <li><s:text name="label.ewp.warning"/></li>
        </ol>
        <hr/>
    </div>
</div>
    <s:if test="selectedInvItemsPolicies[0].availablePolicies.size() != 0">
        <script type="text/javascript">
            dojo.html.show(dojo.byId("ewpWarningSection"));
        </script>
    </s:if>

        <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 80%; background: white;">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
               <div class="policy_section_div">
                <s:if test="(getLoggedInUser().businessUnits).size==1">
                 <div dojoType="twms.widget.MultipleInventoryPicker" searchHandlerUrl="searchInventories_EWP.action" useInnerHTML="false" selectionHandlerUrl="add_inventories_extendedwarrantypurchase.action" searchActionType="HIDEDTD" selectedItemsContentPane="multiInventoryResult" selectionHandlerParams="selectionParams">
				 </div>
                </s:if>
                <s:elseif test="(getLoggedInUser().businessUnits).size>1">
                    <div dojoType="twms.widget.MultipleInventoryPicker" searchHandlerUrl="searchInventories_EWP.action" useInnerHTML="false" selectionHandlerUrl="add_inventories_extendedwarrantypurchase.action" searchActionType="HIDEDTD" selectedItemsContentPane="multiInventoryResult" selectionHandlerParams="selectionParams" isMultiLineUser="TRUE">
                    </div>
                </s:elseif>
                <div dojoType="dojox.layout.ContentPane" id = "multiInventoryResult" >
					<jsp:include  page="purchase_extended_warrantyinclude.jsp" />
				</div>	
				</div>			
			</div>				
		</div>
		
		    <div dojoType="twms.widget.Dialog" id="purchase_confirmation" 
		            title="<s:text name="label.extdWarranty.confirmExtendedWarranty" />" style="height:90%;width: 90%;">
		                <div id="purchase_confirmation_div"></div>
		    </div>
		</div>
</u:body>
</html>



