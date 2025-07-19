package com.example.springtests.components;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class ConfigReader {
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
