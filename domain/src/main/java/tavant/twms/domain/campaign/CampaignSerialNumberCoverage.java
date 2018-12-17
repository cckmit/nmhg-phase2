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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.inventory.InventoryItem;

@Entity
@Table(name="CAMPAIGN_SNO_COVERAGE")
public class CampaignSerialNumberCoverage {

   @Id
    @GeneratedValue(generator = "CampaignSerialNumberCoverage")
	@GenericGenerator(name = "CampaignSerialNumberCoverage", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_SN_COVERAGE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;    
    
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="CAMPAIGN_SNO_COVERAGE_SNO")
	private List<CampaignSerialNumbers> serialNumbers;

	public List<CampaignSerialNumbers> getSerialNumbers() {
		return serialNumbers;
	}

	public void setSerialNumbers(List<CampaignSerialNumbers> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}
    
    @ManyToMany
    private List<InventoryItem> items;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }
    
    
}