package tavant.twms.domain.upload;

import java.sql.Blob;

import org.hibernate.lob.BlobImpl;

import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.DomainRepositoryTestCase;

public class DocumentTest extends DomainRepositoryTestCase {
	
	OrgService orgService;

    User user;
    
	@Override
	protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();     
        user = orgService.findUserByName("dealer");
    }

	public void testDocumentSave() {
        Document document = new Document();
        TimePoint time = Clock.now();
        byte[] b = new byte[10];
        Blob testBlob = new BlobImpl(b);
        
        document.setContent(testBlob);
        document.setUploadedBy(user);
        document.setContentType("image/jpg");
        document.setFileName("uploaded_document.jpg");
        document.setUploadedOn(time);
        getSession().save(document);
        
        assertEquals(testBlob, document.getContent());
        assertEquals("image/jpg", document.getContentType());
        assertEquals("uploaded_document.jpg", document.getFileName());
        assertEquals(time, document.getUploadedOn());
        assertEquals(user, document.getUploadedBy());
    }
}
