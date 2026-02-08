package com.karandashov;

import com.karandashov.model.User;
import com.karandashov.parser.CollectionParser;
import com.karandashov.parser.LineCsvParser;
import com.karandashov.parser.Parser;
import com.karandashov.parser.impl.*;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        ArrayListCsvParser a = new ArrayListCsvParser("users.csv"); // конкретный тип
        LineCsvParser b = new ArrayListCsvParser("users.csv"); // абстрактный базовый тип
        CollectionParser c = new ArrayListCsvParser("users.csv"); // интерфейс
        Parser d = new ArrayListCsvParser("users.csv"); // интерфейс уровнем ниже
        Object e = new ArrayListCsvParser("users.csv"); // вообще самый общий тип

        a.parseAll();
        a.parse();
        // b в принципе не имеет public методов
        c.parseAll();
        c.parse();
        d.parse(); // у интерфейса Parser нет метода parseAll()
        // e просто Object без спец методов

        ArrayListCsvParser arrayListCsvParser = new ArrayListCsvParser("users.csv");
        List<User> arrayListUsers = arrayListCsvParser.parseAll();
        System.out.println("ArrayList:");
        System.out.println(arrayListUsers);

        LinkedListCsvParser linkedListCsvParser = new LinkedListCsvParser("users.csv");
        List<User> linkedListUsers = linkedListCsvParser.parseAll();
        System.out.println("LinkedList:");
        System.out.println(linkedListUsers);

        HashMapCsvParser hashMapCsvParser = new HashMapCsvParser("users.csv");
        Map<Integer, User> hashMapUsers = hashMapCsvParser.parseAll();
        System.out.println("HashMap:");
        System.out.println(hashMapUsers);

        TreeMapCsvParser treeMapCsvParser = new TreeMapCsvParser("users.csv");
        Map<Integer, User> treeMapUsers = treeMapCsvParser.parseAll();
        System.out.println("TreeMap:");
        System.out.println(treeMapUsers);

        HashSetCsvParser hashSetCsvParser = new HashSetCsvParser("users.csv");
        Set<User> setUsers = hashSetCsvParser.parseAll();
        System.out.println("HashSet:");
        System.out.println(setUsers);

        PriorityQueueCsvParser priorityQueueCsvParser = new PriorityQueueCsvParser("users.csv");
        Queue<User> pqUsers = priorityQueueCsvParser.parseAll();
        System.out.println("PriorityQueue:");
        System.out.println(pqUsers);
    }
}