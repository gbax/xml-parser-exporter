package ru.gbax.xmlpe.common.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Утилита для работы с пропертиями
 *
 * Created by GBAX on 27.07.2015.
 */
public class PropertiesUtils {

    final static Logger logger = Logger.getLogger(PropertiesUtils.class);

    private static Properties prop = null;

    /**
     * Инициализация пропертей
     */
    public static void init() {
        InputStream input = null;
        try {
            String property = System.getProperty("property.file");
            File initialFile = new File(property);
            input = new FileInputStream(initialFile);
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }

    /**
     * Получение свойства
     * @param name
     * @return
     */
    public static String getProperty(String name) {
        if (prop == null) {
            init();
        }
        return prop.getProperty(name);
    }


}
