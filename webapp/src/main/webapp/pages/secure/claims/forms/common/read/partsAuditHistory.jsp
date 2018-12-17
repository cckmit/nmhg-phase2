<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Jun 26, 2008
  Time: 2:40:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<s:if test="!partsReplacedInstalledSectionVisible" >
<s:iterator value="claim.serviceInformation.serviceDetail.OEMPartsReplaced" status="partReplacedStatus">
 <s:if test="partReturnAudits!=null && partReturnAudits.size>0">
	<div class="mainTitle" style="margin-top:5px;">
	<s:text name="label.common.partNumber"/>&nbsp(<s:property value="itemReference.unserializedItem.number"/> ) 
	</div>
	<div class="borderTable" >&nbsp;</div>
<table class="grid" width="100%" cellspacing="0" cellpadding="0" style="margin-top:-10px;">
	 <tr>
		<td class="labelStyle" nowrap="nowrap" width="10%"><s:text name="columnTitle.shipmentGenerated.qty"/>:</td>
        <td class="non_editable">
            <s:property value="numberOfUnits"/>
        </td>
     </tr>
     <tr>
		<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="columnTitle.shipmentGenerated.return_location"/>:</td>
		<td class="non_editable" width="30%" >
            <s:property value="partReturns[0].returnLocation.code"/>
        </td>
   	 </tr>
   	 <s:if test=" isLoggedInUserAnInternalUser()">
   	 <tr>
		<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.recoveryClaim.binInfo"/>:</td>
		<td class="non_editable" width="30%">
		  <s:if test="activePartReturn.warehouseLocation!=NULL && !''.equals(activePartReturn.warehouseLocation)">
            <s:property value="activePartReturn.warehouseLocation"/>
          </s:if>
          <s:else>
           	<s:text name="-" />
          </s:else>
        </td>
   	 </tr>
   </s:if>
</table>
<table  class="grid borderForTable" style="width:97%" cellspacing="0" cellpadding="0">
	<thead>
		<tr class="row_head">
            <th><s:text name="label.common.date" /></th>
			<th ><s:text name="label.policyAudit.status"/></th>
			<th ><s:text name="label.common.user"/></th>
			<th ><s:text name="label.common.comments"/></th>
            <th><s:text name="label.partReturnAudit.actionPerformed"/></th>
        </tr>
	</thead>
	<tbody>
       <s:iterator value="partReturnAudits" status="auditStatus">
       <s:if test="!(isLoggedInUserADealer() && (prStatus=='Part Received' || prStatus=='Part Accepted' || prStatus== 'Part Rejected'))">
           <tr>
            <td ><s:property value="d.updatedOn"/></td>
            <td ><s:property value="prStatus"/></td>
             <s:set var = "breakLoop" value = "%{false}" />		       	
           	<s:iterator value="d.lastUpdatedBy.roles">		             					
				<s:if test="!#breakLoop && name=='dealer'">
					<s:set var = "breakLoop" value = "%{true}"/>
				</s:if>																														
			</s:iterator>
			<!-- checking if the comments are entered by 'dealer' or not -->
          		<s:if test="isLoggedInUserASupplier() && isBuConfigAMER() && #breakLoop">			            							
					<td ><s:text name="label.partReturnAudit.dealer"/></td>	
					<td></td>	
				</s:if>	
				<s:else>
					<td ><s:property value="d.CompleteNameAndLogin"/></td>
	            	<td><s:property value="comments"/></td>
				</s:else>            
            <td>
                <table border="0">
                    <s:if test="partReturnAction1!=null">
                    <tr>
                        <td>
                        	<s:property value="partReturnAction1.actionTaken"/>:<s:property value="partReturnAction1.value"/>&nbsp;&nbsp;

                        	<s:if test="(prStatus=='WPRA Generated')">
                                (<s:property value="partReturnAction1.wpraNumber"/>)
                            </s:if>
                        	<s:if test="(prStatus=='Partially Shipped' || prStatus=='Part Shipped') &&  partReturnAction1.trackingNumber!=null">
                                (<s:property value="partReturnAction1.trackingNumber"/>)
                            </s:if>
                            <s:if test="(prStatus=='Shipment Generated' || prStatus=='Partially Shipment Generated') && partReturnAction1.shipmentId!=null">
                                (<s:property value="partReturnAction1.shipmentId"/>)
                            </s:if>
                        </td>

                    </tr>
                    <s:if test="partReturnAction1.actionTaken=='Part Accepted' && !acceptanceCause.isEmpty()">
                    <tr>
                    <td> <s:text name="label.partReturnAudit.acceptanceReason"/>:&nbsp;<s:property value="acceptanceCause"/></td>
                    </tr>
                    	</s:if>
                    </s:if>
                    <s:if test="partReturnAction2!=null">
                    <tr>
                        <td>
                            <s:if test="prStatus=='Partially Received' ||prStatus=='Part Received' ||prStatus=='Part Not Received' ">
                                <s:text name="label.common.notReceived"/>:
                            </s:if>
                            <s:else>
                            <s:property value="partReturnAction2.actionTaken"/>:
                            </s:else>
                            <s:property value="partReturnAction2.value"/>

                        	</td>
                    </tr>
                    <s:if test="partReturnAction2.actionTaken=='Part Rejected' && !failureCause.isEmpty()">
                    <tr>

                       <td> <s:text name="label.partReturnAudit.rejectionReason"/>:&nbsp;<s:property value="failureCause"/></td>


                    </tr></s:if>
                    </s:if>
                    <s:if test="partReturnAction3!=null">
                        <tr>
                            <td>

                                <s:property value="partReturnAction3.actionTaken"/>:
                                <s:property value="partReturnAction3.value"/>
                                </td>
                        </tr>
                    </s:if>
                </table>
            </td>
            </tr>
            </s:if>
        </s:iterator>
	</tbody>
