<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><s:text name="title.fleetmanagement.retailMachineTransfer" /></title>
	<s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>

    <script type="text/javascript" >
         dojo.require("twms.widget.Dialog");            
         dojo.require("twms.widget.MultipleInventoryPicker");
         dojo.require("dijit.layout.LayoutContainer");
         dojo.require("dojox.layout.ContentPane");
    </script>
</head>
 <script type="text/javascript" >
         var submitButton;
         dojo.addOnLoad(function(){
         	dojo.html.hide(dojo.byId("searchAgainLink"));         	
         	dojo.html.hide(dojo.byId("searchDealerAgainLink"));         	         	
         });
         
         dojo.addOnLoad(function() {	
         
			dojo.connect(dijit.byId("selectedInventoriesTag"),"onLoad","bindDealerSelectionForm");
			dojo.connect(dijit.byId("searchedDealersDiv"),"onLoad","bindSelectedDealerForm");
		});
	
        
        function bindDealerSelectionForm()
        {
        	
        	new dojo.io.FormBind({         		
        		formNode: dojo.byId("searchDealerForm"),
        		load: function(data, e) {
						dijit.byId("searchedDealersDiv").setContent(data);
        				},
   				error : function(error) {
   				dojo.body().style.cursor = "auto";
   				}
    			});				
				dojo.connect(dojo.byId("searchButton"),"onclick", function(){
					dijit.byId("searchedDealersDiv").domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
					dojo.html.show(dojo.byId("searchedDealersDiv"));					         			        							
				});
				dojo.connect(dojo.byId("searchDealerAgainLink"),"onclick", function(){
   				  	dojo.html.show(dojo.byId("selectedInventoriesTag"));   				      		
         			dojo.html.show(dojo.byId("searchCompanyTag"));         			
         			dojo.html.hide(dojo.byId("selectedDealerTag"));
         			dojo.html.hide(dojo.byId("searchDealerAgainLink"));         		 	 
         			dojo.html.show(dojo.byId("searchedDealersDiv"));
         		});        
        } 
        
        function bindSelectedDealerForm()
        {
        	new dojo.io.FormBind({         		
        		formNode:dojo.byId("multipleDealersForm"),
        		load: function(data, e) {
						dijit.byId("selectedDealerTag").setContent(data);
        				},
        		error : function(error) {
        				dojo.body().style.cursor = "auto";
        				}
    			});
				 
				dojo.connect(dojo.byId("selectDealerButton"),"onclick", function(){
					dojo.html.show(dojo.byId("searchDealerAgainLink"));
					dojo.html.hide(dojo.byId("searchCompanyTag"));
					dojo.html.show(dojo.byId("selectedDealerTag"));
	         		dojo.html.hide(dojo.byId("searchedDealersDiv"));
	         		dijit.byId("selectedDealerTag").domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
         		});  
         		dojo.connect(dojo.byId("searchDealerAgainLink"),"onclick", function(){
                      dojo.html.hide(dojo.byId("selectedDealerTag"));
                      dojo.html.hide(dojo.byId("searchDealerAgainLink"));                     
                      dojo.html.show(dojo.byId("searchedDealersDiv"));
               });       	
        
        }
         
 </script>
<u:body >
<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y:auto; ">	
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
		<div style="margin:5px;background:#F3FBFE;border:1px solid #EFEBF7" >
			<div  class="section_heading">
				<s:text name="title.fleetmanagement.retailMachineTransfer"/>
			</div>
		
		<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
        <div id="selectInventoriesLink" align="left">
            <s:if test="(getLoggedInUser().businessUnits).size==1 ">
                <div dojoType="twms.widget.MultipleInventoryPicker" searchHandlerUrl="searchInventories_RMT.action"
                     selectionHandlerUrl="handle_inventory_selection_RMT.action" useInnerHTML="false"
                     searchActionType="HIDEDTD"></div>
            </s:if>
            <s:elseif test="(getLoggedInUser().businessUnits).size>1 && loggedInUserAnInternalUser">
                <div dojoType="twms.widget.MultipleInventoryPicker" searchHandlerUrl="searchInventories_RMT.action"
                     selectionHandlerUrl="handle_inventory_selection_RMT.action" useInnerHTML="false"
                     isMultiLineUser="TRUE" searchActionType="HIDEDTD"></div>
            </s:elseif>
            <s:elseif test="(!loggedInUserAnInternalUser && getLoggedInUser().businessUnits).size>1">
                <div dojoType="twms.widget.MultipleInventoryPicker" searchHandlerUrl="searchInventories_RMT.action"
                     selectionHandlerUrl="handle_inventory_selection_RMT.action" useInnerHTML="false"
                     searchInputUrl="setupInventorySearch.action?actionType=RMT"
                     searchActionType="HIDEDTD" isRestrictedBuListDisplayed="TRUE"></div>
            </s:elseif>
        </div>
        </authz:ifNotPermitted>

		<div id="searchAgainLink" >
		<label class="alinkclickable"><s:text name="label.retailMachineTransfer.searchAgain"></s:text></label>			
		</div>		
		
		<div dojoType="dojox.layout.ContentPane" 
			 id="selectedInventoriesPane" >
			 <s:if test="!inventoryItemsForRMT.isEmpty">		 						
		    	<jsp:include page="retail_machine_transfer_equipment_info.jsp" />
		     </s:if>				   	
		</div>										
				
		<div id="selectedInventoriesTag" dojoType="dojox.layout.ContentPane" layoutAlign="client"
		style="padding-bottom: 3px;" >
		</div>
		
		<div id="searchedDealersDiv" dojoType="dojox.layout.ContentPane" layoutAlign="client"
		style="padding-bottom: 3px;" >
		</div>		
		
		<div id="searchDealerAgainLink" >
		<label><s:text name="label.retailMachineTransfer.searchAgain"></s:text></label>			
		</div>		
			
		<div id="selectedDealerTag" dojoType="dojox.layout.ContentPane" layoutAlign="client"
		style="padding-bottom: 3px;" >				
		</div>
	</div>		
	</div>
</div>	   
</u:body>
</html>



