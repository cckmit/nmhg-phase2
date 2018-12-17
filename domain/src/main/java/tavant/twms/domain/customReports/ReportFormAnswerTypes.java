package tavant.twms.domain.customReports;
/**
 *   Copyright (c)2008 Tavant Technologies
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
 * @author kaustubhshobhan.b
 */
public enum ReportFormAnswerTypes {

    SMALL_TEXT("Small Text"),
    LARGE_TEXT("Large Text"),
    NUMBER("Number"),
    SINGLE_SELECT("Single Select"),
    MULTI_SELECT("Multi Select"),
    SINGLE_SELECT_LIST("Single Select List"),
    MULTI_SELECT_LIST("Multi Select List"),
    DATE("Date");

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name();
    }

    private ReportFormAnswerTypes(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
