package tavant.twms.domain.customReports;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 23, 2009
 * Time: 1:17:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("REPORTSECTION")
public class I18NReportSectionText extends ReportI18NText {
}
