package tavant.twms.web.documentOperations;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.engine.jdbc.BlobProxy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.DocumentService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.upload.HeaderUtil;

import com.domainlanguage.timeutil.Clock;

/**
 * @author anshul.khare, janmejay.singh
 */
@SuppressWarnings("serial")
public class DocumentAction extends TwmsActionSupport implements ServletResponseAware, ParameterAware {

    private static Logger logger = LogManager.getLogger(DocumentAction.class);
    private Long docId;
    private DocumentService documentService;
    private List<Document> documents;
    private HttpServletResponse httpServletResponse;
    private TransactionTemplate transactionTemplate;
    private String attachementListIndex;

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public static String ID = "id",
                         NAME = "name",
                         SHARE = "share",
                         SHAREWITHSUPPLIER = "shareWithSupplier",
                         SHAREWITHDEALER = "shareWithDealer",
                         USER = "user",
                         MANDATORY = "mandatory",
                         TYPE = "type",
                         DESCRIPTION = "description",
                         HIDDEN = "hidden",
                         DATE="date",
                         UPLOADED_BY = "uploadedBy",
                         UPLOADED_ON = "uploadedOn",
                         SIZE = "size";
                        
                         

    private int batchSize = 1;

    private Map parameters;

    public static final String FILE_ATTRIBUTE_TEMPLATE = "upload[#]",
                               FILE_NAME_ATTRIBUTE_TEMPLATE = "upload[#]FileName",
                               CONTENT_TYPE_ATTRIBUTE_TEMPLATE = "upload[#]ContentType",
                               SUBSTITUTION_IDENTIFIER = "#";

    private static class UploadedFile {
        private String fileName;
        private String contentType;
        private File file;

        private UploadedFile() {}

        private static String substituteIndex(String key, int index) {
            return key.replace(SUBSTITUTION_IDENTIFIER, "" + index);
        }

        public String getFileName() {
            return fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public File getFile() {
            return file;
        }

        public static UploadedFile getUploadedFile(Map parameters, int index) {
            String fileNameKey = substituteIndex(FILE_NAME_ATTRIBUTE_TEMPLATE, index);
            if(parameters.containsKey(fileNameKey) && StringUtils.hasText(((String[])parameters.get(fileNameKey))[0])) {
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.fileName = ((String[]) parameters.get(fileNameKey))[0];
                uploadedFile.contentType =
                        ((String[]) parameters.get(substituteIndex(CONTENT_TYPE_ATTRIBUTE_TEMPLATE, index)))[0];
                uploadedFile.file = ((File[]) parameters.get(substituteIndex(FILE_ATTRIBUTE_TEMPLATE, index)))[0];
                return uploadedFile;
            }
            return null;
        }
    }

