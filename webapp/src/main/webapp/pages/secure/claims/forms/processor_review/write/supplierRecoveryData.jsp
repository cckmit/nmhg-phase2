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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tda" uri="twmsDomainAware"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
  response.addHeader( "Cache-Control", "must-revalidate" );
  response.addHeader( "Cache-Control", "no-cache" );
  response.addHeader( "Cache-Control", "no-store" );
  response.setDateHeader("Expires", 0); 
%>
 <script type="text/javascript">
   
        dojo.require("twms.data.EmptyFileWriteStore");
 </script>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js">
</script>

<div class="policy_section_div" style="width:100%">
<div class="section_header" >
	<s:text name="title.common.supplier.recoveryInfo"></s:text>
</div>

	    	<table  width="96%" class="grid borderForTable" cellspacing="0" cellpadding="0" align="center" style="margin:5px;">
	    		<thead>
	    			
		    		<tr class="row_head">
		    			<th width="15%">
				             <s:text name="columnTitle.duePartsInspection.causalpart"></s:text>
				        </th>
				        <th width="25%">
				           <s:text name="label.recovery.contract"></s:text>
				        </th>
				        <th width="25%">
				              <s:text name="columnTitle.listContracts.contract_name"></s:text>
				        </th>
				        <th width="15%">
				        	<s:text name="columnTitle.listContracts.supplier_name"></s:text>
				        </th>
				        <th width="15%">
				        	<s:text name="label.supplierNumber"></s:text>
				        </th>
				        <th width="5%">
				            <s:text name="label.common.recoverable"></s:text>
				        </th>
		    		</tr>
	    		</thead>
	    		<tbody>
	    			<tr>
	    				<td width="15%">
	    				<table>
	    				<tr>
	    				<td style="border:none"><span id="ContractCausalPart"/></td>
	    				<td style="border:none">
	    				<authz:ifProcessor>          
			   	                <s:if test="task.claim != null && task.claim.claimNumber !=null && task.claim.serviceInformation.oemDealerCausalPart !=null">
			   	                <img  src="image/comments.gif" id= "oem_dealer_causal_part"
			   			            		title="<s:property value="task.claim.serviceInformation.oemDealerCausalPart.number" />" 
			   			            		alt="<s:property value="task.claim.serviceInformation.oemDealerCausalPart.number" />"/>
			   			    		  		
			   			        </s:if>   
			   			</authz:ifProcessor>
	    				</td>
	    				</tr>
	    				</table>				       
		             	</td>
	    				<td width="25%">
	    				 <script type="text/javascript">
	    				 	 dojo.addOnLoad(function() {
					 	  		var itemNumber;
						        itemNumber='<s:property value="task.claim.serviceInformation.causalPart"/>';
								'<s:set name="causalPartNumber" value="' + itemNumber + '"/>'
									dojo.byId("ContractCausalPart").innerHTML= itemNumber;
								});
						</script>
	    				<sd:autocompleter name='selectedContract' href='list_claim_contracts.action?number=%{task.claim.id}' key='%{task.claim.serviceInformation.contract.id}' value='%{task.claim.serviceInformation.contract.name}' loadMinimumCount='0' listenTopics='/causalPart/changed/queryParams' indicator='contract_indicator' id='contractId' />	
						 <img style="display: none;" id="contract_indicator" class="indicator"
                    		 src="image/indicator.gif" alt="Loading..."/>
					</td>
					
					<td width="25%">
					 <s:a cssStyle="cursor:pointer">				
    	   			<span id="contractLink"/>
    					</s:a>
               		 </td>
               		 <td width="15%">
               		 	<span id="supplierName"/>
               		 	<s:property value="task.claim.serviceInformation.contract.supplier.name"/>
               		 </td>
               		 <td width="15%">
               		 	<span id="supplierNumber"/>
               		 	<s:property value="task.claim.serviceInformation.contract.supplier.supplierNumber"/>
               		 </td>
	    				<td width="5%" align="center">
	    					<s:checkbox id="supplierPartRecoverableId" name="task.claim.serviceInformation.supplierPartRecoverable" />
	    				</td>
	    			</tr>
	    		</tbody>
	    				
	    	</table>
	    
 </div>
 <script type="text/javascript">
 var causalPart;
	 dojo.addOnLoad(function() {
	     var contractIdSelect = dijit.byId("contractId");
	     contractIdSelect.defaultSelectFirstValue = true;
	     contractIdSelect.disableIfNoResult = true;
	});
 	dojo.addOnLoad(function(){
	 	 dojo.connect(dijit.byId("contractId"), "onChange" , function(newValue) {
	 	 	 populateContract(newValue);
	      });
	      });
                    
      dojo.connect(dojo.byId("contractLink"),"onclick",function(data){
          var contract = dijit.byId("contractId");
          var contractId =contract.getValue();
          var contractName= contract.getDisplayedValue();
          var url = "contract_view.action";
          var tabLabel = "Contract";
          var decendentOfTab = "Contract";
          if (contractId) {
              url += "?id="+contractId;
              tabLabel += " " + contractName;
          }
          parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : decendentOfTab});
          delete url, tabLabel;
      });
    dojo.addOnLoad(function(){
    	var causalPart = dijit.byId("causalPart");
    	var claimType = "<s:property value='task.claim.type.type'/>";
    	var repairDate = dijit.byId("claimRepairDate");
		var purchaseDate = dijit.byId("purchaseDate"); 
		var installationDate = dijit.byId("installationDate");
    	if(claimType =='Campaign'){
    		dojo.publish("/causalPart/changed/queryParams", [{
                    url: "list_claim_contracts.action",
                    params: {
                        searchPrefix: "",
                        claimRepairDate: dojo.byId("claimRepairDate").value,
                        number: <s:property value='task.claim.id'/>
                    },
                    makeLocal: true
                  }]);
                  dojo.byId("ContractCausalPart").innerHTML= '-';
                  resetContract();
	    	}else{
	        dojo.connect(causalPart, "onChange", function(newValue) {
	        	dojo.publish("/causalPart/changed/queryParams", [{
                    url: "list_claim_contracts.action",
                    params: {
                        searchPrefix: "",
                        number: <s:property value='task.claim.id'/>,
                        claimRepairDate: dojo.byId("claimRepairDate").value,                        
                        causalPart: newValue 
                    },
                    makeLocal: true
                  }]);
                   dojo.byId("ContractCausalPart").innerHTML= newValue;
                   resetContract();
                   causalPart=newValue; 
                   
                });
               }

			dojo.connect(repairDate, "onChange", function(newValue) {
	            var causalPart = dijit.byId("causalPart");
	        	dojo.publish("/causalPart/changed/queryParams", [{
                    url: "list_claim_contracts.action",
                    params: {
                        searchPrefix: "",
                        number: <s:property value='task.claim.id'/>,
                        claimRepairDate: dojo.byId("claimRepairDate").value,
                        causalPart:causalPart.getValue()  
                    },
                    makeLocal: true
                  }]);
                  resetContract();
                });
                 

	        if(purchaseDate){
		        dojo.connect(purchaseDate, "onChange", function(newValue) {
		            var causalPart = dijit.byId("causalPart");
		        	dojo.publish("/causalPart/changed/queryParams", [{
	                    url: "list_claim_contracts.action",
	                    params: {
	                        searchPrefix: "",
	                        number: <s:property value='task.claim.id'/>,
	                        purchaseDate: dojo.byId("purchaseDate").value,
	                        claimRepairDate: dojo.byId("claimRepairDate").value,
	                        causalPart:causalPart.getValue()  
	                    },
	                    makeLocal: true
	                  }]);
	                  resetContract();
	                });
               	}   
               	                        

			if(installationDate){
		        dojo.connect(installationDate, "onChange", function(newValue) {
		            var causalPart = dijit.byId("causalPart");
		        	dojo.publish("/causalPart/changed/queryParams", [{
	                    url: "list_claim_contracts.action",
	                    params: {
	                        searchPrefix: "",
	                        number: <s:property value='task.claim.id'/>,
	                        installationDate: dojo.byId("installationDate").value,
	                        claimRepairDate: dojo.byId("claimRepairDate").value,
	                        causalPart:causalPart.getValue()  
	                    },
	                    makeLocal: true
	                  }]);
	                  resetContract();
	                });
                }               	
	        });
    
    function resetContract(){
		dojo.byId("contractLink").innerHTML="";
		dojo.byId("supplierName").innerHTML = "";
		dojo.byId("supplierNumber").innerHTML = "";    
		var contractIdSelect = dijit.byId("contractId");
	    contractIdSelect.defaultSelectFirstValue = true; 
	    contractIdSelect.disableIfNoResult = true;
    }

	function populateContract(name) {
		dojo.byId("contractLink").innerHTML=dijit.byId("contractId").getDisplayedValue();
		dojo.byId("supplierName").innerHTML = "";
		dojo.byId("supplierNumber").innerHTML = "";
		
		var causalPart = dijit.byId("causalPart");
		twms.ajax.fireHtmlRequest("get_contractDetails.action", {
                    contractId: dijit.byId("contractId").getValue()
                    }, function(details) {
                    		var contractInfo = eval(details)[0];
                    		var isCollateral = contractInfo["collateral"];
							var isPhysicalshipment=contractInfo["physicalShipment"];
							var count = '<s:property value="%{task.claim.serviceInformation.serviceDetail.oemPartsReplaced.size}"/>';
							if(isPhysicalshipment ){
							for(i =0 ; i < count ; i++)
							{
								var item= dijit.byId("");
								if(isCollateral  || causalPart == dijit.byId("oemPart_" + i +"_itemNo").getDisplayedValue())
								{
									var check=dojo.byId("oemPart_" + i + "_toBeReturned");
									check.checked=true;
									alterValue(i);
								}
							}
						}
						dojo.byId("supplierName").innerHTML = contractInfo["supplierName"];
						dojo.byId("supplierNumber").innerHTML = contractInfo["supplierNumber"];
						delete contractInfo;
					});
   }
</script>
