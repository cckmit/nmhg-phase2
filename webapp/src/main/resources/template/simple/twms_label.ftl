<label id="${parameters.id?html}" for="${parameters.for?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>
><#rt/>
 <@s.text name="${parameters.value?html}"/>&nbsp;:&nbsp;
</label>