    public void downloadDocument() {
        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {
                    Document document = documentService.findById(docId);
                    HeaderUtil.setHeader(httpServletResponse, document.getFileName(), document.getContentType());
                    OutputStream downloadStream = httpServletResponse.getOutputStream();
                    FileCopyUtils.copy(document.getContent().getBinaryStream(), downloadStream);
                } catch (Exception e) {
                    logger.error("Exception in DocumentAction.downloadDocument() for docId " + docId,e);
                }
            }
        });
    }

    public String uploadDocument() {
        documents = new ArrayList<Document>();
        try{
            for (UploadedFile uploadedFile : parseParametersAndPopulateFileDetails()) {
                InputStream is = new FileInputStream(uploadedFile.getFile());
                long fLength = uploadedFile.getFile().length();
                Blob b = BlobProxy.generateProxy(is, (int)fLength);
                Document document = new Document();
                document.setContent(b);
                document.setSize(Integer.parseInt(Long.toString(fLength)));
                document.setFileName(uploadedFile.getFileName());
                document.setContentType(uploadedFile.getContentType());
                document.setUploadedOn(Clock.now());
                document.setUploadedBy(getLoggedInUser());
                if(new SecurityHelper().getLoggedInUser().hasRole("supplier") && !new SecurityHelper().getLoggedInUser().hasRole("recoveryProcessor")){
                	document.setIsSharedWithSupplier(Boolean.TRUE);
                }
                if(new SecurityHelper().getLoggedInUser().isDealer() && !new SecurityHelper().getLoggedInUser().hasRole("processor")){
                	document.setIsSharedWithDealer(Boolean.TRUE);
                }
                documentService.save(document);
                documents.add(document);
            }
        }catch(Exception ex){
            logger.error("Exception in DocumentAction.uploadDocument()");
            ex.printStackTrace();
            addActionError(getText("message.documentUploadFailed"));
            return INPUT;
        }
        return SUCCESS;
    }

    private List<UploadedFile> parseParametersAndPopulateFileDetails() {
        List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
        for(int i = 0; i < batchSize; i++) {
            UploadedFile uploadedFile = UploadedFile.getUploadedFile(parameters, i);
            if(uploadedFile != null) {
                uploadedFiles.add(uploadedFile);
            }
        }
        return uploadedFiles;
    }

    @Required
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public void setServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public String getJSONifiedDocumentList() throws JSONException {
        return getDocumentListJSON(documents).toString();
    }
    
    public String getJSONifiedDocumentListForRecoveryClaim() throws JSONException {
        return getDocumentListJSONForRecoveryClaim(documents).toString();
    }
    
    public String getJSONifiedUnitDocumentList() throws JSONException {
    	return getUnitDocumentListJSON(documents).toString();
    }

   public static JSONArray getDocumentListJSONForRecoveryClaim(Collection<Document> docs)
   			throws JSONException {
   		JSONArray list = new JSONArray();
   		for (Document doc : docs) {
   			if(new SecurityHelper().getLoggedInUser().hasRole("supplier"))
   			{
   						list.put(new JSONObject()
   						.put(ID, doc.getId())
   						.put(NAME,doc.getFileName())
   						.put(SHARE, getBooleanString(doc.getIsEligibilityToShare()))
   						.put(SHAREWITHDEALER, getBooleanString(doc.getIsSharedWithDealer()))
   						.put(USER, doc.getUploadedBy() != null ?
   								getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
   						.put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
   						.put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
   						.put(HIDDEN,!doc.getIsSharedWithSupplier())
   						.put(MANDATORY, getBooleanString(doc.getMandatory())));
   			}
   			else if(new SecurityHelper().getLoggedInUser().hasRole("recoveryProcessor"))
   			{
   				list.put(new JSONObject()
					.put(ID, doc.getId())
					.put(NAME,doc.getFileName())
					.put(SHARE, getBooleanString(doc.getIsEligibilityToShare()))
					.put(SHAREWITHSUPPLIER, getBooleanString(doc.getIsSharedWithSupplier()))
					.put(SHAREWITHDEALER, getBooleanString(doc.getIsSharedWithDealer()))
					.put(USER, doc.getUploadedBy() != null ?
							getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
					.put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
					.put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
					.put(HIDDEN,Boolean.FALSE)
					.put(MANDATORY, getBooleanString(doc.getMandatory())));
   			}
   			else
   			{
   				list.put(new JSONObject()
   					.put(ID, doc.getId())
   					.put(NAME,doc.getFileName())
   					.put(SHARE,getBooleanString(doc.getIsEligibilityToShare()))
					.put(USER,doc.getUploadedBy() != null ?
							getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
					.put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
					.put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
					.put(HIDDEN,!doc.getIsSharedWithSupplier())
					.put(MANDATORY,getBooleanString(doc.getMandatory())));		
   			}

   		}
   		return list;
	}
   
   public static JSONArray getDocumentListJSON(Collection<Document> docs)
   throws JSONException {
	   JSONArray list = new JSONArray();
	   for (Document doc : docs) {
		   if(new SecurityHelper().getLoggedInUser().hasRole("supplier"))
		   {
				   list.put(new JSONObject()
				   .put(ID, doc.getId())
				   .put(NAME,doc.getFileName())
				   .put(SHARE, getBooleanString(doc.getIsEligibilityToShare()))
				   .put(SHAREWITHSUPPLIER, getBooleanString(doc.getIsSharedWithSupplier()))
				   .put(SHAREWITHDEALER, getBooleanString(doc.getIsSharedWithDealer()))
				   .put(USER, doc.getUploadedBy() != null ?
						   getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
						   .put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
						   .put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
						   .put(MANDATORY, getBooleanString(doc.getMandatory()))
						   .put(UPLOADED_BY,doc.getUploadedBy() != null ?
									getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
							.put(UPLOADED_ON, doc.getD().getCreatedTime())
							.put(SIZE, doc.getSize()));
			   
		   }else if(new SecurityHelper().getLoggedInUser().hasRole("recoveryProcessor") || new SecurityHelper().getLoggedInUser().hasRole("processor") )
  			{
  				list.put(new JSONObject()
					.put(ID, doc.getId())
					.put(NAME,doc.getFileName())
					.put(SHARE, getBooleanString(doc.getIsEligibilityToShare()))
					.put(SHAREWITHSUPPLIER, getBooleanString(doc.getIsSharedWithSupplier()))
					.put(SHAREWITHDEALER, getBooleanString(doc.getIsSharedWithDealer()))
					.put(USER, doc.getUploadedBy() != null ?
							getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
					.put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
					.put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
					.put(HIDDEN,Boolean.FALSE)
					.put(MANDATORY, getBooleanString(doc.getMandatory()))
					.put(DATE, doc.getUploadedOn().calendarDate(TimeZone.getDefault()).toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()))
					 .put(UPLOADED_BY,doc.getUploadedBy() != null ?
								getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
						.put(UPLOADED_ON, doc.getD().getCreatedTime())
						.put(SIZE, doc.getSize()));
  			}
		   else if(new SecurityHelper().getLoggedInUser().hasRole("dealer")&&
				   !(new SecurityHelper().getLoggedInUser().isInternalUser()))
		   {
			   
				   list.put(new JSONObject()
				   .put(ID, doc.getId())
				   .put(NAME,doc.getFileName())
				   .put(SHAREWITHSUPPLIER, getBooleanString(doc.getIsSharedWithSupplier()))
				   .put(SHAREWITHDEALER, getBooleanString(doc.getIsSharedWithDealer()))
				   .put(USER, doc.getUploadedBy() != null ?
						   getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
						   .put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
						   .put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
						   .put(MANDATORY, getBooleanString(doc.getMandatory()))
						   .put(UPLOADED_BY,doc.getUploadedBy() != null ?
									getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
							.put(UPLOADED_ON, doc.getD().getCreatedTime())
							.put(SIZE, doc.getSize()));
		   }
		   else
		   {
			   list.put(new JSONObject()
			   .put(ID, doc.getId())
			   .put(NAME,doc.getFileName())
			   .put(SHARE,getBooleanString(doc.getIsEligibilityToShare()))
			   .put(SHAREWITHSUPPLIER, getBooleanString(doc.getIsSharedWithSupplier()))
			   .put(SHAREWITHDEALER, getBooleanString(doc.getIsSharedWithDealer()))
			   .put(USER,doc.getUploadedBy() != null ?
					   getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
					   .put(TYPE, doc.getDocumentType() == null ? "" : doc.getDocumentType().getCode())
					   .put(DESCRIPTION,doc.getDocumentType()==null ? "" : doc.getDocumentType().getDescription())
					   .put(MANDATORY,getBooleanString(doc.getMandatory()))
					   .put(DATE, doc.getUploadedOn().calendarDate(TimeZone.getDefault()).toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()))
					   .put(UPLOADED_BY,doc.getUploadedBy() != null ?
								getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
						.put(UPLOADED_ON, doc.getD().getCreatedTime())
						.put(SIZE, doc.getSize()));

		   }

	   }
	   return list;
   }
   
   public static JSONArray getUnitDocumentListJSON(Collection<Document> docs)
		   throws JSONException {
			   JSONArray list = new JSONArray();
			   for (Document doc : docs) {
				   if(new SecurityHelper().getLoggedInUser().hasOnlyRole("supplier"))
				   {
					   if(doc.getIsEligibilityToShare())
					   {
						   list.put(new JSONObject()
						   .put(ID, doc.getId())
						   .put(NAME,doc.getFileName())
						   .put(TYPE, doc.getUnitDocumentType())
						   .put(SHARE, getBooleanString(doc.getIsEligibilityToShare()))
						   .put(USER, doc.getUploadedBy() != null ?
								   getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
								   .put(TYPE, doc.getUnitDocumentType() == null ? "" : doc.getUnitDocumentType().getCode())
								   .put(DESCRIPTION,doc.getUnitDocumentType() ==null ? "" : doc.getUnitDocumentType().getDescription())
								   .put(MANDATORY, getBooleanString(doc.getMandatory()))
								   .put(UPLOADED_BY,doc.getUploadedBy() != null ?
											getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
									.put(UPLOADED_ON, doc.getD().getCreatedTime())
									.put(SIZE, doc.getSize()));
					   }
				   }
				   else
				   {
					   list.put(new JSONObject()
					   .put(ID, doc.getId())
					   .put(NAME,doc.getFileName())
					   .put(TYPE, doc.getUnitDocumentType())
					   .put(SHARE,getBooleanString(doc.getIsEligibilityToShare()))
					   .put(USER,doc.getUploadedBy() != null ?
							   getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
							   .put(TYPE, doc.getUnitDocumentType() == null ? "" : doc.getUnitDocumentType().getCode())
							   .put(DESCRIPTION,doc.getUnitDocumentType()==null ? "" : doc.getUnitDocumentType().getDescription())
							   .put(MANDATORY,getBooleanString(doc.getMandatory()))
							   .put(UPLOADED_BY,doc.getUploadedBy() != null ?
										getUserName(doc.getUploadedBy()):getUserName(doc.getD().getLastUpdatedBy()))
								.put(UPLOADED_ON, doc.getD().getCreatedTime())
								.put(SIZE, doc.getSize()));
				   }

			   }
			   return list;
		   }

   public static String getUserName(User user){
	String fullName = "";
	if(user.getFirstName() != null){
		fullName = fullName + user.getFirstName() + " ";
	}
	
	if(user.getLastName() != null){
		fullName = fullName + user.getLastName();
	}
	return fullName;
   }
   
   public static String getBooleanString(Boolean flag){
	String enable="false";
	if(flag != null && flag.booleanValue()){
		enable = "true";
	}
	return enable;
   }

    public String getAttachementListIndex() {
        return attachementListIndex;
    }

    public void setAttachementListIndex(String attachementListIndex) {
        this.attachementListIndex = attachementListIndex;
    }

}