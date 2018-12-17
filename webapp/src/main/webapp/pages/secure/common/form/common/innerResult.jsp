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

<%@page contentType="application/x-json"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script>
var selectedItemsId= [];
var rowCount =<s:property value = "matchingSyncTrackerList.size" />;
var next ='<s:property value="nextCounter"/>';
var previous ='<s:property value="previousCounter"/>';
var nextBtn='<s:property value="nextButton"/>';
var previousBtn='<s:property value="previousCounter"/>';
function functionReLoad(){	
getMatchingSyncTrackerRecords(0, dojo.byId("sorting").value, dojo.byId("ordering").value);  
}

dojo.addOnLoad(function() {	
dojo.connect(dojo.byId("masterCheckbox"), "onclick", function(event) {
		if(event.target.checked){
	    	for (var i = 0; i < rowCount; i++) {
                var currentElement = dojo.byId(String(i));
                currentElement.checked=true;
                var indexOf =dojo.indexOf(selectedItemsId, currentElement.value);		
				if (indexOf == -1) {
					selectedItemsId.push(currentElement.value);	
				}				
			}
			validateReprocess();
		}else{
			for (var j = 0; j < rowCount; j++) {
				var currentElement = dojo.byId(String(j));
				currentElement.checked=false;
			}
			selectedItemsId= [];
			validateReprocess();		
		}
   	}); 	
   	for (var i=0; i<rowCount; i++) {		
        var checkBox = dojo.byId(String(i));        
        dojo.connect(checkBox, "onclick", function(event) {
        var targetElement = event.target;
            if(targetElement.checked) {
            indexOfItem=dojo.indexOf(selectedItemsId,targetElement.value);
				if (indexOfItem == -1) {
					selectedItemsId.push(targetElement.value);
				}
            } else {
            dojo.byId("masterCheckbox").checked=false;
            indexOfItem =dojo.indexOf(selectedItemsId,targetElement.value);
				if (indexOfItem >= 0) {
					selectedItemsId.splice(indexOfItem, 1);
				}
				}
				validateReprocess();
        });
    } 	
   	});
   	
   	
    dojo.addOnLoad(function(){
    dojo.connect(dojo.byId("goButton"),"onclick",function(){
    var pageNumber=dojo.byId("enteredPageNo").value;
 	var totalPages='<s:property value="totalpages"/>';
    if(pageNumber!='' && parseInt(pageNumber)>=1 && parseInt(pageNumber)<=parseInt(totalPages)){
 	getRecordsForPage(parseInt(dojo.byId("enteredPageNo").value)-1,dojo.byId("sorting").value, dojo.byId("ordering").value);
 	}
 	else{
 	if(pageNumber==''){
 	alert("Enter Page Number");	
 	}
 	else{
 	alert("Enter Valid Page Number");	
 	} 
 	}	
 	});
 });
 
 
   dojo.addOnLoad(function(){	
	dojo.connect(dojo.byId("nextButton"), "onclick", function() {
			getRecordsForNextButton(dojo.byId("sorting").value,
			 dojo.byId("ordering").value);
   		});
   	dojo.connect(dojo.byId("previousButton"), "onclick", function() {
			getRecordsForPreviousButton(dojo.byId("sorting").value,
			 dojo.byId("ordering").value);
   		});
   		
   	document.onkeypress = stopRKey;	
   		
})


function stopRKey(evt) {
  var evt = (evt) ? evt : ((event) ? event : null);
  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
  if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
}
   	
   	function validateReprocess(){
   	 var enableSubmission = true;
    if(selectedItemsId.length == 0) {
        enableSubmission = false;
    }    	
    if(enableSubmission) {
       dojo.byId("btnSubmit").disabled=false;        
    } else {
       dojo.byId("btnSubmit").disabled=true;
    }    
   	}
   	
   	function finalStatus() {
			 var params={
			 syncType:"<s:property value="syncType"/>"
			 };
       params.records=selectedItemsId;
       var syncTrackerSearchResultDiv = dijit.byId("syncTrackerSearchResultTag");
       syncTrackerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
	   twms.ajax.fireHtmlRequest("get_remoteInteractionLogsReprocess.action",params,function(data) {
			syncTrackerSearchResultDiv.destroyDescendants();
			syncTrackerSearchResultDiv.setContent(data);	
			delete data, syncTrackerSearchResultDiv;
		});
	   delete params;
        }
</script>

<% response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<div class="policy_section_div" style="width:100%;height:93%">
<div id="Result Page" class="section_header"><s:text
		name="Search Result" /></div>
<table border="0" cellpadding="0" cellspacing="0" class="grid">
<tr>
<td align="left" class="labelStyle" width="35%">
<s:text name="Sort Results By" /><s:select label="sorting"
name="sorting" id="sorting"
headerKey="id" headerValue="Id"
list="#{'startTime':'StartTime' ,'uniqueIdValue':'Transaction Id','updateDate':'UpdateDate'}"
value="sortBy"
required="true"
/>
</td>
<td class="labelStyle" width="30%">
<s:select name="ordering" id="ordering" headerKey="Ascending" headerValue="Ascending"
list="#{'Descending':'Descending'}"
value="order"
required="true"
/><s:text name="Order" />
</td>
<td><input type="button" name="Sorting" value="Sort Search" id="btnSubmit2" onclick='javascript:functionReLoad()'
class="buttonGeneric"/></td>
</tr>

