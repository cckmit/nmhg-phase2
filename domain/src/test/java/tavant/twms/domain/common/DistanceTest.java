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
package tavant.twms.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import junit.framework.TestCase;

public class DistanceTest extends TestCase {

    public void testHashCode() {
        Distance _100miles_1 = Distance.miles(Math.PI);
        Distance _100miles_2 = Distance.miles(Math.PI);
        
        assertNotSame(_100miles_1.hashCode(), _100miles_2.hashCode());
        
        Distance inMiles = Distance.miles(100);
        Distance inKms = Distance.kilometers(100);
        
        assertNotSame(inMiles.hashCode(),inKms.hashCode());
    }

    public void testMilesDouble() {
        Distance _100miles_1 = Distance.miles(new BigDecimal(Math.PI));
        Distance _100miles_2 = Distance.miles(Math.PI);
        assertEquals(_100miles_1,_100miles_2);
    }

    public void testKilometersDouble() {
        Distance _100kms_1 = Distance.miles(new BigDecimal(Math.PI));
        Distance _100kms_2 = Distance.miles(Math.PI);
        assertEquals(_100kms_1,_100kms_2);

    }

    public void testMilesBigDecimal() {
        Distance _100miles = Distance.miles(new BigDecimal(Math.PI));
        Distance expected = Distance.valueOf(new BigDecimal(Math.PI),DistanceUnit.mile);
        assertEquals(expected,_100miles);
    }

    public void testKilometersBigDecimal() {
        Distance _100kms = Distance.kilometers(new BigDecimal(Math.PI));
        Distance expected = Distance.valueOf(new BigDecimal(Math.PI),DistanceUnit.kilometer);
        assertEquals(expected,_100kms);
    }

    public void test_10_Miles() {
        Distance expected = Distance.miles(1);
        Distance actual = Distance._10_Miles();
        assertEquals( expected.times(10), actual);
    }

    public void test_100_Miles() {
        Distance _1Mile = Distance.miles(1);
        Distance expected = _1Mile.times(100);
        Distance actual = Distance._100_Miles();
        assertEquals( expected, actual);
    }

    public void test_10_Kilometers() {
        Distance expected = Distance.kilometers(1);
        assertEquals( expected.times(10), Distance._10_Kilometers());
    }

    public void test_100_Kilometers() {
        Distance _1KM = Distance.kilometers(1);
        Distance expected = _1KM.times(100);
        Distance actual = Distance._100_Kilometers();
        assertEquals( expected, actual);
    }

    public void testTimes() {
        Distance _fixture = Distance.miles(3.14);
        BigDecimal bigDecimalOfPI = new BigDecimal(3.14);
        BigDecimal factor = new BigDecimal(1000).setScale(2,RoundingMode.HALF_EVEN);
        bigDecimalOfPI = bigDecimalOfPI.multiply(factor).setScale(2,RoundingMode.HALF_EVEN);
        assertEquals(bigDecimalOfPI,_fixture.times(1000).getQuantity());
    }

    public void testDividedBy() {
        Distance _fixture = Distance.miles(Math.PI);
        BigDecimal quantity = new BigDecimal(Math.PI/10).setScale(2,RoundingMode.HALF_EVEN);
        Distance expected = Distance.miles(quantity);
        assertEquals(expected,_fixture.dividedBy(new BigDecimal(10.0)));
    }

    public void testAdd() {
        Distance _fixture = Distance.kilometers(Math.PI);
        Distance result = _fixture.add( Distance.kilometers(10) );
        assertEquals(13.14,result.getQuantity().doubleValue());
    }

    public void testTo() {
        Distance fixture = Distance.valueOf(Math.PI, DistanceUnit.mile);
        Distance convertedToKm = fixture.to(DistanceUnit.kilometer);
        assertEquals( Distance.kilometers(5.05),convertedToKm);
        
        Distance backToMile = convertedToKm.to(DistanceUnit.mile);
        assertEquals(fixture,backToMile);
    }

    public void testEqualsObject() {
        Distance fixture1 = Distance.miles(Math.PI);
        Distance fixture2 = Distance.miles(3.14);
        assertNotSame( fixture2,fixture1);
        
        Distance miles = Distance.miles(1);
        Distance kms = Distance.kilometers(1.61);
        assertEquals(miles,kms);
    }

    public void testCompareTo() {
        Distance aMile = Distance.miles(1);
        Distance aKm = Distance.kilometers(1);
        
        Distance _2Miles = Distance.miles(2);
        Distance _2Kms = Distance.kilometers(2);
        
        assertEquals(1,aMile.compareTo(aKm));
        assertEquals(-1,aKm.compareTo(aMile));
        assertEquals(0,aKm.compareTo(aKm));        
        assertEquals(0,aMile.compareTo(aMile));
        
        assertEquals(-1,aKm.compareTo(_2Kms));
        assertEquals(1,_2Kms.compareTo(aKm));
        
        assertEquals(-1,aMile.compareTo(_2Miles));        
        assertEquals(1,_2Miles.compareTo(aMile));        
    }
    
    public void testDividedByDistance() {
        Distance _100Miles = Distance.miles(100);
        Distance _33Miles = Distance.miles(33);
        BigDecimal actual = _100Miles.dividedBy(_33Miles);
        BigDecimal expected = new BigDecimal(3.03).setScale(2,RoundingMode.HALF_EVEN);
        assertEquals(expected,actual);
        
        //TODO: Not sure whether this is acceptable or not.
        assertEquals(Distance.miles(99.99),_33Miles.times(_100Miles.dividedBy(_33Miles)));
    }
}
