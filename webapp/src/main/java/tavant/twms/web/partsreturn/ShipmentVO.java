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

import com.domainlanguage.time.CalendarDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author kuldeep.patil
 *
 */
public class ShipmentVO {
	private String shipmentNumber;
	private String shipmentDate;
	private String tackingNumber;
	private String barcode;
	private String senderComments;
    private String printDate;
    private String carrier;
    private String location;
    private String nonWarrantyAnalysis;
	public String getShipmentNumber() {
		return shipmentNumber;
	}
	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}
	public String getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public String getTackingNumber() {
		return tackingNumber;
	}
	public void setTackingNumber(String tackingNumber) {
		this.tackingNumber = tackingNumber;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getSenderComments() {
		return senderComments;
	}
	public void setSenderComments(String senderComments) {
		this.senderComments = senderComments;
	}

    public String getPrintDate() {
        return printDate;
    }

    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }
	/**
	 * @return the carrier
	 */
	public String getCarrier() {
		return carrier;
	}
	/**
	 * @param carrier the carrier to set
	 */
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNonWarrantyAnalysis() {
		return nonWarrantyAnalysis;
	}
	public void setNonWarrantyAnalysis(String nonWarrantyAnalysis) {
		this.nonWarrantyAnalysis = nonWarrantyAnalysis;
	}

    /*public List<LoadInformation> getLoadinfo() {
        return loadinfo;
    }

    public void setLoadinfo(List<LoadInformation> loadinfo) {
        this.loadinfo = loadinfo;
    }*/
}
