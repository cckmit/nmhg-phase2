package tavant.twms.domain.claim;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name = "Mktg_Groups_Lookup")
@Filters({ @Filter(name = "excludeInactive") })
public class MarketingGroupsLookup implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "MarketingGroupsLookup")
	@GenericGenerator(name = "MarketingGroupsLookup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "mktg_groups_lookup_seq"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	private String truckMktgGroupCode;

	private String claimType;

	private String warrantyType;

	private String dealerMktgGroupCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTruckMktgGroupCode() {
		return truckMktgGroupCode;
	}

	public void setTruckMktgGroupCode(String truckMktgGroupCode) {
		this.truckMktgGroupCode = truckMktgGroupCode;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public String getWarrantyType() {
		return warrantyType;
	}

	public void setWarrantyType(String warrantyType) {
		this.warrantyType = warrantyType;
	}

	public String getDealerMktgGroupCode() {
		return dealerMktgGroupCode;
	}

	public void setDealerMktgGroupCode(String dealerMktgGroupCode) {
		this.dealerMktgGroupCode = dealerMktgGroupCode;
	}

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;

	}
}
