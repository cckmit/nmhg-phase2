package tavant.twms.domain.bookings;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.set.CompositeSet.SetMutator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.common.BookingsReport;
import tavant.twms.infra.GenericRepositoryImpl;

public class BookingsRepositoryImpl extends
GenericRepositoryImpl<BookingsReport, Long> implements BookingsRepository {

	public java.sql.Timestamp findLastReportingTimeForInvTransactions() {
		final String query = "select br.INV_TRANS_LAST_PROCESSED_TIME from bookings_report br where br.ID=(select max(br1.ID) from bookings_report br1 where br1.INV_TRANS_LAST_PROCESSED_TIME is not null)";

		return (java.sql.Timestamp) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {						
						return session.createSQLQuery(query).uniqueResult();
					}
				});
	}

	public void save(BookingsReport bookingsReport) {
		getHibernateTemplate().save(bookingsReport);

	}

	public Timestamp findLastReportingTimeForWarranties() {
		final String query = "select br.WARRANTY_LAST_PROCESSED_TIME from bookings_report br where br.ID=(select max(br1.ID) from bookings_report br1 where br1.WARRANTY_LAST_PROCESSED_TIME is not null)";

		return (java.sql.Timestamp) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {						
						return session.createSQLQuery(query).uniqueResult();
					}
				});
	}
	

}
