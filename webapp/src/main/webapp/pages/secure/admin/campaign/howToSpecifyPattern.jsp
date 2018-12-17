
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
		<td class="HeaderTxt"><s:text name="label.campaign.howToSpecifyPattern"/>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="sectionbgPadding">
		<div class="noBorderCellbg">
		<table  border="0" cellpadding="0" cellspacing="1" class="grid borderForTable">
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<div align="center"><u><s:text name="label.campaign.searchPattern"/></u><br /></div>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center"><u><s:text name="label.campaign.pattern"/></u><br /></div>
				</td>
			</tr>
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<s:text name="label.campaign.valuesStartsFrom"/>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center">^CA</div>
				</td>
			</tr>
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<s:text name="label.campaign.valuesEndsWith"/>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center">CA$</div>
				</td>
			</tr>
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<s:text name="label.campaign.valuesContains"/>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center">CA+</div>
				</td>
			</tr>
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<s:text name="label.campaign.valuesStartsFromAndEndsWith"/>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center">CA(.)+BD</div>
				</td>
			</tr>
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<s:text name="label.campaign.valuesStartsFromADigit"/>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center">^\d</div>
				</td>
			</tr>
			<tr>
				<td width="80%" nowrap="nowrap" class="colHeader">
				<s:text name="label.campaign.valuesStartsFromACharacter"/>
				</td>
				<td width="20%" nowrap="nowrap" class="colHeader">
				<div align="center">^\w</div>
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

		<div align="left"><s:text name="label.campaign.supportsRegularExpression"/></div>
		</td>
	</tr>
</table>
</body>
</html>
