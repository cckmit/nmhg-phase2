<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>



<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">    
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>    

</head>
<u:body>
<script type="text/javascript"> 
		dojo.require("dijit.layout.ContentPane");
        dojo.require("dojox.layout.ContentPane");        
		var claimSize='<s:property value="claims.size"/>';
		var attrSize='<s:property value="attributeMapper.listOfAttributes.size"/>';
		dojo.addOnLoad(function(){			
			dojo.connect(dojo.byId("searchButton"),"onclick", function(){
			 var id=document.getElementById("domainPredicateId").value;
			 var url = "detail_view_search_expression.action";
			 url += "?id=" + id;
			 var contextName=document.getElementById("contextName").value;
			 url += "&context="+contextName;
			 var savedQueryId=document.getElementById("savedQueryId").value;
			 url += "&savedQueryId="+savedQueryId;
			 var selectedBusinessUnit=document.getElementById("selectedBusinessUnit").value;
			 url += "&selectedBusinessUnit="+selectedBusinessUnit;
			 var isMultiClaimMaintenance=document.getElementById("isMultiClaimMaintenance").value;
			 url += "&isMultiClaimMaintenance="+isMultiClaimMaintenance;		 
			 parent.publishEvent("/tab/reload", {
			 	label: "Multi Claim Maintenance",
				url: url,
				decendentOf:"Home",
			 	tab: getTabHavingId(getTabDetailsForIframe().tabId)});		
			});		
			dojo.connect(dojo.byId("masterCheckboxForAttributes"),"onclick",function(){
				if(dojo.byId("masterCheckboxForAttributes").checked){
					for(var i=0;i<attrSize;i++){				
					dojo.byId("listOfAttributes"+i).checked=true;
					}
				}else{
					for(var i=0;i<attrSize;i++){				
					dojo.byId("listOfAttributes"+i).checked=false;
					}
				}
			});			
							
			dojo.connect(dojo.byId('submit_button'), "onclick", function() {			
					dojo.query("input[id ^= 'listOfAttributes']").forEach(function(node){
						if(node.checked){							
						 dojo.byId("selectedClaims").value=selectedClaimsId;
						}	
					});									
			});
		});
		function getMatchingClaims(index) {
			var params = {
			      "isMultiClaimMaintenance" : document.getElementById("isMultiClaimMaintenance").value,
			      "context" : document.getElementById("contextName").value,
			      "domainPredicateId" : document.getElementById("domainPredicateId").value,
			      "selectedBusinessUnit" : document.getElementById("selectedBusinessUnit").value,
			      "savedQueryId" : document.getElementById("savedQueryId").value,
			      "selectedClaimsId":selectedClaimsId			      
			};
			if(index){
		       params.pageNo=index;
	        }
			var claimSearchResultDiv = dijit.byId("multiClaimAttributesContent");
		    claimSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
			twms.ajax.fireHtmlRequest("multiClaimAttributesClaims.action",params,function(data) {
					claimSearchResultDiv.destroyDescendants();
					claimSearchResultDiv.setContent(data);
					delete data, claimSearchResultDiv;
				});
		
			delete params;
		} 	    	   
    </script>

<div  style="height:100%;overflow:visible;overflow-Y:auto;width:100%;overflow-X:auto">
<u:actionResults/>

<s:if test="isMultiDealerClaimsSelected()">
<div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
	<h4 class="twmsActionResultActionHead"><s:text name="label.common.warnings" /></h4>
		<ol>
			<s:text name="title.message.invalidAttributeTechnician"></s:text>				
		</ol>
		<hr/>
</div>
</s:if>


<s:form method="post" theme="twms" id="multipleClaimMaintenance" name="multipleClaimMaintenanceForm"
            		action="selectedClaims.action">

<div class="policy_section_div">

<div class="querySearchButton alinkclickable" style="padding:15px;">
<span id="searchButton" class="alinkclickable"> <s:text name="button.viewClaim.showSearchQuery" /> </span>
</div>

<div class="section_header"  >
<s:text name="title.attributes.queryResults" />
</div>
<div id="multiClaimAttributesContent" dojoType="dojox.layout.ContentPane" executeScripts="true">
 <jsp:include page="multiClaimAttributesClaimResults.jsp" flush="true"/>
</div>
</div>
<s:if test="claims.size > 0">
<div class="policy_section_div">
<div class="section_header">
<s:text name="title.attributes.attributes"/>
</div>
<table  cellspacing="0" align="center" cellpadding="0" id="attribute_details_table" 
class="grid borderForTable" style="clear: both;">   
    
    <thead>    	
        <tr>        
            <th class="warColHeader" width="40%" style="padding: 0" align="center">
                <input type="checkbox" id="masterCheckboxForAttributes" style="padding: 0" />
            </th>        
            <th class="warColHeader" width="60%" class="non_editable"><s:text name="title.attributes.attributes"/></th>
        </tr>
    </thead>
    <tbody>        
    	<s:iterator value="attributeMapper.listOfAttributes" status="attributes">  
        <tr> 
        	<td align="center" valign="middle">            
                <s:checkbox id="listOfAttributes%{#attributes.index}" 
                        name="attributeMapper.listOfAttributes[%{#attributes.index}].selected"/>  
                        
            </td>       	
            <td>
            	<s:property value="name"/>
            </td>
        </tr>
        </s:iterator>
   </tbody>
</table>
</div>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="padding-bottom: 10px">
	<table class="buttons">
		<tr>
		<td>
		<center>
			 <s:submit value="%{getText('button.common.cancel')}" type="input" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                        });
                    </script>
			<s:submit id="submit_button"  value="%{getText('button.common.submit')}" />			
		</center>
		</td>
		</tr>
	</table>	
</div>
</s:if>
</s:form>
</div>
</u:body>

