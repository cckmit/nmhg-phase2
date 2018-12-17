<#if (parameters.tagWasUsedBefore == false)>
<script type="text/javascript">
 dojo.require("twms.widget.Select");
</script>
</#if>
<select dojoType="twms.widget.Select" <#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}" <#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}" <#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle}" <#rt/>
</#if>
<#if parameters.required?exists>
 required="${parameters.required?string?html}"<#rt/>
</#if>
 autoComplete="false" name="${parameters.name?html}">
<option value="null"<#rt/>
>${parameters.I18nSelect}</option><#lt/>
<@s.iterator value="parameters.lovList" id="option">
 <#assign itemKey = stack.findValue("code")/>
 <#assign itemKeyStr = itemKey.toString() />
 <#assign itemValue = stack.findString("description")/>
 <option value="${itemKey?html}"<#rt/>
  <#if (parameters.nameValue == itemKey)>
   selected="selected"<#rt/>
  </#if>
  >${itemValue?html}</option><#lt/>
</@s.iterator>
</select>
<#if parameters.id?exists>
	<script type="text/javascript">
		dojo.addOnLoad( function(){
			var lovCombo = dijit.byId("${parameters.id?html}");
			dojo.connect(lovCombo, "onChange", function(newValue) {
				dojo.publish("/track/valueChange", [{newLovCode: newValue}]);
			});
		});	
	</script>
</#if>