package com.karandashov;

import com.karandashov.model.User;
import com.karandashov.parser.HashMapCsvParser;
import com.karandashov.parser.ArrayListCsvParser;
import com.karandashov.parser.LinkedListCsvParser;
import com.karandashov.parser.PriorityQueueCsvParser;
import com.karandashov.parser.HashSetCsvParser;
import com.karandashov.parser.TreeMapCsvParser;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        ArrayListCsvParser arrayListCsvParser = new ArrayListCsvParser("users.csv");
        List<User> arrayListUsers = arrayListCsvParser.parse();
        System.out.println("ArrayList:");
        System.out.println(arrayListUsers);

        LinkedListCsvParser linkedListCsvParser = new LinkedListCsvParser("users.csv");
        List<User> linkedListUsers = linkedListCsvParser.parse();
        System.out.println("LinkedList:");
        System.out.println(linkedListUsers);

        HashMapCsvParser hashMapCsvParser = new HashMapCsvParser("users.csv");
        Map<Integer, User> hashMapUsers = hashMapCsvParser.parse();
        System.out.println("HashMap:");
        System.out.println(hashMapUsers);

        TreeMapCsvParser treeMapCsvParser = new TreeMapCsvParser("users.csv");
        Map<Integer, User> treeMapUsers = treeMapCsvParser.parse();
        System.out.println("TreeMap:");
        System.out.println(treeMapUsers);

        HashSetCsvParser hashSetCsvParser = new HashSetCsvParser("users.csv");
        Set<User> setUsers = hashSetCsvParser.parse();
        System.out.println("HashSet:");
        System.out.println(setUsers);

        PriorityQueueCsvParser priorityQueueCsvParser = new PriorityQueueCsvParser("users.csv");
        Queue<User> pqUsers = priorityQueueCsvParser.parse();
        System.out.println("PriorityQueue:");
        System.out.println(pqUsers);
    }
}