package ru.gbax.xmlpe.testrecord.dao.api;

import ru.gbax.xmlpe.testrecord.model.TestResord;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * ��������� ��� ������ � �������� �������
 *
 * Created by GBAX on 27.07.2015.
 */
public interface TestRecordDAO {

    /**
     * �������� ���������� � ����������
     */
    void createConnection();

    /**
     * ������ � �������� ����������
     * @throws SQLException
     */
    void commitAndCloseConnection() throws SQLException;

    /**
     * ����� � �������� ����������
     * @throws SQLException
     */
    void rollbackAndCloseConnection() throws SQLException;

    /**
     * ������� ������� � ��
     * @param testResords
     * @throws SQLException
     */
    void insertList(List<TestResord> testResords) throws SQLException;

    /**
     * ���������� ������� � ��
     * @param testResords
     * @throws SQLException
     */
    void updateList(Map<Long, TestResord> testResords) throws SQLException;

    /**
     * �������� ������� �� ��
     * @param testResords
     * @throws SQLException
     */
    void deleteList(List<TestResord> testResords) throws SQLException;

    /**
     * ��������� ������� �� ��
     * @return
     */
    List<TestResord> findAll();
}




