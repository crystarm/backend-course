package com.karandashov;

import com.karandashov.dao.PassportDao;
import com.karandashov.dao.SnilsDao;
import com.karandashov.model.impl.Passport;
import com.karandashov.model.impl.Snils;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        SnilsDao snilsDao = new SnilsDao();
        PassportDao passportDao = new PassportDao("passport.json");

        Snils snils1 = new Snils("111-11-111");
        Snils snils2 = new Snils("222-22-222");

        snilsDao.create(snils1);
        snilsDao.create(snils2);

        assert snilsDao.readById("111-11-111") != null;
        snilsDao.deleteById("111-11-111");
        assert snilsDao.readById("111-11-111") == null;
        assert snilsDao.readAll().size() == 1;

        Passport passport1 = new Passport(1111, 222222, "Vlad", LocalDate.of(2002, 10, 29));
        Passport passport2 = new Passport(3333, 444444, "Irakli", LocalDate.of(2000, 1, 1));

        passportDao.create(passport1);
        passportDao.create(passport2);

        assert passportDao.readById("1111-222222") != null;
        passportDao.deleteById("1111-222222");
        assert passportDao.readById("1111-222222") == null;
        assert passportDao.readAll().size() == 1;

        passport2 = new Passport(3333, 444444, "Dobroslav", LocalDate.of(2000, 1, 1));
        passportDao.update(passport2);
        assert passportDao.readById("3333-444444").equals(passport2);

        DaoManager daoManager = new DaoManager(passportDao, snilsDao);
        assert daoManager.getDaoFor(passport2).readById("3333-444444").equals(passport2);
        assert daoManager.getDaoFor(snils2).readById("222-22-222").equals(snils2);
    }
}