package tavant.twms.taglib.nlist;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.util.MakeIterator;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class NList extends ClosingUIBean {
    public static final String INDEX_VARIABLE = "nListIndex";
    public static final String FQ_NAME_VARIABLE = "nListName";

    final public static String OPEN_TEMPLATE = "nList";
    final public static String TEMPLATE = "nList-close";

    private String rowTemplateUrl;
    private String paramsVar;
    private Iterator iterator;
    private int currentIndex = 0;
    private String fqName;
    private Object parentIndexBackup;
    private Object parentNameBackup;

    public NList(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        addParameter("rowTemplateUrl", rowTemplateUrl);
        addParameter("paramsVar", paramsVar);
        addParameter("collectionName", fqName);
    }

    public boolean start(Writer writer) {
        if (value == null) {
            value = "top";
        }

        iterator = MakeIterator.convert(findValue(value));

        backupParentVariables();
        determineFQName();

        // Processes the first element of the iterator only. Remaining elements are all processed by the end(...)
        // method, when it's invoked in a loop (See NListTag.doAfterBody(..))
        return processAndSetCurrentValue(writer);
    }

    public boolean end(Writer writer, String body) {

        if (iterator != null) {
            stack.pop();
        }

        return processAndSetCurrentValue(writer);
    }

    @SuppressWarnings("unchecked")
    private boolean processAndSetCurrentValue(Writer writer) {
        
        if ((iterator != null) && iterator.hasNext()) {
            Object currentValue = iterator.next();

            stack.push(currentValue);
            String fqNameWithIndex = getFQNameWithIndex();
            stack.getContext().put(INDEX_VARIABLE, currentIndex);
            stack.getContext().put(FQ_NAME_VARIABLE, fqNameWithIndex);
            ActionContext.getContext().put(INDEX_VARIABLE, currentIndex);
            ActionContext.getContext().put(FQ_NAME_VARIABLE, fqNameWithIndex);

            currentIndex++;

            return true;
        } else {
            addParameter("nextAvailableIndex", currentIndex);
            restoreParentVariables();
            
            super.end(writer, "");
            return false;
        }
    }
    
    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    public String getRowTemplateUrl() {
        return rowTemplateUrl;
    }

    public void setRowTemplateUrl(String rowTemplateUrl) {
        this.rowTemplateUrl = rowTemplateUrl;
    }

    public String getParamsVar() {
        return paramsVar;
    }

    public void setParamsVar(String paramsVar) {
        this.paramsVar = paramsVar;
    }

    protected String getFQNameWithIndex() {
        return new StringBuilder(fqName).append("[").append(currentIndex)
                .append("]").toString();
    }

    protected void determineFQName() {
        StringBuilder nameBuilder = new StringBuilder(50);

        String parentNameBackupAsString = (parentNameBackup != null) ? parentNameBackup.toString() : "";
        if (StringUtils.hasText(parentNameBackupAsString)) {
            nameBuilder.append(parentNameBackupAsString);
            nameBuilder.append(".");
        }

        fqName = nameBuilder.append(value).toString();
    }

    private void backupParentVariables() {
        parentIndexBackup = ActionContext.getContext().get(INDEX_VARIABLE);
        parentNameBackup = ActionContext.getContext().get(FQ_NAME_VARIABLE);
    }

    @SuppressWarnings("unchecked")
    private void restoreParentVariables() {
        // put the parent variables back on to stack, most JSP's are accessing it from stack
        stack.getContext().put(INDEX_VARIABLE, parentIndexBackup);
        stack.getContext().put(FQ_NAME_VARIABLE, parentNameBackup);
        ActionContext.getContext().put(INDEX_VARIABLE, parentIndexBackup);
        ActionContext.getContext().put(FQ_NAME_VARIABLE, parentNameBackup);
    }
}