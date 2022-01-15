package com.S1A4.List;
import com.S1A4.Pair;

import java.util.Optional;

public class List<K extends Comparable<K>, V> {

    private Node<K, V> head;
    private Node<K, V> tail;
    private long size;

    public Node<K, V> getHead() {
        return head;
    }

    public Node<K, V> getTail() {
        return tail;
    }

    public void setHead(Node<K, V> head) {
        this.head = head;
    }

    public void setTail(Node<K, V> tail) {
        this.tail = tail;
    }

    public static class Node<K extends Comparable<K>, V> {

        private final Pair<K, V> data;
        protected Node<K, V> next;

        public Node(Pair<K, V> data) {
            this.data = data;
        }

        public K getKey() { return this.data.getKey(); }
        public Pair<K, V> getData() { return data; }

        public Node<K, V> getNext() {
            return next;
        }
    }

    public void addNode(Pair<K, V> toAdd) {
        Node<K, V> newNode = new Node<>(toAdd);

        if (head == null) {
            head = newNode;
        }
        else {
            Node<K, V> node = head;
            while (node.next != null) { node = node.next; }
            node.next = newNode;
            tail.next = newNode;
        }

        tail = newNode;
//        tail.next = head;
        size++;
    }

    public boolean contains(K key) {
        Node<K, V> curNode = head;

        if (this.isNotEmpty()) {
            do {
                if (curNode.data.contains(key)) {
                    return true;
                }
                curNode = curNode.next;
            } while (curNode != head);
        }
        return false;
    }

    public Optional<V> get(K key) {
        Node<K, V> curNode = head;

        if (this.isNotEmpty()) {
            do {
                if (curNode.data.getKey() == key)
                { return Optional.of(curNode.data.getValue()); }
                curNode = curNode.next;
            } while (curNode != head);
        }
        return Optional.empty();
    }

    public boolean isNotEmpty() { return head != null; }

    public Optional<V> remove(K neededKey ) {
        Node<K, V> curNode = head;
        if (this.isNotEmpty()) {
            do {
                Node<K, V> nextNode = curNode.next;
                if (nextNode.data.getKey() == neededKey) {
                    Optional<V> neededValue = Optional.of(nextNode.data.getValue());
                    if (tail == head) {
                        head = null;
                        tail = null;
                    } else {
                        curNode.next = nextNode.next;
                        if (head == nextNode) { head = head.next; }
                        if (tail == nextNode) { tail = curNode; }
                    }
                    size--;
                    return neededValue;
                }
                curNode = nextNode;
            } while (curNode != head);
        }

        return Optional.empty();
    }

    public void printList() {
        if (this.isNotEmpty()) {
            Node<K, V> curNode = head;
            while (curNode != null) {
                System.out.println("List["
                        + curNode.data.getKey() + "] = "
                        + curNode.data.getValue());
                curNode = curNode.next;
            }
        } else {
            System.out.println("The list is empty.");
        }

    }

    public long size() { return this.size; }

    private List.Node<K, V> sortedMerge(List.Node<K, V> a, List.Node<K, V> b)
    {
        List.Node<K, V> result;

        /* Base cases */
        if (a == null)
            return b;
        if (b == null)
            return a;

        /* Pick either a or b, and recur */
        if (a.getKey().compareTo(b.getKey()) < 1) {
            result = a;

            result.next = sortedMerge(a.next, b);
        }
        else {
            result = b;
            result.next = sortedMerge(a, b.next);
        }
        return result;
    }

    public List.Node<K, V> mergeSort(List.Node<K, V> h)
    {
        // Base case : if head is null
        if (h == null || h.next == null) {
            return h;
        }

        // get the middle of the list
        List.Node<K, V> middle = getMiddle(h);
        List.Node<K, V> nextOfMiddle = middle.next;

        // set the next of middle node to null
        middle.next = null;

        // Apply mergeSort on left list
        List.Node<K, V> left = mergeSort(h);

        // Apply mergeSort on right list
        List.Node<K, V> right = mergeSort(nextOfMiddle);

        // Merge the left and right lists
        return sortedMerge(left, right);
    }

    // Utility function to get the middle of the linked list
    private List.Node<K, V> getMiddle(List.Node<K, V> head)
    {
        if (head == null)
            return null;

        List.Node<K, V> slow = head, fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
}