<script type="text/javascript">
       dojo.require("dojo.parser");
       dojo.require("twms.widget.Select");
       dojo.require("twms.data.AutoCompleteReadStore");
</script>

<#if parameters.id?if_exists!="">
<div dojoType="twms.data.AutoCompleteReadStore"<#rt/>
    data-dojo-id="autoCompleteStore_${parameters.id?html}"<#rt/>
    <#if parameters.href?if_exists!="">
    url="${parameters.href}"
    </#if>
    <#if parameters.loadMinimumCount??>
    searchPrefixMinimumLength="${parameters.loadMinimumCount?c}"
    </#if>
    requestMethod="post"
    <#if parameters.name?if_exists!="">
    searchPrefixParamAlias="${parameters.name?html}"<#rt/>
    </#if>
    doClientPaging="true">
 </div>
 </#if>
<select  dojoType="twms.widget.Select" autoSelectFirstValueOnTab="false" autoSelectIfSingleResult="true"
<#if parameters.id?if_exists!="">
  id="${parameters.id?html}"
  store="autoCompleteStore_${parameters.id?html}"
</#if>
<#if parameters.name?if_exists != "">
 <#if parameters.keyName?if_exists != "${parameters.name?html}Key">
 name="${parameters.keyName?html}"<#rt/>
 <#else>
 name="${parameters.name?html}"<#rt/>
 </#if>
</#if>
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?string?html}"<#rt/>
</#if>
<#if parameters.delay?exists>
 searchDelay="${parameters.delay?c}"<#rt/>
<#else>
 searchDelay="500"<#rt/>
</#if>
<#if parameters.cssClass?if_exists != "">
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?if_exists != "">
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
 searchAttr="label"<#rt/>
<#if parameters.autoComplete?exists>
 autoComplete="${parameters.autoComplete?string?html}"<#rt/>
</#if>
<#if parameters.delay?exists>
 searchDelay="${parameters.delay?c}"<#rt/>
</#if>
<#if parameters.disabled?exists>
 disabled="${parameters.disabled?string?html}"<#rt/>
</#if>
<#if parameters.dropdownWidth?exists>
 dropdownWidth="${parameters.dropdownWidth?c}"<#rt/>
</#if>
<#if parameters.dropdownHeight?exists>
 dropdownHeight="${parameters.dropdownHeight?c}"<#rt/>
</#if>
<#if parameters.get("size")?exists>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.maxlength?exists>
 maxlength="${parameters.maxlength?string?html}"<#rt/>
</#if>
<#if parameters.nameValue?if_exists != "">
 initialValue="${parameters.nameValue}"<#rt/>
</#if>
<#if parameters.nameKeyValue?if_exists != "">
 initialKey="${parameters.nameKeyValue}"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.formId?if_exists != "">
 formId="${parameters.formId?html}"<#rt/>
</#if>
<#if parameters.formFilter?if_exists != "">
 formFilter="${parameters.formFilter?html}"<#rt/>
</#if>
<#if parameters.listenTopics?if_exists != "">
 listenTopics="${parameters.listenTopics?html}"<#rt/>
</#if>
<#if parameters.notifyTopics?if_exists != "">
 notifyTopics="${parameters.notifyTopics?html}"<#rt/>
</#if>
<#if parameters.indicator?if_exists != "">
 indicator="${parameters.indicator?html}"<#rt/>
</#if>
<#if parameters.loadOnTextChange?default(false)>
 loadOnType="${parameters.loadOnTextChange?string?html}"<#rt/>
</#if>
<#if parameters.loadMinimumCount?exists>
 loadMinimum="${parameters.loadMinimumCount?c}"<#rt/>
</#if>
<#if parameters.showDownArrow?exists>
 hasDownArrow="${parameters.showDownArrow?string?html}"<#rt/>
</#if>
<#if parameters.required?exists>
 required="${parameters.required?string?html}"<#rt/>
</#if>
<#if parameters.iconPath?if_exists != "">
 buttonSrc="<@s.url value='${parameters.iconPath}' encode="false" includeParams='none'/>"<#rt/>
</#if>
>

<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />

<#if parameters.list?exists>
	<#if (parameters.headerKey?exists && parameters.headerValue?exists)>
		<option value="${parameters.headerKey?html}">${parameters.headerValue?html}</option>
	</#if>
	<#if parameters.emptyOption?default(false)>
	    <option value=""></option>
	</#if>
    <@s.iterator value="parameters.list">
    <#if parameters.listKey?exists>
    	<#assign tmpListKey = stack.findString(parameters.listKey) />
    <#else>
    	<#assign tmpListKey = stack.findString('top') />
    </#if>
    <#if parameters.listValue?exists>
    	<#assign tmpListValue = stack.findString(parameters.listValue) />
    <#else>
    	<#assign tmpListValue = stack.findString('top') />
    </#if>
    <option value="${tmpListKey?html}"<#rt/>
        <#if (parameters.nameValue?exists && parameters.nameValue == tmpListKey)>
 selected="selected"<#rt/>
        </#if>
    ><#t/>
            ${tmpListValue?html}<#t/>
    </option><#lt/>
    </@s.iterator>
</#if>
</select>