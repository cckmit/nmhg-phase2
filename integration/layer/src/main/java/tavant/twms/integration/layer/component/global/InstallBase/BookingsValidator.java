package tavant.twms.integration.layer.component.global.InstallBase;

import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.integration.layer.constants.BookingsInterfaceErrorConstants;

import com.tavant.globalsync.bookingsync.BookingsDocument.Bookings;


public class BookingsValidator  {

    
    
	private BookingsInterfaceErrorConstants bookingsInterfaceErrorConstants;


 
		public void validateCommonFields(
			Bookings bookings,final Map<String,String> errorMessageCodes) {
		if (bookings.getDataArea().getBUName() == null||!StringUtils.hasText(bookings.getDataArea().getBUName())) {
			errorMessageCodes.put(BookingsInterfaceErrorConstants.B001, bookingsInterfaceErrorConstants.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B001));
		}

		if (bookings.getDataArea().getUnitSerialNumber() == null||!StringUtils.hasText(bookings.getDataArea().getUnitSerialNumber())) {
			errorMessageCodes.put(BookingsInterfaceErrorConstants.B002, bookingsInterfaceErrorConstants.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B002));
		}
		if (bookings.getDataArea().getTransactionDateTime() == null||!StringUtils.hasText(bookings.getDataArea().getTransactionDateTime().toString())) {
			errorMessageCodes.put(BookingsInterfaceErrorConstants.B003, bookingsInterfaceErrorConstants.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B003));
		}
		if (bookings.getDataArea().getTransactionType() == null||!StringUtils.hasText(bookings.getDataArea().getTransactionType())) {
			errorMessageCodes.put(BookingsInterfaceErrorConstants.B004, bookingsInterfaceErrorConstants.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B004));
		} else if (bookings.getDataArea().getTransactionType().length() > 1	|| (!bookings.getDataArea().getTransactionType().substring(0, 1)
						.equalsIgnoreCase("B")
				&& !bookings.getDataArea().getTransactionType().substring(0, 1)
						.equalsIgnoreCase("C"))) {
			errorMessageCodes
					.put(BookingsInterfaceErrorConstants.B004,
							bookingsInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(BookingsInterfaceErrorConstants.B004));
		}
		
	}
		
		
		public BookingsInterfaceErrorConstants getBookingsInterfaceErrorConstants() {
			return bookingsInterfaceErrorConstants;
		}

		public void setBookingsInterfaceErrorConstants(
				BookingsInterfaceErrorConstants bookingsInterfaceErrorConstants) {
			this.bookingsInterfaceErrorConstants = bookingsInterfaceErrorConstants;
		}



}
