/**
 * Node.java
 *
 *
 * Created: Sat Aug 26 17:23:08 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.expression;

import dynetica.entity.Entity;

public class Node {
    // node of a binary tree realized by means of a linked structure
    Object element; // element stored at the node
    Node left; // left child
    Node right; // right child
    Node parent; // parent node

    public Node() {

    }

    public Node(Object o) {
        this(o, null, null, null);
    }

    public Node(Object o, Node p, Node l, Node r) { // constructor with
                                                    // parameters
        setElement(o);
        setParent(p);
        setLeft(l);
        setRight(r);
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object o) {
        element = o;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node v) {
        left = v;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node v) {
        right = v;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node v) {
        parent = v;
    }

    public static boolean isLeaf(Node v) {
        return (v.getLeft() == null && v.getRight() == null);
    }

    public boolean hasLeft() {
        return (getLeft() != null);
    }

    public boolean hasRight() {
        return (right != null);
    }

    public boolean hasParent(Node v) {
        return (parent != null);
    }

    public boolean isLeaf() {
        return isLeaf(this);
    }

    public String toString() {
        /*
         * StringBuffer s = new StringBuffer(); if (hasLeft()) { if
         * (getLeft().isLeaf()) s.append( getLeft()); else s.append("(" +
         * getLeft() + ")"); }
         * 
         * s.append(((Entity) getElement()).getName());
         * 
         * if (hasRight()) { if (getRight().isLeaf()) s.append(getRight()); else
         * s.append("(" + getRight() + ")"); }
         * 
         * return s.toString();
         */

        if (isLeaf())
            return element.toString();
        else
            return (left.toString() + "  " + right.toString() + "  " + element
                    .toString());
    }
} // Node
