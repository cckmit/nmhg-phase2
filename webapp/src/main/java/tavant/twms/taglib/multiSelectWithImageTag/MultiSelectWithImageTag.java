package tavant.twms.taglib.multiSelectWithImageTag;

import org.apache.struts2.views.jsp.ui.AbstractRequiredListTag;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Apr 1, 2010
 * Time: 7:05:08 PM
 * To change this template use File | Settings | File Templates.
 */
/* This tag is ude for displaying the list of checkbox/radio alongwith images or properties.
   @param action: the url which takes care of streaming the image content
   @param property: the additonal property to be displayed along with listValue
   @param inputType: the input type (i.e checkbox/radio) to be displayed for this tag
   @param docId: the expression to fetch the doc from the stack (i.e with reference to object)
*/
public class MultiSelectWithImageTag extends AbstractRequiredListTag {
    private String action;
    private String property;
    private String inputType;
    private String docId;

    public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new MultiSelectWithImage(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        MultiSelectWithImage multiSelectWithImage = (MultiSelectWithImage) this.component;
        multiSelectWithImage.setAction(this.action);
        multiSelectWithImage.setProperty(this.property);
        multiSelectWithImage.setInputType(this.inputType);
        multiSelectWithImage.setDocId(this.docId);
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
