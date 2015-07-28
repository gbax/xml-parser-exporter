package ru.gbax.xmlpe.common.service;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.gbax.xmlpe.common.service.api.XMLExporterService;
import ru.gbax.xmlpe.testrecord.dao.api.TestRecordDAO;
import ru.gbax.xmlpe.testrecord.model.TestResord;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

/**
 * Сервис экспорта в файл из БД
 *
 * Created by GBAX on 27.07.2015.
 */
public class XMLExporterServiceImpl implements XMLExporterService {

    final static Logger logger = Logger.getLogger(XMLExporterServiceImpl.class);

    private TestRecordDAO testRecordDAO;

    public void setTestRecordDAO(TestRecordDAO testRecordDAO) {
        this.testRecordDAO = testRecordDAO;
    }

    public void runExport(String filePath) {
        try {
            logger.info(String.format("Start export records to file %s", filePath));
            final List<TestResord> all = testRecordDAO.findAll();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            logger.debug("Document created...");
            Element rootElement = doc.createElement("records");
            doc.appendChild(rootElement);
            for (TestResord testResord : all) {

                Element recordElement = doc.createElement("record");
                rootElement.appendChild(recordElement);

                Element depCode = doc.createElement("depCode");
                depCode.appendChild(doc.createTextNode(testResord.getDepCode()));
                recordElement.appendChild(depCode);

                Element depJob = doc.createElement("depJob");
                depJob.appendChild(doc.createTextNode(testResord.getDepJob()));
                recordElement.appendChild(depJob);

                Element description = doc.createElement("description");
                description.appendChild(doc.createTextNode(testResord.getDescription()));
                recordElement.appendChild(description);
                logger.debug(String.format("Record added to document %s ...", testResord));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            logger.debug("Saving to the file...");
            transformer.transform(source, result);
            logger.info(String.format("File %s saved", filePath));
        } catch (ParserConfigurationException e) {
            logger.error("Cannot create DocumentBuilder");
            logger.debug(e);
        } catch (TransformerException e) {
            logger.error("Cannot transform document");
            logger.debug(e);
        }
    }
}
