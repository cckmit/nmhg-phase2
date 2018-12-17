/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.claim.dto;

import java.math.BigDecimal;

/**
 * @author kaustubhshobhan.b
 *
 */
public class ServiceInformationDTO {
    private String oemItemNo;

    private String oemSerialNo;

    private int oemQuantity;

    private String nonOemDescription;

    private String nonOemQuantity;

    private String nonOemPrice;

    private String jobCode;

    private BigDecimal hoursSpent;

    public BigDecimal getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(BigDecimal hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getNonOemDescription() {
        return nonOemDescription;
    }

    public void setNonOemDescription(String nonOemDescription) {
        this.nonOemDescription = nonOemDescription;
    }

    public String getNonOemPrice() {
        return nonOemPrice;
    }

    public void setNonOemPrice(String nonOemPrice) {
        this.nonOemPrice = nonOemPrice;
    }

    public String getNonOemQuantity() {
        return nonOemQuantity;
    }

    public void setNonOemQuantity(String nonOemQuantity) {
        this.nonOemQuantity = nonOemQuantity;
    }

    public String getOemItemNo() {
        return oemItemNo;
    }

    public void setOemItemNo(String oemItemNo) {
        this.oemItemNo = oemItemNo;
    }

    public int getOemQuantity() {
        return oemQuantity;
    }

    public void setOemQuantity(int oemQuantity) {
        this.oemQuantity = oemQuantity;
    }

    public String getOemSerialNo() {
        return oemSerialNo;
    }

    public void setOemSerialNo(String oemSerialNo) {
        this.oemSerialNo = oemSerialNo;
    }
}
