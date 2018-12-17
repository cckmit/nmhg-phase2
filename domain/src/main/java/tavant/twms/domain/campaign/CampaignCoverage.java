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

package tavant.twms.domain.campaign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class CampaignCoverage implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "CampaignCoverage")
	@GenericGenerator(name = "CampaignCoverage", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_COVERAGE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private CampaignRangeCoverage rangeCoverage;
    
    @OneToOne
    @Cascade(CascadeType.ALL)
    private CampaignSerialNumberCoverage serialNumberCoverage;
    
    @Version
    private int version;

    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

    public List<InventoryItem> getItems() {
        Set<InventoryItem> itemsSet = new HashSet<InventoryItem>();
        if(getRangeCoverage() != null && getRangeCoverage().getItems() != null){
            itemsSet.addAll(getRangeCoverage().getItems());
        }
        if(getSerialNumberCoverage() != null && getSerialNumberCoverage().getItems() != null){
            itemsSet.addAll(getSerialNumberCoverage().getItems());
        }
        return new ArrayList<InventoryItem>(itemsSet);
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public CampaignRangeCoverage getRangeCoverage() {
        return rangeCoverage;
    }

    public void setRangeCoverage(CampaignRangeCoverage rangeCoverage) {
        this.rangeCoverage = rangeCoverage;
    }

    public CampaignSerialNumberCoverage getSerialNumberCoverage() {
        return serialNumberCoverage;
    }

    public void setSerialNumberCoverage(CampaignSerialNumberCoverage serialNumberCoverage) {
        this.serialNumberCoverage = serialNumberCoverage;
    }

}