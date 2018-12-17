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
<%--
  @author mritunjay.kumar
--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<table class="form" cellspacing="0" cellpadding="0" id="servicing_location">
    <tr>
        <td width="22%">
            <label for="servicingLocation"  class="labelStyle"><s:text name="label.viewClaim.servicingLocation"/>:</label>
        </td>
        <td>
        	<s:if test="task.claim.isFoc() || task.claim.thirdPartyClaim">
                <table class="form" cellspacing="0" cellpadding="0" id="servicing_location">
			        <tr>
			            <td>
			            	<s:hidden name="task.claim.servicingLocation" id="thirdPartyServiceLoc" />
			            	<span id="serviceLocationDetail">
				            	<s:if test="task.claim.servicingLocation!=null">
					            	<s:property value="task.claim.servicingLocation.getShipToCodeAppended()"/>
					            	<s:hidden name="task.claim.servicingLocation.location" id="servicing_location_id" />
				            	</s:if>
			            	</span>
			            	<script type="text/javascript" >
			            	dojo.addOnLoad(function(){
			            		var serviceLocation = dojo.byId("serviceLocationDetail");
								serviceLocation.innerHTML = '';
								var params = {};
								params["thirdPartyName"] = dojo.byId("thirdPartyForDealer").value;
								twms.ajax.fireJsonRequest("findServiceProvider.action", params,function(details) {
						            dojo.byId("serviceLocationDetail").innerHTML = details[0];
									dojo.byId("thirdPartyServiceLoc").value = details[1];
						           }
						        );
						    });
							</script>
			            </td>
			        </tr>  
			    </table> 
			</s:if>
			<s:else>
			    <s:property value="populateOrgAddress(task.claim.forDealer)"/>
	            <s:set name="servicingLocations" value="getOrgAddresses()"/>
	            <s:if test="buConfigAMER">
	            <s:select label="label.viewClaim.servicingLocation" 
	                      id="servicing_location_id" cssStyle="width:450px;"
					      name="task.claim.servicingLocation"
					      value="%{task.claim.servicingLocation.id}"
					      listKey="id"
					      listValue="locationWithBrand"
					      list="#servicingLocations"
	                      headerKey="null" headerValue="" onchange="populateTranspotationAndTravel()" />
	             </s:if>
	             <s:else>
	             <s:select label="label.viewClaim.servicingLocation" 
	                      id="servicing_location_id" cssStyle="width:450px;"
					      name="task.claim.servicingLocation"
					      value="%{task.claim.servicingLocation.id}"
					      listKey="id"
					      listValue="getShipToCodeAppended()"
					      list="#servicingLocations"
	                      headerKey="null" headerValue=""/>
	             </s:else>
	                        <!-- Code added to Default Service Location when size is 1 -->
	            			<script type="text/javascript">
							    dojo.addOnLoad(function() {
							    <%--<s:if test="task.claim.servicingLocation==null">
								  		dijit.byId('servicing_location_id').setValue("<s:property value="getPrimaryOrganizationAddressForOrganization(task.claim.forDealer)"/>");							              
					            </s:if>--%>
					            <s:if test="task.claim.servicingLocation !=null">
									dijit.byId('servicing_location_id').setValue("<s:property value="task.claim.servicingLocation.id"/>");
								</s:if>
					            <s:elseif test="getOrgAddresses().size()==1">
					            	dijit.byId('servicing_location_id').setValue("<s:property value="getOrgAddresses().get(0).id"/>");
					            </s:elseif>	
					       
					     /*        var travelLoc=dojo.byId("travel_location").innerHTML;
						           if(travelLoc!='')
						        	   {						        	
						        	   console.debug("bb");
						        	   dijit.byId("servicing_location_id").fireOnLoadOnChange=false;	
						           } */					           
						         /*   dojo.connect(dijit.byId("servicing_location_id"), "onChange", function(){							            	
						            	populateTranspotationAndTravel();
						            });	 */				            
					            
					          });
							    
			   </script>
			 </s:else>
        </td>
    </tr>  
</table>       