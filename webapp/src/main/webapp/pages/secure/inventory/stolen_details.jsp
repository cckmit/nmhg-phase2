<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Jul 1, 2008
  Time: 10:40:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<html>
  <head><title>Stolen details Page </title>
  <style>
.borderForTable tr td{
border:none !important;
}
</style>
</head>
  <body>
  
  <table cellspacing="0" cellpadding="0"  class="grid borderForTable" width="100%">
      <thead>
          <tr class="row_head">
              <th><s:text name="label.warrantyAdmin.actionPerformed"/></th>
              <th><s:text name="label.common.comments"/></th>
              <th><s:text name="label.warrantyAdmin.transactionDate"/></th>
                <th><s:text name="label.stolen.stolenDate/unstolenDate"/></th>            
          </tr>
      </thead>
      <tbody>
          <s:if test="inventoryItem.inventoryItemAttrVals.size>0">
              <s:set name="counter" value="inventoryItem.inventoryItemAttrVals.size-1"/>
          </s:if>
          <s:iterator value="inventoryItem.inventoryItemAttrVals">
              <s:if test="'stolenComments'==inventoryItem.inventoryItemAttrVals[#counter].attribute.name  ||
                'unStolenComments'==inventoryItem.inventoryItemAttrVals[#counter].attribute.name">
              <tr>
                  <s:if test="'stolenComments'==inventoryItem.inventoryItemAttrVals[#counter].attribute.name">
                  <td><s:text name="label.warrantyAdmin.itemCondition.stolen"/></td>
                  </s:if>
                  <s:if test="'unStolenComments'==inventoryItem.inventoryItemAttrVals[#counter].attribute.name">
                  <td><s:text name="label.warrantyAdmin.itemCondition.unStolen"/></td>
                  </s:if>
                  <td><s:property value="%{getStolenTransaction(inventoryItem.inventoryItemAttrVals[#counter].value).comments}"></s:property> </td>
                  <td><s:property value="%{getStolenTransaction(inventoryItem.inventoryItemAttrVals[#counter].value).conditionUpdatedOn}"></s:property> </td> 
                  <td><s:property value="%{getStolenTransaction(inventoryItem.inventoryItemAttrVals[#counter].value).dateOfStolenOrUnstolen}"></s:property></td>                 
              </tr>
              </s:if>
              <s:set name="counter" value="#counter-1"/>
          </s:iterator>
      </tbody>
  </table>
  
  </body>
</html>
