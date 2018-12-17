<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<table width="100%" class="repeat borderForTable" style="margin-left:0px;">
   <thead>
      <tr class="row_head">
         <th>
             <s:text name="columnTitle.common.serialNo" />
         </th>
         <th ><s:text name="label.newClaim.applicablePolicy"/></th>
         <authz:ifProcessor>
               <s:if test="task.claim.forMultipleItems">
                   <th ><s:text name="label.common.approved"/></th>
               </s:if>
         </authz:ifProcessor>
     </tr>
  </thead>
  <tbody>
      <s:iterator value="task.claim.claimedItems">
         <tr>
             <td style="margin-left: 25px;">
                   <s:property value="itemReference.referredInventoryItem.serialNumber" />
             </td>
             <td><s:property value="applicablePolicy.code"/></td>
             <authz:ifProcessor>
                  <s:if test="task.claim.forMultipleItems">
                      <td>
                          <s:if test="processorApproved">
                              <span style="color:green">
                                   <s:text name="label.common.yes" />
                               </span>
                          </s:if>
                          <s:else>
                              <span style="color:red">
                                   <s:text name="label.common.no" />
                              </span>
                         </s:else>
                     </td>
                </s:if>
            </authz:ifProcessor>
        </tr>
     </s:iterator>
   </tbody>
</table>
     
