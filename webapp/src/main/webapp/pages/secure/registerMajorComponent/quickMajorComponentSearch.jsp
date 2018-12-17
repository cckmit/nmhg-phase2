<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
 </head>  
  <script type="text/javascript">  	  	
      	dojo.require("dijit.layout.LayoutContainer"); 
  </script>
<body>
<u:actionResults/>
 <div id="separator"></div>   
	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">	
	<form action="viewMajorCompHistoryForQuickSearch.action" method="POST" >
	<div style="margin:5px;" class="policy_section_div">
	<div  class="section_header">        
       	<s:text name="label.majorComponent.quickMajorComponentSearch"/>
    </div>	
		<table   border="0" cellspacing="0" cellpadding="0" class="grid">
			<tr>
			 	 <td  class="labelStyle" width="1%" nowrap="nowrap">&nbsp;<s:text name="label.majorComponent.componentSerialNo" />:</td>
       	 	 	 <td class="searchLabel">
       	 	 	 	<s:textfield name="serialNumber" id="serialNumber" cssStyle="border:1px solid #AAAAAA"/>
       	 	 	 </td>				
			</tr>					
		</table>
		<div align="center" style="margin-top:10px;">
		  <s:submit cssClass="buttonGeneric" name="button.common.submit" />&nbsp;
		</div>
	</div>
	</form>
	</div>	
</body>
</html>     	