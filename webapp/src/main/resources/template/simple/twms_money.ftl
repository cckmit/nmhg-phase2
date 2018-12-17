<script type="text/javascript">
 dojo.require("twms.widget.NumberTextBox");
</script>
<span
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle}"<#rt/>
</#if>
>
<input type="hidden" id="curr_${parameters.id?html}"<#rt/>
<#if parameters.name?exists>
 name="${parameters.name}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="${parameters.symbol}"<#rt/>
</#if>
 />
<span class="money_symbol">${parameters.symbol}</span>
<input class="money_input" type="text" dojoType="twms.widget.NumberTextBox" intermediateChanges="true" id="${parameters.id?html}"<#rt/>
<#if parameters.name?exists>
 name="${parameters.name}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="${parameters.nameValue}"<#rt/>
</#if>
<#if parameters.fieldsize?exists>
 size="${parameters.fieldsize}"<#rt/>
</#if>
<#if parameters.maxlength?exists>
 maxlength="${parameters.maxlength}"<#rt/>
</#if>
<#if parameters.onchange?exists>
 onchange="${parameters.onchange}"<#rt/>
</#if>
 <#if parameters.disabled?exists>
 disabled="${parameters.disabled}"<#rt/>
</#if>
 constraints="{pattern: '#.00'}"
/>

</span>
