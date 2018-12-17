package tavant.twms.web.admin.jobcode;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

public class AssemblyJsonComparator implements Comparator<JSONObject> {
	
	public int compare(JSONObject first, JSONObject second){
		try{
		
		String firstCode = first.getJSONObject("definition").getString("label");
		String secondCode= second.getJSONObject("definition").getString("label");		
		return firstCode.compareTo(secondCode);
		}catch(JSONException jsonException){
			return 0;
		}
	}
}
