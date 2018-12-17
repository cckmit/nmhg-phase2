package tavant.twms.integration.layer.authentication;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.security.wss4j.WSS4JOutHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;

import tavant.twms.integration.layer.authentication.PasswordHandler;


public class DealerAPIAuthenticationTestCase {

    final static String RESPONSE_FILE = "C:/Documents and Settings/TWMSUSER/Desktop/output/response.xml";    
    public static void main(String[] args) {

        String requestXML = DealerAPIAuthenticationTestCase
                .getContents("C:/projects/TSA_DEV/functional/src/test/resources/tavant/twms/dealerinterface/internaluser-soapInput.xml");
        
        boolean success = true;
        if (success) {

            try {
                Client client = new Client(new URL(
                        "http://localhost:8080/twmsServices/DealerIntegrationService?WSDL"));

                client.addOutHandler(new DOMOutHandler());
                Properties outProperties = new Properties();
                configureOutProperties(outProperties);
                // CONFIGURE OUTGOING SECURITY HERE (outProperties) <--
                client.addOutHandler(new WSS4JOutHandler(outProperties));

                Object[] results = client.invoke("getUnitServiceHistory",
                        new Object[] { requestXML });
                if (results[0] != null) {
                    try {
                            BufferedWriter out = new BufferedWriter(new FileWriter(RESPONSE_FILE));
                            out.write((String)results[0]);
                            out.close();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
        
        protected static void configureOutProperties(Properties config)
        {
            // Action to perform : user token
            config.setProperty("action", "UsernameToken");
            // Password type : plain text
            config.setProperty("passwordType", "PasswordText");
            // User name to send
            config.setProperty("user", "fboselli");
            // Callback used to retrieve password for given user.
            config.setProperty("passwordCallbackClass", PasswordHandler.class.getName());
        }
        
        public static String getContents(String filename) {
            File aFile = new File(filename);
            
            StringBuffer contents = new StringBuffer();
          
           //  declared here only to make visible to finally clause
             BufferedReader input = null;
             try {
             //   use buffering, reading one line at a time
              //  FileReader always assumes default encoding is OK!
               input = new BufferedReader( new FileReader(aFile) );
               String line = null;  //not declared within while loop
               /*
            * readLine is a bit quirky : it returns the content of a line MINUS the
            * newline. it returns null only for the END of the stream. it returns
            * an empty String if two newlines appear in a row.
            */
               while (( line = input.readLine()) != null){
                 contents.append(line);
                 contents.append(System.getProperty("line.separator"));
               }
             }
             catch (FileNotFoundException ex) {
               ex.printStackTrace();
             }
             catch (IOException ex){
               ex.printStackTrace();
             }
             finally {
               try {
                 if (input!= null) {
                   // flush and close both "input" and its underlying FileReader
                   input.close();
                 }
               }
               catch (IOException ex) {
                 ex.printStackTrace();
               }
             }
             return contents.toString();
           }


}
