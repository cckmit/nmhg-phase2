package tavant.twms.domain.orgmodel;



import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.ParamDef;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


@Entity
@Filters( { @Filter(name = "excludeInactive") })
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class MinimumLaborRoundUp implements BusinessUnitAware,AuditableColumns {
	
	@Id
	@GeneratedValue
    private Long id;
	
	private Long daysBetweenRepair;
	
	private Long roundUpHours ;
	
	private Boolean applCommericalPolicy ;
	
	private Boolean applCampaignClaim ;
	
	private Boolean applMachineClaim ;
	
	private Boolean applPartsClaim ;
	
    @OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name ="min_lbr_round_up_app_products")
	private List<ItemGroup> applicableProducts;
	
	
	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	
	
	@Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

	// Setters and Getters

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getDaysBetweenRepair() {
		return daysBetweenRepair;
	}



	public void setDaysBetweenRepair(Long daysBetweenRepair) {
		this.daysBetweenRepair = daysBetweenRepair;
	}



	public Long getRoundUpHours() {
		return roundUpHours;
	}



	public void setRoundUpHours(Long roundUpHours) {
		this.roundUpHours = roundUpHours;
	}



	public Boolean getApplCommericalPolicy() {
		return applCommericalPolicy;
	}



	public void setApplCommericalPolicy(Boolean applCommericalPolicy) {
		this.applCommericalPolicy = applCommericalPolicy;
	}



	public Boolean getApplCampaignClaim() {
		return applCampaignClaim;
	}



	public void setApplCampaignClaim(Boolean applCampaignClaim) {
		this.applCampaignClaim = applCampaignClaim;
	}



	public List<ItemGroup> getApplicableProducts() {
		return applicableProducts;
	}



	public void setApplicableProducts(List<ItemGroup> applicableProducts) {
		this.applicableProducts = applicableProducts;
	}



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



	public Boolean getApplMachineClaim() {
		return applMachineClaim;
	}



	public void setApplMachineClaim(Boolean applMachineClaim) {
		this.applMachineClaim = applMachineClaim;
	}



	public Boolean getApplPartsClaim() {
		return applPartsClaim;
	}



	public void setApplPartsClaim(Boolean applPartsClaim) {
		this.applPartsClaim = applPartsClaim;
	}
	
}
