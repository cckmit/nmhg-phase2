<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 10, 2008
  Time: 2:42:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@taglib prefix="tda" uri="twmsDomainAware"%><head>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
</head>
<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
</script>
    <s:if test="customReport.id!=null">
        <s:hidden name="customReport" value="%{customReport.id}"/>
    </s:if>
    <s:if test="existingProdsForReport != null">
    	<s:hidden name="overWriteReports" value="true"/>
    </s:if>
    <s:iterator value="customReport.sections" status="sections">
        <s:hidden name="customReport.sections[%{#sections.index}]" value="%{id}"/>
    </s:iterator>
    <s:hidden name="customReport.published" id="customReportPublish"/>
    <s:hidden name="section.name" id="section_name_hid"/>
    <s:hidden name="section.order" id="section_order_hid"/>
    <s:hidden name="taskName" id="task_Name"/>
    
<table width="100%" border="0" cellpadding="0" cellspacing="0"> 	
        <tbody>
          
            <tr>
                <td width="15%" class="labelStyle" nowrap="nowrap">
                    <s:text name="label.customReport.reportName"/>
                </td>
                <td width="25%">
                	 <s:if test="%{publishButtontoBeDisplayed()}">
                    	<s:textfield name="customReport.name" value="%{customReport.name}" id="customReportName" />
                    </s:if>
                    <s:else>
                    	<s:textfield name="customReport.name" value="%{customReport.name}" id="customReportName" readonly="true"/>
                    </s:else>
                </td>
                <td class="labelStyle" nowrap="nowrap" width="15%">
                    <s:text name="label.lov.reportType"/>:
                </td>
                <td width="45%">
               	 <s:if test="%{publishButtontoBeDisplayed()}">
                 	<tda:lov id="id" name="customReport.reportType" className="ReportType" />
                </s:if>
                <s:else>
	                <s:textfield value="%{customReport.reportType}" readonly="true" name="customReport.reportType"/>
	            </s:else>
                </td>
            </tr>
        </tbody>
    </table>
      <hr/>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
                <tbody>
                    <tr>
                         <td  class="labelStyle" nowrap="nowrap" width="15%">
                            <s:text name="label.common.inventoryType"/>:
                        </td>
                        <td>
                            <s:checkboxlist list="inventoryTypes" listKey="type"
                            	listValue="type" name="customReport.forInventoryTypes" value="%{customReport.forInventoryTypes.{type}}"/>
                        </td>
                    </tr>
                </tbody>
            </table>
