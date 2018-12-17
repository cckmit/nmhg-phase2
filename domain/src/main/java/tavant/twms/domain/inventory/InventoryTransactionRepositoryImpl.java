package tavant.twms.domain.inventory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.common.BookingsReport;
import tavant.twms.infra.GenericRepositoryImpl;

public class InventoryTransactionRepositoryImpl extends
        GenericRepositoryImpl<InventoryTransaction, Long> implements InventoryTransactionRepository{


	public InventoryTransactionType getTransactionTypeByName(final String trnxType) {
        return (InventoryTransactionType) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                String query = "from InventoryTransactionType inventory_transaction_type "
                        + "where inventory_transaction_type.trnxTypeKey = :trnxTypeKey";

                InventoryTransactionType invTransactionType = (InventoryTransactionType) session
                        .createQuery(query).setParameter("trnxTypeKey", trnxType).uniqueResult();

                return invTransactionType;
            }
        });
    }
    
/*	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWith(
			final String partialSerialNumber,final String itemType , final int pageNumber,
			final int pageSize) {
        final String query = "SELECT item FROM InventoryItem item , Item i where upper(item.serialNumber) like :partialSerialNumber and item.serializedPart = 0" +
                " and  i.id=item.ofType and i.itemType = :itemType" +
                " order by item.serialNumber";
        return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.disableFilter("currentOwner");
                Query q = session.createQuery(query)
                        .setParameter("partialSerialNumber", partialSerialNumber + "%")
                        .setParameter("itemType", itemType.toUpperCase())
                        .setFirstResult(pageNumber * pageSize)
                        .setMaxResults(pageSize);
                return q.list();
            }
        });
        	final int pageSize, final Long orgId) {
        final String query = "SELECT item FROM InventoryItem item , Item i where upper(item.serialNumber) like :partialSerialNumber and item.serializedPart = 0" +
                " and  i.id=item.ofType and upper(i.itemType) = :claimType and (item.shipTo.id = :orgId or item.currentOwner.id = :orgId)" +
                " order by item.serialNumber";
    
	}
	*/
	
/*	getDelimitedByCommaValueForList(invSearchCriteria.getPolicies()) + ")"+
	" AND ((policyAudit.warrantyPeriod.fromDate <= :policyFromDate "
					+ " and policyAudit.warrantyPeriod.tillDate >= :policyToDate) or "
					+ "(policyAudit.warrantyPeriod.fromDate >= :policyFromDate "
					+ "and policyAudit.warrantyPeriod.tillDate <= :policyToDate) or "
					+ "(policyAudit.warrantyPeriod.tillDate >= :policyFromDate "
					+ "and policyAudit.warrantyPeriod.tillDate <= :policyToDate) or "
					+ "(policyAudit.warrantyPeriod.fromDate >= :policyFromDate "
					+ "and policyAudit.warrantyPeriod.fromDate <= :policyToDate))" +
                            " AND lower(policyAudit.status)='active'");
params.put("policyFromDate", invSearchCriteria.getPolicyFromDate());*/
	@SuppressWarnings("unchecked")
	public List<InventoryTransaction> getTransactionsOfDRAndD2d(
			final Date lstUpdateTime,final String businessUnit) {
		return (List<InventoryTransaction>) getHibernateTemplate().execute(
				new HibernateCallback() {
					// updatedTime
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String query = "select inventoryTransaction from InventoryTransaction inventoryTransaction , InventoryItem inventoryItem " +
								" where inventoryTransaction.transactedItem=inventoryItem.id"
								+ "  and inventoryTransaction.d.updatedTime > :lastupdate AND " +
										"  inventoryItem.businessUnitInfo=:businessUnit" +
										" order by inventoryTransaction.d.updatedTime desc ";

						Query q = session.createQuery(query).setParameter(
								"lastupdate", lstUpdateTime).setParameter("businessUnit", businessUnit);

						return q.list();
					}
				});
	}
    
	public BookingsReport findLastReportingTime() {
		final String query = "from  BookingsReport bookings_report)";

		return (BookingsReport) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {						
						return session.createQuery(query).list();
					}
				});
	}

	public void save(BookingsReport bookingsReport) {
		getHibernateTemplate().save(bookingsReport);

	}

}
