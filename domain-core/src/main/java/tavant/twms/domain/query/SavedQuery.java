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
package tavant.twms.domain.query;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.security.AuditableColumns;

/**
 * 
 * @author roopali.agrawal
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@SuppressWarnings("serial")
public class SavedQuery implements AuditableColumns,Serializable{
	@Id
	@GeneratedValue(generator = "SavedQuery")
	@GenericGenerator(name = "SavedQuery", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SAVED_QUERY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User createdBy;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private DomainPredicate domainPredicate;

    private boolean temporary;
    
    
    @Lob
    @Column(length = 16777210)
    private String searchQuery;    
    
    private String searchQueryName;
    
    private String context;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public DomainPredicate getDomainPredicate() {
        return this.domainPredicate;
    }

    public void setDomainPredicate(DomainPredicate domainPredicate) {
        this.domainPredicate = domainPredicate;
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

    public boolean isTemporary() {
        return this.temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public String getSearchQueryName() {
		return searchQueryName;
	}

	public void setSearchQueryName(String searchQueryName) {
		this.searchQueryName = searchQueryName;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	
}
