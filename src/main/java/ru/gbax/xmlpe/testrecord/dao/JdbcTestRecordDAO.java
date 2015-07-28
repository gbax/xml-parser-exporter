package ru.gbax.xmlpe.testrecord.dao;

import org.apache.log4j.Logger;
import ru.gbax.xmlpe.testrecord.dao.api.TestRecordDAO;
import ru.gbax.xmlpe.testrecord.model.TestResord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * ДАО для работы записями через JDBC
 *
 * Created by GBAX on 27.07.2015.
 */
public class JdbcTestRecordDAO implements TestRecordDAO {

    final static Logger logger = Logger.getLogger(JdbcTestRecordDAO.class);

    private static final String INSERT_SQL = "INSERT INTO TEST_RECORD (DEP_CODE, DEP_JOB, DESCRIPTION) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TEST_RECORD SET DEP_CODE = ?,DEP_JOB=?, DESCRIPTION=?  WHERE ID = ?";
    private static final String READ_SQL = "SELECT * FROM TEST_RECORD";
    private static final String DELETE_SQL = "DELETE FROM TEST_RECORD WHERE ID = ?";

    private DataSource dataSource;

    static Connection conn = null;

    public void createConnection() {
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            logger.debug("Create transaction...");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertList(List<TestResord> testResords) throws SQLException {
        for (TestResord testResord : testResords) {
            logger.debug(String.format("Executing SQL: %s", INSERT_SQL));
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
            ps.setString(1, testResord.getDepCode());
            ps.setString(2, testResord.getDepJob());
            ps.setString(3, testResord.getDescription());
            ps.executeUpdate();
            ps.close();
        }
    }

    public void updateList(Map<Long, TestResord> testResords) throws SQLException {
        for (Map.Entry<Long, TestResord> record : testResords.entrySet()) {
            logger.debug(String.format("Executing SQL: %s", UPDATE_SQL));
            PreparedStatement ps = conn.prepareStatement(UPDATE_SQL);
            ps.setString(1, record.getValue().getDepCode());
            ps.setString(2, record.getValue().getDepJob());
            ps.setString(3, record.getValue().getDescription());
            ps.setLong(4, record.getKey());
            ps.executeUpdate();
            ps.close();
        }
    }

    public void deleteList(List<TestResord> testResords) throws SQLException {
        for (TestResord testResord : testResords) {
            logger.debug(String.format("Executing SQL: %s", DELETE_SQL));
            PreparedStatement ps = conn.prepareStatement(DELETE_SQL);
            ps.setLong(1, testResord.getId());
            ps.executeUpdate();
            ps.close();
        }
    }

    public List<TestResord> findAll() {
        createConnection();
        try {
            logger.debug(String.format("Executing SQL: %s",READ_SQL));
            PreparedStatement ps = conn.prepareStatement(READ_SQL);
            List<TestResord> testResords = new ArrayList<TestResord>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestResord testResord = new TestResord();
                testResord.setId(rs.getLong("ID"));
                testResord.setDepCode(rs.getString("DEP_CODE"));
                testResord.setDepJob(rs.getString("DEP_JOB"));
                testResord.setDescription(rs.getString("DESCRIPTION"));
                testResords.add(testResord);
            }
            rs.close();
            ps.close();
            return testResords;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                commitAndCloseConnection();
            } catch (SQLException e) {
                logger.error("Error on attempt commit and close JDBC connection.");
            }
        }
    }

    public void commitAndCloseConnection() throws SQLException {
        if (conn != null) {
            conn.commit();
            if (!conn.isClosed()) {
                conn.close();
            }
        }
        logger.debug("Transaction committed.");
    }

    public void rollbackAndCloseConnection() throws SQLException {
        if (conn != null) {
            conn.rollback();
            if (!conn.isClosed()) {
                conn.close();
            }
        }
        logger.debug("Transaction rollback.");
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}




