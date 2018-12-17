package tavant.twms.domain.claim;

import javax.persistence.Embeddable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jun 12, 2008
 * Time: 5:08:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class FailedRuleDetail {
    private String ruleMsg;
    private String defaultRuleMsgInUS;
    private String ruleNumber;
    private String ruleAction;

    public String getRuleMsg() {
        return ruleMsg;
    }

    public void setRuleMsg(String ruleMsg) {
        ruleMsg = formatMsg(ruleMsg);
        this.ruleMsg = ruleMsg;
    }

    private String formatMsg(String ruleMsg) {
        if (ruleMsg != null && ruleMsg.length() > 4000) {
            ruleMsg = ruleMsg.substring(0, 3500);
            ruleMsg = ruleMsg.substring(0, ruleMsg.lastIndexOf(","));
        }
        return ruleMsg;
    }

    public String getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(String ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public String getRuleAction() {
        return ruleAction;
    }

    public void setRuleAction(String ruleAction) {
        this.ruleAction = ruleAction;
    }

    public String getDefaultRuleMsgInUS() {
        return defaultRuleMsgInUS;
    }

    public void setDefaultRuleMsgInUS(String defaultRuleMsgInUS) {
        defaultRuleMsgInUS = formatMsg(defaultRuleMsgInUS);
        this.defaultRuleMsgInUS = defaultRuleMsgInUS;
    }

    public FailedRuleDetail clone() {
        FailedRuleDetail failedRuleDetail = new FailedRuleDetail();
        failedRuleDetail.setDefaultRuleMsgInUS(defaultRuleMsgInUS);
        failedRuleDetail.setRuleAction(ruleAction);
        failedRuleDetail.setRuleMsg(ruleMsg);
        failedRuleDetail.setRuleNumber(ruleNumber);
        return failedRuleDetail;
    }
}
