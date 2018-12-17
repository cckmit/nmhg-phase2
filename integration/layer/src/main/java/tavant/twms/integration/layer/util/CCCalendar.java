package tavant.twms.integration.layer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CCCalendar extends Calendar {

	public CCCalendar() {
		super();
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(this.getTime());
	}
	
	@Override
	public void add(int field, int amount) {
	}

	@Override
	protected void computeFields() {

	}

	@Override
	protected void computeTime() {

	}

	@Override
	public int getGreatestMinimum(int field) {
		return 0;
	}

	@Override
	public int getLeastMaximum(int field) {
		return 0;
	}

	@Override
	public int getMaximum(int field) {
		return 0;
	}

	@Override
	public int getMinimum(int field) {
		return 0;
	}

	@Override
	public void roll(int field, boolean up) {

	}

}
