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
package tavant.twms.web.admin.campaign;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiran.Kollipara
 */
public class UploadSummary {

    private int totalCount = 0;
    private int validCount = 0;
    
    private List<String> invalidNumbers = new ArrayList<String>();
    
    public void incrementValidCount() {
        validCount++;
    }
    
    public void incrementTotalCount() {
        totalCount++;
    }
    
    public void addInvalidNumber(String aSerialNumber) {
        invalidNumbers.add(aSerialNumber);
    }

    public String getInvalidNumbers() {
        String slNoString = StringUtils.collectionToCommaDelimitedString(invalidNumbers);
        slNoString = StringUtils.replace(slNoString, ",", " ,");
        return slNoString;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public int getValidCount() {
        return validCount;
    }
}