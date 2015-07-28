package ru.gbax.xmlpe.testrecord.dao.api;

import ru.gbax.xmlpe.testrecord.model.TestResord;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для работы с записями таблицы
 *
 * Created by GBAX on 27.07.2015.
 */
public interface TestRecordDAO {

    /**
     * Создание соединения и транзакции
     */
    void createConnection();

    /**
     * Коммит и закрытие соединения
     * @throws SQLException
     */
    void commitAndCloseConnection() throws SQLException;

    /**
     * Откат и закрытие соединения
     * @throws SQLException
     */
    void rollbackAndCloseConnection() throws SQLException;

    /**
     * Вставка записей в БД
     * @param testResords
     * @throws SQLException
     */
    void insertList(List<TestResord> testResords) throws SQLException;

    /**
     * Обновление записей в БД
     * @param testResords
     * @throws SQLException
     */
    void updateList(Map<Long, TestResord> testResords) throws SQLException;

    /**
     * Удаление записей из БД
     * @param testResords
     * @throws SQLException
     */
    void deleteList(List<TestResord> testResords) throws SQLException;

    /**
     * Получение записей из БД
     * @return
     */
    List<TestResord> findAll();
}




