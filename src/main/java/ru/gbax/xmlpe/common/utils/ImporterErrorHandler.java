package ru.gbax.xmlpe.common.utils;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Обработчик ошибок при парсинге XML
 * Created by GBAX on 28.07.2015.
 */
public class ImporterErrorHandler implements ErrorHandler {

    static Logger logger = null;

    public ImporterErrorHandler(Class clazz){
        logger = Logger.getLogger(clazz);
    }

    private String getParseExceptionInfo(SAXParseException spe) {
        return "Line=" + spe.getLineNumber() + ": " + spe.getMessage();
    }

    public void warning(SAXParseException spe) throws SAXException {
        logger.warn("Warning: " + getParseExceptionInfo(spe));
    }

    public void error(SAXParseException spe) throws SAXException {
        String message = "Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    public void fatalError(SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

}
