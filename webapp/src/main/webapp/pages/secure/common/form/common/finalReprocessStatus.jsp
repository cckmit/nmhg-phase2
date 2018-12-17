
<%@page contentType="application/x-json"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="preview.css"/>
<u:stylePicker fileName="ui-ext/actionResult/actionResult.css"/>

<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.TabContainer");
</script>
<div dojoType="dijit.layout.ContentPane" title="Deatil" style="overflow:auto;height:410px ">
<s:if test="syncTrackerList.empty">
    <div id="Result Page" class="twmsActionResults">
        <div class="twmsActionResultsSectionWrapper twmsActionResultsErrors ">
            <h4 class="twmsActionResultActionHead">MESSAGES</h4>
            <OL>
                <s:if test="isDeleteAction()">
                    <li><s:text name="No Records Are Hidden !!" />
                </s:if>
                <s:else>
                    <li><s:text name="label.manageRemoteInteractions.noRecordsReprocessed"/></li>
                </s:else>
            </OL>
        </div>
        <table class="grid" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="6" align ="center">
         <input type="button" name="Submit2" value="Close" id="btnSubmit" onclick="refreshAfterReprocess()" class="buttonGeneric" />
         </td>
        </tr>
       </table>
    </div>
</s:if>
<s:else>
                <div id="Result Page" class="twmsActionResults">
                    <div class="twmsActionResultsSectionWrapper twmsActionResultsMessages ">
                    <h4 class="twmsActionResultActionHead">MESSAGES</h4>
                    <OL>
                        <s:if test="isDeleteAction()">
                            <li><s:text name="Following records are hidden Successfully !!" /></li>
                        </s:if>
                        <s:else>
                            <li><s:text name="Successfully Reprocessed following Records" /></li>
                        </s:else>
                    </OL>
                    </div>
                </div>
                    <table class="grid" cellspacing="0" cellpadding="0" id="processed_log_table">
                        <thead>
                                <tr class="row_head">
                                    <th class="labelStyle" align="left"><s:text name="label.manageRemoteInteractions.transactionId"/></th>
                                    <th class="labelStyle" align="left"><s:text name="label.manageRemoteInteractions.syncType"/></th>
                                    <th class="labelStyle" align="left" ><s:text name="label.manageRemoteInteractions.status"/></th>
                                    <th class="labelStyle" align="left" ><s:text name="label.manageRemoteInteractions.ErrorMsg"/></th>
                                    <th class="labelStyle" align="left"><s:text name="label.manageRemoteInteractions.updateDate"/></th>
                                </tr>
                        </thead>
                        <tbody id="syncTrackerReprocess_list">
                                <s:iterator value="syncTrackerList" status="syncTrackerList" id="SyncTrackerReprocessList">
                                    <tr>
                                            <td><s:property value="uniqueIdValue"/></td>
                                            <td><s:property value="syncType"/></td>
                                            <td><s:property value="status.status"/></td>
                                            <td><s:property value="formatErrorMsg(errorMessage)"/></td>
                                            <td><s:property value="updateDate"/></td>
                                    </tr>
                                </s:iterator>
                        <tr><td colspan="6" align ="center">&nbsp;</td></tr>
                        <tr><td colspan="6" align ="center">&nbsp;</td></tr>
                        <tr>
                                <td colspan="6" align ="center">
                                    <input type="button" name="Submit2" value="Close" id="btnSubmit" onclick="<s:if test="isDeleteAction()">
                                        refreshAfterDelete()
                                       </s:if>
                                       <s:else>
                                           refreshAfterReprocess()
                                       </s:else>" class="buttonGeneric" />
                                </td>
                        </tr>
                        </tbody>
                    </table>
</s:else>

</div>