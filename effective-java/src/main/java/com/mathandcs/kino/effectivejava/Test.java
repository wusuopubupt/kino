package com.mathandcs.kino.effectivejava;

public class Test<T> {
    private int capacity;
    private Object lock = new Object();
    private Node<T> head;
    private Node<T> current;
    private int size = 0;

    public Test(int capacity) {
        if (capacity <= 0) {
            throw new RuntimeException("invalid capacity! the value of capacity should be larger than 0.");
        }
        this.capacity = capacity;
    }

    public T get() throws InterruptedException{
        synchronized(lock) {
            while (size == 0) {
                lock.wait();
            }
            Node<T> out = head;
            head = out.next;
            size --;
            lock.notifyAll();
            return out.value();
        }
    }

    public void put(T value) throws InterruptedException{
        synchronized(lock) {
            while (size == capacity) {
                lock.wait();
            }

            Node<T> input = new Node<T>(value);
            if (head == null) {
                head = input;
            }
            if (current == null) {
                current = input;
            } else {
                current.next = input;
                current = input;
            }
            size ++;
            lock.notifyAll();
        }
    }
    private class Node<T> {
        public Node<T> next;
        private final T value;
        public Node(T value) {
            this.value = value;
        }
        public T value() {
            return value;
        }
    }

    private static void consume(Test t) {
        class Task implements Runnable {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("get: " + t.get());
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }

        Task task = new Task();
        task.run();
    }

    public static void main(String[] args) throws Exception{
        Test t = new Test(3);
        int i = 0;
        while (i < 100) {
            t.put(1);
            i++;
            if (i == 3) {
                consume(t);
            }
        }
    }


}



