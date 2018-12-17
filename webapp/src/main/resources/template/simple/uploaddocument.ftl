<script type="text/javascript">
    dojo.require("twms.widget.FileUploader");
</script>

<div dojoType="twms.widget.FileUploader" name="${parameters.name}"
     documentDownloadUrl="${parameters.documentDownloadAction}"<#rt/>
     documentUploadUrl="${parameters.documentUploadAction}"<#rt/>
     documentDeletionUrl="${parameters.documentDeletionAction}"<#rt/>
     fieldSize="${parameters.fieldSize?c}"<#rt/>     
    <#if parameters.id??>id="${parameters.id}"<#rt/></#if>
    <#if parameters.disabled!false><#rt/>
        disabled="disabled"<#rt/><#rt/>
    </#if><#rt/>
    <#if parameters.tabindex??><#rt/>
        tabindex="${parameters.tabindex?html}"<#rt/>
    </#if><#rt/>
    <#if parameters.selectedFilesCountParam??><#rt/>
        selectedFilesCountParamName="${parameters.selectedFilesCountParam?html}"<#rt/>
    </#if><#rt/>
    <#if parameters.trimFileNameDisplayTo??><#rt/>
        trimFileNameDisplayTo="${parameters.trimFileNameDisplayTo?c}"<#rt/>
    </#if><#rt/>
     <#if parameters.maxFilesToBeUploaded??><#rt/>
        maxFilesToBeUploaded="${parameters.maxFilesToBeUploaded?c}"<#rt/>
    </#if><#rt/>
    <#if parameters.singleFileUpload?exists>
 		singleFileUpload="${parameters.singleFileUpload?string?html}"<#rt/>
	</#if>	 
    <#if parameters.uploadedFilesInfo??>uploadedFilesInfo='${parameters.uploadedFilesInfo}'<#rt/></#if>
    <#if parameters.cssClass??>cssClasses='${parameters.cssClass}'<#rt/></#if>
    <#if parameters.cssStyle??>cssStyle='${parameters.cssStyle}'<#rt/></#if>
    <#if parameters.label??>fileBrowserTriggerLabel='${parameters.label}'<#rt/></#if>
    canDeleteAlreadyUploaded='${parameters.canDeleteAlreadyUploaded?string}'<#rt/>
><#rt/>
</div>