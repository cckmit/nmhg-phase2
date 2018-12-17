<#if parameters.action?if_exists != "">
    <#assign actionMapping = "${parameters.action}"/>
</#if>
<#if parameters.inputType?if_exists != "">
    <#assign inputType = "${parameters.inputType}"/>
</#if>
<#assign itemCount = 0/>
<#if parameters.list?exists>
<@s.iterator value="parameters.list">
<#assign itemCount = itemCount + 1/>
<#if parameters.listKey?exists>
<#assign itemKey = stack.findValue(parameters.listKey)/>
<#else>
<#assign itemKey = stack.findValue('top')/>
</#if>
<#if parameters.listValue?exists>
<#assign itemValue = stack.findString(parameters.listValue)/>
<#else>
<#assign itemValue = stack.findString('top')/>
</#if>
<#assign itemKeyStr=itemKey.toString() />
<#if parameters.property?exists>
<#assign propertyValue = stack.findValue(parameters.property)/>
<#else>
<#assign propertyValue = stack.findValue('top')/>
</#if>
<#if parameters.docId?exists>
<#assign documentId = stack.findValue(parameters.docId)/>
</#if>
<#assign documentIdStr=documentId.toString() />
<table>
<tr>
<td >
<input type=${inputType?html} name="${parameters.name?html}" value="${itemKeyStr?html}" id="${parameters.name?html}-${itemCount}"<#rt/>
<#if tag.contains(parameters.nameValue, itemKey)>
checked="checked"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
disabled="disabled"<#rt/>
</#if>
<#if parameters.title?exists>
title="${parameters.title?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
/>
<label for="${parameters.name?html}-${itemCount}" class="checkboxLabel">${itemValue?html}</label> &nbsp&nbsp&nbsp&nbsp
</td>
<#if documentIdStr?if_exists != "-1">
<img src="<@s.url value="${actionMapping?html}.action?docId=${documentIdStr?html}"/>" ><#rt/>
</#if>
<#if parameters.cssStyle?exists>
<#if "${parameters.cssStyle?html}" == "vertical">
<br><#rt/>
</tr>
</table>
</#if>
</#if>
</@s.iterator>
<#else>
&nbsp;
</#if>