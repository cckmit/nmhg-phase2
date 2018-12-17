package tavant.twms.taglib.summaryTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;
import static tavant.twms.taglib.TaglibUtil.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author janmejay.singh
 */
public class Column extends UIBean {

    private String width;
    private String labelColumn;
    private String idColumn;
    private String hidden;
    private String dataType;
    private String cssColumn;
    private String rendererClass;
    private String disableSorting;
    private String disableFiltering;

    private static final String TEMPLATE = "summaryTableColumn";

    public Column(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        SummaryTableColumnData data = new SummaryTableColumnData();
        data.setWidth(getDouble(findValue(width)));
        data.setLabelColumn(getBoolean(findValue(labelColumn)));
        data.setIdColumn(getBoolean(findValue(idColumn)));
        data.setHidden(getBoolean(findValue(hidden)));
        data.setCssColumn(getBoolean(findValue(cssColumn)));
        data.setRendererClass(rendererClass != null ?
            findString(rendererClass) : "tavant.twms.summaryTable.DefaultCellRenderer");
        if(dataType != null) {
            data.setDataType(findString(dataType));
        } else {
            data.setDataType("String");
        }
        data.setLabel(findString("%{getText('" + findString(label) + "')}"));
        data.setId(id);
        data.setDisableFiltering(getBoolean(findValue(disableFiltering)));
        data.setDisableSorting(getBoolean(findValue(disableSorting)));
        addParameter("width", data.getWidth());
        addParameter("labelColumn", data.isLabelColumn());
        addParameter("idColumn", data.isIdColumn());
        addParameter("hidden", data.isHidden());
        addParameter("cssColumn", data.isCssColumn());
        addParameter("dataType", data.getDataType());
        addParameter("label", data.getLabel());
        addSummaryTableColumn(stack, data);
        addParameter("summaryTableId", getSummaryTableId(getStack()));
        addParameter("disableSorting", data.isDisableSorting());
        addParameter("disableFiltering", data.isDisableFiltering());
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setLabelColumn(String labelColumn) {
        this.labelColumn = labelColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setCssColumn(String cssColumn) {
        this.cssColumn = cssColumn;
    }

    public void setRendererClass(String rendererClass) {
        this.rendererClass = rendererClass;
    }

    public void setDisableSorting(String disableSorting) {
        this.disableSorting = disableSorting;
    }

    public void setDisableFiltering(String disableFiltering) {
        this.disableFiltering = disableFiltering;
    }

}