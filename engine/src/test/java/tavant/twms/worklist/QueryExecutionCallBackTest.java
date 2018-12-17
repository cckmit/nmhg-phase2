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

import java.util.HashMap;
import java.util.Map;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageSpecification;

/**
 * This test is very well tied to OrgData.xls
 * @author kannan.ekanath
 * 
 */
public class QueryExecutionCallBackTest extends EngineRepositoryTestCase {

    private String queryString = "from User u where u.belongsToOrganization.id = :org";

    public void testQueryExecutionCallBack() throws Exception {
        ListCriteria criteria = new ListCriteria();
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageNumber(1);
        pageSpecification.setPageSize(3);
        criteria.setPageSpecification(pageSpecification);
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("org", new Long(8));
        QueryExecutionCallBack callBack = new QueryExecutionCallBack("u", queryString, criteria, map);

        InboxItemList inboxItemList = (InboxItemList) callBack.doInHibernate(getSession());
        assertTrue("Atleast 15 users belong to the org with id 8 in test data",
                inboxItemList.getTotalCount() == 15);
        // however only 3 pages would be fetched
        assertTrue("Page size is set to 3 ", inboxItemList.getInboxItems().size() == 3);
    }
    
    public void testFilters() throws Exception {
        ListCriteria criteria = new ListCriteria();
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageNumber(1);
        pageSpecification.setPageSize(3);
        criteria.setPageSpecification(pageSpecification);
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("org", new Long(8));
        
        //ones whose supervisor is 4
        criteria.addFilterCriteria("u.password", "tavant");
        QueryExecutionCallBack callBack = new QueryExecutionCallBack("u", queryString, criteria, map);

        InboxItemList inboxItemList = (InboxItemList) callBack.doInHibernate(getSession());
        assertTrue("Only 15 users belong to the org with id 8 in test data with same password",
                inboxItemList.getTotalCount() == 15);
        // however only 3 pages would be fetched
        assertTrue("Page size is set to 3 ", inboxItemList.getInboxItems().size() == 3);
        
        //Add a sort criteria
        criteria.addSortCriteria("u.email", true);
        inboxItemList = (InboxItemList) callBack.doInHibernate(getSession());
        //you are getting page index 1 basically 2nd page so order would be button, jack, kimi
        assertEquals("button@gmail.com", ((User)inboxItemList.getInboxItems().get(0)).getEmail());
        assertEquals("jack@gmail.com", ((User)inboxItemList.getInboxItems().get(1)).getEmail());
        assertEquals("kimi@gmail.com", ((User)inboxItemList.getInboxItems().get(2)).getEmail());
    }
}
