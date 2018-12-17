/*
 *   Copyright (c)2008 Tavant Technologies
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
package tavant.twms.domain.customReports;

import javax.persistence.*;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Cascade;

/**
 * @author kaustubhshobhan.b
 */
@Entity
public class CustomReportInstructions implements AuditableColumns {

    @Id
    @GeneratedValue(generator = "CustomReportInstructions")
    @GenericGenerator(name = "CustomReportInstructions", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CUST_REPORT_INSTRUCTION"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    private String instructions;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Document attachment;

    @Embedded
	@Cascade( {org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	private AuditableColEntity d = new AuditableColEntity();

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Document getAttachment() {
        return attachment;
    }

    public void setAttachment(Document attachment) {
        this.attachment = attachment;
    }
    //The content type varies with IE & Firefox
	public boolean isDocumentImage() {
		if (attachment != null && attachment.getContentType() != null) {
			//if browser used is Firefox
			if (attachment.getContentType().equalsIgnoreCase("image/jpeg")
					|| attachment.getContentType().equalsIgnoreCase("image/png") || 
					//if browser is IE
					attachment.getContentType().equalsIgnoreCase("image/x-png") || 
					attachment.getContentType().equalsIgnoreCase("image/pjpeg"))
				return true;
			else
				return false;
		}
		return true;
	}
    
}
