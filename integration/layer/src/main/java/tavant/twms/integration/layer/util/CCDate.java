package tavant.twms.integration.layer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CCDate extends Date {

	public CCDate(long l) {
		super(l);
	}

	public CCDate() {
		super();
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(this);
	}
}
