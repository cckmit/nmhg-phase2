<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

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
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-y:auto;" id="root">
   	<div class="policy_section_div">
	   <div class="section_header">        
	      <s:text name="label.manageInclusiveJobCodes.manageInclusiveJobCodes"/>
	   </div>    
       <table cellspacing="0" cellpadding="0" class="grid" >
          <tr>	      
	         <td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="columnTitle.manageInclusiveJobCodes.parentJobCode" />:</td>
      	 	 <td><s:property value="serviceProcedureDefinition.code"/></td>
      	 	  <td><s:property value="serviceProcedureDefinition.getServiceProcDefinitionDesc()"/></td>
      	  </tr>
       </table>	  
       <table cellspacing="0" cellpadding="0" class="grid" >	   
      	   <tr class="row_head">
      	      <th width="30%"><s:text name="label.common.jobCode"/></th>                         
              <th width="20%"><s:text name="label.customReport.description"/></th> 
           </tr> 
         <s:iterator value="serviceProcedureDefinition.childJobs" status="currIter">        
           <tr>         
              <td><s:property value="code"/></td>                             
              <td><s:property value="getServiceProcDefinitionDesc()"/>	</td>                       
            </tr> 
         </s:iterator>     	 
       </table>
    </div>
  </div>     
</u:body>
</html>