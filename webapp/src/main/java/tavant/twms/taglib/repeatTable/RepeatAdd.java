package tavant.twms.taglib.repeatTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import tavant.twms.taglib.TaglibUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tag for putting the add button for the table to add new rows.
 * (To be used inside the Repeat).
 * 
 * @author janmejay.singh
 */
public class RepeatAdd extends ClosingUIBean {
    
    final public static String OPEN_TEMPLATE = "repeatAdd";
    final public static String TEMPLATE = "repeatAdd-close";
    
    private String repeatTableId;

    protected RepeatAdd(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        repeatTableId = TaglibUtil.getRepeatTableId(request);
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("repeatTableId", repeatTableId);
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

}
