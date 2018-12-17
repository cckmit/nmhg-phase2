/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.admin.Permission;
import tavant.twms.domain.admin.SubjectArea;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
        @Filter(name = "excludeInactive")
})
@SuppressWarnings("serial")
public class Role implements Serializable, AuditableColumns, Comparable<Role> {
    public static final String CUSTOMER = "customer";
    public static final String DEALER = "dealer";
    public static final String PROCESSOR = "processor";
    public static final String ADMIN = "admin";
    public static final String DSM = "dsm";
    public static final String RECEIVER = "receiver";
    public static final String INSPECTOR = "inspector";
    public static final String SUPPLIER = "supplier";
    public static final String SRA = "sra";
    public static final String PARTSHIPPER = "partshipper";
    public static final String SYSTEM = "system";
    public static final String RECOVERYPROCESSOR = "recoveryProcessor";
    public static final String TECHNICIAN = "technician";
    public static final String SALES_PERSON = "salesPerson";
    public static final String DSM_ADVISOR = "dsmAdvisor";
    public static final String SYS_ADMIN = "sysAdmin";
    public static final String INVENTORY_ADMIN = "inventoryAdmin";
    public static final String READ_ONLY = "readOnly";
    public static final String READ_ONLY_DEALER = "readOnlyDealer";
    public static final String INVENTORY_FULL_VIEW = "inventoryFullView";
    public static final String TK_USER = "partInventoryDealer";
    public static final String TK_INTERNAL_USER = "partInventoryAdmin";
    public static final String THIRDPARTYPRIVILEGE = "thirdPartyPrivilege";
    public static final String CP_ADVISOR = "cpAdvisor";
    //This role is assumed to be assigned to only internal users who can view FOC claims
    public static final String VIEW_FOC_INBOX = "viewFOCInbox";
    public static final String DEALER_SALES_ADMIN = "dealerSalesAdministration";
    public static final String DEALER_WARRANTY_ADMIN = "dealerWarrantyAdmin";
    public static final String DEALER_ADMIN = "dealerAdministrator";
    public static final String BASE_ROLE = "baserole";
    public static final String INVENTORY_SEARCH = "inventorysearch";
    public static final String INVENTORY_LISTING = "inventorylisting";
    //This role is assigned to all users (internal and external users) who are supposed to see the foc claims
    public static final String VIEW_FOC_CLAIMS = "viewFOCClaims";
    //This role is assigned to all dealers users who can manage sites.
    public static final String DEALER_SITE_ADMIN = "dealerSiteAdmin";
    //These roles are created for TK Marines
    public static final String RECEIVER_LIMITED_VIEW = "receiverLimitedView";
    public static final String INSPECTOR_LIMITED_VIEW = "inspectorLimitedView";
    public static final String PART_SHIPPER_LIMITED_VIEW = "partShipperLimitedView";
    public static final String REDUCED_COVERAGE_REQUESTS_APPROVER = "reducedCoverageRequestsApprover";
    public static final String INTERNAL_USER_ADMIN = "internalUserAdmin";

    public static final String SUPPLIER_REC_INITIATOR = "supplierRecoveryInitiator";
    public static final String CEVA_PROCESSOR = "cevaProcessor";
    public static final String NCR_ADVISOR = "ncrAdvisor";
    public static final String NCR_PROCESSOR = "ncrProcessor";
    
  //These role has been created  for internal users for late fee approval
    public static final String WARRANTY_SUPERVISOR="warrantySupervisor";

    public static final String FLEET_SERVICE_SPECIALIST = "fleetServiceSpecialist";
   
