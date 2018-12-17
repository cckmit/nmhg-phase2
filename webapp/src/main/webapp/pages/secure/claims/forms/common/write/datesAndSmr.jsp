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
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<style>
	.dijitTextBox{width:145px;}
</style>

<table class="form" style="border-top:1px solid #EFEBF7; border-bottom:none;border-left:none; border-right:none;" cellpadding="0" cellspacing="0">
    <tbody>
    <tr><td style="height:2px;" colspan="4"></td></tr>
   	 	<tr>
   	 	    <s:if test="%{showDealerJobNumber()}">
			<td width="26%"><label for="workOrderNumber" class="labelStyle"> <s:text
				name="label.claim.workOrderNumber" />: </label></td>
			<td width="35%" style="padding-left:0;">
				<s:if test="toBeChecked('PART_INSTALLED_ON_NON_SERIALIZED_HOST')">
					<s:textfield name="claim.workOrderNumber" cssStyle="width:145px;" id="workOrderNumber" theme="simple" />
				</s:if>
				<s:elseif test="toBeChecked('PART_INSTALLED_ON_HOST')">
					<s:textfield name="claim.workOrderNumber" cssStyle="width:145px;" id="workOrderNumber" theme="simple" />
				</s:elseif>
				<s:else>
					<s:textfield name="claim.workOrderNumber" cssStyle="width:145px; margin-left:4px;" id="workOrderNumber" theme="simple" />
				</s:else>
			</td>
			</s:if>
			<s:if test="%{displayCPFlagOnClaimPgOne}">
			<td width="15%" nowrap="nowrap"><label for="commercialPolicy"
				id="commercialPolicy" class="labelStyle"> <s:text
				name="label.newClaim.commercialPolicy" />: </label></td>

			<td style="padding-left:0;"><s:checkbox name="claim.commercialPolicy"
				id="comPolicyCheck" cssClass="checkbox"/></td>
			</s:if>
			<s:else>
			<td width="15%" nowrap="nowrap"></td>
			<td style="padding-left:0;"></td>
			</s:else>
			</tr>
			<tr>
				<td style="height:2px;" colspan="4"></td>
			</tr>
            <tr>
            <td width="26%">
                <label for="dateOfFailure" class="labelStyle">
                    <s:text name="label.viewClaim.dateOfFailure"/>:
                </label>
            </td>
            <td width="35%" align="left">
                <sd:datetimepicker cssStyle='width:145px;' name='claim.failureDate' id='dateOfFailure' />
            </td>
            <%-- The following would have been good if it worked; didn't work --%>
            <%-- script type="text/javascript">
            dojo.addOnLoad(function() {
                var repairDate = dijit.byId("dateOfRepair");
                dojo.connect(dijit.byId("dateOfFailure"),"onValueChanged", function(failureDate) {
                    repairDate.startDate = failureDate;
                });
            });
            </script --%>
             <td width="15%">
                <label for="repairEndDate" class="labelStyle">
                    <s:text name="label.viewClaim.repairStartDate" />:
                </label>
            </td>
             <td>
                <sd:datetimepicker cssStyle='width:145px;align:left;padding-left:0px;' name='claim.repairStartDate' label='Format (yyyy-MM-dd)' id='Repair' />
            </td>
        </tr>
        <tr>
         <s:if test="%{showAuthorizationReceived()}">
            <td>
            	<label for="cmsAuthLabel" class="labelStyle">
                    <s:text name="label.viewClaim.cmsAuth"/>
                </label>
            </td>
            <td style="padding-left:0;">
            <s:if test="claim.authNumber != null">
                <s:checkbox name="claim.cmsAuthCheck" id="cmsAuthCheck" cssClass="checkbox" />
            </s:if>
            <s:else>
                <s:checkbox name="claim.cmsAuthCheck" id="cmsAuthCheck" cssClass="checkbox" />
            </s:else>
            </td>
            </s:if>
             <td width="15%">
                <label for="dateOfRepair" class="labelStyle">
                    <s:text name="label.viewClaim.dateOfRepair" />:
                </label>
            </td>
            <td>
                <sd:datetimepicker cssStyle='width:145px;align:left;padding-left:0px;' name='claim.repairDate' label='Format (yyyy-MM-dd)' value='%{claim.repairDate}' id='dateOfRepair' />
            </td>


        </tr>
        <tr>
               <s:if test="%{showAuthorizationNumber()}">
                       <td style="padding-bottom:5px;" nowrap="nowrap">
            	<label id="authNumberLabel" class="labelStyle">
                    <s:text name="label.viewClaim.authNumber"/>:
                </label>
             </td>
             <td>
                 <s:textfield name="authNumber1" cssStyle="width:145px;" value="%{claim.authNumber}" id="authNumber1" theme="simple"  maxlength="255" onblur="setHiddenValue()"/>
                 <s:hidden name="claim.authNumber" id="claim.authNumber" theme="simple"  maxlength="255" />
             </td>
             </s:if>
             <td style="padding-bottom:5px;" nowrap="nowrap">
            	<label id="cmsTicketLabel" class="labelStyle">
                    <s:text name="label.viewClaim.cmsTicket"/>:
                </label>
             </td>
             <td style="padding-bottom:5px;" nowrap="nowrap">
               <s:textfield name="claim.cmsTicketNumber" cssStyle="width:145px;" id="cmsTicketNumber" value='%{claim.cmsTicketNumber}'   maxlength="13" theme="simple" />
             </td>
        </tr>

        <tr id="ncr" style="display:none">
        <s:if test="!isLoggedInUserADealer() && displayNCRandBT30DayNCROnClaimPage()">
                       <td style="padding-bottom:5px;" nowrap="nowrap">
            	<label id="authNumberLabel" class="labelStyle">
                    <s:text name="label.claim.ncr"/>
                </label>
             </td>
             <td>
                 <s:checkbox name="claim.ncr"  cssClass="checkbox"/>
             </td>
          </s:if>
          
          <s:if test="((!isLoggedInUserADealer() && !isLoggedInUserNcrProcessor()) || !getAllowed30DayNcrClasses().isEmpty()) && displayNCRandBT30DayNCROnClaimPage()">
             <td style="padding-bottom:5px;" nowrap="nowrap">
            	<label id="cmsTicketLabel" class="labelStyle">
                    <s:text name="label.claim.ncrWith30Days"/>:
                </label>
             </td>
				<td style="padding-bottom:5px;" nowrap="nowrap">
                <s:if test="!isLoggedInUserADealer()">
                	<s:set name="applicableClassSet" value="%{all30DayNcrClasses}"/>
                </s:if>
                <s:else>
                	<s:set name="applicableClassSet" value="%{allowed30DayNcrClasses}"/>
                </s:else>
               	<s:select id="select30DayNcrClass" list="applicableClassSet" listKey="id" listValue="name"
					name="claim.inventoryClassFor30DayNcr" value='%{claim.inventoryClassFor30DayNcr.getId().toString()}'
					headerKey="-1" headerValue="%{getText('label.common.selectHeader')}"
					style="width: 15em;"
				/>
             </td>
        </s:if>        

         </tr>
         <s:if test="isLoggedInUserAnAdmin() && enableWarrantyOrderClaims()">
                <tr><td style="padding-bottom:5px;" nowrap="nowrap">
                     <label id="cmsTicketLabel" class="labelStyle">
                          <s:text name="label.viewClaim.warrantyOrderClaim"/>:
                     </label>
                     </td>
                     <td style="padding-bottom:5px;" nowrap="nowrap">
                          <s:checkbox name="claim.warrantyOrder" cssClass="checkbox"/>
                     </td>
                </tr>
         </s:if>


        <script type="text/javascript">
        if(dojo.byId('type').value == "Machine" && document.getElementById('ncr') )
        {
        document.getElementById('ncr').style.display='';
        }
        </script>
        
        <tr>
        <s:if test="dealerEligibleToFillSmrClaim">
        <td width="26%">
                <label for="isSmr" class="labelStyle">
                    <s:text name="label.viewClaim.requestSMR"/>:
                </label>
            </td>
            <td width="35%" style="padding-left:0;">
                <s:if test="toBeChecked('PART_INSTALLED_ON_NON_SERIALIZED_HOST')">
					<s:checkbox name="claim.serviceManagerRequest" id="smrCheck" cssClass="checkbox"/>
				</s:if>
				<s:elseif test="toBeChecked('PART_INSTALLED_ON_HOST')">
					<s:checkbox name="claim.serviceManagerRequest" id="smrCheck" cssClass="checkbox"/>
				</s:elseif>
				<s:else>
					<s:checkbox name="claim.serviceManagerRequest" cssStyle="margin-left:4px;" id="smrCheck" cssClass="checkbox"/>
				</s:else>			
            </td>
            	</s:if>
            	<s:if test="displayEmissionOnClaimPage()">
            	<td> 
            	  <label for="isEmission" class="labelStyle">
                    <s:text name="label.viewClaim.emission"/>:
                </label>
            	</td>
            	<td> 
            	   <s:checkbox name="claim.emission" id="emissionCheck" cssClass="checkbox"/>
            	</td>
            	</s:if>
            </tr>
            
			<s:if test="dealerEligibleToFillSmrClaim">
            <tr> <td width="15%" style="height:30px;">
                <label id="smrReasonLabel" style="display:none" class="labelStyle">
                    <s:text name="label.viewClaim.smrReason"/>:
                </label>
            </td>
            <td>
            	<span id="smrReasonSpan">
            		<tda:lov name="claim.reasonForServiceManagerRequest" id="smrReason"
            		className="SmrReason" cssStyle="width:360px; text-align: left"/>
            	</span>
           		<span id="dummySmr">
           			<input type="hidden" name="claim.reasonForServiceManagerRequest" value="null"/>
           		</span>
            </td>
           
            </tr>
            </s:if>
            <tr><td style="padding:0;margin:0" colspan="4">&nbsp;</td></tr>
    </tbody>
