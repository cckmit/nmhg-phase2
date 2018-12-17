dojo.declare("tavant.twms.summaryTableExt.ImageRenderer", tavant.twms.summaryTable.DefaultCellRenderer, {
    render : function(/*cell*/ td, /*data(to be rendered)*/ valueMap) {
        var elementMarkup = "<span title='${title}'><img src='${url}'/></span>";
        var element = dojo.string.substitute(elementMarkup, valueMap);
        td.appendChild(dojo.toDom(element));
    }
});