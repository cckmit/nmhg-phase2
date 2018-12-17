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
package tavant.twms.web.partsreturn;

import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.process.PartTaskBean;
import  tavant.twms.web.partsreturn.PrintShipmentVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// TODO : Do we need this bean ? 
public class PartsWithDealerBeans {
    private List<PartTaskBean> partTaskBeans = new ArrayList<PartTaskBean>();
    
    private String shipmentId;
    
    private List<PrintShipmentVO> claimsInShipment = new ArrayList<PrintShipmentVO>();
    
      
    public List<PrintShipmentVO> getClaimsInShipment() {
		return claimsInShipment;
	}

	public void setClaimsInShipment(List<PrintShipmentVO> claimsInShipment) {
		this.claimsInShipment = claimsInShipment;
	}

	public PartsWithDealerBeans(List<PartTaskBean> partTaskBeans, String shipmentId) {
        this.partTaskBeans = partTaskBeans;
        this.shipmentId = shipmentId;
    }

    public Location getReturnLocation() {
        if(partTaskBeans.size() > 0)
            return partTaskBeans.get(0).getPart().getPartReturns().get(0).getReturnLocation();
        else
            return null;
    }
    
    public ServiceProvider getDealer() {
        if(partTaskBeans.size() > 0)
            return partTaskBeans.get(0).getClaim().getForDealer();
        else
            return null;
    }
        
    public List<PartTaskBean> getPartTaskBeans() {
        return partTaskBeans;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public Shipment getShipment(){
        if(partTaskBeans.size() > 0){
            return partTaskBeans.get(0).getPart().getShipment();
        }
        return null;
    }

}
