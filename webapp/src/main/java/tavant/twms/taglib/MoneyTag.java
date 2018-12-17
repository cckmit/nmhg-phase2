package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MoneyTag extends AbstractUITag{

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Money(stack, req, res);
    }
    @Override
    protected void populateParams() {
        super.populateParams();
        Money money = (Money) component;
        // money.setSymbol(defaultSymbol);
        money.setDefaultSymbol(defaultSymbol);
        money.setSize(size);
        money.setMaxlength(maxlength);
        money.setDisabled(disabled);
    }
    private String defaultSymbol;

    private String size;

    private String maxlength;

    private String disabled;

    public void setDefaultSymbol(String symbol) {
        this.defaultSymbol = symbol;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setSize(String size) {
        this.size = size;
    }
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

}
