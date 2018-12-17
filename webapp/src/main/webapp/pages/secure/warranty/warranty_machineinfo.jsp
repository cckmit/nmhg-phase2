<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>

<script type="text/JavaScript">
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("twms.widget.MultipleInventoryPicker");
    dojo.require("twms.widget.DateTextBox");
</script>
<!-- <div dojoType="dijit.layout.ContentPane" >-->
    <div dojoType="twms.widget.TitlePane"
         title="<s:text name="label.machineInfo"/>"
         id="equipment_info" labelNodeClass="section_header" open="true">        
        <div dojoType="dojox.layout.ContentPane" executeScripts="true" id="selectedInventoriesPane">
        <s:if test="isInstallingDealerEnabled()">
 <jsp:include flush="true" page="../warrantyProcess/common/write/copyInstallDate.jsp" />
                      </s:if>
					  
            <div class="spacer5"></div><jsp:include flush="true" page="warranty_equipment_info.jsp"/>
        </div>
     </div>
<!--</div>-->

