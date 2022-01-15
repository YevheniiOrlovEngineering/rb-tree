package com.S1A4.Tree;


public class BaseBinaryTree<K extends Comparable<K>, V> {

  protected Node<K, V> root;

  public Node<K, V> getRoot() {
    return root;
  }
  
  public void setRoot(Node<K, V> newRoot) {
    this.root = newRoot;
  }
}
