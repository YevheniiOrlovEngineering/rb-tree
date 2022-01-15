package com.S1A4.Tree;


import com.S1A4.Pair;

import java.util.Optional;

public interface BinarySearchTree<K extends Comparable<K>, V> {

  Node<K, V> searchNode(K key);

  void insertNode(Pair<K, V> toAddNode);

  Optional<V> deleteNode(K key);
}
