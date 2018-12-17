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
package tavant.twms.domain.query.view;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import tavant.twms.domain.orgmodel.User;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Parameter;

/**
 * 
 * @author roopali.agrawal
 * 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "created_by", "type" }))
public class InboxView {   
    @Id
	@GeneratedValue(generator = "InboxView")
	@GenericGenerator(name = "InboxView", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INBOX_VIEW_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String type;

    @Column(nullable = false)
    private String name;

    private transient List<InboxField> fields = new ArrayList<InboxField>();

    @Lob
    @Column(length = 16777210)
    private String fieldNames;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)    
    private User createdBy;
    
//	@Column(nullable = false)
	private String folderName;

    public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Long getId() {
        return id;
    }
    
	@Column(length = 255)
	private String sortByField;
	
	private boolean sortOrderAscending;    

    public String getSortByField() {
		return sortByField;
	}

	public void setSortByField(String sortByField) {
		this.sortByField = sortByField;
	}

	public boolean isSortOrderAscending() {
		return sortOrderAscending;
	}

	public void setSortOrderAscending(boolean sortOrderAscending) {
		this.sortOrderAscending = sortOrderAscending;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InboxField> getFields() {
        if (!(fields.size() > 0)) {
	    	InboxViewFields metaData = InboxViewFieldsFactory.getInstance().getInboxViewFields(type,folderName);
        	//need to be refactored
        	//InboxViewFields metaData = null;
	        // fields=new ArrayList<String>();
	        if (fieldNames != null) {
	            StringTokenizer tokenizer = new StringTokenizer(fieldNames, ",");
	            while (tokenizer.hasMoreTokens()) {
	                fields.add(metaData.getField(tokenizer.nextToken()));
	            }
	        }
        }
        return fields;
    }

    public void setFields(List<InboxField> fields) {
        this.fields = fields;
    }

    public String getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String fieldNames) {
        this.fieldNames = fieldNames;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    

}