</table>
 <script type="text/javascript">
            dojo.addOnLoad(function() {
            	<s:if test="dealerEligibleToFillSmrClaim">
                var check = dojo.byId("smrCheck");

                if (check.checked) {
                    showSmrReason();
                }
                dojo.connect(check, "onclick", function(evt) {
                    if (evt.target.checked) {
                        showSmrReason();
                    } else {
                        hideSmrReason();
                    }
                });
                </s:if>
               var checkAuth = dojo.byId("cmsAuthCheck");
               if (checkAuth && checkAuth.checked) {
                 	document.getElementById("authNumber1").disabled = false;
               }
               else{
                   if(document.getElementById("authNumber1")!=null){
                     document.getElementById("authNumber1").disabled = true;
                   }
               }
               if(checkAuth){
               dojo.connect(checkAuth, "onclick", function(evt1) {
               if (evt1.target.checked) {
                    	document.getElementById("authNumber1").disabled = false;
                    	setHiddenValue()
               } else {
                    document.getElementById("authNumber1").disabled = true;
                   	document.getElementById("authNumber1").value=null;
                   	setHiddenValue();
                  }
                });
               }
            });
            function setHiddenValue(){
                 document.getElementById("claim.authNumber").value = document.getElementById("authNumber1").value;
            }
            </script>