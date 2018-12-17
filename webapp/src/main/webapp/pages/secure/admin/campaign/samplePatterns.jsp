
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><s:text name="title.common.warranty"/></title>
	<s:head theme="twms"/>
	<u:stylePicker fileName="detailDesign.css"/>
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="HeaderTxt"><s:text name="label.campaign.supportedPatterns"/>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="sectionbgPadding">
		<div class="noBorderCellbg">
		<table width="100%" border="0" cellpadding="0" cellspacing="1" class="grid borderForTable">
			<tr>
				<td width="12%" nowrap="nowrap" class="colHeader">&nbsp;</td>
				<td width="22%" nowrap="nowrap" class="colHeader">
				<div align="center"><u><s:text name="label.campaign.pattern1"/></u><br />
				<s:text name="label.campaign.numericRange"/><br />
				<s:text name="label.campaign.numeric"/></div>
				</td>
				<td width="22%" nowrap="nowrap" class="colHeader">
				<div align="center"><u><s:text name="label.campaign.pattern2"/></u><br />
				<s:text name="label.campaign.numericRange"/><br />
				<s:text name="label.campaign.alphanumeric"/></div>
				</td>
				<td width="22%" nowrap="nowrap" class="colHeader">
				<div align="center"><u><s:text name="label.campaign.pattern3"/></u><br />
				<s:text name="label.campaign.alphabeticRange"/><br />
				<s:text name="label.campaign.alphanumeric"/></div>
				</td>
				<td width="22%" nowrap="nowrap" class="colHeader">
				<div align="center"><u><s:text name="label.campaign.pattern4"/></u><br />
				<s:text name="label.campaign.alphanumericRange"/><br />
				<s:text name="label.campaign.alphanumeric"/></div>
				</td>
			</tr>
			<tr>
				<td width="12%" nowrap="nowrap" class="tableDataAltRow">
				<s:text name="label.campaign.rangeFrom"/>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">65789200</div>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">657AB200</div>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">657AB200</div>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">6578*200</div>
				</td>
			</tr>
			<tr>
				<td width="12%" nowrap="nowrap" class="tableDataAltRow">
				<s:text name="label.campaign.rangeTo"/>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">65789210</div>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">657AB210</div>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">657AF200</div>
				</td>
				<td width="22%" nowrap="nowrap" class="tableDataAltRow">
				<div align="center">6578*200</div>
				</td>
			</tr>
			<tr>
				<td colspan="5" nowrap="nowrap" ><span
					class="totalTxtBold"><s:text name="label.common.outPut"/></span>
				<div align="center"></div>
				<div align="center"></div>
				<div align="center"></div>
				</td>
			</tr>
			<tr>
				<td nowrap="nowrap" >&nbsp;</td>
				<td valign="top" nowrap="nowrap" >
				<div align="center">65789200<br />
				65789201<br />
				65789202<br />
				65789203<br />
				65789204<br />
				65789205<br />
				65789206<br />
				65789207<br />
				65789208<br />
				65789209<br />
				65789210</div>
				</td>
				<td valign="top" nowrap="nowrap" >
				<div align="center">657AB200<br />
				657AB201<br />
				657AB202<br />
				657AB203<br />
				657AB204<br />
				657AB205<br />
				657AB206<br />
				657AB207<br />
				657AB208<br />
				657AB209<br />
				657AB210</div>
				</td>
				<td valign="top" nowrap="nowrap" >
				<div align="center">657AB200<br />
				657AC200<br />
				657AD200<br />
				657AE200<br />
				657AF200</div>
				</td>
				<td valign="top" nowrap="nowrap" >
				<div align="center">65780200<br />
				65781200<br />
				65782200<br />
				65783200<br />
				65784200<br />
				65785200<br />
				65786200<br />
				65787200<br />
				65788200<br />
				65789200<br />
				6578A200<br />
				6578B200<br />
				6578C200<br />
				6578D200<br />
				6578E200</div>
				</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="labelNormal">
		<div align="left"><s:text name="label.campaign.bottomMessage"/></div>
		</td>
	</tr>
</table>
</body>
</html>
