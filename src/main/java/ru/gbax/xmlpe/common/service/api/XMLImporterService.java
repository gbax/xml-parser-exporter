package ru.gbax.xmlpe.common.service.api;

/**
 * Интерфейс для запуска импорта в БД
 *
 * Created by GBAX on 27.07.2015.
 */
public interface XMLImporterService {

    /**
     * Запуск импорта
     * @param filePath путь и название файла
     */
    void runImport(String filePath);

}
