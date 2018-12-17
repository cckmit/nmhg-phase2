<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<html>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="common.css"/>
  </head>
  <u:body>
	 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  		<tr>
    		<td width="49%" valign="top">
    			<table width="100%" border="0" cellspacing="0" cellpadding="0">
      				<div >
						<div class="section_header">
							<s:text name="accordion_jsp.warrantyAdmin.fleetManagement" />
						</div>
					</div>
					<tr>
      					<td>
						</td>
					</tr>
					<tr>
      					<td>
						</td>
					</tr>
					<tr>
      					<td>
						</td>
					</tr>	
      				<tr>
      					<td>
							<u:openTab decendentOf="%{getText('title.fleetmanagement.managewarrantycoverage')}" id="fleet_coverage_management" tagType="a"
					        	tabLabel="%{getText('title.fleetmanagement.managewarrantycoverage')}" url="select_inventories_fleetcoverage.action" 
					        	catagory="inventory">
					        	<s:text name="title.fleetmanagement.managewarrantycoverage" />
					        </u:openTab>
		     			</td>
		     		</tr>
		     		<tr>
      					<td>
							<u:openTab decendentOf="%{getText('title.fleetmanagement.goodWillExtension')}" id="fleet_goodwill_extension" tagType="a"
					        	tabLabel="%{getText('title.fleetmanagement.goodWillExtension')}" url="select_plan_fleetGWextension.action" 
					        	catagory="inventory">
					        	<s:text name="title.fleetmanagement.goodWillExtension" />
					        </u:openTab>
		     			</td>
		     		</tr>
		     		<tr>
		     		    <td>
		     		        <u:openTab decendentOf="%{getText('title.fleetmanagement.inventroyscrap')}" id="fleet_inventroy_scrap" tagType="a"
					        	tabLabel="%{getText('title.fleetmanagement.inventroyscrap')}" url="select_inventories_fleetscrap.action" 
					        	catagory="inventory">
					        	<s:text name="title.fleetmanagement.inventroyscrap" />
					        </u:openTab>
		     		    </td>
		     		</tr>
      			</table>
      		</td>
      	</tr>
      </table>
  </u:body>
</html>
