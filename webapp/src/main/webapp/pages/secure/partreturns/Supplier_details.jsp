<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("twms.widget.TitlePane");
    </script>

<div dojoType="twms.widget.TitlePane" title="Supplier Details"
				labelNodeClass="sectionTitle">
       <table id="supplier_items_table" class="grid borderForTable" cellspacing="0"
               cellpadding="0" style="width:90%">
            <thead>
                <tr class="row_head">
                    <th width="14%"><s:text name="label.common.supplierName" /></th>
                    <th width="14%"><s:text name="label.common.supplierNumber" /></th>
                    <th width="18%"><s:text name="columnTitle.common.address" /></th>
                </tr>
            </thead>
            <tbody>
                <!-- Iterator over all supplier addresses -->
                   <tr>
                        <td><s:property value="contract.supplier.name"/> </td>
                        <td> <s:property value="contract.supplier.supplierNumber"/> </td>
                        <td><s:property value="contract.supplier.displayAddress"/>  </td>
                   </tr>
            </tbody>

            </table>
           <table id="supplier_item_location_table" class="grid borderForTable" cellspacing="0"
               cellpadding="0" style="width:90%">
            <thead>
                <tr class="row_head">
                    <th width="25%"><s:text name="label.sra.partSouce.supPartDescription" /></th>
                    <th width="10%"><s:text name="label.common.suppliedFromDate" /></th>
                    <th width="10%"><s:text name="label.common.suppliedToDate" /></th>
                    <th width="10%"><s:text name="label.common.status" /></th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value = "supplierItemMapping.supplierItemLocations">
                   <tr>
                        <td><s:property value="itemMapping.toItem.description"/></td>
                        <td><s:property value="fromDate"/></td>
                        <td><s:property value="toDate"/></td>
                        <td>
                        	<s:if test = "status">
                        		Active
                        	</s:if>
                        	<s:else>
                        		InActive
                        	</s:else>
                        </td>
                   </tr>
                </s:iterator>
            </tbody>
            </table>

</div>