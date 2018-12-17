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
package tavant.twms.worklist.customer;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;

public class CustomerWorkListDaoTest extends EngineRepositoryTestCase {

    private CustomerWorkListDao customerWorkListDao;
    
    public void testQuerySyntax() {
        User user = new User();
        user.setId(new Long(1));
        WorkListCriteria workListCriteria = new WorkListCriteria(user);
		InboxItemList inboxItemList = customerWorkListDao.getWarrantiesForCustomer(workListCriteria);
		assertNotNull(inboxItemList);
    }

	public CustomerWorkListDao getCustomerWorkListDao() {
		return customerWorkListDao;
	}

	public void setCustomerWorkListDao(CustomerWorkListDao customerWorkListDao) {
		this.customerWorkListDao = customerWorkListDao;
	}

    
}
