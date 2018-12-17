package tavant.twms.integration;

import tavant.globalsync.warrantyclaimcreditnotification.SyncInvoiceDocumentDTO;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.integration.layer.component.global.ProcessGlobalCreditNotification;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO;
import junit.framework.Assert;
import org.springframework.util.StringUtils;
import org.apache.xmlbeans.XmlException;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Apr 29, 2010
 * Time: 5:02:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreditNotificationSyncTest extends IntegrationTestCase {

    SyncTrackerService syncTrackerService;

    ProcessGlobalCreditNotification processGlobalCreditNotification;

    public void testCreditNotificationSync() {
        login("system");
        SyncTracker syncTracker = syncTrackerService.findById(new Long("1119888487980"));
        Assert.assertNotNull(syncTracker);
        InvoiceTypeDTO creditMemoDTO = null;
        try{
            creditMemoDTO = SyncInvoiceDocumentDTO.Factory.parse(StringUtils
                    .replace(syncTracker.getBodXML(), "<SyncInvoice>",
                            "<SyncInvoice xmlns=\"http://www.tavant.com/globalsync/warrantyclaimcreditnotification\">")).getSyncInvoice().getDataArea().getInvoice();
		}catch(XmlException e){
			logger.error(" Failed to sync ExtendedWarranty Purchase Notification:", e);
		}
        List<tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO> invoices = new ArrayList<InvoiceTypeDTO>();
		invoices.add(creditMemoDTO);
		List<SyncResponse> responses = processGlobalCreditNotification.sync(invoices);
        Assert.assertTrue(responses.get(0).isSuccessful());
        flushAndClear();
    }

    public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
        this.syncTrackerService = syncTrackerService;
    }

    public void setProcessGlobalCreditNotification(ProcessGlobalCreditNotification processGlobalCreditNotification) {
        this.processGlobalCreditNotification = processGlobalCreditNotification;
    }
}
