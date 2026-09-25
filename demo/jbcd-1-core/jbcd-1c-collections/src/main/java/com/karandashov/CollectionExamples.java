package com.karandashov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CollectionExamples {

    public User[] createArray() {
        return new User[]{
                new User(3L, "Petr", 34),
                new User(1L, "Anna", 22),
                new User(2L, "Ivan", 28)
        };
    }

    public List<User> createArrayList(User[] users) {
        List<User> result = new ArrayList<>();
        addAll(users, result);
        return result;
    }

    public List<User> createLinkedList(User[] users) {
        List<User> result = new LinkedList<>();
        addAll(users, result);
        return result;
    }

    public Map<Long, User> createHashMap(User[] users) {
        Map<Long, User> result = new HashMap<>();
        putAll(users, result);
        return result;
    }

    public Map<Long, User> createLinkedHashMap(User[] users) {
        Map<Long, User> result = new LinkedHashMap<>();
        putAll(users, result);
        return result;
    }

    public Map<Long, User> createTreeMap(User[] users) {
        Map<Long, User> result = new TreeMap<>();
        putAll(users, result);
        return result;
    }

    public Set<User> createHashSet(User[] users) {
        Set<User> result = new HashSet<>();
        addAll(users, result);
        return result;
    }

    public Set<User> createTreeSet(User[] users) {
        Set<User> result = new TreeSet<>();
        addAll(users, result);
        return result;
    }

    public PriorityQueue<User> createPriorityQueue(User[] users) {
        PriorityQueue<User> result = new PriorityQueue<>(Comparator.comparingInt(User::getAge));
        addAll(users, result);
        return result;
    }

    private void addAll(User[] users, Collection<User> collection) {
        Collections.addAll(collection, users);
    }

    private void putAll(User[] users, Map<Long, User> map) {
        for (User user : users) {
            map.put(user.getId(), user);
        }
    }
}
