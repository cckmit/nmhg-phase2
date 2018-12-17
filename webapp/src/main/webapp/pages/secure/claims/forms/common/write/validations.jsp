<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");
   	dojo.require("dijit.layout.ContentPane");   
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("twms.widget.Dialog"); 
</script>

<div dojoType="twms.widget.Dialog" id="validations" bgColor="white" style="width:95%; height:90%;" 
    title="<s:text name="button.common.validate"/>" >
	<div dojoType="dijit.layout.LayoutContainer" style="height:450px;overflow-y: auto; overflow-x: auto;">	
        <div dojoType="dojox.layout.ContentPane" layoutAlign="client" id="validations_data" 
			executeScripts="true" renderStyles="true">
            <%-- Validations come here are an AJAX response --%>
        </div>
	 </div>	

</div>

<script type="text/javascript">
    dojo.addOnLoad(function() {

        var validationDialog = dijit.byId("validations");
        var validationIndicator = "<center><img src=\"image/indicator.gif\" class=\"indicator\"/>Validating ...</center>";

        var form = document.getElementById("claim_form");

        dojo.connect(dojo.byId("validateButton"), "onclick", function(/*Event*/ event) {
            form.validateAndCompute = true;
        });

        dojo.connect(form, "submit", function(/*Event*/ event) {
            if (form.validateAndCompute) {
                dojo.stopEvent(event);
                var validations = dijit.byId("validations_data");
                form.validateAndCompute = false;
                validationDialog.show();
                validations.setContent(validationIndicator);

                var content = {};
                var validateButton = dojo.byId("validateButton");
                content[validateButton.name] = validateButton.value;

                dojo.xhrPost({
                    form: form,
                    content: content,
                    load: function(data) {
                        validations.setContent(data);
                    },
                    error: function(error, ioArgs) {
                        validations.setContent(ioArgs.xhr.responseText);
                    }
                });
            }
        });
    });
</script>
