<tr style="display:none">
    <td>
        <script type="text/javascript">
            dojo.require("twms.widget.NList");
        </script>
        <div dojoType="twms.widget.NList"
            nextAvailableIndex="${parameters.nextAvailableIndex}"
            rowTemplateUrl="'${parameters.rowTemplateUrl}'"
            collectionName="'${parameters.collectionName}'"
            <#if parameters.paramsVar?if_exists!="">
                paramsVar="${parameters.paramsVar}"
            </#if>
        >
        </div>
    </td>
</tr>