<#assign msg=""/>
<#list fieldErrors[parameters.name] as error>
   <#assign msg=(msg+error+" ")/>
</#list>
<img src="${base}/tagsupport/alerts.gif" title="${msg?html}" alt="${msg?html}" for="${parameters.name?html}" type="error" class="errormsg"/>