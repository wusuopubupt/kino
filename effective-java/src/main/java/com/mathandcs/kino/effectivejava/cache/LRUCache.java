package com.mathandcs.kino.effectivejava.cache;

import java.util.HashMap;

/**
 * Created by dashwang on 5/12/17.
 * The LRU cache is a hash table of keys and double linked nodes.
 * The hash table makes the time of get() to be O(1).
 * The list of double linked nodes make the nodes adding/removal operations O(1).
 */
public class LRUCache {

    /* linked list node */
    public static class Node {
        int key;
        int val;
        Node pre;
        Node next;

        public Node(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    private int capacity;
    private HashMap<Integer, Node> map = new HashMap<>();
    public Node head;
    public Node tail;

    public int get(int key) {
        if (map.containsKey(key)) {
            Node foundNode = map.get(key);
            remove(foundNode);
            setHead(foundNode);
            return foundNode.val;
        }
        return -1;
    }

    private void remove(Node n) {
        if (n.pre != null) {
            n.pre.next = n.next;
        } else {
            head = n.next;
        }

        if (n.next != null) {
            n.next.pre = n.pre;
        } else {
            tail = n.pre;
        }
    }

    private void setHead(Node n) {
        n.next = head;
        n.pre = null; // Notice !
        if (head != null) {
            head.pre = n;
        }
        head = n;
        if (tail == null) {
            tail = head;
        }
    }

    /**
     * If map contains key, then update map and replace key to list head
     * Else set key to list head, check is map.size > capacity, if true, remove list tail
     */
    public void set(int key, int val) {
        if (map.containsKey(key)) {
            Node foundNode = map.get(key);
            remove(foundNode);
            foundNode.val = val;
            setHead(foundNode);
        } else {
            Node node = new Node(key, val);

            if (map.size() >= capacity) {
                map.remove(tail.key);
                remove(tail);
                setHead(node);
            } else {
                setHead(node);
            }
            map.put(key, node);
        }
    }

}
