<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>

<script type="text/JavaScript">
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("twms.widget.MultipleInventoryPicker");
    dojo.require("twms.widget.DateTextBox");
</script>
<!-- <div dojoType="dijit.layout.ContentPane" style="width: 100%; height: 100%; overflow: auto">-->
    <div dojoType="twms.widget.TitlePane"
         title="<s:text name="label.machineInfo"/>"
         id="equipment_info" labelNodeClass="section_header" open="true">        
        <div dojoType="dojox.layout.ContentPane" executeScripts="true" id="selectedInventoriesPane">
<s:if test="isInstallingDealerEnabled()">
 <jsp:include flush="true" page="../../warrantyProcess/common/write/copyInstallDate.jsp" />
                       </s:if>
            <jsp:include page="warranty_transfer_equipment_info.jsp"/>
        </div>
     </div>
<!--</div>-->

