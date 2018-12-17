package tavant.twms.domain.customReports;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 23, 2009
 * Time: 1:19:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("REPORTQUESTION")
public class I18NQuestionText extends ReportI18NText {
}
