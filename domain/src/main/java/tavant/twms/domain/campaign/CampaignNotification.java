package tavant.twms.domain.campaign;

import java.sql.Types;
import java.util.ArrayList;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.common.Document;
import tavant.twms.security.AuditableColumns;


@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "campaign", "item" }))
public class CampaignNotification implements AuditableColumns{
	
	public static final String COMPLETE = "COMPLETE";
	public static final String INPROCESS = "IN PROGRESS";
	public static final String PENDING = "PENDING";
	public static final String INCOMPLETE = "CAN NOT BE COMPLETED ";
	
    @Id
    @GeneratedValue(generator = "CampaignNotification")
	@GenericGenerator(name = "CampaignNotification", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_NOTIFICATION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceProvider dealership;

    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryItem item;

    @OneToOne(fetch = FetchType.LAZY)
    private Claim claim;

    private String notificationStatus;
    
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @Cascade({})
    @JoinColumn(name = "field_mod_inv_status")
     private FieldModificationInventoryStatus fieldModInvStatus;
    
    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "camp_notification_attachments", joinColumns = { @JoinColumn(name = "campaign_notification") },
    		inverseJoinColumns = { @JoinColumn(name = "attachments") })
    private List<Document> attachments = new ArrayList<Document>();
   
    
    private String comments;
    
    private String status;
    
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.campaign.FieldModUpdateStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private FieldModUpdateStatus campaignStatus;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_field_mod", nullable = false, updatable = false,insertable = true)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	private List<FieldModUpdateAudit> fieldModUpdateAudit = new ArrayList<FieldModUpdateAudit>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ServiceProvider getDealership() {
        return this.dealership;
    }

    public void setDealership(ServiceProvider dealership) {
        this.dealership = dealership;
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

    public InventoryItem getItem() {
        return this.item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    	public Campaign getCampaign() {
        return this.campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Claim getClaim() {
        return this.claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public String getNotificationStatus() {
        return this.notificationStatus;
    }
    
    public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	public FieldModificationInventoryStatus getFieldModInvStatus() {
		return fieldModInvStatus;
	}

	public void setFieldModInvStatus(
			FieldModificationInventoryStatus fieldModInvStatus) {
		this.fieldModInvStatus = fieldModInvStatus;
	}
	
	
	public FieldModUpdateStatus getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(FieldModUpdateStatus campaignStatus) {
		this.campaignStatus = campaignStatus;
	}
	
	public List<FieldModUpdateAudit> getFieldModUpdateAudit() {
		return fieldModUpdateAudit;
	}

	public void setFieldModUpdateAudit(List<FieldModUpdateAudit> fieldModUpdateAudit) {
		this.fieldModUpdateAudit = fieldModUpdateAudit;
	}

	public String getNotificationStatusForAdmin(){
		if(this.notificationStatus.equalsIgnoreCase(COMPLETE)){
			if(this.claim != null && ClaimState.DRAFT.getState().equals(this.claim.getState().getState())){
				return INPROCESS;
			}else{
				return this.notificationStatus;
			}
		}
		return this.notificationStatus;
	}

  public String getDealerUpdateStatuswithReason()
	{    String reason=new String();
		String status=this.getStatus();
			if(status.equalsIgnoreCase("Inactive"))
			{
			 reason =status+"("+this.getFieldModInvStatus().getCode()+")";
			}
			else
			{
			reason=status;
			}
			return reason;			
	}
	public String getEndCustomerForUnit() {
        if (this.item.isRetailed()) {
              return this.item.getLatestBuyer().getName();
        } else {
              return null;
        }

  }
	
	
	 public Party getEndCustomer() {
         if (this.item.isRetailed()) {
               return this.item.getLatestBuyer();
         } else {
               return null;
         }
	 }
	
	public String getStatuswithReason()
	{
		String status=this.getStatus();
		if(status.equalsIgnoreCase("Inactive")){
			if(!this.getCampaignStatus().equals(FieldModUpdateStatus.ACCEPTED)){
				status = "Active";
			}
			else
				status = status+"("+this.getFieldModInvStatus().getCode()+")";
		}
			return status;
	}
	
	public String getStatuswithReasonForExcel() {
		String status = this.getStatus();
		if (campaign.getStatus().equalsIgnoreCase("Active")) {
			if (status.equalsIgnoreCase("Inactive")) {
				if (!this.getCampaignStatus().equals(
						FieldModUpdateStatus.ACCEPTED)) {
					status = "Active";
				} else
					status = status + "("
							+ this.getFieldModInvStatus().getCode() + ")";
			}
		} else {
			status = campaign.getStatus();
		}
		return status;
	}
}
