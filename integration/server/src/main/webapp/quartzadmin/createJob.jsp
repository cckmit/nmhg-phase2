<%-- 
    Document   : createJob
    Created on : 20 Dec, 2011, 11:39:20 AM
    Author     : prasad.r
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE html>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Integration Server :: Quartz Scheduler</title>
		<link href="<s:url includeParams='none' value='/stylesheet/master.css' encode='false'/>" rel="stylesheet" type="text/css">
		<link href="<s:url includeParams='none' value='/stylesheet/style.css' encode='false'/>" rel="stylesheet" type="text/css">
        <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/jquery-1.5.2.min.js' encode='false'/>" type="text/javascript"></script>
    </head>
    <body>
		<div align="center"><b>Integration Server</b> </div>
		<div align="left"><a href='<s:url action="Home"/>' style="color: navy;"> Home </a> &gt; <a href='<s:url value="/quartzadmin/index.jsp"/>'>Scheduler List</a>
		&gt; <a href='schedulerDetail.action?id=<s:property value="id"/>'> Quartz Job Details</a> &gt; <s:if test="'EDIT'.equals(action) || 'UPDATE'.equals(action)">Update Job</s:if><s:else>Create Job</s:else></div>
		<div align="right" style="padding-right: 25px"><a href='<s:url action="Home!logout"/>' style="color: navy;"> Logout </a></div>
        <hr>
        <s:actionerror/>
        <form name="createJob" id="createJob" action="createJob.action" method="post">
            <s:hidden name="id" />
            <s:if test="'EDIT'.equals(action) || 'UPDATE'.equals(action)">
                <div><h3>Update Job</h3></div>
                <s:hidden name="action" value="UPDATE"/>
                <s:hidden name="triggerName" />
                <s:hidden name="triggerGroup" />
            </s:if>
            <s:else>
                <div><h3>Create Job</h3></div>
            </s:else>
            <s:if test="jobType != null && !''.equals(jobType)">
                <h5>Select the Job Type</h5>
            </s:if>
            <div>
                <s:if test="jobType != null && !''.equals(jobType)">
                    <input type="radio" name="jobType" value="MAILJOB" <s:if test="'MAILJOB'.equals(jobType)">checked="true"</s:if> onchange="toggle('#ftpJob','#mailingJob')"/>Mailing Job 
                    <input type="radio" name="jobType" value="FTPJOB" <s:if test="'FTPJOB'.equals(jobType)">checked="true"</s:if> onchange="toggle('#mailingJob','#ftpJob')"/>FTP Job</div>
                </s:if>
            <table>
                <s:if test="'EDIT'.equals(action) || 'UPDATE'.equals(action)">
                    <s:textfield name="jobName" required="true" readonly="true" maxLength="30" label="Job Name:"/>                 
                </s:if>
                <s:else>
                    <s:textfield name="jobName" required="true" maxLength="30" label="Job Name:"/> 
                </s:else>
                <s:textfield name="jobGroup" required="true" maxLength="30" readonly="true" label="Job Group:"/>
				<s:if test="triggers.size > 1">
					<tr>
						<td class="tdLabel">Cron Expression:<s:hidden name="cronExpression" id="cronExpression"/></td>
						<td>
							<s:iterator value="triggers">
								<input name="rad" value="<s:property value="name"/>" type="radio" onclick="setVals('<s:property value="name"/>','<s:property value="group"/>');">&nbsp;<s:property value="name"/>
                                <input id='cronExp_<s:property value="name"/>' value="<s:property value="cronExpression"/>" /><br>
                            </s:iterator>

						</td>
					</tr>
				
				</s:if>
				<s:else>
					<s:textfield name="cronExpression" required="true" maxLength="30" label="Cron Expression:"/>
				</s:else>                
                <s:if test="jobType != null && !''.equals(jobType)">
                    <s:textfield name="reportName" required="true" maxLength="30" label="Name of Report:"/>
                    <s:textfield name="bugId" required="true" maxLength="30" label="Bug No:"/>
                </s:if>
            </table>
            <s:if test="jobType != null && !''.equals(jobType)">
                <div id="mailingJob" style="display: none">
                    <table>
                        <s:textfield maxLength="100" name="toAddress" required="true" label="To Address:"/>
                        <s:textfield maxLength="100" name="ccAddress" required="true" label="CC Address:" />
                        <s:textfield maxLength="50" name="subject" required="true" label="Subject:"/>
                        <s:textarea name="mailContent" required="true" cols="100" rows="10" label="Mail Body:"/>
                        <s:textarea cols="150" name="sqlForMailJob" required="true" rows="15" label="SQL Query To Use:"/>
                        <tr>
                            <td>Format: </td>
                            <td>CSV</td>
                        </tr>

                    </table>
                </div>
                <div id="ftpJob" style="display: none">
                    <table>
                        <s:textfield maxLength="30" name="ftpAddress" required="true" label="FTP Address:"/>
                        <s:textfield maxLength="30" name="ftpUserName" required="true" label="FTP User Name:"/>
                        <s:password maxLength="30" name="ftpPassword" required="true" label="FTP Password:"/>
                        <s:textarea cols="150" name="sqlForFTPJob" required="true" rows="15" label="SQL query to use:"/>
                        <tr>
                            <td>Format: </td>
                            <td>CSV</td>
                        </tr>
                    </table>
                </div>
            </s:if>
            <s:if test="'EDIT'.equals(action) || 'UPDATE'.equals(action)">
                <div><s:submit name="Update Job" value="Update Job" align="center"/></div>
            </s:if>
            <s:else>
                <div><s:submit name="Create Job" value="Create Job" align="center"/></div>
            </s:else>
        </form>
    </body>
    <script lang="javascript">
        <s:if test="triggers.size > 1">
        $("#createJob").submit(function(evt){
            var a = "cronExp_"+$('#triggerName').val();
            var b = $('#'+a).val();
			$('#cronExpression').val(b);
        });
        </s:if>
		function setVals(tName,tGrp){
            $('#triggerName').val(tName);
            $('#triggerGroup').val(tGrp);
        }

        function toggle(s, r){
            $(s).hide();
            $(r).show();
        }
        $(document).ready(function() {
        <s:if test="'MAILJOB'.equals(jobType)">toggle('#ftpJob','#mailingJob');</s:if>
        <s:if test="'FTPJOB'.equals(jobType)">toggle('#mailingJob','#ftpJob')</s:if>
            });
    </script>
</html>


