package com.karandashov;

import com.karandashov.annotation.AnnotationReflectionDocsGenerator;
import com.karandashov.annotation.Document;
import com.karandashov.reflection.ReflectionDocsGenerator;
import com.karandashov.reflection.User;

public class Main {

    public static void main(String[] args) {

        User user = new User("Тест", "Тестович");
        user.setAge(18);
        ReflectionDocsGenerator.printObjectDescription(user);

        Document document = new Document("Паспорт", "Тест Тестович");
        document.setNumber(1122233);
        AnnotationReflectionDocsGenerator.printObjectDescription(document);
    }
}