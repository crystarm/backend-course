package com.karandashov;

import com.karandashov.dao.PassportDao;
import com.karandashov.dao.SnilsDao;
import com.karandashov.model.Document;
import com.karandashov.model.DocumentType;
import com.karandashov.repository.CrudRepository;

import java.util.Map;

public class DaoManager {

    private final Map<DocumentType, CrudRepository<? extends Document>> daoMap;

    public DaoManager(PassportDao passportDao, SnilsDao snilsDao) {
        this.daoMap = Map.of(
                DocumentType.PASSPORT, passportDao,
                DocumentType.SNILS, snilsDao
        );
    }

    public CrudRepository<? extends Document> getDaoFor(Document document) {
        return daoMap.get(document.getType());
    }
}
