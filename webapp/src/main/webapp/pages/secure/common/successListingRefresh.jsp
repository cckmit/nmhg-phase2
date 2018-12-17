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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<!-- This for now just displays the action message. Need to include the parent table refresh or 
	 row hide part.  -->
<html>
<head>
<title><s:text name="title.common.responsePage" /></title>
<s:head theme="twms" />
</head>
<u:body>
	<u:actionResults wipeMessages="false" />
	<p />
	<center><jsp:include page="closeTab.jsp" /></center>
	<br />
	<s:if test="forDR||forETR">
		<s:if test="reductionInCoverage">
			<div align="center"><br />
			<s:a id="requestForExtension" href="#">Request For Extension Inbox</s:a>
			<script type="text/javascript">
					dojo.addOnLoad(function() {
						dojo.connect(dojo.byId("requestForExtension"), "onclick", function(event){															
							var url = "listRequestsForCoverageExtensionDealer.action?view=DealerView" ;																
						    var thisTabLabel = getMyTabLabel();
					                 parent.publishEvent("/tab/open", {
							                    label: "Request For Extension",
							                    url: url, 
							                    decendentOf: thisTabLabel,
							                    forceNewTab: true
					                                    });
					             });
					});	
                 </script></div>
		</s:if>
	</s:if> <script type="text/javascript">
		dojo.addOnLoad(function() {
		    var summaryTableId = getFrameAttribute("TST_ID");             
		    if (summaryTableId) {              		
		        manageTableRefresh(summaryTableId, "<s:property value="id"/>");
		    }
		}); 
	</script>
	 <s:if test="pdiGeneration">
    								<script type="text/javascript">
    			dojo.addOnLoad(function() {
    				 var inventoryItem = '<s:property value="inventoryItemMappings[0].inventoryItem"/>';
    						var thisTabLabel = getMyTabLabel();
                        	parent.publishEvent("/tab/open", {
    					                    label: "Print PDI",
    					                    url: "printTransfer.action?forETR=false&inventoryItems[0]="+inventoryItem,
    					                    decendentOf: thisTabLabel,
    					                    forceNewTab: true
                                           });
                    		});
    			</script>
    							</s:if>
</u:body>
</html>