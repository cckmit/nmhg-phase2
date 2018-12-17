package tavant.twms.integration.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
	
	public static StringBuffer readFromStream(InputStream is) {
		StringBuffer contents = new StringBuffer();
		BufferedReader input = new BufferedReader(new InputStreamReader(is));
	        String line = null;
	        try {
				while (( line = input.readLine()) != null){
				  contents.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		return contents;
	}

}