</table>
<br/>
<table class="grid borderForTable"  align="center" cellpadding="0" cellspacing="0" style="width:98%">
	<thead>
		<tr class="row_head">
		<s:if test="matchingSyncTrackerList.size != 0">
		<s:if test="isReprocessingRequired()">
		    <th  style="padding:0;margin:0"> <input style="margin-left: 4px;" type="checkbox" id="masterCheckbox"/> </th> 
		</s:if>
		</s:if>
		    <th style="display:none" align="center"><input style="margin-left: 4px;" type="checkbox" id="masterCheckbox"/></th> 
		    
		    <th><s:text name="label.manageRemoteInteractions.syncType"/></th>
		    <th><s:text name="label.manageRemoteInteractions.transactionId"/></th>
		    <th><s:text name="label.manageRemoteInteractions.status"/></th>
		    <th><s:text name="label.manageRemoteInteractions.ErrorMsg"/></th>
	   	    <th><s:text name="label.manageRemoteInteractions.updateDate"/></th>
	   	    <th><s:text name="label.manageRemoteInteractions.requestMessage"/></th>
		</tr>
	</thead>
	<tbody>	
    	<s:if test="matchingSyncTrackerList.empty">    	
    	<tr >
    		<td colspan="7" align ="center">
    			<s:text name="label.manageRemoteInteractions.noRecords"/>
    		</td>
    	</tr>     
    	</s:if>
    	<s:else>
    	<s:if test="isReprocessingRequired()">
	<s:iterator value="matchingSyncTrackerList" status="matchingSyncTrackerList">		
		<tr>		
		    <td   nowrap="nowrap" valign="middle">
					<s:checkbox name="testingbox"
						fieldValue="%{id}" id="%{#matchingSyncTrackerList.index}" />
		    </td>
		    
		    <td ><s:property value="syncType"/></td>
		    <td ><s:property value="uniqueIdValue"/></td>
		    <td ><s:property value="status.status"/></td>
		    <td ><s:property value="formatErrorMsg(errorMessage)"/></td>		    
		    <td ><s:property value="updateDate"/></td>
		     <td>
		                <s:url action="download_request_xml" id="url">
		                   <s:param name="id" value="%{id}"/>
		                 </s:url>
		                 <a href="<s:property value="#url" escape="false"/>"> <s:text name="label.manageRemoteInteractions.download"/> </a>
          </td>
		</tr>	
		</s:iterator>
		</s:if>
		<s:else>
		<s:iterator value="matchingSyncTrackerList" status="matchingSyncTrackerList" id="SyncTrackerList">		
		<tr>
		<td align="center"  nowrap="nowrap" valign="middle" style="display:none">
					<s:checkbox name="testingbox"
						fieldValue="%{id}" id="%{#matchingSyncTrackerList.index}" />
		    </td>		
			
			<td ><s:property value="syncType"/></td>
			<td ><s:property value="uniqueIdValue"/></td>
			<td ><s:property value="status.status"/></td>
			<td ><s:property value="formatErrorMsg(errorMessage)"/></td>
			<td ><s:property value="updateDate"/></td>
			<td>
					                <s:url action="download_request_xml" id="url">
					                   <s:param name="id" value="%{id}"/>
					                 </s:url>
					                 <a href="<s:property value="#url" escape="false"/>"> <s:text name="label.manageRemoteInteractions.download"/> </a>
          </td>
		</tr>	
		</s:iterator>
		</s:else>		
		</s:else>
	</tbody>
</table>

<br/>
<s:if test="matchingSyncTrackerList.size != 0">
<s:if test="isReprocessingRequired()">
<div class="spacingAtTop" align="center">
<input type="button" name="Submit2" value="ReProcess" id="btnSubmit" disabled="true" onclick="finalStatus()"
class="buttonGeneric"/>
</div>
</s:if>
</s:if>

<table width="98%">
<tr>
<td class="labelStyle" width="10%" nowrap="nowrap"><s:text name="label.manageRemoteInteractions.noOfPages"/>:<s:property value="totalpages"/></td>
<td>
<input type="text" name="enteredPageNo" id="enteredPageNo" />
<input type="button" value="<s:text name="label.common.go"/>" id="goButton"  class="buttons"/>
</td>
</tr>
</table>
<table width="98%">
<tr>
<td class="buttons">
<input type="button" value="<s:text name="label.common.previous"/>" id="previousButton" class="buttons"/>
</td>
<td>

		<s:iterator value="pageNoList" status="pageCounter">
			&nbsp;
			<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
				<span id="page_<s:property value="%{intValue()-1}"/>">	
			</s:if>	
			<s:else>
				<span id="page_<s:property value="%{intValue()-1}"/>" style="cursor:pointer;text-decoration:underline">
			</s:else>
			<s:property value="%{intValue()}" />
			<script type="text/javascript">
				dojo.addOnLoad(function(){	
		<s:if test="!previousButton">
			dojo.byId("previousButton").disabled=true;
		</s:if>
		<s:if test="!nextButton"> 
			dojo.byId("nextButton").disabled=true;
		</s:if>
		var counter = '<s:property value="%{intValue()}"/>'; 
		var index=parseInt(counter)-parseInt(1);
		var pageNo='<s:property value="pageNo"/>';
		if(index!=pageNo){
			dojo.connect(dojo.byId("page_"+index),"onclick",function(){
				getRecordsForPage(index,dojo.byId("sorting").value, dojo.byId("ordering").value);  
			});
		}	 
	});
			</script>
		   </span>	
	   </s:iterator>
	
</td>
<td>
<div><center class="buttons">
<input type="button" value="<s:text name="label.common.next"/>" id="nextButton" class="buttons"/></center>
</div>
</td>
</tr></table>
</div>

