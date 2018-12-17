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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tda" uri="twmsDomainAware"%>
<%@taglib prefix="t" uri="twms"%>
<script type="text/javascript">
  	dojo.addOnLoad(function(){
		var idsPresent=["transferRadio","denyRadio","acceptRadio"];
		for(var i=0;i<idsPresent.length;i++){
			dojo.connect(dojo.byId(idsPresent[i]), "onchange", function(event) {
				if(event.target.checked){
					showEnabledFields(event.target.id);
				}
	   		});
   		}
	});
	function showEnabledFields(selectedId){
        var allIds=["transferRadio","denyRadio","acceptRadio"];
        if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='transferRadio'){
        dijit.byId("serviceManagers").setDisabled(false);
        
        dojo.byId("task.claim.decision").value='Forward to DSM';
		}
        if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='denyRadio'){
            dijit.byId("serviceManagers").setDisplayedValue("--Select--");
            dijit.byId("serviceManagers").setDisabled(true);
            dojo.byId("validateButton").disabled=false;
            dojo.byId("task.claim.decision").value='Recommended to Reject';
            
        }
        if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='acceptRadio'){
            dijit.byId("serviceManagers").setDisplayedValue("--Select--");
            dijit.byId("serviceManagers").setDisabled(true);
            dojo.byId("validateButton").disabled=false;
            dojo.byId("task.claim.decision").value='Recommended to Approve';
        }
	}
	function showDisabledFields(selectedId){
		dijit.byId("serviceManagers").setDisabled(true);
		dojo.html.hide(dojo.byId("transferClaim"));
		dojo.html.show(dojo.byId("validateButton"));
	}
</script>
<table width="96%" border="0" cellspacing="0" cellpadding="0" class="grid">
	<tr>
		<td width="12%" nowrap="nowrap" class="labelNormalTop labelStyle">
			<s:text name="label.common.externalComments"/>:
		</td>
		<td width="70%" class="labelNormalTop">
			<t:textarea name="task.claim.externalComment" rows="4" cols="50" value=""/>
		</td>
	</tr>
	<tr>
		<td width="12%" nowrap="nowrap" class="labelNormalTop labelStyle">
			<s:text name="label.newClaim.internalComments"/>:
		</td>
		<td width="70%" class="labelNormalTop">
			<t:textarea name="task.claim.internalComment" rows="4" cols="50" value=""/>
		</td>
	</tr>
	
</table>
<s:hidden name="mandatedComments[0]" value="externalComments"/>
<s:hidden name="mandatedComments[1]" value="internalComments"/>
<div id="separator"></div>
<jsp:include flush="true" page="../../common/write/validations.jsp"/>
<table width="96%" border="0" cellspacing="0" cellpadding="0" class="grid">
	<tr>
    	<td class="labelNormal"  width="2%">
        <input id="transferRadio" type="radio" name="task.takenTransition" value="Transfer" class="processor_decesion"/>
            <script type="text/javascript">
                dojo.addOnLoad(function(){
                   dojo.connect(dojo.byId("transferRadio"), "onclick", function(event) {
				        if(event.target.checked){
					        dojo.byId("validateButton").disabled=true;
				        }
	   		        });
                });
            </script>
        </td>
        <td class="labelStyle" >
            <s:text name="label.newClaim.transferTo"/>&nbsp&nbsp&nbsp&nbsp
            <s:select id="serviceManagers"
                list="districtServiceManagers" disabled="true"
                name="task.transferTo"  headerKey="" headerValue="%{getText('label.common.selectHeader')}"/>
            <script type="text/javascript">
                 dojo.addOnLoad(function(){
                   dojo.connect(dijit.byId("serviceManagers"), "onChange", function() {
                        if(dijit.byId("serviceManagers").getValue()=="--Select--"){
					        dojo.byId("validateButton").disabled=true;
				        } else {
					        dojo.byId("validateButton").disabled=false;
                        } 
                         
                       }); 
                });  
            </script>
            
            
            
        </td>
    </tr>
    <tr>
        <td width="2%">
            <input type="radio" name="task.takenTransition" value="Reject" class="processor_decesion" id="denyRadio"/>
        </td>
        <td class="labelStyle"><s:text name="button.common.reject"/></td>
    </tr>
    <tr>
        <td width="2%">
            <input type="radio" name="task.takenTransition" value="Accept" class="processor_decesion" id="acceptRadio"/>
        </td>
        <td class="labelStyle"><s:text name="label.common.accept"/></td>
    </tr>
</table>    
<table width="96%" border="0" cellspacing="0" cellpadding="0" align="center" class="buttons">
    <tr>
        <td align="center">
          <s:if test="task.claim.type.type == 'Campaign'">
	       		<s:submit value="%{getText('button.common.validate')}" type="input" action="campaign_smr_claim_validate" id="validateButton"/>
          </s:if>
          <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
                <s:submit value="%{getText('button.common.validate')}" type="input" action="smr_claim_validate" id="validateButton"/>
          </s:elseif>
          <s:else>
          	    <s:submit value="%{getText('button.common.validate')}" type="input" action="parts_smr_claim_validate" id="validateButton"/>
          </s:else>
          <s:hidden name="task.claim.decision" id="task.claim.decision"/>
        </td>
    </tr>
</table>