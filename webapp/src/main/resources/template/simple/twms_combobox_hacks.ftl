<#--this file is not all Hacks(just name swaping funda is a hack)... actually it contains some onchange related 
code and tabindex implementation as well.. but i had to place it all togather... because otherwise it would have 
affected performance-->
dojo.addOnLoad( function() {
 var ${parameters.id?html}_widget = dijit.byId("${parameters.id?html}");
<#if (parameters.id?exists && parameters.tabIndex?exists)>
 var input_${parameters.id?html} = dojo.query("input", ${parameters.id?html}_widget.domNode)[0];
 input_${parameters.id?html}.setAttribute(tabindex, "${parameters.tabindex?html}");
 delete input_${parameters.id?html};
</#if>
<#if !(parameters.readOnly?exists && parameters.readOnly == true)>
<#--HACK: This wierdo logic here, interchanges the names of first hidden input of the combobox widget
 with the second one.. this is done because the combobox seems to name the hidden input holding the 
 displayed value of the field as the name given in the select box, and the field holding the key(option*s value)
 is named something else... so i interchange the names, so as to submit the option*s value and not the displayed value.-->
 var firstInput_${parameters.id?html} = dojo.query("input", ${parameters.id?html}_widget.domNode)[0];
 var keyHolder_${parameters.id?html} = dojo.dom.getNextSiblingElement(firstInput_${parameters.id?html}, "input");
 var name_${parameters.id?html} = firstInput_${parameters.id?html}.name;
 firstInput_${parameters.id?html}.name = keyHolder_${parameters.id?html}.name;
 keyHolder_${parameters.id?html}.name = name_${parameters.id?html};
 keyHolder_${parameters.id?html}.id = "${parameters.id?html}";
<#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
 ${parameters.id?html}_record = keyHolder_${parameters.id?html}.value;
 bindOnChange(keyHolder_${parameters.id?html}, ${parameters.id?html}_compairer, new Array("timebased"));
</#if>
 delete firstInput_${parameters.id?html}, keyHolder_${parameters.id?html}, name_${parameters.id?html};
</#if>
 delete ${parameters.id?html}_widget;
});