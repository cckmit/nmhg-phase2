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
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
  	dojo.addOnLoad(function(){
		var idsPresent=["radioTransfer","reprocessRadio"];
		for(var i=0;i<idsPresent.length;i++){
			dojo.connect(dojo.byId(idsPresent[i]), "onclick", function(event) {
				if(event.target.checked){
					showEnabledFields(event.target.id);
				}
	   		});
   		}
	});
	function showEnabledFields(selectedId){
		var allIds=["radioTransfer"];				
				dijit.byId("processors").setDisabled(true);
				dijit.byId("processors").setDisplayedValue("--Select--");
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='radioTransfer'){
					dijit.byId("processors").setDisabled(false);
				}
	}
</script>
    <tr>
    	<td class="labelNormal">
            <input id="radioTransfer" checked="checked" type="radio" name="multiClaimAction"
                value="multiTransfer" class="processor_decesion"/>   
                <script type="text/javascript">
                dojo.addOnLoad(function(){
                	dijit.byId("processors").setDisabled(false);
                });
                </script>     
        </td>        
        <td class="labelStyle" width="20%" nowrap="nowrap">
            <s:text name="label.newClaim.transferTo"/>
        </td>
        <td class="labelNormalTop" colspan="2">
        	<s:select id="processors"
                list="eligibleProcessors" disabled="true"                          
                name="transferTo"  headerKey="" headerValue="%{getText('label.common.selectHeader')}"/>
        </td>
    </tr>
    <tr>
    	<td class="labelStyle" width="20%" nowrap="nowrap">
            <input type="radio" name="multiClaimAction"
                value="multiReProcess" class="processor_decesion" id="reprocessRadio"/>        
        </td>
        <td class="labelNormal" colspan="3">
            <s:text name="label.newClaim.reprocess"/>
        </td>
        <td></td>
    </tr>
</table>
&nbsp;
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
			<s:submit id="submit"  value="%{getText('button.common.submit')}"     />
		
			<script>
			dojo.addOnLoad(function() {	
				dojo.connect(dojo.byId('submit'), "onclick", function() {
					if(dojo.byId("reprocessRadio").checked){
						actionName = dojo.byId("reprocessRadio").value;
						dojo.byId("selectedClaims").value=selectedClaimsId;
						this.form.action = actionName + ".action";
						dojo.html.hide(dojo.byId("submit")); 
					}
					if(dojo.byId("radioTransfer").checked){
						actionName = dojo.byId("radioTransfer").value;
						dojo.byId("selectedClaims").value=selectedClaimsId;
						this.form.action = actionName + ".action";
						dojo.html.hide(dojo.byId("submit")); 
					}
				});
			});
		</script>
		
		</center>
		</td>
		</tr>
	</table>	
</div>




