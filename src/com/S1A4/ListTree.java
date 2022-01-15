package com.S1A4;

import com.S1A4.List.List;
import com.S1A4.Tree.RedBlackTree;

import java.util.Optional;

public class ListTree<K extends Comparable<K>, V> {

    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;

    private boolean isListForm = true;
    private long size = 0;

    public List<K, V> listContainer = new List<>();
    RedBlackTree<K, V> treeContainer = new RedBlackTree<>();

    public void put(Pair<K, V> pair) {
        if (isListForm) {
            listContainer.addNode(pair);
            if (++size >= ListTree.TREEIFY_THRESHOLD) {
                isListForm = false;
                treeify();
            }
        } else {
            size++;
            treeContainer.insertNode(pair);
        }
    }

    public Optional<V> get(K key) {
        if (isListForm) { return listContainer.get(key); }
        else { return treeContainer.get(key); }
    }

    public boolean isEmpty() { return size > 0; }

    public Optional<V> remove(K key) {
        if (isListForm) {
            return listContainer.remove(key);
        } else {
            Optional<V> value = treeContainer.deleteNode(key);
            if (value != Optional.empty()) {
                if (--size <= ListTree.UNTREEIFY_THRESHOLD) {
                    isListForm = true;
                    listify();
                }
            }
            return value;
        }
    }

    public boolean contains(K key) {
        if (isListForm) {
            return listContainer.contains(key);
        } else {
            return treeContainer.contains(key);
        }
    }

    public long size() { return size; }

    public void printStdOutContent() {
        if (isListForm) {
            listContainer.printList();
        } else {
            treeContainer.printPreOrder(System.out);
        }
    }

    public void listify() {
        listContainer = treeContainer.traverseLevelOrder(treeContainer.getRoot(), new List<>());
        treeContainer.setRoot(null);
    }

    public void treeify() {
        List.Node<K, V> tmpNode = listContainer.getHead();
        while (tmpNode != null) {
            treeContainer.insertNode(tmpNode.getData());
            tmpNode = tmpNode.getNext();
        }
        listContainer.setHead(null); listContainer.setTail(null);
    }

    public static void main(String[] args) {
        ListTree<Integer, Double> listTree = new ListTree<>();
        for (int i = 0; i < 8; i++) {
            listTree.put(new Pair<>(i, Math.pow(i, 2)));
        }


        listTree.printStdOutContent();      // size = 8; TreeForm
        System.out.println();

        listTree.remove(7);             // size = 7; Still TreeForm
        listTree.remove(10);            // key doesn't exist; size still = 7
        listTree.printStdOutContent();      // check current Form

        listTree.remove(3);             // size = 6; Form is changed to List

        System.out.println();
        listTree.printStdOutContent();      // ListForm

        listTree.put(new Pair<>
                (10, Math.pow(10, 2)));
        listTree.put(new Pair<>
                (12, Math.pow(12, 2)));     // size = 8; Form is changed to Tree;

        System.out.println();
        listTree.printStdOutContent();      // TreeForm

    }
}
