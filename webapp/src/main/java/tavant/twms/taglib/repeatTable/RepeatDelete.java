package tavant.twms.taglib.repeatTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tag for putting the add button for the table to add new rows.
 * (To be used inside the Repeat).
 * 
 * @author janmejay.singh
 */
public class RepeatDelete extends ClosingUIBean {
    
    private String id;
    
    final public static String OPEN_TEMPLATE = "repeatDelete";
    final public static String TEMPLATE = "repeatDelete-close";
    
    protected RepeatDelete(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("id", id);//to make it not evaluate against stack.
        addParameter("collectionName", stack.getContext().get(RepeatTemplate.REPEAT_TABLE_COLLECTION_NAME));
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
