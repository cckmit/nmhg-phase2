package tavant.twms.domain.inventory;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="option_info")
public class Option {
	@Id
	@GeneratedValue(generator = "Option")
	@GenericGenerator(name = "Option", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "OPTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String optionCode;

	private String optionType;

	private String orderOptionLineNumber;

	private String optionDescription;

	private BigDecimal optionGrossPrice;

	private BigDecimal optionNetPrice;

	private BigDecimal optionGrossValue;

	private BigDecimal optionDiscountValue;

	private BigDecimal optionNetValue;

	private BigDecimal optionDiscountPercent;

	private String activeInactiveStatus;

	private String specialOptionStatus;

	private String dieselTier;

    private String mastType;

    private String tireType;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOptionCode() {
		return optionCode;
	}
	public void setOptionCode(String optionCode) {
		this.optionCode = optionCode;
	}
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public String getOrderOptionLineNumber() {
		return orderOptionLineNumber;
	}
	public void setOrderOptionLineNumber(String orderOptionLineNumber) {
		this.orderOptionLineNumber = orderOptionLineNumber;
	}
	public String getOptionDescription() {
		return optionDescription;
	}
	public void setOptionDescription(String optionDescription) {
		this.optionDescription = optionDescription;
	}
	public BigDecimal getOptionGrossPrice() {
		return optionGrossPrice;
	}
	public void setOptionGrossPrice(BigDecimal optionGrossPrice) {
		this.optionGrossPrice = optionGrossPrice;
	}
	public BigDecimal getOptionNetPrice() {
		return optionNetPrice;
	}
	public void setOptionNetPrice(BigDecimal optionNetPrice) {
		this.optionNetPrice = optionNetPrice;
	}
	public BigDecimal getOptionGrossValue() {
		return optionGrossValue;
	}
	public void setOptionGrossValue(BigDecimal optionGrossValue) {
		this.optionGrossValue = optionGrossValue;
	}
	public BigDecimal getOptionDiscountValue() {
		return optionDiscountValue;
	}
	public void setOptionDiscountValue(BigDecimal optionDiscountValue) {
		this.optionDiscountValue = optionDiscountValue;
	}
	public BigDecimal getOptionNetValue() {
		return optionNetValue;
	}
	public void setOptionNetValue(BigDecimal optionNetValue) {
		this.optionNetValue = optionNetValue;
	}
	public BigDecimal getOptionDiscountPercent() {
		return optionDiscountPercent;
	}
	public void setOptionDiscountPercent(BigDecimal optionDiscountPercent) {
		this.optionDiscountPercent = optionDiscountPercent;
	}
	public String getActiveInactiveStatus() {
		return activeInactiveStatus;
	}
	public void setActiveInactiveStatus(String activeInactiveStatus) {
		this.activeInactiveStatus = activeInactiveStatus;
	}
	public String getSpecialOptionStatus() {
		return specialOptionStatus;
	}
	public void setSpecialOptionStatus(String specialOptionStatus) {
		this.specialOptionStatus = specialOptionStatus;
	}
	public String getDieselTier() {
		return dieselTier;
	}
	public void setDieselTier(String dieselTier) {
		this.dieselTier = dieselTier;
	}

    public String getMastType() {
        return mastType;
    }

    public void setMastType(String mastType) {
        this.mastType = mastType;
    }

    public String getTireType() {
        return tireType;
    }

    public void setTireType(String tireType) {
        this.tireType = tireType;
    }
}