</table>

 </s:if>
 </s:iterator>
</s:if>
<s:else>
	<s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled != null">
	<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="partReplacedStatus">
	<s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#partReplacedStatus.index].replacedParts != null">
		<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#partReplacedStatus.index].replacedParts" status="partReplacedStatus2">
		 <s:if test="partReturnAudits!=null && partReturnAudits.size>0">
		<div class="mainTitle" style="margin-top:10px;padding-bottom:10px;"> <s:text name="label.common.partNumber"/> &nbsp(<s:property value="itemReference.unserializedItem.number"/> )
		   </div>
		<table width="100%" class="grid" cellspacing="0" cellpadding="0" >
			 <tr>
				<td class="labelStyle" width="10%" nowrap="nowrap"><s:text name="columnTitle.shipmentGenerated.qty"/>:</td>
		        <td class="non_editable">
		            <s:property value="numberOfUnits"/>
		        </td>
		     </tr>
		     <tr>
				<td class="labelStyle" width="10%" nowrap="nowrap"><s:text name="columnTitle.shipmentGenerated.return_location"/>:</td>
				<td class="non_editable">
				 	<s:if test="isLoggedInUserACanadianDealer()">
	     				<s:property value="getCentralLogisticLocation()"/>
	     			 </s:if>
	     			 <s:else>
	     				<s:property value="partReturns[0].returnLocation.code"/>
	     			 </s:else>		            
		        </td>
		   	 </tr>
		   	 <s:if test="returnDirectlyToSupplier" >
		   	    <tr>
				<td class="labelStyle" width="10%" nowrap="nowrap"><s:text name="columnTitle.partShipperPartsShipped.RGA_number"/>:</td>
				<td class="non_editable">
	     				<s:property value="partReturns[0].rmaNumber"/>
		        </td>
		   	 </tr>
		   	 </s:if>
		   	 <s:if test=" isLoggedInUserAnInternalUser()">
			   	 <tr>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.recoveryClaim.binInfo"/>:</td>
					<td class="non_editable" width="30%">
					  <s:if test="activePartReturn.warehouseLocation!=NULL && !''.equals(activePartReturn.warehouseLocation)">
			            <s:property value="activePartReturn.warehouseLocation"/>
			          </s:if>
			          <s:else>
			           	<s:text name="-" />
			          </s:else>
			        </td>
			   	 </tr>
			   </s:if>
		</table>
		<table  style="width:97%" class="grid borderForTable" cellspacing="0" cellpadding="0">
			<thead>
				<tr class="row_head">
		            <th ><s:text name="label.common.date" /></th>
					<th><s:text name="label.policyAudit.status"/></th>
					<th ><s:text name="label.common.user"/></th>
					<th><s:text name="label.common.comments"/></th>
		            <th ><s:text name="label.partReturnAudit.actionPerformed"/></th>
		        </tr>
			</thead>
			<tbody>
			   <s:if test="claim.foc">
		       <s:iterator value="partReturnAudits" status="auditStatus">
		       <s:if test="!(isLoggedInUserADealer() && (prStatus=='Part Received' || prStatus=='Part Accepted' || prStatus== 'Part Rejected'))">
		       <s:if test="id!=partReturnAudits[0].id || partReturnAudits.size() == 1">
		           <tr>
		            <td ><s:property value="d.updatedOn"/></td>
		            <td><s:property value="prStatus"/></td>
		            <s:set var = "breakLoop" value = "%{false}" />		       	
		             	<s:iterator value="d.lastUpdatedBy.roles">		             					
							<s:if test="!#breakLoop && name=='dealer'">
								<s:set var = "breakLoop" value = "%{true}"/>
							</s:if>																														
						</s:iterator>
							<!-- checking if the comments are entered by 'dealer' or not -->
		            		<s:if test="isLoggedInUserASupplier() && isBuConfigAMER() && #breakLoop">			            							
								<td ><s:text name="label.partReturnAudit.dealer"/></td>	
								<td></td>	
							</s:if>		
							<s:else>
							 	<td ><s:property value="d.lastUpdatedBy.CompleteNameAndLogin"/></td>
		            			<td ><s:property value="comments"/></td>
							</s:else>		           
		            <td>
		                <table border="0">
		                    <s:if test="partReturnAction1!=null">
		                    <tr>
		                        <td>
		                        	<s:property value="partReturnAction1.actionTaken"/>:<s:property value="partReturnAction1.value"/>&nbsp;&nbsp;
                        	        <s:if test="(prStatus=='WPRA Generated')">
                                        (<s:property value="partReturnAction1.wpraNumber"/>)
                                    </s:if>

		                        	<s:if test="(prStatus=='Partially Shipped' || prStatus=='Part Shipped') &&  partReturnAction1.trackingNumber!=null">
		                                (<s:property value="partReturnAction1.trackingNumber"/>)
		                            </s:if>
		                            <s:if test="(prStatus=='Shipment Generated' || prStatus=='Partially Shipment Generated') && partReturnAction1.shipmentId!=null">
		                                (<s:property value="partReturnAction1.shipmentId"/>)
		                            </s:if>
		                        </td>

		                    </tr>
		                    <s:if test="partReturnAction1.actionTaken=='Part Accepted' && !acceptanceCause.isEmpty()">
                    <tr>
                    <td> <s:text name="label.partReturnAudit.acceptanceReason"/>:&nbsp;<s:property value="acceptanceCause"/></td>
                    </tr>
                    	</s:if>
		                    </s:if>
		                    <s:if test="partReturnAction2!=null">
		                    <tr>
		                        <td>
		                            <s:if test="prStatus=='Partially Received' ||prStatus=='Part Received' ">
		                                <s:text name="label.common.notReceived"/>:
		                            </s:if>
		                            <s:else>
		                            <s:property value="partReturnAction2.actionTaken"/>:
		                            </s:else>
		                            <s:property value="partReturnAction2.value"/>   </td>
		                    </tr>
		                    <s:if test="partReturnAction2.actionTaken=='Part Rejected' && !failureCause.isEmpty()">
                    <tr>

                       <td> <s:text name="label.partReturnAudit.rejectionReason"/>:&nbsp;<s:property value="failureCause"/></td>


                    </tr></s:if>
		                    </s:if>
		            <s:if test="partReturnAction3!=null">
                            <tr>
                                <td>

                                    <s:property value="partReturnAction3.actionTaken"/>:
                                    <s:property value="partReturnAction3.value"/>   </td>
                            </tr>
                    <tr>
                    </s:if>
		                </table>
		            </td>
		            </tr>
		            </s:if>
		            </s:if>
		        </s:iterator>
		        </s:if>
		        <s:else>
		        <s:iterator value="partReturnAudits" status="auditStatus">
		        <s:if test="!(isLoggedInUserADealer() && (prStatus=='Part Received' || prStatus=='Part Accepted' || prStatus== 'Part Rejected'))">
		           <tr>
		            <td ><s:property value="d.updatedOn"/></td>
		            <td><s:property value="prStatus"/></td>
		            <s:set var = "breakLoop" value = "%{false}" />		       	
		             	<s:iterator value="d.lastUpdatedBy.roles">		             					
							<s:if test="!#breakLoop && name=='dealer'">
								<s:set var = "breakLoop" value = "%{true}"/>
							</s:if>																														
						</s:iterator>
							<!-- checking if the comments are entered by 'dealer' or not -->
		            		<s:if test="isLoggedInUserASupplier() && isBuConfigAMER() && #breakLoop">			            							
								<td ><s:text name="label.partReturnAudit.dealer"/></td>	
								<td></td>	
							</s:if>							
							<s:else>
								 <td>
					           		<authz:ifUserInRole roles="supplier">
			            		            <s:if test="isDealerComments(partReturnAudits[#auditStatus.index]) && buConfigAMER()">
						            <s:text name="label.common.dealer"/></s:if>
						            <s:else><s:property value="d.lastUpdatedBy.CompleteNameAndLogin"/></s:else>
						            </authz:ifUserInRole>
				            		<authz:else><s:property value="d.lastUpdatedBy.CompleteNameAndLogin"/></authz:else></td>
				            		<td ><authz:ifUserInRole roles="supplier">
						            		            <s:if test="isDealerComments(partReturnAudits[#auditStatus.index])&& buConfigAMER()">
						            <s:text name=""/></s:if>
						            <s:else><s:property value="comments"/></s:else>
						            </authz:ifUserInRole>
				            		<authz:else><s:property value="comments"/></authz:else>
						            
						            </td>
							</s:else>
		            <td>
		                <table border="0" class="NoborderForTable">
		                    <s:if test="partReturnAction1!=null">
		                    <tr>
		                        <td>
		                        	<s:property value="partReturnAction1.actionTaken"/>:<s:property value="partReturnAction1.value"/>
		                            <s:if test="(prStatus=='WPRA Generated')">
                                        (<s:property value="partReturnAction1.wpraNumber"/>)
                                    </s:if>

		                        	<s:if test="(prStatus=='Partially Shipped' || prStatus=='Part Shipped') &&  partReturnAction1.trackingNumber!=null">
		                                (<s:property value="partReturnAction1.trackingNumber"/>)
		                            </s:if>
		                            <s:if test="(prStatus=='Shipment Generated' || prStatus=='Partially Shipment Generated') && partReturnAction1.shipmentId!=null">
		                                (<s:property value="partReturnAction1.shipmentId"/>)
		                            </s:if>
		                        </td>

		                    </tr>
		                    <s:if test="partReturnAction1.actionTaken=='Part Accepted' && !acceptanceCause.isEmpty()">
                    <tr>
                    <td> <s:text name="label.partReturnAudit.acceptanceReason"/>:&nbsp;<s:property value="acceptanceCause"/></td>
                    </tr>
                    	</s:if>
		                    </s:if>
		                    <s:if test="partReturnAction2!=null">
		                    <tr>
		                        <td >
		                            <s:if test="prStatus=='Partially Received' ||prStatus=='Part Received' ">
		                                <s:text name="label.common.notReceived"/>:
		                            </s:if>
		                            <s:else>
		                            <s:property value="partReturnAction2.actionTaken"/>:
		                            </s:else>
		                            <s:property value="partReturnAction2.value"/>   </td>
		                    </tr>
		                    <s:if test="partReturnAction2.actionTaken=='Part Rejected' && !failureCause.isEmpty()">
                    <tr>

                       <td> <s:text name="label.partReturnAudit.rejectionReason"/>:&nbsp;<s:property value="failureCause"/></td>

                    </tr></s:if>
		                    </s:if>
		              <s:if test="partReturnAction3!=null">
                                  <tr>
                                      <td>

                                          <s:property value="partReturnAction3.actionTaken"/>:
                                          <s:property value="partReturnAction3.value"/>   </td>
                                  </tr>
                          <tr>
                          </s:if>
		                </table>
		            </td>
		            </tr>
		            </s:if>
		        </s:iterator>
		        </s:else>
			</tbody>
		</table>

		 </s:if>
		 </s:iterator>
		 </s:if>
		 </s:iterator>
		 </s:if>
</s:else>