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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<!-- This for now just displays the action message. Need to include the parent table refresh or
	 row hide part.  -->
<html>
  <head>
      <title><s:text name="title.common.responsePage"/></title>
      <s:head theme="twms"/>
  </head>
  <u:body>
    <u:actionResults wipeMessages="false"/>
    <s:if test ="toPrint && !task.claim.state.state.equalsIgnoreCase('Draft')">
      <div align="center">
		<a id="claimPrint" href="#"><s:text name="link.print.claim"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				dojo.connect(dojo.byId("claimPrint"), "onclick", function(event){
					var claim = '<s:property value="task.claim"/>';
					var thisTabLabel = getMyTabLabel();
                    parent.publishEvent("/tab/open", {
					                    label: "Print Claim",
					                    url: "printClaim.action?claim="+claim, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                });
			});	
		</script>
	 </div>
	</s:if>
      <p />
      <center><jsp:include page="closeTab.jsp" /></center>
      <script type="text/javascript">
          dojo.addOnLoad(function() {
              var summaryTableId = getFrameAttribute("TST_ID");
              if (summaryTableId) {
                  manageTableRefresh(summaryTableId, true);
              }
          });
      </script>
     <authz:ifProcessor>
	     <div align="center">  
		      <s:if test="!task.claim.commercialPolicy && !'Campaign'.equalsIgnoreCase(task.claim.type.type) 
		      && (task != null && task.takenTransition != null && task.takenTransition.equals('Accept')) && (!task.claim.reopened || task.claim.reopenRecoveryClaim)">   
		        <span>
				    <button id="specifySupplierContract" class="buttonGeneric">
				       <s:text name="button.supplierRecovery.specifySupplierContract"/>
				    </button>
		       </span>      
		       <script type="text/javascript">
				 dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("specifySupplierContract"), "onclick", function(event){
							var claim = '<s:property value="task.claim"/>';
							var thisTabLabel = getMyTabLabel();
		                    parent.publishEvent("/tab/open", {
							                    label: "Specify Contract",
							                    url: "specify_supplier_contract.action?claim="+claim, 
							                    decendentOf: thisTabLabel,
							                    forceNewTab: true
		                                       });
		                                   });
		                              });           
				 </script>   
			  </s:if>
		 </div>	
	 </authz:ifProcessor>
  </u:body>
</html>