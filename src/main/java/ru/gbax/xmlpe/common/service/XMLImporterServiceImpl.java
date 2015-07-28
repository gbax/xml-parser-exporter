package ru.gbax.xmlpe.common.service;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.gbax.xmlpe.common.exception.ImportException;
import ru.gbax.xmlpe.common.service.api.XMLImporterService;
import ru.gbax.xmlpe.common.utils.ImporterErrorHandler;
import ru.gbax.xmlpe.testrecord.dao.api.TestRecordDAO;
import ru.gbax.xmlpe.testrecord.model.TestResord;
import ru.gbax.xmlpe.testrecord.utils.TestRecordKeyModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис импорта в файл из БД
 *
 * Created by GBAX on 27.07.2015.
 */
public class XMLImporterServiceImpl implements XMLImporterService {

    final static Logger logger = Logger.getLogger(XMLImporterServiceImpl.class);

    private TestRecordDAO testRecordDAO;

    public void setTestRecordDAO(TestRecordDAO testRecordDAO) {
        this.testRecordDAO = testRecordDAO;
    }

    public void runImport(String filePath) throws ImportException {
        List<TestResord> allRecords = testRecordDAO.findAll();
        Map<TestRecordKeyModel, TestResord> recordDbMap = new HashMap<>();
        for (TestResord testResord : allRecords) {
            recordDbMap.put(new TestRecordKeyModel(testResord.getDepCode(), testResord.getDepJob()), testResord);
        }
        logger.info(String.format("Start import file %s.", filePath));
        List<TestResord> recordsFromXML;
        recordsFromXML = getRecordsFromXML(filePath);
        logger.info(String.format("Founded records in file %s ...", recordsFromXML.size()));
        if (recordsFromXML.size() == 0) {
            return;
        }
        Map<TestRecordKeyModel, TestResord> recordNewMap = new HashMap<>();
        List<TestResord> recordToDelete = new ArrayList<>();
        List<TestResord> recordToInsert = new ArrayList<>();
        Map<Long, TestResord> recordToUpdate = new HashMap<>();
        for (TestResord testResord : recordsFromXML) {
            TestRecordKeyModel testRecordKeyModel = new TestRecordKeyModel(testResord.getDepCode(), testResord.getDepJob());
            if (recordNewMap.get(testRecordKeyModel) != null) {
                throw new ImportException(String.format("Found duplicate record %s", testResord));
            }
            recordNewMap.put(testRecordKeyModel, testResord);
        }
        for (Map.Entry<TestRecordKeyModel, TestResord> record : recordNewMap.entrySet()) {
            TestResord testResordDb = recordDbMap.get(record.getKey());
            if (testResordDb == null) {
                recordToInsert.add(record.getValue());
            } else {
                recordToUpdate.put(testResordDb.getId(), record.getValue());
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
            return;
        }
        logger.info(String.format("File %s imported", filePath));
    }

    private List<TestResord> getRecordsFromXML(String filePath) throws ImportException {
        List<TestResord> testResords = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ImporterErrorHandler(XMLImporterServiceImpl.class));
            FileInputStream in = new FileInputStream(new File(filePath));
            Document doc = db.parse(in, "UTF-8");
            Element docEle = doc.getDocumentElement();
            NodeList nl = docEle.getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) nl.item(i);
                        Node depCodeNode = el.getElementsByTagName("depCode").item(0);
                        if (depCodeNode == null) {
                            throw new ImportException("Node depCode not found");
                        }
                        String depCode = depCodeNode.getTextContent();
                        Node depJobNode = el.getElementsByTagName("depJob").item(0);
                        if (depJobNode == null) {
                            throw new ImportException("Node depJobNode not found");
                        }
                        String depJob = depJobNode.getTextContent();
                        Node descriptionNode = el.getElementsByTagName("description").item(0);
                        if (descriptionNode == null) {
                            throw new ImportException("Node descriptionNode not found");
                        }
                        String description = descriptionNode.getTextContent();
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
            logger.debug(e);
            throw new ImportException("Cannot create DocumentBuilder");
        } catch (SAXException e) {
            logger.debug(e);
            throw new ImportException(String.format("Error parse file %s", filePath));
        } catch (IOException e) {
            logger.debug(e);
            throw new ImportException("Error reading file");
        } catch (Exception e) {
            logger.debug(e);
            throw new ImportException(String.format("Error reading file: %s", e.getMessage()));
        }
    }
}