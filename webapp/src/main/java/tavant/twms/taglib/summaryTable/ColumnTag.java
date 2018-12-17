package tavant.twms.taglib.summaryTable;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Column
 * @author janmejay.singh
 */
public class ColumnTag extends AbstractUITag {

    private String width;
    private String labelColumn;
    private String idColumn;
    private String hidden;
    private String dataType;
    private String cssColumn;
    private String rendererClass;
    private String disableSorting;
    private String disableFiltering;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new Column(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Column column = (Column) component;
        column.setWidth(width);
        column.setLabelColumn(labelColumn);
        column.setHidden(hidden);
        column.setDataType(dataType);
        column.setIdColumn(idColumn);
        column.setCssColumn(cssColumn);
        column.setRendererClass(rendererClass);
        column.setDisableSorting(disableSorting);
        column.setDisableFiltering(disableFiltering);
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
