package tavant.twms.web.print;

public class WaraningMessageObject 
{
    private String ruleMsg;
    private String defaultRuleMsgInUS;
    private String ruleNumber;
    private String ruleAction;
    public String getRuleMsg() {
		return ruleMsg;
	}
	public void setRuleMsg(String ruleMsg) {
		this.ruleMsg = ruleMsg;
	}
	public String getRuleNumber() {
		return ruleNumber;
	}
	public void setRuleNumber(String ruleNumber) {
		this.ruleNumber = ruleNumber;
	}
	public String getDefaultRuleMsgInUS() {
		return defaultRuleMsgInUS;
	}
	public void setDefaultRuleMsgInUS(String defaultRuleMsgInUS) {
		this.defaultRuleMsgInUS = defaultRuleMsgInUS;
	}
	public String getRuleAction() {
		return ruleAction;
	}
	public void setRuleAction(String ruleAction) {
		this.ruleAction = ruleAction;
	}
	
}
