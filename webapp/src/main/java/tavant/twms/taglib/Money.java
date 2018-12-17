package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dinesh.kk
 *
 */
public class Money extends UIBean{

    public Money(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    final public static String TEMPLATE = "twms_money";


    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        String symbol = null;
        super.evaluateExtraParams();
        // for deriving the symbol from the money object
        if(value!=null){
            symbol= value.substring(0, value.length()-1);
            if(symbol.equalsIgnoreCase("%{acceptedTotal"))
            {
            symbol= symbol+".breachEncapsulationOfCurrency().getSymbol()}";	
            }
            else
            {
            symbol= symbol+".breachEncapsulationOfCurrency().currencyCode}";
            }
            symbol = findString(symbol);

            // setting the default value for symbol if the money is null
            if("".equals(symbol)){
                if (defaultSymbol != null) {
                    symbol = findString(defaultSymbol); // Evaluate default symbol from the stack
                } else {
                    throw new IllegalStateException("value '" + value + "' seems to be NULL and defaultSymbol is not set");
                }
            }
            addParameter("symbol", symbol);

            // for deriving the amount from the money object string
            String nameval =  value.substring(0, value.length()-1);
            nameval = nameval+".breachEncapsulationOfAmount()}";
            nameval = findString(nameval);
            addParameter("nameValue", nameval);
        }
        if(size!=null){
            size = findString(size);
            addParameter("fieldsize", size);
        }
        if(maxlength!=null){
            maxlength = findString(maxlength);
            addParameter("maxlength", maxlength);
        }
        if(disabled!=null && "true".equals(disabled)){
            addParameter("disabled", "true");
        }
    }

    private String defaultSymbol;

//    private String symbol;

    private String size;

    private String maxlength;

    private String disabled;

//    public void setSymbol(String symbol) {
//        this.symbol = symbol;
//    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDefaultSymbol(String defaultSymbol) {
        this.defaultSymbol = defaultSymbol;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }
}
