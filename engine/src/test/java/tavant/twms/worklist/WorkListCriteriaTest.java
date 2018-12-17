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
package tavant.twms.worklist;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.EngineRepositoryTestCase;


public class WorkListCriteriaTest extends EngineRepositoryTestCase {
    
    private WorkListCriteria criteria;
    
    private OrgService orgService;

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
    }
    
    public void testCriteriaNullUser() {
        try {
            criteria = new WorkListCriteria(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Exception expected.
        }
    }
    
    public void testCriteriaValidUser() {
        try {
            criteria = new WorkListCriteria(orgService.findUserByName("ann"));
        }catch (IllegalArgumentException e) {
            fail();
        }
    }
    
    public void testAddSortCriteriaNullColumn() {
        try {
            criteria = new WorkListCriteria(orgService.findUserByName("ann"));
            criteria.addSortCriteria(null, false);
            for(String columnName : criteria.getSortCriteria().keySet()) {
                assertNull(columnName);
            }
        } catch(Exception e) {
            fail();
        }
    }
    
    public void testAddSortCriteriaValidColumn() {
        try {
            criteria = new WorkListCriteria(orgService.findUserByName("ann"));
            criteria.addSortCriteria("taskName", false);
            for(String columnName : criteria.getSortCriteria().keySet()) {
                assertNotNull(columnName);
                assertEquals("desc", criteria.getSortCriteria().get(columnName).toString().trim());
            }
        } catch(Exception e) {
            fail();
        }
    }
    
    public void testAddFilterCriteriaNullColumn() {
        try {
            criteria = new WorkListCriteria(orgService.findUserByName("ann"));
            criteria.addFilterCriteria(null, "");
            for(String columnName : criteria.getFilterCriteria().keySet()) {
                assertNull(columnName);
            }
        } catch(Exception e) {
            fail();
        }
    }
    
    public void testAddFilterCriteriaValidColumn() {
        try {
            criteria = new WorkListCriteria(orgService.findUserByName("ann"));
            criteria.addFilterCriteria("taskName", "");
            for(String columnName : criteria.getFilterCriteria().keySet()) {
                assertNotNull(columnName);
            }
        } catch(Exception e) {
            fail();
        }
    }

    //setters
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
    
}
