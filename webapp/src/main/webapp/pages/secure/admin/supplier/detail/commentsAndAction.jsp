<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="authz" uri="authz"%>

<div style="margin-top: 10px;">
	<div class="detailsHeader">
		<s:text name="title.partReturnConfiguration.comments" />
	</div>
	<table cellspacing="0" cellpadding="0" class="grid">
		<tr>
			<td width="20%" class="carrierLabel" nowrap="nowrap"><s:text
					name="label.partReturnConfiguration.comments" /> :</td>
			<td class="labelNormalTop"><t:textarea cols="80" rows="3"
					name="comments" id="com" wrap="physical" cssClass="bodyText" />
			</td>
		</tr>
	</table>
</div>
<div class="separator" />
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
<div class="buttonWrapperPrimary spacingAtTop">
	<s:submit value="%{getText('button.common.submit')}"
		cssClass="buttonGeneric" />
</div>
</authz:ifNotPermitted>