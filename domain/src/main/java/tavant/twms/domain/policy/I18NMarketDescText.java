package tavant.twms.domain.policy;

import tavant.twms.domain.customReports.ReportI18NText;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: May 27, 2009
 * Time: 3:07:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("MARKETDESC")
/*ReportI18NText base class was created as a part of cutomReport flow. hence the name is ReportI18NText.
  But it can be used for other entities I18N */
public class I18NMarketDescText extends ReportI18NText {
}
