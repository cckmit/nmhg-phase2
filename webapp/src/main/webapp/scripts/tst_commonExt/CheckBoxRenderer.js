var childCheckBoxName;
dojo.declare("tavant.twms.summaryTableExt.CheckBoxRenderer", tavant.twms.summaryTable.DefaultCellRenderer, {
    render : function(/*cell*/ td, /*data(to be rendered)*/ valueMap) {
        var elementMarkup = "<span title='${toolTip}'><input name='${name}' value='${value}' type='checkbox' ${disabled} onclick='childCheckBoxChanged(this)'></span>";
        var element = dojo.string.substitute(elementMarkup, valueMap);
        td.appendChild(dojo.html.createNodesFromText(element, true));
        if(!childCheckBoxName)childCheckBoxName=valueMap['name'];
    }
});

