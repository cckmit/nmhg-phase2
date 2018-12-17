package tavant.twms.taglib.repeatTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import tavant.twms.taglib.TaglibUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;


/**
 * Creates a repeat table, which can add new rows and delete existing once.
 * 
 * @author janmejay.singh
 */
public class Repeat extends ClosingUIBean {
    
    public static final  String OPEN_TEMPLATE = "repeat";
    public static final String TEMPLATE = "repeat-close";
    
    private boolean tagWasUsedBefore = false; 
    
    public Repeat(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
    }
    
    @Override
    public boolean start(Writer writer) {
        TaglibUtil.setRepeatTableId(request, getId());
        return super.start(writer);
    }
    
    @Override
    public boolean end(Writer writer, String body) {
        TaglibUtil.setRepeatTableId(request, null);
        return super.end(writer, body);
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
    public void evaluateParams() {
        super.evaluateParams();
        if (this.cellpadding != null) {
            cellpadding = findString(this.cellpadding);
            addParameter("cellpadding", cellpadding);
        }
        if (this.cellspacing != null) {
            cellspacing = findString(this.cellspacing);
            addParameter("cellspacing", cellspacing);
        }
        if (this.width != null) {
            width = findString(this.width);
            addParameter("width", width);
        }
    }
        
    
    private String cellpadding;
    private String cellspacing;
    private String width;

    public void setCellpadding(String cellpadding) {
        this.cellpadding = cellpadding;
    }

    public void setCellspacing(String cellspacing) {
        this.cellspacing = cellspacing;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
