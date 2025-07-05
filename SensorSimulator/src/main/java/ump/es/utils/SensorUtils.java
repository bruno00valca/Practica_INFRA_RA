package ump.es.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SensorUtils {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = SensorUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException( e.getMessage());
        }
    }
        public static String getProp(String key){
            return props.getProperty(key);
        }
}
