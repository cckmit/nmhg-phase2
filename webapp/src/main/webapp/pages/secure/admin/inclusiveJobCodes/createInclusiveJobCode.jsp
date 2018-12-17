<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />    
</head>   
  
  <script type="text/javascript">
			dojo.require("dojox.layout.ContentPane");
			dojo.require("dijit.layout.ContentPane");
            dojo.require("dijit.layout.LayoutContainer");        
  </script>       

<u:body>
 <s:form action="saveInclusiveJobCodes.action" theme="twms" validate="true" method="POST">
   <s:hidden name="createInclusiveJobCode" value="%{createInclusiveJobCode}"/>
	<div class="policy_section_div">
	   <div class="section_header">        
	      <s:text name="label.manageInclusiveJobCodes.manageInclusiveJobCodes"/>
	   </div>
     <u:actionResults/>
       <table cellspacing="0" cellpadding="0" class="grid" >
	      <tr>	      
	         <td class="labelStyle" width="15%" nowrap="nowrap"><s:text name="columnTitle.manageInclusiveJobCodes.parentJobCode" />:</td>
      	 	 <td>
      	 	      <s:hidden name="serviceProcedureDefinition" value="%{serviceProcedureDefinition.id}" id="serProDefHidden"/>      	 	    
      	 	     <s:if test="isCreateInclusiveJobCode()">       	 	             	 	         	 	    
      	 	       <sd:autocompleter id='ParentJobCode' href='list_job_codes.action' name='serviceProcedureDefinition' value='%{serviceProcedureDefinition.code}' loadOnTextChange='true' showDownArrow='false' loadMinimumCount='2' notifyTopics='serviceProcedureDefinitionChanged/setcode' />	
							      
							   <script type="text/javascript">
							      dojo.addOnLoad(function() {
                                        
							    	dojo.subscribe("serviceProcedureDefinitionChanged/setcode", null, function(data, type, request) {
			                             var selectedParentJobCodeId =  dijit.byId("ParentJobCode").getValue();			                            
                                         if(selectedParentJobCodeId != data){
                                             dojo.byId("serProDefHidden").value = dijit.byId("ParentJobCode").getValue();
                                          }
			                            });
			                            dojo.connect(dijit.byId("ParentJobCode"), "onChange", function() {  
			                            	twms.ajax.fireHtmlRequest("get_description_for_serProcDef.action", {"serviceProcedureDefinition.id": dojo.byId("serProDefHidden").value}, function(data) {  
			                                	  dijit.byId("desc_for_serProcDef").setContent(eval(data)[0]);     		   		  	 
			                            	  });			                            	       	              
			            		        });			                           	                            
			                        });    

	          			      </script>
	          	     
	          	  </s:if>
	          	  <s:else>
	          	     <s:property value="serviceProcedureDefinition.code"/>
	          	  </s:else> 	          	  
	          </td>	  		          			
			  <td>
			      <s:if test="isCreateInclusiveJobCode()">  
			        <div dojoType="dojox.layout.ContentPane" id="desc_for_serProcDef" style="width: 100%; height:100%;"></div>
			      </s:if>
			      <s:else> 
			        <s:property value="serviceProcedureDefinition.getServiceProcDefinitionDesc()"/>	
			      </s:else>		     		  	 				 	  	     	    
			  </td>
		  </tr>
		  <s:if test="!isParentJobCodeExist()">
		  <tr>
		    <u:repeatTable id="myTable" cssClass="grid borderForTable" cellpadding="0"
                             cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
              <thead>
                      <tr class="row_head">
                          <th width="15%"><s:text name="label.common.jobCode"/></th>                         
                          <th width="35%"><s:text name="label.customReport.description"/></th>
                          <th width="9%" >
                             <u:repeatAdd id="adder1" theme="twms">
                                <div align="center"><img src="image/addRow_new.gif" name="addPrice" border="0" align="absmiddle" id="addPrice" style="cursor: pointer;" 
                                    title="<s:text name="label.manageInclusiveJobCodes.addJobCode" />" />                             </div>
                             </u:repeatAdd>
                          </th>
                       </tr>
              </thead>
              <u:repeatTemplate id="mybody" value="serviceProcedureDefinition.childJobs" index="myindex" theme="twms" >
                     <tr index="#myindex" style="border: 1px solid #d6dee1">
                          <td width="15%" style="border-left:1px solid #d6dee1;">                          
                          
      	 	                  <s:hidden name="serviceProcedureDefinition.childJobs[#myindex]" value="%{id}" id="selectedserProDefHidden_#myindex"/>                            
                              <sd:autocompleter id='childJobCode_#myindex' href='list_job_codes.action' name='serviceProcedureDefinition.childJobs' keyName='serviceProcedureDefinition.childJobs[#myindex]' value='%{serviceProcedureDefinition.childJobs[#myindex].code}' loadOnTextChange='true' showDownArrow='false' loadMinimumCount='2' listenTopics='/forSerProcDef/initial/#myindex' notifyTopics='childJobCodeChanged/setcode/#myindex' />
							      
							      <script type="text/javascript">
							      dojo.addOnLoad(function() {
								         dojo.subscribe("childJobCodeChanged/setcode/#myindex", null, function(data, type, request) {
			                             var childJobCode = dijit.byId("childJobCode_#myindex").getValue();			                            		                            
                                         if(childJobCode != data){
                                             dojo.byId("selectedserProDefHidden_#myindex").value = dijit.byId("childJobCode_#myindex").getValue();
                                          }
			                            });
			                            dojo.connect(dijit.byId("childJobCode_#myindex"), "onChange", function() {  
			                            	twms.ajax.fireHtmlRequest("get_description_for_serProcDef.action", {"serviceProcedureDefinition.id": dojo.byId("selectedserProDefHidden_#myindex").value}, function(data) {  
			                                	  dijit.byId("desc_for_serProcDef_#myindex").setContent(eval(data)[0]);     		   		  	 
			                            	  });       	              
			            		        });
			                            
			                        });    

	          			          </script>
							  
							              		 
                           </td>                             
                           <td>                           
                                 <div dojoType="dojox.layout.ContentPane" id="desc_for_serProcDef_#myindex" style="width: 100%; height:100%;"></div>			  	   	         				 	  	     	    
				           </td>                        
                           <td align="center" width="9%">
                                 <u:repeatDelete id="myTableDeleter_#myindex" theme="twms">
								 <img id="delete" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.manageInclusiveJobCodes.deleteJobCode" />"/>
								 </u:repeatDelete>
                            </td>
                       </tr>                       
                      </u:repeatTemplate>                      
                 </u:repeatTable>
                </tr>               
                <div class="spacer3"></div>
                 <div id="submit" align="center">
				       <input id="submit_btn" class="buttonGeneric" type="submit"
							value="<s:text name='button.common.save'/>" />
					   <input id="cancel_btn" class="buttonGeneric" type="button" 
					        value="<s:text name='button.common.cancel'/>"
							onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />        
	            </div>    
                           <div class="spacer7"></div>

              </s:if>
               
       </table>	 	  
   </div>
 </s:form>             
</u:body>