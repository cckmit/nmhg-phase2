/**
 *
 */
package tavant.twms.web.admin.upload.convertor;

import com.domainlanguage.money.Money;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * @author kaustubhshobhan.b
 *
 */
public class MoneyConvertor implements Convertor {

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.web.xls.reader.Convertor#convert(java.lang.Object)
     */
    public Object convert(Object object) {
        String moneyString = (String)object;
        Currency currency = Currency.getInstance(Locale.US);
        Money money = null;
        if(moneyString!=null){
         BigDecimal num = new BigDecimal(moneyString);
         try {
            money = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
         } catch (RuntimeException e) {
            ConversionErrors.getInstance().addError("Cannot convert [" + moneyString
                    + "] to Money");
         }
        }
        return money;
    }

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.web.xls.reader.Convertor#convertWithDependency(java.lang.Object,
     *      java.lang.Object)
     */
    public Object convertWithDependency(Object object, Object dependency) {
        return new UnsupportedOperationException();

    }

}
