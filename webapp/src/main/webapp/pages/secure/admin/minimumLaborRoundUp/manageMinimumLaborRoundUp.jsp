<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	 <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />   
	</head>
	 
	<u:body>
	    <u:actionResults/>
	    <s:form action="saveMinimumLaborRoundUp">
		    <div class="policy_section_div">
					<div id="minimum_labour_header" class="section_header" >
			                 <s:text name="title.MinimumLaborRoundUp"/>
			             </div>
					<table class="grid" width="100%"  border="0" cellspacing="0" cellpadding="0">
				   		 <tr>
			                 <td class="labelStyle" width="22%"><s:text name="label.manageMLR.daysBetweenRepair" />:</td>
							 <td> <s:textfield  name="minimumLaborRoundUp.daysBetweenRepair" /></td>
						</tr>
			             <tr>
			                 <td class="labelStyle"><s:text name="label.manageMLR.roundUpHours" />:</td>
							 <td> <s:textfield name="minimumLaborRoundUp.roundUpHours"  /></td>
						</tr>
			             <tr>
			                 <td class="labelStyle"><s:text name="label.manageMLR.applicableToCommercialPolicy" />:</td>
	                         <td> <s:select list="#{'false' : 'No','true' : 'Yes'}" name="minimumLaborRoundUp.applCommericalPolicy" 
	                         ></s:select>
	                         </td>					
						</tr>
			            
			             <tr>
			                 <td class="labelStyle"><s:text name="label.manageMLR.applicableToClaimTypes" />:</td>
	                         <td>
	                            <s:select theme="simple" list="getClaimTypesForDisplay()" multiple="true" name="selectedClaimTypes"
		                                 id="claimType"  />
		                            <s:if test="minimumLaborRoundUp != null"> 		                                                  
								         <script type="text/javascript">		                         
									  	    dojo.addOnLoad(function() {						  	    										  	    	   	
											  	var machineClaim = '<s:property value="minimumLaborRoundUp.applMachineClaim"/>';											  												  	
											  	var partsClaim = '<s:property value="minimumLaborRoundUp.applPartsClaim"/>';
											  	var campaignClaim = '<s:property value="minimumLaborRoundUp.applCampaignClaim"/>';								  
											  	if(machineClaim == 'true')									  				  		
											  		dojo.byId("claimType").options[0].selected=true;											  	
											  	if(partsClaim == 'true')											  				  		
										  			dojo.byId("claimType").options[1].selected=true;											  	
											  	if(campaignClaim == 'true')											  		  		
									  				dojo.byId("claimType").options[2].selected=true;											  					  				  	
									  	    });			  	
									  </script>	
		                           </s:if>	
		                           <s:else>
		                               <script type="text/javascript">		                         
									  	    dojo.addOnLoad(function() {	
									  	    	dojo.byId("claimType").options[0].selected=false;	
									  	    	dojo.byId("claimType").options[1].selected=false;	
									  	    	dojo.byId("claimType").options[2].selected=false;	
									  	  });			  	
									  </script>	
		                           </s:else>		                                           
	                         </td>					
						</tr>
			            
				      </table>
				      <div class="spacer10"></div>
				      <u:repeatTable id="additional_labour_table" cssClass="grid borderForTable" width="35%">
						    <thead>
						       <tr class="row_head">
							       <th><s:text name="label.manageMLR.specifyProducts"/></th>
							        <th width="9%">
							           <u:repeatAdd id="outside_parts_adder"><div align="center"><div class="repeat_add"/></u:repeatAdd></div></th>
						       </tr>
						    </thead>
	    
	                        <u:repeatTemplate id="outside_parts_replaced_body"  value="minimumLaborRoundUp.applicableProducts">
	                         <script type="text/javascript">
							        dojo.addOnLoad(function() {
							           	dojo.byId("productAutoComplete_#index").value = '<s:property value="minimumLaborRoundUp.applicableProducts[#index].name"/>';
							        });
       						 </script>
	                          <tr index="#index">
	        					<td width="25%">
			                           <sd:autocompleter id='productAutoComplete_#index' href='list_products_name_value_id.action' name='minimumLaborRoundUp.applicableProducts[#index]' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' delay='500' />
		                          
		                           <img style="display: none;" id="indicator" class="indicator"
		                             src="image/indicator.gif" alt="Loading..."/>
	                            </td>
	            
	                           <td width="10%">            
					                <u:repeatDelete id="outside_parts_deleter_#index">
					                    <div align="center"><div class="repeat_del"/></div>
					                </u:repeatDelete>
	                           </td>
	                         </tr>
	                        </u:repeatTemplate>
	                     </u:repeatTable>
	                     	<div class="submit_space" style="width:35%; margin-top:10px;">
		                 		<div id="submit" align="center">
						       		<input id="submit_btn" class="buttonGeneric" type="submit"
									value="<s:text name='button.common.save'/>" />
							   		<input id="cancel_btn" class="buttonGeneric" type="button" 
							        value="<s:text name='button.common.cancel'/>"
									onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />        
			            	</div>    
		                	</div>
				</s:form>
			</div>
	</u:body>
</html>
