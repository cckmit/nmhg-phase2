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

public class PageSpecification {
    private int pageToFetch;

    private int pageSize;

    private long totalRecords;
    
    public PageSpecification() {
        pageSize = DEFAULT_PAGE_SIZE;
    }    
    
    public PageSpecification(int pageNumber, int pageSize) {
        if (pageSize <= 0 ) {
            throw new IllegalArgumentException("Page size should be non zero positive number");
        }
        if (pageNumber < 0 ) {
            throw new IllegalArgumentException("Page number should be a non negative number");
        }
        this.pageToFetch = pageNumber;
        this.pageSize = pageSize;
    }

    public PageSpecification(int pageNumber, int pageSize,long totalRecords) {
        if (pageSize <= 0 ) {
            throw new IllegalArgumentException("Page size should be non zero positive number");
        }
        if (pageNumber < 0 ) {
            throw new IllegalArgumentException("Page number should be a non negative number");
        }
        if (totalRecords < 0 ) {
            throw new IllegalArgumentException("Total records should be a non negative number");
        }
        this.pageToFetch = pageNumber;
        this.pageSize = pageSize;
        this.totalRecords=totalRecords;
    }

    public int getPageNumber() {
        return pageToFetch;
    }

    public PageSpecification setPageNumber(int pageNumber) {
        this.pageToFetch = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageSpecification setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int convertRowsToPages(long count) {
        if  (count == 0) {
            totalRecords=0;
            return 0;
        }
        totalRecords=count;
        return (int) (count / pageSize) + 
                    ((count % pageSize) > 0 ? 1 : 0);
    }

    public Integer offSet() {
        return pageToFetch * pageSize;
    }
    
    public static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        buf.append("pageSize").append('=').append(pageSize);
        buf.append(',');
        buf.append("pageToFetch").append('=').append(pageToFetch);
        buf.append(")");
        return buf.toString();
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
