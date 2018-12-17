<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 9, 2008
  Time: 6:19:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>


<table class="grid borderForTable" style="width:97%" cellspacing="0" cellpadding="0">
    <thead>
        <tr class="row_head">
            <th ><s:text name="label.common.date"/></th>
            <th><s:text name="label.policyAudit.status"/></th>
            <th><s:text name="label.common.user"/></th>
            <th><s:text name="label.common.comments"/></th>
        </tr>
    </thead>
    <tbody>
        <s:if test="forWarranty.warrantyAudits!=null && forWarranty.warrantyAudits.size>0">
            <s:iterator value="forWarranty.warrantyAudits" status="warrantyAudits">
                <tr>
                    <td><s:property value="d.updatedOn"/></td>
                    <td><s:property value="status.status"/></td>
                    <td><s:property
                            value="d.lastUpdatedBy.CompleteNameAndLogin"/></td>
                    <td><s:property value="externalComments"/></td>
                </tr>
            </s:iterator>
        </s:if>
    </tbody>
</table>