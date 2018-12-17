package tavant.twms.taglib;

import org.apache.struts2.views.jsp.ui.AbstractUITag;

/**
 * @see InputComponent
 *
 * @author janmejay.singh
 */
public abstract class InputComponentTag extends AbstractUITag {

    private String readOnlyCssClass;
    private String readOnly;
    private String publishOnChange;

    protected void populateParams() {
        super.populateParams();
        InputComponent field = (InputComponent) component;
        field.setReadOnlyCssClass(readOnlyCssClass);
        field.setReadOnly(readOnly);
        field.setPublishOnChange(publishOnChange);
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getReadOnlyCssClass() {
        return readOnlyCssClass;
    }

    public void setReadOnlyCssClass(String readOnlyCssClass) {
        this.readOnlyCssClass = readOnlyCssClass;
    }

    public String getPublishOnChange() {
        return publishOnChange;
    }

    public void setPublishOnChange(String publishOnChange) {
        this.publishOnChange = publishOnChange;
    }
}
