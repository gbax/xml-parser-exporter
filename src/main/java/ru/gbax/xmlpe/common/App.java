package ru.gbax.xmlpe.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.gbax.xmlpe.common.exception.ImportException;
import ru.gbax.xmlpe.common.utils.ContextHolder;
import ru.gbax.xmlpe.common.utils.Log4jConfigUtil;
import ru.gbax.xmlpe.testrecord.model.TestResord;

import java.util.List;

/**
 * Приложение для импорт и экспорта XML файлов определенной структуры
 *
 * Created by GBAX on 27.07.2015.
 */
public class App {

    private static final String EXPORT_COMMAND = "export";
    private static final String SYNC_COMMAND = "sync";

    final static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        Log4jConfigUtil.config();
        final String command = System.getProperty("command");
        final String file = System.getProperty("file");
        if (StringUtils.isEmpty(command) || StringUtils.isEmpty(file)) {
            logger.error("Command or file path not found");
            return;
        }
        if (!file.endsWith(".xml")) {
            logger.error(String.format("Invalid file extension %s", file));
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, SYNC_COMMAND)) {
            showRecords();
            try {
                ContextHolder.getXmlImporterService().runImport(file);
                showRecords();
            } catch (ImportException e) {
                logger.error(e.getMessage());
            }
        } else if (StringUtils.equalsIgnoreCase(command, EXPORT_COMMAND)) {
            showRecords();
            ContextHolder.getXmlExporterService().runExport(file);
        } else {
            logger.error("Command not found");
        }
    }

    /**
     * Вывод всех записей БД
     */
    public static void showRecords() {
        final List<TestResord> all = ContextHolder.getTestRecordDAO().findAll();
        logger.debug(String.format("The found records: %s", all.size()));
        for (TestResord testResord : all) {
            logger.info(testResord);
        }
    }


}
