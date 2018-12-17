package tavant.twms.integration.layer.transformer;

import org.apache.xmlbeans.XmlException;

import tavant.extwarranty.InvoiceTypeDTO;
import tavant.extwarranty.SyncInvoiceDocumentDTO;

public class ExtWarrantyDebitNotificationTransformer {

	public InvoiceTypeDTO transform(String src) throws XmlException {
		SyncInvoiceDocumentDTO dto = null;
		dto = SyncInvoiceDocumentDTO.Factory.parse(src);
		return dto.getSyncInvoice().getDataArea().getInvoice();
	}
}
