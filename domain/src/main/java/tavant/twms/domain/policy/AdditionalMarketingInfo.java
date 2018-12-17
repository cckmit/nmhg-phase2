package tavant.twms.domain.policy;

import java.sql.Types;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


import org.apache.commons.lang.WordUtils;

@Entity
@Table(name="ADDTL_MARKETING_INFO")
@Filters({
  @Filter(name="excludeInactive")
})
public class AdditionalMarketingInfo implements BusinessUnitAware, AuditableColumns, Comparable<AdditionalMarketingInfo>{
    @Id
    @GeneratedValue(generator = "AdditionalMarketingInfo")
	@GenericGenerator(name = "AdditionalMarketingInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDTL_MARKETING_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;
    
    @ManyToOne
    private ItemGroup applItemGroup;
    
    private String fieldName;
    
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.policy.AdditionalMarketingInfoType"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private AdditionalMarketingInfoType infoType;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ADD_MKTING_INFO_OPTNS_MAPPING", 
	 		   joinColumns = { @JoinColumn(name = "FOR_ADDTL_MKTING_INFO")}, 
			   inverseJoinColumns = { @JoinColumn(name = "OPTION_ID")})
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private List<AdditionalMarketingInfoOptions> options;

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
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

	public ItemGroup getApplItemGroup() {
		return applItemGroup;
	}

	public void setApplItemGroup(ItemGroup applItemGroup) {
		this.applItemGroup = applItemGroup;
	}

	public String getFieldName() {
		return  WordUtils.capitalizeFully(fieldName.toLowerCase());
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public AdditionalMarketingInfoType getInfoType() {
		return infoType;
	}

	public void setInfoType(AdditionalMarketingInfoType infoType) {
		this.infoType = infoType;
	}

	public List<AdditionalMarketingInfoOptions> getOptions() {
		return options;
	}

	public void setOptions(List<AdditionalMarketingInfoOptions> options) {
		this.options = options;
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;		
	}

	public int compareTo(AdditionalMarketingInfo compareAdditionalMarketingInfo) {
        
		String compareFieldName = ((AdditionalMarketingInfo) compareAdditionalMarketingInfo).getFieldName(); 

		return this.fieldName.compareTo(compareFieldName);
	}

}
