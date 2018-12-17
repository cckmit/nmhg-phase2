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
package tavant.twms.worklist.supplier;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.worklist.WorkListCriteria;

/**
 * The essence of this test is to just call the DAO apis and ensure that the
 * syntax of HQL queries are good.
 * 
 * However, the right tests would be to create claims and execute these queries
 * to see if they are in fact fetching right data.
 * 
 * For now, test only if query syntax is fine.
 * 
 * @author kannan.ekanath
 * 
 */
public class SupplierRecoveryWorkListDaoTest extends EngineRepositoryTestCase {

    SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

    public void testQuerySyntax() {
        User user = new User();
        user.setName("user");
        WorkListCriteria criteria = new WorkListCriteria(user);
        criteria.setTaskName("task");
        supplierRecoveryWorkListDao.getSupplierClaimAndShipmentList(criteria);
        supplierRecoveryWorkListDao.getSupplierLocationList(criteria);
        supplierRecoveryWorkListDao.getSupplierRecoveryClaimList(criteria);
        supplierRecoveryWorkListDao.getSupplierShipmentList(criteria);

        Long id = new Long(1);
        String str = "str";
        supplierRecoveryWorkListDao.getPreviewPaneForShipmentAndClaimGroup(id, id, str, str);
        supplierRecoveryWorkListDao.getPreviewPaneForSupplierLocation(str,str, id);
        supplierRecoveryWorkListDao.getPreviewPaneForSupplier(id, str, str);
        supplierRecoveryWorkListDao.getPreviewPaneForSupplierShipment(id, str, str);
    }

    public void setSupplierRecoveryWorkListDao(SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
        this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
    }
}
