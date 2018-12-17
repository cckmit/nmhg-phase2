package tavant.twms.integration;

import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.component.global.GlobalItemSync;
import tavant.globalsync.itemsyncresponse.ItemSyncResponseDocumentDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO.SyncItemMaster;
import junit.framework.Assert;

import org.apache.xmlbeans.XmlException;
import org.springframework.util.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Feb 19, 2010
 * Time: 7:28:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemSyncTest extends IntegrationTestCase {

    SyncTrackerService syncTrackerService;

    GlobalItemSync globalItemSync;

    public void testItemSyncTask() throws XmlException {
        login("system");
        SyncTracker syncTracker = syncTrackerService.findById(new Long("1119888550880"));
        Assert.assertNotNull(syncTracker);
        SyncItemMaster items = SyncItemMasterDocumentDTO.Factory.parse(StringUtils
                    .replace(syncTracker.getBodXML(), "<SyncItemMaster>",
                            "<SyncItemMaster xmlns=\"http://www.tavant.com/globalsync/itemsync\">")).getSyncItemMaster();
        Assert.assertNotNull(items);
        ItemSyncResponseDocumentDTO itemSyncResponse = globalItemSync.sync(items);
        Assert.assertEquals(IntegrationConstants.SUCCESS, itemSyncResponse.getItemSyncResponse().getStatus().getCode());
        flushAndClear();
    }

    public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
        this.syncTrackerService = syncTrackerService;
    }

    public void setGlobalItemSync(GlobalItemSync globalItemSync) {
        this.globalItemSync = globalItemSync;
    }
}
