package com.beyontec.mdcp.company.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesExtractor {
     private static Properties properties;
     
     static {
        properties = new Properties();
            try {
				properties.load(PropertiesExtractor.class.getClassLoader().getResourceAsStream("appins.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        public static String getProperty(String key){
        	
            return properties.getProperty(key);
        }
       
}