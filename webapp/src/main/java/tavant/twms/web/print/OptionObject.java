package tavant.twms.web.print;

public class OptionObject {
	
	private String option;
	
	private String optionDescription;
	
	private String serialNumber;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getOptionDescription() {
		return optionDescription;
	}

	public void setOptionDescription(String optionDescription) {
		this.optionDescription = optionDescription;
	}

	public OptionObject(String option, String optionDescription,
			String serialNumber) {
		super();
		this.option = option;
		this.optionDescription = optionDescription;
		this.serialNumber = serialNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
