package com.S1A4.Tree;


import com.S1A4.List.List;
import com.S1A4.Pair;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class RedBlackTree<K extends Comparable<K>, V> extends BaseBinaryTree<K, V> implements BinarySearchTree<K, V> {

  static final boolean RED = false;
  static final boolean BLACK = true;


  @Override
  public Node<K, V> searchNode(K key) {

    Node<K, V> node = getRoot();
    while (node != null) {

      switch (key.compareTo(node.getData().getKey())) {
        case 0: return node;
        case -1: node = node.left;
        case 1: node = node.right;
      }
    }

    return null;
  }

  public boolean contains(K key) { return (searchNode(key) != null); }

  public Optional<V> get(K key) {
    Node<K, V> node = searchNode(key);
    if (node != null) {
      return Optional.of(node.getData().getValue());
    }
    return Optional.empty();
  }

  // -- Insertion ----------------------------------------------------------------------------------

  @Override
  public void insertNode(Pair<K, V> toAdd) {
    Node<K, V> node = getRoot();
    Node<K, V> parent = null;

    // Traverse the tree to the left or right depending on the key
    while (node != null) {
      parent = node;

      switch (toAdd.getKey().compareTo(node.getData().getKey())) {
        case 0 -> throw new IllegalArgumentException("BST already contains a node with key " + toAdd.getKey());
        case -1 -> node = node.left;
        case 1 -> node = node.right;
      }
    }

    // Insert new node
    Node<K, V> newNode = new Node<>(toAdd);
    newNode.color = RED;
    if (parent == null) {
      root = newNode;
    } else if (toAdd.getKey().compareTo(parent.getData().getKey()) < 0) {
      parent.left = newNode;
    } else {
      parent.right = newNode;
    }
    newNode.parent = parent;

    fixRedBlackPropertiesAfterInsert(newNode);
  }

  private void fixRedBlackPropertiesAfterInsert(Node<K, V> node) {
    Node<K, V> parent = node.parent;

    // Case 1: Parent is null, we've reached the root, the end of the recursion
    if (parent == null) {
      // to enforce black roots (rule 2):
      // node.color = BLACK;
      return;
    }

    // Parent is black --> nothing to do
    if (parent.color == BLACK) {
      return;
    }

    // From here on, parent is red
    Node<K, V> grandparent = parent.parent;

    // Case 2:
    // Not having a grandparent means that parent is the root. If we enforce black roots
    // (rule 2), grandparent will never be null, and the following if-then block can be
    // removed.
    if (grandparent == null) {
      // As this method is only called on red nodes (either on newly inserted ones - or -
      // recursively on red grandparents), all we have to do is to recolor the root black.
      parent.color = BLACK;
      return;
    }

    // Get the uncle (may be null/nil, in which case its color is BLACK)
    Node<K, V> uncle = getUncle(parent);

    // Case 3: Uncle is red -> recolor parent, grandparent and uncle
    if (uncle != null && uncle.color == RED) {
      parent.color = BLACK;
      grandparent.color = RED;
      uncle.color = BLACK;

      // Call recursively for grandparent, which is now red.
      // It might be root or have a red parent, in which case we need to fix more...
      fixRedBlackPropertiesAfterInsert(grandparent);
    }

    // Note on performance:
    // It would be faster to do the uncle color check within the following code. This way
    // we would avoid checking the grandparent-parent direction twice (once in getUncle()
    // and once in the following else-if). But for better understanding of the code,
    // I left the uncle color check as a separate step.

    // Parent is left child of grandparent
    else if (parent == grandparent.left) {
      // Case 4a: Uncle is black and node is left->right "inner child" of its grandparent
      if (node == parent.right) {
        rotateLeft(parent);

        // Let "parent" point to the new root node of the rotated sub-tree.
        // It will be recolored in the next step, which we're going to fall-through to.
        parent = node;
      }

      // Case 5a: Uncle is black and node is left->left "outer child" of its grandparent
      rotateRight(grandparent);

      // Recolor original parent and grandparent
      parent.color = BLACK;
      grandparent.color = RED;
    }

    // Parent is right child of grandparent
    else {
      // Case 4b: Uncle is black and node is right->left "inner child" of its grandparent
      if (node == parent.left) {
        rotateRight(parent);

        // Let "parent" point to the new root node of the rotated sub-tree.
        // It will be recolored in the next step, which we're going to fall-through to.
        parent = node;
      }

      // Case 5b: Uncle is black and node is right->right "outer child" of its grandparent
      rotateLeft(grandparent);

      // Recolor original parent and grandparent
      parent.color = BLACK;
      grandparent.color = RED;
    }
  }

  private Node<K, V> getUncle(Node<K, V> parent) {
    Node<K, V> grandparent = parent.parent;
    if (grandparent.left == parent) {
      return grandparent.right;
    } else if (grandparent.right == parent) {
      return grandparent.left;
    } else {
      throw new IllegalStateException("Parent is not a child of its grandparent");
    }
  }

  // -- Deletion -----------------------------------------------------------------------------------

  @Override
  public Optional<V> deleteNode(K key) {
    Node<K, V> node = getRoot();

    // Find the node to be deleted
    while (node != null && node.getData().getKey().compareTo(key) != 0) {
      // Traverse the tree to the left or right depending on the key
      if (key.compareTo(node.getData().getKey()) < 0) {
        node = node.left;
      } else {
        node = node.right;
      }
    }

    // Node not found?
    if (node == null) {
      return Optional.empty();
    }

    // At this point, "node" is the node to be deleted

    // In this variable, we'll store the node at which we're going to start to fix the R-B
    // properties after deleting a node.
    Node<K, V> movedUpNode;
    boolean deletedNodeColor;

    // Node has zero or one child
    if (node.left == null || node.right == null) {
      movedUpNode = deleteNodeWithZeroOrOneChild(node);
      deletedNodeColor = node.color;
    }

    // Node has two children
    else {
      // Find minimum node of right subtree ("inorder successor" of current node)
      Node<K, V> inOrderSuccessor = findMinimum(node.right);

      // Copy inorder successor's data to current node (keep its color!)
      node.data = inOrderSuccessor.data;

      // Delete inorder successor just as we would delete a node with 0 or 1 child
      movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
      deletedNodeColor = inOrderSuccessor.color;
    }

    if (deletedNodeColor == BLACK) {
      fixRedBlackPropertiesAfterDelete(movedUpNode);

      // Remove the temporary NIL node
      if (movedUpNode.getClass() == NilNode.class) {
        replaceParentsChild(movedUpNode.parent, movedUpNode, null);
      }
    }
    return Optional.of(node.getData().getValue());
  }

  private Node<K, V> deleteNodeWithZeroOrOneChild(Node<K, V> node) {
    // Node has ONLY a left child --> replace by its left child
    if (node.left != null) {
      replaceParentsChild(node.parent, node, node.left);
      return node.left; // moved-up node
    }

    // Node has ONLY a right child --> replace by its right child
    else if (node.right != null) {
      replaceParentsChild(node.parent, node, node.right);
      return node.right; // moved-up node
    }

    // Node has no children -->
    // * node is red --> just remove it
    // * node is black --> replace it by a temporary NIL node (needed to fix the R-B rules)
    else {
      Node<K, V> newChild = node.color == BLACK ? new NilNode<>() : null;
      replaceParentsChild(node.parent, node, newChild);
      return newChild;
    }
  }

  private Node<K, V> findMinimum(Node<K, V> node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  private void fixRedBlackPropertiesAfterDelete(Node<K, V> node) {
    // Case 1: Examined node is root, end of recursion
    if (node == getRoot()) {
      // Uncomment the following line if you want to enforce black roots (rule 2):
      // node.color = BLACK;
      return;
    }

    Node<K, V> sibling = getSibling(node);

    // Case 2: Red sibling
    if (sibling.color == RED) {
      handleRedSibling(node, sibling);
      sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
    }

    // Cases 3+4: Black sibling with two black children
    if (isBlack(sibling.left) && isBlack(sibling.right)) {
      sibling.color = RED;

      // Case 3: Black sibling with two black children + red parent
      if (node.parent.color == RED) {
        node.parent.color = BLACK;
      }

      // Case 4: Black sibling with two black children + black parent
      else {
        fixRedBlackPropertiesAfterDelete(node.parent);
      }
    }

    // Case 5+6: Black sibling with at least one red child
    else {
      handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
    }
  }

  private void handleRedSibling(Node<K, V> node, Node<K, V> sibling) {
    // Recolor...
    sibling.color = BLACK;
    node.parent.color = RED;

    // ... and rotate
    if (node == node.parent.left) {
      rotateLeft(node.parent);
    } else {
      rotateRight(node.parent);
    }
  }

  private void handleBlackSiblingWithAtLeastOneRedChild(Node<K, V> node, Node<K, V> sibling) {
    boolean nodeIsLeftChild = node == node.parent.left;

    // Case 5: Black sibling with at least one red child + "outer nephew" is black
    // --> Recolor sibling and its child, and rotate around sibling
    if (nodeIsLeftChild && isBlack(sibling.right)) {
      sibling.left.color = BLACK;
      sibling.color = RED;
      rotateRight(sibling);
      sibling = node.parent.right;
    } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
      sibling.right.color = BLACK;
      sibling.color = RED;
      rotateLeft(sibling);
      sibling = node.parent.left;
    }

    // Fall-through to case 6...

    // Case 6: Black sibling with at least one red child + "outer nephew" is red
    // --> Recolor sibling + parent + sibling's child, and rotate around parent
    sibling.color = node.parent.color;
    node.parent.color = BLACK;
    if (nodeIsLeftChild) {
      sibling.right.color = BLACK;
      rotateLeft(node.parent);
    } else {
      sibling.left.color = BLACK;
      rotateRight(node.parent);
    }
  }

  private Node<K, V> getSibling(Node<K, V> node) {
    Node<K, V> parent = node.parent;
    if (node == parent.left) {
      return parent.right;
    } else if (node == parent.right) {
      return parent.left;
    } else {
      throw new IllegalStateException("Parent is not a child of its grandparent");
    }
  }

  private boolean isBlack(Node<K, V> node) {
    return node == null || node.color == BLACK;
  }

  private static class NilNode<K extends Comparable<K>, V> extends Node<K, V> {
    private NilNode() {
      super(new Pair<>(null, null));
      this.color = BLACK;
    }
  }

  // -- Helpers for insertion and deletion ---------------------------------------------------------

  private void rotateRight(Node<K, V> node) {
    Node<K, V> parent = node.parent;
    Node<K, V> leftChild = node.left;

    node.left = leftChild.right;
    if (leftChild.right != null) {
      leftChild.right.parent = node;
    }

    leftChild.right = node;
    node.parent = leftChild;

    replaceParentsChild(parent, node, leftChild);
  }

  private void rotateLeft(Node<K, V> node) {
    Node<K, V> parent = node.parent;
    Node<K, V> rightChild = node.right;

    node.right = rightChild.left;
    if (rightChild.left != null) {
      rightChild.left.parent = node;
    }

    rightChild.left = node;
    node.parent = rightChild;

    replaceParentsChild(parent, node, rightChild);
  }

  private void replaceParentsChild(Node<K, V> parent, Node<K, V> oldChild, Node<K, V> newChild) {
    if (parent == null) {
      root = newChild;
    } else if (parent.left == oldChild) {
      parent.left = newChild;
    } else if (parent.right == oldChild) {
      parent.right = newChild;
    } else {
      throw new IllegalStateException("Node is not a child of its parent");
    }

    if (newChild != null) {
      newChild.parent = parent;
    }
  }

  // -- For toString() -----------------------------------------------------------------------------

  private void appendNodeToString(Node<K, V> node, StringBuilder builder) {
    builder.append("(").append(node.data.getKey())
            .append(", ") .append(node.data.getValue()).append(") ")
            .append(node.color == RED ? "[R]" : "[B]");
  }

  public void printPreOrder(PrintStream os) {
    StringBuilder sb = new StringBuilder();
    traversePreOrder(sb, "", "", this.root);
    os.print(sb);
  }

  // -- For Traversing -----------------------------------------------------------------------------

  public List<K, V> traverseLevelOrder(Node<K, V> root, List<K, V> list) {
    if (getRoot() == null) {
      return null;
    }

    Queue<Node<K, V>> queue = new ArrayDeque<>();
    queue.add(root);

    while (!queue.isEmpty()) {
      Node<K, V> node = queue.poll();
      list.addNode(node.data);

      if (node.left != null) {
        queue.add(node.left);
      }
      if (node.right != null) {
        queue.add(node.right);
      }
    }
    return list;
  }

  private void traversePreOrder(StringBuilder sb, String padding, String pointer,
                               Node<K, V> node) {
    if (node != null) {
      sb.append(padding);
      sb.append(pointer);
      appendNodeToString(node, sb);
      sb.append("\n");

      String paddingForBoth = padding + "│  ";
      String pointerForRight = "└──";
      String pointerForLeft = (node.right != null) ? "├──" : "└──";

      traversePreOrder(sb, paddingForBoth, pointerForLeft, node.left);
      traversePreOrder(sb, paddingForBoth, pointerForRight, node.right);
    }
  }
}
