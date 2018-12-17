<%@ page contentType="text/html" language="java" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table width="90%" cellpadding="2" cellspacing="2" border="0">
  <tr>
    <td class="labelStyle" width="16%"><s:text
	name="label.common.copyInstallDate" />
      :</td>
    <td width="18%"><sd:datetimepicker name='copyInstallDate' id='installDate' onchange='copyInstallDate(true)' value='%{copyInstallDate}' /></td>
    <td width="4%"><input type="checkbox" id="checkboxInstallDate"
	name="checkboxInstallDate" />
    </td>
    <td width="62%">&nbsp;</td>
  </tr>
</table>
<script type="text/javascript">
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("checkboxInstallDate"), "onclick", function(value) {                       
                    	copyInstallDate(false);                	
                    });
                });
                        </script>
