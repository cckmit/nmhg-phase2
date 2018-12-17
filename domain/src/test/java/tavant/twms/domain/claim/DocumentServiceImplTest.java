package tavant.twms.domain.claim;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentRepository;
import tavant.twms.domain.common.DocumentServiceImpl;

public class DocumentServiceImplTest extends MockObjectTestCase{

	DocumentServiceImpl documentServiceImpl = new DocumentServiceImpl();
	
	Mock documentRepositoryMock;
	
	Mock documentMock;
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        documentRepositoryMock = mock(DocumentRepository.class);
        documentServiceImpl.setDocumentRepository((DocumentRepository) documentRepositoryMock.proxy());
        
        documentMock = mock(Document.class);
	}
	
	@Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
	
	public void testCreateDocument() throws Exception{
    	Document proxyDocument=(Document)documentMock.proxy();
    	documentRepositoryMock.expects(once()).method("save").with(eq(proxyDocument));        
    	documentServiceImpl.save(proxyDocument);
    }

    public void testFindDocument() throws Exception{
        Long id = new Long(0);
        Document document=(Document)documentMock.proxy();
        documentRepositoryMock.expects(once()).method("findById").with(eq(id)).will(returnValue(document));

        assertSame(document, documentServiceImpl.findById(id));
    }
}