    public static final String ACCOUNT_MANAGER = "accountManager";
    public static final String FLEET_COORDINATOR = "fleetCoordinator";
    public static final String FLEET_CONSULTANT = "fleetConsultant";
    public static final String FLEET_MANAGER = "fleetManager";
    public static final String DEALER_OWNED = "dealerOwned";
    public static final String FLEET_PROCESSOR = "fleetProcessor";
    public static final String OPERATIONAL_MANAGER = "operationalManager";
    public static final String CUSTOMER_INQUIRY = "customerInquiry";
    public static final String CUSTOMER_SERVICE_REQUESTER = "customerServiceRequester";
    public static final String CUSTOMER_QUOTE_APPROVER = "customerQuoteApprover";
    public static final String CUSTOMER_PRE_INVOICE_APPROVER = "customerPreInvoiceApprover";
    public static final String CUSTOMER_ADMIN = "customerAdmin";
    public static final String DEALER_INQUIRY = "dealerInquiry";
    public static final String DEALER_SUB_CONTRACT_APPROVER = "dealerSubContractApprover";
    public static final String DEALER_CLAIM_PROCESSOR = "dealerClaimProcessor";
    public static final String DEALER_SERVICE_REQUEST_CREATOR = "dealerServiceRequestCreator";
    public static final String DEALER_QUOTE_CREATOR = "dealerQuoteCreator";
    public static final String DEALER_EQUIPMENT_STATUS_UPDATER = "dealerEquipmentStatusUpdater";
    public static final String FLEET_DEALER_ADMIN = "fleetDealerAdministrator";
    public static final String INTERNAL_INQUIRY = "internalInquiry";
    public static final String FlEET_ADMIN = "fleetAdmin";
    public static final String CALL_CENTER_GROUP = "callCenterGroup";
    public static final String DEALER_OWNED_FLEET_PROCESSOR = "dealerOwnedFleetProcessor";
    public static final String DEALER_OWNED_DISPATCHER = "dealerOwnedDispatcher";
    public static final String DEALER_OWNED_SERVICE_WRITER = "dealerOwnedServiceWriter";
    public static final String DEALER_OWNED_SERVICE_MANAGER = "dealerOwnedServiceManager";
    public static final String DEALER_OWNED_ADMIN = "dealerOwnedAdmin";
    public static final String DEALER_OWNED_BRANCH_MANAGER = "dealerOwnedBranchManager";
    public static final String DEALER_OWNED_EXECUTIVE_MANAGER = "dealerOwnedExecutiveManager";
    public static final String DEALER_OWNED_COORDINATOR = "dealerOwnedCoordinator";
    public static final String DEALER_OWNED_ACCOUNT_MANAGER = "dealerOwnedAccountManager";
  
    @Id
    @AccessType("field")
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    private String name;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.orgmodel.RoleType"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private RoleType roleType;
    
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.orgmodel.RoleCategory"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private RoleCategory roleCategory;

    private String description;

    private String displayName;
    
    private boolean primaryRole = false;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = Permission.class)
    @JoinColumn(name = "ROLE_DEF_ID", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<Permission> permissions = new ArrayList<Permission>();

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the roleCategory
     */
    public RoleCategory getRoleCategory() {
		return roleCategory;
	}

	/**
	 * @param roleCategory the roleCategory to set
	 */
	public void setRoleCategory(RoleCategory roleCategory) {
		this.roleCategory = roleCategory;
	}

	@Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("name", this.name).toString();
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (Hibernate.getClass(this)!= Hibernate.getClass(obj))
            return false;
        final Role other = (Role) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        return true;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public int compareTo(Role otherRole) {

        final String thisDisplayName = getDisplayName();
        final String otherDisplayName = otherRole.getDisplayName();

        if (thisDisplayName == null || otherDisplayName == null) {
            return -1; // anything other than 0 is fine
        } else {
            return thisDisplayName.compareTo(otherDisplayName);
        }
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissionForSubjectArea(SubjectArea subjecArea) {
        List<Permission> permList = new ArrayList<Permission>();
        for (Permission permission : this.permissions) {
            if (permission.getSubjectArea().getId().longValue() == subjecArea
                    .getId().longValue()) {
                permList.add(permission);
            }
        }
        return permList;

    }

    public static boolean containsRoleType(List<Role> roles, RoleType roletype) {
        for (Role role : roles) {
            if (role.roleType == roletype)
                return true;
        }
        return false;
    }

    public static class RoleComparator implements java.util.Comparator<Role> {
        public int compare(Role role1, Role role2) {
            if (role1 == role2) return 0;
            if (role2 == null || role2.getName() == null) {
                return 1;
            }
            if (role1 == null || role1.getName() == null) {
                return 1;
            }
            return role1.getName().toLowerCase().compareTo(
                    role2.getName().toLowerCase());

        }
    }

	public boolean isPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(boolean primaryRole) {
		this.primaryRole = primaryRole;
	}
}
