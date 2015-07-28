package ru.gbax.xmlpe.common.service;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.gbax.xmlpe.common.service.api.XMLImporterService;
import ru.gbax.xmlpe.common.utils.ImporterErrorHandler;
import ru.gbax.xmlpe.testrecord.dao.api.TestRecordDAO;
import ru.gbax.xmlpe.testrecord.model.TestResord;
import ru.gbax.xmlpe.utils.TestRecordKeyModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для импорта в БД из файла
 *
 * Created by GBAX on 27.07.2015.
 */
public class XMLImporterServiceImpl implements XMLImporterService {

    final static Logger logger = Logger.getLogger(XMLImporterServiceImpl.class);

    private TestRecordDAO testRecordDAO;

    public void setTestRecordDAO(TestRecordDAO testRecordDAO) {
        this.testRecordDAO = testRecordDAO;
    }

    public void runImport(String filePath) {
        List<TestResord> allRecords = testRecordDAO.findAll();
        Map<TestRecordKeyModel, TestResord> recordDbMap = new HashMap<TestRecordKeyModel, TestResord>();
        for (TestResord testResord : allRecords) {
            recordDbMap.put(new TestRecordKeyModel(testResord.getDepCode(), testResord.getDepJob()), testResord);
        }
        logger.info(String.format("Start import file %s.", filePath));
        List<TestResord> recordsFromXML = getRecordsFromXML(filePath);
        if (recordsFromXML.size() == 0) {
            return;
        }
        logger.info(String.format("Founded records in file %s ...", recordsFromXML.size()));
        Map<TestRecordKeyModel, TestResord> recordNewMap = new HashMap<TestRecordKeyModel, TestResord>();
        List<TestResord> recordToDelete = new ArrayList<TestResord>();
        List<TestResord> recordToInsert = new ArrayList<TestResord>();
        Map<Long, TestResord> recordToUpdate = new HashMap<Long, TestResord>();
        for (TestResord testResord : recordsFromXML) {
            TestRecordKeyModel testRecordKeyModel = new TestRecordKeyModel(testResord.getDepCode(), testResord.getDepJob());
            if (recordNewMap.get(testRecordKeyModel) != null) {
                logger.error(String.format("Found duplicate record %s", testResord));
                return;
            }
            recordNewMap.put(testRecordKeyModel, testResord);
        }
        for (Map.Entry<TestRecordKeyModel, TestResord> record : recordNewMap.entrySet()) {
            TestResord testResordDb = recordDbMap.get(record.getKey());
            if (testResordDb == null) {
                recordToInsert.add(record.getValue());
            } else {
                recordToUpdate.put(testResordDb.getId(),record.getValue());
                recordDbMap.remove(record.getKey());
            }
        }
        for (Map.Entry<TestRecordKeyModel, TestResord> record : recordDbMap.entrySet()) {
            recordToDelete.add(record.getValue());
        }
        try {
            testRecordDAO.createConnection();
            testRecordDAO.insertList(recordToInsert);
            logger.info(String.format("Inserted record: %s", recordToInsert.size()));
            testRecordDAO.updateList(recordToUpdate);
            logger.info(String.format("Updated record: %s", recordToUpdate.size()));
            testRecordDAO.deleteList(recordToDelete);
            logger.info(String.format("Deleted record: %s", recordToDelete.size()));
            testRecordDAO.commitAndCloseConnection();
        } catch (SQLException e) {
            try {
                testRecordDAO.rollbackAndCloseConnection();
            } catch (SQLException e1) {
                logger.fatal("Cannot rollback transaction");
                logger.debug(e1);
            }
        }
        logger.info(String.format("File %s imported", filePath));
    }

    private List<TestResord> getRecordsFromXML(String filePath) {
        List<TestResord> testResords = new ArrayList<TestResord>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ImporterErrorHandler(XMLImporterServiceImpl.class));
            Document doc = db.parse(new File(filePath));
            Element docEle = doc.getDocumentElement();
            NodeList nl = docEle.getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) nl.item(i);
                        String depCode = el.getElementsByTagName("depCode").item(0).getTextContent();
                        String depJob = el.getElementsByTagName("depJob").item(0).getTextContent();
                        String description = el.getElementsByTagName("description").item(0).getTextContent();
                        TestResord testResord = new TestResord();
                        testResord.setDepCode(depCode);
                        testResord.setDepJob(depJob);
                        testResord.setDescription(description);
                        logger.debug(String.format("Record is read : %s", testResord));
                        testResords.add(testResord);
                    }
                }
            }
            return testResords;
        } catch (ParserConfigurationException e) {
            logger.error("Cannot create DocumentBuilder");
        } catch (SAXException e) {
            logger.error(String.format("Error parse file %s", filePath));
            logger.debug(e);
        } catch (IOException e) {
            logger.error("Error reading file");
            logger.debug(e);
        }
        return testResords;
    }
}