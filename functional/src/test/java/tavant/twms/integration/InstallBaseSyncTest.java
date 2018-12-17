package tavant.twms.integration;

import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.integration.layer.component.global.GlobalInstallBaseSync;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.globalsync.installbasesync.InventorySyncTypeDTO;
import junit.framework.Assert;

import java.util.Arrays;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.springframework.util.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Mar 18, 2010
 * Time: 11:11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class InstallBaseSyncTest extends IntegrationTestCase {

    SyncTrackerService syncTrackerService;

    GlobalInstallBaseSync globalInstallBaseSync;

    public void testInstallBaseSync() {
        login("system");
        SyncTracker syncTracker = syncTrackerService.findById(new Long("1119888442740"));
        Assert.assertNotNull(syncTracker);
        List<InventorySyncTypeDTO> shipmentSchedules = null;
		try {
            shipmentSchedules = Arrays.asList(InstallBaseSyncDocumentDTO.Factory.parse(StringUtils
                    .replace(syncTracker.getBodXML(), "<InstallBaseSync>",
                            "<InstallBaseSync xmlns=\"http://www.tavant.com/globalsync/installbasesync\">"))
                    .getInstallBaseSync().getDataArea().getInventorySync());
		} catch (XmlException e) {
			logger.error(" Failed to sync Sync InstallBase:", e);
		}
        Assert.assertNotNull(shipmentSchedules);
        List<SyncResponse> responses = globalInstallBaseSync.sync(shipmentSchedules);
        Assert.assertTrue(responses.get(0).isSuccessful());
        flushAndClear();
    }

    public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
        this.syncTrackerService = syncTrackerService;
    }

    public void setGlobalInstallBaseSync(GlobalInstallBaseSync globalInstallBaseSync) {
        this.globalInstallBaseSync = globalInstallBaseSync;
    }
}
