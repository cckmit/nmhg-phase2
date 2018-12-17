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
package tavant.twms.domain.reports;

import junit.framework.TestCase;

import com.domainlanguage.time.CalendarDate;

/**
 * 
 * @author bibin.jacob
 * 
 */
public class ReportServiceImplTest extends TestCase {

    ReportServiceImpl reportService;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        reportService = new ReportServiceImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testfindAllClaimsForCriteria() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        try {
            reportService.findAllClaimsForDealersForCriteria(reportSearchCriteria);
            fail("Should raise an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {

        }
    }
    public void testfindAllClaimsForCriteria_ReportSearchCriteria() {
        ReportSearchCriteria reportSearchCriteria = null;
        try {
            reportService.findAllClaimsForDealersForCriteria(reportSearchCriteria);
            fail("Should raise an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testfindAllClaimsForCriteria_StartDate() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        reportSearchCriteria.setStartDate(null);
        try {
            reportService.findAllClaimsForDealersForCriteria(reportSearchCriteria);
            fail("Should raise an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testfindAllClaimsForCriteria_EndDate() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        reportSearchCriteria.setEndDate(null);
        try {
            reportService.findAllClaimsForDealersForCriteria(reportSearchCriteria);
            fail("Should raise an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testfindAllClaimsForCriteria_StartDateAfterEndDate() {
        ReportSearchCriteria reportSearchCriteria = new ReportSearchCriteria();
        CalendarDate startDate = CalendarDate.date(2008, 5, 1);
        CalendarDate endDate = CalendarDate.date(2007, 5, 1);
        reportSearchCriteria.setStartDate(startDate);
        reportSearchCriteria.setEndDate(endDate);
        try {
            reportService.findAllClaimsForDealersForCriteria(reportSearchCriteria);
            fail("Should raise an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {

        }
    }

}
