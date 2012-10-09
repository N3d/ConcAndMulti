package data_structures.implementation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import data_structures.Sorted;

/**
 * @author Carmine Paolino
 * 
 * @param <T>
 */
public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {
	private Node<T> root;
	private final Lock lock;
	private static final Logger logger = Logger
			.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public FineGrainedTree() {
		root = null;
		lock = new ReentrantLock();
	}

	@Override
	public void add(T t) {
		lock.lock();
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Current tree: " + sequentialToString());
				logger.fine("Adding " + t.toString());
			}

			Node<T> _new = new Node<T>(t);
			if (root == null) {
				root = _new;
			} else {
				sequentialAdd(root, _new);
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void remove(T t) {
		lock.lock();
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Current tree: " + sequentialToString());
				logger.fine("Removing " + t.toString());
			}

			sequentialRemove(root, t);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String toString() {
		PreorderToStringOperation toString = new PreorderToStringOperation();
		lock.lock();
		try {
			sequentialTraverse(root, toString);
		} finally {
			lock.unlock();
		}
		return toString.toString();
	}

	// sequential methods
	private void sequentialRemove(Node<T> curr, T t) {
		if (curr == null) {
			return;
		}
		if (t.equals(new Integer(12))) {
			logger.finer("Removing 12");
		}
		switch (t.compareTo(curr.value)) {
		case 0:
			if (curr.left != null && curr.right != null) {
				Node<T> successor = findMinChild(curr.right);
				curr.value = successor.value;
				replaceNodeInParent(successor, successor.right);
			} else if (curr.left != null) {
				replaceNodeInParent(curr, curr.left);
			} else if (curr.right != null) {
				replaceNodeInParent(curr, curr.right);
			} else {
				replaceNodeInParent(curr, null);
			}
			break;
		case -1:
			sequentialRemove(curr.left, t);
			break;
		case 1:
			sequentialRemove(curr.right, t);
			break;
		}
	}

	private void replaceNodeInParent(Node<T> curr, Node<T> _new) {
		if (curr.parent != null) {
			if (curr == curr.parent.left) {
				curr.parent.left = _new;
			} else {
				curr.parent.right = _new;
			}
		} else {
			root = _new;
		}
		if (_new != null) {
			_new.parent = curr.parent;
		}
	}

	private Node<T> findMinChild(Node<T> curr) {
		while (curr.left != null) {
			curr = curr.left;
		}
		return curr;
	}

	private void sequentialAdd(Node<T> curr, Node<T> _new) {
		switch (_new.value.compareTo(curr.value)) {
		case -1:
			logger.finer("curr.left = " + curr.left);
			if (curr.left == null) {
				curr.left = _new;
				_new.parent = curr;
			} else {
				sequentialAdd(curr.left, _new);
			}
			break;
		case 1:
			logger.finer("curr.right = " + curr.right);
			if (curr.right == null) {
				curr.right = _new;
				_new.parent = curr;
			} else {
				sequentialAdd(curr.right, _new);
			}
			break;
		case 0:
			if (curr.left == null) {
				curr.left = _new;
				_new.parent = curr;
			} else {
				sequentialAdd(curr.left, _new);
			}
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private void sequentialInternalAdd(Node<T> curr, Node<T> _new) {
		if (curr == null) {
			curr = _new;
			_new.parent = curr.parent;
		} else {
			sequentialAdd(curr, _new);
		}
	}

	private void sequentialTraverse(Node<T> curr, Operation<T> operation) {
		if (curr == null) {
			return;
		}
		operation.start(curr);
		sequentialTraverse(curr.left, operation);
		operation.middle(curr);
		sequentialTraverse(curr.right, operation);
		operation.end(curr);
	}

	private interface Operation<T extends Comparable<T>> {
		public void start(Node<T> node);

		public void middle(Node<T> node);

		public void end(Node<T> node);
	}

	private class PreorderToStringOperation implements Operation<T> {
		StringBuilder stringBuilder;

		public PreorderToStringOperation() {
			stringBuilder = new StringBuilder();
		}

		@Override
		public String toString() {
			return stringBuilder.toString();
		}

		@Override
		public void middle(Node<T> node) {
			// nothing
		}

		@Override
		public void start(Node<T> node) {
			stringBuilder.append('[' + node.value.toString());
		}

		@Override
		public void end(Node<T> node) {
			stringBuilder.append(']');
		}
	}

	private String sequentialToString() {
		PreorderToStringOperation toString = new PreorderToStringOperation();
		sequentialTraverse(root, toString);
		return toString.toString();
	}
}
