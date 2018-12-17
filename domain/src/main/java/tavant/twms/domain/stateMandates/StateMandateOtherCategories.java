package tavant.twms.domain.stateMandates;

public class StateMandateOtherCategories {
private String others; 
	
	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}
	private Boolean mandatory=Boolean.FALSE;
	public StateMandateOtherCategories(){
		
	}
	
	public Boolean getMandatory() {
		return mandatory;
	}
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
}
