package tavant.twms.domain.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import javax.validation.constraints.NotNull;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name = "MISC_ITEM_CRITERIA")
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class MiscellaneousItemCriteria implements AuditableColumns, BusinessUnitAware {

	@Id
    @GeneratedValue(generator = "MiscellaneousItemCriteria")
	@GenericGenerator(name = "MiscellaneousItemCriteria", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MISC_ITEM_CRITERIA_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String configName;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private ServiceProvider serviceProvider;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private DealerGroup dealerGroup;

	private boolean active = true;

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "FOR_CRITERIA", nullable = false)
    private List<MiscellaneousItemConfiguration> itemConfigs = new ArrayList<MiscellaneousItemConfiguration>();

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo= new BusinessUnitInfo();

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @NotNull
    private long relevanceScore = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public DealerGroup getDealerGroup() {
		return dealerGroup;
	}

	public void setDealerGroup(DealerGroup dealerGroup) {
		this.dealerGroup = dealerGroup;
	}

	public List<MiscellaneousItemConfiguration> getItemConfigs() {
		return itemConfigs;
	}

	public void setItemConfigs(List<MiscellaneousItemConfiguration> itemConfigs) {
		this.itemConfigs = itemConfigs;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public MiscellaneousItemConfiguration findConfigurationWithPartNumber(String partNumber){
		String prtNo = StringUtils.stripToEmpty(partNumber);
		if (!StringUtils.isBlank(prtNo) &&  itemConfigs != null && itemConfigs.size() > 0 ) {
			for (MiscellaneousItemConfiguration element : itemConfigs) {
              if(prtNo.equalsIgnoreCase(element.getMiscellaneousItem().getPartNumber())){
            	  return element;
              }
			}
		}
		return null;

	}

	public List<MiscellaneousItemConfiguration> getAllActiveConfigurations() {
		List<MiscellaneousItemConfiguration> list = null;
		if (itemConfigs != null && itemConfigs.size() > 0) {
			list = new ArrayList<MiscellaneousItemConfiguration>();
			for (MiscellaneousItemConfiguration element : itemConfigs) {
				if (element.isActive()) {
					list.add(element);
				}
			}
		}
		return list;
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

	public long getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(long relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

}
