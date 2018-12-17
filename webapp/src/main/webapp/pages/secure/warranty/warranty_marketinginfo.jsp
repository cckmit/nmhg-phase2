<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<jsp:include page="/i18N_javascript_vars.jsp"/>

<script type="text/javascript">
    dojo.require("twms.widget.Select");
    var transactionTypes = {};
 </script>

<s:iterator value="listOfTransactionTypes">
    <script type="text/javascript">
        transactionTypes['<s:property value="id"/>'] = '<s:property value="type"/>';
    </script>
</s:iterator>
 <div dojoType="dijit.layout.ContentPane" id="warranty_market_ino">
<div style="width:100%">

<table  style="width:98%;">
<tbody>
<tr>
<td>
<table class="grid" cellpadding="0" cellspacing="0">
<tbody>
<s:if test="getLoggedInUser().isInternalUser() && displayInternalInstallType()">
	<tr>
    	<td class="non_editable labelStyle" width="20%" nowrap="nowrap"><s:text name="label.internalInstallType"/></td>
    	<td>
    		<s:select id="internalInstallType" cssStyle="width:180px;" name="marketingInformation.internalInstallType" 
    		list="listOfInternalInstallTypes" 
    		headerKey="" headerValue="%{getText('label.common.selectHeader')}" 
    		value="%{marketingInformation.internalInstallType.id.toString()}" 
    		listKey="id" listValue="getDisplayInternalInstallType()"/>
    	</td>
	</tr>
</s:if>
<tr>
    <td class="non_editable labelStyle" width="20%" nowrap="nowrap"><s:text name="label.contractCode"/></td>
    <td>
    <s:select id="contractCode" cssStyle="width:180px;" name="marketingInformation.contractCode" 
    list="listOfContractCodes" 
    headerKey="" headerValue="%{getText('label.common.selectHeader')}" 
    value="%{marketingInformation.contractCode.id.toString()}" 
    listKey="id" listValue="getDisplayContractCode()"/>
    </td>
    </tr>
     <tr>
       <td class="non_editable labelStyle" width="20%" nowrap="nowrap"><s:text name="label.maintenanceContract"/></td>
    <td>
    <s:select id="maintenanceContract" cssStyle="width:180px;"
              name="marketingInformation.maintenanceContract"
              list="listofMaintenanceContracts"
              headerKey="" headerValue="%{getText('label.common.selectHeader')}"
              value="%{marketingInformation.maintenanceContract.id.toString()}"
              listKey="id" listValue="getDisplayMaintenanceContract()"/>
    </td>
    
    </tr>
    </tbody>
    </table>
    </td>
    <td>
    <table class="grid" cellpadding="0" cellspacing="0">
    <tbody>

           <tr>
           
	<s:if test="buConfigAMER">
    <td class="non_editable labelStyle" nowrap="nowrap"><s:text name="label.ulClassification" />:</td>
    
     <td width="30%" style="padding-left:5;">
         <s:textfield name="marketingInformation.ulClassification"  cssStyle="width:145px;" id="ulClassification" theme="simple" />
      </td>
      </s:if>
      </tr>
            <tr>
      <td class="non_editable labelStyle" nowrap="nowrap"><s:text name="label.dealerUser.salesPerson" />:</td>
    
     <td width="30%" style="padding-left:5;">
         <s:textfield name="marketingInformation.dealerRepresentative"  cssStyle="width:145px;" id="salesMan" theme="simple" />
      </td>
      </tr>
       <tr>
       <td class="non_editable labelStyle" nowrap="nowrap"><s:text name="Customer Representative" />:</td>
    
     <td width="30%" style="padding-left:5;">
         <s:textfield name="marketingInformation.customerRepresentative"  cssStyle="width:145px;" id="customerRepresentative" theme="simple" />
      </td>
      </tr>
      

   
	<!--<td width="100%">
		 <s:select id="salesMan" name="marketingInformation.salesMan"
		list="listOfSalesPersons" listKey="id" listValue="%{getCompleteNameAndLogin()}"
		headerKey="null" headerValue="%{getText('label.common.selectHeader')}" value="%{marketingInformation.salesMan.id.toString()}" />
	</td> -->
 
    <tr>
    <td class="non_editable labelStyle" nowrap="nowrap"><s:text name="label.industryCode"/></td>
    <td>
    <s:select id="industryCode" cssStyle="width:180px;"
              name="marketingInformation.industryCode"
              list="listOfIndustryCodes"
              headerKey="-1" headerValue="%{getText('label.common.selectHeader')}"
              value="%{marketingInformation.industryCode.id.toString()}"
              listKey="id" listValue="getDisplayIndustryCode()"/>
    </td>
    </tr>
    </tbody>
    </table>
    </td>
    </tr>
    </tbody>
    </table>
</div>
</div>