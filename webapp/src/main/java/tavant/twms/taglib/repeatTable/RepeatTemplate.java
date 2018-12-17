package tavant.twms.taglib.repeatTable;

import static tavant.twms.taglib.TaglibUtil.seperateMarkupAndScript;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.util.MakeIterator;
import tavant.twms.taglib.TaglibUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Iterator;

/**
 * Creates a repeat table, which can add new rows and delete existing once.
 *
 * @author janmejay.singh
 */
public class RepeatTemplate extends ClosingUIBean {

    public static final String REPEAT_TABLE_COLLECTION_NAME = "RepeatTableCollectionName";
    final public static String OPEN_TEMPLATE = "repeatTemplate";
    final public static String TEMPLATE = "repeatTemplate-close";

    private Iterator iterator;

    private String markup;
    private String script;
    private String repeatTableId;
    private String index;
    private int batchSize;
    private String startFrom;
    private int curr = 0;
    private Object oldIndexValue = null;
    private Object oldCollectionNameValue = null;

    public RepeatTemplate(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        repeatTableId = TaglibUtil.getRepeatTableId(request);
        if(repeatTableId == null) {
            throw new IllegalStateException("The repeat-add tag can not be used without the repeat tag.(repeatTableId is null.)");
        }
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("markup", markup);
        addParameter("script", script);
        addParameter("repeatTableId", repeatTableId);
        addParameter("indexInitial", curr-1);
        addParameter("index", index);
        addParameter("collectionName", value);
        addParameter("batchSize", batchSize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean start(Writer writer) {
        // we are going to redefine these values, hence back up the
        // old value, so that we can reset it after we are done
        oldIndexValue = stack.getContext().get(index);
        oldCollectionNameValue = stack.getContext().get(REPEAT_TABLE_COLLECTION_NAME);

        if (value == null) {
            value = "top";
        }
        stack.getContext().put(REPEAT_TABLE_COLLECTION_NAME, value);
        iterator = MakeIterator.convert(findValue(value));
        int n = ((Integer) findValue(startFrom)).intValue();
        for (int i = 0; i < n; i++) {
            iterator.next();
            curr++;
        }
        super.start(writer);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean end(Writer writer, String body) {
        String[] temp = seperateMarkupAndScript((String) request.getAttribute(RepeatTemplateTag.TEMPLATE_CODE), true);
        this.markup = temp[0];
        this.script = temp[1];
        if (iterator!=null && iterator.hasNext()) {
            setNextOnStack();
            return true;
        } else {
            // resetting these to their original values
            stack.getContext().put(REPEAT_TABLE_COLLECTION_NAME, oldCollectionNameValue);
            stack.getContext().put(index, oldIndexValue);
            super.end(writer, "");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    void setNextOnStack() {
        Object currentValue = iterator.next();
        stack.push(currentValue);
        stack.getContext().put(index, curr);
        curr++;
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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getCurrentIndex() {
        return curr-1;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setStartFrom(String startFrom) {
        this.startFrom = startFrom;
    }
}