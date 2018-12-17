package tavant.twms.integration.adapter;

import org.mule.config.builders.MuleXmlConfigurationBuilder;

public class ServerStartup {
	public static void main(String[] args) throws Exception {
        new MuleXmlConfigurationBuilder().configure("mule-config.xml");
    }
}
