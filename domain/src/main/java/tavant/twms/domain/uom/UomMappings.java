package tavant.twms.domain.uom;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import javax.validation.constraints.NotNull;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NFailureTypeDefinition;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class UomMappings implements BusinessUnitAware,AuditableColumns{


	@Id
	@GeneratedValue(generator = "UomMappings")
	@GenericGenerator(name = "UomMappings", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "UOM_MAPPINGS_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	
	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.catalog.ItemUOMTypes"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private ItemUOMTypes baseUom;

	@NotNull
	private String mappedUom;

	@NotNull
	private Long mappingFraction;

	@Version
	private int version;

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	private AuditableColEntity d = new AuditableColEntity();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "UOM_MAPPING_ID", nullable = false)
	private List<I18nUomMappingValues> i18nUomMappings = new ArrayList<I18nUomMappingValues>();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ItemUOMTypes getBaseUom() {
		return baseUom;
	}

	public void setBaseUom(ItemUOMTypes baseUom) {
		this.baseUom = baseUom;
	}

	public String getMappedUom() {
		return mappedUom;
	}

	public void setMappedUom(String mappedUom) {
		this.mappedUom = mappedUom;
	}

	public Long getMappingFraction() {
		return mappingFraction;
	}

	public void setMappingFraction(Long mappingValue) {
		this.mappingFraction = mappingValue;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public List<I18nUomMappingValues> getI18nUomMappings() {
		return i18nUomMappings;
	}

	public void setI18nUomMappings(List<I18nUomMappingValues> uomMappings) {
		i18nUomMappings = uomMappings;
	}

	public String getMappedUomDescription() {
	 		String localizedMappedUOM = this.mappedUom;
			for (I18nUomMappingValues element:this.i18nUomMappings) {
				if (element!=null && element.getLocale()!=null &&
						element.getLocale().equalsIgnoreCase(
						new SecurityHelper().getLoggedInUser().getLocale()
								.toString()) && element.getMappedUom() != null) {
					localizedMappedUOM = element.getMappedUom();
					break;
				}
				else if(element !=null && element.getLocale()!=null &&
						element.getLocale().equalsIgnoreCase("en_US")) {
					localizedMappedUOM = element.getMappedUom();
				}
			}
		return localizedMappedUOM;
	}

}
