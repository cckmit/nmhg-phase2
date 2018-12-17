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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Apr 5, 2007
 * Time: 12:50:25 AM
 */

package tavant.twms.domain.category;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import tavant.twms.domain.orgmodel.ServiceProvider;

@Entity
public class DealerCategory extends Category {

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "dealer_category_dealership", joinColumns = { @JoinColumn(name = "dealer_category") }, inverseJoinColumns = { @JoinColumn(name = "dealership") })
    // Using this name is *not* mandatory, but it enables the use of the
    // generic convenience methods defined in CategoryService.
    @Column(name="dealers")
    private Set<ServiceProvider> members = new HashSet<ServiceProvider>();

    public Set<ServiceProvider> getMembers() {
        return members;
    }

    public void setMembers(Set<ServiceProvider> members) {
        this.members = members;
    }

    public void addDealer(ServiceProvider dealer) {
        members.add(dealer);
    }

    public void removeDealer(ServiceProvider dealer) {
        members.remove(dealer);
    }
}
