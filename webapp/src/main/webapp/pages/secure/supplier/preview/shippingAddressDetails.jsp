<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>
<div dojoType="twms.widget.TitlePane" title="Supplier Details"
				labelNodeClass="sectionTitle">
       <table id="supplier_items_table" class="grid borderForTable" cellspacing="0"
               cellpadding="0" style="width:100%">
            <thead>
                <tr class="row_head">
                    <th width="14%"><s:text name="label.common.location_code" /></th>
                    <th width="14%"><s:text name="label.manageWarehouse.contactPersonName" /></th>
                    <th width="18%"><s:text name="columnTitle.common.address" /></th>
                    <th width="14%"><s:text name="columnTitle.common.city" /></th>
                    <th width="6%"><s:text name="columnTitle.common.state" /></th>
                    <th width="6%"><s:text name="columnTitle.common.country" /></th>
                    <th width="10%"><s:text name="label.common.zipCode" /></th>
                    <th width="10%"><s:text name="label.common.phone" /></th>
                    <th width="12%"><s:text name="label.common.email" /></th>
                </tr>
            </thead>
            <tbody>
                <!-- Iterator over all supplier addresses -->
                   <tr>
                        <td><s:property value="location.code"/> </td>
                        <td>
                        <s:set var="contact_Person_Name" value="%{location.address.contactPersonName}" />
                        <s:if test="shipment != null && shipment.contactPersonName!=null">
                          <s:set var="contact_Person_Name" value="%{shipment.contactPersonName}"/>
                        </s:if>
                         <s:if test="%{taskName.equals(@tavant.twms.jbpm.WorkflowConstants@SUPPLIER_SHIPMENMT_GENERATED)}">
                          <s:textfield name="shipment.contactPersonName"  value="%{#contact_Person_Name}"/>
                         </s:if>
                          <s:else>
                            <s:property value="%{#contact_Person_Name}"/> 
                          </s:else>
                        </td>
                        <td><s:property value="location.address.addressLine1"/>,<s:property value="location.address.addressLine2"/>  </td>
                        <td><s:property value="location.address.city"/>  </td>
                        <td><s:property value="location.address.state"/>  </td>
                        <td><s:property value="location.address.country"/>  </td>
                        <td> <s:property value="location.address.zipCode"/>  </td>
                        <td> <s:property value="location.address.phone"/></td>
                        <td><s:property value="location.address.email"/> </td>
                   </tr>
            </tbody>

            </table>

</div>

