package ru.gbax.xmlpe.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.gbax.xmlpe.common.service.api.XMLExporterService;
import ru.gbax.xmlpe.common.service.api.XMLImporterService;
import ru.gbax.xmlpe.testrecord.dao.api.TestRecordDAO;

/**
 * Холдер для получения спринговских бинов
 *
 * Created by GBAX on 27.07.2015.
 */
public class ContextHolder {

    private static ApplicationContext context = null;

    /**
     * Инициализация контекста
     */
    private static void init() {
        context = new ClassPathXmlApplicationContext("spring/root-context.xml");
    }

    /**
     * Получение бина по имени
     *
     * @param name название
     * @return бин
     */
    private static Object getBean(String name) {
        if (context == null) {
            init();
        }
        return context.getBean(name);
    }

    public static TestRecordDAO getTestRecordDAO( ){
        return (TestRecordDAO) getBean("testRecordDAO");
    }

    public static XMLExporterService getXmlExporterService( ){
        return (XMLExporterService) getBean("xmlExporter");
    }

    public static XMLImporterService getXmlImporterService( ){
        return (XMLImporterService) getBean("xmlImporter");
    }

}
