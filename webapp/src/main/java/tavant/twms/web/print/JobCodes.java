package tavant.twms.web.print;

import java.math.BigDecimal;


public class JobCodes {
	
	private String code;
	private String description;
	private String stdLabHours;
	private String addLabHours;
	private String reasonForAdditionalHours;
    private BigDecimal laborHrsEntered;
	
    private BigDecimal hoursSpent;
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the stdLabHours
	 */
	public String getStdLabHours() {
		return stdLabHours;
	}
	/**
	 * @param stdLabHours the stdLabHours to set
	 */
	public void setStdLabHours(String stdLabHours) {
		this.stdLabHours = stdLabHours;
	}
	/**
	 * @return the addLabHours
	 */
	public String getAddLabHours() {
		return addLabHours;
	}
	/**
	 * @param addLabHours the addLabHours to set
	 */
	public void setAddLabHours(String addLabHours) {
		this.addLabHours = addLabHours;
	}
	/**
	 * @return the reasonForAdditionalHours
	 */
	public String getReasonForAdditionalHours() {
		return reasonForAdditionalHours;
	}
	/**
	 * @param reasonForAdditionalHours the reasonForAdditionalHours to set
	 */
	public void setReasonForAdditionalHours(String reasonForAdditionalHours) {
		this.reasonForAdditionalHours = reasonForAdditionalHours;
	}
	
	/**
	 * @return the laborHrsEntered
	 */
	public BigDecimal getLaborHrsEntered() {
		return laborHrsEntered;
	}
	/**
	 * @param laborHrsEntered the laborHrsEntered to set
	 */
	public void setLaborHrsEntered(BigDecimal laborHrsEntered) {
		this.laborHrsEntered = laborHrsEntered;
	}
	/**
	 * @return the hoursSpent
	 */
	public BigDecimal getHoursSpent() {
		return hoursSpent;
	}
	/**
	 * @param hoursSpent the hoursSpent to set
	 */
	public void setHoursSpent(BigDecimal hoursSpent) {
		this.hoursSpent = hoursSpent;
	}
	

}
