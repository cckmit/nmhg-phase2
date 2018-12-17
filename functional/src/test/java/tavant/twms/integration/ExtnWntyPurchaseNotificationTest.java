package tavant.twms.integration;

import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.integration.layer.component.global.ProcessGlobalExtWarrantyPurchaseNotification;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.globalsync.extendedwarrantynotification.ExtWarrantyNotificationDocumentDTO;
import junit.framework.Assert;
import org.apache.xmlbeans.XmlException;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Mar 18, 2010
 * Time: 3:33:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtnWntyPurchaseNotificationTest extends IntegrationTestCase {

    SyncTrackerService syncTrackerService;

    ProcessGlobalExtWarrantyPurchaseNotification processGlobalExtWarrantyPurchaseNotification;

    public void testExtnWntyPurchaseNotification() {
        login("system");
        SyncTracker syncTracker = syncTrackerService.findById(new Long("1119888479220"));
        Assert.assertNotNull(syncTracker);
        ExtWarrantyNotificationDocumentDTO extnWntyNotificationDocumentDTO = null;
        try{
            extnWntyNotificationDocumentDTO = ExtWarrantyNotificationDocumentDTO.Factory.parse(StringUtils
                    .replace(syncTracker.getBodXML(), "<ExtWarrantyNotification>",
                            "<ExtWarrantyNotification xmlns=\"http://www.tavant.com/globalsync/extendedwarrantynotification\">"));
		}catch(XmlException e){
			logger.error(" Failed to sync ExtendedWarranty Purchase Notification:", e);
		}
		List<SyncResponse> responses = processGlobalExtWarrantyPurchaseNotification.sync(extnWntyNotificationDocumentDTO);
        Assert.assertTrue(responses.get(0).isSuccessful());
        flushAndClear();
    }

    public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
        this.syncTrackerService = syncTrackerService;
    }

    public void setProcessGlobalExtWarrantyPurchaseNotification(ProcessGlobalExtWarrantyPurchaseNotification processGlobalExtWarrantyPurchaseNotification) {
        this.processGlobalExtWarrantyPurchaseNotification = processGlobalExtWarrantyPurchaseNotification;
    }
}
