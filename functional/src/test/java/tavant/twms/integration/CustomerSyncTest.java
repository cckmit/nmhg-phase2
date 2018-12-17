package tavant.twms.integration;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.xmlbeans.XmlException;
import org.springframework.util.StringUtils;

import tavant.globalsync.customersync.CustomerSyncRequestDocumentDTO;
import tavant.globalsync.customersync.CustomerTypeDTO;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.component.global.GlobalCustomerSync;
import tavant.twms.integration.layer.upload.CustomerUploader;

public class CustomerSyncTest  extends IntegrationTestCase {

    SyncTrackerService syncTrackerService;

    GlobalCustomerSync globalCustomerSync;
    
    CustomerUploader customerUploader;
    
    public CustomerUploader getCustomerUploader() {
		return customerUploader;
	}

	public void setCustomerUploader(CustomerUploader customerUploader) {
		this.customerUploader = customerUploader;
	}

	public void testCustomerSync() {
        login("system");
        SyncTracker syncTracker = syncTrackerService.findById(new Long("1119904217800"));
        Assert.assertNotNull(syncTracker);
        List<CustomerTypeDTO> customers = null;
		try {
			customers = Arrays.asList(CustomerSyncRequestDocumentDTO.Factory.parse(StringUtils
                    .replace(syncTracker.getBodXML(), "<CustomerSyncRequest>",
                            "<CustomerSyncRequest xmlns=\"http://www.tavant.com/globalsync/customersync\">"))
                    .getCustomerSyncRequest().getDataArea().getCustomer());
		} catch (XmlException e) {
			logger.error(" Failed to sync Sync Customer:", e);
		}
		List<SyncResponse> responses = globalCustomerSync.sync(customers);
		Assert.assertTrue(responses.get(0).isSuccessful());
        flushAndClear();
    }
    
    public void testCustomerUpload() {
        login("system");
        customerUploader.uploadCustomer();
        flushAndClear();
    }
    
    public void testGlobalCustomerSync() {
    	 login("system");
         SyncTracker syncTracker = syncTrackerService.findById(new Long("1119904217800"));
         Assert.assertNotNull(syncTracker);
         List<CustomerTypeDTO> customers = null;
 		try {
 			customers = Arrays.asList(CustomerSyncRequestDocumentDTO.Factory.parse(StringUtils
 					.replace(syncTracker.getBodXML(), "<CustomerSyncRequest>",
 					"<CustomerSyncRequest xmlns=\"http://www.tavant.com/globalsync/customersync\">"))
                     .getCustomerSyncRequest().getDataArea().getCustomer());
 		} catch (XmlException e) {
 			logger.error(" Failed to sync Sync Customer:", e);
 		}
 		globalCustomerSync.sync(customers.get(0));
         flushAndClear();
    }

	public SyncTrackerService getSyncTrackerService() {
		return syncTrackerService;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}

	public GlobalCustomerSync getGlobalCustomerSync() {
		return globalCustomerSync;
	}

	public void setGlobalCustomerSync(GlobalCustomerSync globalCustomerSync) {
		this.globalCustomerSync = globalCustomerSync;
	}
}
