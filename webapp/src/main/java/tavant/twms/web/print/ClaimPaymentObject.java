package tavant.twms.web.print;

import java.math.BigDecimal;

import tavant.twms.infra.BigDecimalFactory;

public class ClaimPaymentObject {

	private String name;

	private BigDecimal value;
	
	private BigDecimal cpValue;
	
	private BigDecimal claimedValue;

	private Boolean bold;

	private Boolean newLine;
	
	private Boolean firstLine;
	
	private Boolean advisorEnabled;
	
	private String currencyCode;
	
	private Boolean claimedValueDisplay;
	
	private Boolean rateValueDisplay;
	
	private String askedQtyHrs;
    
    private String acceptedQtyHrs;  
    
    private String percentageAcceptance ;
    
    private BigDecimal stateMandateRatePercentage ;
    
    private BigDecimal stateMondateAmount;
    private BigDecimal reviwedAmount;
    
    private Boolean totalAcceptanceChkbox=Boolean.FALSE;
	private Boolean totalAcceptStateMdtChkbox=Boolean.FALSE;
	private String totalClaimAmountCheckBox;
	
	private String rateValue;
	
	private String rateClaimedValue;
	
	private String stateMondateRateValue;


   
	public ClaimPaymentObject(){
		super();
	}
	
	public ClaimPaymentObject(String name, BigDecimal claimedValue, BigDecimal value, BigDecimal cpValue,Boolean bold, Boolean newLine,Boolean advisorEnabled) {
		super();
		this.name = name;
		this.claimedValue = claimedValue;
		this.value = value;
		this.cpValue = cpValue;
		this.bold = bold;
		this.newLine = newLine;
		this.advisorEnabled=advisorEnabled;
	}
	
	
    public ClaimPaymentObject(String name,Boolean bold, Boolean newLine) {
		super();
		this.name = name;
		this.claimedValue = new BigDecimal(0.0);
		this.value = new BigDecimal(0.0);
		this.bold = bold;
		this.newLine = newLine;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Boolean getBold() {
		return bold;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;
	}

	public Boolean getNewLine() {
		return newLine;
	}

	public void setNewLine(Boolean newLine) {
		this.newLine = newLine;
	}

	public Boolean getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(Boolean firstLine) {
		this.firstLine = firstLine;
	}

	public BigDecimal getClaimedValue() {
		return claimedValue;
	}

	public void setClaimedValue(BigDecimal claimedValue) {
		this.claimedValue = claimedValue;
	}

	public BigDecimal getCpValue() {
		return cpValue;
	}

	public void setCpValue(BigDecimal cpValue) {
		this.cpValue = cpValue;
	}

	public Boolean getAdvisorEnabled() {
		return advisorEnabled;
	}

	public void setAdvisorEnabled(Boolean advisorEnabled) {
		this.advisorEnabled = advisorEnabled;
	}
	 public BigDecimal getStateMondateAmount() {
			return stateMondateAmount;
		}

		public void setStateMondateAmount(BigDecimal stateMondateAmount) {
			this.stateMondateAmount = stateMondateAmount;
		}

		public BigDecimal getFlatAmount() {
			return flatAmount;
		}

		public void setFlatAmount(BigDecimal flatAmount) {
			this.flatAmount = flatAmount;
		}

		private BigDecimal flatAmount;
	    
	    
		
		public String getAskedQtyHrs() {
			return askedQtyHrs;
		}

		public void setAskedQtyHrs(String askedQtyHrs) {
			this.askedQtyHrs = askedQtyHrs;
		}

		public String getAcceptedQtyHrs() {
			return acceptedQtyHrs;
		}

		public void setAcceptedQtyHrs(String acceptedQtyHrs) {
			this.acceptedQtyHrs = acceptedQtyHrs;
		}

		
		public BigDecimal getStateMandateRatePercentage() {
			return stateMandateRatePercentage;
		}

		public String getPercentageAcceptance() {
			return percentageAcceptance;
		}

		public void setPercentageAcceptance(String percentageAcceptance) {
			this.percentageAcceptance = percentageAcceptance;
		}

		public void setStateMandateRatePercentage(BigDecimal stateMandateRatePercentage) {
			this.stateMandateRatePercentage = stateMandateRatePercentage;
		}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public BigDecimal getReviwedAmount() {
		return reviwedAmount;
	}

	public void setReviwedAmount(BigDecimal reviwedAmount) {
		this.reviwedAmount = reviwedAmount;
	}

	/**
	 * @return the totalAcceptanceChkbox
	 */
	public Boolean getTotalAcceptanceChkbox() {
		return totalAcceptanceChkbox;
	}

	/**
	 * @param totalAcceptanceChkbox the totalAcceptanceChkbox to set
	 */
	public void setTotalAcceptanceChkbox(Boolean totalAcceptanceChkbox) {
		this.totalAcceptanceChkbox = totalAcceptanceChkbox;
	}

	/**
	 * @return the totalAcceptStateMdtChkbox
	 */
	public Boolean getTotalAcceptStateMdtChkbox() {
		return totalAcceptStateMdtChkbox;
	}

	/**
	 * @param totalAcceptStateMdtChkbox the totalAcceptStateMdtChkbox to set
	 */
	public void setTotalAcceptStateMdtChkbox(Boolean totalAcceptStateMdtChkbox) {
		this.totalAcceptStateMdtChkbox = totalAcceptStateMdtChkbox;
	}

	/**
	 * @return the totalClaimAmountCheckBox
	 */
	public String getTotalClaimAmountCheckBox() {
		return totalClaimAmountCheckBox;
	}

	/**
	 * @param totalClaimAmountCheckBox the totalClaimAmountCheckBox to set
	 */
	public void setTotalClaimAmountCheckBox(String totalClaimAmountCheckBox) {
		this.totalClaimAmountCheckBox = totalClaimAmountCheckBox;
	}

	/**
	 * @return the claimedValueDisplay
	 */
	public Boolean getClaimedValueDisplay() {
		return claimedValueDisplay;
	}

	/**
	 * @param claimedValueDisplay the claimedValueDisplay to set
	 */
	public void setClaimedValueDisplay(Boolean claimedValueDisplay) {
		this.claimedValueDisplay = claimedValueDisplay;
	}

	/**
	 * @return the rateValueDisplay
	 */
	public Boolean getRateValueDisplay() {
		return rateValueDisplay;
	}

	/**
	 * @param rateValueDisplay the rateValueDisplay to set
	 */
	public void setRateValueDisplay(Boolean rateValueDisplay) {
		this.rateValueDisplay = rateValueDisplay;
	}

	/**
	 * @return the rateValue
	 */
	public String getRateValue() {
		return rateValue;
	}

	/**
	 * @param rateValue the rateValue to set
	 */
	public void setRateValue(String rateValue) {
		this.rateValue = rateValue;
	}

	/**
	 * @return the rateClaimedValue
	 */
	public String getRateClaimedValue() {
		return rateClaimedValue;
	}

	/**
	 * @param rateClaimedValue the rateClaimedValue to set
	 */
	public void setRateClaimedValue(String rateClaimedValue) {
		this.rateClaimedValue = rateClaimedValue;
	}

	/**
	 * @return the stateMondateRateValue
	 */
	public String getStateMondateRateValue() {
		return stateMondateRateValue;
	}

	/**
	 * @param stateMondateRateValue the stateMondateRateValue to set
	 */
	public void setStateMondateRateValue(String stateMondateRateValue) {
		this.stateMondateRateValue = stateMondateRateValue;
	}

	

}
