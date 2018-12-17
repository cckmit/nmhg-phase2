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
package tavant.twms.domain.failurestruct;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class FailureType implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "FailureType")
	@GenericGenerator(name = "FailureType", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FAILURE_TYPE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOR_ITEM_GROUP_ID")
    private ItemGroup forItemGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id")
    private FailureTypeDefinition definition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOR_FAULT_CODE")

    private FaultCode forFaultCode;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    
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

    
    public ItemGroup getForItemGroup() {
        return this.forItemGroup;
    }

    public void setForItemGroup(ItemGroup forItemGroup) {
        this.forItemGroup = forItemGroup;
    }

   
    public FaultCode getForFaultCode() {
		return forFaultCode;
	}

	public void setForFaultCode(FaultCode forFaultCode) {
		this.forFaultCode = forFaultCode;
	}

    
    public FailureTypeDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(FailureTypeDefinition defintion) {
        this.definition = defintion;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
