/**
 *
 */
package tavant.twms.domain.partreturn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

/**
 * @author aniruddha.chaturvedi
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class Warehouse implements BusinessUnitAware,AuditableColumns{

	@Id
	@GeneratedValue(generator = "Warehouse")
	@GenericGenerator(name = "Warehouse", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "WAREHOUSE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @OneToOne(cascade = CascadeType.ALL)
    @Sort(type = SortType.NATURAL)
    private Location location;

    @OneToMany
    private List<User> recievers;

    @OneToMany
    private List<User> inspectors;

    @OneToMany
    private List<User> partShippers;

    @CollectionOfElements
    @Sort(type = SortType.NATURAL)
    private Set<String> warehouseBins;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String contactPersonName;
    
    private String businessName;
    
    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
        org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinColumn(name="warehouse")
    private List<WarehouseShippers> warehouseShippers = new ArrayList<WarehouseShippers>();

    public Warehouse() {
    }

    public Warehouse(String code, Address address) {
        Location location = new Location(code, address);
        this.location = location;
    }
    
    @ManyToMany
	private Set<Label> labels = new HashSet<Label>();

    // Only getters and Setters follow.
    public List<User> getInspectors() {
        return this.inspectors;
    }

	public void setInspectors(List<User> inspectors) {
        this.inspectors = inspectors;
    }

    public List<User> getPartShippers() {
        return this.partShippers;
    }

    public void setPartShippers(List<User> partShippers) {
        this.partShippers = partShippers;
    }

    public List<User> getRecievers() {
        return this.recievers;
    }

    public void setRecievers(List<User> recievers) {
        this.recievers = recievers;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public void setWarehouseBins(Set<String> bins) {
        this.warehouseBins = bins;
    }

    public Set<String> getWarehouseBins() {
        return this.warehouseBins;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public List<WarehouseShippers> getWarehouseShippers() {
		return warehouseShippers;
	}

	public void setWarehouseShippers(List<WarehouseShippers> warehouseShippers) {
		this.warehouseShippers = warehouseShippers;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Set<Label> getLabels() {
		return labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}
	
}
