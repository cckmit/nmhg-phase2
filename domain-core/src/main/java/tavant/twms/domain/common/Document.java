/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.domainlanguage.time.CalendarDate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.common.Views;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.TimePoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents document information for a Claim Document. Mapped to the
 * document_history table.
 * @author anshul.khare
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Document implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "Document")
    @GenericGenerator(name = "Document", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "SEQ_Document"),
            @Parameter(name = "initial_value", value = "200"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    @JsonIgnore
    private int version;

    @Lob
    @JsonIgnore
    @Column(length = 1048576)
    private Blob content;

    @JsonView(Views.Public.class)
    private String fileName;

    @JsonIgnore
    @Type(type = "tavant.twms.infra.CalendarTimeUserType")
    private TimePoint uploadedOn;
    
    @JsonView(value=Views.Public.class)
    @ManyToOne(fetch = FetchType.EAGER)
    private User uploadedBy;

    @JsonView(Views.Public.class)
    @Column(name = "DOCUMENT_TYPE")
    private String type;

    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Public.class)
    private String contentType;

    @Column(name = "file_size")
    private int size;

	private Boolean isEligibilityToShare;
    
	private Boolean isSharedWithSupplier;
	
	private Boolean isSharedWithDealer;
	
    private Boolean mandatory;

    private Boolean orphan = Boolean.TRUE;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "ATTACHMENT_TYPE")
    private DocumentType documentType;

    @JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "UNIT_DOCUMENT_TYPE")
	private UnitDocumentType unitDocumentType;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Document(File file) throws Exception {
        this.setFileName((file.getName()));
        this.setContent(BlobProxy.generateProxy(getBytesFromFile(file)));
        this.setDescription("");
        this.setSize((getBytesFromFile(file).length));
        this.setVersion(0);
    }

    public Document(File file, String fileName, String contentType) throws Exception {
        this.setFileName(fileName);
        this.setContent(BlobProxy.generateProxy(getBytesFromFile(file)));
        this.setContentType(contentType);
        this.setDescription("");
        this.setSize((getBytesFromFile(file).length));
        this.setVersion(0);
    }

    public Document() {
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
    	String newFileName = fileName.replaceAll("[^ a-zA-Z0-9.#?&%\\-\\[\\]+()]+","_");
    	// File name with special characters are replaced with _
    	// Modified for SLMSPROD-755
        this.fileName = newFileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Blob getContent() {
        return this.content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TimePoint getUploadedOn() {
        return this.uploadedOn;
    }

    public void setUploadedOn(TimePoint uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public User getUploadedBy() {
        return this.uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("File Name", this.fileName)
                .append("File Description", this.description).append("Content Type",
                                                                     this.contentType)
                .append("File Size", this.size).toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsEligibilityToShare() {		
		return this.isEligibilityToShare == null ? Boolean.FALSE : this.isEligibilityToShare;
	}

	public void setIsEligibilityToShare(Boolean isEligibilityToShare) {
		this.isEligibilityToShare = isEligibilityToShare;
	}
	
	public Boolean getIsSharedWithSupplier() {
		return this.isSharedWithSupplier == null ? Boolean.FALSE : this.isSharedWithSupplier;
	}

	public void setIsSharedWithSupplier(Boolean isSharedWithSupplier) {
		this.isSharedWithSupplier = isSharedWithSupplier;
	}

	public Boolean getIsSharedWithDealer() {
		return this.isSharedWithDealer == null ? Boolean.FALSE : this.isSharedWithDealer;
	}

	public void setIsSharedWithDealer(Boolean isSharedWithDealer) {
		this.isSharedWithDealer = isSharedWithDealer;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

    public Boolean isOrphan() {
        return orphan;
    }

	public void setOrphan(Boolean orphan) {
		this.orphan = orphan;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public UnitDocumentType getUnitDocumentType() {
		return unitDocumentType;
	}

	public void setUnitDocumentType(UnitDocumentType unitDocumentType) {
		this.unitDocumentType = unitDocumentType;
	}
	
    public CalendarDate getUpdatedOn() {
        return d.getUpdatedOn();
    }
}
