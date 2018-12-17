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
package tavant.twms.infra;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

public class CalendarDateUserTypeTest extends MockObjectTestCase {

	CalendarDateUserType fixture;

	public CalendarDateUserTypeTest(String testName) {
		super(testName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fixture = new CalendarDateUserType();
	}

	public void testNullSafeGet() throws HibernateException, SQLException {
		Date expectedDate = now();
		Mock mockResultSet = mock(ResultSet.class);
		mockResultSet.expects(once()).method("getObject")
				.with(eq("START_DATE")).will(returnValue(expectedDate));

		CalendarDate date = (CalendarDate) fixture.nullSafeGet(
				(ResultSet) mockResultSet.proxy(),
				new String[] { "START_DATE" }, null);

		assertEquals(covertJavaDateToCalendarDate(expectedDate), date);
	}

	public void testNullSafeGetResultSetReturnsNull()
			throws HibernateException, SQLException {
		Mock mockResultSet = mock(ResultSet.class);
		mockResultSet.expects(once()).method("getObject")
				.with(eq("START_DATE")).will(returnValue(null));

		CalendarDate date = (CalendarDate) fixture.nullSafeGet(
				(ResultSet) mockResultSet.proxy(),
				new String[] { "START_DATE" }, null);

		assertNull(date);
	}

	public void testNullSafeGetResultSetReturnsNonDateType()
			throws HibernateException, SQLException {
		Mock mockResultSet = mock(ResultSet.class);
		mockResultSet.expects(once()).method("getObject")
				.with(eq("START_DATE")).will(returnValue(new Object()));

		try {
			fixture.nullSafeGet((ResultSet) mockResultSet.proxy(),
					new String[] { "START_DATE" }, null);
			fail("If the field being mapped is not of date type the method should throw exception");
		} catch (HibernateException e) {
			assertEquals(e.getMessage(),
					"CalendarDateUserType can be only used for Date fields");
		}
	}

	public void testnullSafeSet() throws HibernateException, SQLException {
		CalendarDate date = covertJavaDateToCalendarDate(now());
		Mock mockPreparedStatement = mock(PreparedStatement.class);
		mockPreparedStatement.expects(once()).method("setDate")
				.with(eq(0),eq(convertCalendarDateToSqlDate(date))).isVoid();
		
		fixture.nullSafeSet((PreparedStatement) mockPreparedStatement.proxy(), date, 0);
	}

	public void testnullSafeSetWithNullValue() throws HibernateException, SQLException {
		Mock mockPreparedStatement = mock(PreparedStatement.class);
		mockPreparedStatement.expects(once()).method("setDate")
				.with(eq(0),NULL);
		
		fixture.nullSafeSet((PreparedStatement) mockPreparedStatement.proxy(), null, 0);
	}

	public void testEquals() throws InterruptedException {
		assertTrue(fixture.equals(null, null)); //NOPMD
		Date date = now();

		Date anotherDate = new Date(date.getTime());
		assertFalse(fixture.equals(covertJavaDateToCalendarDate(date), null)); //NOPMD
		assertFalse(fixture.equals(null, covertJavaDateToCalendarDate(date))); //NOPMD
		
		assertTrue(fixture.equals(covertJavaDateToCalendarDate(date),
				covertJavaDateToCalendarDate(date)));

		assertTrue(fixture.equals(covertJavaDateToCalendarDate(date),
				covertJavaDateToCalendarDate(anotherDate)));
		assertTrue(fixture.equals(covertJavaDateToCalendarDate(anotherDate),
				covertJavaDateToCalendarDate(date)));

		assertFalse(fixture.equals(covertJavaDateToCalendarDate(date),
				covertJavaDateToCalendarDate(date).nextDay()));
		assertFalse(fixture.equals(covertJavaDateToCalendarDate(date).nextDay(),
				covertJavaDateToCalendarDate(date)));
	}

	public void testIsMutable() {
		assertFalse("Should always return false", fixture.isMutable());
	}

	public void testEqualsAndHashCodeContract() throws InterruptedException {
		Date date = now();
		Date anotherDate = new Date(date.getTime());
		assertEquals(fixture.equals(covertJavaDateToCalendarDate(date),
				covertJavaDateToCalendarDate(anotherDate)), fixture
				.hashCode(covertJavaDateToCalendarDate(date)) == fixture
				.hashCode(covertJavaDateToCalendarDate(anotherDate)));

		assertEquals(fixture.equals(covertJavaDateToCalendarDate(date),
				covertJavaDateToCalendarDate(date).nextDay()), 
				fixture.hashCode(covertJavaDateToCalendarDate(date)) == fixture
				.hashCode(covertJavaDateToCalendarDate(date).nextDay()));

	}
	
	public void testReturnedClass() {
		assertEquals(fixture.returnedClass(),CalendarDate.class);
	}
	
	public void testAssemble() {
		// TODO need to figure out what assemble 
		// this method needs to do
		CalendarDate cached = CalendarDate.date(2006, 5, 31);
		assertEquals(cached,fixture.assemble(cached, null));
	}
	
	public void testDisassemble() {
		// TODO need to figure out what disassemble 
		// this method needs to do
		CalendarDate value = CalendarDate.date(2006, 5, 31);
		assertEquals(value,fixture.disassemble(value));
	}

	public void testReplace() {
		// TODO need to figure out what disassemble 
		// this method needs to do
		CalendarDate original = CalendarDate.date(2006, 5, 31);
		assertEquals(original,fixture.replace(original,null,null));
	}

	public void testSqlTypes() {
		assertEquals(1,fixture.sqlTypes().length);
		assertEquals(Types.DATE,fixture.sqlTypes()[0]);
	}

	private Date now() {
		return new Date(Calendar.getInstance().getTime().getTime());
	}

	private CalendarDate covertJavaDateToCalendarDate(Date expectedDate) {
		return TimePoint.from(expectedDate).calendarDate(TimeZone.getDefault());
	}
	
	private Date convertCalendarDateToSqlDate(CalendarDate date) {
		return new Date(date.startAsTimePoint(TimeZone.getDefault()).asJavaUtilDate().getTime());
	}
}
