package tavant.twms.domain.bookings;

import java.sql.Timestamp;
import java.util.Date;

import tavant.twms.domain.common.BookingsReport;
import tavant.twms.domain.inventory.InventoryTransactionRepository;

public class BookingsServiceImpl implements BookingsService {

	  BookingsRepository bookingsRepository;
	  

	public java.sql.Timestamp findLastReportingTimeForInvTransactions() {
		return bookingsRepository.findLastReportingTimeForInvTransactions();
		}

	public void save(BookingsReport bookingsReport) {
		bookingsRepository.save(bookingsReport);

	}
	
	public BookingsRepository getBookingsRepository() {
		return bookingsRepository;
	}

	public void setBookingsRepository(BookingsRepository bookingsRepository) {
		this.bookingsRepository = bookingsRepository;
	}

	public void createDummyReportObjectForWarranty() {
		BookingsReport bookingReport = new BookingsReport();
		bookingReport.setNoOfD2D(0);
		bookingReport.setNoOfDR(0);
		bookingReport.setNoOfSignatureSheet(0);
		bookingReport.setWarrantyLastProcessedTime(new Date());
		bookingsRepository.save(bookingReport);
	}

	public Timestamp findLastReportingTimeForWarranties() {
		return bookingsRepository.findLastReportingTimeForWarranties();
	}

	public void createDummyReportObjectForInventory() {
		BookingsReport bookingReport = new BookingsReport();
		bookingReport.setNoOfD2D(0);
		bookingReport.setNoOfDR(0);
		bookingReport.setNoOfSignatureSheet(0);
		bookingReport.setInvTransLastProcessedTime(new Date());
		bookingsRepository.save(bookingReport);
		
	}


}
