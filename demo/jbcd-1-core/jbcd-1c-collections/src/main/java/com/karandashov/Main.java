package com.karandashov;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        CollectionExamples examples = new CollectionExamples();
        // Массив имеет фиксированный размер и хранит элементы по индексам.
        User[] users = examples.createArray();

        // ArrayList хранит элементы в массиве внутри себя и быстро получает элемент по индексу.
        List<User> arrayList = examples.createArrayList(users);
        // LinkedList хранит цепочку узлов; вставки в середину могут быть дешевле, но доступ по индексу медленнее.
        List<User> linkedList = examples.createLinkedList(users);
        // HashMap быстро ищет значение по ключу, но не гарантирует порядок обхода.
        Map<Long, User> hashMap = examples.createHashMap(users);
        // LinkedHashMap сохраняет порядок добавления элементов.
        Map<Long, User> linkedHashMap = examples.createLinkedHashMap(users);
        // TreeMap хранит ключи отсортированными.
        Map<Long, User> treeMap = examples.createTreeMap(users);
        // HashSet хранит только уникальные элементы и не гарантирует порядок.
        Set<User> hashSet = examples.createHashSet(users);
        // TreeSet хранит уникальные элементы в отсортированном порядке.
        Set<User> treeSet = examples.createTreeSet(users);
        // PriorityQueue достает первым элемент с наибольшим приоритетом по Comparator.
        PriorityQueue<User> priorityQueue = examples.createPriorityQueue(users);

        print("ArrayList", arrayList);
        print("LinkedList", linkedList);
        print("HashMap", hashMap.values());
        print("LinkedHashMap", linkedHashMap.values());
        print("TreeMap", treeMap.values());
        print("HashSet", hashSet);
        print("TreeSet", treeSet);
        print("PriorityQueue", priorityQueue);
    }

    private static void print(String collectionName, Collection<User> users) {
        System.out.println(collectionName + ": " + users);
    }
}
