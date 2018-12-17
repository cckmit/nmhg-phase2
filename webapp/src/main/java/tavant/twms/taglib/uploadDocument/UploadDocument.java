package tavant.twms.taglib.uploadDocument;

import static tavant.twms.web.util.DocumentTransportUtils.documentCollectionToJSON;
import static tavant.twms.web.util.DocumentTransportUtils.documentToJSON;

import java.util.Collection;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.UIBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Document;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 29 Jun, 2009
 * Time: 6:53:53 PM
 */
public class UploadDocument extends UIBean {

    private int trimFileNameDisplayTo;
    private String selectedFilesCountParam;
    private String documentDownloadAction;
    private String documentUploadAction;
    private String documentDeletionAction;
    private int maxFilesToBeUploaded;
    private boolean singleFileUpload;
    private int fieldSize;
    private String canDeleteAlreadyUploadedIf;

    private static final Pattern STRUTS_EXPR_PATTERN = Pattern.compile("(%\\{|\\})");

    public UploadDocument(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    /**
     * A contract that requires each concrete UI Tag to specify which template should be used as a default.  For
     * example, the CheckboxTab might return "checkbox.vm" while the RadioTag might return "radio.vm".  This value
     * <strong>not</strong> begin with a '/' unless you intend to make the path absolute rather than relative to the
     * current theme.
     *
     * @return The name of the template to be used as the default.
     */
    protected String getDefaultTemplate() {
        return "uploaddocument";
    }

    @SuppressWarnings("unchecked")
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        validate();

        addParameter("trimFileNameDisplayTo", getTrimFileNameDisplayTo());
        
        addParameter("singleFileUpload", isSingleFileUpload());
        
        addParameter("maxFilesToBeUploaded", getMaxFilesToBeUploaded());

        addParamIfSet("selectedFilesCountParam", getSelectedFilesCountParam());

        addParameter("documentDownloadAction", getDocumentDownloadAction());

        addParameter("documentUploadAction", getDocumentUploadAction());

        addParameter("documentDeletionAction", getDocumentDeletionAction());
        
        addParameter("fieldSize", getFieldSize());

        addParamIfSet("uploadedFilesInfo", getUploadedDocsInfo());

        addParameter("canDeleteAlreadyUploaded", computeCanDeleteAlreadyUploaded());
    }

    private void validate() {
        if (getTrimFileNameDisplayTo() <= 0) {
            throw new RuntimeException("The 'trimFileNameDisplayTo' property should be a positive integer.");
        }

        Assert.hasText(getDocumentDownloadAction(),
                "The 'documentDownloadAction' property should be a non empty string.");

        Assert.hasText(getDocumentUploadAction(),
                "The 'documentUploadAction' property should be a non empty string.");
    }

    private void addParamIfSet(String name, String value) {
        if (StringUtils.hasText(value)) {
            addParameter(name, value);
        }
    }

    @SuppressWarnings("unchecked")
    private String getUploadedDocsInfo() {
        // Unwrap all occurrences of %{...} since that messes up when we call
        // findValue(name) during processing.
        String sanitizedName = name;

        if (StringUtils.hasText(sanitizedName)) {
            sanitizedName = STRUTS_EXPR_PATTERN.matcher(name).replaceAll("");
        }

        Object alreadyUploadedDocumentsObj = findValue(sanitizedName);

        String uploadedDocsJSON;
        if (singleFileUpload) {
            uploadedDocsJSON = documentToJSON((Document) alreadyUploadedDocumentsObj);
        } else {
            uploadedDocsJSON = documentCollectionToJSON((Collection<Document>) alreadyUploadedDocumentsObj);
        }

        return uploadedDocsJSON;
    }

    private Boolean computeCanDeleteAlreadyUploaded() {
        Boolean returnValue = Boolean.TRUE;
        
        if(StringUtils.hasText(canDeleteAlreadyUploadedIf)) {
            returnValue = (Boolean)
                    findValue(canDeleteAlreadyUploadedIf, Boolean.class);
        }

        return returnValue;
    }
    
    public int getTrimFileNameDisplayTo() {
        return trimFileNameDisplayTo;
    }

    public void setTrimFileNameDisplayTo(int trimFileNameDisplayTo) {
        this.trimFileNameDisplayTo = trimFileNameDisplayTo;
    }

    public String getDocumentDownloadAction() {
        return documentDownloadAction;
    }

    public void setDocumentDownloadAction(String documentDownloadAction) {
        this.documentDownloadAction = documentDownloadAction;
    }

    public String getSelectedFilesCountParam() {
        return selectedFilesCountParam;
    }

    public void setSelectedFilesCountParam(String selectedFilesCountParam) {
        this.selectedFilesCountParam = selectedFilesCountParam;
    }

    public String getDocumentUploadAction() {
        return documentUploadAction;
    }

    public void setDocumentUploadAction(String documentUploadAction) {
        this.documentUploadAction = documentUploadAction;
    }

    public String getDocumentDeletionAction() {
        return documentDeletionAction;
    }

    public void setDocumentDeletionAction(String documentDeletionAction) {
        this.documentDeletionAction = documentDeletionAction;
    }

	public int getMaxFilesToBeUploaded() {
		return maxFilesToBeUploaded;
	}

	public void setMaxFilesToBeUploaded(int maxFilesToBeUploaded) {
		this.maxFilesToBeUploaded = maxFilesToBeUploaded;
	}

	public boolean isSingleFileUpload() {
		return singleFileUpload;
	}

	public void setSingleFileUpload(boolean singleFileUpload) {
		this.singleFileUpload = singleFileUpload;
	}

	public int getFieldSize() {
		return fieldSize;
	}

	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
	}

    public String getCanDeleteAlreadyUploadedIf() {
        return canDeleteAlreadyUploadedIf;
    }

    public void setCanDeleteAlreadyUploadedIf(String canDeleteAlreadyUploadedIf) {
        this.canDeleteAlreadyUploadedIf = canDeleteAlreadyUploadedIf;
    }
    
}