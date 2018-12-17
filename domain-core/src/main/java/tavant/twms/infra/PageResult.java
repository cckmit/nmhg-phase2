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

package tavant.twms.infra;

import java.util.List;

public class PageResult<T> {
    List<T> result;

    PageSpecification pageSpecification;

    int numberOfPagesAvailable;

    public PageResult(List<T> result, PageSpecification pageSpecification, int numberOfPagesAvailable) {
        this.result = result;
        this.pageSpecification = pageSpecification;
        this.numberOfPagesAvailable = numberOfPagesAvailable;
    }

    public PageSpecification getPageSpecification() {
        return pageSpecification;
    }

    public int getNumberOfPagesAvailable() {
        return numberOfPagesAvailable;
    }

    public List<T> getResult() {
        return result;
    }

}
