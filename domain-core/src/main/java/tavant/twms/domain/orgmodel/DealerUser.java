/*
 *   Copyright (c)2007 Tavant Technologies
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * @author bharat.kumar
 * 
 */
@Entity
public class DealerUser {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    private String firstName;

    private String lastName;

    private Boolean claimSubmitter = false;

    private Boolean salesPerson = false;

    private Boolean technician = false;

    public DealerUser() {
        // for hibernate
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getClaimSubmitter() {
        return claimSubmitter;
    }

    public void setClaimSubmitter(Boolean claimSubmitter) {
        this.claimSubmitter = claimSubmitter;
    }

    public Boolean getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(Boolean salesPerson) {
        this.salesPerson = salesPerson;
    }

    public Boolean getTechnician() {
        return technician;
    }

    public void setTechnician(Boolean technician) {
        this.technician = technician;
    }
}
