package tavant.twms.domain.campaign.relateCampaigns;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters( { @Filter(name = "excludeInactive") })
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class RelateCampaign implements BusinessUnitAware, AuditableColumns {
	
	@Id
	@GeneratedValue(generator = "RelateCampaign")
	@GenericGenerator(name = "RelateCampaign", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "RELATE_CAMPAIGN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	String code;
	@Column(length = 4000)
	String description;
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinTable(name = "CAMPAIGNS_IN_RELATED_CAMPAIGN", joinColumns = @JoinColumn(name = "RELATED_CAMPAIGNS"), 
	inverseJoinColumns = @JoinColumn(name = "CAMPAIGN"))
	List<Campaign> includedCampaigns = new ArrayList<Campaign>();
	
	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Campaign> getIncludedCampaigns() {
		return includedCampaigns;
	}

	public void setIncludedCampaigns(List<Campaign> includedCampaigns) {
		this.includedCampaigns = includedCampaigns;
	}

}
