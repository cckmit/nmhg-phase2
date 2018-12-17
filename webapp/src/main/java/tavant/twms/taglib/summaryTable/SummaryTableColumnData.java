package tavant.twms.taglib.summaryTable;

/**
 * @author janmejay.singh
 */
public class SummaryTableColumnData {
    
    private String label;
    private String id;
    private double width;
    private String dataType;
    private boolean idColumn;
    private boolean labelColumn;
    private boolean cssColumn;
    private boolean hidden;
    private String rendererClass;
    private boolean disableSorting;
    private boolean disableFiltering;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isIdColumn() {
        return idColumn;
    }

    public void setIdColumn(boolean idColumn) {
        this.idColumn = idColumn;
    }

    public boolean isLabelColumn() {
        return labelColumn;
    }

    public void setLabelColumn(boolean labelColumn) {
        this.labelColumn = labelColumn;
    }

    public boolean isCssColumn() {
        return cssColumn;
    }

    public void setCssColumn(boolean cssColumn) {
        this.cssColumn = cssColumn;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getAlignment() {
        return dataType.equalsIgnoreCase("String") ? "left" : "right";
    }

    public String getRendererClass() {
        return rendererClass;
    }

    public void setRendererClass(String rendererClass) {
        this.rendererClass = rendererClass;
    }
    public boolean isDisableSorting() {
        return disableSorting;
    }

    public void setDisableSorting(boolean disableSorting) {
        this.disableSorting = disableSorting;
    }

    public boolean isDisableFiltering() {
        return disableFiltering;
    }

    public void setDisableFiltering(boolean disableFiltering) {
        this.disableFiltering = disableFiltering;
    }
}
