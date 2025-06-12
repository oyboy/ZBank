package org.example.ui.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfProperties {
    protected static FileInputStream fileInputStream;
    protected static Properties PROPERTIES;

    static {
        try {
            fileInputStream = new FileInputStream("src/test/resources/conf.properties");
            PROPERTIES = new Properties();
            PROPERTIES.load(fileInputStream);
        } catch (IOException e) {
            System.out.println("Can't load properties file: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}