package data_structures.implementation;

/**
 * @author Matteo Casenove, Nicola Mularoni, and Carmine Paolino
 * 
 *         This class represents the generic node in the tree.
 * @param <T>
 * 
 */

public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {

	public T value;
	public Node<T> left;
	public Node<T> right;
	public Node<T> parent;

	public Node() {
		this.value = null;
		this.left = null;
		this.right = null;
		this.parent = null;
	}

	public Node(T value) {
		this.value = value;
	}

	public Node(T value, Node<T> left, Node<T> right) {
		this.value = value;
		this.left = left;
		this.right = right;
	}

	public T getValue() {
		return this.value;
	}

	public Node<T> getLeft() {
		return this.left;
	}

	public Node<T> getRight() {
		return this.right;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setLeft(Node<T> left) {
		this.left = left;
	}

	public void setRight(Node<T> right) {
		this.right = right;
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public boolean isLeaf() {
		return (this.left == null && this.right == null);
	}

	@Override
	public int compareTo(Node<T> o) {
		return (this.value).compareTo(o.value);
	}

}
