<#assign errorsOrWarningsAreThere=((parameters.errors?exists && parameters.errors?size > 0) ||
 (parameters.warnings?exists && parameters.warnings?size > 0))/>
<#if !parameters.tagWasUsedBefore>
<link rel="stylesheet" type="text/css" href="css/theme/official/ui-ext/actionResult/actionResult.css"/>
<script type="text/javascript">
    <#if !parameters.errorsOrWarningsExist>
dojo.require("dojo.NodeList-fx");
dojo.addOnLoad(function() {
      setTimeout(function() {dojo.query(".twmsActionResultsFoldable").wipeOut().play();}, ${parameters.wipeOutTime?c});
});
   </#if>

</script>
</#if>
<#if parameters.showComponentBody>
<div class="twmsActionResults">
<#if (parameters.errors?exists && parameters.errors?size > 0)>
    <div class="twmsActionResultsSectionWrapper twmsActionResultsErrors">
        <h4 class="twmsActionResultActionHead"><@s.text name="${parameters.errorsMessageKey}"/></h4>
        <ol>
            <#list parameters.errors as error>
                <li>${error}</li>
            </#list>
        </ol>
        <hr/>
    </div>
</#if>
<#if (parameters.warnings?exists && parameters.warnings?size > 0)>
    <div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
        <h4 class="twmsActionResultActionHead"><@s.text name="${parameters.warningsMessageKey}"/></h4>
        <ol>
            <#list parameters.warnings as warning>
                <li>${warning}</li>
            </#list>
        </ol>
        <hr/>
    </div>
</#if>
<#if (parameters.messages?exists && parameters.messages?size > 0)>
    <div class="twmsActionResultsSectionWrapper twmsActionResultsMessages <#rt/>
     <#if (parameters.wipeMessages?exists && parameters.wipeMessages)><#rt/>
      twmsActionResultsFoldable<#rt/>
     </#if><#rt/>
    ">
        <h4 class="twmsActionResultActionHead"><@s.text name="${parameters.messagesMessageKey}"/></h4>
        <ol>
            <#list parameters.messages as message>
                <li>${message}</li>
            </#list>
        </ol>
        <hr/>
    </div>
</#if>
</div>
</#if>
