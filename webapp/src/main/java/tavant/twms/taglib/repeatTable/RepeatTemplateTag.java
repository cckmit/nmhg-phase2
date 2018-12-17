package tavant.twms.taglib.repeatTable;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @see RepeatTemplate
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class RepeatTemplateTag extends AbstractClosingTag {
    private String index = "index"; //default value is index
    private int batchSize = 1;
    private String startFrom = "0";

    public static final String TEMPLATE_CODE = "repeatTable.template";

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new RepeatTemplate(stack, request, response);
    }

    @Override
    public void populateParams() {
        super.populateParams();
        RepeatTemplate template = (RepeatTemplate) component;
        template.setIndex(index);
        template.setBatchSize(batchSize);
        template.setStartFrom(startFrom);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        if (index != null) {
            this.index = index;
        }
    }

    public String getBatchSize() {
        return Integer.toString(batchSize);
    }

    public void setBatchSize(String batchSize) {
        // An error in conversion is a programing error, and a NumberFormatException
        // will be raised. This is deliberately unhandled.
        this.batchSize = Integer.parseInt(batchSize);
    }

    public String getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(String startFrom) {
        this.startFrom = startFrom;
    }

    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }

    @Override
    public int doAfterBody() throws JspException {
        if (pageContext.getRequest().getAttribute(TEMPLATE_CODE) == null) {
            String template = getTemplateText(bodyContent.getString());
            pageContext.getRequest().setAttribute(TEMPLATE_CODE, template);
            bodyContent.clearBody();
        } else {
            String body = bodyContent.getString();
            int currentIndex = ((RepeatTemplate) component).getCurrentIndex();
            body = body.replaceAll("#" + index, Integer.toString(currentIndex));
            try {
                bodyContent.getEnclosingWriter().write(body);
            } catch (Exception e) {
                throw new JspException(e.getMessage());
            }
            bodyContent.clearBody();
        }
        boolean again = component.end(pageContext.getOut(), getBody());

        if (again) {
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e.getMessage());
                }
            }
            pageContext.getRequest().setAttribute(TEMPLATE_CODE, null);
            return SKIP_BODY;
        }
    }

    private String getTemplateText(String body) {
        // Replace all occurences of name="parent.collection().property"
        // with name="parent.collection.makeNew[#index].property"
        String reg = "name=\"" + value + "\\(\\)\\.";
        String rep = "name=\"" + value + ".makeNew[#" + index + "].";
        body = body.replaceAll(reg, rep);

        // Replace all occurences of name="parent.collection()" with name=""
        reg = "name=\"" + value + "\\(\\)\"";
        rep = "name=\"\"";
        return body.replaceAll(reg, rep);
    }
}
