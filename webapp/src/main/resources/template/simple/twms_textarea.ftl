<script type="text/javascript">
<#if parameters.maxLength?exists>
    dojo.addOnLoad(function() {
      twms.util.restrictTextEntry(dojo.byId("${parameters.id?html}"), ${parameters.maxLength?html});  
    });
</#if>
</script>
<textarea <#rt/>
	 name="${parameters.name?default("")?html}"<#rt/>
	 cols="${parameters.cols?default("")?html}"<#rt/>
	 rows="${parameters.rows?default("")?html}"<#rt/>
	<#if parameters.id?exists>
		id="${parameters.id?html}"<#rt/>
	</#if>
	<#if parameters.tabindex?exists>
		tabindex="${parameters.tabindex?html}"<#rt/>
	</#if>
	<#if parameters.cssClass?exists>
		class="${parameters.cssClass?html}"<#rt/>
	</#if>
	<#if parameters.cssStyle?exists>
	 	style="${parameters.cssStyle?html}"<#rt/>
	</#if>
	<#if parameters.title?exists>
		title="${parameters.title?html}"<#rt/>
	</#if>
	<#if (parameters.readOnly?exists && parameters.readOnly == true)>
		disabled="true"
	</#if>
	<#if parameters.onchange?exists>
		onchange="${parameters.onchange?html}"<#rt/>
	</#if>
>
<#if parameters.nameValue?exists>
<@s.property value="parameters.nameValue"/><#rt/>
</#if>
</textarea>
<#if parameters.maxLength?exists>
<div class="remainingCharsNotification">
    (<span id="${parameters.id?html}_remainingCharsCount"></span> characters left)
</div>
</#if>