package tavant.twms.domain.catalog;

import tavant.twms.domain.customReports.ReportI18NText;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 15, 2009
 * Time: 7:33:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("MISCITEM")
/*ReportI18NText base class was created as a part of cutomReport flow. hence the name is ReportI18NText.
  But it can be used for other entities I18N */
public class I18NMiscItem extends ReportI18NText {
}
