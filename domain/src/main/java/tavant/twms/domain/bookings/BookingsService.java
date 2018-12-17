package tavant.twms.domain.bookings;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.BookingsReport;

public interface BookingsService {
	@Transactional(readOnly = true)
	public java.sql.Timestamp findLastReportingTimeForInvTransactions();
	
	@Transactional(readOnly = false)
	public void save(BookingsReport  BookingsReport  );

	public void createDummyReportObjectForWarranty();

	public Timestamp findLastReportingTimeForWarranties();

	public void createDummyReportObjectForInventory();

	
}
