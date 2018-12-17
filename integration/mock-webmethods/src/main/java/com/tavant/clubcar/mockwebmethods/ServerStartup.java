package com.tavant.clubcar.mockwebmethods;

import org.mule.config.builders.MuleXmlConfigurationBuilder;

public class ServerStartup {
    public static void main(String[] args) throws Exception {
        new MuleXmlConfigurationBuilder().configure("mule-config.xml");
        System.out.println("****************************************");
        System.out.println("*   Starting Mock WebMethods .....     *");
        System.out.println("****************************************");
        System.out.println("*   Mule started successfully........  *");
        System.out.println("****************************************");
    }
}
