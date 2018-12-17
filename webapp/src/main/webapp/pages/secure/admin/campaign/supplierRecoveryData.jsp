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
<div class="admin_section_div">
<div class="admin_section_heading"><s:text name="title.common.supplier.recoveryInfo"/></div>
<table cellspacing="0" cellpadding="0" width="100%">
    <tr>
    	<td width="100%">
	    	<table class="grid borderForTable" width="100%" cellspacing="0" cellpadding="0">
	    		<thead>
		    		<tr class="row_head">
				        <th width="25%">
				           <s:text name="label.recovery.contract"></s:text>
				        </th>
				        <th width="25%">
				           <s:text name="columnTitle.listContracts.contract_name"></s:text>
				        </th>
				        <th width="25%">
				        	<s:text name="columnTitle.listContracts.supplier_name"></s:text>
				        </th>
				        <th width="25%">
				        	<s:text name="label.supplierNumber"></s:text>
				        </th>
		    		</tr>
	    		</thead>
	    		<tbody>
	    			<tr>
	    				<td>
		    				<sd:autocompleter name='selectedContract' size='5' href='list_all_contracts.action?contractName=%{searchString}' value='%{campaign.contract}' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' indicator='contract_indicator' id='contractId' emptyOption='true' />	
								 <img style="display:none;" id="contract_indicator" class="indicator"
	                    		 src="image/indicator.gif" alt="Loading..."/>
						</td>
						<script type="text/javascript">
			               dojo.addOnLoad(function() {
			               		var name = '<s:property value="campaign.contract.name"/>';
			               		if(name != ''){    
									dijit.byId("contractId").setDisplayedValue(name);
								}
							});                                                           
			           </script>		
						
						<td>
                            <s:a cssStyle="cursor:pointer" href="#"><span id="contractLink"/></s:a>
						</td>
	               		<td>
	               			<span id="supplierName"/>
	               		 	<s:property value="campaign.contract.supplier.name"/>
	               		</td>
               		 	<td>
               		 		<span id="supplierNumber"/><s:property value="campaign.contract.supplier.supplierNumber"/>
               		 	</td>
	    			</tr>
	    		</tbody>
	    	</table>
	    </td>	
    </tr>
 </table>   
 </div>
 <script type="text/javascript">
 	dojo.addOnLoad(function(){
	 dojo.connect(dijit.byId("contractId"), "onChange" , function() {
	 	 	 populateContract();
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
      
	function populateContract() {
		dojo.byId("contractLink").innerHTML = "";
		dojo.byId("supplierName").innerHTML = "";
		dojo.byId("supplierNumber").innerHTML = "";
		twms.ajax.fireHtmlRequest("get_contractDetails.action", {
                    contractId: dijit.byId("contractId").getValue()
                    }, function(details) {
                    	var contractInfo = eval(details)[0];
                    	dojo.byId("contractLink").innerHTML = contractInfo["contractName"];
						dojo.byId("supplierName").innerHTML = contractInfo["supplierName"];
						dojo.byId("supplierNumber").innerHTML = contractInfo["supplierNumber"];
						delete contractInfo;
					});
   }
</script>
