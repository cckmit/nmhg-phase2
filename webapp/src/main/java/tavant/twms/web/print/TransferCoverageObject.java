package tavant.twms.web.print;

import com.domainlanguage.money.Money;

public class TransferCoverageObject {

	private String code;

	private Money price;

	private String serialNumber;

	public TransferCoverageObject(String serialNumber, String code, Money price) {
		this.serialNumber = serialNumber;
		this.code = code;
		this.price = price;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}
