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
 *   
 *   @author shraddha.nanda
 */
package tavant.twms.domain.claim;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Cascade;
import tavant.twms.domain.common.Document;

@Entity
@Table(name = "HUSS_PARTS_REPLACED_INSTALLED")
public class HussmanPartsReplacedInstalled {

    @Id
    @GeneratedValue(generator = "HussmanPartsReplacedInstalled")
    @GenericGenerator(name = "HussmanPartsReplacedInstalled", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "HUSSPARTS_REPINSTALLED_SEQ"),
            @Parameter(name = "initial_value", value = "200"),
            @Parameter(name = "increment_size", value = "20")})
    protected Long id;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "OEM_REPLACED_PARTS")
    private List<OEMPartReplaced> replacedParts = new ArrayList<OEMPartReplaced>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "OEM_PARTS_INSTALLED")
    private List<InstalledParts> hussmanInstalledParts = new ArrayList<InstalledParts>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "NON_OEM_PARTS_INSTALLED")
    private List<InstalledParts> nonHussmanInstalledParts = new ArrayList<InstalledParts>();

    private Boolean inventoryLevel;//This field is introduced for the PRM Multi Inventory Claims 


    private boolean readOnly;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<OEMPartReplaced> getReplacedParts() {
        return replacedParts;
    }

    public void setReplacedParts(List<OEMPartReplaced> replacedParts) {
        this.replacedParts = replacedParts;
    }

    public List<InstalledParts> getHussmanInstalledParts() {
        return hussmanInstalledParts;
    }

    public void setHussmanInstalledParts(
            List<InstalledParts> hussmanInstalledParts) {
        this.hussmanInstalledParts = hussmanInstalledParts;
    }

    public List<InstalledParts> getNonHussmanInstalledParts() {
        return nonHussmanInstalledParts;
    }

    public void setNonHussmanInstalledParts(
            List<InstalledParts> nonHussmanInstalledParts) {
        this.nonHussmanInstalledParts = nonHussmanInstalledParts;
    }


    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getInventoryLevel() {
        return inventoryLevel;
    }

    public void setInventoryLevel(Boolean inventoryLevel) {
        this.inventoryLevel = inventoryLevel;
    }


    public HussmanPartsReplacedInstalled clone() {
        HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled = new HussmanPartsReplacedInstalled();
        for (OEMPartReplaced oemPartReplaced : replacedParts) {
            hussmanPartsReplacedInstalled.getReplacedParts().add(oemPartReplaced.clone());
        }
        for (InstalledParts installedParts : hussmanInstalledParts) {
            hussmanPartsReplacedInstalled.getHussmanInstalledParts().add(installedParts.clone());
        }
        for (InstalledParts nonInstalledParts : nonHussmanInstalledParts) {
            hussmanPartsReplacedInstalled.getNonHussmanInstalledParts().add(nonInstalledParts.clone());
        }
        hussmanPartsReplacedInstalled.setReadOnly(hussmanPartsReplacedInstalled.isReadOnly());
        hussmanPartsReplacedInstalled.setInventoryLevel(hussmanPartsReplacedInstalled.getInventoryLevel());
        return hussmanPartsReplacedInstalled;
    }
}

