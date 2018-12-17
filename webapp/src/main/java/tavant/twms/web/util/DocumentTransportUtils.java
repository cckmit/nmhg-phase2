/*
 * 
 */

package tavant.twms.web.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.common.Document;

/**
 *
 * @author vikas.sasidharan
 */
public class DocumentTransportUtils {
    private static final SimpleDateFormat UPLOADED_ON_DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy");
   
    public static JSONArray documentCollectionToJSONArray(
            Collection<Document> documents) {

        if (documents == null) {
            return null;
        }

        JSONArray documentsJSONArray = new JSONArray();
        for(Document document : documents) {
           documentsJSONArray.put(getDocumentJSONObject(document));
        }
        
        return documentsJSONArray;
    } 
    
    public static String documentCollectionToJSON(
            Collection<Document> documents) {
        
    	JSONArray documentsJSONArray = documentCollectionToJSONArray(documents);
        
        if(documentsJSONArray == null)
        	return null;
        
        return documentsJSONArray.toString();
    }

    public static String documentToJSON(Document document) {
        return getDocumentJSONObject(document).toString();
    }

    private static JSONObject getDocumentJSONObject(Document document) {
        if(document == null) {
            return new JSONObject();
        }

        JSONObject documentJSON = new JSONObject();
        try {
            documentJSON.put("name", document.getFileName());
            documentJSON.put("id", document.getId());
            documentJSON.put("size",
                    getFormattedUploadedDocumentSize(document.getSize()));
            documentJSON.put("description", document.getDescription());
            documentJSON.put("isOrphan", document.isOrphan());

            final AuditableColEntity auditObj = document.getD();
            documentJSON.put("uploadedOn",
                    UPLOADED_ON_DATE_FORMAT.format(auditObj.getCreatedTime()));
            final User uploadingUser = auditObj.getLastUpdatedBy();
            documentJSON.put("uploadedBy", 
                    padWithEmptyIfNull(uploadingUser.getFirstName().replaceAll("'", "&#39;")) +
                    " " + padWithEmptyIfNull(uploadingUser.getLastName().replaceAll("'", "&#39;")));
            documentJSON.put("success","The document was successfully uploaded");
            return documentJSON;
        } catch (JSONException e) {
            throw new RuntimeException(
                    "Exception while JSON'ifying uploaded document : " +
                    documentJSON, e);
        }
    }

    private static String getFormattedUploadedDocumentSize(int documentSize) {
        return documentSize < 1024 ? (documentSize + " Bytes") :
            (documentSize / 1024 + " KB");
    }

    private static String padWithEmptyIfNull(String value) {
        return (value == null) ? "" : value;
    }

    public static void markDocumentsAsAttached(Collection<Document> documents) {
        if(documents == null) {
            return;
        }

        Iterator<Document> documentIterator = documents.iterator();
        while(documentIterator.hasNext()) {
            Document document = documentIterator.next();
            
            if(document == null) {
                documentIterator.remove();
            } else {
                document.setOrphan(false);
            }
        }
    }
}
