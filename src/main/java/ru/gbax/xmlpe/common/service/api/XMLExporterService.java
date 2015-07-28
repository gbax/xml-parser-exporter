package ru.gbax.xmlpe.common.service.api;

/**
 * Интерфейс для запуска экспорта из БД
 *
 * Created by GBAX on 27.07.2015.
 */
public interface XMLExporterService {

    /**
     * Запуск экспорта
     * @param filePath путь и имя к файлу
     */
    void runExport(String filePath);

}
