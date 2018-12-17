package tavant.twms.integration.layer.util;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import com.domainlanguage.base.Rounding;

public class GoogleMapDistanceCalculater {
	private static final Logger logger = Logger.getLogger("dealerAPILogger");
	public static String clientId;
	public static String cryptoKey;
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	public static final BigDecimal METER_MILES_CONVERSTION_FACTOR = new BigDecimal(
			0.000621371);
	public static Map<BigDecimal, BigDecimal> calculateDistance(
			String addressFrom, String addressTo) throws IOException,
			JSONException {
		Map<BigDecimal, BigDecimal> distanceHoursMap = new HashMap<BigDecimal, BigDecimal>();
		String encodedFromUrl = URLEncoder.encode(addressFrom, "UTF-8");
		String encodedToUrl = URLEncoder.encode(addressTo, "UTF-8");
		String urlString = "http://maps.googleapis.com/maps/api/directions/json?client=" + clientId + "&sensor=false&origin="
		+ encodedFromUrl + "&destination=" + encodedToUrl +"&signature=";
		String data = "/maps/api/directions/json?client="+clientId+"&sensor=false&origin="+encodedFromUrl+"&destination="+encodedToUrl;
		cryptoKey=cryptoKey.replace('-', '+');				
		cryptoKey = cryptoKey.replace('_', '/');
		urlString = urlString.concat(getSignature(cryptoKey,data));
		logger.info("URL String: " + urlString);
		URL urlGoogleDirService = new URL(urlString);
		HttpURLConnection urlGoogleDirCon = (HttpURLConnection) urlGoogleDirService
				.openConnection();
		urlGoogleDirCon.setAllowUserInteraction(false);
		urlGoogleDirCon.setDoInput(true);
		urlGoogleDirCon.setDoOutput(false);
		urlGoogleDirCon.setUseCaches(true);
		urlGoogleDirCon.setRequestMethod("GET");
		urlGoogleDirCon.setConnectTimeout(7000);
		urlGoogleDirCon.connect();
		OutputStream output = new OutputStream() {
			private StringBuilder string = new StringBuilder();

			@Override
			public void write(int b) throws IOException {
				this.string.append((char) b);
			}

			@Override
			public String toString() {
				return this.string.toString();
			}
		};

		byte buf[] = new byte[1024];
		int len;

		while ((len = urlGoogleDirCon.getInputStream().read(buf)) > 0) {
			output.write(buf, 0, len);
		}
		output.close();
		urlGoogleDirCon.getInputStream().close();
		parseJSONOutPutToJava(output, distanceHoursMap);
		urlGoogleDirCon.disconnect();
		return distanceHoursMap;
	}
private static String getSignature(String key, String data) {
		
		SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(key.getBytes()), HMAC_SHA1_ALGORITHM);
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(secretKeySpec);
			
			byte[] rawHmac = mac.doFinal(data.getBytes());
			
			
			byte[] encodeBase64 = Base64.encodeBase64(rawHmac);
			
			String encoded= new String(encodeBase64);
			
			encoded = encoded.replace("/", "_");
			encoded=encoded.replace('+', '-');
			return encoded;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	private static void parseJSONOutPutToJava(OutputStream output,
			Map<BigDecimal, BigDecimal> distanceHoursMap) throws JSONException {
		String jsonOutput = output.toString();
		JSONObject jsonObject = new JSONObject(jsonOutput);
		if(jsonOutput!=null&&jsonObject.getJSONArray("routes")!=null&&jsonObject.getJSONArray("routes").length()>0){
		JSONArray routesArray = jsonObject.getJSONArray("routes");
		JSONObject route = routesArray.getJSONObject(0);
		JSONArray legs = route.getJSONArray("legs");
		JSONObject leg = legs.getJSONObject(0);
		JSONObject durationObject = leg.getJSONObject("duration");
		JSONObject distanceObject = leg.getJSONObject("distance");
		int duration = (Integer) durationObject.get("value");
		BigDecimal timeInSec = new BigDecimal(duration);
		int distance = (Integer) distanceObject.get("value");
		BigDecimal distanceInMeters = new BigDecimal(distance);
		distanceHoursMap.put(timeInSec, distanceInMeters);
		}
	}

	public static BigDecimal convertDistanceInMiles(BigDecimal value) {
		BigDecimal distanceInMiles = value
				.multiply(METER_MILES_CONVERSTION_FACTOR);
		return distanceInMiles.multiply(BigDecimal.valueOf(2)).setScale(0, RoundingMode.HALF_UP);
	}

	public static BigDecimal convertTravelSecsInHrs(BigDecimal timeInSecs) {
		timeInSecs=timeInSecs.multiply(BigDecimal.valueOf(2));
		BigDecimal numHours = timeInSecs.divide(new BigDecimal(3600),  Rounding.DOWN);
		BigDecimal numHoursInTotal = numHours.setScale(0, Rounding.DOWN);
		String totalHours = numHoursInTotal.toString();
		float remainder = timeInSecs.floatValue() % 3600;
		Integer numMins = (int) Math.floor(remainder / 60);
		String totalMins = numMins.toString();
		String finalMins = null;
		if (totalMins.length() == 1) {
			finalMins = "0" + totalMins;
		} else {
			finalMins = totalMins;
		}
		String totalHoursandMins = totalHours + "." + finalMins;
		return new BigDecimal(totalHoursandMins);
	}
	
	public static BigDecimal convertTravelSecsInHrsForAdditional(BigDecimal timeInSecs) {
		BigDecimal numHours = timeInSecs.divide(new BigDecimal(3600),  Rounding.DOWN);
		BigDecimal numHoursInTotal = numHours.setScale(0, Rounding.DOWN);
		String totalHours = numHoursInTotal.toString();
		float remainder = timeInSecs.floatValue() % 3600;
		Integer numMins = (int) Math.floor(remainder / 60);
		String totalMins = numMins.toString();
		String finalMins = null;
		if (totalMins.length() == 1) {
			finalMins = "0" + totalMins;
		} else {
			finalMins = totalMins;
		}
		String totalHoursandMins = totalHours + "." + finalMins;
		return new BigDecimal(totalHoursandMins);
	}

	public static String getClientId() {
		return clientId;
	}

	public static void setClientId(String clientId) {
		GoogleMapDistanceCalculater.clientId = clientId;
	}

	public static String getCryptoKey() {
		return cryptoKey;
	}

	public static void setCryptoKey(String cryptoKey) {
		GoogleMapDistanceCalculater.cryptoKey = cryptoKey;
	}
	

}