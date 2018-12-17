package tavant.twms.taglib.summaryTable;

import org.apache.struts2.views.jsp.ui.AbstractClosingTag;
import org.apache.struts2.components.Component;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see SummaryTable
 *
 * @author janmejay.singh
 */
public class SummaryTableTag extends AbstractClosingTag {

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

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new SummaryTable(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        SummaryTable table = (SummaryTable) component;
        table.setBodyUrl(bodyUrl);
        table.setPreviewUrl(previewUrl);
        table.setDetailUrl(detailUrl);
        table.setEventHandlerClass(eventHandlerClass);
        table.setExtraParamsVar(extraParamsVar);
        table.setExtraParamsFunctions(extraParamsFunctions);
        table.setPreviewPaneId(previewPaneId);
        table.setFolderName(folderName);
        table.setMultiSelect(multiSelect);
        table.setParentSplitContainerId(parentSplitContainerId);
        table.setPopulateCriteriaDataOn(populateCriteriaDataOn);
        table.setRootLayoutContainerId(rootLayoutContainerId);
        table.setButtonContainerId(buttonContainerId);
        table.setEnableTableMinimize(enableTableMinimize);
        table.setUseDefaultTheme(useDefaultTheme);
    }

    public void setBodyUrl(String bodyUrl) {
        this.bodyUrl = bodyUrl;
    }

    public void setMultiSelect(String multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setParentSplitContainerId(String parentSplitContainerId) {
        this.parentSplitContainerId = parentSplitContainerId;
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
