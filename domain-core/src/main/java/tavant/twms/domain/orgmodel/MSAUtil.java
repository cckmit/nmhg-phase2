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

package tavant.twms.domain.orgmodel;

import java.math.BigDecimal;

public class MSAUtil {

	  // Distance/Radius searching related constants
	  public static final float LAT_LONG_BIAS = 180; // bias used to ensure that lat and long are +
	  public static final float EARTH_RADIUS = (float) 3956; //in miles, = 6367 kms
	  public static final float RADIANS_TO_DEGREES_CONVERSION_FACTOR = (float)57.295780; // = 180/PI
	  public static final float DEGREES_TO_RADIANS_CONVERSION_FACTOR = (float)0.017453293; // = PI/180

	  /**
	   *  Finds the distance in miles between two locations specified by (latitude1, longitude1)
	   *  and (latitude2, longitude2).
	   *  This method uses a mathematically accurate formula called Haversine formula
	   *  for computing the distance.
	   */

	  public static Double calculateHaversineDistance(Float latitude1, Float longitude1,
	                                                  Float latitude2, Float longitude2)
	  {
	    Double distance = null;
	    if ((latitude1 != null) && (longitude1 != null) && (latitude2 != null) && (longitude2 != null))
	    {

	       float lat1 = latitude1.floatValue() * DEGREES_TO_RADIANS_CONVERSION_FACTOR;
	       float lon1 = longitude1.floatValue() * DEGREES_TO_RADIANS_CONVERSION_FACTOR;

	       float lat2 = latitude2.floatValue() *  DEGREES_TO_RADIANS_CONVERSION_FACTOR;
	       float lon2 = longitude2.floatValue() * DEGREES_TO_RADIANS_CONVERSION_FACTOR;

	       float delLat = lat1 - lat2;
	       float delLon = lon1 - lon2;

	       // Haversine formula :
	       // a = (sin(dlat/2))^2 + cos(lat1) * cos(lat2) * (sin(dlon/2))^2
	       // c = 2 * atan2 (sqrt(a), sqrt(1-a))
	       // required distance, d = R * c
	       // Here R is Earth's radius.

	       double a = Math.pow(Math.sin(delLat / 2), 2) +
	                  Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(delLon/2), 2);

	       double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	       double d = EARTH_RADIUS * c;
	       distance = new Double(d);
	    }
	    return (distance);
	  }
	  
	  /**
	   * Translates the search Radius into the range of Latitudes that must be checked
	   */

	  public static Float calculateDelLat(float searchRadius)
	  {
	     return(new Float((searchRadius / EARTH_RADIUS) * RADIANS_TO_DEGREES_CONVERSION_FACTOR));
	  }

	  /**
	   *  Translates the search Radius into the range of Longitudes that must be checked
	   */

	  public static Float calculateDelLon(float searchRadius, double latitude)
	  {
	    Float delLon= null;
	    if (latitude > 0 )
	    {
	      delLon = new Float (((searchRadius / EARTH_RADIUS) * RADIANS_TO_DEGREES_CONVERSION_FACTOR) /
	                          Math.cos(latitude * DEGREES_TO_RADIANS_CONVERSION_FACTOR));
	    }
	    return delLon;
	  }

	  // API will give required MSA's with Latitude and Longitude 
	  // for the given distance (miles) and source address
	  public static MSA[] prepareLocationsBasedOnDistance(MSA sourceAddressMSA, Long distance)
	  {
		MSA[] searchMSA = new MSA[3];
		MSA sourceMSA = new MSA(), destinationMSA = new MSA();

		float deltaLatitude = Math.abs(calculateDelLat(distance));

		double latitudeForSource = Math.abs(sourceAddressMSA.getLatitude().longValue() - deltaLatitude);
		double latitudeForDestination = Math.abs(sourceAddressMSA.getLatitude().longValue() + deltaLatitude);

		float deltaLongitude = Math.abs(calculateDelLon(distance.floatValue(), sourceAddressMSA.getLatitude().longValue()));

		BigDecimal longitudeForSource = BigDecimal.valueOf(sourceAddressMSA.getLongitude().longValue() - deltaLongitude);
		BigDecimal longitudeForDestination = BigDecimal.valueOf(sourceAddressMSA.getLongitude().longValue() + deltaLongitude);

		// Preparation of Source MSA
		sourceMSA.setLatitude(BigDecimal.valueOf(latitudeForSource));
		sourceMSA.setLongitude(longitudeForSource);

		// Preparation of Destination MSA
		destinationMSA.setLatitude(BigDecimal.valueOf(latitudeForDestination));
		destinationMSA.setLongitude(longitudeForDestination);

		searchMSA[0] = sourceMSA;
		searchMSA[1] = destinationMSA;
		return searchMSA;
	  }

}
