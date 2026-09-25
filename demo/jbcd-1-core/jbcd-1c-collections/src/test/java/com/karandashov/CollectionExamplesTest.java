package com.karandashov;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CollectionExamplesTest {

    private final CollectionExamples examples = new CollectionExamples();
    private final User[] users = examples.createArray();

    @Test
    void shouldCreateLists() {
        assertInstanceOf(ArrayList.class, examples.createArrayList(users));
        assertInstanceOf(LinkedList.class, examples.createLinkedList(users));
        assertEquals(3, examples.createArrayList(users).size());
    }

    @Test
    void shouldCreateMaps() {
        assertInstanceOf(HashMap.class, examples.createHashMap(users));
        assertInstanceOf(LinkedHashMap.class, examples.createLinkedHashMap(users));
        assertInstanceOf(TreeMap.class, examples.createTreeMap(users));
        assertEquals(List.of(1L, 2L, 3L), new ArrayList<>(examples.createTreeMap(users).keySet()));
    }

    @Test
    void shouldCreateSets() {
        assertInstanceOf(HashSet.class, examples.createHashSet(users));
        assertInstanceOf(TreeSet.class, examples.createTreeSet(users));
        assertEquals(3, examples.createHashSet(users).size());
    }

    @Test
    void shouldCreatePriorityQueue() {
        PriorityQueue<User> queue = examples.createPriorityQueue(users);

        assertEquals("Anna", Objects.requireNonNull(queue.poll()).getName());
    }
}
