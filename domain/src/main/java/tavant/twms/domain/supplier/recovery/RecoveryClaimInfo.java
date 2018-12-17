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
package tavant.twms.domain.supplier.recovery;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.CascadeType;

import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author kaustubhshobhan.b
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class RecoveryClaimInfo implements AuditableColumns {
    @Id
	@GeneratedValue(generator = "RecoveryClaimInfo")
    @GenericGenerator(name = "RecoveryClaimInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "RECOVERY_CLAIM_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Contract contract;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "REC_CLAIM_INFO_REC_PARTS",inverseJoinColumns = {@JoinColumn(name = "recoverable_parts")})
    @Cascade({CascadeType.ALL})
    private List<RecoverablePart> recoverableParts = new ArrayList<RecoverablePart>();

	@OneToOne(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
    private RecoveryClaim recoveryClaim;
	
	private boolean causalPartRecovery = false;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public void addRecoverablePart(RecoverablePart recoverablePart){
		this.recoverableParts.add(recoverablePart);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public List<RecoverablePart> getRecoverableParts() {
		return recoverableParts;
	}

	public void setRecoverableParts(List<RecoverablePart> recoverableParts) {
		this.recoverableParts = recoverableParts;
	}

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

	public boolean isCausalPartRecovery() {
		return causalPartRecovery;
	}

	public void setCausalPartRecovery(boolean causalPartRecovery) {
		this.causalPartRecovery = causalPartRecovery;
	}

	public RecoveryClaim getRecoveryClaim() {
		return recoveryClaim;
	}

	public void setRecoveryClaim(RecoveryClaim recoveryClaim) {
		this.recoveryClaim = recoveryClaim;
	}
}
