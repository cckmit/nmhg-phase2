package tavant.twms.integration.layer.transformer;

import org.apache.xmlbeans.XmlException;

import tavant.oagis.OEMXREFSyncRequestDocumentDTO;
import tavant.oagis.OEMXREFDocumentDTO.OEMXREF;

public class SyncOEMXRefTransformer {

	public OEMXREF transform(String bod) {

		OEMXREFSyncRequestDocumentDTO doc = null;

		try {
			doc = OEMXREFSyncRequestDocumentDTO.Factory.parse(bod);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}

		OEMXREF oemXRef = doc.getOEMXREFSyncRequest().getOEMXREF();

		return oemXRef;

	}

}
