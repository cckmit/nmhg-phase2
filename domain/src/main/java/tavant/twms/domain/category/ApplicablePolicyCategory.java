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
 * Time: 12:55:08 AM
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
import javax.persistence.Table;

import tavant.twms.domain.policy.PolicyDefinition;

@Entity
@Table(name="policy_category")
@Deprecated
public class ApplicablePolicyCategory extends Category {

    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(name = "policy_category_policy", joinColumns = { @JoinColumn(name = "policy_category") }, inverseJoinColumns = { @JoinColumn(name = "policy") })
    @Column(name="policies")    
    // Using this name is *not* mandatory, but it enables the use of the
    // generic convenience methods defined in CategoryService.
    private Set<PolicyDefinition> members = new HashSet<PolicyDefinition>();

    public Set<PolicyDefinition> getMembers() {
        return members;
    }

    public void setMembers(Set<PolicyDefinition> members) {
        this.members = members;
    }

    public void addPolicy(PolicyDefinition policy) {
        members.add(policy);
    }

    public void removePolicy(PolicyDefinition policy) {
        members.remove(policy);
    }
}
