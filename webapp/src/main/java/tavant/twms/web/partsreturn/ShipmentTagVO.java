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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Deepak.patel
 *
 */
public class ShipmentTagVO {
	private Map<String, String> labels = new HashMap<String, String>();
	private List<ClaimWithPartVO> partDetails;
	private ReturnAddressVO returnToAddress;
	private ShipmentVO shipment;
	private String dealerNumber;
	private AddressVO from;
	private String language;
	private String businessUnit;
    private String wpraNumber;
    private String labelShippedDate; 
    private String nonWarrantyAnalysis;
    private List<LoadInformation> loadinfos;
    private String  nmhgToDealerShipment;
	public Map<String, String> getLabels() {
		return labels;
	}
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	public List<ClaimWithPartVO> getPartDetails() {
		return partDetails;
	}
	public void setPartDetails(List<ClaimWithPartVO> partDetails) {
		this.partDetails = partDetails;
	}
	public ReturnAddressVO getReturnToAddress() {
		return returnToAddress;
	}
	public void setReturnToAddress(ReturnAddressVO returnToAddress) {
		this.returnToAddress = returnToAddress;
	}
	public ShipmentVO getShipment() {
		return shipment;
	}
	public void setShipment(ShipmentVO shipment) {
		this.shipment = shipment;
	}
	public String getDealerNumber() {
		return dealerNumber;
	}
	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}
	public AddressVO getFrom() {
		return from;
	}
	public void setFrom(AddressVO from) {
		this.from = from;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getBusinessUnit() {
		return businessUnit;
	}
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

    public String getWpraNumber() {
        return wpraNumber;
    }

    public void setWpraNumber(String wpraNumber) {
        this.wpraNumber = wpraNumber;
    }
    
	public String getLabelShippedDate() {
		return labelShippedDate;
	}
	public void setLabelShippedDate(String labelShippedDate) {
		this.labelShippedDate = labelShippedDate;
	}

    public List<LoadInformation> getLoadinfos() {
        return loadinfos;
    }

    public void setLoadinfos(List<LoadInformation> loadinfos) {
        this.loadinfos = loadinfos;
    }
	/**
	 * @return the nmhgToDealerShipment
	 */
	public String getNmhgToDealerShipment() {
		return nmhgToDealerShipment;
	}
	/**
	 * @param nmhgToDealerShipment the nmhgToDealerShipment to set
	 */
	public void setNmhgToDealerShipment(String nmhgToDealerShipment) {
		this.nmhgToDealerShipment = nmhgToDealerShipment;
	}
	public String getNonWarrantyAnalysis() {
		return nonWarrantyAnalysis;
	}
	public void setNonWarrantyAnalysis(String nonWarrantyAnalysis) {
		this.nonWarrantyAnalysis = nonWarrantyAnalysis;
	}

	
}
