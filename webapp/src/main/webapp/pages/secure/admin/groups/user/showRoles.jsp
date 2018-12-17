

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<div id="dialogBoxContainer" style="display: none">
    <div dojoType="twms.widget.Dialog" id="searchCriteria" bgColor="#FFF"
         bgOpacity="0.5" toggle="fade" toggleDuration="250"
         title="<s:text name= 'title.roles.allRoles'/>" style="width:70%;overflow-y:hidden">
        <div dojoType="dijit.layout.LayoutContainer" style="height:500px;">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" >
                <table width="100%" cellspacing="0" cellpadding="0">
                    <tr class="row_head">
                        <th width="10%" colspan="1" style="display:none"><input id = "chkAll" type="checkbox"
                                                           onclick=checkUncheckAll();
                                                           /></th>
                        <th width="40%" colspan="2"><s:text
                                name="columnTitle.roles.roleName" /></th>
                        <th width="40%" colspan="2"><s:text
                                name="columnTitle.roles.roleDescription" /></th>
                        <th width="10%" colspan="1"><s:text
                                name="columnTitle.roles.roleType" /></th>

                    </tr>
                </table>

                <table id="allRoles" width="100%" cellspacing="0" cellpadding="0">

                    <s:iterator value="allRoles" status="iter">

                        <tr width="100%" id="<s:property value="%{name}"/>">

                            <td width="10%" colspan="1" class="label"><s:checkbox
                                    name="rolesToAssign[%{#iter.index}]" value="" fieldValue="%{id}" />

                            </td>
                            <td width="40%" colspan="2" class="labelBold"><s:property
                                    value="%{name}" /></td>
                            <td width="40%" colspan="2" class="labelBold"><s:property
                                    value="%{description}" /></td>
                            <td width="10%" colspan="1" class="labelBold"><s:property
                                    value="roleType.type" /></td>
                        </tr>
                    </s:iterator>

                </table>

                <table width="100%" cellspacing="0" cellpadding="0">
                    <tr class="row_head">
                        <th  ><div align = "center"><input type="button" id="select_roles_btn"
                                                       value="<s:text name = 'button.commom.ok'/>" onclick=
                                                           populatelist();>
                    </div></th>
                    </tr>
                </table>


            </div>

        </div>
    </div>
</div>
<script type="text/javascript">
    var checked = false;
    var lengthc;
    function checkUncheckAll() {
        if (checked == false) {
            checked = true
        } else {
            checked = false
        }
        for ( var i = 0; i < dojo.query("input", dojo.byId('allRoles')).length; i++) {

            dojo.query("input", dojo.byId('allRoles'))[i].checked=checked;
        }

    }
</script>
