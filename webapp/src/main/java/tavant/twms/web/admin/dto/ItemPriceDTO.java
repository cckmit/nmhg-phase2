package tavant.twms.web.admin.dto;

import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.catalog.Item;

import java.math.BigDecimal;

public class ItemPriceDTO {

    private Item item;

    private CalendarDate fromDate;

    private CalendarDate tillDate;

    private BigDecimal newValue;

    private String error;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }

    public void setNewValue(BigDecimal newValue) {
        this.newValue = newValue;
    }

    public CalendarDate getTillDate() {
        return tillDate;
    }

    public void setTillDate(CalendarDate tillDate) {
        this.tillDate = tillDate;
    }

    public CalendarDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(CalendarDate toDate) {
        this.fromDate = toDate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
