package data_structures.implementation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 *         This class represents the structure Coarse Grained Tree.
 * 
 */

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {

	private Node<T> root;

	private final Lock lock;

	public CoarseGrainedTree() {
		this.root = null;
		lock = new ReentrantLock();
	}

	@Override
	public void add(T t) {

		Node<T> newNode = new Node<T>(t);
		lock.lock();
		if (Sorted.DEBUG) {
			System.out.println(this.toString());
			System.out.println("New element" + t);
		}
		try {
			if (t == null)
				return;

			if (root == null) {
				this.root = newNode;
				return;
			}

			recoursiveAdd(root, root, newNode);
		} finally {
			lock.unlock();
		}

	}

	private boolean recoursiveAdd(Node<T> parent, Node<T> index, Node<T> newNode) {
		if (parent == null || index == null || newNode == null)
			return false;

		// System.out.println("Current:"+index.value+" new:"+newNode.value);

		if (newNode.compareTo(index) == 0) {
			// the new node will take the place of the
			// node with the same value
			newNode.setLeft(index);
			// the right subtree of the current node
			// will be the new right subtree of the new node
			newNode.setRight(index.getRight());
			// Update the parent child with the new node
			if (index.equals(parent.getLeft()))
				parent.setLeft(newNode);
			else
				parent.setRight(newNode);
			// update the current node
			index.setRight(null);
		}
		if (newNode.compareTo(index) < 0) {
			if (index.getLeft() == null)
				index.setLeft(newNode);
			else
				return recoursiveAdd(index, index.getLeft(), newNode);
		}
		if (newNode.compareTo(index) > 0) {
			if (index.getRight() == null)
				index.setRight(newNode);
			else
				return recoursiveAdd(index, index.getRight(), newNode);
		}
		return true;
	}

	@Override
	public void remove(T t) {
		Node<T> node = new Node<T>(t);
		lock.lock();
		if (Sorted.DEBUG) {
			System.out.println(this.toString());
			System.out.println("Remove element" + t);
		}
		try {

			if (root == null)
				return;

			if (root.compareTo(node) == 0) {
				if (root.getLeft() == null) {
					root = root.getRight();
				} else if (root.getRight() == null) {
					root = root.getLeft();
				} else {
					// search for the first left leaf on its right subtree
					Node<T> substitute = substituteLookUp(root, root.getRight());
					// the substitute node takes its place.
					substitute.setLeft(root.getLeft());
					substitute.setRight(root.getRight());
					// update the root with the substitute
					root = substitute;
					// return true;
				}
			} else if (!recoursiveRemove(root, root, node)) {
				if (Sorted.DEBUG)
					System.out
							.println("I haven't find anything.. O.o Not Correct!");
			}
		} finally {
			lock.unlock();
		}
	}

	private boolean recoursiveRemove(Node<T> parent, Node<T> index,
			Node<T> newNode) {
		if (parent == null || index == null || newNode == null) {
			if (Sorted.DEBUG)
				System.out.println("Fail!");
			return false;
		}
		// System.out.println("Current:"+index.value+" rem:"+newNode.value);
		if (newNode.compareTo(index) == 0) {
			// check if the it can remove the node easly.
			if (index.getLeft() == null) {
				if (index.equals(parent.getLeft()))
					parent.setLeft(index.getRight());
				else
					parent.setRight(index.getRight());
			} else if (index.getRight() == null) {
				if (index.equals(parent.getLeft()))
					parent.setLeft(index.getLeft());
				else
					parent.setRight(index.getLeft());
			} else {
				// search for the first left leaf on its right subtree
				if (Sorted.DEBUG)
					System.out.println("Substitude.. Nodenow:" + index.value);
				Node<T> substitute = substituteLookUp(index, index.getRight());
				// the substitute node takes its place.
				substitute.setLeft(index.getLeft());
				substitute.setRight(index.getRight());
				// update the parent's child with the substitute
				if (index.equals(parent.getLeft()))
					parent.setLeft(substitute);
				else
					parent.setRight(substitute);
				// return true;
			}
		} else {
			// recursively looks for the node
			if (newNode.compareTo(index) < 0) {
				if (index.getLeft() == null)
					return false;
				else
					return recoursiveRemove(index, index.getLeft(), newNode);
			}
			if (newNode.compareTo(index) > 0) {
				if (index.getRight() == null)
					return false;
				else
					return recoursiveRemove(index, index.getRight(), newNode);
			}
		}
		return true;
	}

	private Node<T> substituteLookUp(Node<T> parent, Node<T> node) {
		if (parent == null || node == null) {
			if (Sorted.DEBUG)
				System.out.println("Fail222!");
			return null;
		}
		if (node.isLeaf()) {
			if (Sorted.DEBUG)
				System.out.printf("is leaf");
			if (node.equals(parent.getLeft()))
				parent.setLeft(null);
			else
				parent.setRight(null);
			return node;
		} else if (node.getLeft() == null) {
			if (Sorted.DEBUG)
				System.out.printf("not leaf leaf");
			if (node.equals(parent.getLeft()))
				parent.setLeft(node.getRight());
			else
				parent.setRight(node.getRight());
			if (Sorted.DEBUG)
				System.out.println("Parent:" + parent.value + " Node:"
						+ node.value + " node.right:" + node.getRight());
			return node;
		} else {
			return substituteLookUp(node, node.getLeft());
		}
	}

	@Override
	public String toString() {
		// it is a debugging method so it doesn't need to lock the structure.
		Node<T> node = this.root;
		if (node == null)
			return "[]";
		return "[" + node.getValue() + recursivePrint(node.getLeft())
				+ recursivePrint(node.getRight()) + "]";
	}

	private String recursivePrint(Node<T> node) {
		if (node == null)
			return "";
		return "[" + node.getValue() + recursivePrint(node.getLeft())
				+ recursivePrint(node.getRight()) + "]";
	}

}
