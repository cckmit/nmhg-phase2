package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Int
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class IntTag extends InputComponentTag {
    
    private String maxLimit;
    private String minLimit;
    private String separator;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Int(stack, request, response);
    }
    
    @Override
    public void populateParams() {
        super.populateParams();
        Int field = (Int) component;
        field.setMaxLimit(maxLimit);
        field.setMinLimit(minLimit);
        field.setSeparator(separator);
    }

    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(String minLimit) {
        this.minLimit = minLimit;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
