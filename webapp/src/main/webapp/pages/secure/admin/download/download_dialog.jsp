<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript">  
	dojo.require("twms.widget.Dialog");
	dojo.addOnLoad(function(){
		<s:if test="resultSize >= 0">
		dijit.byId("downloadDlg").show();
		</s:if>
	});
</script>
<s:hidden name="downloadPageNumber"/>
<div dojoType="twms.widget.Dialog" id="downloadDlg" bgColor="white" bgOpacity="0.5" toggle="fade"
     toggleDuration="250" title="Download" style="width: 80%; height: 150px;overflow: hidden;">
	<div class="dialogContent" dojoType="dijit.layout.LayoutContainer" style="border : 1px solid #EFEBF7;height: 125px">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client" >
            <center>
            	<div class="quick_page_switch" style="float:center">
            		
        <s:if test="resultSize == 0">
			<table width="100%" cellspacing="0" cellpadding="0" class="grid">
			<tr align="center"><td>No results found</td></tr>
			</table>
		</s:if>
		<s:elseif test="resultSize > 0 ">
		
		<script>
			function downloadPage(pageNumber) {
				var form = dojo.byId("baseForm");
				form.downloadPageNumber.value=pageNumber;
                form.action = "downloadPageData.action";
                form.submit();
			}
		</script>
		<table  cellspacing="10" cellpadding="0" >
		<tr align="center"><td colspan="3">Totals Records <s:property value="resultSize"/></td></tr>
		<s:bean name="org.apache.struts2.util.Counter"  id="pgCounter"> 
		  <s:param name="last" value="%{(resultSize-1)/maxDownloadSize+1}" /> 
		</s:bean> 
		
		<s:iterator value="#pgCounter">
			<s:if test="top%3 == 1">
			<tr>
			</s:if>
			<td>
			<s:if test="top == #pgCounter.last">
				<s:a onclick="downloadPage(%{top}-1)">
					<s:property value="(top-1) * maxDownloadSize + 1"/> to <s:property value="resultSize"/>
				</s:a>
			</s:if>
			<s:else>
			<s:a onclick="downloadPage(%{top}-1)" href="#">
				<s:property value="(top-1) * maxDownloadSize + 1"/> to <s:property value="top * maxDownloadSize"/>
			</s:a>
			</s:else>
			</td>
			<s:if test="top%3 == 0 || top == #pgCounter.last">
			</tr>
			</s:if>
		</s:iterator>
		</table>
		</s:elseif>
            	</div>
            </center>
		</div>
	</div>
</div>