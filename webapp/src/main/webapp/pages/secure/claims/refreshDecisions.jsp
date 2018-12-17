
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

				 
<script type="text/javascript">	
//Function to refresh decision section on late fee changes
function refreshDecision(lateFee){		
			var isUserLAteFeeSupervisor=<s:property value="isLoggedInUserEligibleForLateFeeApproval()"/>;			
			if(!isUserLAteFeeSupervisor)
				{	
				var updatedLateFee;
				var lateFee
				if(lateFee=='flat')
					{
					lateFee=<s:property value="task.claim.payment.getLineItemGroup('Late Fee').acceptedTotal.breachEncapsulationOfAmount()"/>;
					updatedLateFee=document.getElementById("late_fee_flat").value;
					}
				else
					{
					lateFee=<s:property value="task.claim.payment.getLineItemGroup('Late Fee').percentageAcceptance"/>;
					updatedLateFee=document.getElementById("late_fee_percentage").value;	
					}			
			if(parseFloat(lateFee)!=parseFloat(updatedLateFee))
				 {			
				dojo.byId("lateFeeChanged").value=true;				
				 }	
			else
				{				
				dojo.byId("lateFeeChanged").value=false;
				}
			
				var form = document.getElementById("claim_form");
		        var calculationIndicator = "<center><img src=\"image/indicator.gif\" class=\"indicator\"/><s:text name="label.common.refreshing"/></center>";
		            //dojo.stopEvent(event);
		             var actionsSection = dijit.byId("actionList");
		             var content = {};
		        form.action="refresh_actions.action";
		             dojo.xhrPost({
		                 form: form,
		                 content: content,
		                 load: function(data) {
		                 	//alert(data);
		             		actionsSection.setContent(data);
		                     form.action="claim_submit.action";
		                 },
		                 error: function(error) {
		                	 console.log(" Error");
		                 }	      
			 });
		             actionsSection.setContent(calculationIndicator);
			}
}
</script>
