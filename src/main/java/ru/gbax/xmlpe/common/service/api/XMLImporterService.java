package ru.gbax.xmlpe.common.service.api;

import ru.gbax.xmlpe.common.exception.ImportException;

/**
 * ��������� ��� ������� ������� � ��
 *
 * Created by GBAX on 27.07.2015.
 */
public interface XMLImporterService {

    /**
     * ������ �������
     * @param filePath ���� � �������� �����
     */
    void runImport(String filePath) throws ImportException;

}
