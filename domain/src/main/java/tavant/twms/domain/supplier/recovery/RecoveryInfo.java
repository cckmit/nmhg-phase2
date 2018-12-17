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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.UserComment;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author  kaustubhshobhan.b
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class RecoveryInfo implements AuditableColumns, BusinessUnitAware {
    @Id
	@GeneratedValue
    @GenericGenerator(name = "RecoveryInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "RECOVERY_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	private Claim warrantyClaim;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL ,CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "REC_INFO_REP_PARTS_REC")
    private List<RecoveryClaimInfo> replacedPartsRecovery = new ArrayList<RecoveryClaimInfo>();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL })
    @JoinTable(name="RECOVERY_INFO_COMMENTS")
    @Sort(type = SortType.NATURAL)
    private SortedSet<UserComment> comments = new TreeSet<UserComment>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();
    
    private boolean savedAtPartLevel = true;

    public void addReplacedPartsRecovery(RecoveryClaimInfo recoveryClaimInfo){
		this.replacedPartsRecovery.add(recoveryClaimInfo);
	}

	public void addComments(UserComment comment){
		this.comments.add(comment);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Claim getWarrantyClaim() {
		return warrantyClaim;
	}

	public void setWarrantyClaim(Claim warrantyClaim) {
		this.warrantyClaim = warrantyClaim;
	}

    public RecoveryClaimInfo getCausalPartRecovery() {
        for (RecoveryClaimInfo recoveryClaimInfo : this.getReplacedPartsRecovery()) {
            if (recoveryClaimInfo.getContract()!=null && recoveryClaimInfo.isCausalPartRecovery())
                return recoveryClaimInfo;
        }
        return null;
    }

    public void setCausalPartRecovery(RecoveryClaimInfo causalPartRecovery) {
        if (causalPartRecovery != null) {
            causalPartRecovery.setCausalPartRecovery(true);
            this.getReplacedPartsRecovery().add(causalPartRecovery);
        } else {
            this.getReplacedPartsRecovery().remove(this.getCausalPartRecovery());
        }
    }

	public List<RecoveryClaimInfo> getReplacedPartsRecovery() {
		return replacedPartsRecovery;
	}

	public void setReplacedPartsRecovery(List<RecoveryClaimInfo> replacedPartsRecovery) {
		this.replacedPartsRecovery = replacedPartsRecovery;
	}

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
        this.businessUnitInfo = businessUnitInfo;
    }

	public SortedSet<UserComment> getComments() {
		return comments;
	}

	public void setComments(SortedSet<UserComment> comments) {
		this.comments = comments;
	}

	public boolean isSavedAtPartLevel() {
		return savedAtPartLevel;
	}

	public void setSavedAtPartLevel(boolean savedAtPartLevel) {
		this.savedAtPartLevel = savedAtPartLevel;
	}

}
