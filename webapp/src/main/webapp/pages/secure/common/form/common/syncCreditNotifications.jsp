<%-- 
    Document   : syncCreditNotifications
    Created on : 21 Dec, 2011, 5:32:55 PM
    Author     : prasad.r
--%>


<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<!DOCTYPE html>
<html>
    <head>
        <s:head theme="twms"/>
        <u:stylePicker fileName="yui/reset.css" common="true" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <u:actionResults/>
<u:body>
    	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; overflow-x:hidden; overflow-y:auto;"
		id="root">
            <s:form method="post" theme="twms" id="form" action="syncFailedCreditNotifications.action">
                <table>
                    <tr>
                        <td colspan="2"><s:label value="Enter the comma separated sync tracker ids for credit notifications to be re-processed"/></td>
                    </tr>
                    <tr>
                        <td style="width: 25%">
                            Sync Tracker Ids:
                        </td>
                        <td>
                            <s:textarea name="syncTrackerIds" label="Sync Tracker Ids" required="true" rows="5" cols="100"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <s:submit name="submit" type="submit" />
                        </td>
                    </tr>
                </table>
            </s:form>

        </div>
</u:body>
</html>
