package com.S1A4.Tree;


import com.S1A4.Pair;

public class Node<K extends Comparable<K>, V> {

  // also called "value" in a binary tree
  // also called "key" in a binary search tree
  protected Pair<K, V> data;

  protected Node<K, V> left, right, parent;

  protected boolean color; // used in red-black tree


  public Node(Pair<K, V> data) {
    this.data = data;
  }

  public Pair<K, V> getData() {
    return data;
  }
}
