package tavant.twms.taglib.uploadDocument;

import org.apache.struts2.views.jsp.ui.AbstractUITag;
import org.apache.struts2.components.Component;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 29 Jun, 2009
 * Time: 6:53:10 PM
 */
public class UploadDocumentTag extends AbstractUITag {

    private int trimFileNameDisplayTo = 20;
    private String selectedFilesCountParam;
    private String documentDownloadAction = "download_document";
    private String documentUploadAction = "upload_document";
    private String documentDeletionAction = "delete_document";
    private int maxFilesToBeUploaded = 50;
    private boolean singleFileUpload = false;
    private int fieldSize = 1;
    private String canDeleteAlreadyUploadedIf;

    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new UploadDocument(stack, request, response);
    }

    protected void populateParams() {
        super.populateParams();

        UploadDocument uploadDocument = (UploadDocument) component;

        uploadDocument.setTrimFileNameDisplayTo(getTrimFileNameDisplayTo());
        uploadDocument.setMaxFilesToBeUploaded(getMaxFilesToBeUploaded());
        uploadDocument.setSingleFileUpload(singleFileUpload);
        uploadDocument.setSelectedFilesCountParam(getSelectedFilesCountParam());
        uploadDocument.setDocumentDownloadAction(getDocumentDownloadAction() + ".action");
        uploadDocument.setDocumentUploadAction(getDocumentUploadAction() + ".action");
        uploadDocument.setDocumentDeletionAction(getDocumentDeletionAction() + ".action");
        uploadDocument.setFieldSize(getFieldSize());
        uploadDocument.setCanDeleteAlreadyUploadedIf(getCanDeleteAlreadyUploadedIf());
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

	public String getSingleFileUpload() {
		return singleFileUpload ? "true" : "false";
	}

	public void setSingleFileUpload(String singleFileUpload) {
		this.singleFileUpload = singleFileUpload.equals("true") ? true : false;
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