/**
 * 
 */
package tavant.twms.domain.upload;

import java.sql.Blob;
import java.util.List;

import org.hibernate.lob.BlobImpl;

import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.DomainRepositoryTestCase;

public class DocumentRepositoryTest extends DomainRepositoryTestCase {
	
	DocumentRepository documentRepository;

	OrgService orgService;
	
	User user;
	
	public void setDocumentRepository(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}
	
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
	public void testCreateDocument() throws Exception {
		Document document = createDocument();
		documentRepository.save(document);
		List<Document> documents = documentRepository.findAll();
		for (Document d : documents){
			if(d.getFileName().equalsIgnoreCase(document.getFileName())){
				assertEquals(d,document);
			}
		}
	}
		
	private Document createDocument(){
		Document document = new Document();
		TimePoint time = Clock.now();
        byte[] b = new byte[10];
        Blob testBlob = new BlobImpl(b);
		user=orgService.findUserByName("dealer");
		
		document.setContent(testBlob);
		document.setUploadedBy(user);
        document.setContentType("image/jpg");
        document.setFileName("uploaded_document.jpg");
        document.setUploadedOn(time);
        
		return document;
	}



}
