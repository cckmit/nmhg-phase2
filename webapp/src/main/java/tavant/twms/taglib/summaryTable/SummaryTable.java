package tavant.twms.taglib.summaryTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import static tavant.twms.taglib.TaglibUtil.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import static java.lang.Boolean.parseBoolean;
import java.util.List;

/**
 * This tag generates the SummaryTable.
 *
 * @author janmejay.singh
 */
public class SummaryTable extends ClosingUIBean {

    private String bodyUrl;
    private String folderName;
    private String extraParamsVar;
    private String eventHandlerClass;
    private String previewUrl;
    private String detailUrl;
    private String previewPaneId;
    private String multiSelect;
    private String parentSplitContainerId;
    private String populateCriteriaDataOn;
    private String extraParamsFunctions;
    private String rootLayoutContainerId;
    private String buttonContainerId;
    private String enableTableMinimize;
    private String useDefaultTheme;

    private boolean isIE;

    private static final String TEMPLATE = "summaryTable-close",
                                OPEN_TEMPLATE = "summaryTable";
    
    private boolean tagWasUsedBefore;
    private List<SummaryTableColumnData> columns;

    protected SummaryTable(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
        this.isIE = isIE(httpServletRequest);
        this.tagWasUsedBefore = isUsedBefore(request, this.getClass());
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        setSummaryTableId(stack, id);
        addParameter("bodyUrl", findString(bodyUrl));
        addParameter("folderName", findString(folderName));
        if(populateCriteriaDataOn != null) {
            addParameter("populateCriteriaDataOn", findString(populateCriteriaDataOn));
        }
        if(extraParamsVar != null) {
            addParameter("extraParamsVar", findString(extraParamsVar));
        } else {
            addParameter("extraParamsVar", "{}");
        }
        if(extraParamsFunctions != null) {
            addParameter("extraParamsFunctions", findString(extraParamsFunctions));
        } else {
            addParameter("extraParamsFunctions", "{}");
        }
        if(previewUrl != null) {
            addParameter("previewUrl", findString(previewUrl));
        }
        if(detailUrl != null) {
            addParameter("detailUrl", findString(detailUrl));
        }
        if (previewPaneId != null) {//no point is preview pane... if not previewable
            addParameter("previewPaneId", findString(previewPaneId));
        }
        boolean multiSelect = getBoolean(findValue(this.multiSelect));
        String defaultEventHandlerClass = "tavant.twms.summaryTable.BasicEventHandler";
        if(multiSelect) defaultEventHandlerClass = "tavant.twms.summaryTable.MultiSelectSampleEventHandler";
        addParameter("multiSelect", multiSelect);
        addParameter("eventHandlerClass", (eventHandlerClass != null) ?
                                          findString(eventHandlerClass) : defaultEventHandlerClass);
        addParameter("isIE", isIE);
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        if(columns != null) {
            addParameter("columns", columns);
            doSanityChecks();
        }
        addParameter("totalRecords", "<span id=\"" + getId() + "_totalRecords\"></span>");
        addParameter("pageNoSpan", "<span id=\"" + getId() + "_pageNumber\"></span>");
        addParameter("totalPagesSpan", "<span id=\"" + getId() + "_totalPages\"></span>");
        addParameter("parentSplitContainerId", findString(parentSplitContainerId));
        boolean enableTableMinimize = this.enableTableMinimize != null && parseBoolean(this.enableTableMinimize);
        addParameter("enableTableMinimize", enableTableMinimize);
        if (enableTableMinimize) {
            //if minimize is enabled, both the id's shd be given to the component.
            if (rootLayoutContainerId == null || buttonContainerId == null) {
                throw new IllegalStateException("Table minimize cannot be enabled unless both 'rootLayoutContainerId'" +
                        " and 'buttonContainerId' are provided.");
            }
            addParameter("rootLayoutContainerId", findString(rootLayoutContainerId));
            addParameter("buttonContainerId", findString(buttonContainerId));
        }
        final boolean injectThemeCss = useDefaultTheme != null && parseBoolean(useDefaultTheme);
        addParameter("injectThemeCss", injectThemeCss);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;  
    }

    @Override
    public boolean end(Writer writer, String string) {
        columns = getSummaryTableColumns(stack);
        return super.end(writer, string);
    }

    private void doSanityChecks() {
        int idColumns = 0, labelColumns = 0, cssColumns = 0;
        for(SummaryTableColumnData col : columns) {
            if(col.isIdColumn()) idColumns++;//id column encountered
            if(col.isLabelColumn()) labelColumns++;//label column encountered
            if(col.isCssColumn()) {//css column encountered
                cssColumns++;
                assert !col.isIdColumn();//css column can't be id column
                assert !col.isLabelColumn();//css column can't be label column
            }
            assert idColumns == 1;//only one id column makes sense
            assert labelColumns == 1;//only one label column makes sense
            assert cssColumns <= 1;//only one css column is allowed at the max(there may be none)
        }
    }

    public void setParentSplitContainerId(String parentSplitContainerId) {
        this.parentSplitContainerId = parentSplitContainerId;
    }

    public void setBodyUrl(String bodyUrl) {
        this.bodyUrl = bodyUrl;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setExtraParamsVar(String extraParamsVar) {
        this.extraParamsVar = extraParamsVar;
    }

    public void setEventHandlerClass(String eventHandlerClass) {
        this.eventHandlerClass = eventHandlerClass;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public void setPreviewPaneId(String previewPaneId) {
        this.previewPaneId = previewPaneId;
    }

    public void setMultiSelect(String multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setPopulateCriteriaDataOn(String populateCriteriaDataOn) {
        this.populateCriteriaDataOn = populateCriteriaDataOn;
    }

    public void setExtraParamsFunctions(String extraParamsFunctions) {
        this.extraParamsFunctions = extraParamsFunctions;
    }

    public void setRootLayoutContainerId(String rootLayoutContainerId) {
        this.rootLayoutContainerId = rootLayoutContainerId;
    }

    public void setButtonContainerId(String buttonContainerId) {
        this.buttonContainerId = buttonContainerId;
    }

    public void setEnableTableMinimize(String enableTableMinimize) {
        this.enableTableMinimize = enableTableMinimize;
    }

    public void setUseDefaultTheme(String useDefaultTheme) {
        this.useDefaultTheme = useDefaultTheme;
    }
}
