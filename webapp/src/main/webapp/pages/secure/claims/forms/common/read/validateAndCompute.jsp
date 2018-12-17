
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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript">
        
        // function to disable  the button so that form doesn't get posted 2nd time
        dojo.addOnLoad(function(){
			dojo.query("button,input[type='submit']", dojo.body()).forEach(function(aButton) { 
			     if(aButton.id.indexOf("validations_hider")!=-1 || aButton.id.indexOf("submitButton")!=-1)  {                	
						dojo.connect(dojo.byId(aButton), "onclick", function() {							  						  	
						  aButton.disabled = true;							
						  if(aButton.id.indexOf("submitButton")!=-1){
						  	dojo.byId("validations_hider3").disabled=true;
						  }	
						if(aButton.id.indexOf("validations_hider")!=-1)	{					  
						  dojo.query("button,input[type='submit']", dojo.body()).forEach(function(button){
						    if(button.id.indexOf("submitButton")!=-1){
						    	button.disabled=true;
						    }
						   });	
						}						    
						});
					}
			});
	  });				
</script>
<div id="submittingIndicationDiv" style="width: 100%; height: 100%;">
	<center><img src="image/indicator.gif" class="indicator"/>Submitting ...</center>
</div>
<div dojoType="dojox.layout.ContentPane" id="validationsDiv">
	<u:actionResults/>
    <jsp:include flush="true" page="validationMessages.jsp"/>

    <s:if test="task.baseFormName == 'processor_review' || task.claim.appealed == true">
        <p style="margin: 3px; padding: 5px;">
        	<span style="font-weight: bold;"><s:property value="processorTakenTransition"/></span>
		</p>
    </s:if>

	    <div dojoType="dijit.layout.ContentPane" id="applicable_policy">
            <table width="95%" class="grid borderForTable" style="margin-left:0px;">
                <thead>
                    <tr class="row_head">
                        <th>
                            <s:text name="columnTitle.common.serialNo" />
                        </th>
                        <th ><s:text name="label.newClaim.applicablePolicy"/></th>
                        <authz:ifProcessor>
                            <s:if test="task.claim.forMultipleItems">
                                <th ><s:text name="label.common.approved"/></th>
                            </s:if>
                        </authz:ifProcessor>
                    </tr>
                </thead>
                <tbody>
                <s:iterator value="task.claim.claimedItems">
                    <tr>
                        <td class="text">
                        	<s:if test="task.partsClaim && claim.partItemReference.referredInventoryItem!=null">
                            <s:property value="claim.partItemReference.referredInventoryItem.serialNumber" />
                            </s:if>
                            <s:elseif test="itemReference.referredInventoryItem!=null">
                            <s:property value="itemReference.referredInventoryItem.serialNumber" />
                            </s:elseif>                            
                        </td>
                        <td class="text">
                        <s:if test="claim.state.state.equalsIgnoreCase('Draft')">
                        	<s:if test="applicablePolicy != null">
                        		<s:property value="applicablePolicy.code"/>
                        	</s:if>
                        	<s:else>
                        		<s:if test = "itemReference.isSerialized()">
                        			<s:if test = "itemReference.referredInventoryItem.type.type.equals('RETAIL')">
                        				<s:text name = "label.claim.retailPolicy" />
                        			</s:if>
                        			<s:else>
                        				<s:text name = "label.claim.stockPolicy" />
                        			</s:else>
                        		</s:if>
                        	</s:else>
                        </s:if>
                        <s:else>
                        <s:property value="claim.policyCode"/>
                        </s:else>
                        </td>
                        <authz:ifProcessor>
                            <s:if test="task.claim.forMultipleItems">
                                <td class="text">
                                    <s:if test="processorApproved">
                                        <span style="color:green">
                                            <s:text name="label.common.yes" />
                                        </span>
                                    </s:if>
                                    <s:else>
                                        <span style="color:red">
                                            <s:text name="label.common.no" />
                                        </span>
                                    </s:else>
                                </td>
                            </s:if>
                        </authz:ifProcessor>
                    </tr>
                </s:iterator>
                </tbody>
            </table>
        </div>

    <s:push value="task">
    	<s:if test="!partsReplacedInstalledSectionVisible">
        	<jsp:include page="oempartreplacedforvalidate.jsp"/>
        </s:if>
        <s:else>
        	<jsp:include page="hussmannPartsReplacedInstalledforvalidate.jsp" />
        </s:else>
        
    </s:push>
	
    <s:push value="task">
        <s:if test="baseFormName == 'processor_review'">
            <%--
                This is a special case; we need to show the 4-column payment computation here
            --%>
            <jsp:include page="../../processor_review/read/payment.jsp"/>
        </s:if>
        <s:elseif test="claim.appealed == 'true'  && takenTransition == 'Accept'">
        	<jsp:include page="../../processor_review/read/payment.jsp"/>
        </s:elseif>
        <s:else>
            <jsp:include page="payment.jsp"/>
        </s:else>
    </s:push>    
    <authz:ifDealer> 
    <s:if test="(task.claim.state.state=='draft' || task.claim.state.state == 'Forwarded') && legalDisclaimerAllowed">
    <div >
    	<div id="disclaimer" class="mainTitle" style="margin:10px 0px 5px 0px">
			 <s:text name="title.viewClaim.legalDisclaimer"/>
			 </div>
			 <div class="borderTable">&nbsp;</div>
            <table border ="0">
            	<tr>
            		<td  align="left" width="60%"><center>
            			<s:text name="message.legalDisclaimer">
            			<s:param ><s:property value="getLoggedInUser().getCompleteName()"/></s:param>            		
            			</s:text>
            				</center>
            		</td>
            	</tr>
            	<br> 
            	<tr>
            		<td align="center" width="80%">            		
            		<input type="radio" name="checkAcceptance" id="actionDecidedAccept"  />
            		<s:text name="legal.disclaimer.accept"/>					
					<input type="radio" name ="checkAcceptance" id="actionDecidedReject" checked="checked"/>
					<s:text name="legal.disclaimer.reject"/>		
            		</td>             		           		  
            	</tr>
            </table>
       
        <script type="text/javascript">
        
        dojo.addOnLoad(function(){
	                    <s:if test="task.claim.state.state == 'Appealed' || task.claim.state.state == 'Reopened'">
	                        dojo.html.hide(dojo.byId("submitButton4")); 
	                    </s:if>
	                    <s:elseif test="task.claim.type.type == 'Campaign'">
	                	    dojo.html.hide(dojo.byId("submitButton3")); 
	                	</s:elseif>    
	                	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
	                	    dojo.html.hide(dojo.byId("submitButton")); 
	                	</s:elseif> 
	                	<s:else>
	                	    dojo.html.hide(dojo.byId("submitButton2")); 
	                	</s:else>   
	                	        	
                	   dojo.connect(dojo.byId("actionDecidedAccept"), "onclick", function() {                       
                       if(dojo.byId("actionDecidedAccept").checked){
                		<s:if test="task.claim.state.state == 'Appealed' || task.claim.state.state == 'Reopened'">
                           dojo.html.show(dojo.byId("submitButton4")); 
                        </s:if>
	                    <s:elseif test="task.claim.type.type == 'Campaign'">
	                	    dojo.html.show(dojo.byId("submitButton3")); 
	                	</s:elseif>    
	                	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
	                	    dojo.html.show(dojo.byId("submitButton")); 
	                	</s:elseif> 
	                	<s:else>
	                	    dojo.html.show(dojo.byId("submitButton2")); 
	                	</s:else>                       		
                		}
                    });                  
                    
					
                    dojo.connect(dojo.byId("actionDecidedReject"), "onclick", function() {
                       if(dojo.byId("actionDecidedReject").checked){
                		<s:if test="task.claim.state.state == 'Appealed' || task.claim.state.state == 'Reopened'">
	                        dojo.html.hide(dojo.byId("submitButton4")); 
	                    </s:if>
	                    <s:elseif test="task.claim.type.type == 'Campaign'">
	                	    dojo.html.hide(dojo.byId("submitButton3")); 
	                	</s:elseif>    
	                	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
	                	    dojo.html.hide(dojo.byId("submitButton")); 
	                	</s:elseif> 
	                	<s:else>
	                	    dojo.html.hide(dojo.byId("submitButton2")); 
	                	</s:else>                  		
                		}
                    });
                })
        </script>
    </div>
    </s:if>
    </authz:ifDealer>    

    <table class="buttons" width="100%">
        <tr>
            <td>
            
            	<s:if test="messages!=null && messages.hasErrors()">
            		<s:submit id="validations_hider2" value="%{getText('button.newClaim.editClaim')}" type="button"/>
	                <script type="text/javascript">
	                dojo.addOnLoad(function() {
	                	 dojo.connect(dojo.byId("validations_hider2"), "onclick", function() {
	                        dijit.byId("validations").hide();
	                    });
	                });
	                </script>
				</s:if>
				<s:else>
					<s:hidden name="task"/>
					<s:submit id="validations_hider3" value="%{getText('button.newClaim.editClaim')}" type="button"/>
					<script type="text/javascript">
	                dojo.addOnLoad(function() {
	                	 dojo.connect(dojo.byId("validations_hider3"), "onclick", function() {
	                         dijit.byId("validations").hide();
	                    });
	                });
	                </script>
				</s:else>
                <s:if test="messages==null || !messages.hasErrors()">
                	<s:if test="task.claim.state.state == 'Appealed' || task.claim.state.state == 'Reopened'">
                		<s:submit id="submitButton4" value="%{getText('label.claim.submitClaim')}" type="button"/>
                	</s:if>
                	<s:elseif test="task.claim.type.type == 'Campaign'">
                		<s:submit id="submitButton3" value="%{getText('label.claim.submitClaim')}" type="button"/>
                	</s:elseif>
                    <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
                    	<s:submit id="submitButton" value="%{getText('label.claim.submitClaim')}" type="button"/>
                    </s:elseif>
                    <s:else>
                    	<s:submit id="submitButton2" value="%{getText('label.claim.submitClaim')}" type="button"/>
                    </s:else>
                    <s:if test="task.baseFormName == 'processor_review' || task.baseFormName == 'part_shipped_not_received' 
                    		|| task.claim.state.state == 'Appealed' || task.claim.state.state == 'Reopened'">
                        <%-- In this case we simply need to submit the form --%>
                        <jsp:include page="../../processor_review/write/submit.jsp"/>
                    </s:if>
                    <s:elseif test="task.baseFormName == 'draft_claim'">
                        <%--
                        In this case we need to add the takenTransition input field, and then submit the form
                        --%>
                        <jsp:include page="../../draft_claim/write/submit.jsp"/>
                    </s:elseif>
                    <s:elseif test="task.baseFormName == 'forwarded'">
                        <%--
                        In this case we need to add the takenTransition input field, and then submit the form
                        --%>
                        <jsp:include page="../../forwarded/write/submit.jsp"/>
                    </s:elseif>
                </s:if>
                
           </td>
        </tr>
    </table>
</div>

</div>
</div>


<script type = "text/javascript">
dojo.addOnLoad(function() {
	dojo.html.hide(dojo.byId("submittingIndicationDiv"));
	var submitButton = dojo.byId("submitButton");
	if(submitButton) {
		dojo.connect(submitButton, "onclick", function(event) {
			loadSubmitMessage();
		});
	}
	var submitButton2 = dojo.byId("submitButton2");
	if(submitButton2) {
		dojo.connect(submitButton2, "onclick", function(event) {
			loadSubmitMessage();
		});
	}
	var submitButton3 = dojo.byId("submitButton3");
	if(submitButton3) {
		dojo.connect(submitButton3, "onclick", function(event) {
			loadSubmitMessage();
		});
	}
	var submitButton4 = dojo.byId("submitButton4");
	if(submitButton4) {
		dojo.connect(submitButton4, "onclick", function(event) {
			loadSubmitMessage();
		});
	}
});

function loadSubmitMessage() {
	dojo.html.hide(dojo.byId("validationsDiv"));
	dojo.html.show(dojo.byId("submittingIndicationDiv"));
}
</script>
