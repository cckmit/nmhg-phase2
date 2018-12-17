package tavant.twms.integration.layer.component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.xmlbeans.XmlException;

import tavant.oagis.InvoiceTypeDTO;
import tavant.oagis.SyncInvoiceDocumentDTO;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.external.PaymentAsyncService;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;

public class ProcessCreditNotificationTest extends IntegrationRepositoryTestCase {
	
	ProcessCreditNotification processCreditNotification;

	PaymentAsyncService mockPaymentService = new MockCreditPaymentService(); 

	public void testSyncCreditNotification() throws XmlException, IOException{
		List<InvoiceTypeDTO> invoiceTypeDTO = getCreditInvoiceTypeDTO("/creditnotification/CreditNotification.xml");
		processCreditNotification.sync(invoiceTypeDTO);
	}
    private List<InvoiceTypeDTO> getCreditInvoiceTypeDTO(String xmlPath) throws XmlException, IOException {
		SyncInvoiceDocumentDTO dto = SyncInvoiceDocumentDTO.Factory.parse(ProcessCreditNotificationTest.class.getResourceAsStream(xmlPath));
		return Arrays.asList(dto.getSyncInvoice().getDataArea().getInvoice());    	
    }
	
	public void setProcessCreditNotification(
			ProcessCreditNotification processCreditNotification) {
		this.processCreditNotification = processCreditNotification;
		processCreditNotification.setPaymentAsyncService(mockPaymentService);
	}

}


class MockCreditPaymentService implements PaymentAsyncService {

	public void startAsyncPayment(Claim arg0) {
	}

	public void syncPaymentForClaim(Claim claim, CreditMemo memo) {
		Assert.assertEquals(new Integer(325645),memo.getCreditMemoNumber());
		Assert.assertEquals(new Double(125), memo.getTaxAmount().breachEncapsulationOfAmount().doubleValue());
	}

	public void syncCreditMemo(CreditMemo memo) {
		Assert.assertEquals(new Integer(325645),memo.getCreditMemoNumber());
		Assert.assertEquals(new Double(125), memo.getTaxAmount().breachEncapsulationOfAmount().doubleValue());
	}

	public void startSupplierRecoveryAsyncPayment(RecoveryClaim recoveryClaim) {
		// TODO Auto-generated method stub
		
	}
}

