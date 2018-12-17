package tavant.twms.web.documentOperations;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentService;
import tavant.twms.web.actions.TwmsActionSupport;

import javax.servlet.ServletOutputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.web.util.DocumentTransportUtils;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 26 Jun, 2009
 * Time: 4:30:39 PM
 */
public class DocumentTransportAction extends TwmsActionSupport {
    private Document document;
    private DocumentService documentService;
    private Logger logger = Logger.getLogger(DocumentTransportAction.class);
    private String documentDetailsJSON;
    
    @Override
    public void validate(){
    	if(hasActionErrors()){
    		JSONObject documentJSON = new JSONObject();
        	try {
				documentJSON.put("failure", getActionErrors());
			} catch (JSONException e) {
	            throw new RuntimeException(
	                    "Exception while JSON'ifying uploaded document : " +
	                    documentJSON, e);
	        }
        	documentDetailsJSON = documentJSON.toString();
    	}
    	
    }

    public String downloadDocument() {
        response.setContentType(document.getContentType());
        response.setContentLength(document.getSize());
        response.setHeader("Cache-Control", "public");
        response.setHeader("Content-disposition",
                "attachment; filename=" + document.getFileName());

        Blob inputFile = document.getContent();

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            FileCopyUtils.copy(inputFile.getBinaryStream(), outputStream);
            outputStream.flush();
        } catch (Exception e) {
            String errorMessage =
                    "Failed to write document to output stream : ";
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

        return null;
    }

    public String uploadDocument() throws Exception {
        documentService.save(document);

        documentDetailsJSON = DocumentTransportUtils.documentToJSON(document);

        return SUCCESS;
    }

    public String deleteDocument() throws Exception {
        documentService.delete(document);

        return null;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

	public String getDocumentDetailsJSON() {
		return documentDetailsJSON;
	}

	public void setDocumentDetailsJSON(String documentDetailsJSON) {
		this.documentDetailsJSON = documentDetailsJSON;
	}   
}