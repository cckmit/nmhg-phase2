/**
 * 
 */
package tavant.twms.domain.upload;

import java.sql.Blob;

import org.hibernate.lob.BlobImpl;

import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.DomainRepositoryTestCase;

public class DocumentRepositoryImplTest extends DomainRepositoryTestCase{
	
	DocumentRepository documentRepository;
	
	Document newDocument; 
	
	User user;
	
	OrgService orgService;
	
	@Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
    
        newDocument = new Document();
		TimePoint time = Clock.now();
        byte[] b = new byte[10];
        Blob testBlob = new BlobImpl(b);
		user=orgService.findUserByName("dealer");
		
		newDocument.setContent(testBlob);
		newDocument.setUploadedBy(user);
		newDocument.setContentType("image/jpg");
		newDocument.setFileName("uploaded_document.jpg");
		newDocument.setUploadedOn(time);
    }
	
	public void testCRUD() {
		documentRepository.save(newDocument);
		assertNotNull("Id should have been Created", newDocument.getId());
		
		//flushAndClear();
		Document retreivedFromDB = documentRepository.findById(newDocument.getId());
		assertNotNull(retreivedFromDB);
		assertEquals(newDocument.getContent(),retreivedFromDB.getContent());
		assertEquals(newDocument.getUploadedBy(),retreivedFromDB.getUploadedBy());
		assertEquals(newDocument.getContentType(),retreivedFromDB.getContentType());
		assertEquals(newDocument.getFileName(),retreivedFromDB.getFileName());
		assertEquals(newDocument.getUploadedOn(),retreivedFromDB.getUploadedOn());				
	}
	
	public void setDocumentRepository(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}
	
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
}
